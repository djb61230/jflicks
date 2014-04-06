/*
    This file is part of JFLICKS.

    JFLICKS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JFLICKS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JFLICKS.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.jflicks.tv.postproc.worker.ffmpegscreenshot;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.tv.Recording;
import org.jflicks.tv.postproc.worker.BaseWorker;
import org.jflicks.tv.postproc.worker.BaseWorkerJob;
import org.jflicks.util.Util;

/**
 * This job starts a system job that runs comskip.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class FFmpegScreenshotJob extends BaseWorkerJob implements JobListener {

    private long after;

    /**
     * Constructor with one required argument.
     *
     * @param r A Recording to check for commercials.
     * @param bw The Worker associated with this Job.
     */
    public FFmpegScreenshotJob(Recording r, BaseWorker bw) {

        super(r, bw);
        setSleepTime(1000);
    }

    private long getAfter() {
        return (after);
    }

    private void setAfter(long l) {
        after = l;
    }

    private boolean isReadyToRun() {

        boolean result = false;

        if (isHlsRecording()) {

            // If the 5th segment is there, then the 4th is ready to use.
            File next = new File(getRecordingPath(5));
            result = next.exists();

        } else {

            long now = System.currentTimeMillis();
            result = (now > getAfter());

            if (result) {

                // Time is up.  For sanity sake lets make sure it's there.
                // This getRecordingPath will return the actual path as
                // we are not in HLS mode.
                File fname = new File(getRecordingPath(0));
                if (!fname.exists()) {

                    // Actually not really ready.
                    result = false;
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        Recording r = getRecording();
        if (r != null) {

            // When we have a full .ts recording file we want to fetch
            // 44 seconds into it.  However if we are in HLS mode this
            // doesn't always work so lets just fetch 7 seconds into it.
            String timeoffset = "-00:00:44";
            if (isHlsRecording()) {

                timeoffset = "-00:00:07";
            }

            String path = r.getPath();
            String inputpath = getRecordingPath(4);
            SystemJob job = SystemJob.getInstance("ffmpeg -itsoffset"
                + " "
                + timeoffset
                + " -y -i "
                + "\""
                + inputpath
                + "\""
                + " -vcodec png -vframes 1 -an -f "
                + "rawvideo -s 534x300 "
                + "\""
                + path
                + ".png"
                + "\"");

            fireJobEvent(JobEvent.UPDATE, "command: <" + job.getCommand()
                + ">");

            // Don't run until this time because there won't be video to
            // grab the screen shot!  We tack on 60 seconds just to be safe.
            setAfter(r.getRealStart() + 60000);

            job.addJobListener(this);
            setSystemJob(job);
            JobContainer jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            log(BaseWorker.INFO, "started: " + job.getCommand());
            setTerminate(false);

        } else {

            setTerminate(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        boolean jobStarted = false;
        int loops = 0;

        while (!isTerminate()) {

            if (isReadyToRun()) {

                JobContainer jc = getJobContainer();
                if ((!jobStarted) && (jc != null)) {

                    // We might be done before it comes around again but lets
                    // use a flag just to be safe...
                    jobStarted = true;
                    jc.start();
                }

            } else {

                loops++;
                if (loops >= 180) {

                    log(BaseWorker.INFO, "Giving up on getting screenshot!!!");
                    setTerminate(true);
                }
            }

            JobManager.sleep(getSleepTime());
        }

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();
        }
        setTerminate(true);
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            // Let's make some roku images.
            Recording r = getRecording();
            if (r != null) {

                String rpath = r.getPath();
                if (rpath != null) {

                    String path = rpath + ".png";
                    try {

                        BufferedImage bi = ImageIO.read(new File(path));
                        BufferedImage hd = Util.scale(bi, 388);
                        hd = hd.getSubimage(49, 0, 290, 218);
                        ImageIO.write(hd, "PNG",
                            new File(rpath + ".roku_hd.png"));

                        BufferedImage sd = Util.scale(bi, 256);
                        sd = sd.getSubimage(21, 0, 214, 144);
                        ImageIO.write(sd, "PNG",
                            new File(rpath + ".roku_sd.png"));

                    } catch (Exception ex) {
                        log(BaseWorker.INFO, ex.getMessage());
                    }
                }
            }

            // Nothing to do since we don't change any properties of the
            // Recording.  Clients should still get notified and be able
            // to update their screenshot.
            stop();
        }
    }

}


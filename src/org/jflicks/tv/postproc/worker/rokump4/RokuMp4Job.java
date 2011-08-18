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
package org.jflicks.tv.postproc.worker.rokump4;

import java.io.File;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.tv.Recording;
import org.jflicks.tv.postproc.worker.BaseWorker;
import org.jflicks.tv.postproc.worker.BaseWorkerJob;

/**
 * This job starts a system job that runs comskip.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RokuMp4Job extends BaseWorkerJob implements JobListener {

    /**
     * Constructor with one required argument.
     *
     * @param r A Recording to transcode.
     * @param bw The Worker associated with this job.
     */
    public RokuMp4Job(Recording r, BaseWorker bw) {

        super(r, bw);
    }

    private File computeFile(Recording r, boolean hidden) {

        File result = null;

        if (r != null) {

            result = new File(r.getPath());
            if (result.exists()) {

                String tname = null;
                if (hidden) {
                    tname = "." + result.getName() + ".mp4";
                } else {
                    tname = result.getName() + ".mp4";
                }

                result = new File(result.getParentFile(), tname);
            }
        }

        return (result);
    }

    private void move() {

        Recording r = getRecording();
        if (r != null) {

            File hidden = computeFile(r, true);
            if ((hidden != null) && (hidden.exists())) {

                log(BaseWorker.INFO, "moving " + hidden.getPath() + " to "
                    + computeFile(r, false));
                hidden.renameTo(computeFile(r, false));
                r.setIndexedExtension("mp4");
            }
        }
    }

    private void remove() {

        Recording r = getRecording();
        if (r != null) {

            File hidden = computeFile(r, true);
            if ((hidden != null) && (hidden.exists())) {

                if (!hidden.delete()) {

                    log(BaseWorker.INFO, "Failed to delete hidden file.");
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        Recording r = getRecording();
        if (r != null) {

            String path = r.getPath();
            File tmp = computeFile(r, true);
            SystemJob job = SystemJob.getInstance(
                "ionice -c3 HandBrakeCLI -e x264 -f mp4 -q 22.0 -r 29.97"
                + " -a 1 -E faac -B 128 -6 stereo -R 48"
                + " --decomb -4 -O"
                + " -x ref=1:bframes=0:cabac=0:8x8dct=0:weightp=0:me=dia:subq=0:rc-lookahead=0:analyse=none:trellis=0:aq-mode=0:no-deblock=1:scenecut=0:mbtree=0 -i " + path
                + " -o " + tmp.getPath());
            /*
                + " -w 1280 -l 720 --crop 0:0:0:0 --modulus 16"
            SystemJob job = SystemJob.getInstance(
                "ionice -c3 ffmpeg -i " + path + " -acodec libfaac -ab 128k"
                + " -vcodec libx264 -vpre lossless_ultrafast -r 29.97"
                + " -deinterlace -crf 22 -threads 0 -f mp4 " + tmp.getPath());
            */

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

        while (!isTerminate()) {

            JobContainer jc = getJobContainer();
            if ((!jobStarted) && (jc != null)) {

                jobStarted = true;
                jc.start();
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

            SystemJob job = getSystemJob();
            if ((job != null) && (job.getExitValue() == 0)) {
                move();
            } else {
                remove();
            }

            stop();
        }
    }

}


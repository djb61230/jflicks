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
package org.jflicks.tv.postproc.worker.mediainfo;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Recording;
import org.jflicks.tv.postproc.worker.BaseWorker;
import org.jflicks.tv.postproc.worker.BaseWorkerJob;
import org.jflicks.util.LogUtil;

/**
 * This job starts a system job that runs mediainfo.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class FFmpegJob extends BaseWorkerJob implements JobListener {

    private long after;

    /**
     * Constructor with one required argument.
     *
     * @param r A Recording to check for commercials.
     * @param bw The Worker associated with this Job.
     */
    public FFmpegJob(Recording r, BaseWorker bw) {

        super(r, bw);
    }

    private long getAfter() {
        return (after);
    }

    private void setAfter(long l) {
        after = l;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        Recording r = getRecording();
        if (r != null) {

            String path = getRecordingPath(0);
            SystemJob job = SystemJob.getInstance("ffmpeg -i \"" + path
                + "\"");
            fireJobEvent(JobEvent.UPDATE, "command: <" + job.getCommand()
                + ">");

            // Don't run until this time because there won't be video to
            // grab the screen shot!  We tack on 30 seconds just to be safe.
            setAfter(r.getRealStart() + 30000);

            job.addJobListener(this);
            setSystemJob(job);
            JobContainer jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            LogUtil.log(LogUtil.INFO, "started: " + job.getCommand());
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

            long now = System.currentTimeMillis();
            if (now > getAfter()) {

                JobContainer jc = getJobContainer();
                if ((!jobStarted) && (jc != null)) {

                    // We might be done before it comes around again but lets
                    // use a flag just to be safe...
                    jobStarted = true;
                    jc.start();
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

            SystemJob job = getSystemJob();
            Recording r = getRecording();
            if ((r != null) && (job != null)) {

                String output = job.getOutputText();
                LogUtil.log(LogUtil.INFO, "output: " + output);
                if (output != null) {

                    String videoLine = null;
                    String audioLine = null;

                    int vindex = output.indexOf("Video:");
                    int aindex = output.indexOf("Audio:");
                    if ((vindex >= 0) && (aindex >= 0)) {

                        videoLine = output.substring(vindex, aindex);
                        audioLine = output.substring(aindex);
                    }

                    LogUtil.log(LogUtil.INFO, "videoLine: " + videoLine);
                    LogUtil.log(LogUtil.INFO, "audioLine: " + audioLine);
                    if (videoLine != null) {

                        if (videoLine.indexOf("1920x1080") != -1) {
                            r.setVideoFormat(NMSConstants.VIDEO_1080I);
                        } else if (videoLine.indexOf("1280x720") != -1) {
                            r.setVideoFormat(NMSConstants.VIDEO_720P);
                        } else if (videoLine.indexOf("x480") != -1) {
                            r.setVideoFormat(NMSConstants.VIDEO_480I);
                        }
                    }

                    // Now check the audio...
                    if (audioLine != null) {

                        if (audioLine.indexOf("5.1") != -1) {
                            r.setAudioFormat(
                                NMSConstants.AUDIO_DOLBY_DIGITAL_5_1);
                        } else if (audioLine.indexOf("2.0") != -1) {
                            r.setAudioFormat(
                                NMSConstants.AUDIO_DOLBY_DIGITAL_2_0);
                        } else if (audioLine.indexOf("stereo") != -1) {
                            r.setAudioFormat(
                                NMSConstants.AUDIO_DOLBY_DIGITAL_2_0);
                        }
                    }
                }
            }

            stop();
        }
    }

}


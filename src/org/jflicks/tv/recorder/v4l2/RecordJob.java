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
package org.jflicks.tv.recorder.v4l2;

import java.io.File;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobManager;
import org.jflicks.tv.recorder.BaseDeviceJob;
//import org.jflicks.tv.recorder.CopyJob;

/**
 * After finding, setting a channel, it's time to record from a v4l2
 * device.  The resulting video stream is stored to a local File and
 * the user can configure the time in seconds for the recording job to run.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordJob extends BaseDeviceJob {

    private File file;
    private long duration;
    private String audioTranscodeOptions;

    /**
     * Simple no argument constructor.
     */
    public RecordJob() {
    }

    /**
     * Audio options to pass to ffmpeg.
     *
     * @return A String instance.
     */
    public String getAudioTranscodeOptions() {
        return (audioTranscodeOptions);
    }

    /**
     * Audio options to pass to ffmpeg.
     *
     * @param s A String instance.
     */
    public void setAudioTranscodeOptions(String s) {
        audioTranscodeOptions = s;
    }

    /**
     * The time in seconds to record from a HDHR.
     *
     * @return The time in seconds.
     */
    public long getDuration() {
        return (duration);
    }

    /**
     * The time in seconds to record from a HDHR.
     *
     * @param l The time in seconds.
     */
    public void setDuration(long l) {
        duration = l;
    }

    /**
     * The stream from the HDHR needs a File as a destination.
     *
     * @return The File instance that details the location of the stream data.
     */
    public File getFile() {
        return (file);
    }

    /**
     * The stream from the HDHR needs a File as a destination.
     *
     * @param f The File instance that details the location of the stream data.
     */
    public void setFile(File f) {
        file = f;
    }

    private String fileToString() {

        String result = "/tmp/tmp.mpg";

        File f = getFile();
        if (f != null) {

            result = f.getPath();
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setTerminate(false);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        DeviceJob job = new DeviceJob(getDevice(), fileToString());
        job.setAudioCodec(getAudioTranscodeOptions());
        job.addJobListener(this);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();
        /*
        CopyJob job = new CopyJob(getDevice(), fileToString());
        job.addJobListener(this);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();
        */

        // End  a few seconds early...
        long l = getDuration() - 3;
        if (l == 0) {

            // This is just to record something...the duration was not set so
            // lets record for one minute.  This should not happen.
            l = 60 * 1000;

        } else {

            // Turn seconds into milliseconds.
            l *= 1000;
        }

        long now = System.currentTimeMillis();
        l += now;
        long sleep = getSleepTime();

        while (!isTerminate()) {

            JobManager.sleep(sleep);
            now = System.currentTimeMillis();
            if (now >= l) {

                stop();

            } else if ((now + 20000) > l) {

                // twenty second warning!
                sleep = 100;
            }
        }

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
        JobContainer jc = getJobContainer();
        if (jc != null) {

            // First lets stop listening since we are stopping it ourselves.
            DeviceJob dj = (DeviceJob) jc.getJob();
            dj.removeJobListener(this);
            jc.stop();
            setJobContainer(null);
            /*
            CopyJob cj = (CopyJob) jc.getJob();
            cj.removeJobListener(this);
            jc.stop();
            setJobContainer(null);
            */
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            // If we got here, then the recording stopped early.  We need to
            // stop too so at least the recording length will be correct.
            setTerminate(true);
        }
    }

}

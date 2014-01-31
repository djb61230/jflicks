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
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Timer;
import java.util.TimerTask;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobManager;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.recorder.BaseDeviceJob;
import org.jflicks.tv.recorder.CopyJob;
import org.jflicks.tv.recorder.StreamJob;
import org.jflicks.util.Util;

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
    private JobContainer readJobContainer;
    private String readMode;

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

    /**
     * The read mode of the device.
     *
     * @return A String from NMSConstants.
     */
    public String getReadMode() {
        return (readMode);
    }

    /**
     * The read mode of the device.
     *
     * @param s A String from NMSConstants.
     */
    public void setReadMode(String s) {
        readMode = s;
    }

    private JobContainer getReadJobContainer() {
        return (readJobContainer);
    }

    private void setReadJobContainer(JobContainer jc) {
        readJobContainer = jc;
    }

    private String fileToString() {

        String result = "/tmp/tmp.mpg";

        File f = getFile();
        if (f != null) {

            result = f.getPath();
        }

        return (result);
    }

    private boolean available(int port) {

        boolean result = false;

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {

            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            result = true;

        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return result;
    }

    private int computeStreamPort() {

        int result = -1;

        boolean found = false;
        for (int i = 4888; i < 5000; i++) {

            if (available(i)) {

                result = i;
                found = true;
                break;
            }
        }

        if (found) {
            fireJobEvent(JobEvent.UPDATE, "Using valid port " + result);
        } else {
            fireJobEvent(JobEvent.UPDATE, "Could not find a valid port!");
        }

        return (result);
    }

    private boolean isReadModeCopyOnly() {

        return (NMSConstants.READ_MODE_COPY_ONLY.equals(getReadMode()));
    }

    private boolean isReadModeUdp() {

        return (NMSConstants.READ_MODE_UDP.equals(getReadMode()));
    }

    private boolean isReadModeFFmpegDirect() {

        return (NMSConstants.READ_MODE_FFMPEG_DIRECT.equals(getReadMode()));
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

        if (isReadModeCopyOnly()) {

            CopyJob job = new CopyJob(getDevice(), fileToString());
            job.addJobListener(this);
            JobContainer jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            jc.start();

        } else if (isReadModeUdp()) {

            int sport = computeStreamPort();

            // Build the proper URL.
            String url = "'udp://localhost:" + sport
                + "?fifo_size=1000000&overrun_nonfatal=1'";

            DeviceJob job = new DeviceJob(url, fileToString());
            job.setAudioCodec(getAudioTranscodeOptions());
            job.addJobListener(this);
            JobContainer jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            jc.start();

            Timer timer = new Timer();
            timer.schedule(new StreamJobTask(sport, this), 1000);

        } else if (isReadModeFFmpegDirect()) {

            DeviceJob job = new DeviceJob(getDevice(), fileToString());
            job.setAudioCodec(getAudioTranscodeOptions());
            job.addJobListener(this);
            JobContainer jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            jc.start();
        }

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
            AbstractJob aj = (AbstractJob) jc.getJob();
            aj.removeJobListener(this);
            jc.stop();
            setJobContainer(null);
        }

        jc = getReadJobContainer();
        if (jc != null) {

            AbstractJob aj = (AbstractJob) jc.getJob();
            aj.removeJobListener(this);
            jc.stop();
            setReadJobContainer(null);
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

        } else if (event.getType() == JobEvent.UPDATE) {

            fireJobEvent(event);
        }
    }

    class StreamJobTask extends TimerTask {

        private int port;
        private RecordJob recordJob;

        public StreamJobTask(int i, RecordJob rj) {

            port = i;
            recordJob = rj;
        }

        public void run() {

            StreamJob job = new StreamJob();
            job.setDevice(getDevice());
            job.setHost("localhost");
            job.setPort(port);
            job.addJobListener(recordJob);
            JobContainer jc = JobManager.getJobContainer(job);
            setReadJobContainer(jc);
            jc.start();
        }

    }

}

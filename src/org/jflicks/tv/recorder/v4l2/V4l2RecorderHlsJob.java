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
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.jflicks.configure.NameValue;
import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Channel;
import org.jflicks.tv.recorder.HlsJob;
import org.jflicks.tv.recorder.StreamJob;

/**
 * This job supports the V4l2 recorder.  There are several steps to recording
 * from an V4l2.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class V4l2RecorderHlsJob extends AbstractJob implements JobListener {

    private V4l2Recorder v4l2Recorder;
    private ControlJob controlJob;
    private ChannelJob channelJob;
    private HlsJob hlsJob;
    private StreamJob streamJob;
    private JobContainer jobContainer;
    private JobContainer readJobContainer;

    /**
     * This job supports the V4l2Recorder plugin.
     *
     * @param r A given V4l2Recorder instance.
     */
    public V4l2RecorderHlsJob(V4l2Recorder r) {

        setV4l2Recorder(r);
    }

    private ControlJob getControlJob() {
        return (controlJob);
    }

    private void setControlJob(ControlJob j) {
        controlJob = j;
    }

    private ChannelJob getChannelJob() {
        return (channelJob);
    }

    private void setChannelJob(ChannelJob j) {
        channelJob = j;
    }

    private HlsJob getHlsJob() {
        return (hlsJob);
    }

    private void setHlsJob(HlsJob j) {
        hlsJob = j;
    }

    private StreamJob getStreamJob() {
        return (streamJob);
    }

    private void setStreamJob(StreamJob j) {
        streamJob = j;
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer jc) {
        jobContainer = jc;
    }

    private JobContainer getReadJobContainer() {
        return (readJobContainer);
    }

    private void setReadJobContainer(JobContainer jc) {
        readJobContainer = jc;
    }

    private V4l2Recorder getV4l2Recorder() {
        return (v4l2Recorder);
    }

    private void setV4l2Recorder(V4l2Recorder r) {
        v4l2Recorder = r;
    }

    private void log(int status, String message) {

        V4l2Recorder r = getV4l2Recorder();
        if ((r != null) && (message != null)) {

            r.log(status, message);
        }
    }

    private String getDevice() {

        String result = null;

        V4l2Recorder r = getV4l2Recorder();
        if (r != null) {

            result = r.getDevice();
        }

        return (result);
    }

    private int getAudioInput() {

        int result = 0;

        V4l2Recorder r = getV4l2Recorder();
        if (r != null) {

            result = r.getConfiguredAudioInputIndex();
        }

        return (result);
    }

    private int getVideoInput() {

        int result = 0;

        V4l2Recorder r = getV4l2Recorder();
        if (r != null) {

            result = r.getConfiguredVideoInputIndex();
        }

        return (result);
    }

    private String getControlArgument() {

        String result = null;

        V4l2Recorder r = getV4l2Recorder();
        if (r != null) {

            NameValue[] array = r.getConfiguredControls();
            if (array != null) {

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < array.length; i++) {

                    String tag = array[i].getName();
                    String val = array[i].getValue();

                    if ((tag != null) && (val != null)) {

                        tag = tag.trim();
                        val = val.trim();

                        if (sb.length() > 0) {

                            sb.append(",");
                        }

                        sb.append(tag + "=" + val);
                    }
                }

                if (sb.length() > 0) {

                    result = sb.toString();
                }
            }
        }

        return (result);
    }

    private String getFrequencyTable() {

        String result = null;

        V4l2Recorder r = getV4l2Recorder();
        if (r != null) {

            result = r.getConfiguredFrequencyTable();
        }

        return (result);
    }

    private String getChannelChangeScriptName() {

        String result = null;

        V4l2Recorder r = getV4l2Recorder();
        if (r != null) {

            result = r.getConfiguredChannelChangeScriptName();
        }

        return (result);
    }

    private String getChannel() {

        String result = null;

        V4l2Recorder r = getV4l2Recorder();
        if (r != null) {

            Channel c = r.getChannel();
            if (c != null) {

                result = c.getNumber();
            }
        }

        return (result);
    }

    private long getDuration() {

        long result = -1;

        V4l2Recorder r = getV4l2Recorder();
        if (r != null) {

            result = r.getDuration();
        }

        return (result);
    }

    private File getFile() {

        File result = null;

        V4l2Recorder r = getV4l2Recorder();
        if (r != null) {

            result = r.getDestination();
        }

        return (result);
    }

    private String getAudioTranscodeOptions() {

        String result = null;

        V4l2Recorder r = getV4l2Recorder();
        if (r != null) {

            result = r.getAudioTranscodeOptions();
        }

        return (result);
    }

    private String getReadMode() {

        String result = NMSConstants.READ_MODE_COPY_ONLY;

        V4l2Recorder r = getV4l2Recorder();
        if (r != null) {

            result = r.getConfiguredReadMode();
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

    /**
     * {@inheritDoc}
     */
    public void start() {

        File f = getFile();
        if (f != null) {

            setTerminate(false);

            ControlJob conj = new ControlJob();
            setControlJob(conj);
            conj.addJobListener(this);
            conj.setDevice(getDevice());
            conj.setAudioInput(getAudioInput());
            conj.setVideoInput(getVideoInput());
            conj.setControlArgument(getControlArgument());

            ChannelJob cj = new ChannelJob();
            setChannelJob(cj);
            cj.addJobListener(this);
            cj.setDevice(getDevice());
            cj.setChannel(getChannel());
            cj.setFrequencyTable(getFrequencyTable());
            cj.setScript(getChannelChangeScriptName());

            File parent = f.getParentFile();
            String prefix = f.getName();
            prefix = prefix.substring(0, prefix.lastIndexOf("."));

            if ((isReadModeCopyOnly()) || (isReadModeFFmpegDirect())) {

                // Well HLS can't use Copy Only so we have to use
                // ffmpeg to read from the device, which is not ideal.
                HlsJob hjob =
                    new HlsJob(getDevice(), prefix, parent, getDuration());
                hjob.setAudioCodec(getAudioTranscodeOptions());
                hjob.addJobListener(this);
                setHlsJob(hjob);
                setStreamJob(null);

            } else if (isReadModeUdp()) {

                int sport = computeStreamPort();

                // Build the proper URL.
                String url = "'udp://localhost:" + sport
                    + "?fifo_size=1000000&overrun_nonfatal=1'";
                HlsJob hjob = new HlsJob(url, prefix, parent, getDuration());
                hjob.setAudioCodec(getAudioTranscodeOptions());
                hjob.addJobListener(this);
                setHlsJob(hjob);

                StreamJob sjob = new StreamJob();
                sjob.setDevice(getDevice());
                sjob.setHost("localhost");
                sjob.setPort(sport);
                sjob.addJobListener(this);
                setStreamJob(sjob);
            }

            JobContainer jc = JobManager.getJobContainer(conj);
            setJobContainer(jc);
            jc.start();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());
        }

    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();
        }

        jc = getReadJobContainer();
        if (jc != null) {

            jc.stop();
        }

        V4l2Recorder r = getV4l2Recorder();
        if (r != null) {

            r.setRecording(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            if (event.getSource() == getControlJob()) {

                JobContainer jc = JobManager.getJobContainer(getChannelJob());
                setJobContainer(jc);
                jc.start();

            } else if (event.getSource() == getChannelJob()) {

                JobContainer jc = JobManager.getJobContainer(getHlsJob());
                setJobContainer(jc);
                jc.start();

                StreamJob sjob = getStreamJob();
                if (sjob != null) {

                    Timer timer = new Timer();
                    timer.schedule(new StreamJobTask(), 1000);
                }

            } else if (event.getSource() == getHlsJob()) {

                log(V4l2Recorder.INFO, "recording done at "
                    + new Date(System.currentTimeMillis()));
                stop();
            }

        } else if (event.getType() == JobEvent.UPDATE) {

            log(V4l2Recorder.DEBUG, event.getMessage());
        }
    }

    class StreamJobTask extends TimerTask {

        public StreamJobTask() {
        }

        public void run() {

            StreamJob job = getStreamJob();
            if (job != null) {

                JobContainer jc = JobManager.getJobContainer(job);
                setReadJobContainer(jc);
                jc.start();
            }
        }

    }
}

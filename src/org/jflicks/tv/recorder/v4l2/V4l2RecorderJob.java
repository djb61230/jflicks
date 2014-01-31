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
import java.util.Date;

import org.jflicks.configure.NameValue;
import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Channel;

/**
 * This job supports the V4l2 recorder.  There are several steps to recording
 * from an V4l2.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class V4l2RecorderJob extends AbstractJob implements JobListener {

    private V4l2Recorder v4l2Recorder;
    private ControlJob controlJob;
    private ChannelJob channelJob;
    private RecordJob recordJob;
    private JobContainer jobContainer;

    /**
     * This job supports the V4l2Recorder plugin.
     *
     * @param r A given V4l2Recorder instance.
     */
    public V4l2RecorderJob(V4l2Recorder r) {

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

    private RecordJob getRecordJob() {
        return (recordJob);
    }

    private void setRecordJob(RecordJob j) {
        recordJob = j;
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer jc) {
        jobContainer = jc;
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

    private String getReadMode() {

        String result = NMSConstants.READ_MODE_COPY_ONLY;

        V4l2Recorder r = getV4l2Recorder();
        if (r != null) {

            result = r.getConfiguredReadMode();
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

    private String getAudioTranscodeOptions() {

        String result = null;

        V4l2Recorder r = getV4l2Recorder();
        if (r != null) {

            result = r.getAudioTranscodeOptions();
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

    /**
     * {@inheritDoc}
     */
    public void start() {

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

        RecordJob rj = new RecordJob();
        setRecordJob(rj);
        rj.addJobListener(this);
        rj.setAudioTranscodeOptions(getAudioTranscodeOptions());
        rj.setDevice(getDevice());
        rj.setFile(getFile());
        rj.setDuration(getDuration());
        rj.setReadMode(getReadMode());

        JobContainer jc = JobManager.getJobContainer(conj);
        setJobContainer(jc);
        jc.start();
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

                JobContainer jc = JobManager.getJobContainer(getRecordJob());
                setJobContainer(jc);
                jc.start();

            } else if (event.getSource() == getRecordJob()) {

                log(V4l2Recorder.INFO, "recording done at "
                    + new Date(System.currentTimeMillis()));
                stop();
            }

        } else if (event.getType() == JobEvent.UPDATE) {

            log(V4l2Recorder.DEBUG, event.getMessage());
        }
    }

}

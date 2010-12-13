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
package org.jflicks.tv.recorder.dvb;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.tv.Channel;
import org.jflicks.tv.recorder.StreamJob;

/**
 * This job supports the DVB recorder.  There are two steps to streaming
 * from a DVB device.  The first to set the channel, the second to copy data
 * from the device node.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DvbStreamJob extends AbstractJob implements JobListener {

    private DvbRecorder dvbRecorder;
    private ChannelJob channelJob;
    private StreamJob streamJob;
    private JobContainer channelJobContainer;
    private JobContainer streamJobContainer;

    /**
     * This job supports the DvbRecorder service.
     *
     * @param r A given DvbRecorder instance.
     */
    public DvbStreamJob(DvbRecorder r) {

        setDvbRecorder(r);
    }

    private ChannelJob getChannelJob() {
        return (channelJob);
    }

    private void setChannelJob(ChannelJob j) {
        channelJob = j;
    }

    private StreamJob getStreamJob() {
        return (streamJob);
    }

    private void setStreamJob(StreamJob j) {
        streamJob = j;
    }

    private JobContainer getChannelJobContainer() {
        return (channelJobContainer);
    }

    private void setChannelJobContainer(JobContainer jc) {
        channelJobContainer = jc;
    }

    private JobContainer getStreamJobContainer() {
        return (streamJobContainer);
    }

    private void setStreamJobContainer(JobContainer jc) {
        streamJobContainer = jc;
    }

    private DvbRecorder getDvbRecorder() {
        return (dvbRecorder);
    }

    private void setDvbRecorder(DvbRecorder r) {
        dvbRecorder = r;
    }

    private void log(int status, String message) {

        DvbRecorder r = getDvbRecorder();
        if ((r != null) && (message != null)) {

            r.log(status, message);
        }
    }

    private String getDevice() {

        String result = null;

        DvbRecorder r = getDvbRecorder();
        if (r != null) {

            result = r.getDevice();
        }

        return (result);
    }

    private String getChannelChangeScriptName() {

        String result = null;

        DvbRecorder r = getDvbRecorder();
        if (r != null) {

            result = r.getConfiguredChannelChangeScriptName();
        }

        return (result);
    }

    private String getChannel() {

        String result = null;

        DvbRecorder r = getDvbRecorder();
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

        DvbRecorder r = getDvbRecorder();
        if (r != null) {

            result = r.getDuration();
        }

        return (result);
    }

    private String getHost() {

        String result = null;

        DvbRecorder r = getDvbRecorder();
        if (r != null) {

            result = r.getHost();
        }

        return (result);
    }

    private int getPort() {

        int result = 1234;

        DvbRecorder r = getDvbRecorder();
        if (r != null) {

            result = r.getPort();
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setTerminate(false);

        ChannelJob cj = new ChannelJob();
        setChannelJob(cj);
        cj.addJobListener(this);
        cj.setDevice(getDevice());
        cj.setChannel(getChannel());
        cj.setScript(getChannelChangeScriptName());

        StreamJob sj = new StreamJob();
        setStreamJob(sj);
        sj.addJobListener(this);
        sj.setDevice(getDevice());
        sj.setHost(getHost());
        sj.setPort(getPort());

        JobContainer jc = JobManager.getJobContainer(cj);
        setChannelJobContainer(jc);
        jc.start();

        // We need to wait a bit before we start to stream to give
        // the channel time to lock in.
        jc = JobManager.getJobContainer(sj);
        setStreamJobContainer(jc);
        final JobContainer fjc = jc;
        ActionListener streamPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                fjc.start();
            }
        };

        Timer streamTimer = new Timer(3000, streamPerformer);
        streamTimer.setRepeats(false);
        streamTimer.start();
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
        JobContainer jc = getStreamJobContainer();
        if (jc != null) {

            jc.stop();
        }

        jc = getChannelJobContainer();
        if (jc != null) {

            jc.stop();
        }

        DvbRecorder r = getDvbRecorder();
        if (r != null) {

            r.setRecording(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.UPDATE) {

            log(DvbRecorder.DEBUG, event.getMessage());
        }
    }

}

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

import java.io.File;
import java.util.Properties;

import org.jflicks.configure.BaseConfiguration;
import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Channel;
import org.jflicks.tv.recorder.BaseRecorder;
import org.jflicks.util.Util;

/**
 * Class that can record from a Linux DVB device.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DvbRecorder extends BaseRecorder {

    private JobContainer jobContainer;

    /**
     * Simple default constructor.
     */
    public DvbRecorder() {

        setTitle("DVB");
        setExtension("mpg");
        setQuickTunable(false);
    }

    /**
     * {@inheritDoc}
     */
    public void startRecording(Channel c, long duration, File destination,
        boolean live) {

        if (!isRecording()) {

            setStartedAt(System.currentTimeMillis());
            setChannel(c);
            setDuration(duration);
            setDestination(destination);
            setRecording(true);
            setRecordingLiveTV(live);

            DvbRecorderJob job = new DvbRecorderJob(this);
            JobContainer jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            jc.start();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecording() {

        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();
            setJobContainer(null);
            setRecording(false);
            setRecordingLiveTV(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void startStreaming(Channel c, String host, int port) {

        if (!isRecording()) {

            setChannel(c);
            setHost(host);
            setPort(port);
            setRecording(true);
            setRecordingLiveTV(true);

            DvbStreamJob job = new DvbStreamJob(this);
            JobContainer jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            jc.start();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stopStreaming() {

        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();
            setJobContainer(null);
            setRecording(false);
            setRecordingLiveTV(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void quickTune(Channel c) {
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer jc) {
        jobContainer = jc;
    }

    /**
     * We need to update the "Source" property of the Default Configuration
     * instance because there may be more than one DVB and this will make
     * this instance unique.  We will use the Device property to help us.
     */
    public void updateDefault() {

        BaseConfiguration c = (BaseConfiguration) getDefaultConfiguration();
        if (c != null) {

            c.setSource(c.getSource() + " " + getDevice());
        }
    }

    /**
     * Write out the configuration.
     *
     * @param c A given Configuration to write.
     */
    public void write(Configuration c) {

        if (c != null) {

            Properties p = toProperties(c);

            if (p != null) {

                Util.writeProperties(new File(getPropertiesName()), p);
            }
        }
    }

    /**
     * The DVB recorders might want to change channels using the name instead
     * of the channel number.
     *
     * @return True if one should use the channel name to tune to it.
     */
    public boolean isConfiguredUseChannelName() {

        boolean result = false;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue[] array = c.getNameValues();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    String name = array[i].getName();
                    if ((name != null) && (name.equals(
                        NMSConstants.USE_CHANNEL_NAME))) {

                        result = Util.str2boolean(array[i].getValue(), result);
                        break;
                    }
                }
            }
        }

        return (result);
    }

    /**
     * Convenience method to get the channel changing script name.
     *
     * @return A String path pointing to a script file.
     */
    public String getConfiguredChannelChangeScriptName() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue[] array = c.getNameValues();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    String name = array[i].getName();
                    if ((name != null) && (name.equals(
                        NMSConstants.CHANGE_CHANNEL_SCRIPT_NAME))) {

                        result = array[i].getValue();
                        if (result != null) {

                            result = result.trim();
                        }

                        if ((result != null) && (result.length() == 0)) {
                            result = null;
                        }

                        break;
                    }
                }
            }
        }

        return (result);
    }

}


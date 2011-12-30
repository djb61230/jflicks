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
        setExtension("ts");
        setQuickTunable(false);
    }

    private int getAdapterNumber(String s) {

        int result = -1;

        if (s != null) {

            int index = s.indexOf("adapter");
            if (index != -1) {

                index += 7;
                int lastIndex = s.indexOf("/", index);
                if ((lastIndex != -1) && (lastIndex >= index)) {

                    result =
                        Util.str2int(s.substring(index, lastIndex), result);
                }
            }
        }

        return (result);
    }

    private int getDvrNumber(String s) {

        int result = -1;

        if (s != null) {

            int index = s.indexOf("dvr");
            if (index != -1) {

                index += 3;
                result = Util.str2int(s.substring(index), result);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void performScan(Channel[] array, String type) {

        log(DEBUG, "performScan dvb called: " + array);
        for (int i = 0; i < array.length; i++) {

            log(DEBUG, "number: " + array[i].getNumber());
            log(DEBUG, "refnumber: " + array[i].getReferenceNumber());
            log(DEBUG, "------------------");
        }

        DvbScanJob scanner = new DvbScanJob(this, array, type);
        JobContainer jc = JobManager.getJobContainer(scanner);
        jc.start();
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsScan() {
        return (true);
    }

    /**
     * {@inheritDoc}
     */
    public Configuration getDefaultConfiguration() {

        BaseConfiguration bc =
            (BaseConfiguration) super.getDefaultConfiguration();

        if (bc != null) {

            // What we want to do is set the CHANGE_CHANNEL_SCRIPT_NAME
            // to something consistant.  The user can always change it
            // to whatever they want but in this moment of time we do
            // know the device name so we can customize it a lot nicer.
            String dname = getDevice();
            if (dname != null) {

                NameValue nv = bc.findNameValueByName(
                    NMSConstants.CHANGE_CHANNEL_SCRIPT_NAME);
                if (nv != null) {

                    int adapter = getAdapterNumber(dname);
                    int dvr = getDvrNumber(dname);
                    if ((adapter != -1) && (dvr != -1)) {

                        nv.setValue("azap -a " + adapter + " -f " + dvr
                            + " -c conf/adapter" + adapter + "_dvr" + dvr
                            + "_channels.conf -r");
                    }
                }
            }
        }

        return (bc);
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

    /**
     * Convenience method to get the channel changing ready text.
     *
     * @return A String showing that it is OK to change channels.
     */
    public String getConfiguredChannelChangeReadyText() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue[] array = c.getNameValues();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    String name = array[i].getName();
                    if ((name != null) && (name.equals(
                        NMSConstants.CHANGE_CHANNEL_READY_TEXT))) {

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


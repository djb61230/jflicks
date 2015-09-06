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
import java.util.ArrayList;
import java.util.Properties;

import org.jflicks.configure.BaseConfiguration;
import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Channel;
import org.jflicks.tv.recorder.BaseRecorder;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

/**
 * Class that can record from an V4l2.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class V4l2Recorder extends BaseRecorder {

    private JobContainer jobContainer;
    private String cardType;

    /**
     * Simple default constructor.
     */
    public V4l2Recorder() {

        setTitle("V4l2");
        setExtension("mpg");
        setQuickTunable(false);
    }

    /**
     * This is the "card type" property from a probe by the v4l2-ctl program.
     * The value will be the same for every instance of the same device on
     * a computer.  For example if there are two PVR-150 PCI cards installed,
     * both will have this card type.
     *
     * @return The card type String.
     */
    public String getCardType() {
        return (cardType);
    }

    /**
     * This is the "card type" property from a probe by the v4l2-ctl program.
     * The value will be the same for every instance of the same device on
     * a computer.  For example if there are two PVR-150 PCI cards installed,
     * both will have this card type.
     *
     * @param s The card type String.
     */
    public void setCardType(String s) {
        cardType = s;
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

            if (isHlsMode()) {

                V4l2RecorderHlsJob job = new V4l2RecorderHlsJob(this);
                JobContainer jc = JobManager.getJobContainer(job);
                setJobContainer(jc);
                jc.start();

            } else {

                V4l2RecorderJob job = new V4l2RecorderJob(this);
                JobContainer jc = JobManager.getJobContainer(job);
                setJobContainer(jc);
                jc.start();
            }
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

            LogUtil.log(LogUtil.DEBUG, "stream to <" + host + "> port <" + port + ">");
            LogUtil.log(LogUtil.DEBUG, "\t channel " + c);
            setChannel(c);
            setHost(host);
            setPort(port);
            setRecording(true);
            setRecordingLiveTV(true);

            V4l2StreamJob job = new V4l2StreamJob(this);
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

            LogUtil.log(LogUtil.DEBUG, "stopStreaming!");
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

    /**
     * {@inheritDoc}
     */
    public void performScan(Channel[] array, String type) {
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsScan() {
        return (false);
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer jc) {
        jobContainer = jc;
    }

    /**
     * The configuration properties name is generated from the card type
     * property.  We expect a file named "SOURCE.v4l2.properties".  The
     * source will be the "CardType" property from probing the device
     * with the exception that any spaces in the text will be replaced
     * by underscore.
     *
     * @return The name of the expected properties file.
     */
    public String getPropertiesName() {

        String result = null;

        String ct = getCardType();
        if (ct != null) {

            ct = ct.replace(" ", "_");
            result = "conf/" + ct + ".v4l2.properties";
        }

        return (result);
    }

    /**
     * We need to update the "Source" property of the Default Configuration
     * instance because there may be more than one V4l2 and this will make
     * this instance unique.  We will use the Device property to help us.
     */
    public void updateDefault() {

        BaseConfiguration c = (BaseConfiguration) getDefaultConfiguration();
        if (c != null) {

            c.setSource(c.getSource() + " " + getDevice());
        }

        // This is quite hackish but the only analog v4l2 device we
        // know that creates transport streams is the Hauppauge HD-PVR.
        // All others appear to make program streams.  So we should overwrite
        // the extension property for the HD-PVR.
        LogUtil.log(LogUtil.DEBUG, "card type:" + cardType);
        if ((cardType != null) && (cardType.indexOf("HD PVR") != -1)) {

            LogUtil.log(LogUtil.DEBUG, "setting extension to ts!");
            setExtension("ts");
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
     * Convenience method to get the audio input index.
     *
     * @return An int value.
     */
    public int getConfiguredAudioInputIndex() {

        int result = 0;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue[] array = c.getNameValues();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    String name = array[i].getName();
                    if ((name != null)
                        && (name.equals(NMSConstants.AUDIO_INPUT_NAME))) {

                        String tmp = array[i].getValue();
                        if (tmp != null) {

                            tmp = tmp.trim();
                            String[] choices = array[i].getChoices();
                            if (choices != null) {

                                for (int j = 0; j < choices.length; j++) {

                                    if (tmp.equals(choices[j])) {

                                        result = j;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }

        return (result);
    }

    /**
     * Convenience method to get the video input index.
     *
     * @return An int value.
     */
    public int getConfiguredVideoInputIndex() {

        int result = 0;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue[] array = c.getNameValues();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    String name = array[i].getName();
                    if ((name != null)
                        && (name.equals(NMSConstants.VIDEO_INPUT_NAME))) {

                        String tmp = array[i].getValue();
                        if (tmp != null) {

                            tmp = tmp.trim();
                            String[] choices = array[i].getChoices();
                            if (choices != null) {

                                for (int j = 0; j < choices.length; j++) {

                                    if (tmp.equals(choices[j])) {

                                        result = j;
                                    }
                                }
                            }
                        }
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
    public String getConfiguredFrequencyTable() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue[] array = c.getNameValues();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    String name = array[i].getName();
                    if ((name != null) && (name.equals(
                        NMSConstants.FREQUENCY_TABLE_NAME))) {

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

    private boolean isControl(NameValue nv) {

        boolean result = false;

        if (nv != null) {

            String name = nv.getName();
            if ((name != null)
                && (!name.equals(NMSConstants.AUDIO_INPUT_NAME))
                && (!name.equals(NMSConstants.AUDIO_TRANSCODE_OPTIONS))
                && (!name.equals(NMSConstants.READ_MODE))
                && (!name.equals(NMSConstants.HLS_MODE))
                && (!name.equals(NMSConstants.VIDEO_INPUT_NAME))
                && (!name.equals(NMSConstants.FREQUENCY_TABLE_NAME))
                && (!name.equals(NMSConstants.CUSTOM_CHANNEL_LIST))
                && (!name.equals(NMSConstants.CUSTOM_CHANNEL_LIST_TYPE))
                && (!name.equals(NMSConstants.CHANGE_CHANNEL_SCRIPT_NAME))
                && (!name.equals(NMSConstants.RECORDING_INDEXER_NAME))) {

                result = true;
            }
        }

        return (result);
    }

    /**
     * The V4l2 analog devices have a dynamic set of controls defined by
     * the driver.  By dynamic I mean each driver defines a different
     * set.  Here we can get what they are in our NameValue object.
     *
     * @return An array of NameValue instances.
     */
    public NameValue[] getConfiguredControls() {

        NameValue[] result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue[] array = c.getNameValues();
            if (array != null) {

                ArrayList<NameValue> l = new ArrayList<NameValue>();
                for (int i = 0; i < array.length; i++) {

                    if (isControl(array[i])) {

                        l.add(array[i]);
                    }
                }

                if (l.size() > 0) {

                    result = l.toArray(new NameValue[l.size()]);
                }
            }
        }

        return (result);
    }

    /**
     * Convenience method to get the read mode for this v4l2 device.
     *
     * @return A String defining the read mode from NMSConstants.
     */
    public String getConfiguredReadMode() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue[] array = c.getNameValues();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    String name = array[i].getName();
                    if ((name != null) && (name.equals(
                        NMSConstants.READ_MODE))) {

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


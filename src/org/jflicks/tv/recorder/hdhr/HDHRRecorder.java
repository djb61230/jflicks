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
package org.jflicks.tv.recorder.hdhr;

import java.io.File;

import org.jflicks.configure.BaseConfiguration;
import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.tv.Channel;
import org.jflicks.tv.recorder.BaseRecorder;

/**
 * Class that can record from an HDHR.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class HDHRRecorder extends BaseRecorder {

    private static final String FREQUENCY_TYPE = "Frequency Type";
    private static final String AUTO = "auto";
    private static final String US_BCAST = "us-bcast";
    private static final String US_CABLE = "us-cable";
    private static final String US_HRC = "us-hrc";
    private static final String US_IRC = "us-irc";

    private JobContainer jobContainer;
    private boolean useScanFile;
    private ScanFile scanFile;

    /**
     * Simple default constructor.
     */
    public HDHRRecorder() {

        setTitle("HDHomerun");
        setExtension("mpg");
        setQuickTunable(true);
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

            HDHRRecorderJob job = new HDHRRecorderJob(this);
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

            HDHRStreamJob job = new HDHRStreamJob(this);
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

        if (isRecordingLiveTV()) {

            boolean doFrequency = true;
            Channel old = getChannel();
            if ((old != null) && (c != null)) {

                if (old.getFrequency() == c.getFrequency()) {

                    doFrequency = false;
                }
            }

            setChannel(c);

            HDHRQuickTuneJob job = new HDHRQuickTuneJob(this, doFrequency);
            JobContainer jc = JobManager.getJobContainer(job);
            jc.start();
        }
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer jc) {
        jobContainer = jc;
    }

    private boolean isUseScanFile() {

        if (!useScanFile) {

            // It's set NOT to use, so let's check the existence of the
            // scan file.
            File conf = new File("conf");
            if ((conf.exists()) && (conf.isDirectory())) {

                File scan = new File(conf, getDevice() + "-scan.log");
                if ((scan.exists()) && (scan.isFile())) {

                    useScanFile = true;
                    setScanFile(new ScanFile(getDevice()));

                } else {

                    scan = new File(conf, "hdhr-scan.log");
                    if ((scan.exists()) && (scan.isFile())) {

                        useScanFile = true;
                        setScanFile(new ScanFile(getDevice()));
                    }
                }
            }
        }

        return (useScanFile);
    }

    private ScanFile getScanFile() {
        return (scanFile);
    }

    private void setScanFile(ScanFile sf) {
        scanFile = sf;
    }

    /**
     * We need to update the "Source" property of the Default Configuration
     * instance because there may be more than one HDHR and this will make
     * this instance unique.  We will use the Device property to help us.
     */
    public void updateDefault() {

        BaseConfiguration c = (BaseConfiguration) getDefaultConfiguration();
        if (c != null) {

            c.setSource(c.getSource() + " " + getDevice());
        }
    }

    private int getFromScanFile(String s) {

        int result = -1;

        ScanFile sf = getScanFile();
        if ((s != null) && (sf != null)) {

            result = sf.getFrequency(s);
        }

        return (result);
    }

    /**
     * Convenience method to get the proper frequency
     *
     * @return A frequency as an int.
     */
    public int getFrequency() {

        int result = -1;

        Channel c = getChannel();
        if (c != null) {

            if (isUseScanFile()) {

                result = getFromScanFile(c.getNumber());

            } else {

                result = c.getFrequency();
            }
        }

        return (result);
    }

    /**
     * Convenience method to see the configured frequency type.
     *
     * @return The setting as a String.
     */
    public String getConfiguredFrequencyType() {

        String result = "auto";

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue[] array = c.getNameValues();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    String name = array[i].getName();
                    if ((name != null) && (name.equals(FREQUENCY_TYPE))) {

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


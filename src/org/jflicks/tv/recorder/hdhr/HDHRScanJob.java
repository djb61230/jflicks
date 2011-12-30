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
import java.io.IOException;
import java.util.Arrays;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Channel;
import org.jflicks.util.Util;

/**
 * This job will create the HDHR channel scan config file that we invented.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class HDHRScanJob extends AbstractJob implements JobListener {

    private HDHRRecorder hdhrRecorder;
    private Channel[] channels;
    private ScanJob scanJob;
    private JobContainer jobContainer;
    private String type;

    /**
     * This job does all the work to create a HDHR scan config file.
     *
     * @param r A given HDHRRecorder instance.
     * @param array An array of possible Channel instances we should be able
     * to tune.
     * @param type Scan for this type of frequency.
     */
    public HDHRScanJob(HDHRRecorder r, Channel[] array, String type) {

        setHDHRRecorder(r);
        setChannels(array);
        setType(type);
    }

    private HDHRRecorder getHDHRRecorder() {
        return (hdhrRecorder);
    }

    private void setHDHRRecorder(HDHRRecorder l) {
        hdhrRecorder = l;
    }

    private Channel[] getChannels() {
        return (channels);
    }

    private void setChannels(Channel[] array) {
        channels = array;
    }

    private ScanJob getScanJob() {
        return (scanJob);
    }

    private void setScanJob(ScanJob j) {
        scanJob = j;
    }

    private String getType() {
        return (type);
    }

    private void setType(String s) {
        type = s;
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer jc) {
        jobContainer = jc;
    }

    private String getDevice() {

        String result = null;

        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            result = r.getDevice();
        }

        return (result);
    }

    private String getId() {

        String result = null;

        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            result = r.getDevice();
            if (result != null) {

                result = result.substring(0, result.indexOf("-"));
            }
        }

        return (result);
    }

    private int getTuner() {

        int result = -1;

        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            String tmp = r.getDevice();
            if (tmp != null) {

                tmp = tmp.substring(tmp.indexOf("-") + 1);
                result = Util.str2int(tmp, result);
            }
        }

        return (result);
    }

    private String getFrequencyType() {

        String result = null;

        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            result = r.getConfiguredFrequencyType();
        }

        return (result);
    }

    private void log(int status, String message) {

        HDHRRecorder r = getHDHRRecorder();
        if ((r != null) && (message != null)) {

            r.log(status, message);
            NMS n = r.getNMS();
            if (n != null) {

                n.sendMessage(NMSConstants.MESSAGE_RECORDER_SCAN_UPDATE
                    + " " + message);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setTerminate(false);

        ScanJob sj = new ScanJob();
        setScanJob(sj);
        sj.addJobListener(this);
        sj.setId(getId());
        sj.setTuner(getTuner());
        String usertype = getType();
        if (usertype == null) {
            usertype = getFrequencyType();
        }
        sj.setFrequencyType(usertype);

        log(HDHRRecorder.DEBUG, "starting scan job...");
        JobContainer jc = JobManager.getJobContainer(sj);
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
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            if (event.getSource() == getScanJob()) {

                ParseScanFile psf = new ParseScanFile(getScanJob().getFile());
                Channel[] array = getChannels();
                if ((array != null) && (array.length > 0)) {

                    Arrays.sort(array);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < array.length; i++) {

                        String ref = array[i].getReferenceNumber();
                        int freq = psf.get(ref);
                        if (freq != -1) {

                            String line = array[i].getNumber() + "="
                                + ref + ":" + freq;
                            sb.append(line);
                            sb.append("\n");

                        } else {

                            log(HDHRRecorder.DEBUG, "Not Found!");
                        }
                    }

                    if (sb.length() > 0) {

                        log(HDHRRecorder.DEBUG, "-------------------------");
                        log(HDHRRecorder.DEBUG, sb.toString());
                        log(HDHRRecorder.DEBUG, "-------------------------");
                        File conf = new File("conf");
                        if ((conf.exists()) && (conf.isDirectory())) {

                            File scan = new File(conf,
                                getDevice() + "-scan.conf");
                            try {

                                Util.writeTextFile(scan, sb.toString());
                                log(HDHRRecorder.DEBUG, "Writing "
                                    + scan.getPath());

                            } catch (IOException ex) {

                                log(HDHRRecorder.DEBUG, ex.getMessage());
                            }
                        }
                    }
                }
            }

        } else if (event.getType() == JobEvent.UPDATE) {

            log(HDHRRecorder.DEBUG, event.getMessage());
        }
    }

}

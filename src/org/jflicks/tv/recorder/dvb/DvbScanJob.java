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
 * This job will create the Dvb channel scan config file that we invented.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DvbScanJob extends AbstractJob implements JobListener {

    private DvbRecorder dvbRecorder;
    private Channel[] channels;
    private ScanJob scanJob;
    private JobContainer jobContainer;

    /**
     * This job does all the work to create a Dvb scan config file.
     *
     * @param r A given DvbRecorder instance.
     * @param array An array of possible Channel instances we should be able
     * to tune.
     */
    public DvbScanJob(DvbRecorder r, Channel[] array) {

        setDvbRecorder(r);
        setChannels(array);
    }

    private DvbRecorder getDvbRecorder() {
        return (dvbRecorder);
    }

    private void setDvbRecorder(DvbRecorder l) {
        dvbRecorder = l;
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

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer jc) {
        jobContainer = jc;
    }

    private String getDevice() {

        String result = null;

        DvbRecorder r = getDvbRecorder();
        if (r != null) {

            result = r.getDevice();
        }

        return (result);
    }

    private void log(int status, String message) {

        DvbRecorder r = getDvbRecorder();
        if ((r != null) && (message != null)) {

            r.log(status, message);
            NMS n = r.getNMS();
            if (n != null) {

                message = NMSConstants.MESSAGE_RECORDER_SCAN_UPDATE
                    + " " + message;
                n.sendMessage(message);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setTerminate(false);

        String[] args = {
            "w_scan", "-c", "US", "-A", "1", "-o", "7", "-f", "a", "-X",
            "-O", "0"
        };
        ScanJob sj = new ScanJob(args);
        setScanJob(sj);
        sj.addJobListener(this);

        log(DvbRecorder.DEBUG, "starting scan job...");
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

    private String process(String number, String ref, String[] lines) {

        String result = null;

        if ((number != null) && (ref != null) && (lines != null)) {

            String line = null;
            for (int i = 0; i < lines.length; i++) {

                if (lines[i].startsWith(ref)) {

                    line = lines[i];
                    break;
                }
            }

            if (line != null) {

                int index = line.indexOf(":");
                if (index != -1) {

                    result = number + line.substring(index);
                }
            }
        }

        return (result);
    }

    private String getChannelScanFileName() {

        String result = null;

        String dname = getDevice();
        if (dname != null) {

            int adapter = getAdapterNumber(dname);
            int dvr = getDvrNumber(dname);
            if ((adapter != -1) && (dvr != -1)) {

                result = "adapter" + adapter + "_dvr" + dvr + "_channels.conf";
            }
        }

        return (result);
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
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            if (event.getSource() == getScanJob()) {

                Channel[] array = getChannels();
                if ((array != null) && (array.length > 0)) {

                    String text = getScanJob().getFileText();
                    String[] lines = text.split("\n");
                    Arrays.sort(array);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < array.length; i++) {

                        String num = array[i].getNumber();
                        String ref = array[i].getReferenceNumber();
                        log(DvbRecorder.DEBUG, "Checking ref <" + ref + ">");
                        String line = process(num, ref, lines);
                        if (line != null) {

                            log(DvbRecorder.DEBUG, "Adding <" + line + ">");
                            sb.append(line);
                            sb.append("\n");

                        } else {

                            log(DvbRecorder.DEBUG, "Not Found!");
                        }
                    }

                    if (sb.length() > 0) {

                        log(DvbRecorder.DEBUG, "-------------------------");
                        log(DvbRecorder.DEBUG, sb.toString());
                        log(DvbRecorder.DEBUG, "-------------------------");
                        File conf = new File("conf");
                        if ((conf.exists()) && (conf.isDirectory())) {

                            File scan =
                                new File(conf, getChannelScanFileName());

                            try {

                                Util.writeTextFile(scan, sb.toString());
                                log(DvbRecorder.DEBUG, "Writing "
                                    + scan.getPath());

                            } catch (IOException ex) {

                                log(DvbRecorder.DEBUG, ex.getMessage());
                            }
                        }
                    }
                }
            }

        } else if (event.getType() == JobEvent.UPDATE) {

            log(DvbRecorder.DEBUG, event.getMessage());
        }
    }

}

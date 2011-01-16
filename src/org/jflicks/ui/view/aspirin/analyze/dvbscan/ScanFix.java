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
package org.jflicks.ui.view.aspirin.analyze.dvbscan;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.tv.recorder.ScanJob;
import org.jflicks.ui.view.aspirin.analyze.BaseFix;
import org.jflicks.util.Util;

/**
 * A Fix implementation that can scan for channels and write out a proper
 * config file.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ScanFix extends BaseFix implements JobListener {

    private DvbPath[] dvbPaths;
    private ScanJob scanJob;
    private JobContainer jobContainer;

    /**
     * Simple no argument constructor.
     */
    public ScanFix() {
    }

    /**
     * An array of DvbPath instances representing recording devices.
     *
     * @return An array of DvbPath instances.
     */
    public DvbPath[] getDvbPaths() {

        DvbPath[] result = null;

        if (dvbPaths != null) {

            result = Arrays.copyOf(dvbPaths, dvbPaths.length);
        }

        return (result);
    }

    /**
     * An array of DvbPath instances representing recording devices.
     *
     * @param array An array of DvbPath instances.
     */
    public void setDvbPaths(DvbPath[] array) {

        if (array != null) {
            dvbPaths = Arrays.copyOf(array, array.length);
        } else {
            dvbPaths = null;
        }
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

    /**
     * {@inheritDoc}
     */
    public void start() {

        setTerminate(false);

        String[] args = {
            "w_scan", "-c", "US", "-A", "3", "-o", "7", "-f", "a", "-X", "-O", "0"
        };

        ScanJob job = new ScanJob(args);
        setScanJob(job);
        job.addJobListener(this);
        JobContainer jc = JobManager.getJobContainer(job);
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

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);

        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();
            setJobContainer(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            ScanJob job = getScanJob();
            if (job != null) {

                // I guess we are assuming one w_scan gets all channels
                // for every DVB device.  We are limited on our testing...
                String text = job.getFileText();
                DvbPath[] dvbs = getDvbPaths();
                if ((text != null) && (dvbs != null) && (dvbs.length > 0)) {

                    File conf = new File(dvbs[0].getConfPath());
                    if ((conf.exists()) && (conf.isDirectory())) {

                        for (int i = 0; i < dvbs.length; i++) {

                            File f = new File(conf,
                                dvbs[i].getChannelScanFileName());

                            try {

                                Util.writeTextFile(f, text);

                            } catch (IOException ex) {

                                fireJobEvent(JobEvent.UPDATE, ex.getMessage());
                            }
                        }
                    }
                }
            }

            stop();

        } else if (event.getType() == JobEvent.UPDATE) {

            fireJobEvent(event);
        }
    }

}

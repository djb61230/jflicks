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
package org.jflicks.tv.recorder.jhdhr;

import java.io.File;
import java.util.Date;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

/**
 * This job supports the HDHR recorder.  There are several steps to recording
 * from an HDHR.  This class will complete the steps by executing 4 command
 * line jobs.
 *
 * First is to tune the HDHR to the proper frequency.  Second set the
 * "program".  Third record to a local File.  And lastly when the time has
 * expired to tune to "none".
 *
 * All input parameters are available in the HDHRRecorder property that is
 * required for this job.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class HDHRRecorderJob extends AbstractJob implements JobListener {

    private HDHRRecorder hdhrRecorder;
    private RecordJob recordJob;
    private JobContainer jobContainer;

    /**
     * This job supports the HDHRRecorder plugin.
     *
     * @param r A given HDHRRecorder instance.
     */
    public HDHRRecorderJob(HDHRRecorder r) {

        setHDHRRecorder(r);
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

    private HDHRRecorder getHDHRRecorder() {
        return (hdhrRecorder);
    }

    private void setHDHRRecorder(HDHRRecorder l) {
        hdhrRecorder = l;
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

    private String getModel() {

        String result = null;

        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            result = r.getModel();
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

    private int getFrequency() {

        int result = -1;

        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            result = r.getFrequency();
        }

        return (result);
    }

    private String getFrequencyType() {

        String result = "auto";

        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            String tmp = r.getConfiguredFrequencyType();
            if (tmp != null) {
                result = tmp;
            }
        }

        return (result);
    }

    private String getProgram() {

        String result = null;

        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            result = r.getProgram();
        }

        return (result);
    }

    private long getDuration() {

        long result = -1;

        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            result = r.getDuration();
        }

        return (result);
    }

    private File getFile() {

        File result = null;

        HDHRRecorder r = getHDHRRecorder();
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

        RecordJob rj = new RecordJob();
        setRecordJob(rj);
        rj.addJobListener(this);
        rj.setId(getId());
        rj.setTuner(getTuner());
        rj.setFile(getFile());
        rj.setDuration(getDuration());

        // Set the frequency.
        HDHRConfig config = new HDHRConfig();
        config.applyFrequency(getHDHRRecorder(), "" + getFrequency());

        // We have to sleep a bit before we set the program.
        try {

            Thread.sleep(1000);

        } catch (Exception ex) {
        }

        StreamInfo sinfo = new StreamInfo();
        sinfo.setProgram(getProgram());

        config.program(getHDHRRecorder(), sinfo.getProgramId(getHDHRRecorder()));

        JobContainer jc = JobManager.getJobContainer(rj);
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
        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            r.setRecording(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            if (event.getSource() == getRecordJob()) {

                HDHRConfig config = new HDHRConfig();
                config.applyFrequency(getHDHRRecorder(), "none");

                LogUtil.log(LogUtil.INFO, "recording done at " + new Date(System.currentTimeMillis()));
                stop();
            }

        } else if (event.getType() == JobEvent.UPDATE) {

            LogUtil.log(LogUtil.DEBUG, event.getMessage());
        }
    }

}
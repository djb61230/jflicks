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
 * This job supports the HDTC recorder. Much simpler as we just record.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class HDHRRecorderTranscodeJob extends AbstractJob
    implements JobListener {

    private HDHRRecorder hdhrRecorder;
    private RecordTranscodeJob recordTranscodeJob;
    private JobContainer jobContainer;

    /**
     * This job supports the HDHRRecorder plugin.
     *
     * @param r A given HDHRRecorder instance.
     */
    public HDHRRecorderTranscodeJob(HDHRRecorder r) {

        setHDHRRecorder(r);
    }

    private RecordTranscodeJob getRecordTranscodeJob() {
        return (recordTranscodeJob);
    }

    private void setRecordTranscodeJob(RecordTranscodeJob j) {
        recordTranscodeJob = j;
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

    private String getIpAddress() {

        String result = null;

        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            result = r.getIpAddress();
        }

        return (result);
    }

    private String getAudioTranscodeOptions() {

        String result = null;

        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            result = r.getAudioTranscodeOptions();
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

        RecordTranscodeJob rtj = new RecordTranscodeJob();
        setRecordTranscodeJob(rtj);
        rtj.addJobListener(this);
        rtj.setIpAddress(getIpAddress());
        rtj.setProgram(getProgram());
        rtj.setAudioTranscodeOptions(getAudioTranscodeOptions());
        rtj.setTuner(getTuner());
        rtj.setFile(getFile());
        rtj.setDuration(getDuration());

        JobContainer jc = JobManager.getJobContainer(rtj);
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

            LogUtil.log(LogUtil.INFO, "recording done at "
                + new Date(System.currentTimeMillis()));
            stop();

        } else if (event.getType() == JobEvent.UPDATE) {

            LogUtil.log(LogUtil.DEBUG, event.getMessage());
        }
    }

}

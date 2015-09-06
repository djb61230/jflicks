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

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.tv.Channel;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class HDHRQuickTuneJob extends AbstractJob implements JobListener {

    private HDHRRecorder hdhrRecorder;
    private StreamJob streamJob;
    private JobContainer jobContainer;
    private boolean changeFrequency;

    /**
     * This job supports the HDHRRecorder plugin.
     *
     * @param r A given HDHRRecorder instance.
     * @param doFrequency Don't change frequency if we are just tuning to
     * another subchannel.
     */
    public HDHRQuickTuneJob(HDHRRecorder r, boolean doFrequency) {

        setHDHRRecorder(r);
        setChangeFrequency(doFrequency);
    }

    private StreamJob getStreamJob() {
        return (streamJob);
    }

    private void setStreamJob(StreamJob j) {
        streamJob = j;
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

    private boolean isChangeFrequency() {
        return (changeFrequency);
    }

    private void setChangeFrequency(boolean b) {
        changeFrequency = b;
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

            Channel c = r.getChannel();
            if (c != null) {

                result = c.getFrequency();
            }
        }

        return (result);
    }

    private String getProgram() {

        String result = null;

        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            Channel c = r.getChannel();
            if (c != null) {

                result = c.getNumber();
            }
        }

        return (result);
    }

    private String getHost() {

        String result = null;

        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            result = r.getHost();
        }

        return (result);
    }

    private int getPort() {

        int result = 1234;

        HDHRRecorder r = getHDHRRecorder();
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

        StreamJob sj = new StreamJob();
        setStreamJob(sj);
        sj.addJobListener(this);
        sj.setId(getId());
        sj.setTuner(getTuner());
        sj.setHost(getHost());
        sj.setPort(getPort());

        HDHRConfig config = new HDHRConfig();
        if (isChangeFrequency()) {

            // Set the frequency.
            config.applyFrequency(getHDHRRecorder(), "" + getFrequency());
        }

        StreamInfo sinfo = new StreamInfo();
        sinfo.setProgram(getProgram());

        config.program(getHDHRRecorder(), sinfo.getProgramId(getHDHRRecorder()));

        LogUtil.log(LogUtil.DEBUG, "starting job since same freq");
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

        // Let's set the frequency to none now...
        /*
        jc = JobManager.getJobContainer(getNoneFrequencyJob());
        setJobContainer(jc);
        jc.start();
        */

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

        } else if (event.getType() == JobEvent.UPDATE) {

            LogUtil.log(LogUtil.DEBUG, event.getMessage());
        }
    }

}

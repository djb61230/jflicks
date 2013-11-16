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
import java.util.Date;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.tv.recorder.HlsJob;
import org.jflicks.util.Hostname;
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
public class HDHRRecorderHlsJob extends AbstractJob implements JobListener {

    private HDHRRecorder hdhrRecorder;
    private FrequencyJob frequencyJob;
    private StreamInfoJob streamInfoJob;
    private ProgramJob programJob;
    private StreamJob streamJob;
    private HlsJob hlsJob;
    private FrequencyJob noneFrequencyJob;
    private JobContainer jobContainer;

    /**
     * This job supports the HDHRRecorder plugin.
     *
     * @param r A given HDHRRecorder instance.
     */
    public HDHRRecorderHlsJob(HDHRRecorder r) {

        setHDHRRecorder(r);
    }

    private StreamJob getStreamJob() {
        return (streamJob);
    }

    private void setStreamJob(StreamJob j) {
        streamJob = j;
    }

    private HlsJob getHlsJob() {
        return (hlsJob);
    }

    private void setHlsJob(HlsJob j) {
        hlsJob = j;
    }

    private FrequencyJob getFrequencyJob() {
        return (frequencyJob);
    }

    private void setFrequencyJob(FrequencyJob j) {
        frequencyJob = j;
    }

    private StreamInfoJob getStreamInfoJob() {
        return (streamInfoJob);
    }

    private void setStreamInfoJob(StreamInfoJob j) {
        streamInfoJob = j;
    }

    private ProgramJob getProgramJob() {
        return (programJob);
    }

    private void setProgramJob(ProgramJob j) {
        programJob = j;
    }

    private FrequencyJob getNoneFrequencyJob() {
        return (noneFrequencyJob);
    }

    private void setNoneFrequencyJob(FrequencyJob j) {
        noneFrequencyJob = j;
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

    private String getHost() {
        return (Hostname.getHostAddress());
    }

    private int getPort() {

        int result = 1234;

        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            String device = r.getDevice();
            if (device != null) {

                String front = device.substring(0, device.lastIndexOf("-"));
                String back = device.substring(device.lastIndexOf("-") + 1);
                result = hexstr2int(front, 16, 1000);
                int iback = hexstr2int(back, 10, 0);
                while (result > 10000) {

                    result /= 10;
                }

                result += iback;
            }
        }

        return (result);
    }

    private static int hexstr2int(String s, int base, int defaultValue) {

        int result = defaultValue;

        if (s != null) {

            try {

                result = Integer.parseInt(s, base);

            } catch (Exception ex) {

                result = defaultValue;
            }
        }

        return (result);
    }

    private void log(int status, String message) {

        HDHRRecorder r = getHDHRRecorder();
        if ((r != null) && (message != null)) {

            r.log(status, message);
        }
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

        File f = getFile();
        if (f != null) {

            setTerminate(false);

            FrequencyJob fj = new FrequencyJob();
            setFrequencyJob(fj);
            fj.addJobListener(this);
            fj.setId(getId());
            fj.setTuner(getTuner());
            fj.setFrequency(getFrequency());
            fj.setType(getFrequencyType());

            StreamInfoJob sij = new StreamInfoJob();
            setStreamInfoJob(sij);
            sij.addJobListener(this);
            sij.setId(getId());
            sij.setTuner(getTuner());
            sij.setProgram(getProgram());

            ProgramJob pj = new ProgramJob();
            setProgramJob(pj);
            pj.addJobListener(this);
            pj.setId(getId());
            pj.setTuner(getTuner());

            StreamJob sj = new StreamJob();
            setStreamJob(sj);
            sj.addJobListener(this);
            sj.setId(getId());
            sj.setTuner(getTuner());
            sj.setHost(getHost());
            sj.setPort(getPort());

            File parent = f.getParentFile();
            String url = "udp://@" + getHost() + ":" + getPort();
            String prefix = f.getName();
            prefix = prefix.substring(0, prefix.lastIndexOf("."));
            HlsJob hjob = new HlsJob(url, prefix, parent, getDuration());
            hjob.addJobListener(this);
            setHlsJob(hjob);

            FrequencyJob nfj = new FrequencyJob();
            setNoneFrequencyJob(nfj);
            nfj.addJobListener(this);
            nfj.setId(getId());
            nfj.setTuner(getTuner());
            nfj.setFrequency(-1);

            JobContainer jc = JobManager.getJobContainer(getHlsJob());
            jc.start();

            jc = JobManager.getJobContainer(fj);
            setJobContainer(jc);
            jc.start();
        }
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

            System.out.println("setting recording to false");
            r.setRecording(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            if (event.getSource() == getFrequencyJob()) {

                JobContainer jc =
                    JobManager.getJobContainer(getStreamInfoJob());
                setJobContainer(jc);
                jc.start();

            } else if (event.getSource() == getStreamInfoJob()) {

                StreamInfoJob sij = (StreamInfoJob) event.getSource();
                ProgramJob pj = getProgramJob();
                pj.setProgram(sij.getProgramId());
                JobContainer jc = JobManager.getJobContainer(pj);
                setJobContainer(jc);
                jc.start();

            } else if (event.getSource() == getProgramJob()) {

                JobContainer jc = JobManager.getJobContainer(getStreamJob());
                setJobContainer(jc);
                jc.start();

            } else if (event.getSource() == getStreamJob()) {

                //JobContainer jc = JobManager.getJobContainer(getHlsJob());
                //setJobContainer(jc);
                //jc.start();

            } else if (event.getSource() == getHlsJob()) {

                System.out.println("HLS ended");
                JobContainer jc =
                    JobManager.getJobContainer(getNoneFrequencyJob());
                setJobContainer(jc);
                jc.start();

            } else if (event.getSource() == getNoneFrequencyJob()) {

                log(HDHRRecorder.INFO, "recording done at "
                    + new Date(System.currentTimeMillis()));
                stop();
            }

        } else if (event.getType() == JobEvent.UPDATE) {

            log(HDHRRecorder.DEBUG, event.getMessage());
        }
    }

}

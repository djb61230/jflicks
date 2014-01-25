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
 * This job supports the HDTC recorder.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class HDHRRecorderTranscodeHlsJob extends AbstractJob
    implements JobListener {

    private HDHRRecorder hdhrRecorder;
    private HlsJob hlsJob;
    private JobContainer jobContainer;

    /**
     * This job supports the HDHRRecorder plugin.
     *
     * @param r A given HDHRRecorder instance.
     */
    public HDHRRecorderTranscodeHlsJob(HDHRRecorder r) {

        setHDHRRecorder(r);
    }

    private HlsJob getHlsJob() {
        return (hlsJob);
    }

    private void setHlsJob(HlsJob j) {
        hlsJob = j;
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

    private void log(int status, String message) {

        HDHRRecorder r = getHDHRRecorder();
        if ((r != null) && (message != null)) {

            r.log(status, message);
        }
    }

    private String getIpAddress() {

        String result = null;

        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            result = r.getIpAddress();
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

    private String getVideoTranscodeOptions() {

        String result = null;

        HDHRRecorder r = getHDHRRecorder();
        if (r != null) {

            result = r.getConfiguredVideoTranscodeOptions();
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

    /**
     * {@inheritDoc}
     */
    public void start() {

        File f = getFile();
        if (f != null) {

            setTerminate(false);

            String url = "http://"
                + getIpAddress()
                + ":5004/tuner"
                + getTuner()
                + "/v"
                + getProgram()
                + "?transcode=heavy";

            File parent = f.getParentFile();
            String prefix = f.getName();
            prefix = prefix.substring(0, prefix.lastIndexOf("."));
            HlsJob hjob = new HlsJob(url, prefix, parent, getDuration());
            hjob.setVideoCodec(getVideoTranscodeOptions());
            hjob.setAudioCodec(getAudioTranscodeOptions());
            hjob.addJobListener(this);
            setHlsJob(hjob);

            JobContainer jc = JobManager.getJobContainer(getHlsJob());
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

            log(HDHRRecorder.INFO, "recording done at "
                + new Date(System.currentTimeMillis()));
            stop();

        } else if (event.getType() == JobEvent.UPDATE) {

            log(HDHRRecorder.DEBUG, event.getMessage());
        }
    }

}

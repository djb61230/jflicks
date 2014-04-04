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

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;

/**
 * Transfer a file using curl.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DeviceJob extends AbstractJob implements JobListener {

    private SystemJob systemJob;
    private JobContainer jobContainer;
    private String input;
    private String output;
    private String videoCodec;
    private String audioCodec;

    /**
     * Simple one argument constructor.
     *
     * @param f A given File.
     */
    public DeviceJob(String input, String output) {

        setInput(input);
        setOutput(output);
        setVideoCodec("copy");
        setAudioCodec("copy");
    }

    /**
     * The input String as a URL or path.
     *
     * @return The input String.
     */
    public String getInput() {
        return (input);
    }

    private void setInput(String s) {
        input = s;
    }

    /**
     * The output String as a file path.
     *
     * @return The output String.
     */
    public String getOutput() {
        return (output);
    }

    private void setOutput(String s) {
        output = s;
    }

    public String getVideoCodec() {
        return (videoCodec);
    }

    public void setVideoCodec(String s) {
        videoCodec = s;
    }

    public String getAudioCodec() {
        return (audioCodec);
    }

    public void setAudioCodec(String s) {
        audioCodec = s;
    }

    private SystemJob getSystemJob() {
        return (systemJob);
    }

    private void setSystemJob(SystemJob j) {
        systemJob = j;
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer j) {
        jobContainer = j;
    }

    private boolean isV4l2(String s) {

        boolean result = false;

        if (s != null) {

            result = s.startsWith("/dev/video");
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setTerminate(false);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        String inStr = getInput();
        String outStr = getOutput();
        if ((inStr != null) && (outStr != null)) {

            String prefix = "";
            if (isV4l2(inStr)) {

                prefix = "-f v4l2";
            }

            String command = "ffmpeg -y -i"
                + " " + prefix
                + " " + inStr
                + " -vcodec " + getVideoCodec()
                + " " + getAudioCodec()
                + " " + outStr;

                //+ " -acodec " + getAudioCodec()
                //+ " -ss 00:00:03"

            SystemJob job = SystemJob.getInstance(command);
            fireJobEvent(JobEvent.UPDATE,
                "command: <" + job.getCommand() + ">");
            setSystemJob(job);
            job.addJobListener(this);
            JobContainer jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            jc.start();

            while (!isTerminate()) {

                JobManager.sleep(getSleepTime());
            }
        }

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
        JobContainer jc = getJobContainer();
        SystemJob job = getSystemJob();
        if ((jc != null) && (job != null)) {

            jc.stop();
            job.removeJobListener(this);
            setJobContainer(null);
            setSystemJob(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            setTerminate(true);

        } else if (event.getType() == JobEvent.UPDATE) {

            //fireJobEvent(JobEvent.UPDATE, event.getMessage());
        }
    }

}

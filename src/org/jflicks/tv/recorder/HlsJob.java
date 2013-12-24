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
package org.jflicks.tv.recorder;

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
public class HlsJob extends AbstractJob implements JobListener {

    private SystemJob systemJob;
    private JobContainer jobContainer;
    private String input;
    private String output;
    private String videoCodec;
    private String audioCodec;
    private String optional;
    private File directory;
    private long duration;

    /**
     * Simple one argument constructor.
     *
     * @param f A given File.
     */
    public HlsJob(String input, String output, File directory, long duration) {

        setInput(input);
        setOutput(output);
        setDirectory(directory);
        setDuration(duration);
        setVideoCodec("copy");
        setAudioCodec("libfdk_aac");
        setOptional("");
    }

    /**
     * The local File to save the data.
     *
     * @return The File instance.
     */
    public File getDirectory() {
        return (directory);
    }

    private void setDirectory(File f) {
        directory = f;
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
     * The output String as a file prefix.
     *
     * @return The output String.
     */
    public String getOutput() {
        return (output);
    }

    private void setOutput(String s) {
        output = s;
    }

    /**
     * The time in seconds to record from a HDHR.
     *
     * @return The time in seconds.
     */
    public long getDuration() {
        return (duration);
    }

    private void setDuration(long l) {
        duration = l;
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

    public String getOptional() {
        return (optional);
    }

    public void setOptional(String s) {
        optional = s;
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

        File f = getDirectory();
        String inStr = getInput();
        String outStr = getOutput();
        if ((f != null) && (inStr != null) && (outStr != null)) {

            if (inStr.startsWith("udp")) {

                inStr = "'" + inStr + "?fifo_size=1000000&overrun_nonfatal=1'";
            }

            String command = "ffmpeg -i"
                + " " + inStr
                + " -vcodec " + getVideoCodec()
                + " -acodec " + getAudioCodec()
                + " " + getOptional()
                + " -map 0 -f segment -segment_list"
                + " " + outStr + ".m3u8"
                + " -segment_time 10 -segment_list_flags +live"
                + " " + outStr + ".%06d.ts";

            SystemJob job = SystemJob.getInstance(command, f);
            fireJobEvent(JobEvent.UPDATE,
                "command: <" + job.getCommand() + ">");
            setSystemJob(job);
            job.addJobListener(this);
            JobContainer jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            jc.start();

            long l = getDuration();
            if (l == 0) {

                // This is just to record something...the duration was
                // not set so lets record for one minute.  This should
                // not happen.
                l = 60 * 1000;

            } else {

                // Turn seconds into milliseconds.
                l *= 1000;
            }

            long now = System.currentTimeMillis();
            l += now;
            long sleep = getSleepTime();
            while (!isTerminate()) {

                JobManager.sleep(sleep);
                now = System.currentTimeMillis();
                if (now >= l) {

                    stop();

                } else if ((now + 20000) > l) {

                    // twenty second warning!
                    sleep = 100;
                }
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

            job.removeJobListener(this);
            jc.stop();
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

            fireJobEvent(JobEvent.UPDATE, event.getMessage());
        }
    }

    public static void main(String[] args) {

        File dir = new File("/Users/djb/tmp/ggg");
        HlsJob job = new HlsJob(
            "/Users/djb/tmp/EP014124480053_2013_11_13_01_00.ts",
            "EP014124480053_2013_11_13_01_00", dir, 60);
        final JobContainer jc = JobManager.getJobContainer(job);
        jc.start();
    }

}

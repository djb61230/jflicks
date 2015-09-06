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
package org.jflicks.tv.postproc;

import java.util.StringTokenizer;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.tv.Recording;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

/**
 * Use ffmpeg to find true duration of a recording.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordingLengthJob extends AbstractJob implements JobListener {

    private Recording recording;
    private SystemJob systemJob;
    private JobContainer jobContainer;
    private long seconds;

    /**
     * Constructor with our required argument.
     *
     * @param r A Recording to process.
     */
    public RecordingLengthJob(Recording r) {

        setRecording(r);
        setSeconds(0L);
    }

    /**
     * The number of seconds that are determined.
     *
     * @return The length of the media in seconds.
     */
    public long getSeconds() {
        return (seconds);
    }

    private void setSeconds(long l) {
        seconds = l;
    }

    private Recording getRecording() {
        return (recording);
    }

    private void setRecording(Recording r) {
        recording = r;
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

        Recording r = getRecording();
        if (r != null) {

            SystemJob job = SystemJob.getInstance("ffmpeg -i " + r.getPath()
                + "." + r.getIndexedExtension());
            LogUtil.log(LogUtil.DEBUG, "getting real length: " + job.getCommand());
            job.addJobListener(this);
            setSystemJob(job);
            JobContainer jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            jc.start();
            setTerminate(false);

        } else {

            setTerminate(true);
        }
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

        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();
        }

        setTerminate(true);
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            SystemJob job = getSystemJob();
            Recording r = getRecording();
            if ((r != null) && (job != null)) {

                String output = job.getOutputText();
                if (output != null) {

                    String timeline = null;
                    int tindex = output.indexOf("Duration:");
                    if (tindex != -1) {

                        timeline = output.substring(tindex + 9);
                        timeline = timeline.trim();
                        timeline = timeline.substring(0, timeline.indexOf(","));

                        // Should have something like 00:00:00.00
                        int hours = 0;
                        int minutes = 0;
                        int secs = 0;
                        int index = 0;
                        StringTokenizer st = new StringTokenizer(timeline, ":");
                        while (st.hasMoreTokens()) {

                            String tmp = st.nextToken();
                            if (index == 0) {
                                hours = Util.str2int(tmp, hours);
                            } else if (index == 1) {
                                minutes = Util.str2int(tmp, minutes);
                            } else if (index == 2) {

                                tmp = tmp.substring(0, tmp.indexOf("."));
                                secs = Util.str2int(tmp, secs);
                            }
                            index++;
                        }

                        setSeconds(hours * 3600 + minutes * 60 + secs);
                    }
                }
            }

            stop();

        } else {

            //LogUtil.log(LogUtil.DEBUG, event.getMessage());
        }
    }

    public static long getRecordingLength(Recording r) {

        long result = 0L;

        RecordingLengthJob job = new RecordingLengthJob(r);
        JobContainer jc = JobManager.getJobContainer(job);
        jc.start();

        boolean done = false;
        int count = 0;
        while (jc.isAlive()) {

            if (!jc.isAlive()) {

                done = true;

            } else {

                count += 100;
                if (count > 2900) {

                    done = true;

                } else {

                    JobManager.sleep(100);
                }
            }
        }

        result = job.getSeconds();

        return (result);
    }

}

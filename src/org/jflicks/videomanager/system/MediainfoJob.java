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
package org.jflicks.videomanager.system;

import java.util.StringTokenizer;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.nms.Video;
import org.jflicks.util.Util;

/**
 * A job that saves images to an NMS.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class MediainfoJob extends AbstractJob implements JobListener {

    private Video video;
    private SystemJob systemJob;
    private JobContainer jobContainer;
    private long seconds;

    /**
     * Constructor with our required argument.
     *
     * @param v A Video to process.
     */
    public MediainfoJob(Video v) {

        setVideo(v);
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

    private Video getVideo() {
        return (video);
    }

    private void setVideo(Video v) {
        video = v;
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

        Video v = getVideo();
        if (v != null) {

            SystemJob job = SystemJob.getInstance("ffmpeg -i " + v.getPath());
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
            Video v = getVideo();
            if ((v != null) && (job != null)) {

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

            //System.out.println(event.getMessage());
        }
    }

}

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
package org.jflicks.tv.postproc.worker.mediainfo;

import java.util.StringTokenizer;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Recording;

/**
 * This job starts a system job that runs mediainfo.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class MediainfoJob extends AbstractJob implements JobListener {

    private Recording recording;
    private SystemJob systemJob;
    private JobContainer jobContainer;
    private long after;

    /**
     * Constructor with one required argument.
     *
     * @param r A Recording to check for commercials.
     */
    public MediainfoJob(Recording r) {

        setRecording(r);

        // Lets get 30 seconds of video out there before we check.
        //setSleepTime(30000);
    }

    /**
     * A mediainfo job acts upon a Recording.
     *
     * @return A Recording instance.
     */
    public Recording getRecording() {
        return (recording);
    }

    /**
     * A mediainfo job acts upon a Recording.
     *
     * @param r A Recording instance.
     */
    public void setRecording(Recording r) {
        recording = r;
    }

    private long getAfter() {
        return (after);
    }

    private void setAfter(long l) {
        after = l;
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

            SystemJob job = SystemJob.getInstance("mediainfo " + r.getPath());

            // Don't run until this time because there won't be video to
            // grab the screen shot!  We tack on 30 seconds just to be safe.
            setAfter(r.getRealStart() + 30000);

            job.addJobListener(this);
            setSystemJob(job);
            JobContainer jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            System.out.println("started: " + job.getCommand());
            setTerminate(false);

        } else {

            setTerminate(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        boolean jobStarted = false;

        while (!isTerminate()) {

            long now = System.currentTimeMillis();
            if (now > getAfter()) {

                JobContainer jc = getJobContainer();
                if ((!jobStarted) && (jc != null)) {

                    // We might be done before it comes around again but lets
                    // use a flag just to be safe...
                    jobStarted = true;
                    jc.start();
                }
            }

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

                    String scanType = null;
                    String width = null;
                    String height = null;
                    String format = null;
                    String channels = null;
                    boolean audio = false;

                    StringTokenizer st = new StringTokenizer(output, "\n");
                    while (st.hasMoreTokens()) {

                        String line = st.nextToken();
                        if (line.startsWith("Audio")) {
                            audio = true;
                        }

                        int index = line.indexOf(":");
                        if (index != -1) {

                            String tag = line.substring(0, index);
                            tag = tag.trim();
                            String val = line.substring(index + 1);
                            val = val.trim();

                            if (tag.startsWith("Scan type")) {

                                 if (scanType == null) {
                                     scanType = val;
                                 }

                            } else if (tag.startsWith("Width")) {

                                 if (width == null) {
                                     width = val;
                                 }

                            } else if (tag.startsWith("Height")) {

                                 if (height == null) {
                                     height = val;
                                 }

                            } else if (tag.startsWith("Format")) {

                                if ((audio) && (format == null)) {
                                    format = val;
                                }

                            } else if (tag.startsWith("Channel(s)")) {

                                if (channels == null) {
                                    channels = val;
                                }
                            }
                        }
                    }

                    if ((width != null) && (height != null)
                        && (scanType != null)) {

                        // We can set the video format.
                        if ((width.startsWith("1 920"))
                            && (height.startsWith("1 080"))) {

                            if (scanType.startsWith("Interlaced")) {
                                r.setVideoFormat(NMSConstants.VIDEO_1080I);
                            } else {
                                r.setVideoFormat(NMSConstants.VIDEO_1080P);
                            }

                        } else if ((width.startsWith("1 280"))
                            && (height.startsWith("720"))) {

                            r.setVideoFormat(NMSConstants.VIDEO_720P);

                        } else if (height.startsWith("480")) {

                            if (scanType.startsWith("Interlaced")) {
                                r.setVideoFormat(NMSConstants.VIDEO_480I);
                            } else {
                                r.setVideoFormat(NMSConstants.VIDEO_480P);
                            }
                        }
                    }

                    // Now check the audio...
                    System.out.println("parsed format: <" + format + ">");
                    System.out.println("parsed channels: <" + channels + ">");
                    if ((format != null) && (channels != null)) {

                        if (format.startsWith("AC-3")) {

                            if (channels.startsWith("2")) {
                                r.setAudioFormat(
                                    NMSConstants.AUDIO_DOLBY_DIGITAL_2_0);
                            } else if (channels.startsWith("6")) {
                                r.setAudioFormat(
                                    NMSConstants.AUDIO_DOLBY_DIGITAL_5_1);
                            }

                        } else {

                            // Not sure what to set here....
                        }
                    }
                }
            }

            stop();

        } else {

            //System.out.println(event.getMessage());
        }
    }

}


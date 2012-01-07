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
package org.jflicks.transfer.system;

import java.io.File;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.tv.Recording;

/**
 * Transfer a file using curl.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class WgetTransferJob extends AbstractJob implements JobListener {

    private static final int NO_PROBLEM_EXIT_CODE = 0;
    private static final String DONE_MESSAGE = "file is already fully";

    private SystemJob systemJob;
    private JobContainer jobContainer;
    private Recording recording;
    private File file;
    private String maxRate;
    private int restSeconds;

    /**
     * Simple one argument constructor.
     *
     * @param r A Recording instance.
     * @param f A given File.
     * @param s The max rate for transfers for curl.
     * @param i The number of seconds to rest after we catch up.
     */
    public WgetTransferJob(Recording r, File f, String s, int i) {

        setRecording(r);
        setFile(f);
        setMaxRate(s);
        setRestSeconds(i);
    }

    /**
     * The Recording property.
     *
     * @return The Recording to transfer.
     */
    public Recording getRecording() {
        return (recording);
    }

    /**
     * The Recording property.
     *
     * @param r The Recording to transfer.
     */
    public void setRecording(Recording r) {
        recording = r;

        if (recording == null) {

            stop();
        }
    }

    /**
     * The local File to save the Recording file.
     *
     * @return The File instance.
     */
    public File getFile() {
        return (file);
    }

    /**
     * The local File to save the Recording file.
     *
     * @param f The File instance.
     */
    public void setFile(File f) {
        file = f;
    }

    /**
     * The max transfer rate to avoid IO problems.  See curl man page
     * for details (ex. --limit-rate 10m).  This value is the 10m part.
     *
     * @return The max rate as a String.
     */
    public String getMaxRate() {
        return (maxRate);
    }

    /**
     * The max transfer rate to avoid IO problems.  See curl man page
     * for details (ex. --limit-rate 10m).  This value is the 10m part.
     *
     * @param s The max rate as a String.
     */
    public void setMaxRate(String s) {
        maxRate = s;
    }

    public int getRestSeconds() {
        return (restSeconds);
    }

    public void setRestSeconds(int i) {
        restSeconds = i;
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

    private boolean isFinished(String s) {

        boolean result = false;

        if (s != null) {

            //System.out.println(s);
            result = s.indexOf(DONE_MESSAGE) != -1;
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

        Recording r = getRecording();
        File f = getFile();
        if ((r != null) && (f != null)) {

            String continueString = "";
            if (f.exists()) {

                continueString = "-c ";
            }
            SystemJob job = SystemJob.getInstance("wget --limit-rate="
                + getMaxRate() + " " + continueString
                + " " + r.getStreamURL() + " -O " + f.getPath());
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

            job.removeJobListener(this);
            jc.stop();
            setJobContainer(null);
            setSystemJob(null);
        }
    }

    private String toFtp(String s) {

        String result = null;

        if ((s != null) && (s.startsWith("http"))) {

            result = "ftp" + s.substring(s.indexOf(":"));
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            Recording r = getRecording();
            File f = getFile();
            SystemJob job = getSystemJob();
            if ((r != null) && (f != null) && (job != null)) {

                fireJobEvent(JobEvent.UPDATE, "ProgramJob: exit: "
                    + job.getExitValue());

                // We want to continue on even if the recording has
                // finished.  We can tell we have the whole file when
                // we have received a message in the output text.
                if (!isFinished(job.getOutputText())) {

                    // Let's sleep a few seconds and try to get more data.
                    JobManager.sleep(getRestSeconds() * 1000);

                    job = SystemJob.getInstance("wget --limit-rate="
                        + getMaxRate() + " -c "
                        + r.getStreamURL() + " -O " + f.getPath());
                    fireJobEvent(JobEvent.UPDATE,
                        "command: <" + job.getCommand() + ">");
                    setSystemJob(job);
                    job.addJobListener(this);
                    JobContainer jc = JobManager.getJobContainer(job);
                    setJobContainer(jc);
                    jc.start();

                } else {

                    stop();
                }

            } else {

                stop();
            }
        }
    }

}

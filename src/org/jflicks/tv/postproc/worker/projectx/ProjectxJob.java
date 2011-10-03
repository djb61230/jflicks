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
package org.jflicks.tv.postproc.worker.projectx;

import java.io.File;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.tv.Recording;
import org.jflicks.tv.postproc.worker.BaseWorker;
import org.jflicks.tv.postproc.worker.BaseWorkerJob;

/**
 * This job starts a system job that runs comskip.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ProjectxJob extends BaseWorkerJob implements JobListener {

    private String commandLine;
    private File videoFile;
    private File audioFile;
    private File logFile;
    private boolean demuxDone;

    /**
     * Constructor with one required argument.
     *
     * @param r A Recording to transcode.
     * @param bw The Worker associated with this job.
     */
    public ProjectxJob(Recording r, BaseWorker bw) {

        super(r, bw);
        setCommandLine("java -jar bin/ProjectX.jar INPUT_PATH");
    }

    private String getCommandLine() {
        return (commandLine);
    }

    private void setCommandLine(String s) {
        commandLine = s;
    }

    private File getVideoFile() {
        return (videoFile);
    }

    private void setVideoFile(File f) {
        videoFile = f;
    }

    private File getAudioFile() {
        return (audioFile);
    }

    private void setAudioFile(File f) {
        audioFile = f;
    }

    private File getLogFile() {
        return (logFile);
    }

    private void setLogFile(File f) {
        logFile = f;
    }

    private boolean isDemuxDone() {
        return (demuxDone);
    }

    private void setDemuxDone(boolean b) {
        demuxDone = b;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setDemuxDone(false);
        Recording r = getRecording();
        if (r != null) {

            String path = r.getPath();
            String cl = getCommandLine();
            if ((path != null) && (cl != null)) {

                // First build out our resulting files...
                String pre = path.substring(0, path.lastIndexOf("."));
                setVideoFile(new File(pre + ".m2v"));
                setAudioFile(new File(pre + ".ac3"));
                setLogFile(new File(pre + "_log.txt"));

                cl = cl.replaceFirst("INPUT_PATH", path);
                SystemJob job = SystemJob.getInstance("ionice -c3 " + cl);

                job.addJobListener(this);
                setSystemJob(job);
                JobContainer jc = JobManager.getJobContainer(job);
                setJobContainer(jc);
                log(BaseWorker.INFO, "started: " + job.getCommand());
                setTerminate(false);
            }

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

            JobContainer jc = getJobContainer();
            if ((!jobStarted) && (jc != null)) {

                jobStarted = true;
                jc.start();
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

            if (!isDemuxDone()) {

                setDemuxDone(true);
                MkvmergeJob job = new MkvmergeJob(getRecording(),
                    getBaseWorker(), getVideoFile(), getAudioFile());
                job.addJobListener(this);
                JobContainer jc = JobManager.getJobContainer(job);
                setJobContainer(jc);
                jc.start();

            } else {

                File f = getVideoFile();
                if ((f != null) && (f.exists()) && (f.isFile())) {

                    if (!f.delete()) {
                        log(BaseWorker.INFO, "delete failure: " + f.getPath());
                    }
                }

                f = getAudioFile();
                if ((f != null) && (f.exists()) && (f.isFile())) {

                    if (!f.delete()) {
                        log(BaseWorker.INFO, "delete failure: " + f.getPath());
                    }
                }

                f = getLogFile();
                if ((f != null) && (f.exists()) && (f.isFile())) {

                    if (!f.delete()) {
                        log(BaseWorker.INFO, "delete failure: " + f.getPath());
                    }
                }

                stop();
            }
        }
    }

}


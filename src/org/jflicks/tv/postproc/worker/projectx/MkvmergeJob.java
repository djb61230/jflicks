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
import org.jflicks.util.LogUtil;

/**
 * This job starts a system job that runs comskip.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class MkvmergeJob extends BaseWorkerJob implements JobListener {

    private String commandLine;
    private File videoFile;
    private File audioFile;

    /**
     * Constructor with one required argument.
     *
     * @param r A Recording to transcode.
     * @param bw The Worker associated with this job.
     */
    public MkvmergeJob(Recording r, BaseWorker bw, File vfile, File afile) {

        super(r, bw);
        setExtension("mkv");
        setCommandLine("mkvmerge -o OUTPUT_PATH INPUT_VPATH INPUT_APATH");
        setVideoFile(vfile);
        setAudioFile(afile);
    }

    private String getCommandLine() {
        return (commandLine);
    }

    private void setCommandLine(String s) {
        commandLine = s;
    }

    public File getVideoFile() {
        return (videoFile);
    }

    private void setVideoFile(File f) {
        videoFile = f;
    }

    public File getAudioFile() {
        return (audioFile);
    }

    private void setAudioFile(File f) {
        audioFile = f;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        Recording r = getRecording();
        File vfile = getVideoFile();
        File afile = getAudioFile();
        if ((r != null) && (vfile != null) && (afile != null)) {

            String path = r.getPath();
            File tmp = computeFile(r, true);
            String cl = getCommandLine();
            if (cl != null) {

                String tpath = tmp.getPath();
                tpath = tpath.replace("\\", "/");
                cl = cl.replaceFirst("OUTPUT_PATH", tpath);
                tpath = vfile.getPath();
                tpath = tpath.replace("\\", "/");
                cl = cl.replaceFirst("INPUT_VPATH", tpath);
                tpath = afile.getPath();
                tpath = tpath.replace("\\", "/");
                cl = cl.replaceFirst("INPUT_APATH", tpath);
                SystemJob job = null;
                String nice = getNice();
                if (nice != null) {
                    job = SystemJob.getInstance(nice + " " + cl);
                } else {
                    job = SystemJob.getInstance(cl);
                }

                job.addJobListener(this);
                setSystemJob(job);
                JobContainer jc = JobManager.getJobContainer(job);
                setJobContainer(jc);
                LogUtil.log(LogUtil.INFO, "started: " + job.getCommand());
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

            SystemJob job = getSystemJob();
            if ((job != null) && (job.getExitValue() == 0)) {
                move();
            } else {
                remove();
            }

            stop();
        }
    }

}


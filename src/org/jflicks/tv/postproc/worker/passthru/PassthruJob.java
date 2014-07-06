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
package org.jflicks.tv.postproc.worker.passthru;

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
public class PassthruJob extends BaseWorkerJob implements JobListener {

    private SystemJob mkvSystemJob;
    private SystemJob mkvpropSystemJob;
    private SystemJob mp4SystemJob;
    private File mkvFile;

    /**
     * Constructor with one required argument.
     *
     * @param r A Recording to transcode.
     * @param bw The Worker associated with this job.
     */
    public PassthruJob(Recording r, BaseWorker bw) {

        super(r, bw);
        setExtension("mp4");
    }

    private SystemJob getMkvSystemJob() {
        return (mkvSystemJob);
    }

    private void setMkvSystemJob(SystemJob j) {
        mkvSystemJob = j;
    }

    private SystemJob getMkvpropSystemJob() {
        return (mkvpropSystemJob);
    }

    private void setMkvpropSystemJob(SystemJob j) {
        mkvpropSystemJob = j;
    }

    private SystemJob getMp4SystemJob() {
        return (mp4SystemJob);
    }

    private void setMp4SystemJob(SystemJob j) {
        mp4SystemJob = j;
    }

    private File getMkvFile() {
        return (mkvFile);
    }

    private void setMkvFile(File f) {
        mkvFile = f;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        Recording r = getRecording();
        if (r != null) {

            String path = r.getPath();
            if (path != null) {

                // See if we are to be nice.
                String nice = getNice();

                // First make a path and File for the temp mkv file.
                String mkvpath = path + ".mkv";
                setMkvFile(new File(mkvpath));

                // We setup and run three jobs.  First make a valid mkv file.
                String command = "ffmpeg -i "
                    + "\""
                    + path
                    + "\""
                    + " -map 0:0 -map 0:1 -map 0:1 -c:v copy -c:a:1 copy -c:a:1 ac3 "
                    + "\""
                    + mkvpath
                    + "\"";

                if (nice != null) {
                    setMkvSystemJob(SystemJob.getInstance(nice + " " + command));
                } else {
                    setMkvSystemJob(SystemJob.getInstance(command));
                }

                // Next the mkvpropedit job.  We do not need to be nice.
                command = "mkvpropedit "
                    + "\""
                    + mkvpath
                    + "\""
                    + " --edit track:a2 --set flag-default=0"
                    + " --edit track:a2 --set flag-enabled=0";

                setMkvpropSystemJob(SystemJob.getInstance(command));

                // Finally make the mp4 file correctly.  Be nice.
                File tmp = computeFile(r, true);
                command = "ffmpeg -i "
                    + "\""
                    + mkvpath
                    + "\""
                    + " -map 0:0 -map 0:1 -map 0:2 -c:v copy -c:a:1 copy -c:a:2 copy "
                    + "-strict -2 "
                    + "\""
                    + tmp.getPath()
                    + "\"";

                if (nice != null) {
                    setMp4SystemJob(SystemJob.getInstance(nice + " " + command));
                } else {
                    setMp4SystemJob(SystemJob.getInstance(command));
                }

                // Start the first job.
                SystemJob job = getMkvSystemJob();
                job.addJobListener(this);
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

            if (event.getSource() == getMkvSystemJob()) {

                // Start the mkvpropedit job.
                SystemJob job = getMkvpropSystemJob();
                job.addJobListener(this);
                JobContainer jc = JobManager.getJobContainer(job);
                log(BaseWorker.INFO, "started: " + job.getCommand());
                jc.start();

            } else if (event.getSource() == getMkvpropSystemJob()) {

                // Start the mp4 job.
                SystemJob job = getMp4SystemJob();
                job.addJobListener(this);
                JobContainer jc = JobManager.getJobContainer(job);
                log(BaseWorker.INFO, "started: " + job.getCommand());
                jc.start();

            } else if (event.getSource() == getMp4SystemJob()) {

                SystemJob job = getMp4SystemJob();
                if ((job != null) && (job.getExitValue() == 0)) {
                    move();
                } else {
                    remove();
                }

                File f = getMkvFile();
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


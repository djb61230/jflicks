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
package org.jflicks.tv.postproc.worker.comskip;

import java.io.File;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.tv.Commercial;
import org.jflicks.tv.Recording;
import org.jflicks.tv.postproc.worker.BaseWorker;
import org.jflicks.tv.postproc.worker.BaseWorkerJob;
import org.jflicks.util.Util;

/**
 * This job starts a system job that runs comskip.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ComskipJob extends BaseWorkerJob implements JobListener {

    /**
     * Constructor with one required argument.
     *
     * @param r A Recording to check for commercials.
     */
    public ComskipJob(Recording r, BaseWorker bw) {

        super(r, bw);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        Recording r = getRecording();
        if (r != null) {

            SystemJob job = null;

            if (Util.isLinux()) {

                job = SystemJob.getInstance("wine bin/comskip "
                    + "--ini=conf/comskip.ini " + r.getPath());

            } else {

                job = SystemJob.getInstance("bin\\comskip "
                    + "--ini=conf/comskip.ini " + r.getPath());
            }

            job.addJobListener(this);
            setSystemJob(job);
            JobContainer jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            jc.start();
            log(BaseWorker.INFO, "started: " + job.getCommand());
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

            Recording r = getRecording();
            if (r != null) {

                // First delete the log files...
                String path = r.getPath();
                if (path != null) {

                    path = path.substring(0, path.lastIndexOf("."));
                    File file = new File(path + ".log");
                    boolean delresult = file.delete();
                    if (!delresult) {
                        log(BaseWorker.INFO, file.getPath() + " not found");
                    }

                    file = new File(path + ".logo.txt");
                    delresult = file.delete();
                    if (!delresult) {
                        log(BaseWorker.INFO, file.getPath() + " not found");
                    }

                    file = new File(path + ".txt");
                    delresult = file.delete();
                    if (!delresult) {
                        log(BaseWorker.INFO, file.getPath() + " not found");
                    }

                    file = new File(path + ".edl");
                    log(BaseWorker.INFO, "setting commercials...");
                    r.setCommercials(Commercial.fromEDL(file));
                }
            }

            setTerminate(true);
        }
    }

}


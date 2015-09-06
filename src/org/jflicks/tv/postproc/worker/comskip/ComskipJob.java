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
import org.jflicks.util.LogUtil;
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
     * @param bw The Worker associated with this job.
     */
    public ComskipJob(Recording r, BaseWorker bw) {

        super(r, bw);

        // Check the recording for completion every minute.
        setSleepTime(60000);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        Recording r = getRecording();
        if (r != null) {

            SystemJob job = null;

            if (Util.isLinux() || Util.isMac()) {

                job = SystemJob.getInstance("wine bin/comskip "
                    + "--ini=conf/comskip.ini " + "\"" + r.getPath() + "\"");

            } else {

                job = SystemJob.getInstance("bin\\comskip "
                    + "--ini=conf/comskip.ini " + "\"" + r.getPath() + "\"");
            }

            job.addJobListener(this);
            setSystemJob(job);
            JobContainer jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            LogUtil.log(LogUtil.INFO, "will start: " + job.getCommand());
            setTerminate(false);

        } else {

            setTerminate(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        boolean begun = false;

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());
            if (!begun) {

                Recording r = getRecording();
                if (!r.isCurrentlyRecording()) {

                    File ts = new File(r.getPath());
                    if (ts.exists()) {

                        JobContainer jc = getJobContainer();
                        if (jc != null) {

                            jc.start();
                            begun = true;
                            LogUtil.log(LogUtil.INFO, "Kicked off comskip");
                        }
                    }

                } else {

                    LogUtil.log(LogUtil.INFO, "Recording still seems to be on. "
                        + "Waiting until finished to work.");
                }
            }
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

                    String origPath = path;

                    path = path.substring(0, path.lastIndexOf("."));
                    File file = new File(path + ".log");
                    boolean delresult = file.delete();
                    if (!delresult) {
                        LogUtil.log(LogUtil.INFO, file.getPath() + " not found");
                    }

                    file = new File(path + ".logo.txt");
                    delresult = file.delete();
                    if (!delresult) {
                        LogUtil.log(LogUtil.INFO, file.getPath() + " not found");
                    }

                    file = new File(path + ".txt");
                    delresult = file.delete();
                    if (!delresult) {
                        LogUtil.log(LogUtil.INFO, file.getPath() + " not found");
                    }

                    file = new File(path + ".edl");
                    LogUtil.log(LogUtil.INFO, "setting commercials...");
                    r.setCommercials(Commercial.fromEDL(file));

                    // Next we want to write a chapter file for mp4chaps.
                    String ext = r.getIndexedExtension();
                    if ((ext != null) && (ext.equals("mp4"))) {

                        Commercial[] coms = r.getCommercials();
                        if ((coms != null) && (coms.length > 0)) {

                            StringBuilder sb = new StringBuilder();
                            sb.append("00:00:00.000 Chapter 1\n");
                            for (int i = 0; i < coms.length; i++) {

                                String fmt = formatSeconds(coms[i].getEnd());
                                sb.append(fmt + ".000 Chapter " + (i + 2)
                                    + "\n");
                            }

                            file = new File(origPath + ".chapters.txt");
                            try {

                                Util.writeTextFile(file, sb.toString());
                                SystemJob job = SystemJob.getInstance("mp4chaps -i \"" + origPath + ".mp4\"");
                                JobContainer jc = JobManager.getJobContainer(job);
                                LogUtil.log(LogUtil.INFO, "will start: " + job.getCommand());
                                jc.start();

                            } catch (Exception ex) {

                                LogUtil.log(LogUtil.INFO, "Couldn't do chapters");
                            }
                        }
                    }
                }
            }

            setTerminate(true);
        }
    }

    private static String formatSeconds(int secsIn) { 

        int hours = secsIn / 3600; 
        int remainder = secsIn % 3600;
        int minutes = remainder / 60;
        int seconds = remainder % 60; 

        return ( (hours < 10 ? "0" : "") + hours 
        + ":" + (minutes < 10 ? "0" : "") + minutes 
        + ":" + (seconds< 10 ? "0" : "") + seconds ); 
    }
}


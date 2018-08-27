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
package org.jflicks.tv.postproc.system;

import java.io.File;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobManager;
import org.jflicks.tv.Recording;
import org.jflicks.tv.postproc.worker.Worker;
import org.jflicks.tv.postproc.worker.WorkerEvent;
import org.jflicks.tv.postproc.worker.WorkerListener;
import org.jflicks.util.LogUtil;

/**
 * This job will run and queue recording jobs.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SystemPostProcHeavyJob extends SystemPostProcJob {

    private int max;

    /**
     * This job supports the SystemScheduler plugin.
     *
     * @param s A given SystemScheduler instance.
     */
    public SystemPostProcHeavyJob(SystemPostProc s) {

        super(s);
        setMax(1);
    }

    private int getMax() {
        return (max);
    }

    private void setMax(int i) {

        max = i;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());
            SystemPostProc spp = getSystemPostProc();
            if (spp != null) {

                setMax(spp.getConfiguredMaximumJobs());
                if (isReady()) {

                    WorkerRecording wr = spp.popHeavyWorkerRecording();
                    if (wr != null) {

                        Worker w = wr.getWorker();
                        Recording r = wr.getRecording();
                        if ((w != null) && (r != null)) {

                            String path = r.getPath();
                            if (path != null) {

                                // We also need to check an HLS path too.
                                String hlspath = path.substring(0, path.lastIndexOf("."));
                                hlspath = hlspath + ".000000.ts";

                                File hlsfile = new File(hlspath);
                                File file = new File(path);
                                LogUtil.log(LogUtil.INFO, "One of these paths must exist:");
                                LogUtil.log(LogUtil.INFO, "\t" + file + " " + file.exists());
                                LogUtil.log(LogUtil.INFO, "\t" + hlsfile + " " + hlsfile.exists());
                                if (file.exists() || hlsfile.exists()) {

                                    LogUtil.log(LogUtil.INFO, "We have work!!");
                                    w.addWorkerListener(this);
                                    setCount(getCount() + 1);
                                    w.work(r);

                                } else {

                                    // We probably got here before the
                                    // recording  started.  Let's push
                                    // and get it next time.
                                    //spp.pushHeavyWorkerRecording(wr);
                                    LogUtil.log(LogUtil.INFO, "Something not correct now for " + r);
                                    if (getLastWorkerRecording() == null) {

                                        setRetryCount(1);
                                        setLastWorkerRecording(wr);
                                        spp.pushHeavyWorkerRecording(wr);

                                    } else {

                                        setRetryCount(getRetryCount() + 1);
                                        if (getRetryCount() >= MAX_RETRIES) {

                                            setRetryCount(0);
                                            setLastWorkerRecording(null);

                                        } else {

                                            spp.pushHeavyWorkerRecording(wr);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public void workerUpdate(WorkerEvent event) {

        if (event.getType() == WorkerEvent.COMPLETE) {

            setCount(getCount() - 1);
            Worker w = (Worker) event.getSource();
            w.removeWorkerListener(this);
        }
    }

}

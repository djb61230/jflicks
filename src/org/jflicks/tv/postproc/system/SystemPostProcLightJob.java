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
public class SystemPostProcLightJob extends SystemPostProcJob {

    /**
     * This job supports the SystemScheduler plugin.
     *
     * @param s A given SystemScheduler instance.
     */
    public SystemPostProcLightJob(SystemPostProc s) {

        super(s);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());
            SystemPostProc spp = getSystemPostProc();
            if (spp != null) {

                if (isReady()) {

                    WorkerRecording wr = spp.popLightWorkerRecording();
                    if (wr != null) {

                        Worker w = wr.getWorker();
                        Recording r = wr.getRecording();
                        if ((w != null) && (r != null)) {

                            String path = r.getPath();
                            if (path != null) {

                                // We also need to check an HLS path too.
                                String hlspath =
                                    path.substring(0, path.lastIndexOf("."));
                                hlspath = hlspath + ".000000.ts";

                                File hlsfile = new File(hlspath);
                                File file = new File(path);
                                if (file.exists() || hlsfile.exists()) {

                                    LogUtil.log(LogUtil.INFO, "We have work!!");
                                    setLastWorkerRecording(null);
                                    w.addWorkerListener(this);
                                    setCount(1);
                                    w.work(r);

                                } else {

                                    // We probably got here before the
                                    // recording  started.  Let's push
                                    // and get it next time.
                                    if (getLastWorkerRecording() == null) {

                                        setRetryCount(1);
                                        setLastWorkerRecording(wr);
                                        spp.pushLightWorkerRecording(wr);

                                    } else {

                                        setRetryCount(getRetryCount() + 1);
                                        if (getRetryCount() >= MAX_RETRIES) {

                                            setRetryCount(0);
                                            setLastWorkerRecording(null);

                                        } else {

                                            spp.pushLightWorkerRecording(wr);
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

            setCount(0);
            setLastWorkerRecording(null);
            Worker w = (Worker) event.getSource();
            w.removeWorkerListener(this);
        }
    }

}

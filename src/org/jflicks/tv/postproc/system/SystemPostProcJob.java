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

/**
 * This job will run and queue recording jobs.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class SystemPostProcJob extends AbstractJob
    implements WorkerListener {

    public static final int MAX_RETRIES = 20;

    private SystemPostProc systemPostProc;
    private int count;
    private int retryCount;
    private WorkerRecording lastWorkerRecording;

    /**
     * This job supports the SystemScheduler plugin.
     *
     * @param s A given SystemScheduler instance.
     */
    public SystemPostProcJob(SystemPostProc s) {

        setSystemPostProc(s);

        setRetryCount(0);
        setCount(0);
        setSleepTime(10000);
    }

    public SystemPostProc getSystemPostProc() {
        return (systemPostProc);
    }

    public void setSystemPostProc(SystemPostProc s) {
        systemPostProc = s;
    }

    public int getCount() {
        return (count);
    }

    public void setCount(int i) {
        count = i;

        log(SystemPostProc.INFO, getClass().getName()
            + "queue size now: " + count);
    }

    public int getRetryCount() {
        return (retryCount);
    }

    public void setRetryCount(int i) {
        retryCount = i;
    }

    public WorkerRecording getLastWorkerRecording() {
        return (lastWorkerRecording);
    }

    public void setLastWorkerRecording(WorkerRecording wr) {
        lastWorkerRecording = wr;
    }

    public void log(int status, String message) {

        SystemPostProc spp = getSystemPostProc();
        if (spp != null) {

            spp.log(status, message);
        }
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
    public void stop() {

        setTerminate(true);
    }

    public boolean isReady() {
        return (getCount() < 1);
    }

}

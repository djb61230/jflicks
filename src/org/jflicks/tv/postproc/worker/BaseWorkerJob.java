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
package org.jflicks.tv.postproc.worker;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.SystemJob;
import org.jflicks.tv.Recording;

/**
 * A base worker job class that workers can extend.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseWorkerJob extends AbstractJob {

    private Recording recording;
    private SystemJob systemJob;
    private JobContainer jobContainer;
    private BaseWorker baseWorker;

    /**
     * Constructor with one required argument.
     *
     * @param r A Recording to process.
     */
    public BaseWorkerJob(Recording r, BaseWorker bw) {

        setRecording(r);
        setBaseWorker(bw);
    }

    /**
     * A job acts upon a Recording.
     *
     * @return A Recording instance.
     */
    public Recording getRecording() {
        return (recording);
    }

    /**
     * A job acts upon a Recording.
     *
     * @param r A Recording instance.
     */
    public void setRecording(Recording r) {
        recording = r;
    }

    /**
     * Most workers will need to do some system job so we have one as
     * a property as a convenience to extensions.
     *
     * @return A SystemJob instance.
     */
    public SystemJob getSystemJob() {
        return (systemJob);
    }

    /**
     * Most workers will need to do some system job so we have one as
     * a property as a convenience to extensions.
     *
     * @param j A SystemJob instance.
     */
    public void setSystemJob(SystemJob j) {
        systemJob = j;
    }

    /**
     * Most workers will need to do some job running so we have a
     * JobContainer as a property as a convenience to extensions.
     *
     * @return A JobContainer instance.
     */
    public JobContainer getJobContainer() {
        return (jobContainer);
    }

    /**
     * Most workers will need to do some job running so we have a
     * JobContainer as a property as a convenience to extensions.
     *
     * @param j A JobContainer instance.
     */
    public void setJobContainer(JobContainer j) {
        jobContainer = j;
    }

    /**
     * It's handy for the Job to be able to access the Worker that it is
     * associated.
     *
     * @return A BaseWorker instance.
     */
    public BaseWorker getBaseWorker() {
        return (baseWorker);
    }

    /**
     * It's handy for the Job to be able to access the Worker that it is
     * associated.
     *
     * @param bw A BaseWorker instance.
     */
    public void setBaseWorker(BaseWorker bw) {
        baseWorker = bw;
    }

    /**
     * Convenience method to log using the BaseWorker.
     *
     * @param level The log level.
     * @param message The message to log.
     */
    public void log(int level, String message) {

        BaseWorker bw = getBaseWorker();
        if (bw != null) {

            bw.log(level, message);
        }
    }

}


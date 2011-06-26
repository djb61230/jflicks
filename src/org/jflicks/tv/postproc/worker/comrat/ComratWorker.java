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
package org.jflicks.tv.postproc.worker.comrat;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.tv.Recording;
import org.jflicks.tv.postproc.worker.BaseWorker;
import org.jflicks.tv.postproc.worker.WorkerEvent;
import org.jflicks.util.DetectRatingPlan;

/**
 * Worker implementation that can flag a Recording using the comrat
 * class.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ComratWorker extends BaseWorker implements JobListener {

    private int backup;
    private int span;
    private boolean verbose;
    private DetectRatingPlan[] detectRatingPlans;

    /**
     * Simple default constructor.
     */
    public ComratWorker() {

        setHeavy(true);
        setDefaultRun(false);
    }

    /**
     * We want to actually adjust the break a few seconds.
     *
     * @return An int value in seconds.
     */
    public int getBackup() {
        return (backup);
    }

    /**
     * We want to actually adjust the break a few seconds.
     *
     * @param i An int value in seconds.
     */
    public void setBackup(int i) {
        backup = i;
    }

    /**
     * The time between each frame.  Defaults to five.
     *
     * @return The span as an int value.
     */
    public int getSpan() {
        return (span);
    }

    /**
     * The time between each frame.  Defaults to five.
     *
     * @param i The span as an int value.
     */
    public void setSpan(int i) {
        span = i;
    }

    /**
     * Turning on verbose will send messages to the console and leave
     * working images on disk.  This is handy for debugging.
     *
     * @return True when the program should be verbose.
     */
    public boolean isVerbose() {
        return (verbose);
    }

    /**
     * Turning on verbose will send messages to the console and leave
     * working images on disk.  This is handy for debugging.
     *
     * @param b True when the program should be verbose.
     */
    public void setVerbose(boolean b) {
        verbose = b;
    }

    /**
     * We have a set of plans to help us find the logos.
     *
     * @return An array of DetectRatingPlan instances.
     */
    public DetectRatingPlan[] getDetectRatingPlans() {
        return (detectRatingPlans);
    }

    /**
     * We have a set of plans to help us find the logos.
     *
     * @param array An array of DetectRatingPlan instances.
     */
    public void setDetectRatingPlans(DetectRatingPlan[] array) {
        detectRatingPlans = array;
    }

    /**
     * {@inheritDoc}
     */
    public void work(Recording r) {

        if (r != null) {

            ComratJob job = new ComratJob(r, this);
            job.setBackup(getBackup());
            job.setSpan(getSpan());
            job.setVerbose(isVerbose());
            job.setDetectRatingPlans(getDetectRatingPlans());
            job.addJobListener(this);
            JobContainer jc = JobManager.getJobContainer(job);
            addJobContainer(jc);
            jc.start();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            log(INFO, "ComratWorker: completed");
            ComratJob job = (ComratJob) event.getSource();
            removeJobContainer(job);
            fireWorkerEvent(WorkerEvent.COMPLETE, job.getRecording(), true);

        } else {

            //log(DEBUG, "ComratWorker: " + event.getMessage());
        }
    }

}


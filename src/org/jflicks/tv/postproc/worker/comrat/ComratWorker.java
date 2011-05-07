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

/**
 * Worker implementation that can flag a Recording using the comskip
 * program.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ComratWorker extends BaseWorker implements JobListener {

    private int type;
    private int fudge;
    private boolean verbose;

    /**
     * Simple default constructor.
     */
    public ComratWorker() {

        setHeavy(true);
    }

    /**
     * This is the Detect type property, either BLACK or WHITE.
     *
     * @return The type as an int.
     */
    public int getType() {
        return (type);
    }

    /**
     * This is the Detect type property, either BLACK or WHITE.
     *
     * @param i The type as an int.
     */
    public void setType(int i) {
        type = i;
    }

    /**
     * This is an integer to tell the Detect program to mitigate the
     * BLACK or WHITE value to shades of gray.
     *
     * @return An int value.
     */
    public int getFudge() {
        return (fudge);
    }

    /**
     * This is an integer to tell the Detect program to mitigate the
     * BLACK or WHITE value to shades of gray.
     *
     * @param i An int value.
     */
    public void setFudge(int i) {
        fudge = i;
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
     * {@inheritDoc}
     */
    public void work(Recording r) {

        if (r != null) {

            ComratJob job = new ComratJob(r, this);
            job.setType(getType());
            job.setFudge(getFudge());
            job.setVerbose(isVerbose());
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


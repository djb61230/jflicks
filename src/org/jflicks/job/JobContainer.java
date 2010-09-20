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
package org.jflicks.job;

/**
 * The idea for running jobs is that one usually thinks of threading in
 * java when doing jobs.  In J2EE threads are not allowed by the spec
 * (though many people do them anyway).  In J2EE they have a notion of
 * "Work" instead of a thread.  So we have abstracted this out by the
 * use of this class.  Client code will remain the same but depending
 * on the environment one might be doing "work" or threading normally.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class JobContainer {

    private Job job;

    /**
     * See if the running job has been interrupted.
     *
     * @return True if it has been.
     */
    public abstract boolean isInterrupted();

    /**
     * Is the job alive?
     *
     * @return True if the thread is still running.
     */
    public abstract boolean isAlive();

    /**
     * Interrupt the thread NOW!
     */
    public abstract void interrupt();

    /**
     * The priority of the job.
     *
     * @return An int value representing the priority.
     */
    public abstract int getPriority();

    /**
     * The priority of the job.
     *
     * @param i An int value representing the priority.
     */
    public abstract void setPriority(int i);

    /**
     * Empty constructor.
     */
    public JobContainer() {
    }

    /**
     * We need a Job to run or what it the point!
     *
     * @return A Job instance.
     */
    public Job getJob() {
        return (job);
    }

    /**
     * We need a Job to run or what it the point!
     *
     * @param j A Job instance.
     */
    public void setJob(Job j) {
        job = j;
    }

    /**
     * Start it up!
     */
    public void start() {

        Job j = getJob();
        if (j != null) {

            j.start();
            j.run();
        }

    }

    /**
     * Stop it!
     */
    public void stop() {

        Job j = getJob();
        if (j != null) {

            j.stop();
        }
        interrupt();
    }

}

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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * An abstract base class that others may extend that implement Job.
 * Added here is a boolean property called Terminate.  Child classes can check
 * this property value to decide if they should let their run method exit, thus
 * cleaning ending the job.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class AbstractJob implements Job, Jobable {

    private static final int DEFAULT_SLEEP = 500;

    private ArrayList<JobListener> jobList = new ArrayList<JobListener>();

    private volatile boolean terminate;
    private volatile long sleepTime;

    /**
     * Default empty constructor.
     */
    public AbstractJob() {

        setSleepTime(DEFAULT_SLEEP);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTerminate() {
        return (terminate);
    }

    /**
     * {@inheritDoc}
     */
    public void setTerminate(boolean b) {
        terminate = b;
    }

    /**
     * {@inheritDoc}
     */
    public long getSleepTime() {
        return (sleepTime);
    }

    /**
     * {@inheritDoc}
     */
    public void setSleepTime(long l) {
        sleepTime = l;
    }

    /**
     * {@inheritDoc}
     */
    public void addJobListener(JobListener l) {

        if (!jobList.contains(l)) {

            jobList.add(l);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeJobListener(JobListener l) {
        jobList.remove(l);
    }

    /**
     * Convenience method to fire an event with a certain type.
     *
     * @param type A given type.
     */
    public void fireJobEvent(int type) {
        processJobEvent(new JobEvent(this, type));
    }

    /**
     * Convenience method to fire an event with a certain type and message.
     *
     * @param type A given type.
     * @param message A given message.
     */
    public void fireJobEvent(int type, String message) {
        processJobEvent(new JobEvent(this, type, message));
    }

    /**
     * Convenience method to fire an event with a certain type and state.
     *
     * @param type A given type.
     * @param state A given state.
     */
    public void fireJobEvent(int type, Serializable state) {
        processJobEvent(new JobEvent(this, type, state));
    }

    /**
     * Convenience method to fire a given event instance.
     *
     * @param event A given event.
     */
    public void fireJobEvent(JobEvent event) {
        processJobEvent(event);
    }

    protected synchronized void processJobEvent(JobEvent event) {

        synchronized (jobList) {

            for (int i = 0; i < jobList.size(); i++) {

                JobListener l = jobList.get(i);
                l.jobUpdate(event);
            }
        }
    }

}


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

import java.util.ArrayList;

import org.jflicks.job.Job;
import org.jflicks.job.JobContainer;
import org.jflicks.tv.Recording;

/**
 * This class is a base implementation of the Worker interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseWorker implements Worker {

    private ArrayList<WorkerListener> workerList =
        new ArrayList<WorkerListener>();
    private ArrayList<JobContainer> jobContainerList;

    private String title;
    private String description;
    private boolean heavy;
    private boolean defaultRun;
    private boolean userSelectable;

    /**
     * Simple empty constructor.
     */
    public BaseWorker() {

        setHeavy(true);
        setDefaultRun(true);
        setUserSelectable(true);
        setJobContainerList(new ArrayList<JobContainer>());
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return (title);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given title value.
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return (description);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given description value.
     */
    public void setDescription(String s) {
        description = s;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isHeavy() {
        return (heavy);
    }

    /**
     * Convenience method to set this property.
     *
     * @param b The given heavy value.
     */
    public void setHeavy(boolean b) {
        heavy = b;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDefaultRun() {
        return (defaultRun);
    }

    /**
     * Convenience method to set this property.
     *
     * @param b The given heavy value.
     */
    public void setDefaultRun(boolean b) {
        defaultRun = b;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUserSelectable() {
        return (userSelectable);
    }

    /**
     * Convenience method to set this property.
     *
     * @param b The given boolean value.
     */
    public void setUserSelectable(boolean b) {
        userSelectable = b;
    }

    /**
     * {@inheritDoc}
     */
    public void addWorkerListener(WorkerListener l) {
        workerList.add(l);
    }

    /**
     * {@inheritDoc}
     */
    public void removeWorkerListener(WorkerListener l) {
        workerList.remove(l);
    }

    /**
     * Convenience method to fire an event with a certain type.
     *
     * @param type A given type.
     * @param r A given Recording.
     * @param update True if the Recording needs updating by the Scheduler.
     */
    public void fireWorkerEvent(int type, Recording r, boolean update) {
        processWorkerEvent(new WorkerEvent(this, type, r, update));
    }

    /**
     * Convenience method to fire an event with a certain type and message.
     *
     * @param type A given type.
     * @param r A given Recording.
     * @param message A given message.
     */
    public void fireWorkerEvent(int type, Recording r, String message) {
        processWorkerEvent(new WorkerEvent(this, type, r, message));
    }

    /**
     * Convenience method to fire a given event instance.
     *
     * @param event A given event.
     */
    public void fireWorkerEvent(WorkerEvent event) {
        processWorkerEvent(event);
    }

    protected synchronized void processWorkerEvent(WorkerEvent event) {

        synchronized (workerList) {

            for (int i = 0; i < workerList.size(); i++) {

                WorkerListener l = workerList.get(i);
                l.workerUpdate(event);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void cancel(Recording r) {

        removeJobContainer(r);
    }

    /**
     * We maintain a list of Jobs that are running or need to run.
     *
     * @return A List of JobContainer instances.
     */
    public ArrayList<JobContainer> getJobContainerList() {
        return (jobContainerList);
    }

    /**
     * We maintain a list of Jobs that are running or need to run.
     *
     * @param l A List of JobContainer instances.
     */
    public void setJobContainerList(ArrayList<JobContainer> l) {
        jobContainerList = l;
    }

    /**
     * Add a JobContainer to our list.
     *
     * @param jc A given JobContainer.
     */
    public void addJobContainer(JobContainer jc) {

        ArrayList<JobContainer> l = getJobContainerList();
        if ((jc != null) && (l != null)) {
            l.add(jc);
        }
    }

    /**
     * Remove a JobContainer from our list using a Job instance.
     *
     * @param j A given Job.
     */
    public void removeJobContainer(Job j) {

        ArrayList<JobContainer> l = getJobContainerList();
        if ((j != null) && (l != null)) {

            int index = -1;
            for (int i = 0; i < l.size(); i++) {

                if (l.get(i).getJob() == j) {

                    index = i;
                    break;
                }
            }

            if (index != -1) {

                l.remove(index);
            }
        }
    }

    /**
     * Remove a JobContainer from our list using a Recording instance.
     *
     * @param r A given Recording.
     */
    public void removeJobContainer(Recording r) {

        ArrayList<JobContainer> l = getJobContainerList();
        if ((r != null) && (l != null)) {

            int index = -1;
            for (int i = 0; i < l.size(); i++) {

                BaseWorkerJob job = (BaseWorkerJob) l.get(i).getJob();
                Recording tmp = job.getRecording();
                if (tmp.equals(r)) {

                    index = i;
                    break;
                }
            }
            if (index != -1) {

                JobContainer jc = l.get(index);
                jc.stop();
                l.remove(index);
            }
        }
    }

    /**
     * Override to return the title property.
     *
     * @return The title.
     */
    public String toString() {
        return (getTitle());
    }

}


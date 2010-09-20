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
package org.jflicks.tv.postproc;

import java.util.ArrayList;

import org.jflicks.configure.BaseConfig;
import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Recording;
import org.jflicks.tv.postproc.worker.Worker;
import org.jflicks.tv.postproc.worker.WorkerListener;
import org.jflicks.tv.scheduler.Scheduler;
import org.jflicks.util.Util;

/**
 * This class is a base implementation of the PostProc interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BasePostProc extends BaseConfig implements PostProc,
    WorkerListener {

    private String title;
    private NMS nms;
    private ArrayList<Worker> workerList;

    /**
     * Simple empty constructor.
     */
    public BasePostProc() {

        setWorkerList(new ArrayList<Worker>());
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
    public NMS getNMS() {
        return (nms);
    }

    /**
     * {@inheritDoc}
     */
    public void setNMS(NMS n) {
        nms = n;
    }

    private ArrayList<Worker> getWorkerList() {
        return (workerList);
    }

    private void setWorkerList(ArrayList<Worker> l) {
        workerList = l;
    }

    /**
     * Convenience method for extensions to add a worker instance.
     *
     * @param w A Worker to add.
     */
    public void addWorker(Worker w) {

        ArrayList<Worker> l = getWorkerList();
        if ((l != null) && (w != null)) {

            l.add(w);
            w.addWorkerListener(this);
        }
    }

    /**
     * Convenience method for extensions to remove a worker instance.
     *
     * @param w A Worker to remove.
     */
    public void removeWorker(Worker w) {

        ArrayList<Worker> l = getWorkerList();
        if ((l != null) && (w != null)) {
            l.remove(w);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Worker[] getWorkers() {

        Worker[] result = null;

        ArrayList<Worker> l = getWorkerList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Worker[l.size()]);
        }

        return (result);
    }

    /**
     * Convenience method for extensions to get a Worker by it's title.
     *
     * @param s A Worker title property.
     * @return A Worker instance if it exists.
     */
    public Worker getWorkerByTitle(String s) {

        Worker result = null;

        ArrayList<Worker> l = getWorkerList();
        if ((s != null) && (l != null) && (l.size() > 0)) {

            for (int i = 0; i < l.size(); i++) {

                Worker tmp = l.get(i);
                if (s.equals(tmp.getTitle())) {

                    result = tmp;
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * Convenience method to update a Recording if a Worker has deemed
     * it necessary.
     *
     * @param r A given Recording instance.
     */
    public void updateRecording(Recording r) {

        NMS n = getNMS();
        if ((n != null) && (r != null)) {

            Scheduler s = n.getScheduler();
            if (s != null) {

                s.updateRecording(r);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getConfiguredMaximumJobs() {

        int result = 1;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv =
                c.findNameValueByName(NMSConstants.POST_PROC_MAXIMUM_JOBS);
            if (nv != null) {

                result = Util.str2int(nv.getValue(), result);
            }
        }

        return (result);
    }

}


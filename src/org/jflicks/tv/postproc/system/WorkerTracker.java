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

import org.jflicks.tv.postproc.worker.Worker;
import org.jflicks.util.BaseTracker;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A tracker for the worker service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class WorkerTracker extends BaseTracker {

    private SystemPostProc systemPostProc;

    /**
     * Contructor with BundleContext and NMS instance.
     *
     * @param bc A given BundleContext needed to communicate with OSGi.
     * @param spp Our PostProc implementation.
     */
    public WorkerTracker(BundleContext bc, SystemPostProc spp) {

        super(bc, Worker.class.getName());
        setSystemPostProc(spp);
    }

    private SystemPostProc getSystemPostProc() {
        return (systemPostProc);
    }

    private void setSystemPostProc(SystemPostProc spp) {
        systemPostProc = spp;
    }

    /**
     * A new Worker service has come online.
     *
     * @param sr The ServiceReference object.
     * @return The instantiation.
     */
    public Object addingService(ServiceReference sr) {

        Object result = null;

        BundleContext bc = getBundleContext();
        SystemPostProc spp = getSystemPostProc();
        if ((bc != null) && (spp != null)) {

            Worker service = (Worker) bc.getService(sr);
            spp.addWorker(service);
            result = service;
        }

        return (result);
    }

    /**
     * A worker service has been modified.
     *
     * @param sr The Worker ServiceReference.
     * @param svc The Worker instance.
     */
    public void modifiedService(ServiceReference sr, Object svc) {
    }

    /**
     * A worker service has gone away.  Bye-bye.
     *
     * @param sr The ServiceReference.
     * @param svc The instance.
     */
    public void removedService(ServiceReference sr, Object svc) {

        SystemPostProc spp = getSystemPostProc();
        if (spp != null) {

            spp.removeWorker((Worker) svc);
        }
    }

}

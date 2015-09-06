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
    along with JFLICKS.  If not, see <remote://www.gnu.org/licenses/>.
*/
package org.jflicks.nms.system;

import ch.ethz.iks.r_osgi.RemoteOSGiService;

import org.jflicks.discovery.ServiceDescription;
import org.jflicks.util.BaseTracker;
import org.jflicks.util.LogUtil;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A tracker for the remote stream service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RemoteTracker extends BaseTracker {

    private ServiceDescription serviceDescription;

    /**
     * Contructor with BundleContext and model.
     *
     * @param bc A given BundleContext needed to communicate with OSGi.
     * @param sd Our ServiceDescription.
     */
    public RemoteTracker(BundleContext bc, ServiceDescription sd) {

        super(bc, RemoteOSGiService.class.getName());
        setServiceDescription(sd);
    }

    private ServiceDescription getServiceDescription() {
        return (serviceDescription);
    }

    private void setServiceDescription(ServiceDescription sd) {
        serviceDescription = sd;
    }

    /**
     * A new RemoteOSGiService has come online.
     *
     * @param sr The ServiceReference object.
     * @return The instantiation.
     */
    public Object addingService(ServiceReference sr) {

        Object result = null;

        BundleContext bc = getBundleContext();
        ServiceDescription sd = getServiceDescription();
        if ((bc != null) && (sd != null)) {

            RemoteOSGiService service = (RemoteOSGiService) bc.getService(sr);
            if (service != null) {

                //sd.setPort(service.getListeningPort("r-osgi"));
                LogUtil.log(LogUtil.DEBUG, "port: " + sd.getPort());
            }

            result = service;
        }

        return (result);
    }

    /**
     * A remote service has been modified.
     *
     * @param sr The Stream ServiceReference.
     * @param svc The Stream instance.
     */
    public void modifiedService(ServiceReference sr, Object svc) {
    }

    /**
     * A remote service has gone away.  Bye-bye.
     *
     * @param sr The ServiceReference.
     * @param svc The instance.
     */
    public void removedService(ServiceReference sr, Object svc) {
    }

}

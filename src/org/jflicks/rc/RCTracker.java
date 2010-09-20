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
package org.jflicks.rc;

import org.jflicks.util.BaseTracker;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A tracker for the theme service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RCTracker extends BaseTracker {

    private RCProperty rcProperty;

    /**
     * Contructor with BundleContext and RCProperty instance.
     *
     * @param bc A given BundleContext needed to communicate with OSGi.
     * @param rcp Our user of a RC service.
     */
    public RCTracker(BundleContext bc, RCProperty rcp) {

        super(bc, RC.class.getName());
        setRCProperty(rcp);
    }

    private RCProperty getRCProperty() {
        return (rcProperty);
    }

    private void setRCProperty(RCProperty rcp) {
        rcProperty = rcp;
    }

    /**
     * A new RC service has come online.
     *
     * @param sr The ServiceReference object.
     * @return The instantiation.
     */
    public Object addingService(ServiceReference sr) {

        Object result = null;

        BundleContext bc = getBundleContext();
        RCProperty rcp = getRCProperty();
        if ((bc != null) && (rcp != null)) {

            RC service = (RC) bc.getService(sr);
            rcp.setRC(service);
            result = service;
        }

        return (result);
    }

    /**
     * A recorder service has been modified.
     *
     * @param sr The RC ServiceReference.
     * @param svc The RC instance.
     */
    public void modifiedService(ServiceReference sr, Object svc) {
    }

    /**
     * A recorder service has gone away.  Bye-bye.
     *
     * @param sr The ServiceReference.
     * @param svc The instance.
     */
    public void removedService(ServiceReference sr, Object svc) {

        RCProperty rcp = getRCProperty();
        if (rcp != null) {

            rcp.setRC(null);
        }
    }

}

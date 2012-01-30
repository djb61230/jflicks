/*
    This file is part of JFLICKS.

    JFLICKS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JFLICKS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MEUpdateHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JFLICKS.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.jflicks.update;

import org.jflicks.util.BaseTracker;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A tracker for the image cache service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class UpdateTracker extends BaseTracker {

    private UpdateProperty updateProperty;

    /**
     * Contructor with BundleContext and UpdateProperty instance.
     *
     * @param bc A given BundleContext needed to communicate with OSGi.
     * @param icp Our user of a Update service.
     */
    public UpdateTracker(BundleContext bc, UpdateProperty icp) {

        super(bc, Update.class.getName());
        setUpdateProperty(icp);
    }

    private UpdateProperty getUpdateProperty() {
        return (updateProperty);
    }

    private void setUpdateProperty(UpdateProperty up) {
        updateProperty = up;
    }

    /**
     * A new Update service has come online.
     *
     * @param sr The ServiceReference object.
     * @return The instantiation.
     */
    public Object addingService(ServiceReference sr) {

        Object result = null;

        BundleContext bc = getBundleContext();
        UpdateProperty icp = getUpdateProperty();
        if ((bc != null) && (icp != null)) {

            Update service = (Update) bc.getService(sr);
            icp.setUpdate(service);
            result = service;
        }

        return (result);
    }

    /**
     * A image cache service has been modified.
     *
     * @param sr The Update ServiceReference.
     * @param svc The Update instance.
     */
    public void modifiedService(ServiceReference sr, Object svc) {
    }

    /**
     * A image cache service has gone away.  Bye-bye.
     *
     * @param sr The ServiceReference.
     * @param svc The instance.
     */
    public void removedService(ServiceReference sr, Object svc) {

        UpdateProperty icp = getUpdateProperty();
        if (icp != null) {

            icp.setUpdate(null);
        }
    }

}

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
package org.jflicks.stream.http;

import org.jflicks.util.BaseTracker;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

/**
 * A tracker for the http service.
 *
 * @author Doug Barnum
 * @version 1.0 - 14 Sep 09
 */
public class HttpServiceTracker extends BaseTracker {

    private HttpServiceProperty httpServiceProperty;

    /**
     * Contructor with BundleContext and model.
     *
     * @param bc A given BundleContext needed to communicate with OSGi.
     * @param p Our http service property.
     */
    public HttpServiceTracker(BundleContext bc, HttpServiceProperty p) {

        super(bc, HttpService.class.getName());
        setHttpServiceProperty(p);
    }

    private HttpServiceProperty getHttpServiceProperty() {
        return (httpServiceProperty);
    }

    private void setHttpServiceProperty(HttpServiceProperty p) {
        httpServiceProperty = p;
    }

    /**
     * A new HttpService has come online.
     *
     * @param sr The ServiceReference object.
     * @return The instantiation.
     */
    public Object addingService(ServiceReference sr) {

        Object result = null;

        BundleContext bc = getBundleContext();
        HttpServiceProperty p = getHttpServiceProperty();
        if ((bc != null) && (p != null)) {

            HttpService service = (HttpService) bc.getService(sr);
            p.setHttpService(service);
            result = service;
        }

        return (result);
    }

    /**
     * A http service has been modified.
     *
     * @param sr The Stream ServiceReference.
     * @param svc The Stream instance.
     */
    public void modifiedService(ServiceReference sr, Object svc) {
    }

    /**
     * A http service has gone away.  Bye-bye.
     *
     * @param sr The ServiceReference.
     * @param svc The instance.
     */
    public void removedService(ServiceReference sr, Object svc) {

        HttpServiceProperty p = getHttpServiceProperty();
        if (p != null) {

            p.setHttpService(null);
        }
    }

}

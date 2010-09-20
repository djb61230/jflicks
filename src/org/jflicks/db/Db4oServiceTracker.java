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
package org.jflicks.db;

import org.jflicks.util.BaseTracker;

import com.db4o.osgi.Db4oService;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Track all Db4oService services.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Db4oServiceTracker extends BaseTracker {

    private DbWorker dbWorker;

    /**
     * Contructor with BundleContext and model.
     *
     * @param bc A given BundleContext needed to communicate with OSGi.
     * @param w Our object that needs to be hooked up with DBServices.
     */
    public Db4oServiceTracker(BundleContext bc, DbWorker w) {

        super(bc, Db4oService.class.getName());
        setDbWorker(w);
    }

    private DbWorker getDbWorker() {
        return (dbWorker);
    }

    private void setDbWorker(DbWorker w) {
        dbWorker = w;
    }

    /**
     * A new Db4oService has come online.
     *
     * @param sr The Db4oService ServiceReference object.
     * @return The Db4oService instantiation.
     */
    public Object addingService(ServiceReference sr) {

        Object result = null;

        BundleContext bc = getBundleContext();
        DbWorker w = getDbWorker();
        if ((bc != null) && (w != null)) {

            Db4oService s = (Db4oService) bc.getService(sr);
            w.setDb4oService(s);
            result = s;
        }

        return (result);
    }

    /**
     * A Db4oService has been modified.
     *
     * @param sr The Db4oService ServiceReference.
     * @param svc The Db4oService instance.
     */
    public void modifiedService(ServiceReference sr, Object svc) {
    }

    /**
     * A Db4oService service has gone away.  Bye-bye.
     *
     * @param sr The Db4oService ServiceReference.
     * @param svc The Db4oService instance.
     */
    public void removedService(ServiceReference sr, Object svc) {

        DbWorker w = getDbWorker();
        if (w != null) {
            w.setDb4oService(null);
        }
    }

}

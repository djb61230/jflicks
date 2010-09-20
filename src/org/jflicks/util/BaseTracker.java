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
package org.jflicks.util;

import java.util.HashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simple class that others can extend that simplifies tracking services
 * even easier.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseTracker extends ServiceTracker {

    private BundleContext bundleContext;
    private HashMap<String, ServiceReference> hashMap;

    /**
     * A simple contructor that will track all instances of classname.
     *
     * @param bc A given BundleContext instance.
     * @param className A class name we will track.
     */
    public BaseTracker(BundleContext bc, String className) {

        super(bc, className, null);
        setBundleContext(bc);
        setHashMap(new HashMap<String, ServiceReference>());
    }

    /**
     * Any tracker needs a BundleContext instance to communicate with OSGi.
     *
     * @return A given BundleContext instance.
     */
    public BundleContext getBundleContext() {
        return (bundleContext);
    }

    /**
     * Any tracker needs a BundleContext instance to communicate with OSGi.
     *
     * @param bc A given BundleContext instance we can use in the future.
     */
    public void setBundleContext(BundleContext bc) {
        bundleContext = bc;
    }

    private HashMap<String, ServiceReference> getHashMap() {
        return (hashMap);
    }

    private void setHashMap(HashMap<String, ServiceReference> hm) {
        hashMap = hm;
    }

    /**
     * We maintain a list of service references so we can "unget" when
     * they are no longer needed or available.  Here we add one to
     * our list.
     *
     * @param name A given ServiceReference name we identify it.
     * @param sr A given ServiceReference.
     */
    public void add(String name, ServiceReference sr) {

        HashMap<String, ServiceReference> hm = getHashMap();
        if ((hm != null) && (name != null) && (sr != null)) {

            hm.put(name, sr);
        }
    }

    /**
     * We maintain a list of service references so we can "unget" when
     * they are no longer needed or available.  Here we remove it from
     * our list.
     *
     * @param name A given ServiceReference name we previously stored.
     */
    public void remove(String name) {

        HashMap<String, ServiceReference> hm = getHashMap();
        if ((hm != null) && (name != null)) {

            hm.remove(name);
        }
    }

    /**
     * We maintain a list of service references so we can "unget" when
     * they are no longer needed or available.
     *
     * @param name A given ServiceReference name we previously stored.
     * @return A ServiceReference instance that is identified by the
     * given name.
     */
    public ServiceReference get(String name) {

        ServiceReference result = null;

        HashMap<String, ServiceReference> hm = getHashMap();
        if ((hm != null) && (name != null)) {

            result = hm.get(name);
        }

        return (result);
    }

    /**
     * A previously tracked bundle needs to be "unget" so the system can
     * properly maintain who is using what.
     *
     * @param name A given bundle or service name.
     */
    public void dispose(String name) {

        if (name != null) {

            ServiceReference sr = get(name);
            BundleContext bc = getBundleContext();
            if ((bc != null) && (sr != null)) {

                remove(name);
                bc.ungetService(sr);
            }
        }
    }

    /**
     * Make sure the system is ready to track bundles.
     *
     * @return True if the tracker is ready.
     */
    public boolean ready() {

        boolean result = false;

        BundleContext bc = getBundleContext();
        if (bc != null) {

            if ((bc.getBundle(0).getState()
                & (Bundle.STARTING | Bundle.ACTIVE)) == 0) {

                result = false;

            } else {

                result = true;
            }
        }

        return (result);
    }

}

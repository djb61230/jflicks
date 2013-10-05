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
package org.jflicks.restlet;

import java.util.ArrayList;

import org.jflicks.log.Log;
import org.jflicks.nms.NMS;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.restlet.ext.wadl.WadlApplication;

/**
 * This class is a base implementation of a restlet application.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseApplication extends WadlApplication implements Log {

    private ServiceTracker logServiceTracker;
    private BundleContext bundleContext;
    private ArrayList<NMS> nmsList;

    /**
     * Simple empty constructor.
     */
    public BaseApplication() {

        setNMSList(new ArrayList<NMS>());
    }

    /**
     * {@inheritDoc}
     */
    public ServiceTracker getLogServiceTracker() {
        return (logServiceTracker);
    }

    /**
     * {@inheritDoc}
     */
    public void setLogServiceTracker(ServiceTracker st) {
        logServiceTracker = st;
    }

    /**
     * {@inheritDoc}
     */
    public void log(int level, String message) {

        ServiceTracker st = getLogServiceTracker();
        if ((st != null) && (message != null)) {

            LogService ls = (LogService) st.getService();
            if (ls != null) {

                ls.log(level, message);
            }
        }
    }

    public BundleContext getBundleContext() {
        return (bundleContext);
    }

    public void setBundleContext(BundleContext bc) {
        bundleContext = bc;
    }

    private ArrayList<NMS> getNMSList() {
        return (nmsList);
    }

    private void setNMSList(ArrayList<NMS> l) {
        nmsList = l;
    }

    public NMS[] getNMS() {

        NMS[] result = null;

        ArrayList<NMS> l = getNMSList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new NMS[l.size()]);
        }

        return (result);
    }

    public void setNMS(NMS[] array) {

        System.out.println("setNMS dude");
        ArrayList<NMS> l = getNMSList();
        if (l != null) {

            NMS[] oldArray = null;
            if (l.size() > 0) {

                oldArray = l.toArray(new NMS[l.size()]);
            }

            l.clear();
            if ((array != null) && (array.length > 0)) {

                for (int i = 0; i < array.length; i++) {

                    l.add(array[i]);
                }
            }

            NMS[] newArray = null;
            if (l.size() > 0) {

                newArray = l.toArray(new NMS[l.size()]);
            }
        }
    }

}


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

import org.jflicks.nms.NMS;
import org.jflicks.restlet.servercomponent.ServerComponent;
import org.jflicks.restlet.servercomponent.ServerComponentNotify;
import org.jflicks.util.LogUtil;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.ext.wadl.WadlApplication;

/**
 * This class is a base implementation of a restlet application.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseApplication extends WadlApplication implements ServerComponentNotify {

    private boolean attached;
    private ServerComponent serverComponent;
    private BundleContext bundleContext;
    private ArrayList<NMS> nmsList;
    private String alias;

    /**
     * Simple empty constructor.
     */
    public BaseApplication() {

        setNMSList(new ArrayList<NMS>());
    }

    /**
     * {@inheritDoc}
     */
    public ServerComponent getServerComponent() {
        return (serverComponent);
    }

    /**
     * {@inheritDoc}
     */
    public void setServerComponent(ServerComponent c) {

        serverComponent = c;

        if (serverComponent != null) {

            attach();

        } else {

            // The server component "has gone away" so flag that
            // we will need to reattach if it comes back.
            setAttached(false);
        }
    }

    /**
     * Run time property to signify is our application has been attached
     * to a Restlet server component.
     *
     * @return True when attached.
     */
    public boolean isAttached() {
        return (attached);
    }

    private void setAttached(boolean b) {
        attached = b;
    }

    /**
     * An application has an alias so it's URLs will be unique.
     *
     * @return A String instance.
     */
    public String getAlias() {
        return (alias);
    }

    /**
     * An application has an alias so it's URLs will be unique.
     *
     * @param s A String instance.
     */
    public void setAlias(String s) {
        alias = s;
    }

    private void attach() {

        if (!isAttached()) {

            // We are not attached so we will try as long as we have valid
            // ServerComponent instance.
            ServerComponent sc = getServerComponent();
            if (sc != null) {

                Component c = sc.getComponent();
                if (c != null) {

                    try {

                        setAttached(true);
                        String att = "/jflicks/" + getAlias();
                        c.getDefaultHost().attach(att, (Application) this);
                        c.start();

                    } catch (Exception ex) {

                        LogUtil.log(LogUtil.DEBUG, ex.getMessage());
                    }
                }
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

        LogUtil.log(LogUtil.DEBUG, "setNMS dude");
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

        LiveTVSupport lsup = LiveTVSupport.getInstance();
        lsup.setNMS(getNMS());

        NMSSupport nsup = NMSSupport.getInstance();
        nsup.setNMS(getNMS());
    }

    public String getBaseURI() {

        String result = null;

        ServerComponent sc = getServerComponent();
        if (sc != null) {

            result = sc.getBaseURI() + "/jflicks/" + getAlias();
        }

        return (result);
    }

}


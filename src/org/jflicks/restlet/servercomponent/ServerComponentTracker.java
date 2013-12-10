package org.jflicks.restlet.servercomponent;

import org.jflicks.util.BaseTracker;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A tracker for the http service.
 *
 * @author Doug Barnum
 * @version 3.0
 */
public class ServerComponentTracker extends BaseTracker {

    private ServerComponentNotify serverComponentNotify;

    /**
     * Contructor with BundleContext and model.
     *
     * @param bc A given BundleContext needed to communicate with OSGi.
     * @param n Our component service property.
     */
    public ServerComponentTracker(BundleContext bc, ServerComponentNotify n) {

        super(bc, ServerComponent.class.getName());
        setServerComponentNotify(n);
    }

    private ServerComponentNotify getServerComponentNotify() {
        return (serverComponentNotify);
    }

    private void setServerComponentNotify(ServerComponentNotify n) {
        serverComponentNotify = n;
    }

    /**
     * A new ServerComponent has come online.
     *
     * @param sr The ServiceReference object.
     * @return The instantiation.
     */
    public Object addingService(ServiceReference sr) {

        Object result = null;

        BundleContext bc = getBundleContext();
        ServerComponentNotify n = getServerComponentNotify();
        if ((bc != null) && (n != null)) {

            ServerComponent service = (ServerComponent) bc.getService(sr);
            n.setServerComponent(service);
            result = service;
        }

        return (result);
    }

    /**
     * A component service has been modified.
     *
     * @param sr The Stream ServiceReference.
     * @param svc The Stream instance.
     */
    public void modifiedService(ServiceReference sr, Object svc) {
    }

    /**
     * A component service has gone away.  Bye-bye.
     *
     * @param sr The ServiceReference.
     * @param svc The instance.
     */
    public void removedService(ServiceReference sr, Object svc) {

        ServerComponentNotify n = getServerComponentNotify();
        if (n != null) {

            n.setServerComponent(null);
        }
    }

}

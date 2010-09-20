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
package org.jflicks.nms.remote;

import ch.ethz.iks.r_osgi.RemoteOSGiService;
import ch.ethz.iks.r_osgi.RemoteServiceReference;
import ch.ethz.iks.r_osgi.URI;

import org.jflicks.nms.NMS;
import org.jflicks.util.BaseTracker;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A tracker for the remote NMS service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RemoteTracker extends BaseTracker {

    private RemoteOSGiService currentRemoteOSGiService;
    private Bundle remoteServiceBundle;
    private RemoteServiceReference remoteServiceReference;
    private boolean connected;

    /**
     * Contructor with BundleContext and model.
     *
     * @param bc A given BundleContext needed to communicate with OSGi.
     */
    public RemoteTracker(BundleContext bc) {

        super(bc, RemoteOSGiService.class.getName());
    }

    private RemoteOSGiService getCurrentRemoteOSGiService() {
        return (currentRemoteOSGiService);
    }

    private void setCurrentRemoteOSGiService(RemoteOSGiService s) {
        currentRemoteOSGiService = s;
    }

    private Bundle getRemoteServiceBundle() {
        return (remoteServiceBundle);
    }

    private void setRemoteServiceBundle(Bundle b) {
        remoteServiceBundle = b;
    }

    private RemoteServiceReference getRemoteServiceReference() {
        return (remoteServiceReference);
    }

    private void setRemoteServiceReference(RemoteServiceReference r) {
        remoteServiceReference = r;
    }

    /**
     * Are we currently connected then return True.
     *
     * @return True when connected.
     */
    public boolean isConnected() {
        return (connected);
    }

    private void setConnected(boolean b) {
        connected = b;
    }

    /**
     * Cannot connect to a remote NMS until we have the RemoteOSGiService
     * up and running.  We can quite easily get a service response over the
     * network before we have fully running services running.  So this should
     * be used to check before trying to do the remote check.
     *
     * @return True if the RemoteOSGiService service is available.
     */
    public boolean hasRemoteOSGiService() {

        return (getCurrentRemoteOSGiService() != null);
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
        if (bc != null) {

            RemoteOSGiService service = (RemoteOSGiService) bc.getService(sr);
            if (service != null) {

                setCurrentRemoteOSGiService(service);
            }

            result = service;
        }

        return (result);
    }

    /**
     * A remote service has been modified.
     *
     * @param sr The NMS ServiceReference.
     * @param svc The NMS instance.
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

        setCurrentRemoteOSGiService(null);
    }

    /**
     * This will connect to the remote system which will in turn cause
     * it to be discovered locally as if it was local.
     *
     * @param host The host name as a String.
     * @param port The port the remote RMI is running.
     */
    public void connect(String host, int port) {

        System.out.println("connect: " + host + ":" + port);
        RemoteOSGiService r = getCurrentRemoteOSGiService();
        if ((r != null) && (host != null)) {

            try {

                // Ok we don't already have a valid connection.
                URI uri = new URI("r-osgi://" + host + ":" + port);
                r.connect(uri);

                RemoteServiceReference[] srefs =
                    r.getRemoteServiceReferences(uri, NMS.class.getName(),
                    null);
                if ((srefs != null) && (srefs.length > 0)) {

                    setRemoteServiceReference(srefs[0]);
                    r.getRemoteService(srefs[0]);
                }

            } catch (Exception ex) {
            }
        }
    }

    /**
     * Convenience method to disconnect to a URI given a host and port.
     *
     * @param host A given host name.
     * @param port A given port.
     */
    public void disconnect(String host, int port) {

        RemoteOSGiService r = getCurrentRemoteOSGiService();
        if ((r != null) && (host != null)) {

            try {

                URI uri = new URI("r-osgi://" + host + ":" + port);
                r.disconnect(uri);

            } catch (Exception ex) {

                System.out.println("WARNING: could not disconnect.");
            }
        }
    }

    /**
     * Convenience method to disconnect to a URI.
     *
     * @param uri A given URI instance.
     */
    public void disconnect(URI uri) {

        RemoteOSGiService r = getCurrentRemoteOSGiService();
        if ((r != null) && (uri != null)) {

            try {

                r.disconnect(uri);

            } catch (Exception ex) {
            }
        }
    }

}

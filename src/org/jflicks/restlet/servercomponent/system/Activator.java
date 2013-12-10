package org.jflicks.restlet.servercomponent.system;

import java.io.File;
import java.util.Hashtable;
import java.util.Properties;

import org.jflicks.restlet.servercomponent.ServerComponent;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.util.Series;

/**
 * Activator for our simple security implementation.
 *
 * @author Doug Barnum
 * @version 3.0
 */
public class Activator implements BundleActivator {

    private SystemServerComponent systemServerComponent;
    private ServiceRegistration serviceRegistration;

    public void start(BundleContext bc) {

        systemServerComponent = new SystemServerComponent(bc);
        systemServerComponent.start();

        Hashtable<String, String> h = new Hashtable<String, String>();
        h.put(Constants.SERVICE_PID, systemServerComponent.getId());
        serviceRegistration =
            bc.registerService(ServerComponent.class.getName(),
                systemServerComponent, h);
    }

    public void stop(BundleContext bc) {

        if (systemServerComponent != null) {

            Component component = systemServerComponent.getComponent();
            if (component != null) {

                try {

                    component.stop();

                } catch (Exception ex) {
                }

                component = null;
            }

            // Unregister as a ServerComponent.
            ServiceRegistration sr =
                systemServerComponent.getServiceRegistration();
            if (sr != null) {

                sr.unregister();
            }
        }

        // Unregister as a managed service.
        if (serviceRegistration != null) {

            serviceRegistration.unregister();
            serviceRegistration = null;
        }
    }

}

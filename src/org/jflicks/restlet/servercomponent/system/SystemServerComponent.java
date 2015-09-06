package org.jflicks.restlet.servercomponent.system;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Properties;

import org.jflicks.restlet.servercomponent.BaseServerComponent;
import org.jflicks.restlet.servercomponent.ServerComponent;
import org.jflicks.util.LogUtil;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.util.Series;
import org.jflicks.util.Util;

/**
 * A ServerComponent implementation that runs all our restlet apps.
 *
 * @author Doug Barnum
 * @version 3.0
 */
public class SystemServerComponent extends BaseServerComponent {

    private BundleContext bundleContext;
    private ServiceRegistration serviceRegistration;

    /**
     * Our component that supports our RESTlet applications.
     *
     * @param bc We need to interact with OSGi with the BundleContext.
     */
    public SystemServerComponent(BundleContext bc) {

        super();
        setId("ServerComponent");
        setBundleContext(bc);
    }

    /**
     * We advertise ourselves as a ServerComponent after we get our
     * configuration information from ConfigAdmin.  We cannot really
     * do anything until we have that information because it includes
     * things like port values and such.  We allow reading this property
     * since our Activator (or whomever is in charge) can manage our
     * resources properly.
     *
     * @return A ServiceRegistration instance.
     */
    public ServiceRegistration getServiceRegistration() {
        return (serviceRegistration);
    }

    private void setServiceRegistration(ServiceRegistration sr) {
        serviceRegistration = sr;
    }

    private BundleContext getBundleContext() {
        return (bundleContext);
    }

    private void setBundleContext(BundleContext bc) {
        bundleContext = bc;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        // Now we start things up right.
        try {

            String restPort = System.getProperty("org.jflicks.restlet.servercomponent.system.SystemServerComponent");
            int httpPort = Util.str2int(restPort, 8182);
            Component c = new Component();
            setComponent(c);
            c.getServers().add(Protocol.HTTP, httpPort);
            c.getClients().add(Protocol.FILE);

        } catch (Exception ex) {

            LogUtil.log(LogUtil.WARNING, "ServerComponent: " + ex.getMessage());
        }

        Hashtable<String, String> h = new Hashtable<String, String>();
        h.put(Constants.SERVICE_PID, getId());
        setServiceRegistration(getBundleContext().registerService(
            ServerComponent.class.getName(), this, h));
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        // At this point we have the proper settings and should start our
        // RESTlet component.  If it is currently non-null, we have to stop
        // the old one.
        Component old = getComponent();
        if (old != null) {

            try {

                old.stop();

            } catch (Exception ex) {

                LogUtil.log(LogUtil.WARNING, "ServerComponent: " + ex.getMessage());
            }

            // Also let's unregister so restlet applications can know that
            // they will need to re-attach after we restart things.
            ServiceRegistration sr = getServiceRegistration();
            if (sr != null) {

                sr.unregister();
            }
        }
    }

    private String getHostname() {

        String result = "localhost";

        try {

            InetAddress addr = InetAddress.getLocalHost();
            result = addr.getHostName();

        } catch (UnknownHostException e) {
        }

        return (result);
    }

}

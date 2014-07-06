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
    along with JFLICKS.  If not, see <recorder://www.gnu.org/licenses/>.
*/
package org.jflicks.restlet.nms;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.jflicks.util.BaseActivator;
import org.jflicks.restlet.NMSTracker;
import org.jflicks.restlet.servercomponent.ServerComponentTracker;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simple activater that starts our NMS restlet.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private JmDNS jmdns;
    private NMSTracker nmsTracker;
    private ServerComponentTracker serverComponentTracker;
    private ServiceTracker logServiceTracker;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        NMSApplication app = new NMSApplication();

        nmsTracker = new NMSTracker(bc, app);
        nmsTracker.open();

        serverComponentTracker = new ServerComponentTracker(bc, app);
        serverComponentTracker.open();

        logServiceTracker =
            new ServiceTracker(bc, LogService.class.getName(), null);
        app.setLogServiceTracker(logServiceTracker);
        logServiceTracker.open();

        // Setup DNS for discovery.
        try {

            jmdns = JmDNS.create("localhost");
            ServiceInfo si = ServiceInfo.create("_http._tcp.local.",
                "jflicks", 8182, "jflicks REST service");
            jmdns.registerService(si);
            System.out.println("we have it registered dude!");

        } catch (Exception ex) {

            System.out.println("poo: " + ex.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        if (nmsTracker != null) {

            nmsTracker.close();
            nmsTracker = null;
        }

        if (logServiceTracker != null) {

            logServiceTracker.close();
            logServiceTracker = null;
        }

        if (serverComponentTracker != null) {

            serverComponentTracker.close();
            serverComponentTracker = null;
        }

        if (jmdns != null) {

            jmdns.unregisterAllServices();
        }
    }

}

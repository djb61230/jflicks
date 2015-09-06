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

import org.jflicks.restlet.NMSTracker;
import org.jflicks.restlet.servercomponent.ServerComponentTracker;
import org.jflicks.util.BaseActivator;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
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

        // Setup DNS for discovery.
        try {

            String restPort = System.getProperty("org.jflicks.restlet.servercomponent.system.SystemServerComponent");
            int defaultPort = Util.str2int(restPort, 8182);

            jmdns = JmDNS.create("localhost");
            ServiceInfo si = ServiceInfo.create("_http._tcp.local.",
                "jflicks", defaultPort, "jflicks REST service");
            jmdns.registerService(si);
            LogUtil.log(LogUtil.DEBUG, "we have it registered dude!");

        } catch (Exception ex) {

            LogUtil.log(LogUtil.WARNING, "poo: " + ex.getMessage());
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

        if (serverComponentTracker != null) {

            serverComponentTracker.close();
            serverComponentTracker = null;
        }

        if (jmdns != null) {

            jmdns.unregisterAllServices();
        }
    }

}

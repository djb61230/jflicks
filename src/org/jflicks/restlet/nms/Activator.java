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

import org.jflicks.util.BaseActivator;
import org.jflicks.restlet.NMSTracker;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 * Simple activater that starts our NMS restlet.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private Component component;
    private NMSTracker nmsTracker;
    private ServiceTracker logServiceTracker;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        try {

            component = new Component();
            component.getServers().add(Protocol.HTTP, 8182);

            // Here we attach our restlet application.
            NMSApplication app = new NMSApplication();
            RecordingResource.setNMSApplication(app);
            nmsTracker = new NMSTracker(bc, app);
            nmsTracker.open();

            logServiceTracker =
                new ServiceTracker(bc, LogService.class.getName(), null);
            app.setLogServiceTracker(logServiceTracker);
            logServiceTracker.open();

            component.getDefaultHost().attach("/nms", app);
            component.start();

        } catch (Exception ex) {

            System.out.println(ex.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        if (component != null) {

            try {

                component.stop();

            } catch (Exception ex) {

                System.out.println(ex.getMessage());
            }

            component = null;
        }

        if (nmsTracker != null) {

            nmsTracker.close();
            nmsTracker = null;
        }
    }

}

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
package org.jflicks.ui.view.aspirin;

import java.util.Hashtable;

import org.jflicks.mvc.Controller;
import org.jflicks.mvc.View;
import org.jflicks.util.BaseActivator;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simple activator that creates a AspirinView and starts it.  Also registers
 * the AspirinView so a Controller can find it.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private ServiceTracker controllerServiceTracker;
    private ServiceTracker logServiceTracker;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        AspirinView v = new AspirinView();
        v.setBundleContext(bc);

        ServiceTracker cst =
            new ServiceTracker(bc, Controller.class.getName(), null);
        setControllerServiceTracker(cst);
        v.setControllerServiceTracker(cst);
        cst.open();

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(AspirinView.TITLE_PROPERTY, "JFLICKS-ASPIRIN");

        bc.registerService(View.class.getName(), v, dict);

        logServiceTracker =
            new ServiceTracker(bc, LogService.class.getName(), null);
        v.setLogServiceTracker(logServiceTracker);
        logServiceTracker.open();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        ServiceTracker cst = getControllerServiceTracker();
        if (cst != null) {
            cst.close();
        }

        if (logServiceTracker != null) {

            logServiceTracker.close();
            logServiceTracker = null;
        }
    }

    private ServiceTracker getControllerServiceTracker() {
        return (controllerServiceTracker);
    }

    private void setControllerServiceTracker(ServiceTracker cst) {
        controllerServiceTracker = cst;
    }

}

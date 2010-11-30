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
package org.jflicks.ui.view.fe.screen.schedule;

import java.util.Hashtable;

import org.jflicks.rc.RCTracker;
import org.jflicks.ui.view.fe.screen.Screen;
import org.jflicks.util.BaseActivator;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private RCTracker rcTracker;
    private ServiceTracker logServiceTracker;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        ScheduleScreen s = new ScheduleScreen();

        RCTracker rct = new RCTracker(bc, s);
        setRCTracker(rct);
        rct.open();

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(Screen.TITLE_PROPERTY, s.getTitle());

        bc.registerService(Screen.class.getName(), s, dict);

        logServiceTracker =
            new ServiceTracker(bc, LogService.class.getName(), null);
        s.setLogServiceTracker(logServiceTracker);
        logServiceTracker.open();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        RCTracker rct = getRCTracker();
        if (rct != null) {
            rct.close();
        }

        if (logServiceTracker != null) {

            logServiceTracker.close();
            logServiceTracker = null;
        }
    }

    private RCTracker getRCTracker() {
        return (rcTracker);
    }

    private void setRCTracker(RCTracker t) {
        rcTracker = t;
    }

}

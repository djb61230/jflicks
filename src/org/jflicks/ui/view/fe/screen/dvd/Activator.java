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
package org.jflicks.ui.view.fe.screen.dvd;

import java.util.Hashtable;

import org.jflicks.player.Player;
import org.jflicks.rc.RCTracker;
import org.jflicks.ui.view.fe.screen.Screen;
import org.jflicks.util.BaseActivator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.Filter;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simple screen that calls a DVD player.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private ServiceTracker serviceTracker;
    private RCTracker rcTracker;
    private ServiceTracker logServiceTracker;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        DVDScreen s = new DVDScreen();

        // Now we listen for command events.
        String[] topics = new String[] {
            "org/jflicks/rc/COMMAND"
        };

        Hashtable<String, String[]> h = new Hashtable<String, String[]>();
        h.put(EventConstants.EVENT_TOPIC, topics);
        bc.registerService(EventHandler.class.getName(), s, h);

        RCTracker rct = new RCTracker(bc, s);
        setRCTracker(rct);
        rct.open();

        try {

            Filter filter = bc.createFilter("(Player-Handle="
                + Player.PLAYER_VIDEO_DVD + ")");
            ServiceTracker st = new ServiceTracker(bc, filter, null);
            setServiceTracker(st);
            st.open();
            s.setPlayerServiceTracker(st);

        } catch (InvalidSyntaxException ex) {
        }

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

        ServiceTracker t = getServiceTracker();
        if (t != null) {
            t.close();
        }

        RCTracker rct = getRCTracker();
        if (rct != null) {
            rct.close();
        }

        if (logServiceTracker != null) {

            logServiceTracker.close();
            logServiceTracker = null;
        }
    }

    private ServiceTracker getServiceTracker() {
        return (serviceTracker);
    }

    private void setServiceTracker(ServiceTracker t) {
        serviceTracker = t;
    }

    private RCTracker getRCTracker() {
        return (rcTracker);
    }

    private void setRCTracker(RCTracker t) {
        rcTracker = t;
    }

}

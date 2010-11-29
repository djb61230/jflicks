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
package org.jflicks.player.photoshow;

import java.util.Hashtable;

import org.jflicks.player.Player;
import org.jflicks.util.BaseActivator;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simple activater that starts the PhotoShow job.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private PhotoShow photoShow;
    private ServiceTracker eventServiceTracker;
    private ServiceTracker logServiceTracker;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        photoShow = new PhotoShow();

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(Player.TITLE_PROPERTY, photoShow.getTitle());
        dict.put(Player.HANDLE_PROPERTY, photoShow.getType());

        bc.registerService(Player.class.getName(), photoShow, dict);

        eventServiceTracker =
            new ServiceTracker(bc, EventAdmin.class.getName(), null);
        photoShow.setEventServiceTracker(eventServiceTracker);
        eventServiceTracker.open();

        logServiceTracker =
            new ServiceTracker(bc, LogService.class.getName(), null);
        photoShow.setLogServiceTracker(logServiceTracker);
        logServiceTracker.open();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        if (eventServiceTracker != null) {

            eventServiceTracker.close();
            eventServiceTracker = null;
        }

        if (logServiceTracker != null) {

            logServiceTracker.close();
            logServiceTracker = null;
        }
    }

}

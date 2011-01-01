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
package org.jflicks.player.vlcj;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import org.jflicks.player.Player;
import org.jflicks.util.BaseActivator;
import org.jflicks.util.Util;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simple activater that starts the vlcj service.  Also registers the Player
 * based upon vlcj.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ActivatorVideo extends BaseActivator {

    private ServiceTracker eventServiceTracker;
    private ServiceTracker logServiceTracker;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        Vlcj v = new Vlcj();
        v.setType(Vlcj.PLAYER_VIDEO);

        // Check for a properties file for vlc...
        File conf = new File("conf");
        if ((conf.exists()) && (conf.isDirectory())) {

            File props = new File(conf, "vlc.properties");
            System.out.println(props);
            if ((props.exists()) && (props.isFile())) {

                Properties p = Util.findProperties(props);
                if (p != null) {

                    ArrayList<String> l = new ArrayList<String>();
                    int count = Util.str2int(p.getProperty("argCount"), 0);
                    for (int i = 0; i < count; i++) {

                        String tmp = p.getProperty("arg" + i);
                        if (tmp != null) {

                            l.add(tmp.trim());
                        }
                    }

                    if (l.size() > 0) {

                        v.setArgs(l.toArray(new String[l.size()]));
                    }
                }
            }
        }

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(Player.TITLE_PROPERTY, v.getTitle());
        dict.put(Player.HANDLE_PROPERTY, v.getType());

        bc.registerService(Player.class.getName(), v, dict);

        eventServiceTracker =
            new ServiceTracker(bc, EventAdmin.class.getName(), null);
        v.setEventServiceTracker(eventServiceTracker);
        eventServiceTracker.open();

        logServiceTracker =
            new ServiceTracker(bc, LogService.class.getName(), null);
        v.setLogServiceTracker(logServiceTracker);
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

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
package org.jflicks.tv.ondemand.appletv;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import org.jflicks.configure.BaseConfiguration;
import org.jflicks.tv.ondemand.OnDemand;
import org.jflicks.util.BaseActivator;
import org.jflicks.util.Util;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simple activator for the AppleTV OnDemand.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private ArrayList<AppleTVOnDemand> appleTVOnDemandList =
        new ArrayList<AppleTVOnDemand>();;
    private ServiceTracker logServiceTracker;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        // We settle for at least one if there is no conf file.
        int count = 1;
        ArrayList<String> nameList = new ArrayList<String>();
        nameList.add("Apple TV");

        // Check for a properties file...
        File here = new File(".");
        File conf = new File(here, "conf");
        if ((conf.exists()) && (conf.isDirectory())) {

            File prop = new File(conf, "appleTV.ondemand.properties");
            if ((prop.exists() && (prop.isFile()))) {

                Properties p = Util.findProperties(prop);
                if (p != null) {

                    int pcount = Util.str2int(p.getProperty("count"), 0);
                    if (pcount > 0) {

                        count = pcount;
                        nameList.clear();
                        for (int i = 0; i < count; i++) {

                            String title = p.getProperty("title" + i);
                            if (title == null) {

                                title = "AppleTV";
                            }

                            nameList.add(title);
                        }
                    }
                }
            }
        }

        logServiceTracker =
            new ServiceTracker(bc, LogService.class.getName(), null);

        for (int i = 0; i < count; i++) {

            AppleTVOnDemand rod = new AppleTVOnDemand();
            rod.setTitle(nameList.get(i));
            BaseConfiguration c =
                (BaseConfiguration) rod.getDefaultConfiguration();
            if (c != null) {

                c.setSource(c.getSource() + " " + nameList.get(i));
            }

            appleTVOnDemandList.add(rod);

            Hashtable<String, String> dict = new Hashtable<String, String>();
            dict.put(OnDemand.TITLE_PROPERTY, rod.getTitle());

            System.out.println("registering ondemand");
            bc.registerService(OnDemand.class.getName(), rod, dict);

            rod.setLogServiceTracker(logServiceTracker);
        }

        logServiceTracker.open();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        if (logServiceTracker != null) {

            logServiceTracker.close();
            logServiceTracker = null;
        }
    }

}

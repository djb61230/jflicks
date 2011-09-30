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
package org.jflicks.transfer.system;

import java.io.File;
import java.util.Hashtable;
import java.util.Properties;

import org.jflicks.transfer.Transfer;
import org.jflicks.util.BaseActivator;
import org.jflicks.util.Util;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simple activator for the system video manager.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private SystemTransfer systemTransfer;
    private ServiceTracker logServiceTracker;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);
        SystemTransfer st = new SystemTransfer();
        setSystemTransfer(st);

        // Check for a properties file for transfer...
        File conf = new File("conf");
        if ((conf.exists()) && (conf.isDirectory())) {

            File props = new File(conf, "transfer.properties");
            if ((props.exists()) && (props.isFile())) {

                Properties p = Util.findProperties(props);
                if (p != null) {

                    String maxRate = p.getProperty("maxRate");
                    if (maxRate != null) {

                        st.setMaxRate(maxRate);
                    }
                }
            }
        }

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(Transfer.TITLE_PROPERTY, st.getTitle());

        bc.registerService(Transfer.class.getName(), st, dict);

        logServiceTracker =
            new ServiceTracker(bc, LogService.class.getName(), null);
        st.setLogServiceTracker(logServiceTracker);
        logServiceTracker.open();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        SystemTransfer st = getSystemTransfer();
        if (st != null) {
            st.close();
        }

        if (logServiceTracker != null) {

            logServiceTracker.close();
            logServiceTracker = null;
        }
    }

    private SystemTransfer getSystemTransfer() {
        return (systemTransfer);
    }

    private void setSystemTransfer(SystemTransfer st) {
        systemTransfer = st;
    }

}

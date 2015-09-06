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
package org.jflicks.tv.live.system;

import java.util.Hashtable;

import org.jflicks.tv.live.Live;
import org.jflicks.util.BaseActivator;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simple activator for the system live.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private SystemLive systemLive;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);
        SystemLive sl = new SystemLive();
        setSystemLive(sl);

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(Live.TITLE_PROPERTY, sl.getTitle());

        bc.registerService(Live.class.getName(), sl, dict);
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {
    }

    private SystemLive getSystemLive() {
        return (systemLive);
    }

    private void setSystemLive(SystemLive spp) {
        systemLive = spp;
    }

}

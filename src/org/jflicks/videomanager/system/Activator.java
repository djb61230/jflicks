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
package org.jflicks.videomanager.system;

import java.util.Hashtable;

import org.jflicks.db.Db4oServiceTracker;
import org.jflicks.videomanager.VideoManager;
import org.jflicks.util.BaseActivator;

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

    private Db4oServiceTracker db4oServiceTracker;
    private SystemVideoManager systemVideoManager;
    private ServiceTracker logServiceTracker;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);
        SystemVideoManager dpm = new SystemVideoManager();
        setSystemVideoManager(dpm);

        Db4oServiceTracker t = new Db4oServiceTracker(bc, dpm);
        setDb4oServiceTracker(t);
        t.open();

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(VideoManager.TITLE_PROPERTY, dpm.getTitle());

        bc.registerService(VideoManager.class.getName(), dpm, dict);

        logServiceTracker =
            new ServiceTracker(bc, LogService.class.getName(), null);
        dpm.setLogServiceTracker(logServiceTracker);
        logServiceTracker.open();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        SystemVideoManager vm = getSystemVideoManager();
        if (vm != null) {
            vm.close();
        }

        Db4oServiceTracker t = getDb4oServiceTracker();
        if (t != null) {
            t.close();
        }

        if (logServiceTracker != null) {

            logServiceTracker.close();
            logServiceTracker = null;
        }
    }

    private SystemVideoManager getSystemVideoManager() {
        return (systemVideoManager);
    }

    private void setSystemVideoManager(SystemVideoManager vm) {
        systemVideoManager = vm;
    }

    private Db4oServiceTracker getDb4oServiceTracker() {
        return (db4oServiceTracker);
    }

    private void setDb4oServiceTracker(Db4oServiceTracker t) {
        db4oServiceTracker = t;
    }

}

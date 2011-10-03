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
package org.jflicks.tv.postproc.worker.projectx;

import java.io.File;

import org.jflicks.job.JobContainer;
import org.jflicks.tv.postproc.worker.Worker;
import org.jflicks.util.BaseActivator;
import org.jflicks.util.Util;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import java.util.Hashtable;

/**
 * Simple activator for the projectx indexer worker.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private ServiceTracker logServiceTracker;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        logServiceTracker =
            new ServiceTracker(bc, LogService.class.getName(), null);
        logServiceTracker.open();

        ProjectxWorker w = new ProjectxWorker();
        w.setLogServiceTracker(logServiceTracker);

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(Worker.TITLE_PROPERTY, w.getTitle());

        bc.registerService(Worker.class.getName(), w, dict);
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        JobContainer jc = getJobContainer();
        if (jc != null) {
            jc.stop();
        }

        if (logServiceTracker != null) {

            logServiceTracker.close();
            logServiceTracker = null;
        }
    }

}

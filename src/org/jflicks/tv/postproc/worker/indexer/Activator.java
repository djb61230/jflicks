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
package org.jflicks.tv.postproc.worker.indexer;

import java.io.File;

import org.jflicks.job.JobContainer;
import org.jflicks.tv.postproc.worker.Worker;
import org.jflicks.util.BaseActivator;
import org.jflicks.util.Util;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import java.util.Hashtable;
import java.util.Properties;

/**
 * Simple activator for the indexer worker.
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

        IndexerWorker[] array = null;

        // Check for a properties file...
        File here = new File(".");
        File conf = new File(here, "conf");
        if ((conf.exists()) && (conf.isDirectory())) {

            File prop = new File(conf, "indexer.properties");
            if ((prop.exists() && (prop.isFile()))) {

                Properties p = Util.findProperties(prop);
                if (p != null) {

                    int count = Util.str2int(p.getProperty("count"), 0);
                    if (count > 0) {

                        array = new IndexerWorker[count];
                        for (int i = 0; i < array.length; i++) {

                            array[i] = new IndexerWorker();
                            array[i].setTitle(p.getProperty("title" + i));
                            array[i].setDescription(
                                p.getProperty("description" + i));
                            boolean old = array[i].isHeavy();
                            array[i].setHeavy(Util.str2boolean(
                                p.getProperty("heavy" + i), old));
                            array[i].setExtension(
                                p.getProperty("extension" + i));
                            array[i].setCommandLine(
                                p.getProperty("commandLine" + i));
                        }
                    }
                }
            }
        }

        if (array != null) {

            System.out.println("array.length: " + array.length);
            for (int i = 0; i < array.length; i++) {

                array[i].setLogServiceTracker(logServiceTracker);

                Hashtable<String, String> dict =
                    new Hashtable<String, String>();
                dict.put(Worker.TITLE_PROPERTY, array[i].getTitle());

                bc.registerService(Worker.class.getName(), array[i], dict);
            }
        }
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

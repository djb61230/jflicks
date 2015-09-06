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
package org.jflicks.tv.postproc.worker.comrat;

import java.util.Hashtable;
import java.util.Properties;

import org.jflicks.job.JobContainer;
import org.jflicks.tv.postproc.worker.Worker;
import org.jflicks.util.BaseActivator;
import org.jflicks.util.DetectRatingPlan;
import org.jflicks.util.Util;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simple activator for the comrat worker.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        // We want to register all the comrat properties files found
        // in conf/comrat.properties.
        Properties p = Util.findProperties("conf/comrat.properties");
        if (p != null) {

            ComratWorker cw = new ComratWorker();
            cw.setDefaultRun(Util.str2boolean(
                p.getProperty("defaultrun"), false));
            cw.setTitle(p.getProperty("title"));
            cw.setDescription(p.getProperty("description"));
            cw.setBackup(Util.str2int(p.getProperty("backup"), 0));
            cw.setSpan(Util.str2int(p.getProperty("span"), 5));
            cw.setVerbose(Util.str2boolean(p.getProperty("verbose"), false));

            int count = Util.str2int(p.getProperty("count"), 0);
            if (count > 0) {

                // Ok we have some plans...
                DetectRatingPlan[] plans = new DetectRatingPlan[count];
                for (int i = 0; i < count; i++) {

                    plans[i] = new DetectRatingPlan();
                    plans[i].setType(
                        Util.str2int(p.getProperty("type_" + i), 0));
                    plans[i].setValue(
                        Util.str2int(p.getProperty("value_" + i), 0));
                }

                cw.setDetectRatingPlans(plans);

                Hashtable<String, String> dict =
                    new Hashtable<String, String>();
                dict.put(Worker.TITLE_PROPERTY, cw.getTitle());

                bc.registerService(Worker.class.getName(), cw, dict);
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
    }

}

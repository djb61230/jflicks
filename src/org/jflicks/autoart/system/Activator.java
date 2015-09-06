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
package org.jflicks.autoart.system;

import java.util.Hashtable;

import org.jflicks.autoart.AutoArt;
import org.jflicks.db.Db4oServiceTracker;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.util.BaseActivator;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simple activator for the system video manager.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private Db4oServiceTracker db4oServiceTracker;
    private SystemAutoArt systemAutoArt;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);
        SystemAutoArt saa = new SystemAutoArt();
        setSystemAutoArt(saa);

        Db4oServiceTracker t = new Db4oServiceTracker(bc, saa);
        setDb4oServiceTracker(t);
        t.open();

        SystemAutoArtJob job = new SystemAutoArtJob(saa);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(AutoArt.TITLE_PROPERTY, saa.getTitle());

        bc.registerService(AutoArt.class.getName(), saa, dict);
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        JobContainer jc = getJobContainer();
        if (jc != null) {
            jc.stop();
        }

        Db4oServiceTracker t = getDb4oServiceTracker();
        if (t != null) {
            t.close();
        }

        SystemAutoArt aa = getSystemAutoArt();
        if (aa != null) {
            aa.close();
        }
    }

    private SystemAutoArt getSystemAutoArt() {
        return (systemAutoArt);
    }

    private void setSystemAutoArt(SystemAutoArt aa) {
        systemAutoArt = aa;
    }

    private Db4oServiceTracker getDb4oServiceTracker() {
        return (db4oServiceTracker);
    }

    private void setDb4oServiceTracker(Db4oServiceTracker t) {
        db4oServiceTracker = t;
    }

}

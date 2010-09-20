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
package org.jflicks.tv.scheduler.system;

import java.util.Hashtable;

import org.jflicks.db.Db4oServiceTracker;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.tv.scheduler.Scheduler;
import org.jflicks.util.BaseActivator;

import org.osgi.framework.BundleContext;

/**
 * Simple activator for the system scheduler.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private Db4oServiceTracker db4oServiceTracker;
    private SystemScheduler systemScheduler;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);
        SystemScheduler ss = new SystemScheduler();
        setSystemScheduler(ss);

        Db4oServiceTracker t = new Db4oServiceTracker(bc, ss);
        setDb4oServiceTracker(t);
        t.open();

        SystemSchedulerJob job = new SystemSchedulerJob(ss);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(Scheduler.TITLE_PROPERTY, ss.getTitle());

        bc.registerService(Scheduler.class.getName(), ss, dict);
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        SystemScheduler ss = getSystemScheduler();
        if (ss != null) {
            ss.close();
        }

        JobContainer jc = getJobContainer();
        if (jc != null) {
            jc.stop();
        }

        Db4oServiceTracker t = getDb4oServiceTracker();
        if (t != null) {
            t.close();
        }
    }

    private SystemScheduler getSystemScheduler() {
        return (systemScheduler);
    }

    private void setSystemScheduler(SystemScheduler ss) {
        systemScheduler = ss;
    }

    private Db4oServiceTracker getDb4oServiceTracker() {
        return (db4oServiceTracker);
    }

    private void setDb4oServiceTracker(Db4oServiceTracker t) {
        db4oServiceTracker = t;
    }

}

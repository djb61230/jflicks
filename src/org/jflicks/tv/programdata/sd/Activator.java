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
package org.jflicks.tv.programdata.sd;

import java.util.Hashtable;

import org.jflicks.db.Db4oServiceTracker;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.tv.programdata.ProgramData;
import org.jflicks.util.BaseActivator;

import org.osgi.framework.BundleContext;

/**
 * Simple activator for the Schedules Direct Program Data service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private Db4oServiceTracker db4oServiceTracker;
    private SchedulesDirectProgramData schedulesDirectProgramData;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        SchedulesDirectProgramData pd = new SchedulesDirectProgramData();
        setSchedulesDirectProgramData(pd);

        Db4oServiceTracker t = new Db4oServiceTracker(bc, pd);
        setDb4oServiceTracker(t);
        t.open();

        SchedulesDirectProgramDataJob job =
            new SchedulesDirectProgramDataJob(pd);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(ProgramData.TITLE_PROPERTY, pd.getTitle());

        bc.registerService(ProgramData.class.getName(), pd, dict);
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        SchedulesDirectProgramData pd = getSchedulesDirectProgramData();
        if (pd != null) {
            pd.close();
        }

        Db4oServiceTracker t = getDb4oServiceTracker();
        if (t != null) {
            t.close();
        }

        JobContainer jc = getJobContainer();
        if (jc != null) {
            jc.stop();
        }
    }

    private SchedulesDirectProgramData getSchedulesDirectProgramData() {
        return (schedulesDirectProgramData);
    }

    private void setSchedulesDirectProgramData(SchedulesDirectProgramData pd) {
        schedulesDirectProgramData = pd;
    }

    private Db4oServiceTracker getDb4oServiceTracker() {
        return (db4oServiceTracker);
    }

    private void setDb4oServiceTracker(Db4oServiceTracker t) {
        db4oServiceTracker = t;
    }

}

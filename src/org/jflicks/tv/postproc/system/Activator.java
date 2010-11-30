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
package org.jflicks.tv.postproc.system;

import java.util.Hashtable;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.tv.postproc.PostProc;
import org.jflicks.util.BaseActivator;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simple activator for the system postproc.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private SystemPostProc systemPostProc;
    private JobContainer lightJobContainer;
    private WorkerTracker workerTracker;
    private ServiceTracker logServiceTracker;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);
        SystemPostProc spp = new SystemPostProc();
        setSystemPostProc(spp);

        WorkerTracker t = new WorkerTracker(bc, spp);
        setWorkerTracker(t);
        t.open();

        SystemPostProcHeavyJob job = new SystemPostProcHeavyJob(spp);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();

        SystemPostProcLightJob ljob = new SystemPostProcLightJob(spp);
        JobContainer ljc = JobManager.getJobContainer(ljob);
        setLightJobContainer(ljc);
        ljc.start();

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(PostProc.TITLE_PROPERTY, spp.getTitle());

        bc.registerService(PostProc.class.getName(), spp, dict);

        logServiceTracker =
            new ServiceTracker(bc, LogService.class.getName(), null);
        spp.setLogServiceTracker(logServiceTracker);
        logServiceTracker.open();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        WorkerTracker t = getWorkerTracker();
        if (t != null) {
            t.close();
        }

        JobContainer jc = getJobContainer();
        if (jc != null) {
            jc.stop();
        }

        jc = getLightJobContainer();
        if (jc != null) {
            jc.stop();
        }

        if (logServiceTracker != null) {

            logServiceTracker.close();
            logServiceTracker = null;
        }
    }

    private SystemPostProc getSystemPostProc() {
        return (systemPostProc);
    }

    private void setSystemPostProc(SystemPostProc spp) {
        systemPostProc = spp;
    }

    private JobContainer getLightJobContainer() {
        return (lightJobContainer);
    }

    private void setLightJobContainer(JobContainer jc) {
        lightJobContainer = jc;
    }

    private WorkerTracker getWorkerTracker() {
        return (workerTracker);
    }

    private void setWorkerTracker(WorkerTracker t) {
        workerTracker = t;
    }

}

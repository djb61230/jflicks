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
    along with JFLICKS.  If not, see <remote://www.gnu.org/licenses/>.
*/
package org.jflicks.nms.remote;

import org.jflicks.discovery.ServiceBrowserJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.util.BaseActivator;

import org.osgi.framework.BundleContext;

/**
 * Simple activater that uses the remote nms service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private RemoteTracker remoteTracker;
    private JobContainer remoteJobContainer;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        RemoteTracker tracker = new RemoteTracker(bc);
        setRemoteTracker(tracker);
        tracker.open();

        RemoteNMSJob rnms = new RemoteNMSJob(tracker);
        JobContainer rjc = JobManager.getJobContainer(rnms);
        setRemoteJobContainer(rjc);
        rjc.start();

        ServiceBrowserJob job = new ServiceBrowserJob("nms");
        job.addDiscoverListener(rnms);

        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        RemoteTracker tracker = getRemoteTracker();
        if (tracker != null) {
            tracker.close();
        }

        JobContainer jc = getRemoteJobContainer();
        if (jc != null) {

            jc.stop();
        }

        jc = getJobContainer();
        if (jc != null) {

            jc.stop();
        }
    }

    private JobContainer getRemoteJobContainer() {
        return (remoteJobContainer);
    }

    private void setRemoteJobContainer(JobContainer jc) {
        remoteJobContainer = jc;
    }

    private RemoteTracker getRemoteTracker() {
        return (remoteTracker);
    }

    private void setRemoteTracker(RemoteTracker t) {
        remoteTracker = t;
    }

}

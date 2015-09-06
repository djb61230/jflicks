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
package org.jflicks.nms.remote;

import java.util.ArrayList;

import org.jflicks.discovery.DiscoverEvent;
import org.jflicks.discovery.DiscoverListener;
import org.jflicks.discovery.ServiceDescription;
import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobManager;
import org.jflicks.util.LogUtil;

/**
 * This job will run and listen for remote discovery acknowledgements.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RemoteNMSJob extends AbstractJob implements DiscoverListener {

    private RemoteTracker remoteTracker;
    private ArrayList<ServiceDescription> serviceDescriptionList;

    /**
     * Constructor taking a RemoteTracker argument.
     *
     * @param t A RemoteTracker instance.
     */
    public RemoteNMSJob(RemoteTracker t) {

        setRemoteTracker(t);
        setServiceDescriptionList(new ArrayList<ServiceDescription>());
    }

    private RemoteTracker getRemoteTracker() {
        return (remoteTracker);
    }

    private void setRemoteTracker(RemoteTracker t) {
        remoteTracker = t;
    }

    private ArrayList<ServiceDescription> getServiceDescriptionList() {
        return (serviceDescriptionList);
    }

    private void setServiceDescriptionList(ArrayList<ServiceDescription> l) {
        serviceDescriptionList = l;
    }

    private void add(ServiceDescription sd) {

        ArrayList<ServiceDescription> l = getServiceDescriptionList();
        if ((l != null) && (sd != null)) {

            synchronized (l) {

                l.add(sd);
            }
        }
    }

    private void remove(ServiceDescription sd) {

        ArrayList<ServiceDescription> l = getServiceDescriptionList();
        if ((l != null) && (sd != null)) {

            synchronized (l) {

                l.remove(sd);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setTerminate(false);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        while (!isTerminate()) {

            ArrayList<ServiceDescription> list = getServiceDescriptionList();
            RemoteTracker tracker = getRemoteTracker();
            if ((list != null) && (list.size() > 0) && (tracker != null)) {

                if (tracker.hasRemoteOSGiService()) {

                    ServiceDescription[] array =
                        list.toArray(new ServiceDescription[list.size()]);
                    for (int i = 0; i < array.length; i++) {

                        tracker.connect(array[i].getAddressAsString(),
                            array[i].getPort());
                        remove(array[i]);
                    }
                }
            }

            JobManager.sleep(getSleepTime());
        }

    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
    }

    /**
     * {@inheritDoc}
     */
    public void serviceReply(DiscoverEvent event) {

        LogUtil.log(LogUtil.INFO, "We received a DiscoverEvent event");
        add(event.getServiceDescription());
    }

}

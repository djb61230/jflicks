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
package org.jflicks.tv.recorder.hdhr;

import java.util.ArrayList;
import java.util.Hashtable;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.tv.recorder.Recorder;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This job supports the HDHR recorder.  This job will discover the
 * current HDHR devices on the network.  Then will configure a HDHRRecorder
 * instance for each HDHR device found.  It will set the HDHRRecorder Device
 * property to ID-N where ID is the ID of the HDHR and N is the tuner number.
 *
 * As this job runs it will add and delete HDHRRecorder services and the
 * real devices come and go.  Please note just because they are found and
 * a HDHRRecorder service started, it would need further configuration to
 * use properly.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class HDHRDiscoveryJob extends AbstractJob implements JobListener {

    private ArrayList<HDHRRecorder> hdhrRecorderList;
    private BundleContext bundleContext;
    private DiscoverJob discoverJob;
    private JobContainer jobContainer;
    private ServiceTracker logServiceTracker;

    /**
     * This job supports the HDHRRecorder plugin.
     *
     * @param bc Need a bundle context to register, unregister HDHR devices
     * as they "come and go" from the network.
     * @param log A Tracker for the LogService.
     */
    public HDHRDiscoveryJob(BundleContext bc, ServiceTracker log) {

        setHDHRRecorderList(new ArrayList<HDHRRecorder>());
        setBundleContext(bc);
        setLogServiceTracker(log);
    }

    private ServiceTracker getLogServiceTracker() {
        return (logServiceTracker);
    }

    private void setLogServiceTracker(ServiceTracker st) {
        logServiceTracker = st;
    }

    private BundleContext getBundleContext() {
        return (bundleContext);
    }

    private void setBundleContext(BundleContext bc) {
        bundleContext = bc;
    }

    private DiscoverJob getDiscoverJob() {
        return (discoverJob);
    }

    private void setDiscoverJob(DiscoverJob j) {
        discoverJob = j;
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer jc) {
        jobContainer = jc;
    }

    private ArrayList<HDHRRecorder> getHDHRRecorderList() {
        return (hdhrRecorderList);
    }

    private void setHDHRRecorderList(ArrayList<HDHRRecorder> l) {
        hdhrRecorderList = l;
    }

    private void addHDHRRecorder(HDHRRecorder r) {

        ArrayList<HDHRRecorder> l = getHDHRRecorderList();
        if ((l != null) && (r != null)) {
            l.add(r);
        }
    }

    private void removeHDHRRecorder(HDHRRecorder r) {

        ArrayList<HDHRRecorder> l = getHDHRRecorderList();
        if ((l != null) && (r != null)) {
            l.remove(r);
        }
    }

    private boolean contains(String id, int tuner) {

        boolean result = false;

        ArrayList<HDHRRecorder> l = getHDHRRecorderList();
        if ((id != null) && (l != null)) {

            String tmp = id + "-" + tuner;
            for (int i = 0; i < l.size(); i++) {

                HDHRRecorder r = l.get(i);
                if (r != null) {

                    result = tmp.equals(r.getDevice());
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setTerminate(false);

        DiscoverJob job = new DiscoverJob();
        setDiscoverJob(job);
        job.addJobListener(this);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        while (!isTerminate()) {

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
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            DiscoverJob job = getDiscoverJob();
            if (job != null) {

                String[] array = job.getIds();
                String[] iparray = job.getIps();
                String[] modelarray = job.getModels();
                BundleContext bc = getBundleContext();
                if ((array != null) && (iparray != null) && (modelarray != null) && (bc != null)) {

                    // We will assume dual tuners for right now, but this
                    // needs to be fixed to handle single tuners.
                    for (int i = 0; i < array.length; i++) {

                        if (!contains(array[i], 0)) {
                            registerRecorder(bc, array[i], iparray[i], modelarray[i], 0);
                        }
                        if (!contains(array[i], 1)) {
                            registerRecorder(bc, array[i], iparray[i], modelarray[i], 1);
                        }
                    }
                }
            }
        }
    }

    private void registerRecorder(BundleContext bc, String id, String ip, String model,
        int tuner) {

        if ((bc != null) && (id != null)) {

            System.out.println("register id <" + id + "> tuner <" + tuner + "> ip <" + ip + "> model <" + model + ">");
            HDHRRecorder r = new HDHRRecorder();
            r.setDevice(id + "-" + tuner);
            r.setIpAddress(ip);
            r.setModel(model);
            r.updateDefault();
            r.setLogServiceTracker(getLogServiceTracker());
            addHDHRRecorder(r);

            Hashtable<String, String> dict = new Hashtable<String, String>();
            dict.put(Recorder.TITLE_PROPERTY, r.getTitle());
            bc.registerService(Recorder.class.getName(), r, dict);
        }
    }

}

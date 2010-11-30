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
package org.jflicks.tv.recorder.v4l2;

import java.io.File;
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
 * This job supports the V4l2 recorders.  This job will discover the
 * current v4l2 devices on the computer.  Then will configure a V4l2Recorder
 * instance for each v4l2 device found.  It will set the V4l2Recorder Device
 * property to /dev/videoN where N is the tuner number.
 *
 * As this job runs it will add and delete V4l2Recorder services and the
 * real devices come and go.  Please note just because they are found and
 * a V4l2Recorder service started, it would need further configuration to
 * use properly.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class V4l2DiscoveryJob extends AbstractJob implements JobListener {

    private ArrayList<V4l2Recorder> v4l2RecorderList;
    private BundleContext bundleContext;
    private DiscoverJob discoverJob;
    private JobContainer jobContainer;
    private ServiceTracker logServiceTracker;

    /**
     * This job supports the V4l2Recorder plugin.
     *
     * @param bc Need a bundle context to register, unregister V4l2 devices
     * as they "come and go" from the computer.
     * @param log A log tracker we can pass to instantiated recorders.
     */
    public V4l2DiscoveryJob(BundleContext bc, ServiceTracker log) {

        setV4l2RecorderList(new ArrayList<V4l2Recorder>());
        setBundleContext(bc);
        setLogServiceTracker(log);
    }

    private BundleContext getBundleContext() {
        return (bundleContext);
    }

    private void setBundleContext(BundleContext bc) {
        bundleContext = bc;
    }

    private ServiceTracker getLogServiceTracker() {
        return (logServiceTracker);
    }

    private void setLogServiceTracker(ServiceTracker st) {
        logServiceTracker = st;
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

    private ArrayList<V4l2Recorder> getV4l2RecorderList() {
        return (v4l2RecorderList);
    }

    private void setV4l2RecorderList(ArrayList<V4l2Recorder> l) {
        v4l2RecorderList = l;
    }

    private void addV4l2Recorder(V4l2Recorder r) {

        ArrayList<V4l2Recorder> l = getV4l2RecorderList();
        if ((l != null) && (r != null)) {
            l.add(r);
        }
    }

    private void removeV4l2Recorder(V4l2Recorder r) {

        ArrayList<V4l2Recorder> l = getV4l2RecorderList();
        if ((l != null) && (r != null)) {
            l.remove(r);
        }
    }

    private boolean contains(V4l2Device d) {

        boolean result = false;

        ArrayList<V4l2Recorder> l = getV4l2RecorderList();
        if ((d != null) && (l != null)) {

            for (int i = 0; i < l.size(); i++) {

                V4l2Recorder r = l.get(i);
                String path = d.getPath();
                if ((r != null) && (path != null)) {

                    result = path.equals(r.getDevice());
                    if (result) {
                        break;
                    }
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

            if (event.getSource() == getDiscoverJob()) {

                DiscoverJob job = getDiscoverJob();

                V4l2Device[] array = job.getV4l2Devices();
                BundleContext bc = getBundleContext();
                if ((array != null) && (bc != null)) {

                    for (int i = 0; i < array.length; i++) {

                        // Only add if it's new...
                        if (!contains(array[i])) {
                            registerRecorder(bc, array[i]);
                        }
                    }
                }

            } else {

                if (event.getSource() instanceof CreatePropertiesJob) {

                    CreatePropertiesJob cpj =
                        (CreatePropertiesJob) event.getSource();
                    V4l2Recorder r = cpj.getV4l2Recorder();

                    BundleContext bc = getBundleContext();
                    if ((r != null) && (bc != null)) {

                        // We have a CreatePropertiesJob that completed so
                        // we can now add it as a recorder.
                        r.updateDefault();
                        addV4l2Recorder(r);

                        Hashtable<String, String> dict =
                            new Hashtable<String, String>();
                        dict.put(Recorder.TITLE_PROPERTY, r.getTitle());
                        bc.registerService(Recorder.class.getName(), r, dict);
                    }
                }
            }
        }
    }

    private void registerRecorder(BundleContext bc, V4l2Device d) {

        System.out.println("registerRecorder: " + bc + " " + d);
        if ((bc != null) && (d != null)) {

            V4l2Recorder r = new V4l2Recorder();
            r.setDevice(d.getPath());
            r.setCardType(d.getCardType());
            r.setLogServiceTracker(getLogServiceTracker());

            // First we need to ensure a default properties file exists
            // for this device.  If it is missing we can generate it.
            String pname = r.getPropertiesName();
            System.out.println("registerRecorder: pname <" + pname + ">");
            if (pname != null) {

                File pfile = new File(pname);
                if (pfile.exists()) {

                    // We have a default config so we just add the recorder.
                    r.updateDefault();
                    addV4l2Recorder(r);

                    Hashtable<String, String> dict =
                        new Hashtable<String, String>();
                    dict.put(Recorder.TITLE_PROPERTY, r.getTitle());
                    bc.registerService(Recorder.class.getName(), r, dict);
                    System.out.println("registerRecorder: registered in osgi");

                } else {

                    // We need to create a default properties so there is
                    // something there for the first time it is discovered.
                    CreatePropertiesJob cpj = new CreatePropertiesJob(r);
                    cpj.addJobListener(this);
                    JobContainer jc = JobManager.getJobContainer(cpj);
                    jc.start();
                }
            }
        }
    }

}

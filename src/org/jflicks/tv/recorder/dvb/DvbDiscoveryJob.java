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
package org.jflicks.tv.recorder.dvb;

import java.util.ArrayList;
import java.util.Hashtable;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.tv.recorder.Recorder;
import org.jflicks.util.LogUtil;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This job supports the DVB recorders.  This job will discover the
 * current DVB devices on the computer.  Then will configure a DvbRecorder
 * instance for each DVB device found.
 *
 * As this job runs it will add and delete DvbRecorder services and the
 * real devices come and go.  Please note just because they are found and
 * a DvbRecorder service started, it would need further configuration to
 * use properly.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DvbDiscoveryJob extends AbstractJob implements JobListener {

    private ArrayList<DvbRecorder> dvbRecorderList;
    private BundleContext bundleContext;
    private DiscoverJob discoverJob;
    private JobContainer jobContainer;

    /**
     * This job supports the DvbRecorder plugin.
     *
     * @param bc Need a bundle context to register, unregister Dvb devices
     * as they "come and go" from the computer.
     */
    public DvbDiscoveryJob(BundleContext bc) {

        setDvbRecorderList(new ArrayList<DvbRecorder>());
        setBundleContext(bc);
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

    private ArrayList<DvbRecorder> getDvbRecorderList() {
        return (dvbRecorderList);
    }

    private void setDvbRecorderList(ArrayList<DvbRecorder> l) {
        dvbRecorderList = l;
    }

    private void addDvbRecorder(DvbRecorder r) {

        ArrayList<DvbRecorder> l = getDvbRecorderList();
        if ((l != null) && (r != null)) {
            l.add(r);
        }
    }

    private void removeDvbRecorder(DvbRecorder r) {

        ArrayList<DvbRecorder> l = getDvbRecorderList();
        if ((l != null) && (r != null)) {
            l.remove(r);
        }
    }

    private boolean contains(DvbDevice d) {

        boolean result = false;

        ArrayList<DvbRecorder> l = getDvbRecorderList();
        if ((d != null) && (l != null)) {

            for (int i = 0; i < l.size(); i++) {

                DvbRecorder r = l.get(i);
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

                DvbDevice[] array = job.getDvbDevices();
                BundleContext bc = getBundleContext();
                if ((array != null) && (bc != null)) {

                    for (int i = 0; i < array.length; i++) {

                        // Only add if it's new...
                        if (!contains(array[i])) {
                            registerRecorder(bc, array[i]);
                        }
                    }
                }
            }
        }
    }

    private void registerRecorder(BundleContext bc, DvbDevice d) {

        if ((bc != null) && (d != null)) {

            DvbRecorder r = new DvbRecorder();
            r.setDevice(d.getPath());
            r.updateDefault();

            addDvbRecorder(r);
            Hashtable<String, String> dict = new Hashtable<String, String>();
            dict.put(Recorder.TITLE_PROPERTY, r.getTitle());
            bc.registerService(Recorder.class.getName(), r, dict);
            LogUtil.log(LogUtil.INFO, "registerRecorder: registered in osgi");
        }
    }

}

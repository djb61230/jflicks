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

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobManager;

/**
 * This job finds the Video4Linux2 recorders on the local machine.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DiscoverJob extends BaseV4l2Job {

    private ArrayList<V4l2Device> v4l2DeviceList;
    private String[] devicePaths;
    private int pathIndex;

    /**
     * Simple no argument constructor.
     */
    public DiscoverJob() {

        setV4l2DeviceList(new ArrayList<V4l2Device>());
    }

    private String[] getDevicePaths() {
        return (devicePaths);
    }

    private void setDevicePaths(String[] array) {
        devicePaths = array;
    }

    private int getPathIndex() {
        return (pathIndex);
    }

    private void setPathIndex(int i) {
        pathIndex = i;
    }

    private String getNextDevicePath() {

        String result = null;

        String[] array = getDevicePaths();
        if (array != null) {

            int i = getPathIndex();
            if ((i >= 0) && (i < array.length)) {

                result = array[i];
                setPathIndex(i + 1);
            }
        }

        return (result);
    }

    private ArrayList<V4l2Device> getV4l2DeviceList() {
        return (v4l2DeviceList);
    }

    private void setV4l2DeviceList(ArrayList<V4l2Device> l) {
        v4l2DeviceList = l;
    }

    private void addV4l2Device(V4l2Device s) {

        ArrayList<V4l2Device> l = getV4l2DeviceList();
        if ((l != null) && (s != null)) {
            l.add(s);
        }
    }

    private void removeV4l2Device(V4l2Device s) {

        ArrayList<V4l2Device> l = getV4l2DeviceList();
        if ((l != null) && (s != null)) {
            l.remove(s);
        }
    }

    private void clearDeviceList() {

        ArrayList<V4l2Device> l = getV4l2DeviceList();
        if (l != null) {
            l.clear();
        }
    }

    /**
     * The array of v4l2 devices found on the computer.  There will be one
     * device for each v4l2 item found.
     *
     * @return An array of String instances representing v4l2 devices.
     */
    public V4l2Device[] getV4l2Devices() {

        V4l2Device[] result = null;

        ArrayList<V4l2Device> l = getV4l2DeviceList();
        if (l != null) {

            result = l.toArray(new V4l2Device[l.size()]);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        clearDeviceList();
        File dev = new File("/dev");
        String[] listing = dev.list(new DevVideoFilter());
        if ((listing != null) && (listing.length > 0)) {

            setTerminate(false);
            setPathIndex(0);
            setDevicePaths(listing);
            InfoJob ij = new InfoJob();
            ij.setDevice("/dev/" + getNextDevicePath());
            ij.addJobListener(this);
            JobContainer jc = JobManager.getJobContainer(ij);
            jc.start();

        } else {

            setTerminate(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());
        }

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();
            setJobContainer(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            InfoJob job = (InfoJob) event.getSource();
            addV4l2Device(job.getV4l2Device());

            String path = getNextDevicePath();
            if (path != null) {

                // We have another to do...
                job.setDevice("/dev/" + path);
                JobContainer jc = JobManager.getJobContainer(job);
                jc.start();

            } else {

                // We are done with them all....
                stop();
            }
        }
    }

}

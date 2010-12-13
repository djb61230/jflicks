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

import java.io.File;
import java.util.ArrayList;

import org.jflicks.job.JobEvent;
import org.jflicks.tv.recorder.BaseDeviceJob;

/**
 * This job finds the Video4Linux2 recorders on the local machine.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DiscoverJob extends BaseDeviceJob {

    private ArrayList<DvbDevice> dvbDeviceList;
    private String[] devicePaths;
    private int pathIndex;

    /**
     * Simple no argument constructor.
     */
    public DiscoverJob() {

        setDvbDeviceList(new ArrayList<DvbDevice>());
    }

    private ArrayList<DvbDevice> getDvbDeviceList() {
        return (dvbDeviceList);
    }

    private void setDvbDeviceList(ArrayList<DvbDevice> l) {
        dvbDeviceList = l;
    }

    private void addDvbDevice(DvbDevice s) {

        ArrayList<DvbDevice> l = getDvbDeviceList();
        if ((l != null) && (s != null)) {
            l.add(s);
        }
    }

    private void removeDvbDevice(DvbDevice d) {

        ArrayList<DvbDevice> l = getDvbDeviceList();
        if ((l != null) && (d != null)) {
            l.remove(d);
        }
    }

    private void clearDeviceList() {

        ArrayList<DvbDevice> l = getDvbDeviceList();
        if (l != null) {
            l.clear();
        }
    }

    /**
     * The array of DVB devices found on the computer.  There will be one
     * device for each DVB item found.
     *
     * @return An array of String instances representing DVB devices.
     */
    public DvbDevice[] getDvbDevices() {

        DvbDevice[] result = null;

        ArrayList<DvbDevice> l = getDvbDeviceList();
        if (l != null) {

            result = l.toArray(new DvbDevice[l.size()]);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        clearDeviceList();
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        File dev = new File("/dev");
        File devdvb = new File(dev, "dvb");
        File[] listing = devdvb.listFiles(new DevDvbAdapterFilter());
        if ((listing != null) && (listing.length > 0)) {

            // Now for each adapter, check for DVR nodes.
            for (int i = 0; i < listing.length; i++) {

                File[] dvrs =
                    listing[i].listFiles(new DevDvbAdapterDvrFilter());
                if ((dvrs != null) && (dvrs.length > 0)) {

                    for (int j = 0; j < dvrs.length; j++) {

                        DvbDevice tmp = new DvbDevice();
                        tmp.setPath(dvrs[j].getPath());
                        addDvbDevice(tmp);
                    }
                }
            }
        }

        fireJobEvent(JobEvent.COMPLETE);
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
    }

}

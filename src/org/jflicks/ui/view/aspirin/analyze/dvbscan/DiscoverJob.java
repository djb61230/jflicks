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
package org.jflicks.ui.view.aspirin.analyze.dvbscan;

import java.io.File;
import java.util.ArrayList;

import org.jflicks.job.JobEvent;
import org.jflicks.tv.recorder.BaseDeviceJob;
import org.jflicks.util.DevDvbAdapterFilter;
import org.jflicks.util.DevDvbAdapterDvrFilter;

/**
 * This job finds the Video4Linux2 recorders on the local machine.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DiscoverJob extends BaseDeviceJob {

    private ArrayList<DvbPath> dvbPathList;

    /**
     * Simple no argument constructor.
     */
    public DiscoverJob() {

        setDvbPathList(new ArrayList<DvbPath>());
    }

    private ArrayList<DvbPath> getDvbPathList() {
        return (dvbPathList);
    }

    private void setDvbPathList(ArrayList<DvbPath> l) {
        dvbPathList = l;
    }

    private void addDvbPath(DvbPath s) {

        ArrayList<DvbPath> l = getDvbPathList();
        if ((l != null) && (s != null)) {
            l.add(s);
        }
    }

    private void removeDvbPath(DvbPath d) {

        ArrayList<DvbPath> l = getDvbPathList();
        if ((l != null) && (d != null)) {
            l.remove(d);
        }
    }

    private void clearDeviceList() {

        ArrayList<DvbPath> l = getDvbPathList();
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
    public DvbPath[] getDvbPaths() {

        DvbPath[] result = null;

        ArrayList<DvbPath> l = getDvbPathList();
        if (l != null) {

            result = l.toArray(new DvbPath[l.size()]);
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

                        DvbPath tmp = new DvbPath();
                        tmp.setPath(dvrs[j].getPath());
                        addDvbPath(tmp);
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

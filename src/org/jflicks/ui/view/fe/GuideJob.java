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
package org.jflicks.ui.view.fe;

import java.util.HashMap;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;
import org.jflicks.nms.NMS;
import org.jflicks.tv.Channel;
import org.jflicks.tv.ShowAiring;

/**
 * A job that gets ShowAiring instances from a NMS for an array of
 * Channel instances.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class GuideJob extends AbstractJob {

    private NMS nms;
    private Channel[] channels;

    /**
     * Constructor with our required argument.
     *
     * @param nms A NMS to access.
     * @param array A Channel to find ShowAirings for.
     */
    public GuideJob(NMS nms, Channel[] array) {

        setNMS(nms);
        setChannels(array);
    }

    private NMS getNMS() {
        return (nms);
    }

    private void setNMS(NMS n) {
        nms = n;
    }

    private Channel[] getChannels() {
        return (channels);
    }

    private void setChannels(Channel[] array) {
        channels = array;
    }

    /**
     * @inheritDoc
     */
    public void start() {
        setTerminate(false);
    }

    /**
     * @inheritDoc
     */
    public void run() {

        HashMap<Channel, ShowAiring[]> hm = null;

        NMS n = getNMS();
        Channel[] array = getChannels();
        if ((n != null) && (array != null) && (array.length > 0)) {

            hm = new HashMap<Channel, ShowAiring[]>();
            for (int i = 0; i < array.length; i++) {

                System.out.println("getting guide for: " + array[i]);
                ShowAiring[] data = nms.getShowAiringsByChannel(array[i]);
                if (data != null) {

                    hm.put(array[i], data);
                }
            }
        }

        fireJobEvent(JobEvent.COMPLETE, hm);
    }

    /**
     * @inheritDoc
     */
    public void stop() {
        setTerminate(true);
    }

}

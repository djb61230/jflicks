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
public class AllGuideJob extends AbstractJob {

    private NMS[] nms;

    /**
     * Constructor with our required argument.
     *
     * @param nms A NMS to access.
     */
    public AllGuideJob(NMS[] nms) {

        setNMS(nms);
    }

    private NMS[] getNMS() {
        return (nms);
    }

    private void setNMS(NMS[] array) {
        nms = array;
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

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0)) {

            hm = new HashMap<Channel, ShowAiring[]>();
            for (int i = 0; i < array.length; i++) {

                Channel[] chans = array[i].getRecordableChannels();
                if (chans != null) {

                    for (int j = 0; j < chans.length; j++) {

                        ShowAiring[] data = array[i].getShowAiringsByChannel(chans[j]);
                        if (data != null) {

                            hm.put(chans[j], data);
                        }
                    }
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

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
package org.jflicks.ui.view.scheduler;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;
import org.jflicks.nms.NMS;
import org.jflicks.tv.Channel;
import org.jflicks.tv.ShowAiring;

/**
 * A job that gets ShowAiring instances from a NMS.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ShowAiringJob extends AbstractJob {

    private NMS nms;
    private Channel channel;

    /**
     * Constructor with our required argument.
     *
     * @param nms A NMS to access.
     * @param c A Channel to find ShowAirings for.
     */
    public ShowAiringJob(NMS nms, Channel c) {

        setNMS(nms);
        setChannel(c);
    }

    private NMS getNMS() {
        return (nms);
    }

    private void setNMS(NMS n) {
        nms = n;
    }

    private Channel getChannel() {
        return (channel);
    }

    private void setChannel(Channel c) {
        channel = c;
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

        ShowAiring[] array = null;

        NMS n = getNMS();
        Channel c = getChannel();
        if ((n != null) && (c != null)) {

            array = nms.getShowAiringsByChannel(c);
        }

        fireJobEvent(JobEvent.COMPLETE, array);
    }

    /**
     * @inheritDoc
     */
    public void stop() {
        setTerminate(true);
    }

}

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
package org.jflicks.ui.view.j4cc.scheduler;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;
import org.jflicks.nms.NMS;
import org.jflicks.tv.Recording;

/**
 * A job that gets Recording instances from a NMS.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordingJob extends AbstractJob {

    private NMS nms;

    /**
     * Constructor with our required argument.
     *
     * @param nms A NMS to access.
     */
    public RecordingJob(NMS nms) {

        setNMS(nms);
    }

    private NMS getNMS() {
        return (nms);
    }

    private void setNMS(NMS n) {
        nms = n;
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

        Recording[] array = null;

        NMS n = getNMS();
        if (n != null) {

            array = nms.getRecordings();
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

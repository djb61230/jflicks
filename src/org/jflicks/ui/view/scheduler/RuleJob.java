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
import org.jflicks.tv.RecordingRule;

/**
 * A job that gets ShowAiring instances from a NMS.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RuleJob extends AbstractJob {

    private NMS nms;

    /**
     * Constructor with our required argument.
     *
     * @param nms A NMS to access.
     */
    public RuleJob(NMS nms) {

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

        RecordingRule[] array = null;

        NMS n = getNMS();
        if (n != null) {

            array = nms.getRecordingRules();
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

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
package org.jflicks.nms.system;

import java.util.ArrayList;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobManager;
import org.jflicks.tv.Recording;
import org.jflicks.tv.recorder.Recorder;

/**
 * A job that deletes recording files but only if no recording is
 * currently happening.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RemoveRecordingJob extends AbstractJob {

    private SystemNMS systemNMS;

    /**
     * Constructor with our required argument.
     *
     * @param nms A NMS to access.
     */
    public RemoveRecordingJob(SystemNMS nms) {

        setSystemNMS(nms);

        // We only need to check every 10 minutes or so...
        setSleepTime(120000);
    }

    private SystemNMS getSystemNMS() {
        return (systemNMS);
    }

    private void setSystemNMS(SystemNMS n) {
        systemNMS = n;
    }

    /**
     * @inheritDoc
     */
    public void start() {
        setTerminate(false);
    }

    private void check(boolean force) {

        SystemNMS n = getSystemNMS();
        if (n != null) {

            boolean doit = force;
            if (!force) {

                doit = true;
                Recorder[] array = n.getRecorders();
                if (array != null) {

                    for (int i = 0; i < array.length; i++) {

                        if (array[i].isRecording()) {

                            doit = false;
                            break;
                        }
                    }
                }
            }

            if (doit) {

                // We can delete all pending right now...
                ArrayList<Recording> l = n.getRemoveRecordingList();
                if ((l != null) && (l.size() > 0)) {

                    Recording[] recs = null;
                    synchronized (l) {

                        recs = l.toArray(new Recording[l.size()]);
                        l.clear();
                    }

                    if (recs != null) {

                        for (int i = 0; i < recs.length; i++) {

                            n.performRemoval(recs[i]);
                        }
                    }
                }
            }
        }

    }

    /**
     * @inheritDoc
     */
    public void run() {

        while (!isTerminate()) {

             JobManager.sleep(getSleepTime());
             check(false);
        }

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * @inheritDoc
     */
    public void stop() {

        check(true);
        setTerminate(true);
    }

}

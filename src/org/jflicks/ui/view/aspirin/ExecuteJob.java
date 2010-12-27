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
package org.jflicks.ui.view.aspirin;

import java.util.ArrayList;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;
import org.jflicks.ui.view.aspirin.analyze.Analyze;
import org.jflicks.ui.view.aspirin.analyze.Finding;

/**
 * A job that executes a set of Analyze instances.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ExecuteJob extends AbstractJob {

    private Analyze[] analyzes;

    /**
     * Constructor with our required argument.
     *
     * @param array An array of Analyze instances to execute.
     */
    public ExecuteJob(Analyze[] array) {

        setAnalyzes(array);
    }

    private Analyze[] getAnalyzes() {
        return (analyzes);
    }

    private void setAnalyzes(Analyze[] array) {
        analyzes = array;
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

        ArrayList<Finding> list = new ArrayList<Finding>();

        int fcount = 0;
        Analyze[] array = getAnalyzes();
        if (array != null) {

            fireJobEvent(JobEvent.UPDATE, "\nRunning " + array.length
                + " tasks.");
            for (int i = 0; i < array.length; i++) {

                fireJobEvent(JobEvent.UPDATE, "\nCalling \""
                    + array[i].getShortDescription() + "\"");
                Finding f = array[i].analyze();
                if (f != null) {

                    if (!f.isPassed()) {
                        fcount++;
                    }
                    list.add(f);
                    fireJobEvent(JobEvent.UPDATE, f.getDescription());
                }

                if (isTerminate()) {
                    break;
                }
            }
        }

        fireJobEvent(JobEvent.UPDATE, "\nAll tasks have been run.");
        if (fcount == 1) {
            fireJobEvent(JobEvent.UPDATE, "There was 1 failure.");
        } else {
            fireJobEvent(JobEvent.UPDATE, "There were " + fcount
                + " failures.");
        }
        if (list.size() > 0) {

            Finding[] result = list.toArray(new Finding[list.size()]);
            fireJobEvent(JobEvent.COMPLETE, result);

        } else {
            fireJobEvent(JobEvent.COMPLETE);
        }
    }

    /**
     * @inheritDoc
     */
    public void stop() {
        setTerminate(true);
    }

}

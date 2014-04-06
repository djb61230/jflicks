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
package org.jflicks.util;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class ExecTest {

    /**
     * Default empty constructor.
     */
    private ExecTest() {
    }

    /**
     * Simple main method that dumps the system properties to stdout.
     *
     * @param args Arguments that happen to be ignored.
     */
    public static void main(String[] args) {

        SystemJob job = SystemJob.getInstance("mplayer " + args[0]);
        JobContainer jc = JobManager.getJobContainer(job);
        jc.start();
    }

}


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
package org.jflicks.job;

/**
 * This is a high level class that returns a JobContainer for a Job instance.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class JobManager {

    private JobManager() {
    }

    /**
     * Given a Job create a JobContainer that then can be controlled by the
     * user to start and stop the Job.
     *
     * @param j A given Job.
     * @return A JobContainer that can start the job.
     */
    public static JobContainer getJobContainer(Job j) {

        if (j != null) {

            return (new ThreadContainer(j));
        }

        return (null);
    }

    /**
     * Simple method to sleep.  The time is in milliseconds.
     *
     * @param l The number of milliseconds to sleep.
     */
    public static void sleep(long l) {

        try {

            Thread.sleep(l);

        } catch (Exception e) {

            //System.out.println("Sleep interrupted!");
        }
    }

    /**
     * Interrupt the JobContainer NOW!
     *
     * @param jc A given JobContainer to interrupt.
     */
    public static void interrupt(JobContainer jc) {

        if (jc != null) {

            jc.interrupt();
        }
    }

}

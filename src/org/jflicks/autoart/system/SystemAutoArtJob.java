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
package org.jflicks.autoart.system;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobManager;

/**
 * This job supports the Schedules Direct Program Data service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SystemAutoArtJob extends AbstractJob {

    private SystemAutoArt systemAutoArt;

    /**
     * This job supports the SystemAutoArt plugin.
     *
     * @param saa A SystemAutoArt instance.
     */
    public SystemAutoArtJob(SystemAutoArt saa) {

        setSystemAutoArt(saa);
    }

    private SystemAutoArt getSystemAutoArt() {
        return (systemAutoArt);
    }

    private void setSystemAutoArt(SystemAutoArt saa) {
        systemAutoArt = saa;
    }

    private void log(int status, String message) {

        SystemAutoArt saa = getSystemAutoArt();
        if ((saa != null) && (message != null)) {

            saa.log(status, message);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setTerminate(false);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        long defaultSleeptime = 20 * 60 * 1000;

        long wait = defaultSleeptime;
        while (!isTerminate()) {

            SystemAutoArt saa = getSystemAutoArt();
            log(SystemAutoArt.INFO, "SystemAutoArt " + saa);
            if (saa != null) {

                log(SystemAutoArt.INFO, "Calling performUpdate...");
                saa.performUpdate();
                wait = saa.getConfiguredUpdateTimeInMinutes() * 60 * 1000;
                log(SystemAutoArt.INFO, "Returned now sleeping " + wait);
            }

            JobManager.sleep(wait);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
    }

}

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
package org.jflicks.tv.programdata.sd;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobManager;

import net.sf.xtvdclient.xtvd.DataDirectException;
import net.sf.xtvdclient.xtvd.datatypes.Xtvd;

/**
 * This job supports the Schedules Direct Program Data service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SchedulesDirectProgramDataJob extends AbstractJob {

    private SchedulesDirectProgramData schedulesDirectProgramData;

    /**
     * This job supports the SchedulesDirectProgramData plugin.
     *
     * @param d A SchedulesDirectProgramData instance.
     */
    public SchedulesDirectProgramDataJob(SchedulesDirectProgramData d) {

        setSchedulesDirectProgramData(d);
        setSleepTime(60000);
    }

    private SchedulesDirectProgramData getSchedulesDirectProgramData() {
        return (schedulesDirectProgramData);
    }

    private void setSchedulesDirectProgramData(SchedulesDirectProgramData r) {
        schedulesDirectProgramData = r;
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

        boolean firstTime = true;

        while (!isTerminate()) {

            SchedulesDirectProgramData pd = getSchedulesDirectProgramData();
            if (pd != null) {

                if (pd.isTimeToUpdate()) {

                    pd.setUpdatingNow(true);
                    SchedulesDirect sd = SchedulesDirect.getInstance();

                    String user = pd.getConfiguredUserName();
                    String pass = pd.getConfiguredPassword();
                    String country = pd.getConfiguredCountry();
                    String zip = pd.getConfiguredZipCode();
                    if ((user != null) && (pass != null) && (country != null) && (zip != null)) {

                        Xtvd xtvd = sd.getXtvd(user, pass, country, zip);
                        if (xtvd != null) {

                            pd.process(xtvd, sd.getChannelLogos());
                        }
                        pd.setUpdatingNow(false);

                    } else {

                        JobManager.sleep(getSleepTime());
                    }

                } else {

                    if (firstTime) {

                        pd.notify(false);
                    }
                }

                firstTime = false;
            }

            JobManager.sleep(getSleepTime());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
    }

}

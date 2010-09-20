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
package org.jflicks.tv.recorder.hdhr;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;

/**
 * This job will set a HDHR to a particular frequency.  For example an
 * OTA digital station broadcasts on a certain frequency in the VHF or
 * UHF bands.  To eventually record something from the HDHR, it needs
 * to be set to this frequency.  Subsequently it also needs to be set
 * to the channels "program" before recording can occur.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class FrequencyJob extends BaseHDHRJob {

    private int frequency;

    /**
     * Simple no argument constructor.
     */
    public FrequencyJob() {
    }

    /**
     * The frequency to tune the HDHR.  This value is an int in the range
     * of 2-58 to represent the VHF-UHF bands.  It also is used to set a
     * cable TV QAM frequency.  Setting it to zero will "untune" the HDHR
     * or set it to "none".
     *
     * @return The frequency value.
     */
    public int getFrequency() {
        return (frequency);
    }

    /**
     * The frequency to tune the HDHR.  This value is an int in the range
     * of 2-58 to represent the VHF-UHF bands.  It also is used to set a
     * cable TV QAM frequency.  Setting it to zero will "untune" the HDHR
     * or set it to "none".
     *
     * @param i The frequency value.
     */
    public void setFrequency(int i) {
        frequency = i;
    }

    private String frequencyToString() {

        String result = "none";

        int f = getFrequency();
        if (f > 0) {
            result = "auto:" + f;
        }

        return (result);
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

        SystemJob job = SystemJob.getInstance("bin/hdhomerun_config "
            + getId() + " set /tuner" + getTuner() + "/channel "
            + frequencyToString());
        System.out.println("command: <" + job.getCommand() + ">");
        setSystemJob(job);
        job.addJobListener(this);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());
        }

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();
            setJobContainer(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            SystemJob job = getSystemJob();
            if (job != null) {

                System.out.println("FrequencyJob: exit: " + job.getExitValue());
                stop();
            }
        }
    }

}

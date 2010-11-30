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
 * This job will set a HDHR device to a particular "program".  A program
 * is a particular stream on a frequency.  For example, an OTA broadcaster
 * has it's main channel and often one or more sub-channels.  After the
 * HDHR is set to a frequency, this job will tune it to a particular
 * stream - either the main channel one of it's sub-channels.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ProgramJob extends BaseHDHRJob {

    private String program;

    /**
     * Simple no argument constructor.
     */
    public ProgramJob() {
    }

    /**
     * The program value is the digital channel name.  For example "2.1" or
     * "2.2".
     *
     * @return The program as a String.
     */
    public String getProgram() {
        return (program);
    }

    /**
     * The program value is the digital channel name.  For example "2.1" or
     * "2.2".
     *
     * @param s The program as a String.
     */
    public void setProgram(String s) {
        program = s;
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
            + getId() + " set /tuner" + getTuner() + "/program "
            + getProgram());
        fireJobEvent(JobEvent.UPDATE, "command: <" + job.getCommand() + ">");
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

                fireJobEvent(JobEvent.UPDATE, "ProgramJob: exit: "
                    + job.getExitValue());
                stop();
            }
        }
    }

}

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
package org.jflicks.player.hulu;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;

/**
 * This job starts a system job that runs hulu.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class HuluJob extends AbstractJob implements JobListener {

    private Hulu hulu;
    private SystemJob systemJob;
    private JobContainer jobContainer;

    /**
     * Constructor with one required argument.
     *
     * @param hulu The Hulu player instance.
     */
    public HuluJob(Hulu hulu) {

        setHulu(hulu);
    }

    private Hulu getHulu() {
        return (hulu);
    }

    private void setHulu(Hulu c) {
        hulu = c;
    }

    private SystemJob getSystemJob() {
        return (systemJob);
    }

    private void setSystemJob(SystemJob j) {
        systemJob = j;
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer j) {
        jobContainer = j;
    }

    private void log(int level, String message) {

        Hulu c = getHulu();
        if ((c != null) && (message != null)) {

            c.log(level, message);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        SystemJob job = SystemJob.getInstance("huludesktop");

        log(Hulu.DEBUG, "started: " + job.getCommand());
        job.addJobListener(this);
        setSystemJob(job);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();
        setTerminate(false);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());
        }

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();
        }

        setTerminate(true);
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        fireJobEvent(event);
    }

}


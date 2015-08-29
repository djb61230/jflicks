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
 * This job finds the model of a given HDHR recorder.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ModelJob extends BaseHDHRJob {

    private String id;
    private String model;

    /**
     * Simple one argument constructor.
     *
     * @param id A given Id to use.
     */
    public ModelJob(String id) {

        setId(id);
    }

    public String getModel() {
        return (model);
    }

    private void setModel(String s) {
        model = s;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setTerminate(false);
        setModel(null);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        SystemJob job = SystemJob.getInstance("hdhomerun_config " + getId() + " get /sys/hwmodel");
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

                String output = job.getOutputText();
                setJobContainer(null);
                if (output != null) {

                     output = output.trim();
                     setModel(output);
                }
                stop();
            }
        }
    }

}

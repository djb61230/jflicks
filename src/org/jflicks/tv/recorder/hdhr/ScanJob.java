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

import java.io.File;
import java.io.IOException;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;

/**
 * This job finds the HDHR recorders on the local network.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ScanJob extends BaseHDHRJob {

    private File file;

    /**
     * Simple no argument constructor.
     */
    public ScanJob() {
    }

    /**
     * The log data is sent to a File.
     *
     * @return A File instance.
     */
    public File getFile() {
        return (file);
    }

    private void setFile(File f) {
        file = f;
    }

    private boolean isChannelMap() {

        boolean result = false;

        String s = getFrequencyType();
        if ((s != null) && (!s.equals("auto"))) {

            result = true;
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

        try {

            File f = File.createTempFile("hdhrscan", ".log");
            setFile(f);

            SystemJob job = null;

            if (isChannelMap()) {

                job = SystemJob.getInstance("hdhomerun_config " + getId()
                    + " set /tuner" + getTuner() + "/channelmap "
                    + getFrequencyType());
                fireJobEvent(JobEvent.UPDATE, "command: <" + job.getCommand()
                    + ">");
                setSystemJob(job);

            } else {

                job = SystemJob.getInstance("hdhomerun_config " + getId()
                    + " scan /tuner" + getTuner() + " " + f.getPath());
                fireJobEvent(JobEvent.UPDATE, "command: <" + job.getCommand()
                    + ">");
                setSystemJob(job);
            }

            if (job != null) {

                job.addJobListener(this);
                JobContainer jc = JobManager.getJobContainer(job);
                setJobContainer(jc);
                jc.start();
            }

            while (!isTerminate()) {

                JobManager.sleep(getSleepTime());
            }

        } catch (IOException ex) {
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

            if (isChannelMap()) {

                setFrequencyType(null);

                File f = getFile();
                SystemJob job = SystemJob.getInstance("hdhomerun_config "
                    + getId() + " scan /tuner" + getTuner() + " "
                    + f.getPath());
                fireJobEvent(JobEvent.UPDATE, "command: <" + job.getCommand()
                    + ">");
                setSystemJob(job);
                job.addJobListener(this);
                JobContainer jc = JobManager.getJobContainer(job);
                setJobContainer(jc);
                jc.start();

            } else {

                stop();
            }

        } else if (event.getType() == JobEvent.UPDATE) {

            fireJobEvent(event);
        }
    }

}

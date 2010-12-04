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
 * After finding, setting a frequency and a program, it's time to record
 * from an HDHR device.  The resulting video stream is stored to a local
 * File and the user can configure the time in seconds for the recording
 * job to run.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class StreamJob extends BaseHDHRJob {

    private String host;
    private int port;

    /**
     * Simple no argument constructor.
     */
    public StreamJob() {
    }

    /**
     * We need a host to send packets.
     *
     * @return A host as a String.
     */
    public String getHost() {
        return (host);
    }

    /**
     * We need a host to send packets.
     *
     * @param s A host as a String.
     */
    public void setHost(String s) {
        host = s;
    }

    /**
     * We need a port to send packets.
     *
     * @return A port as an int.
     */
    public int getPort() {
        return (port);
    }

    /**
     * We need a port to send packets.
     *
     * @param i A port as an int.
     */
    public void setPort(int i) {
        port = i;
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

        SystemJob job = SystemJob.getInstance("hdhomerun_config "
            + getId() + " set /tuner" + getTuner() + "/target udp://"
            + getHost() + ":" + getPort());

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

                System.out.println("StreamJob: exit: " + job.getExitValue());
                stop();
            }
        }
    }

}

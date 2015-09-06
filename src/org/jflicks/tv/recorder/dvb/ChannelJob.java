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
package org.jflicks.tv.recorder.dvb;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.tv.recorder.BaseDeviceJob;

/**
 * This job will change the channel for a DVB Linux device.  It can use the
 * dvb-apps zap family of programs to set the frequency start the video
 * stream.  It has a property called "script" that is a command-line to
 * execute.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ChannelJob extends BaseDeviceJob {

    private String channel;
    private String script;
    private String readyText;
    private boolean ready;

    /**
     * Simple no argument constructor.
     */
    public ChannelJob() {
    }

    /**
     * The channel value is the channel number.
     *
     * @return The channel as a String.
     */
    public String getChannel() {
        return (channel);
    }

    /**
     * The channel value is the channel number.
     *
     * @param s The channel as a String.
     */
    public void setChannel(String s) {
        channel = s;
    }

    /**
     * A channel will be set by running an external program with the last
     * argument being the Channel property.
     *
     * @return A command line calling a zap program.
     */
    public String getScript() {
        return (script);
    }

    /**
     * A channel will be set by running an external program with the last
     * argument being the Channel property.
     *
     * @param s A command line calling a zap program.
     */
    public void setScript(String s) {
        script = s;
    }

    public String getReadyText() {
        return (readyText);
    }

    public void setReadyText(String s) {
        readyText = s;
    }

    public boolean isReady() {
        return (ready);
    }

    private void setReady(boolean b) {

        boolean old = ready;
        ready = b;
        firePropertyChange("Ready", old, ready);
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

        SystemJob job = null;
        String scr = getScript();
        if (scr != null) {

            job = SystemJob.getInstance(scr + " " + getChannel());
            fireJobEvent(JobEvent.UPDATE, "command:<" + job.getCommand() + ">");
            setSystemJob(job);
            job.addJobListener(this);
            JobContainer jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            jc.start();

        } else {

            setTerminate(true);
        }

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

        } else {

            String text = event.getMessage();
            if ((!isReady()) && (text != null)) {

                setReady(text.indexOf(getReadyText()) != -1);
            }
        }
    }

}

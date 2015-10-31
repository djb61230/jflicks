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
package org.jflicks.tv.postproc.worker;

import java.util.Stack;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.tv.Commercial;

/**
 * Transfer a file using curl.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ChapterFrameJob extends AbstractJob implements JobListener {

    private Stack<SystemJob> systemJobStack;
    private String path;
    private Commercial[] commercials;

    /**
     * Simple two argument constructor.
     *
     * @param path A given path to a recording file.
     * @param array An array of Commercial instances.
     */
    public ChapterFrameJob(String path, Commercial[] array) {

        setPath(path);
        setCommercials(array);
    }

    /**
     * The local File to save the data.
     *
     * @return The File instance.
     */
    public Commercial[] getCommercials() {
        return (commercials);
    }

    private void setCommercials(Commercial[] array) {
        commercials = array;
    }

    /**
     * The path String.
     *
     * @return The path String.
     */
    public String getPath() {
        return (path);
    }

    private void setPath(String s) {
        path = s;
    }

    private Stack<SystemJob> getSystemJobStack() {
        return (systemJobStack);
    }

    private void setSystemJobStack(Stack<SystemJob> l) {
        systemJobStack = l;
    }

    private static String formatSeconds(int secsIn) {

        int hours = secsIn / 3600;
        int remainder = secsIn % 3600;
        int minutes = remainder / 60;
        int seconds = remainder % 60;

        return ( (hours < 10 ? "0" : "") + hours
        + ":" + (minutes < 10 ? "0" : "") + minutes
        + ":" + (seconds< 10 ? "0" : "") + seconds + ".000");
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        Commercial[] comms = getCommercials();
        String fpath = getPath();
        if ((fpath != null) && (comms != null) && (comms.length > 0)) {

            // Setup all the frame grabs into a list.
            Stack<SystemJob> stack = new Stack<SystemJob>();
            String cmdline = "ffmpeg -i \"" + path + "\" -ss 00:00:00.000 -vframes 1 \"" + path + ".cframe0.png\"";
            SystemJob job = SystemJob.getInstance(cmdline);
            job.addJobListener(this);
            stack.push(job);
            for (int i = 0; i < comms.length; i++) {

                String fmt = formatSeconds(comms[i].getEnd());
                cmdline = "ffmpeg -i \"" + path + "\" -ss " + fmt + " -vframes 1 \"" + path + ".cframe" + (i + 1)
                    + ".png\"";
                job = SystemJob.getInstance(cmdline);
                job.addJobListener(this);
                stack.push(job);
            }

            setSystemJobStack(stack);
            setTerminate(false);

            // Start the first job.
            job = stack.pop();
            JobContainer jc = JobManager.getJobContainer(job);
            jc.start();

        } else {

            // Nothing to do.
            setTerminate(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        // Wail until all frame grabs are done.
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
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            Stack<SystemJob> stack = getSystemJobStack();
            if (stack != null) {

                if (!stack.isEmpty()) {

                    SystemJob job = stack.pop();
                    JobContainer jc = JobManager.getJobContainer(job);
                    jc.start();

                } else {

                    stop();
                }

            } else {

                stop();
            }
        }
    }

    public static void main(String[] args) {

        Commercial[] array = new Commercial[10];
        array[0] = new Commercial();
        array[0].setEnd(5);
        array[1] = new Commercial();
        array[1].setEnd(811);
        array[2] = new Commercial();
        array[2].setEnd(1175);
        array[3] = new Commercial();
        array[3].setEnd(1271);
        array[4] = new Commercial();
        array[4].setEnd(1999);
        array[5] = new Commercial();
        array[5].setEnd(2045);
        array[6] = new Commercial();
        array[6].setEnd(2617);
        array[7] = new Commercial();
        array[7].setEnd(2725);
        array[8] = new Commercial();
        array[8].setEnd(3397);
        array[9] = new Commercial();
        array[9].setEnd(3623);

        ChapterFrameJob job = new ChapterFrameJob("./EP011581290130_2015_10_23_21_00.ts.mp4", array);
        final JobContainer jc = JobManager.getJobContainer(job);
        jc.start();
    }

}

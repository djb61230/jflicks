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
package org.jflicks.ui.view.metadata;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;

/**
 * A job that creates an image from a video file.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ThumbnailerJob extends AbstractJob implements JobListener {

    private String inputPath;
    private String outputPath;
    private int seconds;

    /**
     * Constructor with 3 required arguments.
     *
     * @param in A path to a video file.
     * @param out A path to an output image file.
     * @param seconds The seconds in the file to seek.
     */
    public ThumbnailerJob(String in, String out, int seconds) {

        setInputPath(in);
        setOutputPath(out);
        setSeconds(seconds);
    }

    private String getInputPath() {
        return (inputPath);
    }

    private void setInputPath(String s) {
        inputPath = s;
    }

    private String getOutputPath() {
        return (outputPath);
    }

    private void setOutputPath(String s) {
        outputPath = s;
    }

    private int getSeconds() {
        return (seconds);
    }

    private void setSeconds(int i) {
        seconds = i;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        SystemJob job = SystemJob.getInstance("ffmpeg -ss " + getSeconds()
            + " -y -i " + getInputPath() + " -vcodec png -vframes 1 -an -f "
            + "rawvideo " + getOutputPath());

        job.addJobListener(this);
        JobContainer jc = JobManager.getJobContainer(job);
        System.out.println("started: " + job.getCommand());
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

        setTerminate(true);
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            stop();

        } else {

            System.out.println(event.getMessage());
        }
    }

}

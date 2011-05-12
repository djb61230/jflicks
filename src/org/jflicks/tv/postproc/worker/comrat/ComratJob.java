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
package org.jflicks.tv.postproc.worker.comrat;

import java.io.File;
import java.io.IOException;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.tv.Commercial;
import org.jflicks.tv.Recording;
import org.jflicks.tv.postproc.worker.BaseWorker;
import org.jflicks.tv.postproc.worker.BaseWorkerJob;
import org.jflicks.util.Detect;

/**
 * This job starts a system job that runs comskip.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ComratJob extends BaseWorkerJob implements JobListener {

    private File directory;
    private int type;
    private int fudge;
    private int backup;
    private int span;
    private boolean verbose;

    /**
     * Constructor with one required argument.
     *
     * @param r A Recording to check for commercials.
     * @param bw The Worker associated with this Job.
     */
    public ComratJob(Recording r, BaseWorker bw) {

        super(r, bw);

        // Check the recording for completion every minute.
        setSleepTime(60000);
        setSpan(5);
    }

    /**
     * This is the Detect type property, either BLACK or WHITE.
     *
     * @return The type as an int.
     */
    public int getType() {
        return (type);
    }

    /**
     * This is the Detect type property, either BLACK or WHITE.
     *
     * @param i The type as an int.
     */
    public void setType(int i) {
        type = i;
    }

    /**
     * This is an integer to tell the Detect program to mitigate the
     * BLACK or WHITE value to shades of gray.
     *
     * @return An int value.
     */
    public int getFudge() {
        return (fudge);
    }

    /**
     * This is an integer to tell the Detect program to mitigate the
     * BLACK or WHITE value to shades of gray.
     *
     * @param i An int value.
     */
    public void setFudge(int i) {
        fudge = i;
    }

    /**
     * We want to actually adjust the break a few seconds.
     *
     * @return An int value in seconds.
     */
    public int getBackup() {
        return (backup);
    }

    /**
     * We want to actually adjust the break a few seconds.
     *
     * @param i An int value in seconds.
     */
    public void setBackup(int i) {
        backup = i;
    }

    /**
     * The time between each frame.  Defaults to five.
     *
     * @return The span as an int value.
     */
    public int getSpan() {
        return (span);
    }

    /**
     * The time between each frame.  Defaults to five.
     *
     * @param i The span as an int value.
     */
    public void setSpan(int i) {
        span = i;
    }

    /**
     * Turning on verbose will send messages to the console and leave
     * working images on disk.  This is handy for debugging.
     *
     * @return True when the program should be verbose.
     */
    public boolean isVerbose() {
        return (verbose);
    }

    /**
     * Turning on verbose will send messages to the console and leave
     * working images on disk.  This is handy for debugging.
     *
     * @param b True when the program should be verbose.
     */
    public void setVerbose(boolean b) {
        verbose = b;
    }

    private File getDirectory() {
        return (directory);
    }

    private void setDirectory(File f) {
        directory = f;
    }

    private File createTempFile() {

        File result = null;

        try {

            File dir = File.createTempFile("comrat", "work");
            if (!dir.delete()) {
                log(BaseWorker.INFO, dir.getPath() + " not found");
            }
            if (dir.mkdir()) {

                result = dir;

            } else {

                log(BaseWorker.INFO, "Failed to make " + dir.getPath());
            }

        } catch (IOException ex) {

            result = null;
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        Recording r = getRecording();
        if (r != null) {

            File dir = createTempFile();
            setDirectory(dir);
            if (dir != null) {

                SystemJob job = SystemJob.getInstance("ffmpeg -i "
                    + r.getPath() + " -r 1/" + getSpan() + " -s hd480 "
                    + dir.getPath() + File.separator + "frame-%4d.jpg");

                job.addJobListener(this);
                setSystemJob(job);
                JobContainer jc = JobManager.getJobContainer(job);
                setJobContainer(jc);
                log(BaseWorker.INFO, "Will start: " + job.getCommand());
                setTerminate(false);

            } else {

                log(BaseWorker.INFO, "Couldn't make a working dir - quitting.");
                setTerminate(true);
            }

        } else {

            log(BaseWorker.INFO, "Recording is null - quitting.");
            setTerminate(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        boolean frameStarted = false;

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());

            if (!frameStarted) {

                Recording r = getRecording();
                if (!r.isCurrentlyRecording()) {

                    // We are ready to start ffmpeg.
                    JobContainer jc = getJobContainer();
                    if (jc != null) {

                        jc.start();
                        frameStarted = true;
                        log(BaseWorker.INFO, "Actually kicked off ffmpeg");
                    }

                } else {

                    log(BaseWorker.INFO, "Recording still seems to be on. "
                        + "Waiting until finished to grab frames.");
                }
            }
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

        if (event.getType() == JobEvent.COMPLETE) {

            log(BaseWorker.INFO, "Frame grab finished...");
            File dir = getDirectory();
            Recording r = getRecording();
            if ((dir != null) && (r != null)) {

                // ffmpeg finished, now we need to look for the rating
                // frames.
                try {

                    Detect detect = new Detect();
                    detect.setBackup(getBackup());
                    detect.setSpan(getSpan());
                    log(BaseWorker.INFO, "Start processing of frames...");
                    int[] array = detect.processDirectory(dir, "jpg", getType(),
                        getFudge(), isVerbose());
                    log(BaseWorker.INFO, "Finished processing of frames...");
                    if ((array != null) && (array.length > 0)) {

                        log(BaseWorker.INFO, "Found " + array.length
                            + " rating frames...setting commercials");
                        Commercial[] coms = new Commercial[array.length];
                        for (int i = 0; i < coms.length; i++) {

                            coms[i] = new Commercial();

                            int start = array[i] - 60;
                            if (start < 0) {
                                start = 0;
                            }
                            coms[i].setStart(start);
                            coms[i].setEnd(array[i]);
                        }

                        r.setCommercials(coms);

                    } else {

                        log(BaseWorker.INFO, "Didn't find any rating frames!");
                    }

                    // Now need to delete frames....
                    boolean hosed = false;
                    File[] files = dir.listFiles();
                    if ((files != null) && (files.length > 0)) {

                        for (int i = 0; i < files.length; i++) {

                            if (!files[i].delete()) {

                                hosed = true;
                            }
                        }
                    }

                    if (!hosed) {

                        if (!dir.delete()) {

                            log(BaseWorker.INFO, "Crap left working dir :"
                                + dir.getPath());
                        }
                    }

                    setDirectory(null);

                } catch (IOException ex) {

                    log(BaseWorker.INFO, "Comrat IO bad news.");
                }
            }

            setTerminate(true);
        }
    }

}


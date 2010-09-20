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
package org.jflicks.player.mplayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;

/**
 * This job starts a system job that runs mplayer.  It also is a conduit to
 * send mplayer commands over stdin.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class MPlayerJob extends AbstractJob implements JobListener {

    private SystemJob systemJob;
    private JobContainer jobContainer;
    private long position;
    private int seconds;
    private String path;
    private boolean autoSkip;
    private FileWriter fileWriter;

    /**
     * Constructor with three required arguments.  There are two ways to
     * begin playing at some point in the video and if either is non-zero
     * it will be used.  The position field is checked first so it's best
     * to have only one of them non-zero if playing past the beginning is
     * desired.
     *
     * @param position The number of bytes into the video to begin playing.
     * @param seconds The number of seconds into the video to begin playing.
     * @param path The path to the file to play.
     * @param autoSkip When true try to auto skip commercials.
     */
    public MPlayerJob(long position, int seconds, String path,
        boolean autoSkip) {

        setPosition(position);
        setSeconds(seconds);
        setPath(path);
        setAutoSkip(autoSkip);
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

    /**
     * The video is going to begin at byte offset Position.  Of course this
     * could be zero signifying the beginning in which case it will be ignored.
     *
     * @return The number of bytes into the video to begin playing.
     */
    public long getPosition() {
        return (position);
    }

    /**
     * The video is going to begin at byte offset Position.  Of course this
     * could be zero signifying the beginning in which case it will be ignored.
     *
     * @param l The number of bytes into the video to begin playing.
     */
    public void setPosition(long l) {
        position = l;
    }

    /**
     * The video is going to begin at seconds.  Of course this could
     * be zero signifying the beginning in which case it will be ignored.
     *
     * @return The number of seconds into the video to begin playing.
     */
    public int getSeconds() {
        return (seconds);
    }

    /**
     * The video is going to begin at seconds.  Of course this could
     * be zero signifying the beginning in which case it will be ignored.
     *
     * @param i The number of seconds into the video to begin playing.
     */
    public void setSeconds(int i) {
        seconds = i;
    }

    /**
     * When we start the mplayer system job we load and start playing the
     * video from the command line.
     *
     * @return The path to the video file.
     */
    public String getPath() {
        return (path);
    }

    /**
     * When we start the mplayer system job we load and start playing the
     * video from the command line.
     *
     * @param s The path to the video file.
     */
    public void setPath(String s) {
        path = s;
    }

    /**
     * When auto skip is enabled, mplayer will use an edl file.  The file
     * is assumed to be the same file name as the Path argument but with
     * an ".edl" extension instead of it's current extenstion.
     *
     * @return True if auto skip is desired.
     */
    public boolean isAutoSkip() {
        return (autoSkip);
    }

    /**
     * When auto skip is enabled, mplayer will use an edl file.  The file
     * is assumed to be the same file name as the Path argument but with
     * an ".edl" extension instead of it's current extenstion.
     *
     * @param b True if auto skip is desired.
     */
    public void setAutoSkip(boolean b) {
        autoSkip = b;
    }

    private FileWriter getFileWriter() {
        return (fileWriter);
    }

    private void setFileWriter(FileWriter w) {
        fileWriter = w;
    }

    /**
     * This method will pass a command to mplayer over stdin.
     *
     * @param s The given command to send.
     */
    public void command(String s) {

        FileWriter w = getFileWriter();
        if ((s != null) && (w != null)) {

            try {

                System.out.println("Sending...<" + s + ">");
                w.write(s, 0, s.length());
                w.flush();

            } catch (IOException ex) {

                throw new RuntimeException(ex);
            }
        }
    }

    private String computeEDLArgument(String s) {

        String result = "";

        if ((s != null) && (isAutoSkip())) {

            String fname = s.substring(0, s.lastIndexOf("."));
            result = "-edl " + fname + ".edl";
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        String startParameter = "";
        if (getPosition() > 0) {

            startParameter = "-sb " + getPosition();

        } else if (getSeconds() > 0) {

            startParameter = "-ss " + getSeconds();
        }

        String edltext = computeEDLArgument(getPath());

        File conf = new File("conf");
        String cpath = conf.getAbsolutePath();
        SystemJob job = SystemJob.getInstance(
                "mplayer -input"
                + " nodefault-bindings:conf="
                + cpath + "/mplayer.conf:" + "file=mplayer.fifo"
                //+ " nodefault-bindings:conf=/dev/null:file=mplayer.fifo"
                + " -fs -zoom -slave -framedrop " + edltext
                //+ " -fs -zoom -slave -cache 65536 -framedrop " + edltext
                + " " + startParameter + " " + getPath());

        System.out.println("started: " + job.getCommand());
        job.addJobListener(this);
        setSystemJob(job);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();
        try {

            FileWriter fw = new FileWriter("mplayer.fifo");
            setFileWriter(fw);

        } catch (IOException ex) {

            System.out.println("WARNING: FIFO not opened");
        }
        setTerminate(false);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();
        }

        FileWriter w = getFileWriter();
        if (w != null) {

            try {

                w.close();

            } catch (IOException ex) {

                System.out.println("WARNING: could not close FIFO");
            }
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


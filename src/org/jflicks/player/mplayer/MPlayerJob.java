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
import org.jflicks.util.Util;

/**
 * This job starts a system job that runs mplayer.  It also is a conduit to
 * send mplayer commands over stdin.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class MPlayerJob extends AbstractJob implements JobListener {

    private MPlayer mplayer;
    private SystemJob systemJob;
    private JobContainer jobContainer;
    private String windowId;
    private long position;
    private int seconds;
    private String path;
    private boolean autoSkip;
    private FileWriter fileWriter;
    private String[] args;

    /**
     * Constructor with three required arguments.  There are two ways to
     * begin playing at some point in the video and if either is non-zero
     * it will be used.  The position field is checked first so it's best
     * to have only one of them non-zero if playing past the beginning is
     * desired.
     *
     * @param mplayer The player instance creating the job.
     * @param wid A window ID.
     * @param args An array of arguments to give to mplayer.
     * @param position The number of bytes into the video to begin playing.
     * @param seconds The number of seconds into the video to begin playing.
     * @param path The path to the file to play.
     * @param autoSkip When true try to auto skip commercials.
     */
    public MPlayerJob(MPlayer mplayer, String wid, String[] args,
        long position, int seconds, String path, boolean autoSkip) {

        setMPlayer(mplayer);
        setWindowId(wid);
        setArgs(args);
        setPosition(position);
        setSeconds(seconds);
        setPath(path);
        setAutoSkip(autoSkip);
    }

    private MPlayer getMPlayer() {
        return (mplayer);
    }

    private void setMPlayer(MPlayer p) {
        mplayer = p;
    }

    private String[] getArgs() {
        return (args);
    }

    private void setArgs(String[] array) {
        args = array;
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

        MPlayer m = getMPlayer();
        if ((m != null) && (message != null)) {

            m.log(level, message);
        }
    }

    /**
     * When set mplayer can be told to use an existing window instead of
     * making a new one.
     *
     * @return A window ID as a String.
     */
    public String getWindowId() {
        return (windowId);
    }

    /**
     * When set mplayer can be told to use an existing window instead of
     * making a new one.
     *
     * @param s A window ID as a String.
     */
    public void setWindowId(String s) {
        windowId = s;
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

        if (Util.isLinux()) {

            FileWriter w = getFileWriter();
            if ((s != null) && (w != null)) {

                try {

                    w.write(s, 0, s.length());
                    w.flush();

                } catch (IOException ex) {

                    throw new RuntimeException(ex);
                }
            }

        } else {

            // We assume windows or at least non-fifo communication.
            SystemJob job = getSystemJob();
            if (job != null) {

                try {

                    job.write(s.getBytes(), 0, s.length());

                } catch (IOException ex) {

                    throw new RuntimeException(ex);
                }
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

    private String computeDemuxer() {

        String result = "";

        MPlayer m = getMPlayer();
        if (m != null) {

            if (m.isVideoTransportStreamType()) {

                result = "-demuxer mpegts";

            } else if (m.isVideoProgramStreamType()) {

                result = "-demuxer mpegps";
            }
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

        String userArg = "";
        String[] userArgs = getArgs();
        if ((userArgs != null) && (userArgs.length > 0)) {

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < userArgs.length; i++) {

                sb.append(userArgs[i]);
                sb.append(" ");
            }

            userArg = sb.toString().trim();
        }

        SystemJob job = null;

        String demuxer = computeDemuxer();

        String wid = getWindowId();
        if (wid != null) {

            job = SystemJob.getInstance(
                "mplayer -wid " + wid + " " + userArg + " " + demuxer
                + " -input nodefault-bindings:conf=/dev/null:"
                + "file=mplayer.fifo" + " -slave " + edltext
                + " " + startParameter + " " + getPath());

        } else {

            File conf = new File("conf");
            File full = new File(conf, "mplayer.conf");
            job = SystemJob.getInstance(
                "mplayer -fs -zoom" + " " + userArg + " " + demuxer
                + " -input nodefault-bindings:conf="
                + full.getAbsolutePath() + ":"
                + "file=mplayer.fifo" + " -slave " + edltext
                + " " + startParameter + " " + getPath());
        }

        log(MPlayer.DEBUG, "started: " + job.getCommand());
        job.addJobListener(this);
        setSystemJob(job);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();
        try {

            FileWriter fw = new FileWriter("mplayer.fifo");
            setFileWriter(fw);

        } catch (IOException ex) {

            log(MPlayer.WARNING, "WARNING: FIFO not opened");
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

                log(MPlayer.WARNING, "WARNING: could not close FIFO");
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


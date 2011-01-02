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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.player.PlayState;
import org.jflicks.util.Util;

/**
 * This job communicates with mplayer to maintain the play state of the
 * video.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class PlayStateJob extends AbstractJob implements JobListener,
    PropertyChangeListener {

    private static final int STREAM_POSITION = 2;
    private static final int STREAM_END = 3;

    private static final String STREAM_POSITION_COMMAND =
        "pausing_keep_force get_property stream_pos";
    private static final String STREAM_POSITION_ANSWER = "ANS_stream_pos";

    private static final String STREAM_END_COMMAND =
        "pausing_keep_force get_property stream_end";
    private static final String STREAM_END_ANSWER = "ANS_stream_end";

    private MPlayer mplayer;
    private MPlayerJob mplayerJob;
    private double time;
    private double minimumTime;
    private int percent;
    private int state;
    private int startSeconds;
    private long position;
    private long length;
    private boolean usedSeconds;
    private PlayState currentPlayState;
    private boolean checkLength;
    private boolean preferTime;

    /**
     * Contructor with two required arguments.
     *
     * @param p The MPlayer implementation of a Player.
     * @param job The job that execs mplayer in slave mode.
     * @param startSeconds The video may have been started somewhere
     * after the start so we need to know so the current time is computed
     * correctly.
     * @param usedSeconds To work around a bug, we try to seek a second in
     * the video as the "-ss seconds" parameter sometimes seem to result
     * in a freeze frame when beginning playback.  This is a hack for sure.
     * @param preferTime The bookmark should use the time value, so we will
     * skip setting the position totally.
     */
    public PlayStateJob(MPlayer p, MPlayerJob job, int startSeconds,
        boolean usedSeconds, boolean preferTime) {

        setMPlayer(p);
        setUsedSeconds(usedSeconds);
        if (p != null) {

            p.addPropertyChangeListener("Playing", this);
        }

        setMPlayerJob(job);
        if (job != null) {

            job.addJobListener(this);
        }

        setPreferTime(preferTime);
        setMinimumTime(Double.MAX_VALUE);
        setStartSeconds(startSeconds);
        setPosition(0L);
        setLength(0L);
        setSleepTime(3000);
        setCheckLength(true);
        setCurrentPlayState(new PlayState());
    }

    private MPlayer getMPlayer() {
        return (mplayer);
    }

    private void setMPlayer(MPlayer p) {
        mplayer = p;
    }

    private MPlayerJob getMPlayerJob() {
        return (mplayerJob);
    }

    private void setMPlayerJob(MPlayerJob j) {
        mplayerJob = j;
    }

    private int getStartSeconds() {
        return (startSeconds);
    }

    private void setStartSeconds(int i) {
        startSeconds = i;
    }

    private int getState() {
        return (state);
    }

    private void setState(int i) {
        state = i;
    }

    private boolean isUsedSeconds() {
        return (usedSeconds);
    }

    private void setUsedSeconds(boolean b) {
        usedSeconds = b;
    }

    private PlayState getCurrentPlayState() {
        return (currentPlayState);
    }

    private void setCurrentPlayState(PlayState ps) {
        currentPlayState = ps;
    }

    private boolean isCheckLength() {
        return (checkLength);
    }

    private void setCheckLength(boolean b) {
        checkLength = b;
    }

    private boolean isPreferTime() {
        return (preferTime);
    }

    private void setPreferTime(boolean b) {
        preferTime = b;
    }

    private double getTime() {
        return (time);
    }

    private void setTime(double d) {
        time = d;

        if (time < getMinimumTime()) {

            log(MPlayer.DEBUG, "setTime argument: " + time);
            log(MPlayer.DEBUG, "setTime start seconds: " + getStartSeconds());

            double dtmp = (double) getStartSeconds();
            if (Math.abs(time - dtmp) < 12) {

                dtmp = 0;

            } else {

                dtmp = time - dtmp;
                log(MPlayer.DEBUG, "setTime dtmp: " + dtmp);
                if (dtmp < 0.0) {

                    dtmp = time;
                }
            }

            log(MPlayer.DEBUG, "setTime setMinimumTime: " + dtmp);
            setMinimumTime(dtmp);
        }
    }

    /**
     * Since the video we play may not be time stamped correctly, we need to
     * find the "lowest" value of a time stamp.  We then can normalize any
     * time we want by using this value to adjust to zero.
     *
     * @return The lowset time stamp (closest to the beginning) of the video.
     */
    public double getMinimumTime() {
        return (minimumTime);
    }

    private void setMinimumTime(double d) {
        minimumTime = d;
    }

    private int getPercent() {
        return (percent);
    }

    private void setPercent(int i) {
        percent = i;
    }

    private long getPosition() {
        return (position);
    }

    private void setPosition(long l) {
        position = l;
    }

    private long getLength() {
        return (length);
    }

    private void setLength(long l) {
        length = l;
    }

    /**
     * Retrieve the most recent PlayState contructed from a running mplayer
     * program.
     *
     * @return A PlayState instance.
     */
    public PlayState getPlayState() {

        PlayState result = getCurrentPlayState();
        result.setTime(getTime() - getMinimumTime());

        result.setPosition(getPosition());
        MPlayer p = getMPlayer();
        if (p != null) {

            result.setPlaying(p.isPlaying());
            result.setPaused(p.isPaused());
        }

        return (result);
    }

    private void log(int level, String message) {

        MPlayer m = getMPlayer();
        if ((m != null) && (message != null)) {

            m.log(level, message);
        }
    }

    private void command(String s) {

        MPlayerJob job = getMPlayerJob();
        if ((s != null) && (job != null)) {

            job.command(s);
        }
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

        MPlayer p = getMPlayer();
        if (p != null) {

            while (!isTerminate()) {

                JobManager.sleep(getSleepTime());

                if ((p.isPlaying()) && (!p.isPaused())) {

                    int commandState = getState();
                    switch (commandState) {

                    default:
                    case STREAM_POSITION:
                        if (!isPreferTime()) {
                            command(STREAM_POSITION_COMMAND + "\n");
                        }
                        break;

                    case STREAM_END:
                        if (isCheckLength()) {
                            command(STREAM_END_COMMAND + "\n");
                        }
                        break;
                    }
                }
            }
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
     * We listen for property changes from the Player.
     *
     * @param event A PropertyChangeEvent instance.
     */
    public void propertyChange(PropertyChangeEvent event) {

        setState(STREAM_POSITION);
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            MPlayer p = getMPlayer();
            if (p != null) {

                setPercent(0);
                p.setPlaying(false);
                p.setCompleted(!p.isUserStop());
                p.dispose();
            }

            stop();

        } else {

            String message = event.getMessage();
            MPlayer p = getMPlayer();
            if ((p != null) && (p.isPlaying()) && (message != null)) {

                if (message.startsWith(STREAM_POSITION_ANSWER)) {

                    setPosition(Util.str2long(message.substring(
                        message.indexOf("=") + 1), 0L));

                } else if (message.startsWith(STREAM_END_ANSWER)) {

                    long oldlength = getLength();
                    long newlength = Util.str2long(message.substring(
                        message.indexOf("=") + 1), 0L);
                    if (oldlength != newlength) {

                        setLength(newlength);

                    } else {

                        setCheckLength(false);
                    }

                    setState(STREAM_POSITION);

                } else if (message.indexOf("V:") != -1) {

                    int sindex = message.indexOf("V:") + 2;
                    int eindex = message.indexOf("A-V:") - 1;
                    String tmp = message.substring(sindex, eindex);
                    double dtmp = Util.str2double(tmp, Double.MAX_VALUE);
                    if (dtmp > 0.0) {

                        setTime(dtmp);

                        if (isUsedSeconds()) {

                            setUsedSeconds(false);
                            command("seek 1\n");
                        }
                    }

                } else {

                    System.out.println("From mplayer: " + message);
                    //log(MPlayer.DEBUG, "From mplayer: " + message);
                }

            } else {

                log(MPlayer.DEBUG, "From mplayer: " + message);
            }
        }
    }

}


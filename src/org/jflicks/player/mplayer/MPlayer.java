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

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.player.BasePlayer;
import org.jflicks.player.Bookmark;
import org.jflicks.player.PlayState;
import org.jflicks.util.Util;

/**
 * This Player (with other classes in this package) is capable of
 * executing the program mplayer to play media files.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class MPlayer extends BasePlayer {

    private MPlayerJob mplayerJob;
    private PlayStateJob statusJob;
    private JobContainer jobContainer;
    private JobContainer statusJobContainer;
    private boolean userStop;

    /**
     * Simple constructor.
     */
    public MPlayer() {

        setType(PLAYER_VIDEO);
        setTitle("M");
    }

    private MPlayerJob getMPlayerJob() {
        return (mplayerJob);
    }

    private void setMPlayerJob(MPlayerJob j) {
        mplayerJob = j;
    }

    private PlayStateJob getPlayStateJob() {
        return (statusJob);
    }

    private void setPlayStateJob(PlayStateJob j) {
        statusJob = j;
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer jc) {
        jobContainer = jc;
    }

    private JobContainer getPlayStateJobContainer() {
        return (statusJobContainer);
    }

    private void setPlayStateJobContainer(JobContainer jc) {
        statusJobContainer = jc;
    }

    /**
     * Flag to signify thay the user stopped the video, that it didn't come
     * to it's natural end.
     *
     * @return True if the user quit.
     */
    public boolean isUserStop() {
        return (userStop);
    }

    private void setUserStop(boolean b) {
        userStop = b;
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsPause() {
        return (true);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsAutoSkip() {
        return (true);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsSeek() {
        return (true);
    }

    /**
     * {@inheritDoc}
     */
    public void play(String url) {

        play(url, null);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void play(String url, Bookmark b) {

        if (!isPlaying()) {

            setAudioOffset(0);
            setPaused(false);
            setPlaying(true);
            setCompleted(false);
            setUserStop(false);

            long position = 0L;
            int time = 0;
            int playStateTime = 0;
            boolean bookmarkSeconds = false;

            if (b != null) {

                playStateTime = b.getTime();
                if (b.isPreferTime()) {

                    bookmarkSeconds = true;
                    time = playStateTime;

                } else {

                    position = b.getPosition();
                }
            }

            MPlayerJob job = new MPlayerJob(position, time, url, isAutoSkip());
            setMPlayerJob(job);
            PlayStateJob psj =
                new PlayStateJob(this, job, playStateTime, bookmarkSeconds);
            setPlayStateJob(psj);

            JobContainer jc = JobManager.getJobContainer(psj);
            setPlayStateJobContainer(jc);
            jc.start();

            jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            jc.start();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setPaused(false);
        setPlaying(false);
        setUserStop(true);
        command("stop\n");
    }

    /**
     * {@inheritDoc}
     */
    public void pause(boolean b) {

        setPaused(b);
        command("pause\n");
    }

    /**
     * {@inheritDoc}
     */
    public void seek(int seconds) {

        if (seconds > 0) {

            command("seek +" + seconds + "\n");

        } else {

            command("seek " + seconds + "\n");
        }

        command("set_property fullscreen 1\n");
    }

    /**
     * {@inheritDoc}
     */
    public void next() {

        command("pt_step +1\n");
    }

    /**
     * {@inheritDoc}
     */
    public void previous() {

        command("pt_step -1\n");
    }

    /**
     * {@inheritDoc}
     */
    public void audiosync(double offset) {

        double current = getAudioOffset();
        current += offset;
        if (current < -100.0) {
            current = -100.0;
        }
        if (current > 100.0) {
            current = 100.0;
        }
        setAudioOffset(current);

        command("set_property audio_delay " + current + "\n");
    }

    /**
     * {@inheritDoc}
     */
    public PlayState getPlayState() {

        PlayState result = null;

        PlayStateJob job = getPlayStateJob();
        if (job != null) {
            result = job.getPlayState();
        }

        return (result);
    }

    private void command(String s) {

        MPlayerJob job = getMPlayerJob();
        if ((s != null) && (job != null)) {

            job.command(s);
        }
    }

}


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
package org.jflicks.player.hulu;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.player.BasePlayer;
import org.jflicks.player.Bookmark;
import org.jflicks.player.PlayState;
import org.jflicks.util.Util;

/**
 * This Player (with other classes in this package) is capable of
 * executing the program huludesktop to watch hulu.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Hulu extends BasePlayer implements JobListener {

    private HuluJob huluJob;
    private JobContainer jobContainer;

    /**
     * Simple constructor.
     */
    public Hulu() {

        setType(PLAYER_APPLICATION);
        setTitle("Hulu");
    }

    private HuluJob getHuluJob() {
        return (huluJob);
    }

    private void setHuluJob(HuluJob j) {
        huluJob = j;
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer jc) {
        jobContainer = jc;
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
    public boolean supportsMaximize() {
        return (false);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsAutoSkip() {
        return (false);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsSeek() {
        return (false);
    }

    /**
     * {@inheritDoc}
     */
    public void play(String ... urls) {

        play(null, null);
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

            HuluJob job = new HuluJob(this);
            job.addJobListener(this);
            setHuluJob(job);

            JobContainer jc = JobManager.getJobContainer(job);
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
        setCompleted(true);

        try {

            Robot r = new Robot();

            if (Util.isLinux()) {

                r.keyPress(KeyEvent.VK_ALT);
                r.keyPress(KeyEvent.VK_SPACE);
                r.keyRelease(KeyEvent.VK_SPACE);
                r.keyRelease(KeyEvent.VK_ALT);
                r.keyPress(KeyEvent.VK_UP);
                r.keyRelease(KeyEvent.VK_UP);
                r.keyPress(KeyEvent.VK_ENTER);
                r.keyRelease(KeyEvent.VK_ENTER);
            }

        } catch (Exception ex) {

            log(ERROR, "Robot error exiting hulu");
        }

        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void maximize(boolean b) {

        setMaximized(b);
    }

    /**
     * {@inheritDoc}
     */
    public void pause(boolean b) {

        setPaused(b);
    }

    /**
     * {@inheritDoc}
     */
    public void seek(int seconds) {
    }

    /**
     * {@inheritDoc}
     */
    public void seekPosition(int seconds) {
    }

    /**
     * {@inheritDoc}
     */
    public void seekPosition(double percentage) {
    }

    /**
     * {@inheritDoc}
     */
    public void guide() {
    }

    /**
     * {@inheritDoc}
     */
    public void next() {
    }

    /**
     * {@inheritDoc}
     */
    public void previous() {
    }

    /**
     * {@inheritDoc}
     */
    public void audiosync(double offset) {
    }

    /**
     * {@inheritDoc}
     */
    public PlayState getPlayState() {

        PlayState result = null;

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            setPaused(false);
            setPlaying(false);
            setCompleted(true);

            JobContainer jc = getJobContainer();
            if (jc != null) {

                jc.stop();
            }
        }
    }

}


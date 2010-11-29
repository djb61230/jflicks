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
package org.jflicks.player.vlcdvd;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.player.BasePlayer;
import org.jflicks.player.Bookmark;
import org.jflicks.player.PlayState;

/**
 * This Player (with other classes in this package) is capable of
 * executing the program cvlc to play streaming video.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class VlcDvd extends BasePlayer implements JobListener {

    private VlcDvdJob vlcdvdJob;
    private JobContainer jobContainer;
    private Robot robot;

    /**
     * Simple constructor.
     */
    public VlcDvd() {

        setType(PLAYER_VIDEO_DVD);
        setTitle("VlcDvd");

        try {

            setRobot(new Robot());

        } catch (AWTException ex) {

            log(ERROR, ex.getMessage());
        }
    }

    private Robot getRobot() {
        return (robot);
    }

    private void setRobot(Robot r) {
        robot = r;
    }

    private VlcDvdJob getVlcDvdJob() {
        return (vlcdvdJob);
    }

    private void setVlcDvdJob(VlcDvdJob j) {
        vlcdvdJob = j;
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

            VlcDvdJob job = new VlcDvdJob(this, url);
            job.addJobListener(this);
            setVlcDvdJob(job);

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

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_Q);
            r.keyRelease(KeyEvent.VK_Q);
        }

        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();
            setJobContainer(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void pause(boolean b) {

        setPaused(b);

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_SPACE);
            r.keyRelease(KeyEvent.VK_SPACE);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void seek(int seconds) {

        Robot r = getRobot();
        if (r != null) {

            if (seconds < 0) {

                r.keyPress(KeyEvent.VK_SHIFT);
                r.keyPress(KeyEvent.VK_LEFT);
                r.keyRelease(KeyEvent.VK_LEFT);
                r.keyRelease(KeyEvent.VK_SHIFT);

            } else {

                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_RIGHT);
                r.keyRelease(KeyEvent.VK_RIGHT);
                r.keyRelease(KeyEvent.VK_CONTROL);
            }
        }
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
    public void next() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_SHIFT);
            r.keyPress(KeyEvent.VK_N);
            r.keyRelease(KeyEvent.VK_N);
            r.keyRelease(KeyEvent.VK_SHIFT);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void previous() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_SHIFT);
            r.keyPress(KeyEvent.VK_P);
            r.keyRelease(KeyEvent.VK_P);
            r.keyRelease(KeyEvent.VK_SHIFT);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void audiosync(double offset) {

        Robot r = getRobot();
        if (r != null) {

            if (offset < 0.0) {

                r.keyPress(KeyEvent.VK_J);
                r.keyRelease(KeyEvent.VK_J);

            } else {

                r.keyPress(KeyEvent.VK_K);
                r.keyRelease(KeyEvent.VK_K);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void guide() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_SHIFT);
            r.keyPress(KeyEvent.VK_M);
            r.keyRelease(KeyEvent.VK_M);
            r.keyRelease(KeyEvent.VK_SHIFT);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void up() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_UP);
            r.keyRelease(KeyEvent.VK_UP);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void down() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_DOWN);
            r.keyRelease(KeyEvent.VK_DOWN);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void left() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_LEFT);
            r.keyRelease(KeyEvent.VK_LEFT);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void right() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_RIGHT);
            r.keyRelease(KeyEvent.VK_RIGHT);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void enter() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_ENTER);
            r.keyRelease(KeyEvent.VK_ENTER);
        }
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

            stop();
        }
    }

}


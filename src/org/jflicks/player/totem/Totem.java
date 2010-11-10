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
package org.jflicks.player.totem;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.player.BasePlayer;
import org.jflicks.player.Bookmark;
import org.jflicks.player.PlayState;

/**
 * This Player (with other classes in this package) is capable of
 * executing the program totem to load web pages.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Totem extends BasePlayer {

    private TotemJob totemJob;
    private JobContainer jobContainer;
    private Robot robot;

    /**
     * Simple constructor.
     */
    public Totem() {

        setType(PLAYER_VIDEO_DVD);
        setTitle("Totem");

        try {

            setRobot(new Robot());

        } catch (AWTException ex) {

            throw new RuntimeException(ex);
        }
    }

    private TotemJob getTotemJob() {
        return (totemJob);
    }

    private void setTotemJob(TotemJob j) {
        totemJob = j;
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer jc) {
        jobContainer = jc;
    }

    private Robot getRobot() {
        return (robot);
    }

    private void setRobot(Robot r) {
        robot = r;
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

            TotemJob job = new TotemJob(url);
            setTotemJob(job);

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

        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();

            // As an added bonus - let's killall - of course only Linux.
            SystemJob job = SystemJob.getInstance("killall totem");
            jc = JobManager.getJobContainer(job);
            jc.start();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void pause(boolean b) {

        setPaused(b);

        String command = null;
        if (b) {
            command = "--pause";
        } else {
            command = "--play";
        }
        SystemJob job = SystemJob.getInstance("totem " + command);
        System.out.println("started: " + job.getCommand());
        JobContainer jc = JobManager.getJobContainer(job);
        jc.start();
    }

    /**
     * {@inheritDoc}
     */
    public void seek(int seconds) {

        String command = null;
        if (seconds < 0) {
            command = "--seek-bwd";
        } else {
            command = "--seek-fwd";
        }
        SystemJob job = SystemJob.getInstance("totem " + command);
        System.out.println("started: " + job.getCommand());
        JobContainer jc = JobManager.getJobContainer(job);
        jc.start();
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

        SystemJob job = SystemJob.getInstance("totem --next");
        System.out.println("started: " + job.getCommand());
        JobContainer jc = JobManager.getJobContainer(job);
        jc.start();
    }

    /**
     * {@inheritDoc}
     */
    public void previous() {

        SystemJob job = SystemJob.getInstance("totem --previous");
        System.out.println("started: " + job.getCommand());
        JobContainer jc = JobManager.getJobContainer(job);
        jc.start();
    }

    /**
     * {@inheritDoc}
     */
    public void audiosync(double offset) {
    }

    /**
     * {@inheritDoc}
     */
    public void guide() {

        Robot r = getRobot();
        if (r != null) {

            r.keyPress(KeyEvent.VK_M);
            r.keyRelease(KeyEvent.VK_M);
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

}


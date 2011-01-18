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
package org.jflicks.player.chrome;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.player.BasePlayer;
import org.jflicks.player.Bookmark;
import org.jflicks.player.PlayState;
import org.jflicks.util.Util;

/**
 * This Player (with other classes in this package) is capable of
 * executing the program chrome to load web pages.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Chrome extends BasePlayer {

    private ChromeJob chromeJob;
    private JobContainer jobContainer;

    /**
     * Simple constructor.
     */
    public Chrome() {

        setType(PLAYER_VIDEO_WEB);
        setTitle("Chrome");
    }

    private ChromeJob getChromeJob() {
        return (chromeJob);
    }

    private void setChromeJob(ChromeJob j) {
        chromeJob = j;
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
    public void play(String ... urls) {

        if ((urls != null) && (urls.length > 0)) {

            play(urls[0], null);
        }
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

            ChromeJob job = new ChromeJob(this, url);
            setChromeJob(job);

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

                r.keyPress(KeyEvent.VK_SHIFT);
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_Q);
                r.keyRelease(KeyEvent.VK_Q);
                r.keyRelease(KeyEvent.VK_CONTROL);
                r.keyRelease(KeyEvent.VK_SHIFT);

            } else if (Util.isWindows()) {

                r.keyPress(KeyEvent.VK_ALT);
                r.keyPress(KeyEvent.VK_F4);
                r.keyRelease(KeyEvent.VK_F4);
                r.keyRelease(KeyEvent.VK_ALT);
            }

        } catch (Exception ex) {

            log(ERROR, "Robot error exiting chrome");
        }

        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void pause(boolean b) {

        setPaused(b);

        try {

            Robot r = new Robot();

            r.keyPress(KeyEvent.VK_SPACE);
            r.keyRelease(KeyEvent.VK_SPACE);

        } catch (Exception ex) {

            log(ERROR, "Robot error hitting space");
        }
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
    public void next() {

        log(INFO, "chrome next");

        try {

            Robot r = new Robot();

            r.keyPress(KeyEvent.VK_ALT);
            r.keyPress(KeyEvent.VK_RIGHT);
            r.keyRelease(KeyEvent.VK_RIGHT);
            r.keyRelease(KeyEvent.VK_ALT);

        } catch (Exception ex) {

            log(ERROR, "Robot error next chrome");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void previous() {

        log(INFO, "chrome previous");
        try {

            Robot r = new Robot();

            r.keyPress(KeyEvent.VK_ALT);
            r.keyPress(KeyEvent.VK_LEFT);
            r.keyRelease(KeyEvent.VK_LEFT);
            r.keyRelease(KeyEvent.VK_ALT);

        } catch (Exception ex) {

            log(ERROR, "Robot error previous chrome");
        }
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

}


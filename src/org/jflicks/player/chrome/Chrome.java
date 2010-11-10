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

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.player.BasePlayer;
import org.jflicks.player.Bookmark;
import org.jflicks.player.PlayState;

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

            ChromeJob job = new ChromeJob(url);
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

        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();

            // As an added bonus - let's killall - of course only Linux.
            SystemJob job = SystemJob.getInstance("killall chrome");
            jc = JobManager.getJobContainer(job);
            jc.start();
        }
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

}


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

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.util.Util;

/**
 * This job starts a system job that runs chrome.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ChromeJob extends AbstractJob implements JobListener {

    private Chrome chrome;
    private SystemJob systemJob;
    private JobContainer jobContainer;
    private String url;

    /**
     * Constructor with one required argument.
     *
     * @param chrome The Chrome player instance.
     * @param url The url to the page to load.
     */
    public ChromeJob(Chrome chrome, String url) {

        setChrome(chrome);
        setURL(url);
        setSleepTime(2000);
    }

    private Chrome getChrome() {
        return (chrome);
    }

    private void setChrome(Chrome c) {
        chrome = c;
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

        Chrome c = getChrome();
        if ((c != null) && (message != null)) {

            c.log(level, message);
        }
    }

    /**
     * When we start the chrome system job we load the URL video from the
     * command line.
     *
     * @return The URL to the web page.
     */
    public String getURL() {
        return (url);
    }

    /**
     * When we start the chrome system job we load the URL video from the
     * command line.
     *
     * @param s The URL to the web page.
     */
    public void setURL(String s) {
        url = s;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        String prgname = "google-chrome";
        if (Util.isWindows()) {

            prgname = "chrome";
        }

        SystemJob job = SystemJob.getInstance(prgname
            + " -kiosk " + getURL());

        log(Chrome.DEBUG, "started: " + job.getCommand());
        job.addJobListener(this);
        setSystemJob(job);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();
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

        setTerminate(true);
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        fireJobEvent(event);
    }

}


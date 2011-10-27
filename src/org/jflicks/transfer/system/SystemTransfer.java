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
package org.jflicks.transfer.system;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.Timer;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.tv.Recording;
import org.jflicks.transfer.BaseTransfer;

/**
 * This is our implementation of a Transfer service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SystemTransfer extends BaseTransfer implements JobListener,
    ActionListener {

    private CurlTransferJob curlTransferJob;
    private JobContainer jobContainer;
    private Timer timer;
    private String maxRate;

    /**
     * Default empty constructor.
     */
    public SystemTransfer() {

        setTitle("SystemTransfer");
        Timer t = new Timer(60000, this);
        t.start();
        setTimer(t);
        setMaxRate("4m");
    }

    public String getMaxRate() {
        return (maxRate);
    }

    public void setMaxRate(String s) {
        maxRate = s;
    }

    private CurlTransferJob getCurlTransferJob() {
        return (curlTransferJob);
    }

    private void setCurlTransferJob(CurlTransferJob j) {
        curlTransferJob = j;
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer j) {
        jobContainer = j;
    }

    private Timer getTimer() {
        return (timer);
    }

    private void setTimer(Timer t) {
        timer = t;
    }

    public void actionPerformed(ActionEvent event) {

        cleanup();
    }

    /**
     * {@inheritDoc}
     */
    public String transfer(Recording r) {

        String result = null;

        log(DEBUG, "Recording: " + r);
        if (r != null) {

            // First thing we do is stop the last one if it exists.
            CurlTransferJob job = getCurlTransferJob();
            JobContainer jc = getJobContainer();
            if ((job != null) && (jc != null)) {

                job.setRecording(null);
                jc.stop();
                setCurlTransferJob(null);
                setJobContainer(null);
            }

            // Now start up the new one.
            File local = toFile(r);
            if (local != null) {

                result = local.getPath();

                job = new CurlTransferJob(r, local, getMaxRate());
                job.addJobListener(this);
                jc = JobManager.getJobContainer(job);
                setCurlTransferJob(job);
                setJobContainer(jc);
                jc.start();

                // Now we want to block until we have a file with some
                // data.
                boolean done = false;
                int maxwaits = 10;
                int waits = 0;
                while (!done) {

                    if ((local.exists()) && (local.isFile())
                        && (local.length() > 1024000L)) {

                        done = true;

                    } else {

                        waits++;
                        if (waits < maxwaits) {
                            JobManager.sleep(1000);
                        } else {
                            done = true;
                            result = null;
                        }
                    }
                }
            }
        }

        return (result);
    }

    public void close() {

        Timer t = getTimer();
        if (t != null) {

            t.stop();
        }

        cleanup();
    }

    private void cleanup() {

        // We are closing so let's just clean up all cache files.
        File here = new File(".");
        File tfile = new File(here, "transfer");
        if ((tfile.exists()) && (tfile.isDirectory())) {

            File[] files = tfile.listFiles();
            if ((files != null) && (files.length > 0)) {

                for (int i = 0; i < files.length; i++) {

                    // Get the modified time plus a day.
                    long modified = files[i].lastModified() + (3600000 * 24);
                    long now = System.currentTimeMillis();
                    if (now > modified) {

                        if (!files[i].delete()) {

                            log(WARNING, "Failed to delete "
                                + files[i].getPath());
                        }
                    }
                }
            }
        }
    }

    public File toFile(Recording r) {

        File result = null;

        if (r != null) {

            String path = r.getPath();
            if (path != null) {

                File fpath = new File(path);
                File here = new File(".");
                File tfile = new File(here, "transfer");
                if ((tfile.exists()) && (tfile.isDirectory())) {

                    result = new File(tfile, fpath.getName());
                }
            }
        }

        return (result);
    }

    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            setCurlTransferJob(null);
            setJobContainer(null);
            log(DEBUG, "curl job done!!");

        } else if (event.getType() == JobEvent.UPDATE) {

            log(DEBUG, event.getMessage());
        }
    }

}


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
package org.jflicks.tv.postproc.worker;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.util.Util;

/**
 * Transfer a file using curl.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ConcatJob extends AbstractJob implements JobListener {

    private SystemJob systemJob;
    private JobContainer jobContainer;
    private String prefix;
    private File directory;

    /**
     * Simple one argument constructor.
     *
     * @param prefix A given prefix as a String instance..
     * @param directory A given File.
     */
    public ConcatJob(String prefix, File directory) {

        setPrefix(prefix);
        setDirectory(directory);
    }

    /**
     * The local File to save the data.
     *
     * @return The File instance.
     */
    public File getDirectory() {
        return (directory);
    }

    private void setDirectory(File f) {
        directory = f;
    }

    /**
     * The prefix String.
     *
     * @return The input String.
     */
    public String getPrefix() {
        return (prefix);
    }

    private void setPrefix(String s) {
        prefix = s;
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

        File f = getDirectory();
        String pref = getPrefix();
        if ((f != null) && (pref != null)) {

            TSFileFilter tsff = new TSFileFilter(pref);
            File[] array = f.listFiles(tsff);
            if ((array != null) && (array.length > 0)) {

                Arrays.sort(array);
                StringBuilder sb = new StringBuilder();
                if (Util.isWindows()) {

                    sb.append("copy /b ");
                    for (int i = 0; i < array.length; i++) {

                        if (i > 0) {
                            sb.append(" + ");
                        }
                        sb.append(array[i].getName());
                    }
                    sb.append(" " + pref + ".ts");

                } else {

                    sb.append("cat ");
                    for (int i = 0; i < array.length; i++) {

                        sb.append(array[i].getName());
                        sb.append(" ");
                    }
                    sb.append(" >> " + pref + ".ts");
                }

                String command = sb.toString();

                SystemJob job = SystemJob.getInstance(command, f);
                fireJobEvent(JobEvent.UPDATE,
                    "command: <" + job.getCommand() + ">");
                setSystemJob(job);
                job.addJobListener(this);
                JobContainer jc = JobManager.getJobContainer(job);
                setJobContainer(jc);
                jc.start();

                while (!isTerminate()) {

                    JobManager.sleep(getSleepTime());
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
        JobContainer jc = getJobContainer();
        SystemJob job = getSystemJob();
        if ((jc != null) && (job != null)) {

            job.removeJobListener(this);
            jc.stop();
            setJobContainer(null);
            setSystemJob(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            setTerminate(true);
        }
    }

    private class TSFileFilter implements FileFilter {

        private String prefix;

        public TSFileFilter(String s) {
            prefix = s;
        }

        public boolean accept(File f) {

            boolean result = false;

            if ((prefix != null) && (f != null)) {

                String name = f.getName();
                if ((name.startsWith(prefix)) && (name.endsWith(".ts"))) {

                    result = true;
                }
            }

            return (result);
        }
    }

    public static void main(String[] args) {

        File dir = new File("/Users/djb/tmp/ggg");
        ConcatJob job = new ConcatJob(
            "EP014124480053_2013_11_13_01_00", dir);
        final JobContainer jc = JobManager.getJobContainer(job);
        jc.start();
    }

}

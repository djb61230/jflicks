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
package org.jflicks.tv.recorder.hdhr;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;

/**
 * This job finds the HDHR recorders on the local network.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DiscoverJob extends BaseHDHRJob {

    private ArrayList<String> idList;

    /**
     * Simple no argument constructor.
     */
    public DiscoverJob() {

        setIdList(new ArrayList<String>());
    }

    private ArrayList<String> getIdList() {
        return (idList);
    }

    private void setIdList(ArrayList<String> l) {
        idList = l;
    }

    private void addId(String s) {

        ArrayList<String> l = getIdList();
        if ((l != null) && (s != null)) {
            l.add(s);
        }
    }

    private void removeId(String s) {

        ArrayList<String> l = getIdList();
        if ((l != null) && (s != null)) {
            l.remove(s);
        }
    }

    private void clearIdList() {

        ArrayList<String> l = getIdList();
        if (l != null) {
            l.clear();
        }
    }

    /**
     * The array of HDHR ID values found on the network.  There will be one
     * ID for each HDHR found.
     *
     * @return An array od String instances representing HDHR ID values.
     */
    public String[] getIds() {

        String[] result = null;

        ArrayList<String> l = getIdList();
        if (l != null) {

            result = l.toArray(new String[l.size()]);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setTerminate(false);
        clearIdList();
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        SystemJob job = SystemJob.getInstance("hdhomerun_config discover");
        fireJobEvent(JobEvent.UPDATE, "command: <" + job.getCommand() + ">");
        setSystemJob(job);
        job.addJobListener(this);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());
        }

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();
            setJobContainer(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            SystemJob job = getSystemJob();
            if (job != null) {

                String output = job.getOutputText();
                setJobContainer(null);
                if (output != null) {

                     String[] array = output.split("\n");
                     if (array != null) {

                         for (int i = 0; i < array.length; i++) {

                             System.out.println("output line <" + array[i] + ">");
                             StringTokenizer st = new StringTokenizer(array[i]);
                             if (st.countTokens() > 3) {

                                 st.nextToken();
                                 st.nextToken();
                                 addId(st.nextToken());
                             }
                         }
                     }
                }
                stop();
            }
        }
    }

}

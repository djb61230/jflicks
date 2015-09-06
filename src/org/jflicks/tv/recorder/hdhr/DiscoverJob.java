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
import org.jflicks.util.LogUtil;

/**
 * This job finds the HDHR recorders on the local network.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DiscoverJob extends BaseHDHRJob {

    private ArrayList<String> idList;
    private ArrayList<String> ipList;
    private ArrayList<String> modelList;
    private ModelJob[] modelJobs;

    /**
     * Simple no argument constructor.
     */
    public DiscoverJob() {

        setIdList(new ArrayList<String>());
        setIpList(new ArrayList<String>());
        setModelList(new ArrayList<String>());
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

    private ArrayList<String> getIpList() {
        return (ipList);
    }

    private void setIpList(ArrayList<String> l) {
        ipList = l;
    }

    private void addIp(String s) {

        ArrayList<String> l = getIpList();
        if ((l != null) && (s != null)) {
            l.add(s);
        }
    }

    private void removeIp(String s) {

        ArrayList<String> l = getIpList();
        if ((l != null) && (s != null)) {
            l.remove(s);
        }
    }

    private void clearIpList() {

        ArrayList<String> l = getIpList();
        if (l != null) {
            l.clear();
        }
    }

    private ArrayList<String> getModelList() {
        return (modelList);
    }

    private void setModelList(ArrayList<String> l) {
        modelList = l;
    }

    private void addModel(String s) {

        ArrayList<String> l = getModelList();
        if ((l != null) && (s != null)) {
            l.add(s);
        }
    }

    private void removeModel(String s) {

        ArrayList<String> l = getModelList();
        if ((l != null) && (s != null)) {
            l.remove(s);
        }
    }

    private void clearModelList() {

        ArrayList<String> l = getModelList();
        if (l != null) {
            l.clear();
        }
    }

    /**
     * The array of HDHR ID values found on the network.  There will be one
     * ID for each HDHR found.
     *
     * @return An array of String instances representing HDHR ID values.
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
     * The array of HDHR IP addresses found on the network.  There will be one
     * IP address for each HDHR found.
     *
     * @return An array of String instances representing HDHR IP addresses.
     */
    public String[] getIps() {

        String[] result = null;

        ArrayList<String> l = getIpList();
        if (l != null) {

            result = l.toArray(new String[l.size()]);
        }

        return (result);
    }

    /**
     * The array of HDHR models found on the network.  There will be one
     * model for each HDHR found.
     *
     * @return An array of String instances representing HDHR model value.
     */
    public String[] getModels() {

        String[] result = null;

        ArrayList<String> l = getModelList();
        if (l != null) {

            result = l.toArray(new String[l.size()]);
        }

        return (result);
    }

    private ModelJob[] getModelJobs() {
        return (modelJobs);
    }

    private void setModelJobs(ModelJob[] array) {
        modelJobs = array;
    }

    private int findNextIdIndex(String s) {

        int result = -1;

        String[] all = getIds();
        if ((s != null) && (all != null) && (all.length > 0)) {

            for (int i = 0; i < all.length; i++) {

                if (s.equals(all[i])) {

                    result = i + 1;
                    break;
                }
            }

            // See if we are done.
            if (result == all.length) {
                result = -1;
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setTerminate(false);
        clearIdList();
        clearIpList();
        clearModelList();
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

                             LogUtil.log(LogUtil.DEBUG, "output line <" + array[i] + ">");
                             StringTokenizer st = new StringTokenizer(array[i]);
                             if (st.countTokens() > 3) {

                                 st.nextToken();
                                 st.nextToken();
                                 addId(st.nextToken());
                                 st.nextToken();
                                 st.nextToken();
                                 addIp(st.nextToken());
                             }
                         }
                     }
                }

                // At this point we know all the HDHRs on the network.  Now we have to check
                // the model for each one.  We set SystemJob to null so we know we are in finding
                // the models mode.
                setSystemJob(null);

                String[] all = getIds();
                if ((all != null) && (all.length > 0)) {

                    ModelJob[] mjobs = new ModelJob[all.length];
                    for (int i = 0; i < mjobs.length; i++) {

                        mjobs[i] = new ModelJob(all[i]);
                    }

                    setModelJobs(mjobs);

                    // Now start the first ModelJob.  We will do them one at a time.
                    mjobs[0].addJobListener(this);
                    JobContainer jc = JobManager.getJobContainer(mjobs[0]);
                    setJobContainer(jc);
                    jc.start();

                } else {

                    // Didn't find any so just stop.
                    stop();
                }

            } else {

                // A ModelJob has just finished.
                ModelJob mj = (ModelJob) event.getSource();
                addModel(mj.getModel());
                int next = findNextIdIndex(mj.getId());
                if (next != -1) {

                    // Now start the next ModelJob.
                    ModelJob[] mjobs = getModelJobs();
                    mjobs[next].addJobListener(this);
                    JobContainer jc = JobManager.getJobContainer(mjobs[next]);
                    setJobContainer(jc);
                    jc.start();

                } else {

                    stop();
                }
            }
        }
    }

}

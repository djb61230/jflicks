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
package org.jflicks.tv.recorder.v4l2;

import org.jflicks.configure.BaseConfiguration;
import org.jflicks.configure.NameValue;
import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.nms.NMSConstants;

/**
 * This job will examine a V4l2 device and create a default properties
 * file for it.  The properties in the file will be dependent on the
 * values returned from a few calls to v4l2-ctl.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class CreatePropertiesJob extends AbstractJob implements JobListener {

    private V4l2Recorder v4l2Recorder;
    private ListAudioInputJob listAudioInputJob;
    private ListVideoInputJob listVideoInputJob;
    private ListControlMenuJob listControlMenuJob;
    private JobContainer jobContainer;
    private BaseConfiguration baseConfiguration;

    /**
     * This job supports V4l2 devices.
     *
     * @param r A given V4l2Recorder instance.
     */
    public CreatePropertiesJob(V4l2Recorder r) {

        setV4l2Recorder(r);
    }

    private ListAudioInputJob getListAudioInputJob() {
        return (listAudioInputJob);
    }

    private void setListAudioInputJob(ListAudioInputJob j) {
        listAudioInputJob = j;
    }

    private ListVideoInputJob getListVideoInputJob() {
        return (listVideoInputJob);
    }

    private void setListVideoInputJob(ListVideoInputJob j) {
        listVideoInputJob = j;
    }

    private ListControlMenuJob getListControlMenuJob() {
        return (listControlMenuJob);
    }

    private void setListControlMenuJob(ListControlMenuJob j) {
        listControlMenuJob = j;
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer jc) {
        jobContainer = jc;
    }

    /**
     * We act upon a V4l2Recorder instance.
     *
     * @return A V4l2Recorder instance that is used to generate a file.
     */
    public V4l2Recorder getV4l2Recorder() {
        return (v4l2Recorder);
    }

    /**
     * We act upon a V4l2Recorder instance.
     *
     * @param r A V4l2Recorder instance that is used to generate a file.
     */
    public void setV4l2Recorder(V4l2Recorder r) {
        v4l2Recorder = r;
    }

    private BaseConfiguration getBaseConfiguration() {
        return (baseConfiguration);
    }

    private void setBaseConfiguration(BaseConfiguration bc) {
        baseConfiguration = bc;
    }

    private String getDevice() {

        String result = null;

        V4l2Recorder r = getV4l2Recorder();
        if (r != null) {

            result = r.getDevice();
        }

        return (result);
    }

    private void log(int status, String message) {

        V4l2Recorder r = getV4l2Recorder();
        if ((r != null) && (message != null)) {

            r.log(status, message);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        String device = getDevice();
        if (device != null) {

            setTerminate(false);

            setBaseConfiguration(new BaseConfiguration());

            ListAudioInputJob laij = new ListAudioInputJob();
            laij.setDevice(device);
            setListAudioInputJob(laij);
            laij.addJobListener(this);

            ListVideoInputJob lvij = new ListVideoInputJob();
            lvij.setDevice(device);
            setListVideoInputJob(lvij);
            lvij.addJobListener(this);

            ListControlMenuJob lcmj = new ListControlMenuJob();
            lcmj.setDevice(device);
            setListControlMenuJob(lcmj);
            lcmj.addJobListener(this);

            JobContainer jc = JobManager.getJobContainer(laij);
            setJobContainer(jc);
            jc.start();

        } else {

            setTerminate(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

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
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            if (event.getSource() == getListAudioInputJob()) {

                ListAudioInputJob job = getListAudioInputJob();
                BaseConfiguration bc = getBaseConfiguration();
                if (bc != null) {

                    bc.addNameValue(job.getNameValue());
                }

                JobContainer jc =
                    JobManager.getJobContainer(getListVideoInputJob());
                setJobContainer(jc);
                jc.start();

            } else if (event.getSource() == getListVideoInputJob()) {

                ListVideoInputJob job = getListVideoInputJob();
                BaseConfiguration bc = getBaseConfiguration();
                if (bc != null) {

                    bc.addNameValue(job.getNameValue());
                }

                JobContainer jc =
                    JobManager.getJobContainer(getListControlMenuJob());
                setJobContainer(jc);
                jc.start();

            } else if (event.getSource() == getListControlMenuJob()) {

                ListControlMenuJob job = getListControlMenuJob();
                BaseConfiguration bc = getBaseConfiguration();
                if (bc != null) {

                    NameValue nv = new NameValue();
                    nv.setName(NMSConstants.FREQUENCY_TABLE_NAME);
                    nv.setType(NameValue.STRING_FROM_CHOICE_TYPE);
                    String[] choices = {
                        "us-cable",
                        "us-cable-hrc",
                        "us-cable-irc"
                    };

                    nv.setDefaultValue(choices[0]);
                    nv.setValue(choices[0]);
                    nv.setChoices(choices);
                    nv.setDescription(NMSConstants.FREQUENCY_TABLE_NAME);
                    bc.addNameValue(nv);

                    NameValue[] array = job.getNameValues();
                    if (array != null) {

                        for (int i = 0; i < array.length; i++) {

                            bc.addNameValue(array[i]);
                        }
                    }

                    V4l2Recorder r = getV4l2Recorder();
                    if (r != null) {

                        bc.setName(NMSConstants.RECORDER_NAME);
                        bc.setSource(r.getTitle());
                        NameValue ccnv = new NameValue();
                        ccnv.setName(NMSConstants.CHANGE_CHANNEL_SCRIPT_NAME);
                        ccnv.setType(NameValue.STRING_TYPE);
                        bc.addNameValue(ccnv);
                        r.write(bc);
                    }
                }

                stop();
            }
        }
    }

}

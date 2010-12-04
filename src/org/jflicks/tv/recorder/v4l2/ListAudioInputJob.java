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

import java.util.ArrayList;

import org.jflicks.configure.NameValue;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.recorder.BaseDeviceJob;

/**
 * This job finds the audio inputs on a V4l2 device.  It runs the v4l2-ctl
 * program with the --list-audio-inputs argument and creates a NameValue
 * instance with the possible audio inputs as a choice list.  It does make
 * an assumption that the inputs are returned by the v4l2-ctl in "order".
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ListAudioInputJob extends BaseDeviceJob {

    private static final String NAME = "Name";

    private NameValue nameValue;

    /**
     * Simple no argument constructor.
     */
    public ListAudioInputJob() {
    }

    /**
     * The audio inputs choices are stored in a NameValue instance.
     *
     * @return A NameValue instance.
     */
    public NameValue getNameValue() {
        return (nameValue);
    }

    private void setNameValue(NameValue nv) {
        nameValue = nv;
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

        SystemJob job = SystemJob.getInstance("v4l2-ctl -d " + getDevice()
            + " --list-audio-inputs");
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

                        ArrayList<String> choices = new ArrayList<String>();
                        NameValue nv = new NameValue();
                        nv.setName(NMSConstants.AUDIO_INPUT_NAME);
                        nv.setDescription(NMSConstants.AUDIO_INPUT_NAME);
                        nv.setType(NameValue.STRING_FROM_CHOICE_TYPE);
                        for (int i = 0; i < array.length; i++) {

                            String line = array[i];
                            line = line.trim();
                            int index = line.indexOf(":") + 1;

                            if (index != 0) {

                                String tag = line.substring(0, index - 1);
                                if (tag != null) {
                                    tag = tag.trim();
                                }

                                String val = line.substring(index);
                                if (val != null) {
                                    val = val.trim();
                                }

                                // We do have a tag/value pair.  We check if
                                // the tag is NAME and only handle it.
                                if (tag.equals(NAME)) {

                                    choices.add(val);
                                }
                            }
                        }

                        if (choices.size() > 0) {

                            String[] data =
                                choices.toArray(new String[choices.size()]);
                            nv.setChoices(data);
                            nv.setDefaultValue(data[0]);
                            nv.setValue(data[0]);

                            setNameValue(nv);
                        }
                    }
                }

                stop();
            }
        }
    }

}

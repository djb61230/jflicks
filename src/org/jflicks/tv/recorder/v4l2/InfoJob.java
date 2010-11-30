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

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;

/**
 * This job runs the v4l2-ctl program with the --info argument, then
 * parses the output and keeps important information in it's properties.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class InfoJob extends BaseV4l2Job {

    private static final String DRIVER_INFO = "Driver Info";
    private static final String DRIVER_NAME = "Driver name";
    private static final String CARD_TYPE = "Card type";
    private static final String BUS_INFO = "Bus info";
    private static final String DRIVER_VERSION = "Driver version";
    private static final String CAPABILITIES = "Capabilities";

    private V4l2Device v4l2Device;

    /**
     * Simple no argument constructor.
     */
    public InfoJob() {
    }

    /**
     * The video for linux device.
     *
     * @return A V4l2Device instance.
     */
    public V4l2Device getV4l2Device() {
        return (v4l2Device);
    }

    private void setV4l2Device(V4l2Device d) {
        v4l2Device = d;
    }

    private void applyDriverName(String s) {

        V4l2Device d = getV4l2Device();
        if (d != null) {

            d.setDriverName(s);
        }
    }

    private void applyCardType(String s) {

        V4l2Device d = getV4l2Device();
        if (d != null) {

            d.setCardType(s);
        }
    }

    private void applyBusInfo(String s) {

        V4l2Device d = getV4l2Device();
        if (d != null) {

            d.setBusInfo(s);
        }
    }

    private void applyDriverVersion(String s) {

        V4l2Device d = getV4l2Device();
        if (d != null) {

            d.setDriverVersion(s);
        }
    }

    private void applyCapabilitiesMask(String s) {

        V4l2Device d = getV4l2Device();
        if (d != null) {

            d.setCapabilitiesMask(s);
        }
    }

    private void addCapability(String s) {

        V4l2Device d = getV4l2Device();
        if ((d != null) && (s != null)) {

            d.addCapability(s);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        V4l2Device d = new V4l2Device();
        d.setPath(getDevice());
        setV4l2Device(d);
        setTerminate(false);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        SystemJob job = SystemJob.getInstance("v4l2-ctl -d " + getDevice()
            + " --info");
        fireJobEvent(JobEvent.UPDATE, "command <" + job.getCommand() + ">");
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

                            String line = array[i];
                            line = line.trim();
                            int index = line.indexOf(":") + 1;
                            String tmp = line.substring(index);
                            if (tmp != null) {
                                tmp = tmp.trim();
                            }
                            if (line.startsWith(DRIVER_NAME)) {

                                applyDriverName(tmp);

                            } else if (line.startsWith(CARD_TYPE)) {

                                applyCardType(tmp);

                            } else if (line.startsWith(BUS_INFO)) {

                                applyBusInfo(tmp);

                            } else if (line.startsWith(DRIVER_VERSION)) {

                                applyDriverVersion(tmp);

                            } else if (line.startsWith(CAPABILITIES)) {

                                applyCapabilitiesMask(tmp);

                            } else {

                                if (!line.startsWith(DRIVER_INFO)) {
                                    addCapability(tmp);
                                }
                            }
                        }
                    }
                }

                stop();
            }
        }
    }

}

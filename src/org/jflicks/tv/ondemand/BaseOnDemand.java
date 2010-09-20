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
package org.jflicks.tv.ondemand;

import java.io.File;
import java.util.ArrayList;

import org.jflicks.configure.BaseConfig;
import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.Video;
import org.jflicks.tv.recorder.Recorder;
import org.jflicks.util.Util;

/**
 * This class is a base implementation of the OnDemand interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseOnDemand extends BaseConfig implements OnDemand {

    private String title;
    private NMS nms;

    /**
     * Simple empty constructor.
     */
    public BaseOnDemand() {
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return (title);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given title value.
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * {@inheritDoc}
     */
    public NMS getNMS() {
        return (nms);
    }

    /**
     * {@inheritDoc}
     */
    public void setNMS(NMS n) {
        nms = n;
    }

    /**
     * Convenience method to get the configured Recorder Source.
     *
     * @return A directory path as a String.
     */
    public String getConfiguredRecorderSource() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv =
                c.findNameValueByName(NMSConstants.RECORDING_DEVICE);
            if (nv != null) {

                result = nv.getValue();
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Recorder getRecorder() {

        Recorder result = null;

        String source = getConfiguredRecorderSource();
        NMS n = getNMS();
        if ((source != null) && (n != null)) {

            String device = source.substring(source.lastIndexOf(" "));
            device = device.trim();
            result = n.getRecorderByDevice(device);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public StreamSession openSession(String host, int port) {

        StreamSession result = null;

        System.out.println("openSession: " + host);
        System.out.println("openSession: " + port);
        if (host != null) {

            Recorder r = getRecorder();
            NMS n = getNMS();
            System.out.println("openSession: " + r);
            if ((n != null) && (r != null) && (!r.isRecording())) {

                result = new StreamSession(getTitle(), n.getHost() + ":"
                    + n.getPort());

                r.startStreaming(null, host, port);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void closeSession(StreamSession ss) {

        Recorder r = getRecorder();
        if (r != null) {

            r.stopStreaming();
        }
    }

    /**
     * Convenience method to get the configured value of HOST, if an
     * OnDemand service defines one.
     *
     * @return A String object.
     */
    public String getConfiguredHost() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(NMSConstants.HOST);
            if (nv != null) {

                result = nv.getValue();
            }
        }

        return (result);
    }

    /**
     * Convenience method to get the configured value of PORT, if an
     * OnDemand service defines one.
     *
     * @return A String object.
     */
    public int getConfiguredPort() {

        int result = 8080;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(NMSConstants.PORT);
            if (nv != null) {

                result = Util.str2int(nv.getValue(), result);
            }
        }

        return (result);
    }

}


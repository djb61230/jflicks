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
package org.jflicks.tv.recorder.jhdhr;

import java.io.Serializable;

/**
 * A class to interact with your HDHR devices on the network.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class HDHRDevice implements Serializable {

    private String id;
    private int tuner;
    private String ip;
    private String model;
    private int plan;
    private String planDescription;
    private boolean transcodeToMp4;
    private boolean wantToUse;

    /**
     * Simple four argument constructor.
     *
     * @param id The ID of the device.
     * @param tuner The tuner number, 0 or 1.
     * @param ip The IP address of the device.
     * @param model The model of the hardware.
     */
    public HDHRDevice(String id, int tuner, String ip, String model) {

        setId(id);
        setTuner(tuner);
        setIp(ip);
        setModel(model);
        setTranscodeToMp4(!isHDTC());
        setWantToUse(true);
    }

    /**
     * Simple no argument constructor.
     */
    public HDHRDevice() {

        setTranscodeToMp4(true);
        setWantToUse(true);
    }

    public String getId() {
        return (id);
    }

    public void setId(String s) {
        id = s;
    }

    public int getTuner() {
        return (tuner);
    }

    public void setTuner(int i) {
        tuner = i;
    }

    public String getIp() {
        return (ip);
    }

    public void setIp(String s) {
        ip = s;
    }

    public String getModel() {
        return (model);
    }

    public void setModel(String s) {
        model = s;

        if (model != null) {

            if (model.equalsIgnoreCase("hdhomeruntc_atsc")) {

                setPlan(0);

            } else {

                setPlan(1);
            }
        }
    }

    public boolean isTranscodeToMp4() {
        return (transcodeToMp4);
    }

    public void setTranscodeToMp4(boolean b) {
        transcodeToMp4 = b;
    }

    public boolean isWantToUse() {
        return (wantToUse);
    }

    public void setWantToUse(boolean b) {
        wantToUse = b;
    }

    public int getPlan() {
        return (plan);
    }

    public void setPlan(int i) {
        plan = i;
    }

    public String getPlanDescription() {

        String result = null;

        switch (plan) {

        default:
        case 0:
            result = "This will produce mpeg4 video.";
            break;
        case 1:
            result = "This will produce mpeg2 video.";
            break;
        }

        return (result);
    }

    public boolean isHDTC() {

        boolean result = false;

        if (model != null) {

            return (model.equalsIgnoreCase("hdhomeruntc_atsc"));
        }

        return (result);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        if (id != null) {

            sb.append("id : ");
            sb.append(id);

        } else {

            sb.append("id : -");
        }

        if (ip != null) {

            sb.append(" ip : ");
            sb.append(ip);

        } else {

            sb.append(" ip : -");
        }

        sb.append(" tuner : ");
        sb.append(tuner);

        if (model != null) {

            sb.append(" model : ");
            sb.append(model);

        } else {

            sb.append(" model : -");
        }

        return (sb.toString());
    }

}

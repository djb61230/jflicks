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
package org.jflicks.tv.programdata.sd.json;

import java.io.Serializable;

/**
 * A class to capture the JSON defining a station.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class StationID implements Serializable {

    private String stationID;
    private int uhfVhf;
    private int atscMajor;
    private int atscMinor;
    private String channel;

    /**
     * Simple empty constructor.
     */
    public StationID() {
    }

    public String getStationID() {
        return (stationID);
    }

    public void setStationID(String s) {
        stationID = s;
    }

    public int getUhfVhf() {
        return (uhfVhf);
    }

    public void setUhfVhf(int i) {
        uhfVhf = i;
    }

    public int getAtscMajor() {
        return (atscMajor);
    }

    public void setAtscMajor(int i) {
        atscMajor = i;
    }

    public int getAtscMinor() {
        return (atscMinor);
    }

    public void setAtscMinor(int i) {
        atscMinor = i;
    }

    public String getChannel() {
        return (channel);
    }

    public void setChannel(String s) {
        channel = s;
    }

}


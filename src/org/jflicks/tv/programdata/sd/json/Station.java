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
public class Station implements Serializable {

    private String callsign;
    private String channel;
    private String name;
    private String broadcastLanguage;
    private String descriptionLanguage;
    private String stationID;

    /**
     * Simple empty constructor.
     */
    public Station() {
    }

    public String getCallsign() {
        return (callsign);
    }

    public void setCallsign(String s) {
        callsign = s;
    }

    public String getName() {
        return (name);
    }

    public void setName(String s) {
        name = s;
    }

    public String getChannel() {
        return (channel);
    }

    public void setChannel(String s) {
        channel = s;
    }

    public String getBroadcastLanguage() {
        return (broadcastLanguage);
    }

    public void setBroadcastLanguage(String s) {
        broadcastLanguage = s;
    }

    public String getDescriptionLanguage() {
        return (descriptionLanguage);
    }

    public void setDescriptionLanguage(String s) {
        descriptionLanguage = s;
    }

    public String getStationID() {
        return (stationID);
    }

    public void setStationID(String s) {
        stationID = s;
    }

    public String toString() {

        return getStationID() + ":" + getName();
    }

}


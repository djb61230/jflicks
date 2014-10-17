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
 * A class to capture the JSON defining a schedule.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class StationSchedule implements Serializable {

    private String stationID;
    private Program[] programs;
    private Metadata metadata;

    /**
     * Simple empty constructor.
     */
    public StationSchedule() {
    }

    public String getStationID() {
        return (stationID);
    }

    public void setStationID(String s) {
        stationID = s;
    }

    public Program[] getPrograms() {
        return (programs);
    }

    public void setPrograms(Program[] array) {
        programs = array;
    }

    public Metadata getMetadata() {
        return (metadata);
    }

    public void setMetadata(Metadata m) {
        metadata = m;
    }

}


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
public class Mapping implements Serializable {

    private StationID[] map;
    private Station[] stations;
    private Metadata metadata;

    /**
     * Simple empty constructor.
     */
    public Mapping() {
    }

    public StationID[] getMap() {
        return (map);
    }

    public void setMap(StationID[] array) {
        map = array;
    }

    public Station[] getStations() {
        return (stations);
    }

    public void setStations(Station[] array) {
        stations = array;
    }

    public Metadata getMetadata() {
        return (metadata);
    }

    public void setMetadata(Metadata m) {
        metadata = m;
    }

    public StationID getStationID(String id) {

        StationID result = null;

        StationID[] array = getMap();
        if ((id != null) && (array != null) && (array.length > 0)) {

            for (int i = 0; i < array.length; i++) {

                if (id.equals(array[i].getStationID())) {

                    result = array[i];
                    break;
                }
            }
        }

        return (result);
    }

}


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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class to capture the JSON defining a station.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class GuideRequest implements Serializable {

    private String stationID;
    private String[] date;

    /**
     * Simple empty constructor.
     */
    public GuideRequest() {

        // Next we set the date array for the next 13 days.
        String[] array = new String[13];

        // First do today.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date tmp = new Date();
        array[0] = sdf.format(tmp);
        for (int i = 1; i < array.length; i++) {

            tmp = new Date(tmp.getTime() + 86400000L);
            array[i] = sdf.format(tmp);
        }

        setDate(array);
    }

    public GuideRequest(String s) {

        this();
        setStationID(s);
    }

    public String getStationID() {
        return (stationID);
    }

    public void setStationID(String s) {
        stationID = s;
    }

    public String[] getDate() {
        return (date);
    }

    public void setDate(String[] array) {
        date = array;
    }

}


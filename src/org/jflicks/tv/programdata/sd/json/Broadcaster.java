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
import java.util.StringTokenizer;

/**
 * A class to capture the JSON defining a lineup.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Broadcaster implements Serializable {

    private String city;
    private String state;
    private String postalcode;
    private String country;

    /**
     * Simple empty constructor.
     */
    public Broadcaster() {
    }

    public String getCity() {
        return (city);
    }

    public void setCity(String s) {
        city = s;
    }

    public String getState() {
        return (state);
    }

    public void setState(String s) {
        state = s;
    }

    public String getPostalcode() {
        return (postalcode);
    }

    public void setPostalcode(String s) {
        postalcode = s;
    }

    public String getCountry() {
        return (country);
    }

    public void setCountry(String s) {
        country = s;
    }

}


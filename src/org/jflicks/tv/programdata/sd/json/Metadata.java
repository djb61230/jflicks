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
 * A class to capture the JSON defining some metadata.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Metadata implements Serializable {

    private String lineup;
    private String modified;
    private String transport;
    private String md5;
    private String startDate;
    private String endDate;
    private int days;
    private Tribune tribune;

    /**
     * Simple empty constructor.
     */
    public Metadata() {
    }

    public String getLineup() {
        return (lineup);
    }

    public void setLineup(String s) {
        lineup = s;
    }

    public String getModified() {
        return (modified);
    }

    public void setModified(String s) {
        modified = s;
    }

    public String getTransport() {
        return (transport);
    }

    public void setTransport(String s) {
        transport = s;
    }

    public String getMd5() {
        return (md5);
    }

    public void setMd5(String s) {
        md5 = s;
    }

    public String getStartDate() {
        return (startDate);
    }

    public void setStartDate(String s) {
        startDate = s;
    }

    public String getEndDate() {
        return (endDate);
    }

    public void setEndDate(String s) {
        endDate = s;
    }

    public int getDays() {
        return (days);
    }

    public void setDays(int i) {
        days = i;
    }

    public Tribune getTribune() {
        return (tribune);
    }

    public void setTribune(Tribune t) {
        tribune = t;
    }

}


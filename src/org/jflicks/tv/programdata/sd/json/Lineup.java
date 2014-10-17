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
public class Lineup implements Serializable {

    private String id;
    private String name;
    private String type;
    private String location;
    private String modified;
    private String uri;

    /**
     * Simple empty constructor.
     */
    public Lineup() {
    }

    public String getId() {

        String result = id;

        if (id == null) {

            // Ok here we hack because the listing id
            // is not nicely it's own field in the json
            // data.
            if (type != null) {

                if (type.equalsIgnoreCase("antenna")) {
                    result = location;
                } else if (uri != null) {

                    // We take the middle token out of the URL;
                    int index = uri.lastIndexOf("/");
                    String tmp = uri.substring(index + 1);
                    StringTokenizer st = new StringTokenizer(tmp, "-");
                    result = st.nextToken();
                    result = st.nextToken();
                }

            } else {

                result = name;
            }
        }

        return (result);
    }

    public void setId(String s) {
        id = s;
    }

    public String getName() {
        return (name);
    }

    public void setName(String s) {
        name = s;
    }

    public String getType() {
        return (type);
    }

    public void setType(String s) {
        type = s;
    }

    public String getLocation() {
        return (location);
    }

    public void setLocation(String s) {
        location = s;
    }

    public String getModified() {
        return (modified);
    }

    public void setModified(String s) {
        modified = s;
    }

    public String getUri() {
        return (uri);
    }

    public void setUri(String s) {
        uri = s;
    }

    public String toString() {

        String result = null;

        StringBuilder sb = new StringBuilder(getName());
        String tmp = getType();
        if (tmp != null) {

            sb.append("-");
            sb.append(tmp);
        }
        tmp = getLocation();
        if (tmp != null) {

            sb.append("-");
            sb.append(tmp);
        }
        result = sb.toString();
        result = result.replaceAll(" ", "-");

        return (result);
    }

}


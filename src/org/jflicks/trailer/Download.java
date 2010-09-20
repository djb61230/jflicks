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
package org.jflicks.trailer;

import java.io.Serializable;

/**
 * This class contains all the properties representing a Download.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Download implements Serializable {

    private String id;
    private String title;
    private String url;

    /**
     * Simple empty constructor.
     */
    public Download() {
    }

    /**
     * A unique ID is associated with this object.
     *
     * @return An ID value as a String.
     */
    public String getId() {
        return (id);
    }

    /**
     * A unique ID is associated with this object.
     *
     * @param s An ID value as a String.
     */
    public void setId(String s) {
        id = s;
    }

    /**
     * A Download has an associated title.
     *
     * @return The title.
     */
    public String getTitle() {
        return (title);
    }

    /**
     * A Download has an associated title.
     *
     * @param s The title.
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * A Download has an associated URL.
     *
     * @return The URL.
     */
    public String getUrl() {
        return (url);
    }

    /**
     * A Download has an associated URL.
     *
     * @param s The URL.
     */
    public void setUrl(String s) {
        url = s;
    }

}


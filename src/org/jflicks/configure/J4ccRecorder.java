/*
    This file is part of JFLICKS.

    JFLICKS is free software: you can redistribute it and/or modify
    it under the terms of the GNU J4cc Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JFLICKS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU J4cc Public License for more details.

    You should have received a copy of the GNU J4cc Public License
    along with JFLICKS.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.jflicks.configure;

import java.io.Serializable;

/**
 * This class has the fields for a general configuration of an j4cc
 * installation covering the most common things a user would like to
 * configure.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class J4ccRecorder implements Serializable {

    private String name;
    private String listing;
    private boolean hdtc;
    private boolean transcode;

    /**
     * Simple empty constructor.
     */
    public J4ccRecorder() {
    }

    public String getName() {
        return (name);
    }

    public void setName(String s) {
        name = s;
    }

    public String getListing() {
        return (listing);
    }

    public void setListing(String s) {
        listing = s;
    }

    public boolean isHDTC() {
        return (hdtc);
    }

    public void setHDTC(boolean b) {
        hdtc = b;
    }

    public boolean isTranscode() {
        return (transcode);
    }

    public void setTranscode(boolean b) {
        transcode = b;
    }

}


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
public class J4ccConfiguration implements Serializable {

    private String userName;
    private String password;
    private String zipCode;
    private String[] paths;
    private String[] listings;
    private J4ccRecorder[] j4ccRecorders;

    /**
     * Simple empty constructor.
     */
    public J4ccConfiguration() {
    }

    public String getUserName() {
        return (userName);
    }

    public void setUserName(String s) {
        userName = s;
    }

    public String getPassword() {
        return (password);
    }

    public void setPassword(String s) {
        password = s;
    }

    public String getZipCode() {
        return (zipCode);
    }

    public void setZipCode(String s) {
        zipCode = s;
    }

    public String[] getPaths() {
        return (paths);
    }

    public void setPaths(String[] array) {
        paths = array;
    }

    public String[] getListings() {
        return (listings);
    }

    public void setListings(String[] array) {
        listings = array;
    }

    public J4ccRecorder[] getJ4ccRecorders() {
        return (j4ccRecorders);
    }

    public void setJ4ccRecorders(J4ccRecorder[] array) {
        j4ccRecorders = array;
    }

}


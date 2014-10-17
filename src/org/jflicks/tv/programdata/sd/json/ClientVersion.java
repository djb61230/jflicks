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
 * A class to capture the JSON from checking for the correct version of
 * the jflicks client.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ClientVersion implements Serializable {

    private String response;
    private int code;
    private String client;
    private String version;
    private String serverID;
    private String datetime;
    private String message;

    /**
     * Simple empty constructor.
     */
    public ClientVersion() {
    }

    public String getResponse() {
        return (response);
    }

    public void setResponse(String s) {
        response = s;
    }

    public int getCode() {
        return (code);
    }

    public void setCode(int i) {
        code = i;
    }

    public String getClient() {
        return (client);
    }

    public void setClient(String s) {
        client = s;
    }

    public String getVersion() {
        return (version);
    }

    public void setVersion(String s) {
        version = s;
    }

    public String getServerID() {
        return (serverID);
    }

    public void setServerID(String s) {
        serverID = s;
    }

    public String getDatetime() {
        return (datetime);
    }

    public void setDatetime(String s) {
        datetime = s;
    }

    public String getMessage() {
        return (message);
    }

    public void setMessage(String s) {
        message = s;
    }

}


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
package org.jflicks.tv.ondemand;

import java.io.Serializable;

/**
 * This class contains all the properties representing a on demand session for
 * the server side.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class StreamSession implements Serializable {

    private String name;
    private String hostPort;

    /**
     * Constructor with two required arguments.
     *
     * @param name The name of this session.
     * @param hostPort A way for a client to find the NMS.
     */
    public StreamSession(String name, String hostPort) {

        setName(name);
        setHostPort(hostPort);
    }

    /**
     * A name for this session.
     *
     * @return A String instance.
     */
    public String getName() {
        return (name);
    }

    private void setName(String s) {
        name = s;
    }

    /**
     * String in the format of host:port that can be parsed to see what
     * NMS instance this session is associated.
     *
     * @return A String instance.
     */
    public String getHostPort() {
        return (hostPort);
    }

    private void setHostPort(String s) {
        hostPort = s;
    }

}


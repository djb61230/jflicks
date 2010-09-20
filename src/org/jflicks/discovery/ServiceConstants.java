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
package org.jflicks.discovery;

/**
 * A simple class to define some constants.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class ServiceConstants {

    private ServiceConstants() {
    }

    /**
     * The multicast address that both client and server will use.
     */
    public static final String MULTICAST_INET_ADDRESS = "230.0.0.1";

    /**
     * The multicast port that both client and server will use.
     */
    public static final int MULTICAST_PORT = 4321;

    /**
     * The datagram length that both client and server will use.
     */
    public static final int DATAGRAM_LENGTH = 1024;

    /**
     * The service responder timeout value.
     */
    public static final int RESPONDER_SOCKET_TIMEOUT = 1250;

    /**
     * The service client timeout value.
     */
    public static final int BROWSER_SOCKET_TIMEOUT = 1250;

    /**
     * How often the client will search for services.
     */
    public static final int BROWSER_QUERY_INTERVAL = 5000;

}

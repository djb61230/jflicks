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
package org.jflicks.nms;

/**
 * A utility class with helpful methods when dealing with NMS implementations.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class NMSUtil {

    private NMSUtil() {
    }

    /**
     * Convenience method to find the NMS defined by the host:port in the
     * array.
     *
     * @param array An array of NMS instances.
     * @param hostPort A host:port String instance.
     * @return An NMS instance if it exists in the given array.
     */
    public static NMS select(NMS[] array, String hostPort) {

        NMS result = null;

        if ((array != null) && (hostPort != null)) {

            for (int i = 0; i < array.length; i++) {

                String tmp = array[i].getHost() + ":" + array[i].getPort();
                if (hostPort.equals(tmp)) {

                    result = array[i];
                    break;
                }
            }
        }

        return (result);
    }

}


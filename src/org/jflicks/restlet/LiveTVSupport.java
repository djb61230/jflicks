/*
    This file is part of ATM.

    ATM is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ATM is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ATM.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.jflicks.restlet;

import org.jflicks.nms.NMS;

/**
 * This class is a singleton to handle state of LiveTV support
 * vis REST.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class LiveTVSupport {

    private static LiveTVSupport instance = new LiveTVSupport();

    private NMS[] nms;

    /**
     * Default empty constructor.
     */
    private LiveTVSupport() {
    }

    /**
     * We are a singleton, so users need access to it.
     *
     * @return A LiveTVSupport instance.
     */
    public static LiveTVSupport getInstance() {
        return (instance);
    }

    /**
     * We need to have the known NMS instances to do anything.
     *
     * @return An array of NMS instances.
     */
    public NMS[] getNMS() {
        return (nms);
    }

    /**
     * We need to have the known NMS instances to do anything.
     *
     * @param array An array of NMS instances.
     */
    public void setNMS(NMS[] array) {
        nms = array;
    }

    /**
     * In any moment in time get the LiveTVItem instances available.
     *
     * @return An array of LiveTVItem instances.
     */
    public LiveTVItem[] getLiveTVItems() {

        LiveTVItem[] result = null;

        NMS[] narray = getNMS();
        if (narray != null) {

            for (int i = 0; i < narray.length; i++) {
            }
        }

        return (result);
    }

}


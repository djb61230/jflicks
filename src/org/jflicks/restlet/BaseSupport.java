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

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This base class is for the two singletons we use for REST.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseSupport {

    private NMS[] nms;

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

}


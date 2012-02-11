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
package org.jflicks.restlet.nms;

import java.util.Map;

import org.jflicks.nms.NMS;
import org.jflicks.restlet.BaseServerResource;

import org.restlet.resource.ResourceException;

/**
 * This class is a base implementation of a nms restlet ServerResource.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseNMSApplicationServerResource
    extends BaseServerResource {

    private static NMSApplication nmsApplication;

    /**
     * Simple empty constructor.
     */
    public BaseNMSApplicationServerResource() {
    }

    public static NMSApplication getNMSApplication() {
        return (nmsApplication);
    }

    public static void setNMSApplication(NMSApplication a) {
        nmsApplication = a;
    }

    public NMS[] getNMS() {

        NMS[] result = null;

        if (nmsApplication != null) {

            result = nmsApplication.getNMS();
        }

        return (result);
    }

    @Override
    protected void doInit() throws ResourceException {

        super.doInit();
        Map<String, Object> map = getRequestAttributes();
        if (map != null) {

            // Here we will extract all subclass attributes into
            // simple properties so subclasses need not worry about
            // doing this on their own.
            //setVersion((String) map.get("version"));
        }
    }

}


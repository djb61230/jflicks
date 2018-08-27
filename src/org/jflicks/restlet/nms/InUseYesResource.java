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

import org.jflicks.restlet.BaseServerResource;
import org.jflicks.restlet.NMSSupport;
import org.jflicks.nms.InUse;
import org.jflicks.util.LogUtil;

import org.restlet.data.Status;
import org.restlet.resource.Put;
import org.restlet.representation.Representation;

/**
 * This class will return the current upcomings as XML or JSON.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class InUseYesResource extends BaseServerResource {

    /**
     * Simple empty constructor.
     */
    public InUseYesResource() {
    }

    @Put
    public void markInUse(Representation r) {

        InUse inUse = new InUse();
        inUse.setClientIpAddress(getRequest().getClientInfo().getAddress());
        inUse.setRecordingId(getRecordingId());
        inUse.setHostPort(getHostPort());

        NMSSupport nsup = NMSSupport.getInstance();
        nsup.markInUse(inUse, true);

        setStatus(Status.SUCCESS_OK);
    }

}


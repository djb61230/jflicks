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

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.Put;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;

/**
 * This class will return the current recordings as XML or JSON.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class StopRecordingResource extends BaseNMSApplicationServerResource {

    /**
     * Simple empty constructor.
     */
    public StopRecordingResource() {

        setName("Stop Recording");
        setDescription("Given a Recording Id, stop the recording.");
    }

    @Put
    public void stopRecording() {

        String rid = getRecordingId();
        System.out.println("stopRecording: " + rid);
        if (rid != null) {

            stopRecording(rid);
            setStatus(Status.SUCCESS_ACCEPTED);
        }
    }

}


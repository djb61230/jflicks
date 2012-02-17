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
import org.restlet.resource.Delete;
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
public class DeleteRecordingResource extends BaseNMSApplicationServerResource {

    /**
     * Simple empty constructor.
     */
    public DeleteRecordingResource() {

        setName("Delete Recording");
        setDescription("Given a Recording Id, delete it.  The value for "
           + "'allowRerecord' is true|false.");
    }

    @Delete
    public void deleteRecording() {

        String rid = getRecordingId();
        if (rid != null) {

            processDelete(rid, isAllowRerecord());
            setStatus(Status.SUCCESS_ACCEPTED);
        }
    }

}


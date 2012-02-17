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

import org.jflicks.nms.State;

import org.restlet.data.MediaType;
import org.restlet.resource.Get;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;

/**
 * This class will return the current state as XML or JSON.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class StateResource extends BaseNMSApplicationServerResource {

    /**
     * Simple empty constructor.
     */
    public StateResource() {

        XStream x = getXStream();
        x.alias("state", State.class);
    }

    @Get("xml|json")
    public Representation get() {

        Representation result = null;

        if (isFormatJson()) {

            State state = getState();
            Gson g = getGson();
            if ((g != null) && (state != null)) {

                String data = g.toJson(state);
                if (data != null) {

                    StringRepresentation sr = new StringRepresentation(data);
                    sr.setMediaType(MediaType.APPLICATION_JSON);
                    result = sr;
                }
            }

        } else if (isFormatXml()) {

            State state = getState();
            XStream x = getXStream();
            if ((x != null) && (state != null)) {

                String data = x.toXML(state);
                if (data != null) {

                    StringRepresentation sr = new StringRepresentation(data);
                    sr.setMediaType(MediaType.TEXT_XML);
                    result = sr;
                }
            }
        }

        return (result);
    }

}


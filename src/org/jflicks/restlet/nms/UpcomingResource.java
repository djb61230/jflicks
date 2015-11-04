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

import java.io.IOException;
import java.util.Date;

import org.jflicks.restlet.BaseServerResource;
import org.jflicks.restlet.NMSSupport;
import org.jflicks.tv.Upcoming;
import org.jflicks.util.LogUtil;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;

/**
 * This class will return the current upcomings as XML or JSON.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class UpcomingResource extends BaseServerResource {

    /**
     * Simple empty constructor.
     */
    public UpcomingResource() {

        XStream x = getXStream();
        x.alias("upcomings", Upcoming[].class);
        x.alias("upcoming", Upcoming.class);
        x.alias("date", Date.class);
    }

    @Get("xml|json")
    public Representation get() {

        Representation result = null;

        NMSSupport nsup = NMSSupport.getInstance();

        if (isFormatJson()) {

            Upcoming[] array = nsup.getUpcomings();
            if (array == null) {
                array = new Upcoming[0];
            }

            Gson g = getGson();
            if ((g != null) && (array != null)) {

                String data = g.toJson(array);
                if (data != null) {

                    JsonRepresentation sr = new JsonRepresentation(data);
                    result = sr;
                }
            }

        } else if (isFormatXml()) {

            Upcoming[] array = nsup.getUpcomings();
            if (array == null) {
                array = new Upcoming[0];
            }
            XStream x = getXStream();
            if ((x != null) && (array != null)) {

                String data = x.toXML(array);
                if (data != null) {

                    StringRepresentation sr = new StringRepresentation(data);
                    sr.setMediaType(MediaType.TEXT_XML);
                    result = sr;
                }
            }
        }

        LogUtil.log(LogUtil.DEBUG, "Finished getting upcomings.");

        return (result);
    }

    @Put
    public void override(Representation r) {

        if (r != null) {

            try {

                String showId = r.getText();
                if (showId != null) {

                    int index = showId.indexOf("=");
                    if (index != -1) {

                        showId = showId.substring(index + 1);
                    }

                    NMSSupport nsup = NMSSupport.getInstance();
                    Upcoming u = nsup.getUpcomingByShowId(showId);
                    if (u != null) {

                        nsup.overrideUpcoming(u);
                        setStatus(Status.SUCCESS_OK);
                    }
                }

            } catch (IOException ex) {

                LogUtil.log(LogUtil.WARNING, ex.getMessage());
            }
        }
    }

}


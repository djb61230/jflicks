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

import org.jflicks.tv.Upcoming;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
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
public class UpcomingResource extends BaseNMSApplicationServerResource {

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

        if (isFormatJson()) {

            Upcoming[] array = getUpcomings();
            Gson g = getGson();
            if ((g != null) && (array != null)) {

                String data = g.toJson(array);
                if (data != null) {

                    StringRepresentation sr = new StringRepresentation(data);
                    sr.setMediaType(MediaType.APPLICATION_JSON);
                    result = sr;
                }
            }

        } else if (isFormatXml()) {

            Upcoming[] array = getUpcomings();
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

        return (result);
    }

    @Put
    public void override(Representation r) {

        System.out.println("we r <" + r + ">");
        if (r != null) {

            try {

                String showId = r.getText();
                System.out.println("we showId <" + showId + ">");
                if (showId != null) {

                    int index = showId.indexOf("=");
                    if (index != -1) {

                        showId = showId.substring(index + 1);
                    }

                    Upcoming u = getUpcomingByShowId(showId);
                    System.out.println("we u <" + u + ">");
                    if (u != null) {

                        overrideUpcoming(u);
                        setStatus(Status.SUCCESS_ACCEPTED);
                    }
                }

            } catch (IOException ex) {

                System.out.println(ex.getMessage());
            }
        }

        /*
        Gson g = getGson();
        if ((g != null) && (r != null)) {

            try {

                String json = r.getText();
                System.out.println("we got <" + json + ">");
                Upcoming up = g.fromJson(json, Upcoming.class);
                System.out.println("up <" + up + ">");
                if (up != null) {

                    String showId = up.getShowId();
                    System.out.println("showId <" + showId + ">");
                    if (showId != null) {

                        Upcoming u = getUpcomingByShowId(showId);
                        System.out.println("u <" + u + ">");
                        if (u != null) {

                            overrideUpcoming(u);
                            setStatus(Status.SUCCESS_ACCEPTED);
                        }
                    }
                }

            } catch (IOException ex) {

                System.out.println(ex.getMessage());
            }
        }
        */
    }

}


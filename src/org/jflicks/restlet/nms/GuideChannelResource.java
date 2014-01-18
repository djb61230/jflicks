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

import org.jflicks.tv.ShowAiring;

import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;

/**
 * This class will return the current channels as XML or JSON.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class GuideChannelResource extends BaseNMSApplicationServerResource {

    /**
     * Simple empty constructor.
     */
    public GuideChannelResource() {

        XStream x = getXStream();
        x.alias("showairings", ShowAiring[].class);
        x.alias("showairing", ShowAiring.class);

        setName("Guide Information");
        setDescription("The shows to be aired on a given channel.");
    }

    @Get("xml|json")
    public Representation get() {

        Representation result = null;

        if (isFormatJson()) {

            ShowAiring[] array =
                getShowAiringsByChannel(getChannelById(getChannelId()));
            log(NMSApplication.DEBUG, "getShowAiringsByChannel array null = "
                + (array == null));
            Gson g = getGson();
            if ((g != null) && (array != null)) {

                String data = g.toJson(array);
                log(NMSApplication.DEBUG, "json data null = " + (data == null));
                if (data != null) {

                    JsonRepresentation jr = new JsonRepresentation(data);
                    result = jr;
                }
            }

        } else if (isFormatXml()) {

            ShowAiring[] array =
                getShowAiringsByChannel(getChannelById(getChannelId()));
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

        log(NMSApplication.DEBUG, "Finished getting guide by channel.");

        return (result);
    }

}


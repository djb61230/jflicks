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

import java.util.ArrayList;

import org.jflicks.tv.Show;
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
public class GuideTitleChannelResource extends BaseNMSApplicationServerResource {

    /**
     * Simple empty constructor.
     */
    public GuideTitleChannelResource() {

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
            array = filter(getTerm(), array);
            Gson g = getGson();
            if ((g != null) && (array != null)) {

                String data = g.toJson(array);
                if (data != null) {

                    JsonRepresentation jr = new JsonRepresentation(data);
                    result = jr;
                }
            }

        } else if (isFormatXml()) {

            ShowAiring[] array =
                getShowAiringsByChannel(getChannelById(getChannelId()));
            array = filter(getTerm(), array);
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

    private ShowAiring[] filter(String title, ShowAiring[] array) {

        ShowAiring[] result = null;

        if ((title != null) && (array != null) && (array.length > 0)) {

            ArrayList<ShowAiring> l = new ArrayList<ShowAiring>();
            for (int i = 0; i < array.length; i++) {

                Show s = array[i].getShow();
                if (s != null) {

                    if (title.equals(s.getTitle())) {
                        l.add(array[i]);
                    }
                }
            }

            if (l.size() > 0) {

                result = l.toArray(new ShowAiring[l.size()]);
            }
        }

        return (result);
    }

}


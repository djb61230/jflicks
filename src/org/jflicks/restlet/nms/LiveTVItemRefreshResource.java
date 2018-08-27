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
import org.jflicks.restlet.LiveTVItem;
import org.jflicks.restlet.LiveTVSupport;
import org.jflicks.restlet.NMSSupport;
import org.jflicks.tv.Channel;
import org.jflicks.tv.ShowAiring;
import org.jflicks.util.LogUtil;

import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
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
public class LiveTVItemRefreshResource extends BaseServerResource {

    /**
     * Simple empty constructor.
     */
    public LiveTVItemRefreshResource() {

        XStream x = getXStream();
        x.alias("liveTVItem", LiveTVItem.class);
        x.alias("channel", Channel.class);
        x.alias("showAiring", ShowAiring.class);
    }

    @Get("xml|json")
    public Representation get() {

        Representation result = null;

        LiveTVSupport lsup = LiveTVSupport.getInstance();

        LogUtil.log(LogUtil.DEBUG, "GET LiveTVItems.");
        if (isFormatJson()) {

            LogUtil.log(LogUtil.DEBUG, "User wants JSON.");
            LiveTVItem item = lsup.getLiveTVItemByChannelId(getChannelId());
            LogUtil.log(LogUtil.DEBUG, "getLiveTVItemByChannelId null = " + (item == null));
            Gson g = getGson();
            if ((g != null) && (item != null)) {

                String data = g.toJson(item);
                LogUtil.log(LogUtil.DEBUG, "JSON data null = " + (data == null));
                if (data != null) {

                    JsonRepresentation sr = new JsonRepresentation(data);
                    result = sr;
                }
            }

        } else if (isFormatXml()) {

            LogUtil.log(LogUtil.DEBUG, "User wants XML.");
            LiveTVItem item = lsup.getLiveTVItemByChannelId(getChannelId());
            LogUtil.log(LogUtil.DEBUG, "getLiveTVItemByChannelId null = " + (item == null));
            XStream x = getXStream();
            if ((x != null) && (item != null)) {

                String data = x.toXML(item);
                if (data != null) {

                    StringRepresentation sr = new StringRepresentation(data);
                    sr.setMediaType(MediaType.TEXT_XML);
                    result = sr;
                }
            }
        }

        LogUtil.log(LogUtil.DEBUG, "Finished getting livetvitems.");

        return (result);
    }

}


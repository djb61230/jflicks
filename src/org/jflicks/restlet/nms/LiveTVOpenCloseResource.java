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
import org.jflicks.restlet.LiveTVBean;
import org.jflicks.restlet.LiveTVSupport;
import org.jflicks.restlet.NMSSupport;
import org.jflicks.tv.Channel;
import org.jflicks.util.LogUtil;

import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;

/**
 * This class will return enable open and close of live tv via REST.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class LiveTVOpenCloseResource extends BaseServerResource {

    /**
     * Simple empty constructor.
     */
    public LiveTVOpenCloseResource() {

        XStream x = getXStream();
        x.alias("liveTV", LiveTVBean.class);
    }

    @Get("xml|json")
    public Representation open() {

        Representation result = null;

        LiveTVSupport lsup = LiveTVSupport.getInstance();

        LogUtil.log(LogUtil.DEBUG, "GET LiveTV session.");
        if (isFormatJson()) {

            LogUtil.log(LogUtil.DEBUG, "User wants JSON.");
            String cid = getChannelId();
            LogUtil.log(LogUtil.DEBUG, "cid: <" + cid + ">");
            LiveTVBean ltv = lsup.openSession(cid);
            LogUtil.log(LogUtil.DEBUG, "ltv: <" + ltv + ">");
            Gson g = getGson();
            if ((g != null) && (ltv != null)) {

                String data = g.toJson(ltv);
                LogUtil.log(LogUtil.DEBUG, "JSON data null = " + (data == null));
                if (data != null) {

                    JsonRepresentation sr = new JsonRepresentation(data);
                    result = sr;
                }
            }

        } else if (isFormatXml()) {

            LogUtil.log(LogUtil.DEBUG, "User wants XML.");
            String cid = getChannelId();
            LiveTVBean ltv = lsup.openSession(cid);
            XStream x = getXStream();
            if ((x != null) && (ltv != null)) {

                String data = x.toXML(ltv);
                if (data != null) {

                    StringRepresentation sr = new StringRepresentation(data);
                    sr.setMediaType(MediaType.TEXT_XML);
                    result = sr;
                }
            }
        }

        LogUtil.log(LogUtil.DEBUG, "Finished livetv open session.");

        return (result);
    }

    @Put
    public Representation close() {

        Representation result = null;

        String lid = getLiveTVId();
        LiveTVSupport lsup = LiveTVSupport.getInstance();
        lsup.closeSession(lid);

        LiveTVBean ltv = new LiveTVBean();
        ltv.setId(lid);
        ltv.setStreamURL(null);
        ltv.setMessage("Session Closed");

        if (isFormatJson()) {

            LogUtil.log(LogUtil.DEBUG, "User wants JSON.");
            LogUtil.log(LogUtil.DEBUG, "ltv: <" + ltv + ">");
            Gson g = getGson();
            if ((g != null) && (ltv != null)) {

                String data = g.toJson(ltv);
                LogUtil.log(LogUtil.DEBUG, "JSON data null = " + (data == null));
                if (data != null) {

                    JsonRepresentation sr = new JsonRepresentation(data);
                    result = sr;
                }
            }

        } else if (isFormatXml()) {

            LogUtil.log(LogUtil.DEBUG, "User wants XML.");
            XStream x = getXStream();
            if ((x != null) && (ltv != null)) {

                String data = x.toXML(ltv);
                if (data != null) {

                    StringRepresentation sr = new StringRepresentation(data);
                    sr.setMediaType(MediaType.TEXT_XML);
                    result = sr;
                }
            }
        }

        LogUtil.log(LogUtil.DEBUG, "Finished livetv close session.");

        return (result);
    }

}


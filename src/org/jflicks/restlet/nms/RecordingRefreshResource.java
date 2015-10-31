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

import java.util.Date;

import org.jflicks.restlet.BaseServerResource;
import org.jflicks.restlet.NMSSupport;
import org.jflicks.tv.Commercial;
import org.jflicks.tv.Recording;
import org.jflicks.util.LogUtil;

import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
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
public class RecordingRefreshResource extends BaseServerResource {

    /**
     * Simple empty constructor.
     */
    public RecordingRefreshResource() {

        XStream x = getXStream();
        x.alias("recording", Recording.class);
        x.alias("commercial", Commercial.class);
        x.alias("date", Date.class);
    }

    @Get("xml|json")
    public Representation get() {

        Representation result = null;

        NMSSupport nsup = NMSSupport.getInstance();

        LogUtil.log(LogUtil.DEBUG, "GET recording refresh.");
        if (isFormatJson()) {

            LogUtil.log(LogUtil.DEBUG, "User wants JSON.");
            Recording rec = nsup.getRecordingById(getRecordingId());
            LogUtil.log(LogUtil.DEBUG, "getRecordingById = " + rec);
            Gson g = getGson();
            if ((g != null) && (rec != null)) {

                String data = g.toJson(rec);
                LogUtil.log(LogUtil.DEBUG, "JSON data null = " + (data == null));
                if (data != null) {

                    JsonRepresentation sr = new JsonRepresentation(data);
                    result = sr;
                }
            }

        } else if (isFormatXml()) {

            LogUtil.log(LogUtil.DEBUG, "User wants XML.");
            Recording rec = nsup.getRecordingById(getRecordingId());
            LogUtil.log(LogUtil.DEBUG, "getRecordingById = " + rec);
            XStream x = getXStream();
            if ((x != null) && (rec != null)) {

                String data = x.toXML(rec);
                if (data != null) {

                    StringRepresentation sr = new StringRepresentation(data);
                    sr.setMediaType(MediaType.TEXT_XML);
                    result = sr;
                }
            }
        }

        LogUtil.log(LogUtil.DEBUG, "Finished getting recording refresh.");

        return (result);
    }

}


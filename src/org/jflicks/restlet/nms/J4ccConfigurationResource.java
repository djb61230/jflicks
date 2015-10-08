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

import org.jflicks.configure.J4ccConfiguration;
import org.jflicks.configure.J4ccRecorder;
import org.jflicks.restlet.BaseServerResource;
import org.jflicks.restlet.NMSSupport;

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
public class J4ccConfigurationResource extends BaseServerResource {

    /**
     * Simple empty constructor.
     */
    public J4ccConfigurationResource() {

        XStream x = getXStream();
        x.alias("j4ccrecorders", J4ccRecorder[].class);
        x.alias("j4ccconfiguration", J4ccConfiguration.class);
    }

    @Get("xml|json")
    public Representation get() {

        Representation result = null;

        NMSSupport nsup = NMSSupport.getInstance();

        if (isFormatJson()) {

            J4ccConfiguration con = nsup.getJ4ccConfiguration(getHost());
            Gson g = getGson();
            if ((g != null) && (con != null)) {

                String data = g.toJson(con);
                if (data != null) {

                    JsonRepresentation jr = new JsonRepresentation(data);
                    result = jr;
                }
            }

        } else if (isFormatXml()) {

            J4ccConfiguration con = nsup.getJ4ccConfiguration(getHost());
            XStream x = getXStream();
            if ((x != null) && (con != null)) {

                String data = x.toXML(con);
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


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
package org.jflicks.restlet;

import java.util.Map;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * This class is a base implementation of a restlet ServerResource.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseServerResource extends ServerResource {

    public static final String JSON = "json";
    public static final String XML = "xml";

    private String version;
    private String lang;
    private String format;
    private Gson gson;
    private XStream xstream;

    /**
     * Simple empty constructor.
     */
    public BaseServerResource() {

        setVersion("1.0");
        setLang("en");
        setFormat("xml");
        setGson(new Gson());
        setXStream(new XStream(new DomDriver()));
    }

    public String getVersion() {
        return (version);
    }

    public void setVersion(String s) {
        version = s;
    }

    public String getLang() {
        return (lang);
    }

    public void setLang(String s) {
        lang = s;
    }

    public String getFormat() {
        return (format);
    }

    public void setFormat(String s) {
        format = s;
    }

    public Gson getGson() {
        return (gson);
    }

    private void setGson(Gson g) {
        gson = g;
    }

    public XStream getXStream() {
        return (xstream);
    }

    private void setXStream(XStream x) {
        xstream = x;
    }

    public boolean isFormatJson() {
        return (JSON.equalsIgnoreCase(getFormat()));
    }

    public boolean isFormatXml() {
        return (XML.equalsIgnoreCase(getFormat()));
    }

    @Override
    protected void doInit() throws ResourceException {

        Map<String, Object> map = getRequestAttributes();
        if (map != null) {

            setVersion((String) map.get("version"));
            setFormat((String) map.get("format"));
        }
    }

}


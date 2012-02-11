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

import org.jflicks.nms.NMS;
import org.jflicks.tv.Channel;

import org.restlet.data.MediaType;
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
public class ChannelResource extends BaseNMSApplicationServerResource {

    /**
     * Simple empty constructor.
     */
    public ChannelResource() {

        XStream x = getXStream();
        x.alias("channel", Channel.class);
        x.alias("channels", Channel[].class);
    }

    @Get
    public Representation channels() {

        Representation result = null;

        if (isFormatJson()) {

            Channel[] array = getChannels();
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

            Channel[] array = getChannels();
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

    private Channel[] getChannels() {

        Channel[] result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0)) {

            ArrayList<Channel> clist = new ArrayList<Channel>();
            for (int i = 0; i < array.length; i++) {

                Channel[] carray = getChannels(array[i]);
                if ((carray != null) && (carray.length > 0)) {

                    for (int j = 0; j < carray.length; j++) {

                        clist.add(carray[j]);
                    }
                }
            }

            if (clist.size() > 0) {

                result = clist.toArray(new Channel[clist.size()]);
            }
        }

        return (result);
    }

    private Channel[] getChannels(NMS n) {

        Channel[] result = null;

        if (n != null) {

            result = n.getRecordableChannels();
        }

        return (result);
    }

}


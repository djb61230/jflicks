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
import java.util.Date;

import org.jflicks.nms.NMS;
import org.jflicks.tv.Commercial;
import org.jflicks.tv.Recording;

import org.restlet.data.MediaType;
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
public class RecordingResource extends BaseNMSApplicationServerResource {

    /**
     * Simple empty constructor.
     */
    public RecordingResource() {

        XStream x = getXStream();
        x.alias("recordings", Recording[].class);
        x.alias("recording", Recording.class);
        x.alias("commercial", Commercial.class);
        x.alias("date", Date.class);
    }

    @Get
    public Representation recordings() {

        Representation result = null;

        if (isFormatJson()) {

            Recording[] array = getRecordings();
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

            Recording[] array = getRecordings();
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

    private Recording[] getRecordings() {

        Recording[] result = null;

        NMS[] array = getNMS();
        System.out.println("do we have nms: " + array);
        if ((array != null) && (array.length > 0)) {

            ArrayList<Recording> rlist = new ArrayList<Recording>();
            for (int i = 0; i < array.length; i++) {

                Recording[] rarray = getRecordings(array[i]);
                if ((rarray != null) && (rarray.length > 0)) {

                    for (int j = 0; j < rarray.length; j++) {

                        rlist.add(rarray[j]);
                    }
                }
            }
            if (rlist.size() > 0) {

                result = rlist.toArray(new Recording[rlist.size()]);
            }
        }

        return (result);
    }

    private Recording[] getRecordings(NMS n) {

        Recording[] result = null;

        System.out.println("nms: " + n);
        if (n != null) {

            Recording[] array = n.getRecordings();
            if ((array != null) && (array.length > 0)) {

                ArrayList<Recording> l = new ArrayList<Recording>();
                for (int i = 0; i < array.length; i++) {

                    String surl = array[i].getStreamURL();
                    if (surl != null) {

                        l.add(array[i]);
                    }
                }

                if (l.size() > 0) {
                    result = l.toArray(new Recording[l.size()]);
                }
            }
        }

        return (result);
    }

}


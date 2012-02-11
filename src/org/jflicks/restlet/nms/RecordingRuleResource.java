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
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.ShowAiring;
import org.jflicks.tv.Task;

import org.restlet.data.MediaType;
import org.restlet.resource.Get;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;

/**
 * This class will return the current recording rules as XML or JSON.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordingRuleResource extends BaseNMSApplicationServerResource {

    /**
     * Simple empty constructor.
     */
    public RecordingRuleResource() {

        XStream x = getXStream();
        x.alias("recordingrules", RecordingRule[].class);
        x.alias("recordingrule", RecordingRule.class);
        x.alias("tasks", Task[].class);
        x.alias("task", Task.class);
        x.alias("showairing", ShowAiring.class);
    }

    @Get
    public Representation recordingrules() {

        Representation result = null;

        if (isFormatJson()) {

            RecordingRule[] array = getRecordingRules();
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

            RecordingRule[] array = getRecordingRules();
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

    private RecordingRule[] getRecordingRules() {

        RecordingRule[] result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0)) {

            ArrayList<RecordingRule> rlist = new ArrayList<RecordingRule>();
            for (int i = 0; i < array.length; i++) {

                RecordingRule[] rarray = getRecordingRules(array[i]);
                if ((rarray != null) && (rarray.length > 0)) {

                    for (int j = 0; j < rarray.length; j++) {

                        rlist.add(rarray[j]);
                    }
                }
            }

            if (rlist.size() > 0) {

                result = rlist.toArray(new RecordingRule[rlist.size()]);
            }
        }

        return (result);
    }

    private RecordingRule[] getRecordingRules(NMS n) {

        RecordingRule[] result = null;

        if (n != null) {

            result = n.getRecordingRules();
        }

        return (result);
    }

}


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

import java.io.IOException;

import org.jflicks.restlet.BaseServerResource;
import org.jflicks.restlet.NMSSupport;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.ShowAiring;
import org.jflicks.tv.Task;

import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
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
public class RecordingRuleResource extends BaseServerResource {

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

    @Get("xml|json")
    public Representation get() {

        Representation result = null;

        NMSSupport nsup = NMSSupport.getInstance();

        if (isFormatJson()) {

            RecordingRule[] array = nsup.getRecordingRules();
            if (array == null) {
                array = new RecordingRule[0];
            }
            Gson g = getGson();
            if ((g != null) && (array != null)) {

                String data = g.toJson(array);
                if (data != null) {

                    JsonRepresentation jr = new JsonRepresentation(data);
                    result = jr;
                }
            }

        } else if (isFormatXml()) {

            RecordingRule[] array = nsup.getRecordingRules();
            if (array == null) {
                array = new RecordingRule[0];
            }
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

    @Post("json")
    public void edit(Representation r) {

        Gson g = getGson();
        if ((g != null) && (r != null)) {

            try {

                String json = r.getText();
                RecordingRule rr = g.fromJson(json, RecordingRule.class);
                if (rr != null) {

                    NMSSupport nsup = NMSSupport.getInstance();
                    RecordingRule oldrr =
                        nsup.getRecordingRuleById(getRuleId());
                    if (oldrr != null) {

                        // We really only care about 4 fields.
                        int beginPadding = oldrr.getBeginPadding();
                        if (contains(json, "beginPadding")) {

                            beginPadding = rr.getBeginPadding();
                        }

                        int endPadding = oldrr.getEndPadding();
                        if (contains(json, "endPadding")) {

                            endPadding = rr.getEndPadding();
                        }

                        int type = oldrr.getType();
                        if (contains(json, "type")) {
                            type = rr.getType();
                        }

                        int priority = oldrr.getPriority();
                        if (contains(json, "priority")) {
                            priority = rr.getPriority();
                        }

                        oldrr.setBeginPadding(beginPadding);
                        oldrr.setEndPadding(endPadding);
                        oldrr.setType(type);
                        oldrr.setPriority(priority);

                        Task[] tasks = rr.getTasks();
                        if (tasks != null) {
                            oldrr.setTasks(tasks);
                        }

                        nsup.schedule(oldrr);
                    }
                }

            } catch (IOException ex) {

                System.out.println(ex.getMessage());
            }
        }
    }

}


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
import org.jflicks.tv.Task;

import org.restlet.data.MediaType;
import org.restlet.resource.Get;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;

/**
 * This class will return the current tasks as XML or JSON.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class TaskResource extends BaseNMSApplicationServerResource {

    /**
     * Simple empty constructor.
     */
    public TaskResource() {

        XStream x = getXStream();
        x.alias("tasks", Task[].class);
        x.alias("task", Task.class);
    }

    @Get
    public Representation tasks() {

        Representation result = null;

        if (isFormatJson()) {

            Task[] array = getTasks();
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

            Task[] array = getTasks();
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

    private Task[] getTasks() {

        Task[] result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0)) {

            ArrayList<Task> tlist = new ArrayList<Task>();
            for (int i = 0; i < array.length; i++) {

                Task[] tarray = getTasks(array[i]);
                if ((tarray != null) && (tarray.length > 0)) {

                    for (int j = 0; j < tarray.length; j++) {

                        tlist.add(tarray[j]);
                    }
                }
            }

            if (tlist.size() > 0) {

                result = tlist.toArray(new Task[tlist.size()]);
            }
        }

        return (result);
    }

    private Task[] getTasks(NMS n) {

        Task[] result = null;

        if (n != null) {

            result = n.getTasks();
        }

        return (result);
    }

}


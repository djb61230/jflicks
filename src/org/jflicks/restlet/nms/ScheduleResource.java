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

import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Task;

import org.restlet.data.MediaType;
import org.restlet.resource.Post;
import org.restlet.representation.Representation;

import com.google.gson.Gson;

/**
 * This class will return the current recording rules as XML or JSON.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ScheduleResource extends BaseNMSApplicationServerResource {

    /**
     * Simple empty constructor.
     */
    public ScheduleResource() {
    }

    @Post("json")
    public void schedule(Representation r) {

        Representation result = null;

        Gson g = getGson();
        if ((g != null) && (r != null)) {

            try {

                String json = r.getText();
                RecordingRule rr = g.fromJson(json, RecordingRule.class);
                if (rr != null) {

                    RecordingRule myrr = new RecordingRule();
                    myrr.setShowAiring(rr.getShowAiring());
                    myrr.setType(rr.getType());
                    myrr.setName(rr.getName());
                    myrr.setShowId(rr.getShowId());
                    myrr.setSeriesId(rr.getSeriesId());
                    myrr.setChannelId(rr.getChannelId());
                    myrr.setListingId(rr.getListingId());
                    myrr.setDuration(rr.getDuration());
                    myrr.setPriority(rr.getPriority());
                    myrr.setTasks(rr.getTasks());

                    schedule(myrr);
                }

            } catch (IOException ex) {

                System.out.println(ex.getMessage());
            }
        }
    }

}


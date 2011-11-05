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
package org.jflicks.stream.http;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import org.jflicks.nms.State;
import org.jflicks.nms.Video;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Task;
import org.jflicks.tv.Upcoming;
import org.jflicks.util.Util;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class GtvFeed extends BaseFeed {

    private Gson gson;

    /**
     * Default constructor.
     */
    public GtvFeed() {

        super("gtvfeed");
        setRequireMp4(false);
        setGson(new Gson());
    }

    private Gson getGson() {
        return (gson);
    }

    private void setGson(Gson g) {
        gson = g;
    }

    private String getStateAsString() {

        String result = null;

        State s = getState();
        Gson g = getGson();
        if ((g != null) && (s != null)) {

            result = g.toJson(s);
        }

        return (result);
    }

    private String getRecordingsAsString() {

        String result = null;

        Recording[] array = getRecordings();
        Gson g = getGson();
        if ((g != null) && (array != null)) {

            result = g.toJson(array);
        }

        return (result);
    }

    private String getChannelsAsString() {

        String result = null;

        Channel[] array = getChannels();
        Gson g = getGson();
        if ((g != null) && (array != null)) {

            result = g.toJson(array);
        }

        return (result);
    }

    private String getRecordingRulesAsString() {

        String result = null;

        RecordingRule[] array = getRecordingRules();
        Gson g = getGson();
        if ((g != null) && (array != null)) {

            result = g.toJson(array);
        }

        return (result);
    }

    private String getTasksAsString() {

        String result = null;

        Task[] array = getTasks();
        Gson g = getGson();
        if ((g != null) && (array != null)) {

            result = g.toJson(array);
        }

        return (result);
    }

    private String getUpcomingsAsString() {

        String result = null;

        Upcoming[] array = getUpcomings();
        Gson g = getGson();
        if ((g != null) && (array != null)) {

            result = g.toJson(array);
        }

        return (result);
    }

    private String getVideosAsString() {

        String result = null;

        Video[] array = getVideos();
        Gson g = getGson();
        if ((g != null) && (array != null)) {

            result = g.toJson(array);
        }

        return (result);
    }

    private RecordingRule toRecordingRule(String s) {

        RecordingRule result = null;

        Gson g = getGson();
        if ((g != null) && (s != null)) {

            result = g.fromJson(s, RecordingRule.class);
        }

        return (result);
    }

    private Upcoming toUpcoming(String s) {

        Upcoming result = null;

        Gson g = getGson();
        if ((g != null) && (s != null)) {

            result = g.fromJson(s, Upcoming.class);
        }

        return (result);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        String action = req.getParameter("action");
        System.out.println("action: " + action);
        if (action != null) {

            if (action.equals("getState")) {

                String out = getStateAsString();
                System.out.println(out);
                if (out != null) {
                    resp.getWriter().write(out);
                } else {
                    resp.getWriter().write("\n");
                }

            } else if (action.equals("getRecordings")) {

                String out = getRecordingsAsString();
                if (out != null) {
                    resp.getWriter().write(out);
                } else {
                    resp.getWriter().write("\n");
                }

            } else if (action.equals("getChannels")) {

                String out = getChannelsAsString();
                if (out != null) {
                    resp.getWriter().write(out);
                } else {
                    resp.getWriter().write("\n");
                }

            } else if (action.equals("getRecordingRules")) {

                String out = getRecordingRulesAsString();
                if (out != null) {
                    resp.getWriter().write(out);
                } else {
                    resp.getWriter().write("\n");
                }

            } else if (action.equals("getTasks")) {

                String out = getTasksAsString();
                if (out != null) {
                    resp.getWriter().write(out);
                } else {
                    resp.getWriter().write("\n");
                }

            } else if (action.equals("getUpcomings")) {

                String out = getUpcomingsAsString();
                if (out != null) {
                    resp.getWriter().write(out);
                } else {
                    resp.getWriter().write("\n");
                }

            } else if (action.equals("getVideos")) {

                String out = getVideosAsString();
                if (out != null) {
                    resp.getWriter().write(out);
                } else {
                    resp.getWriter().write("\n");
                }

            } else if (action.equals("schedule")) {

                RecordingRule rr =
                    toRecordingRule(req.getParameter("RecordingRule"));
                if (rr != null) {

                    schedule(rr);
                }
                resp.getWriter().write("\n");

            } else if (action.equals("overrideUpcoming")) {

                Upcoming u = toUpcoming(req.getParameter("Upcoming"));
                if (u != null) {

                    overrideUpcoming(u);
                }
                resp.getWriter().write("\n");
            }
        }
    }

    static class RecordingSortByTitle implements Comparator<Recording>,
        Serializable {

        public int compare(Recording r0, Recording r1) {

            String title0 = Util.toSortableTitle(r0.getTitle());
            String title1 = Util.toSortableTitle(r1.getTitle());

            return (title0.compareTo(title1));
        }
    }

    static class UpcomingSortByTitle implements Comparator<Upcoming>,
        Serializable {

        public int compare(Upcoming u0, Upcoming u1) {

            String title0 = Util.toSortableTitle(u0.getTitle());
            String title1 = Util.toSortableTitle(u1.getTitle());

            return (title0.compareTo(title1));
        }
    }

    static class RecordingRuleSortByName implements Comparator<RecordingRule>,
        Serializable {

        public int compare(RecordingRule rr0, RecordingRule rr1) {

            String name0 = Util.toSortableTitle(rr0.getName());
            String name1 = Util.toSortableTitle(rr1.getName());

            return (name0.compareTo(name1));
        }
    }

}

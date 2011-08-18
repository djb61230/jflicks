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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.Video;
import org.jflicks.tv.Airing;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;
import org.jflicks.tv.Task;
import org.jflicks.tv.Upcoming;
import org.jflicks.util.Util;

/**
 * This is a servlet that will return an RSS feed of our recordings which
 * should be consumable by roku.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RokuFeed extends BaseFeed {

    private static final String NOT_RECORDING_TEXT = "NOT recording";
    private static final String NOT_RECORDING = "NOT_RECORDING";
    private static final String ONCE_TEXT = "Recording Just this Episode";
    private static final String ONCE = "ONCE";
    private static final String SERIES_TEXT = "Recording this Series";
    private static final String SERIES = "SERIES";

    private SimpleDateFormat originalDateFormat;
    private SimpleDateFormat upcomingDateFormat;
    private String apachePath = "/var/www/";

    /**
     * Default constructor.
     */
    public RokuFeed() {

        super("rokufeed");
        setOriginalDateFormat(new SimpleDateFormat("M/d/yyyy"));
        setUpcomingDateFormat(new SimpleDateFormat("EEE MMM d h:mm aaa"));
    }

    private SimpleDateFormat getOriginalDateFormat() {
        return (originalDateFormat);
    }

    private void setOriginalDateFormat(SimpleDateFormat df) {
        originalDateFormat = df;
    }

    private SimpleDateFormat getUpcomingDateFormat() {
        return (upcomingDateFormat);
    }

    private void setUpcomingDateFormat(SimpleDateFormat df) {
        upcomingDateFormat = df;
    }

    private String formatUnknown(String s) {

        String result = "unknown";
        if (s != null) {

            result = s;
        }

        return (result);
    }

    private String formatDate(SimpleDateFormat df, Date d) {

        String result = null;

        if (df != null) {

            if (d != null) {

                StringBuffer sb = new StringBuffer();
                df.format(d, sb, new FieldPosition(0));
                result = sb.toString();
            }

        } else {

            if (d != null) {

                result = d.toString();
            }
        }

        return (result);
    }

    private Recording[] getRecordings() {

        Recording[] result = null;

        NMS n = getNMS();
        System.out.println("nms: " + n);
        if (n != null) {

            Recording[] array = n.getRecordings();
            if ((array != null) && (array.length > 0)) {

                ArrayList<Recording> l = new ArrayList<Recording>();
                for (int i = 0; i < array.length; i++) {

                    String path = array[i].getPath();
                    String iext = array[i].getIndexedExtension();
                    if ((path != null) && (iext != null)
                        && (iext.equals("mp4"))) {

                        File tmp = new File(path + "." + iext);
                        if ((tmp.exists()) && (tmp.isFile())) {

                            l.add(array[i]);
                        }
                    }
                }

                if (l.size() > 0) {

                    result = l.toArray(new Recording[l.size()]);
                }
            }
        }

        return (result);
    }

    private Upcoming[] getUpcomings() {

        Upcoming[] result = null;

        NMS n = getNMS();
        System.out.println("nms: " + n);
        if (n != null) {

            result = n.getUpcomings();
        }

        return (result);
    }

    private Video[] getVideos() {

        Video[] result = null;

        NMS n = getNMS();
        if (n != null) {

            result = n.getVideos();
        }

        return (result);
    }

    private Channel[] getChannels() {

        Channel[] result = null;

        NMS n = getNMS();
        if (n != null) {

            result = n.getRecordableChannels();
        }

        return (result);
    }

    private RecordingRule[] getRecordingRules() {

        RecordingRule[] result = null;

        NMS n = getNMS();
        if (n != null) {

            result = n.getRecordingRules();
        }

        return (result);
    }

    private Task[] getTasks() {

        Task[] result = null;

        NMS n = getNMS();
        if (n != null) {

            result = n.getTasks();
        }

        return (result);
    }

    private Channel getChannelById(Airing a) {

        Channel result = null;

        if (a != null) {

            result = getChannelById(a.getChannelId(), a.getListingId());
        }

        return (result);
    }

    private Channel getChannelById(int cid, String listingId) {

        Channel result = null;

        NMS n = getNMS();
        if ((n != null) && (listingId != null)) {

            result = n.getChannelById(cid, listingId);
        }

        return (result);
    }

    private ShowAiring[] getShowAiringByChannelListing(String channelListing) {

        ShowAiring[] result = null;

        NMS n = getNMS();
        if ((n != null) && (channelListing != null)) {

            String[] parts = channelListing.split("_");
            if ((parts != null) && (parts.length == 2)) {

                int id = Util.str2int(parts[0], 0);
                Channel c = n.getChannelById(id, parts[1]);
                if (c != null) {

                    result = n.getShowAiringsByChannel(c);
                }
            }
        }

        return (result);
    }

    private ShowAiring getShowAiringByChannel(Channel c, String showAiringId) {

        ShowAiring result = null;

        NMS n = getNMS();
        if ((n != null) && (c != null) && (showAiringId != null)) {

            ShowAiring[] array = n.getShowAiringsByChannel(c);
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    if (showAiringId.equals(array[i].getId())) {

                        result = array[i];
                        break;
                    }
                }
            }
        }

        return (result);
    }

    private void schedule(RecordingRule rr) {

        NMS n = getNMS();
        if ((n != null) && (rr != null)) {

            n.schedule(rr);
        }
    }

    private Upcoming getUpcomingByShowId(String showId) {

        Upcoming result = null;

        Upcoming[] array = getUpcomings();
        if ((array != null) && (showId != null)) {

            for (int i = 0; i < array.length; i++) {

                if (showId.equals(array[i].getShowId())) {

                    result = array[i];
                    break;
                }
            }
        }

        return (result);
    }

    private Video[] getVideoByCategory(String s) {

        Video[] result = null;

        Video[] array = getVideos();
        if ((s != null) && (array != null)) {

            ArrayList<Video> vlist = new ArrayList<Video>();
            for (int i = 0; i < array.length; i++) {

                if (s.equals(array[i].getCategory())) {
                    vlist.add(array[i]);
                }
            }

            if (vlist.size() > 0) {

                result = vlist.toArray(new Video[vlist.size()]);
            }
        }

        return (result);
    }

    private Video[] getVideoByCategoryAndSubcategory(String cat, String sub) {

        Video[] result = null;

        Video[] array = getVideoByCategory(cat);
        if ((sub != null) && (array != null)) {

            ArrayList<Video> vlist = new ArrayList<Video>();
            for (int i = 0; i < array.length; i++) {

                if (sub.equals(array[i].getSubcategory())) {
                    vlist.add(array[i]);
                }
            }

            if (vlist.size() > 0) {

                result = vlist.toArray(new Video[vlist.size()]);
            }
        }

        return (result);
    }

    private HashMap<String, Video[]> getCategoryVideoMap() {

        HashMap<String, Video[]> result = null;

        Video[] array = getVideos();
        if ((array != null) && (array.length > 0)) {

            // First pass to find all the cats.
            ArrayList<String> catlist = new ArrayList<String>();
            for (int i = 0; i < array.length; i++) {

                String cat = array[i].getCategory();
                if (cat != null) {

                    if (!catlist.contains(cat)) {

                        catlist.add(cat);
                    }
                }
            }

            if (catlist.size() > 0) {

                result = new HashMap<String, Video[]>();

                // Now do a pass for each cat.
                for (int i = 0; i < catlist.size(); i++) {

                    String cat = catlist.get(i);
                    Video[] varray = getVideoByCategory(cat);
                    if (varray != null) {

                        result.put(cat, varray);
                    }
                }
            }
        }

        return (result);
    }

    private RecordingRule getRecordingRuleById(String s) {

        RecordingRule result = null;

        RecordingRule[] rules = getRecordingRules();
        if ((s != null) && (rules != null)) {

            for (int i = 0; i < rules.length; i++) {

                if (s.equals(rules[i].getId())) {

                    result = rules[i];
                    break;
                }
            }
        }

        return (result);
    }

    private RecordingRule getRecordingRuleByShowAiring(ShowAiring sa) {

        RecordingRule result = null;

        if (sa != null) {

            Show s = sa.getShow();
            RecordingRule[] rules = getRecordingRules();
            if ((s != null) && (rules != null)) {

                for (int i = 0; i < rules.length; i++) {

                    if (rules[i].isOnceType()) {

                        if (sa.equals(rules[i].getShowAiring())) {

                            result = rules[i];
                            break;
                        }

                    } else {

                        String sid = s.getSeriesId();
                        if ((sid != null)
                            && (sid.equals(rules[i].getSeriesId()))) {

                            result = rules[i];
                            break;
                        }
                    }
                }
            }
        }

        return (result);
    }

    private String checkRule(ShowAiring sa) {

        String result = NOT_RECORDING_TEXT;

        if (sa != null) {

            Show s = sa.getShow();
            RecordingRule[] rules = getRecordingRules();
            if ((s != null) && (rules != null)) {

                for (int i = 0; i < rules.length; i++) {

                    if (rules[i].isOnceType()) {

                        if (sa.equals(rules[i].getShowAiring())) {

                            result = ONCE_TEXT;
                            break;
                        }

                    } else {

                        String sid = s.getSeriesId();
                        if ((sid != null)
                            && (sid.equals(rules[i].getSeriesId()))) {

                            result = SERIES_TEXT;
                            break;
                        }
                    }
                }
            }
        }

        return (result);
    }

    private int checkRulePriority(ShowAiring sa) {

        int result = RecordingRule.NORMAL_PRIORITY;

        if (sa != null) {

            Show s = sa.getShow();
            RecordingRule[] rules = getRecordingRules();
            if ((s != null) && (rules != null)) {

                for (int i = 0; i < rules.length; i++) {

                    if (rules[i].isOnceType()) {

                        if (sa.equals(rules[i].getShowAiring())) {

                            result = rules[i].getPriority();
                            break;
                        }

                    } else {

                        String sid = s.getSeriesId();
                        if ((sid != null)
                            && (sid.equals(rules[i].getSeriesId()))) {

                            result = rules[i].getPriority();
                            break;
                        }
                    }
                }
            }
        }

        return (result);
    }

    private String priorityToString(int i) {

        String result = "Normal";

        if (i == RecordingRule.HIGHEST_PRIORITY) {
            result = "Highest";
        } else if (i == RecordingRule.HIGH_PRIORITY) {
            result = "High";
        } else if (i == RecordingRule.LOW_PRIORITY) {
            result = "Low";
        } else if (i == RecordingRule.LOWEST_PRIORITY) {
            result = "Lowest";
        }

        return (result);
    }

    private String typeToString(int i) {

        String result = "Series";

        if (i == RecordingRule.ONCE_TYPE) {
            result = "Once";
        } else if (i == RecordingRule.DO_NOT_RECORD_TYPE) {
            result = "Do Not Record";
        }

        return (result);
    }

    private int toPriority(String s) {

        int result = RecordingRule.NORMAL_PRIORITY;

        if (s != null) {

            if (s.equals("Highest")) {
                result = RecordingRule.HIGHEST_PRIORITY;
            } else if (s.equals("High")) {
                result = RecordingRule.HIGH_PRIORITY;
            } else if (s.equals("Low")) {
                result = RecordingRule.LOW_PRIORITY;
            } else if (s.equals("Lowest")) {
                result = RecordingRule.LOWEST_PRIORITY;
            }
        }

        return (result);
    }

    private int toType(String s) {

        int result = RecordingRule.SERIES_TYPE;

        if (s != null) {

            if (s.equals("Once")) {
                result = RecordingRule.ONCE_TYPE;
            } else if (s.equals("Do Not Record")) {
                result = RecordingRule.DO_NOT_RECORD_TYPE;
            }
        }

        return (result);
    }

    private void editRule(String id, String type, String priority) {

        RecordingRule rr = getRecordingRuleById(id);
        if (rr != null) {

            rr = new RecordingRule(rr);
            System.out.println("type: <" + type + ">");
            System.out.println("priority: <" + priority + ">");
            int itype = toType(type);
            int ipriority = toPriority(priority);
            rr.setType(itype);
            rr.setPriority(ipriority);
            schedule(rr);

        } else {

            System.out.println("Can't find rule for id <" + id + ">");
        }
    }

    private void processRule(String action, String showAiringId,
        String channelId, String listingId, String priority) {

        if ((action != null) && (showAiringId != null) && (channelId != null)
            && (listingId != null)) {

            int pint = toPriority(priority);
            int cid = Util.str2int(channelId, 0);
            Channel c = getChannelById(cid, listingId);
            System.out.println("channel: " + c);
            if (c != null) {

                ShowAiring sa = getShowAiringByChannel(c, showAiringId);
                System.out.println("sa: " + sa);
                if (sa != null) {

                    // We have the exact ShowAiring submitted by the roku
                    // user.
                    if (action.equals(NOT_RECORDING)) {

                        // We should have a recording rule so we have to
                        // find it and change it to not record.
                        RecordingRule rr = getRecordingRuleByShowAiring(sa);
                        if (rr != null) {

                            rr.setType(RecordingRule.DO_NOT_RECORD_TYPE);
                            schedule(rr);
                        }

                    } else if (action.equals(ONCE)) {

                        Show show = sa.getShow();
                        Airing airing = sa.getAiring();
                        if ((show != null) && (airing != null)) {

                            // We might have a recording rule that needs to be
                            // changed to ONCE.  Or it may be a new rule.
                            RecordingRule rr = getRecordingRuleByShowAiring(sa);
                            if (rr == null) {

                                // We have to make a new one.
                                rr = new RecordingRule();
                            }

                            rr.setShowAiring(sa);
                            rr.setType(RecordingRule.ONCE_TYPE);
                            rr.setName(show.getTitle());
                            rr.setShowId(show.getId());
                            rr.setSeriesId(show.getSeriesId());
                            rr.setChannelId(airing.getChannelId());
                            rr.setListingId(airing.getListingId());
                            rr.setDuration(airing.getDuration());
                            rr.setPriority(pint);
                            rr.setTasks(getTasks());

                            schedule(rr);
                        }

                    } else if (action.equals(SERIES)) {

                        Show show = sa.getShow();
                        Airing airing = sa.getAiring();
                        if ((show != null) && (airing != null)) {

                            // We might have a recording rule that needs to be
                            // changed to SERIES.  Or it may be a new rule.
                            RecordingRule rr = getRecordingRuleByShowAiring(sa);
                            if (rr == null) {

                                // We have to make a new one.
                                rr = new RecordingRule();
                            }

                            // Lots to set for a series rule.
                            rr.setShowAiring(sa);
                            rr.setType(RecordingRule.SERIES_TYPE);
                            rr.setName(show.getTitle());
                            rr.setShowId(show.getId());
                            rr.setSeriesId(show.getSeriesId());
                            rr.setChannelId(airing.getChannelId());
                            rr.setListingId(airing.getListingId());
                            rr.setDuration(airing.getDuration());
                            rr.setPriority(pint);
                            rr.setTasks(getTasks());

                            schedule(rr);
                        }
                    }
                }
            }
        }
    }

    private String processOverride(String showId) {

        String result = "";

        if (showId != null) {

            Upcoming u = getUpcomingByShowId(showId);
            if (u != null) {

                overrideUpcoming(u);
                u = getUpcomingByShowId(showId);
                if (u != null) {
                    result = formatDate(getUpcomingDateFormat(),
                        u.getDate()) + " - " + u.getStatus();
                }
            }
        }

        return (result);

    }

    private ShowAiring[] getShowAirings(String term) {

        ShowAiring[] result = null;

        NMS n = getNMS();
        if ((term != null) && (n != null)) {

            result = n.getShowAirings(term, NMSConstants.SEARCH_TITLE);
            System.out.println("result: " + result);
            if (result != null) {
                System.out.println("result.length: " + result.length);
            }
        }

        return (result);
    }

    private String processDelete(String id, boolean b) {

        String result = "\n";

        NMS n = getNMS();
        if ((id != null) && (n != null)) {

            n.removeRecording(n.getRecordingById(id), b);
        }

        return (result);
    }

    private void overrideUpcoming(Upcoming u) {

        NMS n = getNMS();
        if (n != null) {

            n.overrideUpcoming(u);
        }
    }

    private String computeURL(String urlbase, String path) {

        String result = urlbase;

        if (path != null) {

            result = result.substring(0, result.lastIndexOf(":"));
            int index = path.indexOf("/var/www");
            if (index != -1) {

                result = result + path.substring(index + 8);
            }
        }

        return (result);
    }

    private Element createCategoryLeaf(String title, String urlbase,
        String action) {

        Element result = new Element("categoryLeaf");

        if (title != null) {
            result.setAttribute("title", title);
        }
        result.setAttribute("description", "");

        if (urlbase != null) {

            try {
                title = URLEncoder.encode(title, "UTF-8");
            } catch (Exception ex) {
            }
            String feed = urlbase + "/" + getAlias() + "?action=" + action
                + "&title=" + title;
            result.setAttribute("feed", feed);
        }

        return (result);
    }

    private Element createCategoryLeaf(String title, String urlbase,
        String action, String id, boolean useTitle) {

        Element result = new Element("categoryLeaf");

        if (title != null) {
            result.setAttribute("title", title);
        }
        result.setAttribute("description", "");

        if (urlbase != null) {

            try {
                title = URLEncoder.encode(title, "UTF-8");
            } catch (Exception ex) {
            }
            String feed = urlbase + "/" + getAlias() + "?action=" + action
                + "&id=" + id;
            if (useTitle) {

                feed = feed + "&title=" + title;
            }

            result.setAttribute("feed", feed);
        }

        return (result);
    }

    private Element[] createCategoryLeaves(Recording[] array, String urlbase) {

        Element[] result = null;

        if (array != null) {

            Arrays.sort(array, new RecordingSortByTitle());
            ArrayList<String> l = new ArrayList<String>();
            for (int i = 0; i < array.length; i++) {

                String title = array[i].getTitle();
                if (!l.contains(title)) {

                    l.add(title);
                }
            }

            l.add(0, "All");

            result = new Element[l.size()];
            for (int i = 0; i < l.size(); i++) {
                result[i] = createCategoryLeaf(l.get(i), urlbase, "recordings");
            }
        }

        return (result);
    }

    private Element[] createCategoryLeaves(Upcoming[] array, String urlbase) {

        Element[] result = null;

        if (array != null) {

            Arrays.sort(array, new UpcomingSortByTitle());
            ArrayList<String> l = new ArrayList<String>();
            for (int i = 0; i < array.length; i++) {

                String title = array[i].getTitle();
                if (!l.contains(title)) {

                    l.add(title);
                }
            }

            l.add(0, "All");

            result = new Element[l.size()];
            for (int i = 0; i < l.size(); i++) {
                result[i] = createCategoryLeaf(l.get(i), urlbase, "upcomings");
            }
        }

        return (result);
    }

    private Element[] createCategoryLeaves(Channel[] array, String urlbase) {

        Element[] result = null;

        if (array != null) {

            Arrays.sort(array);
            ArrayList<String> ltitle = new ArrayList<String>();
            ArrayList<String> lid = new ArrayList<String>();
            for (int i = 0; i < array.length; i++) {

                String title = array[i].toString();
                String id = array[i].getId() + "_" + array[i].getListingId();
                if (!ltitle.contains(title)) {

                    ltitle.add(title);
                    lid.add(id);
                }
            }

            result = new Element[ltitle.size()];
            for (int i = 0; i < ltitle.size(); i++) {
                result[i] = createCategoryLeaf(ltitle.get(i), urlbase,
                    "channel", lid.get(i), false);
            }
        }

        return (result);
    }

    private Element[] createCategoryLeaves(Video[] array, String urlbase) {

        Element[] result = null;

        if (array != null) {

            Arrays.sort(array);
            ArrayList<String> lcat = new ArrayList<String>();
            ArrayList<String> lsub = new ArrayList<String>();
            for (int i = 0; i < array.length; i++) {

                String cat = array[i].getCategory();
                String subcat = array[i].getSubcategory();
                if ((subcat != null) && (!lsub.contains(subcat))) {

                    lsub.add(subcat);
                    lcat.add(cat);
                }
            }

            result = new Element[lsub.size()];
            for (int i = 0; i < lsub.size(); i++) {
                result[i] = createCategoryLeaf(lsub.get(i), urlbase, "videos",
                    lcat.get(i), true);
            }
        }

        return (result);
    }

    private Element createCategory(String title, String description,
        String type, String vtype, String sdUrl, String hdUrl) {

        Element result = new Element("category");

        if (title != null) {
            result.setAttribute("title", title);
        }
        if (type != null) {
            result.setAttribute("type", type);
        } else {
            result.setAttribute("type", "normal");
        }
        if (vtype != null) {
            result.setAttribute("vtype", vtype);
        }
        if (description != null) {
            result.setAttribute("description", description);
        }
        if (sdUrl != null) {
            result.setAttribute("sd_img", sdUrl);
        }
        if (hdUrl != null) {
            result.setAttribute("hd_img", hdUrl);
        }

        return (result);
    }

    private Element createFeedItem(Recording r, String urlbase,
        boolean useTitle) {

        Element result = new Element("item");

        if ((r != null) && (urlbase != null)) {

            String url = computeURL(urlbase, r.getPath());
            result.setAttribute("sdImg", url + ".roku_sd.png");
            result.setAttribute("hdImg", url + ".roku_hd.png");

            String sub = r.getSubtitle();
            Element title = new Element("title");
            if (useTitle) {

                if (sub != null) {
                    title.setText(r.getTitle() + " - " + sub);
                } else {
                    title.setText(r.getTitle());
                }

            } else {

                if (sub != null) {
                    title.setText(sub);
                } else {
                    title.setText(r.getTitle());
                }
            }

            Element description = new Element("description");
            description.setText(r.getDescription());

            Element contentId = new Element("contentId");
            contentId.setText(r.getId());

            Element contentType = new Element("contentType");
            contentType.setText("episode");

            Element contentQuality = new Element("contentQuality");
            contentQuality.setText("HD");

            Element media = new Element("media");

            Element streamFormat = new Element("streamFormat");
            streamFormat.setText(r.getIndexedExtension());

            Element streamQuality = new Element("streamQuality");
            streamQuality.setText("HD");

            Element streamBitrate = new Element("streamBitrate");
            streamBitrate.setText("2048");

            Element streamUrl = new Element("streamUrl");
            streamUrl.setText(url + "." + r.getIndexedExtension());

            media.addContent(streamFormat);
            media.addContent(streamQuality);
            media.addContent(streamBitrate);
            media.addContent(streamUrl);

            Element synopsis = new Element("synopsis");
            synopsis.setText(r.getDescription());

            Element releasedate = new Element("releasedate");
            releasedate.setText(formatDate(getOriginalDateFormat(),
                r.getOriginalAirDate()));

            Element jtype = new Element("jtype");
            jtype.setText("recording");

            Element runtime = new Element("runtime");
            runtime.setText("" + r.getDuration());

            result.addContent(title);
            result.addContent(description);
            result.addContent(contentId);
            result.addContent(contentType);
            result.addContent(contentQuality);
            result.addContent(media);
            result.addContent(synopsis);
            result.addContent(releasedate);
            result.addContent(jtype);
            result.addContent(runtime);

            File tmp = new File(r.getPath() + ".hd.bif");
            System.out.println(tmp);
            if ((tmp.exists()) && (tmp.isFile())) {

                Element hdBifUrl = new Element("hdBifUrl");
                hdBifUrl.setText(url + ".hd.bif");
                result.addContent(hdBifUrl);
            }

            tmp = new File(r.getPath() + ".sd.bif");
            System.out.println(tmp);
            if ((tmp.exists()) && (tmp.isFile())) {

                Element sdBifUrl = new Element("sdBifUrl");
                sdBifUrl.setText(url + ".sd.bif");
                result.addContent(sdBifUrl);
            }

        }

        return (result);
    }

    private Element createFeedItem(RecordingRule rr, String urlbase) {

        Element result = new Element("item");

        if ((rr != null) && (urlbase != null)) {

            result.setAttribute("sdImg", urlbase + "/images/" + rr.getSeriesId()
                + "_fanart_roku_sd.jpg");
            result.setAttribute("hdImg", urlbase + "/images/" + rr.getSeriesId()
                + "_fanart_roku_hd.jpg");

            Element title = new Element("title");
            title.setText(rr.getName());

            Element description = new Element("description");
            description.setText("");

            Element contentId = new Element("contentId");
            contentId.setText(rr.getId());

            Element contentType = new Element("contentType");
            contentType.setText("episode");

            Element contentQuality = new Element("contentQuality");
            contentQuality.setText("HD");

            Element media = new Element("media");

            Element streamFormat = new Element("streamFormat");
            streamFormat.setText("mp4");

            Element streamQuality = new Element("streamQuality");
            streamQuality.setText("HD");

            Element streamBitrate = new Element("streamBitrate");
            streamBitrate.setText("2048");

            Element streamUrl = new Element("streamUrl");
            streamUrl.setText("DOES NOT EXIST");

            media.addContent(streamFormat);
            media.addContent(streamQuality);
            media.addContent(streamBitrate);
            media.addContent(streamUrl);

            Element synopsis = new Element("synopsis");
            synopsis.setText("");

            Element jtype = new Element("jtype");
            jtype.setText("rules");

            Element rulestatus = new Element("rulestatus");
            rulestatus.setText(typeToString(rr.getType()));

            Element rulepriority = new Element("rulepriority");
            rulepriority.setText(priorityToString(rr.getPriority()));

            Element runtime = new Element("runtime");
            runtime.setText("" + rr.getDuration());

            result.addContent(title);
            result.addContent(description);
            result.addContent(contentId);
            result.addContent(contentType);
            result.addContent(contentQuality);
            result.addContent(media);
            result.addContent(synopsis);
            result.addContent(jtype);
            result.addContent(rulestatus);
            result.addContent(rulepriority);
            result.addContent(runtime);
        }

        return (result);
    }

    private Element createFeedItem(Video v, String urlbase) {

        Element result = new Element("item");

        if ((v != null) && (urlbase != null)) {

            result.setAttribute("sdImg", v.getPosterURL());
            result.setAttribute("hdImg", v.getPosterURL());

            Element title = new Element("title");
            title.setText(v.getTitle());

            Element contentId = new Element("contentId");
            contentId.setText(v.getId());

            Element contentType = new Element("contentType");
            contentType.setText("movie");

            Element contentQuality = new Element("contentQuality");
            contentQuality.setText("HD");

            Element media = new Element("media");

            Element streamFormat = new Element("streamFormat");
            streamFormat.setText("mp4");

            Element streamQuality = new Element("streamQuality");
            streamQuality.setText("HD");

            Element streamBitrate = new Element("streamBitrate");
            streamBitrate.setText("2048");

            Element streamUrl = new Element("streamUrl");
            String url = computeURL(urlbase, v.getPath());
            streamUrl.setText(url);

            media.addContent(streamFormat);
            media.addContent(streamQuality);
            media.addContent(streamBitrate);
            media.addContent(streamUrl);

            Element synopsis = new Element("synopsis");
            synopsis.setText(v.getDescription());

            Element releasedate = new Element("releasedate");
            releasedate.setText(v.getReleased());

            Element jtype = new Element("jtype");
            jtype.setText("video");

            Element runtime = new Element("runtime");
            runtime.setText("" + v.getDuration());

            result.addContent(title);
            result.addContent(contentId);
            result.addContent(contentType);
            result.addContent(contentQuality);
            result.addContent(media);
            result.addContent(synopsis);
            result.addContent(releasedate);
            result.addContent(jtype);
            result.addContent(runtime);
        }

        return (result);
    }

    private Element createFeedItem(Upcoming u, String urlbase,
        boolean useTitle) {

        Element result = new Element("item");

        if ((u != null) && (urlbase != null)) {

            result.setAttribute("sdImg", urlbase + "/images/" + u.getSeriesId()
                + "_fanart_roku_sd.jpg");
            result.setAttribute("hdImg", urlbase + "/images/" + u.getSeriesId()
                + "_fanart_roku_hd.jpg");

            String sub = u.getSubtitle();
            Element title = new Element("title");
            if (useTitle) {

                if (sub != null) {
                    title.setText(u.getTitle() + " - " + sub);
                } else {
                    title.setText(u.getTitle());
                }

            } else {

                if (sub != null) {
                    title.setText(sub);
                } else {
                    title.setText(u.getTitle());
                }
            }

            Element description = new Element("description");
            description.setText(formatDate(getUpcomingDateFormat(),
                u.getDate()) + " - " + u.getStatus());

            Element contentId = new Element("contentId");
            contentId.setText(u.getShowId());

            Element contentType = new Element("contentType");
            contentType.setText("episode");

            Element contentQuality = new Element("contentQuality");
            contentQuality.setText("HD");

            Element media = new Element("media");

            Element streamFormat = new Element("streamFormat");
            streamFormat.setText("mp4");

            Element streamQuality = new Element("streamQuality");
            streamQuality.setText("HD");

            Element streamBitrate = new Element("streamBitrate");
            streamBitrate.setText("2048");

            Element streamUrl = new Element("streamUrl");
            streamUrl.setText("DOES NOT EXIST");

            media.addContent(streamFormat);
            media.addContent(streamQuality);
            media.addContent(streamBitrate);
            media.addContent(streamUrl);

            Element synopsis = new Element("synopsis");
            synopsis.setText(u.getDescription());

            Element releasedate = new Element("releasedate");
            releasedate.setText(formatDate(getOriginalDateFormat(),
                u.getDate()));

            Element jtype = new Element("jtype");
            jtype.setText("upcoming");

            Element runtime = new Element("runtime");
            runtime.setText(u.getDuration());
            String mins = u.getDuration();
            if (mins != null) {

                mins = mins.substring(0, mins.indexOf(" "));
                runtime.setText("" + (Util.str2int(mins, 0) * 60));
            }

            result.addContent(title);
            result.addContent(description);
            result.addContent(contentId);
            result.addContent(contentType);
            result.addContent(contentQuality);
            result.addContent(media);
            result.addContent(synopsis);
            result.addContent(releasedate);
            result.addContent(jtype);
            result.addContent(runtime);
        }

        return (result);
    }

    private Element createFeedItem(ShowAiring sa, String urlbase) {

        Element result = new Element("item");

        if ((sa != null) && (urlbase != null)) {

            Airing a = sa.getAiring();
            Show s = sa.getShow();
            Channel c = getChannelById(a);

            result.setAttribute("sdImg", urlbase + "/images/" + s.getSeriesId()
                + "_fanart_roku_sd.jpg");
            result.setAttribute("hdImg", urlbase + "/images/" + s.getSeriesId()
                + "_fanart_roku_hd.jpg");

            Element title = new Element("title");
            title.setText(s.getTitle());

            String rstatus = checkRule(sa);
            Element description = new Element("description");
            description.setText(formatDate(getUpcomingDateFormat(),
                a.getAirDate()) + " " + c.toString() + " - Episode "
                + formatUnknown(s.getEpisodeNumber())
                + " - " + rstatus);

            Element contentId = new Element("contentId");
            contentId.setText(sa.getId() + "_" + a.getChannelId() + "_"
                + a.getListingId());

            Element contentType = new Element("contentType");
            contentType.setText("episode");

            Element contentQuality = new Element("contentQuality");
            contentQuality.setText("HD");

            Element media = new Element("media");

            Element streamFormat = new Element("streamFormat");
            streamFormat.setText("mp4");

            Element streamQuality = new Element("streamQuality");
            streamQuality.setText("HD");

            Element streamBitrate = new Element("streamBitrate");
            streamBitrate.setText("2048");

            Element streamUrl = new Element("streamUrl");
            streamUrl.setText("DOES NOT EXIST");

            media.addContent(streamFormat);
            media.addContent(streamQuality);
            media.addContent(streamBitrate);
            media.addContent(streamUrl);

            Element synopsis = new Element("synopsis");
            synopsis.setText(s.getDescription());

            Element releasedate = new Element("releasedate");
            releasedate.setText(formatDate(getOriginalDateFormat(),
                s.getOriginalAirDate()));

            Element jtype = new Element("jtype");
            jtype.setText("channel");

            Element rulestatus = new Element("rulestatus");
            rulestatus.setText(rstatus);

            Element rulepriority = new Element("rulepriority");
            rulepriority.setText(priorityToString(checkRulePriority(sa)));

            Element runtime = new Element("runtime");
            runtime.setText("" + a.getDuration());

            result.addContent(title);
            result.addContent(description);
            result.addContent(contentId);
            result.addContent(contentType);
            result.addContent(contentQuality);
            result.addContent(media);
            result.addContent(synopsis);
            result.addContent(releasedate);
            result.addContent(jtype);
            result.addContent(rulestatus);
            result.addContent(rulepriority);
            result.addContent(runtime);
        }

        return (result);
    }

    private Element createFeedItem(String urlbase) {

        Element result = new Element("item");

        if (urlbase != null) {

            result.setAttribute("sdImg", urlbase + "/images/roku_empty_sd.jpg");
            result.setAttribute("hdImg", urlbase + "/images/roku_empty_hd.jpg");

            Element title = new Element("title");
            title.setText("No Results");

            Element description = new Element("description");
            description.setText("No Results");

            Element contentId = new Element("contentId");
            contentId.setText("No Results");

            Element contentType = new Element("contentType");
            contentType.setText("episode");

            Element contentQuality = new Element("contentQuality");
            contentQuality.setText("HD");

            Element media = new Element("media");

            Element streamFormat = new Element("streamFormat");
            streamFormat.setText("mp4");

            Element streamQuality = new Element("streamQuality");
            streamQuality.setText("HD");

            Element streamBitrate = new Element("streamBitrate");
            streamBitrate.setText("2048");

            Element streamUrl = new Element("streamUrl");
            streamUrl.setText("DOES NOT EXIST");

            media.addContent(streamFormat);
            media.addContent(streamQuality);
            media.addContent(streamBitrate);
            media.addContent(streamUrl);

            Element synopsis = new Element("synopsis");
            synopsis.setText("No Results");

            Element releasedate = new Element("releasedate");
            releasedate.setText("No Results");

            Element jtype = new Element("jtype");
            jtype.setText("channel");

            Element rulestatus = new Element("rulestatus");
            rulestatus.setText("No Results");

            Element runtime = new Element("runtime");
            runtime.setText("1");

            result.addContent(title);
            result.addContent(description);
            result.addContent(contentId);
            result.addContent(contentType);
            result.addContent(contentQuality);
            result.addContent(media);
            result.addContent(synopsis);
            result.addContent(releasedate);
            result.addContent(jtype);
            result.addContent(rulestatus);
            result.addContent(runtime);
        }

        return (result);
    }

    private boolean equals(String first, String second) {

        boolean result = false;

        if ((first != null) && (second != null)) {

            result = first.equals(second);
        }

        return (result);
    }

    private String createRecordingFeed(String urlbase, String title) {

        String result = null;

        if ((urlbase != null) && (title != null)) {

            Recording[] array = getRecordings();
            if (array != null) {

                boolean useTitle = title.equals("All");
                if (!useTitle) {

                    ArrayList<Recording> rlist = new ArrayList<Recording>();
                    for (int i = 0; i < array.length; i++) {

                        if (equals(title, array[i].getTitle())) {

                            rlist.add(array[i]);
                        }
                    }

                    array = rlist.toArray(new Recording[rlist.size()]);
                }

                // At this point array holds only the recordings we want
                // and should be newest to oldest.
                Element root = new Element("feed");
                Element resultLength = new Element("resultLength");
                resultLength.setText("" + array.length);
                Element endIndex = new Element("endIndex");
                endIndex.setText("" + array.length);

                root.addContent(resultLength);
                root.addContent(endIndex);

                for (int i = 0; i < array.length; i++) {

                    Element item = createFeedItem(array[i], urlbase, useTitle);
                    if (item != null) {

                        root.addContent(item);
                    }
                }

                Document doc = new Document(root);

                Format f = Format.getPrettyFormat();
                f.setEncoding("ISO-8859-1");
                XMLOutputter out = new XMLOutputter(f);
                result = out.outputString(doc);
            }
        }

        return (result);
    }

    private String createRecordingRuleFeed(String urlbase) {

        String result = null;

        if (urlbase != null) {

            RecordingRule[] array = getRecordingRules();
            if (array != null) {

                Arrays.sort(array, new RecordingRuleSortByName());
                Element root = new Element("feed");
                Element resultLength = new Element("resultLength");
                resultLength.setText("" + array.length);
                Element endIndex = new Element("endIndex");
                endIndex.setText("" + array.length);

                root.addContent(resultLength);
                root.addContent(endIndex);

                for (int i = 0; i < array.length; i++) {

                    Element item = createFeedItem(array[i], urlbase);
                    if (item != null) {

                        root.addContent(item);
                    }
                }

                Document doc = new Document(root);

                Format f = Format.getPrettyFormat();
                f.setEncoding("ISO-8859-1");
                XMLOutputter out = new XMLOutputter(f);
                result = out.outputString(doc);
            }
        }

        return (result);
    }

    private String createUpcomingFeed(String urlbase, String title) {

        String result = null;

        if ((urlbase != null) && (title != null)) {

            Upcoming[] array = getUpcomings();
            if (array != null) {

                boolean useTitle = title.equals("All");
                if (!useTitle) {

                    ArrayList<Upcoming> ulist = new ArrayList<Upcoming>();
                    for (int i = 0; i < array.length; i++) {

                        if (equals(title, array[i].getTitle())) {

                            ulist.add(array[i]);
                        }
                    }

                    array = ulist.toArray(new Upcoming[ulist.size()]);
                }

                // At this point array holds only the upcomings we want
                // and should be in time order.
                Element root = new Element("feed");
                Element resultLength = new Element("resultLength");
                resultLength.setText("" + array.length);
                Element endIndex = new Element("endIndex");
                endIndex.setText("" + array.length);

                root.addContent(resultLength);
                root.addContent(endIndex);

                for (int i = 0; i < array.length; i++) {

                    Element item = createFeedItem(array[i], urlbase, useTitle);
                    if (item != null) {

                        root.addContent(item);
                    }
                }

                Document doc = new Document(root);

                Format f = Format.getPrettyFormat();
                f.setEncoding("ISO-8859-1");
                XMLOutputter out = new XMLOutputter(f);
                result = out.outputString(doc);
            }
        }

        return (result);
    }

    private String createVideoFeed(String urlbase, String cat, String subcat) {

        String result = null;

        if ((urlbase != null) && (cat != null) && (subcat != null)) {

            Video[] array = getVideoByCategoryAndSubcategory(cat, subcat);
            if (array != null) {

                Element root = new Element("feed");
                Element resultLength = new Element("resultLength");
                resultLength.setText("" + array.length);
                Element endIndex = new Element("endIndex");
                endIndex.setText("" + array.length);

                root.addContent(resultLength);
                root.addContent(endIndex);

                for (int i = 0; i < array.length; i++) {

                    Element item = createFeedItem(array[i], urlbase);
                    if (item != null) {

                        root.addContent(item);
                    }
                }

                Document doc = new Document(root);

                Format f = Format.getPrettyFormat();
                f.setEncoding("ISO-8859-1");
                XMLOutputter out = new XMLOutputter(f);
                result = out.outputString(doc);
            }
        }

        return (result);
    }

    private String createChannelFeed(String urlbase, String id) {

        String result = null;

        if ((urlbase != null) && (id != null)) {

            ShowAiring[] array = getShowAiringByChannelListing(id);
            if (array != null) {

                Element root = new Element("feed");
                Element resultLength = new Element("resultLength");
                resultLength.setText("" + array.length);
                Element endIndex = new Element("endIndex");
                endIndex.setText("" + array.length);

                root.addContent(resultLength);
                root.addContent(endIndex);

                int max = array.length;
                if (max > 100) {
                    max = 100;
                }
                for (int i = 0; i < max; i++) {

                    Element item = createFeedItem(array[i], urlbase);
                    if (item != null) {

                        root.addContent(item);
                    }
                }

                Document doc = new Document(root);

                Format f = Format.getPrettyFormat();
                f.setEncoding("ISO-8859-1");
                XMLOutputter out = new XMLOutputter(f);
                result = out.outputString(doc);
            }
        }

        return (result);
    }

    private String createSearchFeed(String urlbase, String term) {

        String result = null;

        if ((urlbase != null) && (term != null)) {

            Element root = new Element("feed");
            ShowAiring[] array = getShowAirings(term);
            if (array != null) {

                Element resultLength = new Element("resultLength");
                resultLength.setText("" + array.length);
                Element endIndex = new Element("endIndex");
                endIndex.setText("" + array.length);

                root.addContent(resultLength);
                root.addContent(endIndex);

                int max = array.length;
                if (max > 100) {
                    max = 100;
                }
                for (int i = 0; i < max; i++) {

                    Element item = createFeedItem(array[i], urlbase);
                    if (item != null) {

                        root.addContent(item);
                    }
                }

            } else {

                Element item = createFeedItem(urlbase);
                if (item != null) {

                    root.addContent(item);
                }
            }

            Document doc = new Document(root);

            Format f = Format.getPrettyFormat();
            f.setEncoding("ISO-8859-1");
            XMLOutputter out = new XMLOutputter(f);
            result = out.outputString(doc);
        }

        return (result);
    }

    private String createCategories(String urlbase) {

        String result = null;

        String recUrl = urlbase + "/images/roku_recordings.png";
        Element root = new Element("categories");
        Element recordings = createCategory("Recordings", "jflicks recordings",
            "normal", "tv", recUrl, recUrl);

        Element[] shows = createCategoryLeaves(getRecordings(), urlbase);
        if ((shows != null) && (shows.length > 0)) {

            for (int i = 0; i < shows.length; i++) {

                recordings.addContent(shows[i]);
            }

            root.addContent(recordings);
        }

        String upUrl = urlbase + "/images/roku_upcomings.png";
        Element upcomings = createCategory("Upcoming", "Future recordings",
            "normal", "tv", upUrl, upUrl);
        Element[] ups = createCategoryLeaves(getUpcomings(), urlbase);
        if ((ups != null) && (ups.length > 0)) {

            for (int i = 0; i < ups.length; i++) {

                upcomings.addContent(ups[i]);
            }

            root.addContent(upcomings);
        }

        RecordingRule[] rrarray = getRecordingRules();
        if ((rrarray != null) && (rrarray.length > 0)) {

            String rulesUrl = urlbase + "/images/roku_rules.png";
            Element rules = createCategory("Recording Rules",
                "Edit Your Recording Rules",
                "normal", "rules", rulesUrl, rulesUrl);
            Element ruleelement = createCategoryLeaf("All", urlbase, "rules");
            rules.addContent(ruleelement);

            root.addContent(rules);
        }

        String chanUrl = urlbase + "/images/roku_schedule_channel.png";
        Element channels = createCategory("Schedule By Channel",
            "Channel Guide", "normal", "tv", chanUrl, chanUrl);
        Element[] chans = createCategoryLeaves(getChannels(), urlbase);
        if ((chans != null) && (chans.length > 0)) {

            for (int i = 0; i < chans.length; i++) {

                channels.addContent(chans[i]);
            }

            root.addContent(channels);
        }

        String searchUrl = urlbase + "/images/roku_schedule_search.png";
        Element search = createCategory("Schedule By Search",
            "Search the Guide", "special_category", "search", searchUrl,
            searchUrl);

        root.addContent(search);

        HashMap<String, Video[]> m = getCategoryVideoMap();

        if ((m != null) && (m.size() > 0)) {

            Set<Map.Entry<String, Video[]>> set = m.entrySet();
            Iterator<Map.Entry<String, Video[]>> iter = set.iterator();
            while (iter.hasNext()) {

                Map.Entry<String, Video[]> me = iter.next();
                String key = me.getKey();
                Video[] varray = me.getValue();

                String vidUrl = urlbase + "/images/roku_video_" + key + ".png";
                Element videos = createCategory(key, "Video Library", "normal",
                    "video", vidUrl, vidUrl);
                Element[] vids = createCategoryLeaves(varray, urlbase);
                if ((vids != null) && (vids.length > 0)) {

                    for (int i = 0; i < vids.length; i++) {

                        videos.addContent(vids[i]);
                    }
                }

                root.addContent(videos);
            }
        }

        String settingsUrl = urlbase + "/images/roku_settings.png";
        Element settings = createCategory("Settings",
            "Edit the settings", "special_category", "settings", settingsUrl,
            settingsUrl);

        root.addContent(settings);

        Document doc = new Document(root);

        Format f = Format.getPrettyFormat();
        f.setEncoding("ISO-8859-1");
        XMLOutputter out = new XMLOutputter(f);
        result = out.outputString(doc);

        return (result);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        String action = req.getParameter("action");
        System.out.println("action: " + action);
        if (action != null) {

            if (action.equals("categories")) {

                resp.getWriter().write(createCategories(getBaseURL(req)));

            } else if (action.equals("recordings")) {

                String title = req.getParameter("title");
                resp.getWriter().write(createRecordingFeed(getBaseURL(req),
                    title));

            } else if (action.equals("rules")) {

                resp.getWriter().write(createRecordingRuleFeed(getBaseURL(req)));
            } else if (action.equals("upcomings")) {

                String title = req.getParameter("title");
                resp.getWriter().write(createUpcomingFeed(getBaseURL(req),
                    title));

            } else if (action.equals("videos")) {

                String cat = req.getParameter("id");
                String subcat = req.getParameter("title");
                resp.getWriter().write(createVideoFeed(getBaseURL(req), cat,
                    subcat));

            } else if (action.equals("channel")) {

                String id = req.getParameter("id");
                resp.getWriter().write(createChannelFeed(getBaseURL(req), id));

            } else if (action.equals("search")) {

                String term = req.getParameter("term");
                resp.getWriter().write(createSearchFeed(getBaseURL(req), term));

            } else if (action.equals("override")) {

                String showid = req.getParameter("showid");
                System.out.println("user override!!! " + showid);
                resp.getWriter().write(processOverride(showid));

            } else if (action.equals("deleterecording")) {

                String id = req.getParameter("id");
                System.out.println("user delete!!! " + id);
                resp.getWriter().write(processDelete(id, false));

            } else if (action.equals("deleterecordingallow")) {

                String id = req.getParameter("id");
                System.out.println("user delete!!! " + id);
                resp.getWriter().write(processDelete(id, true));

            } else if (action.equals("editrule")) {

                String id = req.getParameter("id");
                String type = req.getParameter("type");
                String priority = req.getParameter("priority");
                editRule(id, type, priority);
                resp.getWriter().write("\n");

            } else if (action.equals(NOT_RECORDING)) {

                String id = req.getParameter("id");
                System.out.println("NOT_RECORDING " + id);
                String[] parts = id.split("_");
                if ((parts != null) && (parts.length == 3)) {
                    processRule(NOT_RECORDING, parts[0], parts[1], parts[2],
                        req.getParameter("priority"));
                }
                resp.getWriter().write("\n");

            } else if (action.equals(ONCE)) {

                String id = req.getParameter("id");
                System.out.println("ONCE " + id);
                String[] parts = id.split("_");
                if ((parts != null) && (parts.length == 3)) {
                    processRule(ONCE, parts[0], parts[1], parts[2],
                        req.getParameter("priority"));
                }
                resp.getWriter().write("\n");

            } else if (action.equals(SERIES)) {

                String id = req.getParameter("id");
                System.out.println("SERIES " + id);
                String[] parts = id.split("_");
                if ((parts != null) && (parts.length == 3)) {
                    processRule(SERIES, parts[0], parts[1], parts[2],
                        req.getParameter("priority"));
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

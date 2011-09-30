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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jflicks.nms.NMS;
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

    /**
     * Default constructor.
     */
    public RokuFeed() {

        super("rokufeed");
        setRequireMp4(true);
        setOriginalDateFormat(new SimpleDateFormat("M/d/yyyy"));
        setUpcomingDateFormat(new SimpleDateFormat("EEE MMM d h:mm aaa"));
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

            result.setAttribute("sdImg", urlbase + "/images/"
                + getImageName(rr.getSeriesId() + "_fanart_roku_sd.jpg"));
            result.setAttribute("hdImg", urlbase + "/images/"
                + getImageName(rr.getSeriesId() + "_fanart_roku_hd.jpg"));

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

            String sub = null;
            if (v.isTV()) {

                String desc = v.getDescription();
                if (desc != null) {

                    desc = desc.trim();
                    if (desc.startsWith("\"")) {

                        int endIndex = desc.indexOf("\"", 1);
                        if (endIndex > 1) {

                            sub = desc.substring(1, endIndex);
                        }
                    }
                }
            }
            if (sub != null) {
                title.setText(v.getTitle() + " - " + sub);
            } else {
                title.setText(v.getTitle());
            }

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

            result.setAttribute("sdImg", urlbase + "/images/"
                + getImageName(u.getSeriesId() + "_fanart_roku_sd.jpg"));
            result.setAttribute("hdImg", urlbase + "/images/"
                + getImageName(u.getSeriesId() + "_fanart_roku_hd.jpg"));

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

            result.setAttribute("sdImg", urlbase + "/images/"
                + getImageName(s.getSeriesId() + "_fanart_roku_sd.jpg"));
            result.setAttribute("hdImg", urlbase + "/images/"
                + getImageName(s.getSeriesId() + "_fanart_roku_hd.jpg"));

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

            } else {

                Element root = new Element("feed");
                Element resultLength = new Element("resultLength");
                resultLength.setText("1");
                Element endIndex = new Element("endIndex");
                endIndex.setText("1");

                root.addContent(resultLength);
                root.addContent(endIndex);

                Element item = createFeedItem(urlbase);
                if (item != null) {

                    root.addContent(item);
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

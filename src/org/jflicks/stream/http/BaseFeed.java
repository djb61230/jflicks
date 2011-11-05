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
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.jflicks.nms.BaseNMS;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.NMSUtil;
import org.jflicks.nms.State;
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

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

/**
 * This is a base class servlet that will handle the lower level chores.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseFeed extends HttpServlet
    implements HttpServiceProperty {

    public static final String NOT_RECORDING_TEXT = "NOT recording";
    public static final String NOT_RECORDING = "NOT_RECORDING";
    public static final String ONCE_TEXT = "Recording Just this Episode";
    public static final String ONCE = "ONCE";
    public static final String SERIES_TEXT = "Recording this Series";
    public static final String SERIES = "SERIES";

    private String alias;
    private HttpService httpService;
    private BundleContext bundleContext;
    private SimpleDateFormat originalDateFormat;
    private SimpleDateFormat upcomingDateFormat;
    private boolean requireMp4;
    private ArrayList<NMS> nmsList;

    /**
     * Default constructor.
     */
    public BaseFeed() {

        setNMSList(new ArrayList<NMS>());
    }

    /**
     * Constructor with one required argument.
     *
     * @param s A given alias name.
     */
    public BaseFeed(String s) {

        this();
        setAlias(s);
    }

    public BundleContext getBundleContext() {
        return (bundleContext);
    }

    public void setBundleContext(BundleContext bc) {
        bundleContext = bc;
    }

    public SimpleDateFormat getOriginalDateFormat() {
        return (originalDateFormat);
    }

    public void setOriginalDateFormat(SimpleDateFormat df) {
        originalDateFormat = df;
    }

    public SimpleDateFormat getUpcomingDateFormat() {
        return (upcomingDateFormat);
    }

    public void setUpcomingDateFormat(SimpleDateFormat df) {
        upcomingDateFormat = df;
    }

    public boolean isRequireMp4() {
        return (requireMp4);
    }

    public void setRequireMp4(boolean b) {
        requireMp4 = b;
    }

    /**
     * A feed servlet has an alias name property.
     *
     * @return A String instance.
     */
    public String getAlias() {
        return (alias);
    }

    /**
     * A feed servlet has an alias name property.
     *
     * @param s A String instance.
     */
    public void setAlias(String s) {
        alias = s;
    }

    /**
     * A tracker updates us when the http service comes and goes.
     *
     * @return The OSGi HttpService instance.
     */
    public HttpService getHttpService() {
        return (httpService);
    }

    /**
     * A tracker updates us when the http service comes and goes.
     *
     * @param hs The OSGi HttpService instance.
     */
    public void setHttpService(HttpService hs) {

        httpService = hs;
        if (httpService != null) {

            try {

                httpService.registerServlet("/" + getAlias(), this, null, null);

            } catch (ServletException ex) {

                System.out.println("BaseFeed: " + ex.getMessage());

            } catch (NamespaceException ex) {

                System.out.println("NamespaceException: " + ex.getMessage());
            }
        }
    }

    private ArrayList<NMS> getNMSList() {
        return (nmsList);
    }

    private void setNMSList(ArrayList<NMS> l) {
        nmsList = l;
    }

    public NMS[] getNMS() {

        NMS[] result = null;

        ArrayList<NMS> l = getNMSList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new NMS[l.size()]);
        }

        return (result);
    }

    public void setNMS(NMS[] array) {

        ArrayList<NMS> l = getNMSList();
        if (l != null) {

            NMS[] oldArray = null;
            if (l.size() > 0) {

                oldArray = l.toArray(new NMS[l.size()]);
            }

            l.clear();
            if ((array != null) && (array.length > 0)) {

                for (int i = 0; i < array.length; i++) {

                    l.add(array[i]);
                }
            }

            NMS[] newArray = null;
            if (l.size() > 0) {

                newArray = l.toArray(new NMS[l.size()]);
            }
        }
    }

/*
    public NMS getNMS() {

        NMS result = null;

        BundleContext bc = getBundleContext();
        if (bc != null) {

            ServiceReference ref = bc.getServiceReference(NMS.class.getName());
            if (ref != null) {

                result = (NMS) bc.getService(ref);
            }
        }

        return (result);
    }
*/

    public String getBaseURL(HttpServletRequest req) {

        String result = null;

        if (req != null) {

            String uri = req.getRequestURI();
            String url = req.getRequestURL().toString();
            if ((uri != null) && (url != null)) {

                int index = url.indexOf(uri);

                if (index != -1) {

                    result = url.substring(0, index);

                } else {

                    result = url;
                }
            }
        }

        return (result);
    }

    protected String formatUnknown(String s) {

        String result = "unknown";
        if (s != null) {

            result = s;
        }

        return (result);
    }

    protected String formatDate(SimpleDateFormat df, Date d) {

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

    public File getImageHome() {

        File result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0)
            && (array[0] instanceof BaseNMS)) {

            BaseNMS bn = (BaseNMS) array[0];
            String path = bn.getConfiguredImageHome();
            if (path != null) {

                result = new File(path);
            }
        }

        return (result);
    }

    public String getImageName(String s) {

        String result = "no_image.jpg";

        if (s != null) {

            File f = getImageHome();
            if (f != null) {

                File iname = new File(f, s);
                if ((iname.exists()) && (iname.isFile())) {

                    result = s;
                }
            }
        }

        return (result);
    }

    public State getState() {

        State result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0)) {

            result = array[0].getState();
            if (result != null) {

                for (int i = 1; i < array.length; i++) {

                    result = result.merge(array[i].getState());
                }
            }
        }

        return (result);
    }

    public Recording[] getRecordings() {

        Recording[] result = null;

        NMS[] array = getNMS();
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

    public Recording[] getRecordings(NMS n) {

        Recording[] result = null;

        System.out.println("nms: " + n);
        if (n != null) {

            Recording[] array = n.getRecordings();
            if ((array != null) && (array.length > 0)) {

                ArrayList<Recording> l = new ArrayList<Recording>();
                for (int i = 0; i < array.length; i++) {

                    String surl = array[i].getStreamURL();
                    String iext = array[i].getIndexedExtension();
                    if (isRequireMp4()) {

                        if ((surl != null) && (iext != null)
                            && (iext.equals("mp4"))
                            && (surl.endsWith(iext))) {

                            l.add(array[i]);
                        }

                    } else {

                        if (surl != null) {

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

    public Upcoming[] getUpcomings() {

        Upcoming[] result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0)) {

            ArrayList<Upcoming> ulist = new ArrayList<Upcoming>();
            for (int i = 0; i < array.length; i++) {

                Upcoming[] uarray = getUpcomings(array[i]);
                if ((uarray != null) && (uarray.length > 0)) {

                    for (int j = 0; j < uarray.length; j++) {

                        ulist.add(uarray[j]);
                    }
                }
            }

            if (ulist.size() > 0) {

                result = ulist.toArray(new Upcoming[ulist.size()]);
            }
        }

        return (result);
    }

    public Upcoming[] getUpcomings(NMS n) {

        Upcoming[] result = null;

        System.out.println("nms: " + n);
        if (n != null) {

            result = n.getUpcomings();
        }

        return (result);
    }

    public Video[] getVideos() {

        Video[] result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0)) {

            ArrayList<Video> vlist = new ArrayList<Video>();
            for (int i = 0; i < array.length; i++) {

                Video[] varray = getVideos(array[i]);
                if ((varray != null) && (varray.length > 0)) {

                    for (int j = 0; j < varray.length; j++) {

                        vlist.add(varray[j]);
                    }
                }
            }

            if (vlist.size() > 0) {

                result = vlist.toArray(new Video[vlist.size()]);
            }
        }

        return (result);
    }

    public Video[] getVideos(NMS n) {

        Video[] result = null;

        if (n != null) {

            result = n.getVideos();
        }

        return (result);
    }

    public Channel[] getChannels() {

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

    public Channel[] getChannels(NMS n) {

        Channel[] result = null;

        if (n != null) {

            result = n.getRecordableChannels();
        }

        return (result);
    }

    public RecordingRule[] getRecordingRules() {

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

    public RecordingRule[] getRecordingRules(NMS n) {

        RecordingRule[] result = null;

        if (n != null) {

            result = n.getRecordingRules();
        }

        return (result);
    }

    public Task[] getTasks() {

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

    public Task[] getTasks(NMS n) {

        Task[] result = null;

        if (n != null) {

            result = n.getTasks();
        }

        return (result);
    }

    public Channel getChannelById(Airing a) {

        Channel result = null;

        if (a != null) {

            result = getChannelById(a.getChannelId(), a.getListingId());
        }

        return (result);
    }

    public Channel getChannelById(int cid, String listingId) {

        Channel result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0) && (listingId != null)) {

            for (int i = 0; i < array.length; i++) {

                result = array[i].getChannelById(cid, listingId);
                if (result != null) {
                    break;
                }
            }
        }

        return (result);
    }

    public ShowAiring[] getShowAiringByChannelListing(String channelListing) {

        ShowAiring[] result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0) && (channelListing != null)) {

            String[] parts = channelListing.split("_");
            if ((parts != null) && (parts.length == 2)) {

                int id = Util.str2int(parts[0], 0);
                Channel c = null;
                NMS n = null;
                for (int i = 0; i < array.length; i++) {

                    c = array[i].getChannelById(id, parts[1]);
                    if (c != null) {

                        n = array[i];
                        break;
                    }
                }
                if ((n != null) && (c != null)) {

                    result = n.getShowAiringsByChannel(c);
                }
            }
        }

        return (result);
    }

    public ShowAiring getShowAiringByChannel(Channel c, String showAiringId) {

        ShowAiring result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0)) {

            for (int i = 0; i < array.length; i++) {

                result = getShowAiringByChannel(array[i], c, showAiringId);
                if (result != null) {
                    break;
                }
            }
        }

        return (result);
    }

    public ShowAiring getShowAiringByChannel(NMS n, Channel c,
        String showAiringId) {

        ShowAiring result = null;

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

    public void schedule(RecordingRule rr) {

        if (rr != null) {

            NMS n = NMSUtil.select(getNMS(), rr.getHostPort());
            if (n != null) {
                n.schedule(rr);
            }
        }
    }

    public Upcoming getUpcomingByShowId(String showId) {

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

    public Video[] getVideoByCategory(String s) {

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

    public Video[] getVideoByCategoryAndSubcategory(String cat, String sub) {

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

    public HashMap<String, Video[]> getCategoryVideoMap() {

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

    public RecordingRule getRecordingRuleById(String s) {

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

    public RecordingRule getRecordingRuleByShowAiring(ShowAiring sa) {

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

    public String checkRule(ShowAiring sa) {

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

    public int checkRulePriority(ShowAiring sa) {

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

    public String priorityToString(int i) {

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

    public String typeToString(int i) {

        String result = "Series";

        if (i == RecordingRule.ONCE_TYPE) {
            result = "Once";
        } else if (i == RecordingRule.DO_NOT_RECORD_TYPE) {
            result = "Do Not Record";
        }

        return (result);
    }

    public int toPriority(String s) {

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

    public int toType(String s) {

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

    public void editRule(String id, String type, String priority) {

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

    public void processRule(String action, String showAiringId,
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

    public String processOverride(String showId) {

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

    public ShowAiring[] getShowAirings(String term) {

        ShowAiring[] result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0)) {

            ArrayList<ShowAiring> slist = new ArrayList<ShowAiring>();
            for (int i = 0; i < array.length; i++) {

                ShowAiring[] sarray = getShowAirings(array[i], term);
                if ((sarray != null) && (sarray.length > 0)) {

                    for (int j = 0; j < sarray.length; j++) {

                        slist.add(sarray[j]);
                    }
                }
            }

            if (slist.size() > 0) {

                result = slist.toArray(new ShowAiring[slist.size()]);
            }
        }

        return (result);
    }

    public ShowAiring[] getShowAirings(NMS n, String term) {

        ShowAiring[] result = null;

        if ((term != null) && (n != null)) {

            result = n.getShowAirings(term, NMSConstants.SEARCH_TITLE);
            System.out.println("result: " + result);
            if (result != null) {
                System.out.println("result.length: " + result.length);
            }
        }

        return (result);
    }

    public String processDelete(String id, boolean b) {

        String result = "\n";

        NMS[] array = getNMS();
        if ((id != null) && (array != null) && (array.length > 0)) {

            NMS n = null;
            Recording r = null;
            for (int i = 0; i < array.length; i++) {

                r = array[i].getRecordingById(id);
                if (r != null) {

                    n = array[i];
                    break;
                }
            }

            if ((n != null) && (r != null)) {

                n.removeRecording(r, b);
            }
        }

        return (result);
    }

    public void overrideUpcoming(Upcoming u) {

        if (u != null) {

            NMS n = NMSUtil.select(getNMS(), u.getHostPort());
            if (n != null) {

                n.overrideUpcoming(u);
            }
        }
    }

}

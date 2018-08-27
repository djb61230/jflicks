/*
    This file is part of ATM.

    ATM is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ATM is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ATM.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.jflicks.restlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.jflicks.configure.Configuration;
import org.jflicks.configure.J4ccConfiguration;
import org.jflicks.nms.InUse;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.NMSUtil;
import org.jflicks.nms.State;
import org.jflicks.nms.Video;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;
import org.jflicks.tv.Task;
import org.jflicks.tv.Upcoming;
import org.jflicks.tv.scheduler.Scheduler;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This class is a singleton to handle state of NMS support
 * vis REST.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class NMSSupport extends BaseSupport {

    private static NMSSupport instance = new NMSSupport();

    /**
     * Default empty constructor.
     */
    private NMSSupport() {
    }

    /**
     * We are a singleton, so users need access to it.
     *
     * @return A NMSSupport instance.
     */
    public static NMSSupport getInstance() {
        return (instance);
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

    private Channel[] getChannels(NMS n) {

        Channel[] result = null;

        if (n != null) {

            result = n.getRecordableChannels();
        }

        return (result);
    }

    public Channel getChannelById(String id) {

        Channel result = null;

        LogUtil.log(LogUtil.DEBUG, "id: " + id);
        Channel[] array = getChannels();
        if ((id != null) && (array != null) && (array.length > 0)) {

            int cid = Util.str2int(id, 0);
            if (cid != 0) {

                for (int i = 0; i < array.length; i++) {

                    if (cid == array[i].getId()) {

                        result = array[i];
                        break;
                    }
                }
            }
        }

        return (result);
    }

    public ShowAiring[] getShowAiringsByChannel(Channel c) {

        ShowAiring[] result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0)) {

            ArrayList<ShowAiring> slist = new ArrayList<ShowAiring>();
            for (int i = 0; i < array.length; i++) {

                ShowAiring[] sarray = getShowAiringsByChannel(array[i], c);
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

    private ShowAiring[] getShowAiringsByChannel(NMS n, Channel c) {

        ShowAiring[] result = null;

        if ((n != null) && (c != null)) {

            result = n.getShowAiringsByChannel(c);
        }

        return (result);
    }

    public String[] getRecordingTitles() {

        String[] result = null;

        NMS[] array = getNMS();
        LogUtil.log(LogUtil.DEBUG, "do we have nms: " + array);
        if ((array != null) && (array.length > 0)) {

            ArrayList<String> tlist = new ArrayList<String>();
            for (int i = 0; i < array.length; i++) {

                Recording[] rarray = getRecordings(array[i]);
                if ((rarray != null) && (rarray.length > 0)) {

                    for (int j = 0; j < rarray.length; j++) {

                        String title = rarray[j].getTitle();
                        if (!tlist.contains(title)) {

                            tlist.add(title);
                        }
                    }
                }
            }
            if (tlist.size() > 0) {

                result = tlist.toArray(new String[tlist.size()]);
                Arrays.sort(result, new RecordingSortByTitle());
            }
        }

        return (result);
    }

    public Recording[] getRecordingsByTitle(String s) {

        Recording[] result = null;

        if (s != null) {

            Recording[] array = getRecordings();
            if ((array != null) && (array.length > 0)) {

                ArrayList<Recording> rlist = new ArrayList<Recording>();
                for (int i = 0; i < array.length; i++) {

                    if (s.equals(array[i].getTitle())) {

                        rlist.add(array[i]);
                    }
                }

                if (rlist.size() > 0) {

                    result = rlist.toArray(new Recording[rlist.size()]);
                }
            }
        }

        return (result);
    }

    public Recording[] getRecordings() {

        Recording[] result = null;

        NMS[] array = getNMS();
        LogUtil.log(LogUtil.DEBUG, "NMS array null = " + (array == null));
        if ((array != null) && (array.length > 0)) {

            ArrayList<Recording> rlist = new ArrayList<Recording>();
            for (int i = 0; i < array.length; i++) {

                Recording[] rarray = getRecordings(array[i]);
                LogUtil.log(LogUtil.DEBUG, "recording array null = "
                    + (rarray == null));
                if ((rarray != null) && (rarray.length > 0)) {

                    LogUtil.log(LogUtil.DEBUG, "rarray.length " + rarray.length);
                    for (int j = 0; j < rarray.length; j++) {

                        rlist.add(rarray[j]);
                    }
                }
            }

            LogUtil.log(LogUtil.DEBUG, "rlist.size() " + rlist.size());
            if (rlist.size() > 0) {

                result = rlist.toArray(new Recording[rlist.size()]);
            }
        }

        return (result);
    }

    private Recording[] getRecordings(NMS n) {

        Recording[] result = null;

        LogUtil.log(LogUtil.DEBUG, "NMS null = " + (n == null));
        if (n != null) {

            Recording[] array = n.getRecordings();
            LogUtil.log(LogUtil.DEBUG, "array null = " + (array == null));
            if ((array != null) && (array.length > 0)) {

                LogUtil.log(LogUtil.DEBUG, "array length = " + array.length);
                ArrayList<Recording> l = new ArrayList<Recording>();
                for (int i = 0; i < array.length; i++) {

                    l.add(array[i]);
                }

                if (l.size() > 0) {
                    result = l.toArray(new Recording[l.size()]);
                }
            }
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

    private RecordingRule[] getRecordingRules(NMS n) {

        RecordingRule[] result = null;

        if (n != null) {

            result = n.getRecordingRules();
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

    public Recording getRecordingById(String s) {

        Recording result = null;

        Recording[] recs = getRecordings();
        if ((s != null) && (recs != null)) {

            for (int i = 0; i < recs.length; i++) {

                if (s.equals(recs[i].getId())) {

                    result = recs[i];
                    break;
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

    public void stopRecording(String id) {

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
                n.stopRecording(r);
            }
        }
    }

    public String processDelete(String id, boolean b) {

        String result = null;

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

                if (n.isInUse(r.getId(), true)) {
                    result = "Sorry recording in use, cannot delete.";
                } else {
                    n.removeRecording(r, b);
                }
            }
        }

        return (result);
    }

    public ShowAiring[] getShowAirings(String term, int searchType) {

        ShowAiring[] result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0)) {

            ArrayList<ShowAiring> slist = new ArrayList<ShowAiring>();
            for (int i = 0; i < array.length; i++) {

                ShowAiring[] sarray =
                    getShowAirings(array[i], term, searchType);
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

    private ShowAiring[] getShowAirings(NMS n, String term, int searchType) {

        ShowAiring[] result = null;

        if ((term != null) && (n != null)) {

            result = n.getShowAirings(term, searchType);
        }

        return (result);
    }

    public ShowAiring[] getShowAiringsByLetter(String letter, boolean unique) {

        ShowAiring[] result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0)) {

            ArrayList<ShowAiring> slist = new ArrayList<ShowAiring>();
            for (int i = 0; i < array.length; i++) {

                ShowAiring[] sarray =
                    getShowAiringsByLetter(array[i], letter, unique);
                if ((sarray != null) && (sarray.length > 0)) {

                    for (int j = 0; j < sarray.length; j++) {

                        slist.add(sarray[j]);
                    }
                }
            }

            if (slist.size() > 0) {

                LogUtil.log(LogUtil.DEBUG, "Found: " + slist.size());
                result = slist.toArray(new ShowAiring[slist.size()]);
                Arrays.sort(result, new ShowAiringSortByTitle());
            }
        }
        return (result);
    }

    public ShowAiring[] getShowAiringsByLetter(NMS n, String letter,
        boolean unique) {

        ShowAiring[] result = null;

        if ((letter != null) && (n != null)) {

            letter = letter.toLowerCase();
            Channel[] channels = getChannels(n);
            if ((channels != null) && (channels.length > 0)) {

                ArrayList<ShowAiring> l = new ArrayList<ShowAiring>();
                ArrayList<ShowAiring> chanlist = new ArrayList<ShowAiring>();
                ArrayList<String> showlist = new ArrayList<String>();
                for (int i = 0; i < channels.length; i++) {

                    LogUtil.log(LogUtil.DEBUG, "Processing: " + channels[i]);
                    ShowAiring[] array = getShowAiringsByChannel(n, channels[i]);
                    if ((array != null) && (array.length > 0)) {

                        for (int j = 0; j < array.length; j++) {

                            Show s = array[j].getShow();
                            if (s != null) {

                                String title = s.getTitle();
                                title = Util.toSortableTitle(title);
                                title = title.toLowerCase();
                                if ((title != null)
                                    && (title.startsWith(letter))) {

                                    if (unique) {

                                        if (!showlist.contains(title)) {

                                            LogUtil.log(LogUtil.DEBUG, "Added: " + array[j]);
                                            chanlist.add(array[j]);
                                            showlist.add(title);
                                        }

                                    } else {

                                        LogUtil.log(LogUtil.DEBUG, "Added: " + array[j]);
                                        chanlist.add(array[j]);
                                    }
                                }
                            }
                        }

                        l.addAll(chanlist);
                        chanlist.clear();
                        showlist.clear();
                    }
                }

                if (l.size() > 0) {

                    result = l.toArray(new ShowAiring[l.size()]);
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

    public Task[] getTasks() {

        Task[] result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0)) {

            ArrayList<Task> tlist = new ArrayList<Task>();
            for (int i = 0; i < array.length; i++) {

                Task[] tarray = getTasks(array[i]);
                if ((tarray != null) && (tarray.length > 0)) {

                    for (int j = 0; j < tarray.length; j++) {

                        LogUtil.log(LogUtil.DEBUG, tarray[j].getDescription());
                        if (tarray[j].isSelectable()) {

                            tlist.add(tarray[j]);
                        }
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

    public J4ccConfiguration getJ4ccConfiguration(String host) {

        J4ccConfiguration result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0) && (host != null)) {

            for (int i = 0; i < array.length; i++) {

                if (host.equals(array[i].getHost())) {

                    result = array[i].getJ4ccConfiguration();
                    break;
                }
            }
        }

        return (result);
    }

    public Configuration[] getConfigurations() {

        Configuration[] result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0)) {

            ArrayList<Configuration> clist = new ArrayList<Configuration>();
            for (int i = 0; i < array.length; i++) {

                Configuration[] carray = getConfigurations(array[i]);
                if ((carray != null) && (carray.length > 0)) {

                    for (int j = 0; j < carray.length; j++) {

                        clist.add(carray[j]);
                    }
                }
            }

            if (clist.size() > 0) {

                result = clist.toArray(new Configuration[clist.size()]);
            }
        }

        return (result);
    }

    private Configuration[] getConfigurations(NMS n) {

        Configuration[] result = null;

        LogUtil.log(LogUtil.DEBUG, "nms: " + n);
        if (n != null) {

            result = n.getConfigurations();
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

    private Upcoming[] getUpcomings(NMS n) {

        Upcoming[] result = null;

        LogUtil.log(LogUtil.DEBUG, "nms: " + n);
        if (n != null) {

            result = n.getUpcomings();
        }

        return (result);
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

    public void markInUse(InUse iu, boolean add) {

        if (iu != null) {

            NMS n = NMSUtil.select(getNMS(), iu.getHostPort());
            if (n != null) {

                if (add) {
                    n.addInUse(iu);
                } else {
                    n.removeInUse(iu);
                }
            }
        }
    }

    public void markInUseFree(InUse iu) {

        if (iu != null) {

            NMS[] array = getNMS();
            if ((array != null) && (array.length > 0)) {

                for (int i = 0; i < array.length; i++) {
                    array[i].removeInUse(iu);
                }
            }
        }
    }

    public void overrideUpcoming(Upcoming u) {

        if (u != null) {

            NMS n = NMSUtil.select(getNMS(), u.getHostPort());
            if (n != null) {

                // First we want to check if this is coming from a ONCE
                // RecordingRule.  If so we want to delete the rule instead
                // of toggling the recorded DB.
                RecordingRule rr = getOnceRecordingRuleByUpcoming(n, u);
                if (rr != null) {

                    LogUtil.log(LogUtil.DEBUG, "override a once recording: " + rr);
                    Scheduler s = n.getScheduler();
                    if (s != null) {

                        s.removeRecordingRule(rr);
                        s.requestRescheduling();
                    }

                } else {

                    n.overrideUpcoming(u);
                }
            }
        }
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

    private Video[] getVideos(NMS n) {

        Video[] result = null;

        if (n != null) {

            result = n.getVideos();
        }

        return (result);
    }

    private RecordingRule getOnceRecordingRuleByUpcoming(NMS n, Upcoming u) {

        RecordingRule result = null;

        if ((n != null) && (u != null)) {

            Channel c = getChannelByNumber(n, u.getChannelNumber());
            Scheduler s = n.getScheduler();
            if ((s != null) && (c != null)) {

                RecordingRule[] rules = s.getRecordingRules();
                if (rules != null) {

                    for (int i = 0; i < rules.length; i++) {

                        if (rules[i].isOnceType()) {

                            LogUtil.log(LogUtil.DEBUG, "found a once recording rule possibility: " + rules[i]);
                            if (rules[i].getChannelId() == c.getId()) {

                                LogUtil.log(LogUtil.DEBUG, "channelId match: " + c.getId());
                                ShowAiring sa = rules[i].getShowAiring();
                                if (sa != null) {

                                    Show show = sa.getShow();
                                    if (show != null) {

                                        if (show.getId() == u.getShowId()) {

                                            LogUtil.log(LogUtil.DEBUG, "showId match: " + show.getId());
                                            result = rules[i];
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return (result);
    }

    private Channel getChannelByNumber(NMS n, String number) {

        Channel result = null;

        if ((n != null) && (number != null)) {

            Channel[] chans = n.getRecordableChannels();
            if (chans != null) {

                for (int i = 0; i < chans.length; i++) {

                    if (number.equals(chans[i].getNumber())) {

                        result = chans[i];
                        break;
                    }
                }
            }
        }

        return (result);
    }

    static class ShowAiringSortByTitle implements Comparator<ShowAiring>,
        Serializable {

        public int compare(ShowAiring sa0, ShowAiring sa1) {

            String title0 = Util.toSortableTitle(sa0.getShow().getTitle());
            String title1 = Util.toSortableTitle(sa1.getShow().getTitle());

            return (title0.compareTo(title1));
        }
    }

    static class RecordingSortByTitle implements Comparator<String>, Serializable {

        public int compare(String s0, String s1) {

            String title0 = Util.toSortableTitle(s0);
            String title1 = Util.toSortableTitle(s1);

            return (title0.compareTo(title1));
        }
    }

}


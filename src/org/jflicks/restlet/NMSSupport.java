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
import org.jflicks.log.Log;
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
import org.jflicks.util.Util;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This class is a singleton to handle state of NMS support
 * vis REST.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class NMSSupport implements Log {

    private static NMSSupport instance = new NMSSupport();

    private ServiceTracker logServiceTracker;
    private NMS[] nms;

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

    /**
     * {@inheritDoc}
     */
    public ServiceTracker getLogServiceTracker() {
        return (logServiceTracker);
    }

    /**
     * {@inheritDoc}
     */
    public void setLogServiceTracker(ServiceTracker st) {
        logServiceTracker = st;
    }

    /**
     * {@inheritDoc}
     */
    public void log(int level, String message) {

        ServiceTracker st = getLogServiceTracker();
        if ((st != null) && (message != null)) {

            LogService ls = (LogService) st.getService();
            if (ls != null) {

                ls.log(level, message);
            }
        }
    }

    /**
     * We need to have the known NMS instances to do anything.
     *
     * @return An array of NMS instances.
     */
    public NMS[] getNMS() {
        return (nms);
    }

    /**
     * We need to have the known NMS instances to do anything.
     *
     * @param array An array of NMS instances.
     */
    public void setNMS(NMS[] array) {
        nms = array;
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

        System.out.println("id: " + id);
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

        System.out.println(result);
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
        System.out.println("do we have nms: " + array);
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
                Arrays.sort(result);
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
        log(Log.DEBUG, "NMS array null = " + (array == null));
        if ((array != null) && (array.length > 0)) {

            ArrayList<Recording> rlist = new ArrayList<Recording>();
            for (int i = 0; i < array.length; i++) {

                Recording[] rarray = getRecordings(array[i]);
                log(Log.DEBUG, "recording array null = "
                    + (rarray == null));
                if ((rarray != null) && (rarray.length > 0)) {

                    log(Log.DEBUG, "rarray.length " + rarray.length);
                    for (int j = 0; j < rarray.length; j++) {

                        rlist.add(rarray[j]);
                    }
                }
            }

            log(Log.DEBUG, "rlist.size() " + rlist.size());
            if (rlist.size() > 0) {

                result = rlist.toArray(new Recording[rlist.size()]);
            }
        }

        return (result);
    }

    private Recording[] getRecordings(NMS n) {

        Recording[] result = null;

        log(Log.DEBUG, "NMS null = " + (n == null));
        if (n != null) {

            Recording[] array = n.getRecordings();
            log(Log.DEBUG, "array null = " + (array == null));
            if ((array != null) && (array.length > 0)) {

                log(Log.DEBUG, "array length = " + array.length);
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

                log(BaseApplication.DEBUG, "Found: " + slist.size());
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

                    log(BaseApplication.DEBUG, "Processing: " + channels[i]);
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

                                            log(BaseApplication.DEBUG,
                                                "Added: " + array[j]);
                                            chanlist.add(array[j]);
                                            showlist.add(title);
                                        }

                                    } else {

                                        log(BaseApplication.DEBUG,
                                            "Added: " + array[j]);
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

        System.out.println("nms: " + n);
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

        System.out.println("nms: " + n);
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

    public void overrideUpcoming(Upcoming u) {

        if (u != null) {

            NMS n = NMSUtil.select(getNMS(), u.getHostPort());
            if (n != null) {

                n.overrideUpcoming(u);
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

    static class ShowAiringSortByTitle implements Comparator<ShowAiring>,
        Serializable {

        public int compare(ShowAiring sa0, ShowAiring sa1) {

            String title0 = Util.toSortableTitle(sa0.getShow().getTitle());
            String title1 = Util.toSortableTitle(sa1.getShow().getTitle());

            return (title0.compareTo(title1));
        }
    }

}


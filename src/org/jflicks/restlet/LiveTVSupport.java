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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSUtil;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Listing;
import org.jflicks.tv.LiveTV;
import org.jflicks.tv.ShowAiring;
import org.jflicks.tv.live.Live;
import org.jflicks.tv.programdata.ProgramData;
import org.jflicks.tv.recorder.Recorder;
import org.jflicks.tv.scheduler.Scheduler;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

/**
 * This class is a singleton to handle state of LiveTV support
 * vis REST.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class LiveTVSupport extends BaseSupport {

    private static LiveTVSupport instance = new LiveTVSupport();

    private HashMap<String, LiveTV> map;

    /**
     * Default empty constructor.
     */
    private LiveTVSupport() {

        setMap(new HashMap<String, LiveTV>());
    }

    /**
     * We are a singleton, so users need access to it.
     *
     * @return A LiveTVSupport instance.
     */
    public static LiveTVSupport getInstance() {
        return (instance);
    }

    private HashMap<String, LiveTV> getMap() {
        return (map);
    }

    private void setMap(HashMap<String, LiveTV> m) {
        map = m;
    }

    private LiveTV getLiveTVById(String id) {

        LiveTV result = null;

        if (id != null) {

            result = map.get(id);
        }

        return (result);
    }

    private void addLiveTV(String id, LiveTV ltv) {

        if ((id != null) && (ltv != null)) {
            map.put(id, ltv);
        }
    }

    private LiveTV removeLiveTV(String id) {

        LiveTV result = null;

        if (id != null) {

            result = map.get(id);
            map.remove(result);
        }

        return (result);
    }

    public LiveTVBean openDirect(String recorderId) {

        LiveTVBean result = new LiveTVBean();

        NMS[] narray = getNMS();
        LogUtil.log(LogUtil.DEBUG, "recorderId: <" + recorderId + ">");
        if ((narray != null) && (narray.length > 0) && (recorderId != null)) {

            boolean found = false;
            for (int i = 0; i < narray.length; i++) {

                Scheduler s = narray[i].getScheduler();
                if (s != null) {

                    Recorder[] recs = s.getConfiguredRecorders();
                    if ((recs != null) && (recs.length > 0)) {

                        for (int j = 0; j < recs.length; j++) {

                            LogUtil.log(LogUtil.DEBUG, "recorderId to check: <" + recs[j].getDevice() + ">");
                            if (recorderId.equals(recs[j].getDevice())) {

                                recs[j].setRecordingLiveTV(true);
                                found = true;
                                result.setReady(true);
                                break;
                            }
                        }
                    }
                }

                if (found) {

                    break;
                }
            }
        }

        return (result);
    }

    public LiveTVBean closeDirect(String recorderId) {

        LiveTVBean result = new LiveTVBean();

        NMS[] narray = getNMS();
        if ((narray != null) && (narray.length > 0) && (recorderId != null)) {

            boolean found = false;
            for (int i = 0; i < narray.length; i++) {

                Scheduler s = narray[i].getScheduler();
                if (s != null) {

                    Recorder[] recs = s.getConfiguredRecorders();
                    if ((recs != null) && (recs.length > 0)) {

                        for (int j = 0; j < recs.length; j++) {

                            if (recorderId.equals(recs[j].getDevice())) {

                                recs[j].setRecordingLiveTV(false);
                                found = true;
                                result.setReady(true);
                                break;
                            }
                        }
                    }
                }

                if (found) {

                    break;
                }
            }
        }

        return (result);
    }

    public LiveTVBean openSession(String channelId) {

        LiveTVBean result = null;

        LiveTVItem item = getLiveTVItemByChannelId(channelId);
        LogUtil.log(LogUtil.DEBUG, "item: <" + item + ">");
        if (item != null) {

            Channel c = item.getChannel();
            LogUtil.log(LogUtil.DEBUG, "channel: <" + c + ">");
            LogUtil.log(LogUtil.DEBUG, "hostPort: <" + item.getHostPort() + ">");
            NMS n = NMSUtil.select(getNMS(), item.getHostPort());
            LogUtil.log(LogUtil.DEBUG, "nms: <" + n + ">");
            if ((c != null) && (n != null)) {

                LiveTV ltv = n.openSession(c.getNumber());
                if (ltv != null) {

                    addLiveTV(ltv.getId(), ltv);
                    result = new LiveTVBean(ltv);

                } else {
                     result = new LiveTVBean("Could not open session!");
                }

            } else {

                if (c == null) {
                     result = new LiveTVBean("Channel not available!");
                } else {
                     result = new LiveTVBean("Server not available!");
                }
            }

        } else {

            result = new LiveTVBean("Channel not available!");
        }

        return (result);
    }

    public void closeSession(String liveId) {

        LogUtil.log(LogUtil.DEBUG, "closeSession liveId: <" + liveId + ">");
        LiveTV ltv = getLiveTVById(liveId);
        LogUtil.log(LogUtil.DEBUG, "closeSession liveTV: <" + ltv + ">");
        if (ltv != null) {

            NMS n = NMSUtil.select(getNMS(), ltv.getHostPort());
            if (n != null) {

                n.closeSession(ltv);
            }

            removeLiveTV(liveId);
        }
    }

    /**
     * In any moment in time get the LiveTVItem instances available.
     *
     * @return An array of LiveTVItem instances.
     */
    public LiveTVItem[] getLiveTVItems() {

        LiveTVItem[] result = null;

        NMS[] narray = getNMS();
        if (narray != null) {

            // First get all unique channels.
            HashMap<NMS, Channel[]> map = new HashMap<NMS, Channel[]>();
            for (int i = 0; i < narray.length; i++) {
            //for (int i = 0; i < 1; i++) {

                if (supportsLive(narray[i])) {

                    // Sweet something that can be played live.
                    Channel[] array = getAvailableChannels(narray[i]);
                    if ((array != null) && (array.length > 0)) {

                        map.put(narray[i], array);
                    }
                }
            }

            if (map.size() > 0) {

                // We are going to keep these unique by channel and keep
                // only the first one found.
                ArrayList<Channel> clist = new ArrayList<Channel>();
                ArrayList<LiveTVItem> llist = new ArrayList<LiveTVItem>();

                Set<Map.Entry<NMS, Channel[]>> set = map.entrySet();
                Iterator<Map.Entry<NMS, Channel[]>> iter = set.iterator();
                while (iter.hasNext()) {

                    Map.Entry<NMS, Channel[]> entry = iter.next();
                    NMS key = entry.getKey();
                    Channel[] value = entry.getValue();
                    for (int i = 0; i < value.length; i++) {

                        if (!clist.contains(value[i])) {

                            LiveTVItem item = new LiveTVItem();
                            item.setHostPort(key.getHost() + ":" + key.getPort());
                            item.setChannel(value[i]);
                            ShowAiring[] sas = key.getShowAiringsByChannel(value[i]);
                            if ((sas != null) && (sas.length > 0)) {

                                item.setShowAiring(sas[0]);
                                clist.add(value[i]);
                                llist.add(item);
                            }
                        }
                    }
                }

                if (llist.size() > 0) {

                    result = llist.toArray(new LiveTVItem[llist.size()]);

                    // We want to set the directUrl property if it exists.
                    for (int i = 0; i < result.length; i++) {

                        applyDirectUrl(result[i]);
                    }
                }
            }
        }

        if (result == null) {

            // We will return an empty array.
            result = new LiveTVItem[0];
        }

        return (result);
    }

    private boolean supportsLive(NMS n) {

        boolean result = false;

        if (n != null) {

            Live l = n.getLive();
            if (l != null) {

                Recorder[] array = n.getConfiguredRecorders();
                if ((array != null) && (array.length > 0)) {

                    result = true;
                }
            }
        }

        return (result);
    }

    private void applyDirectUrl(LiveTVItem item) {

        NMS[] narray = getNMS();
        if ((narray != null) && (item != null)) {

            Channel c = item.getChannel();
            LogUtil.log(LogUtil.DEBUG, "applyDirectUrl: c " + c);
            if (c != null) {

                // Ok we have at least one NMS and a LiveTVItem.
                for (int i = 0; i < narray.length; i++) {

                    // The Recorders in this moment of time ready to record.
                    Recorder[] recs = getRecorders(narray[i]);
                    LogUtil.log(LogUtil.DEBUG, "applyDirectUrl: recs " + recs);
                    if ((recs != null) && (recs.length > 0)) {

                        for (int j = 0; j < recs.length; j++) {

                            LogUtil.log(LogUtil.DEBUG, "applyDirectUrl: before supports ");
                            if (supportsChannel(narray[i], c, recs[j])) {

                                LogUtil.log(LogUtil.DEBUG, "applyDirectUrl: after supports ");

                                // Ok lets get the directUrlPrefix
                                String directUrlPrefix = recs[j].getDirectUrlPrefix();
                                LogUtil.log(LogUtil.DEBUG, "applyDirectUrl: directUrlPrefix " + directUrlPrefix);
                                if (directUrlPrefix != null) {

                                    StringBuilder sb = new StringBuilder(directUrlPrefix);
                                    sb.append(c.getNumber());
                                    String directUrlSuffix = recs[j].getDirectUrlSuffix();
                                    if (directUrlSuffix != null) {

                                        sb.append(directUrlSuffix);
                                    }

                                    item.setDirectUrl(sb.toString());
                                    item.setRecorderId(recs[j].getDevice());

                                    // We continue on because we want to use the last recorder.
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Channel[] getAvailableChannels(NMS n) {

        Channel[] result = null;

        if (n != null) {

            Recorder[] recs = n.getRecorders();
            Scheduler s = n.getScheduler();
            if ((s != null) && (recs != null) && (recs.length > 0)) {

                Channel[] all = s.getRecordableChannels();
                if ((all != null) && (all.length > 0)) {

                    ArrayList<Channel> clist = new ArrayList<Channel>();
                    for (int i = 0; i < all.length; i++) {

                        String listId = all[i].getListingId();
                        for (int j = 0; j < recs.length; j++) {

                            if ((!recs[j].isRecording()) && (!recs[j].isRecordingLiveTV())) {

                                // A free recorder.  Can it do this channel?
                                if (supportsChannel(n, all[i], recs[j])) {

                                    if (!clist.contains(all[i])) {

                                        clist.add(all[i]);
                                    }
                                }
                            }
                        }
                    }

                    if (clist.size() > 0) {

                        result = clist.toArray(new Channel[clist.size()]);
                        Arrays.sort(result);
                    }
                }
            }
        }

        return (result);
    }

    private boolean supportsChannel(NMS n, Channel c, Recorder r) {

        boolean result = false;

        if ((n != null) && (c != null) && (r != null)) {

            Scheduler s = n.getScheduler();
            if (s != null) {

                String lname = s.getListingNameByRecorder(r);
                String lid = c.getListingId();
                if ((lname != null) && (lid != null)) {

                    ProgramData[] array = n.getProgramData();
                    if ((array != null) && (array.length > 0)) {

                        // Should have just one....
                        for (int i = 0; i < array.length; i++) {

                            ProgramData pd = array[i];
                            Listing listing = pd.getListingByName(lname);
                            if (listing != null) {

                                if (lid.equals(listing.getId())) {

                                    LogUtil.log(LogUtil.DEBUG, "SC: lid matches");
                                    String[] channelNameList = r.getChannelNameList();
                                    LogUtil.log(LogUtil.DEBUG, "SC: channelNameList " + channelNameList);
                                    if ((channelNameList != null) && (channelNameList.length > 0)) {

                                        // We have the right listing but we have to check white/black list.
                                        if (r.isWhiteList()) {

                                            // Only set to true and break if the channel is in this list.
                                            if (isInList(channelNameList, c)) {

                                                result = true;
                                                break;
                                            }

                                        } else if (r.isBlackList()) {

                                            // Only set to true and break if the channel is NOT in this list.
                                            if (!isInList(channelNameList, c)) {

                                                result = true;
                                                break;
                                            }

                                        } else {

                                            // They may have configured a list but it's now ignored.
                                            result = true;
                                            break;
                                        }

                                    } else {

                                        // No list at all so we accept.
                                        result = true;
                                        break;
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

    private boolean isInList(String[] array, Channel c) {

        boolean result = false;

        if ((array != null) && (array.length > 0) && (c != null)) {

            for (String name : array) {

                if (isChannelNameOrNumber(c, name)) {

                    result = true;
                    break;
                }
            }
        }

        return (result);
    }

    private boolean isChannelNameOrNumber(Channel c, String s) {

        boolean result = false;

        if ((c != null) && (s != null)) {

            String name = c.getName();
            String number = c.getNumber();

            result = ((s.equals(name)) || (s.equals(number)));
        }

        return (result);
    }

    public LiveTVItem getLiveTVItemByChannelId(String channelId) {

        LiveTVItem result = null;

        if (channelId != null) {

            int cid = Util.str2int(channelId, -1);
            if (cid != -1) {

                LiveTVItem[] all = getLiveTVItems();
                if ((all != null) && (all.length > 0)) {

                    for (int i = 0; i < all.length; i++) {

                        Channel c = all[i].getChannel();
                        if (c != null) {

                            if (cid == c.getId()) {

                                result = all[i];
                                break;
                            }
                        }
                    }

                } else {

                    LogUtil.log(LogUtil.DEBUG, "No channels available for recording now!");
                }

            } else {

                LogUtil.log(LogUtil.DEBUG, "bad channel id: <" + channelId + ">");
            }
        }

        LogUtil.log(LogUtil.DEBUG, "LiveTVItem to use null = " + (result == null));

        return (result);
    }

    private Recorder[] getRecorders(NMS n) {

        Recorder[] result = null;

        if (n != null) {

            Scheduler s = n.getScheduler();
            if (s != null) {

                Recorder[] array = s.getConfiguredRecorders();
                if ((array != null) && (array.length > 0)) {

                    ArrayList<Recorder> rlist = new ArrayList<Recorder>();
                    for (int i = 0; i < array.length; i++) {

                        if ((!array[i].isRecording()) && (!array[i].isRecordingLiveTV())) {

                            rlist.add(array[i]);
                        }
                    }

                    if (rlist.size() > 0) {

                        result = rlist.toArray(new Recorder[rlist.size()]);
                    }
                }
            }
        }

        return (result);
    }

    private String[] getListingNames(NMS n) {

        String[] result = null;

        if (n != null) {

            Scheduler s = n.getScheduler();
            if (s != null) {

                result = s.getConfiguredListingNames();
            }
        }

        return (result);
    }

}


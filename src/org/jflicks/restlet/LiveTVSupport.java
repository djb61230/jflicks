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
import org.jflicks.tv.Channel;
import org.jflicks.tv.Listing;
import org.jflicks.tv.ShowAiring;
import org.jflicks.tv.live.Live;
import org.jflicks.tv.programdata.ProgramData;
import org.jflicks.tv.recorder.Recorder;
import org.jflicks.tv.scheduler.Scheduler;

/**
 * This class is a singleton to handle state of LiveTV support
 * vis REST.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class LiveTVSupport extends BaseSupport {

    private static LiveTVSupport instance = new LiveTVSupport();

    /**
     * Default empty constructor.
     */
    private LiveTVSupport() {
    }

    /**
     * We are a singleton, so users need access to it.
     *
     * @return A LiveTVSupport instance.
     */
    public static LiveTVSupport getInstance() {
        return (instance);
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

                System.out.println("gern: " + narray[i]);
                if (supportsLive(narray[i])) {

                    System.out.println("gern after: " + narray[i]);
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
                            item.setHostPort(key.getHost() + ":"
                                + key.getPort());
                            item.setChannel(value[i]);
                            ShowAiring[] sas =
                                key.getShowAiringsByChannel(value[i]);
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
                }
            }
        }

        return (result);
    }

    private boolean supportsLive(NMS n) {

        boolean result = false;

        if (n != null) {

            Live l = n.getLive();
            if (l != null) {

                Recorder[] array = n.getRecorders();
                if ((array != null) && (array.length > 0)) {

                    for (int i = 0; i < array.length; i++) {

                        if (array[i].isHlsMode()) {

                            result = true;
                            break;
                        }
                    }
                }
            }
        }

        return (result);
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

                            if ((!recs[j].isRecording())
                                && (recs[j].isHlsMode())) {

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

                                    result = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return (result);
    }

}

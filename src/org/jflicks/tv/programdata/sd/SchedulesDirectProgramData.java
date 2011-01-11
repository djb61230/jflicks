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
package org.jflicks.tv.programdata.sd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jflicks.db.DbWorker;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Airing;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Listing;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;
import org.jflicks.tv.programdata.BaseProgramData;
import org.jflicks.util.Util;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.osgi.Db4oService;
import com.db4o.query.Predicate;
import net.sf.xtvdclient.xtvd.datatypes.Duration;
import net.sf.xtvdclient.xtvd.datatypes.Lineup;
import net.sf.xtvdclient.xtvd.datatypes.Program;
import net.sf.xtvdclient.xtvd.datatypes.Schedule;
import net.sf.xtvdclient.xtvd.datatypes.Station;
import net.sf.xtvdclient.xtvd.datatypes.Xtvd;
import net.sf.xtvdclient.xtvd.datatypes.XtvdDate;

/**
 * This class is an implementation of the ProgramData interface using
 * Schedules Direct.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SchedulesDirectProgramData extends BaseProgramData
    implements DbWorker {

    private ObjectContainer objectContainer;
    private Db4oService db4oService;

    /**
     * Simple empty constructor.
     */
    public SchedulesDirectProgramData() {

        setTitle("Schedules Direct");
    }

    /**
     * We use the Db4oService to persist the program data.
     *
     * @return A Db4oService instance.
     */
    public Db4oService getDb4oService() {
        return (db4oService);
    }

    /**
     * We use the Db4oService to persist the program data.
     *
     * @param s A Db4oService instance.
     */
    public void setDb4oService(Db4oService s) {
        db4oService = s;
    }

    /**
     * {@inheritDoc}
     */
    public Listing[] getListings() {

        Listing[] result = null;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            ObjectSet<Listing> os = oc.queryByExample(Listing.class);
            if (os != null) {

                result = os.toArray(new Listing[os.size()]);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Listing getListingByName(String name) {

        Listing result = null;

        ObjectContainer oc = getObjectContainer();
        if ((oc != null) && (name != null)) {

            final String lname = name;

            // Should be just one Listing.
            List<Listing> listings = oc.query(new Predicate<Listing>() {

                public boolean match(Listing l) {
                    return (lname.equals(l.getName()));
                }
            });

            if ((listings != null) && (listings.size() > 0)) {

                result = listings.get(0);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Channel[] getChannels() {

        Channel[] result = null;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            ObjectSet<Channel> os = oc.queryByExample(Channel.class);
            if (os != null) {

                result = os.toArray(new Channel[os.size()]);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Show[] getShows() {

        Show[] result = null;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            ObjectSet<Show> os = oc.queryByExample(Show.class);
            if (os != null) {

                result = os.toArray(new Show[os.size()]);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Channel[] getChannelsByListing(Listing l) {

        Channel[] result = null;

        ObjectContainer oc = getObjectContainer();
        if ((oc != null) && (l != null)) {

            final String id = l.getId();
            if (id != null) {

                List<Channel> channels = oc.query(new Predicate<Channel>() {
                    public boolean match(Channel c) {
                        return (id.equals(c.getListingId()));
                    }
                });

                if ((channels != null) && (channels.size() > 0)) {

                    result = channels.toArray(new Channel[channels.size()]);
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Channel getChannelById(int id) {

        Channel result = null;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            final int cid = id;

            // Should be just one Channel.
            List<Channel> channels = oc.query(new Predicate<Channel>() {

                public boolean match(Channel c) {
                    return (cid == c.getId());
                }
            });

            if ((channels != null) && (channels.size() > 0)) {

                result = channels.get(0);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Show getShowById(String id) {

        Show result = null;

        ObjectContainer oc = getObjectContainer();
        if ((oc != null) && (id != null)) {

            final String sid = id;

            // Should be just one Show.
            List<Show> shows = oc.query(new Predicate<Show>() {

                public boolean match(Show s) {
                    return (sid.equals(s.getId()));
                }
            });

            if ((shows != null) && (shows.size() > 0)) {

                result = shows.get(0);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Airing[] getAiringsByChannel(Channel c) {

        Airing[] result = null;

        ObjectContainer oc = getObjectContainer();
        if ((oc != null) && (c != null)) {

            final int id = c.getId();

            List<Airing> airings = oc.query(new Predicate<Airing>() {

                public boolean match(Airing a) {
                    return (id == a.getChannelId());
                }
            });

            if ((airings != null) && (airings.size() > 0)) {

                result = airings.toArray(new Airing[airings.size()]);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Airing[] getAiringsByShow(Show s) {

        Airing[] result = null;

        ObjectContainer oc = getObjectContainer();
        if ((oc != null) && (s != null)) {

            final String id = s.getId();

            List<Airing> airings = oc.query(new Predicate<Airing>() {

                public boolean match(Airing a) {
                    return (id.equals(a.getShowId()));
                }
            });

            if ((airings != null) && (airings.size() > 0)) {

                result = airings.toArray(new Airing[airings.size()]);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public ShowAiring[] getShowAiringsByChannelAndSeriesId(Channel c,
        String seriesId) {

        ShowAiring[] result = null;

        Airing[] airings = getAiringsByChannel(c);
        if ((airings != null) && (c != null) && (seriesId != null)) {

            ArrayList<ShowAiring> list = new ArrayList<ShowAiring>();
            for (int i = 0; i < airings.length; i++) {

                Show show = getShowByAiring(airings[i]);
                if (show != null) {

                    if (seriesId.equals(show.getSeriesId())) {

                        // We have one.
                        ShowAiring sa = new ShowAiring(show, airings[i]);
                        list.add(sa);
                    }
                }
            }

            if (list.size() > 0) {

                result = list.toArray(new ShowAiring[list.size()]);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public ShowAiring[] getShowAirings(String pattern, int searchType) {

        ShowAiring[] result = null;

        Show[] shows = getShows(pattern, searchType);
        if (shows != null) {

            ArrayList<ShowAiring> list = new ArrayList<ShowAiring>();
            for (int i = 0; i < shows.length; i++) {

                Airing[] airings = getAiringsByShow(shows[i]);
                if (airings != null) {

                    for (int j = 0; j < airings.length; j++) {

                        ShowAiring sa = new ShowAiring(shows[i], airings[j]);
                        if (!list.contains(sa)) {
                            list.add(sa);
                        }
                    }
                }
            }

            if (list.size() > 0) {

                result = list.toArray(new ShowAiring[list.size()]);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Show getShowByAiring(Airing a) {

        Show result = null;

        ObjectContainer oc = getObjectContainer();
        if ((oc != null) && (a != null)) {

            final String id = a.getShowId();

            List<Show> shows = oc.query(new Predicate<Show>() {

                public boolean match(Show show) {
                    return (id.equals(show.getId()));
                }
            });

            if ((shows != null) && (shows.size() > 0)) {

                result = shows.get(0);
            }
        }

        return (result);
    }

    private Show[] getShows(String pattern, int searchType) {

        Show[] result = null;

        ObjectContainer oc = getObjectContainer();
        if ((oc != null) && (pattern != null)) {

            final String pat = pattern.toLowerCase();
            final int type = searchType;

            List<Show> shows = oc.query(new Predicate<Show>() {

                public boolean match(Show s) {

                    boolean result = false;

                    String search = null;
                    switch (type) {

                    default:
                    case NMSConstants.SEARCH_TITLE:
                        search = s.getTitle();
                        break;

                    case NMSConstants.SEARCH_DESCRIPTION:
                        search = s.getDescription();
                        break;

                    case NMSConstants.SEARCH_TITLE_DESCRIPTION:
                        search = s.getTitle() + " " + s.getDescription();
                        break;
                    }
                    if (search != null) {

                        search = search.trim();
                        if (search.length() > 0) {

                            search = search.toLowerCase();
                            result = search.indexOf(pat) != -1;
                        }
                    }

                    return (result);
                }
            });

            if ((shows != null) && (shows.size() > 0)) {

                result = shows.toArray(new Show[shows.size()]);
            }
        }

        return (result);
    }

    /**
     * We have an update to the Schedules Direct data that we persist.  He
     * we have the opportunity to update our local store with the latest
     * guide data.
     *
     * @param xtvd The object instance containing the Schedules Direct data.
     */
    public void process(Xtvd xtvd) {

        ObjectContainer oc = getObjectContainer();
        if ((oc != null) && (xtvd != null)) {

            updateStatus();

            ArrayList<Channel> clist = new ArrayList<Channel>();
            Map map = xtvd.getStations();
            if (map != null) {

                Collection coll = map.values();
                if (coll != null) {

                    purge(oc, Channel.class);
                    Iterator iter = coll.iterator();
                    while (iter.hasNext()) {

                        Station station = (Station) iter.next();
                        Channel tmp = new Channel();
                        tmp.setId(station.getId());
                        tmp.setName(station.getName());
                        tmp.setFrequency(station.getFccChannelNumber());
                        tmp.setAffiliate(station.getAffiliate());
                        tmp.setCallSign(station.getCallSign());
                        clist.add(tmp);
                    }
                }
            }

            Map<String, Lineup> lmap = xtvd.getLineups();
            if (lmap != null) {

                Collection<Lineup> coll = lmap.values();
                if (coll != null) {

                    purge(oc, Listing.class);
                    Iterator<Lineup> iter = coll.iterator();
                    while (iter.hasNext()) {

                        Lineup lineup = iter.next();
                        Listing listing = processLineup(lineup, clist);
                        oc.store(listing);
                    }
                    oc.commit();
                }
            }

            // We have built all the fields to the Channels we know about.
            // Now lets write then to the DB.
            for (int i = 0; i < clist.size(); i++) {

                Channel tmp = clist.get(i);
                oc.store(tmp);
            }
            oc.commit();

            // Process the Programs and put new Show instances into the
            // database.
            Map<String, Program> pmap = xtvd.getPrograms();
            if (pmap != null) {

                Collection<Program> coll = pmap.values();
                if (coll != null) {

                    purge(oc, Show.class);
                    Iterator<Program> iter = coll.iterator();
                    while (iter.hasNext()) {

                        Program p = iter.next();
                        Show show = new Show();
                        show.setId(p.getId());
                        show.setTitle(p.getTitle());
                        show.setSubtitle(p.getSubtitle());
                        show.setDescription(p.getDescription());
                        show.setType(p.getShowType());
                        show.setEpisodeNumber(p.getSyndicatedEpisodeNumber());
                        XtvdDate xdate = p.getOriginalAirDate();
                        if (xdate != null) {
                            show.setOriginalAirDate(xdate.getDate());
                        }
                        show.setSeriesId(p.getSeries());
                        oc.store(show);
                    }
                    oc.commit();
                }
            }

            // Next we process the schedules and put Airings into the db.
            Collection<Schedule> scheds = xtvd.getSchedules();
            if ((scheds != null) && (scheds.size() > 0)) {

                purge(oc, Airing.class);
                Iterator<Schedule> iter = scheds.iterator();
                while (iter.hasNext()) {

                    Schedule s = iter.next();
                    Airing airing = new Airing();
                    airing.setShowId(s.getProgram());
                    airing.setChannelId(s.getStation());
                    airing.setAirDate(s.getTime().getLocalDate());
                    Duration dur = s.getDuration();
                    if (dur != null) {

                        int hours = Util.str2int(dur.getHours(), 0) * 3600;
                        int mins = Util.str2int(dur.getMinutes(), 0) * 60;
                        airing.setDuration((long) (hours + mins));
                        oc.store(airing);
                    }
                }

                oc.commit();
            }

            log(INFO, "Schedules Direct data process complete!");

            // Let others know...
            fireDataUpdateEvent();
        }
    }

    private Listing processLineup(Lineup l, ArrayList<Channel> list) {

        Listing result = null;

        if ((l != null) && (list != null)) {

            result = new Listing();
            result.setName(l.getName());
            result.setId(l.getId());

            Collection<net.sf.xtvdclient.xtvd.datatypes.Map> coll = l.getMaps();
            if (coll != null) {

                Iterator<net.sf.xtvdclient.xtvd.datatypes.Map> iter =
                    coll.iterator();
                while (iter.hasNext()) {

                    net.sf.xtvdclient.xtvd.datatypes.Map map = iter.next();
                    Channel found = findChannel(list, map.getStation());
                    if (found != null) {

                        found.setListingId(l.getId());
                        if (found.getFrequency() != 0) {

                            int minor = map.getChannelMinor();
                            if (minor != 0) {

                                found.setNumber(map.getChannel() + "." + minor);

                            } else {

                                found.setNumber(map.getChannel());
                            }

                        } else {

                            found.setNumber(map.getChannel());
                        }
                    }
                }
            }

        }

        return (result);
    }

    private Channel findChannel(ArrayList<Channel> list, int id) {

        Channel result = null;

        if (list != null) {

            for (int i = 0; i < list.size(); i++) {

                Channel tmp = list.get(i);
                if (tmp != null) {

                    if (tmp.getId() == id) {

                        result = tmp;
                        break;
                    }
                }
            }
        }

        return (result);
    }

    private synchronized ObjectContainer getObjectContainer() {

        if (objectContainer == null) {

            Db4oService s = getDb4oService();
            if (s != null) {

                Configuration config = s.newConfiguration();
                config.objectClass(
                    Airing.class).objectField("id").indexed(true);
                config.objectClass(
                    Airing.class).objectField("channelId").indexed(true);
                config.objectClass(
                    Channel.class).objectField("id").indexed(true);
                config.objectClass(
                    Channel.class).objectField("listingId").indexed(true);
                config.objectClass(Show.class).objectField("id").indexed(true);
                config.objectClass(
                    Show.class).objectField("title").indexed(true);
                config.objectClass(
                    Show.class).objectField("description").indexed(true);
                objectContainer = s.openFile(config, "db/sd.dat");
            }
        }

        return (objectContainer);
    }

    /**
     * Close up all resources.
     */
    public void close() {

        if (objectContainer != null) {

            boolean result = objectContainer.close();
            log(DEBUG, "SchedulesDirectProgramData: closed " + result);
            objectContainer = null;

        } else {

            log(DEBUG, "SchedulesDirectProgramData: Tried to close "
                + "but objectContainer null.");
        }
    }

    private void purge(ObjectContainer db, Class c) {

        if ((db != null) && (c != null)) {

            ObjectSet result = db.queryByExample(c);
            while (result.hasNext()) {
                db.delete(result.next());
            }
        }
    }

    /*
    private boolean exists(ObjectContainer db, Program p) {

        boolean result = false;

        if ((db != null) && (p != null)) {

            final String id = p.getId();
            if (id != null) {

                List<Show> shows = db.query(new Predicate<Show>() {

                    public boolean match(Show s) {
                        return (id.equals(s.getId()));
                    }
                });

                if ((shows != null) && (shows.size() > 0)) {
                    result = true;
                }
            }
        }

        return (result);
    }
    */

    /**
     * We compute if it's time to update data from Schedules Direct.  We
     * should update once a day.
     *
     * @return True if it's time to get more data.
     */
    public boolean isTimeToUpdate() {

        boolean result = false;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            Status status = null;

            ObjectSet<Status> os = oc.queryByExample(Status.class);
            if (os != null) {

                if (os.size() > 0) {

                    status = os.next();
                    long now = System.currentTimeMillis();
                    long next = status.getNextUpdate();
                    if (now > next) {

                        result = true;
                        log(INFO, "Time to update! Now is newer!");

                    } else {

                        log(INFO, "Not time to update: " + new Date(next));
                    }

                } else {

                    status = new Status();
                    status.setNextUpdate(System.currentTimeMillis());
                    oc.store(status);
                    oc.commit();
                    log(INFO, "Time to update! No history...");
                    result = true;
                }

            } else {

                status = new Status();
                status.setNextUpdate(System.currentTimeMillis());
                oc.store(status);
                oc.commit();
                log(INFO, "Time to update! No history...");
                result = true;
            }
        }

        return (result);
    }

    private void updateStatus() {

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            ObjectSet<Status> os = oc.queryByExample(Status.class);
            if (os.size() > 0) {

                Status status = os.next();
                status.setLastUpdate(status.getNextUpdate());
                status.setNextUpdate(System.currentTimeMillis()
                    + (1000 * 3600 * 24));
                purge(oc, Status.class);
                oc.store(status);
                oc.commit();
            }
        }
    }

}


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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jflicks.configure.NameValue;
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
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

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
    private boolean overrideTimeToUpdate;
    private boolean updatingNow;

    /**
     * Simple empty constructor.
     */
    public SchedulesDirectProgramData() {

        setTitle("Schedules Direct");
    }

    /**
     * {@inheritDoc}
     */
    public boolean requestUpdate() {

        boolean result = false;

        if (!isUpdatingNow()) {

            result = true;
            setOverrideTimeToUpdate(true);
        }

        return (result);
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

    private boolean isOverrideTimeToUpdate() {
        return (overrideTimeToUpdate);
    }

    private void setOverrideTimeToUpdate(boolean b) {
        overrideTimeToUpdate = b;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUpdatingNow() {
        return (updatingNow);
    }

    public void setUpdatingNow(boolean b) {
        updatingNow = b;
    }

    /**
     * {@inheritDoc}
     */
    public void setConfiguration(org.jflicks.configure.Configuration c) {

        super.setConfiguration(c);

        if (c != null) {

            // We overwrite the conf/XTVD.xml file so the next fetch has these
            // configuration properties, which at this point are just
            // user/password.
            NameValue usernv = c.findNameValueByName("User Name");
            NameValue passwordnv = c.findNameValueByName("Password");
            if ((usernv != null) && (passwordnv != null)) {

                String utext = usernv.getValue();
                String ptext = passwordnv.getValue();
                if ((utext != null) && (ptext != null)) {

                    File conf = new File("conf");
                    File xtvd = new File(conf, "XTVD.xml");
                    if (needsUpdating(xtvd, utext, ptext)) {

                        Element root = new Element("properties");
                        Element userName = new Element("userName");
                        userName.setText(utext);
                        Element password = new Element("password");
                        password.setText(ptext);
                        Element numberOfDays = new Element("numberOfDays");
                        numberOfDays.setText("14");
                        Element webserviceURI = new Element("webserviceURI");
                        webserviceURI.setText("http://webservices."
                            + "schedulesdirect.tmsdatadirect.com/"
                            + "schedulesdirect/tvlistings/xtvdService");

                        root.addContent(userName);
                        root.addContent(password);
                        root.addContent(numberOfDays);
                        root.addContent(webserviceURI);

                        Document doc = new Document(root);
                        Format f = Format.getPrettyFormat();
                        f.setEncoding("ISO-8859-1");
                        XMLOutputter out = new XMLOutputter(f);
                        String text = out.outputString(doc);

                        if (xtvd.exists()) {

                            File old = new File(xtvd.getPath() + ".old");
                            xtvd.renameTo(old);
                        }

                        try {

                            Util.writeTextFile(xtvd, text);
                            setOverrideTimeToUpdate(true);

                        } catch (IOException ex) {

                            log(WARNING, ex.getMessage());
                        }
                    }
                }
            }
        }
    }

    private boolean needsUpdating(File f, String user, String password) {

        boolean result = true;

        if ((f != null) && (f.exists()) && (user != null)
            && (password != null)) {

            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            builder.setIgnoringElementContentWhitespace(true);
            try {

                Document doc = builder.build(f);
                if (doc != null) {

                    Element root = doc.getRootElement();
                    if (root != null) {

                        Element uelement = root.getChild("userName");
                        if ((uelement != null)
                            && (user.equals(uelement.getText()))) {

                            Element pelement = root.getChild("password");
                            if ((pelement != null)
                                && (password.equals(pelement.getText()))) {

                                result = false;
                            }
                        }
                    }
                }

            } catch (JDOMException ex) {
            } catch (IOException ex) {
            }
        }

        return (result);
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
    public Channel getChannelById(int id, String lid) {

        Channel result = null;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            final int cid = id;
            final String clid = lid;

            // Should be just one Channel.
            List<Channel> channels = oc.query(new Predicate<Channel>() {

                public boolean match(Channel c) {

                    // Here we are changing the listingId from an
                    // exact match to a substring of.  The json data
                    // has not made a nice field that matches the
                    // listingId exactly.  So we are just going to
                    // check for indexOf to be not -1.
                    return ((cid == c.getId())
                        && (clid.indexOf(c.getListingId()) != -1));
                    /*
                    return ((cid == c.getId())
                        && (clid.equals(c.getListingId())));
                    */
                }
            });

            if ((channels != null) && (channels.size() > 0)) {

                result = channels.get(0);
            }
        }

        return (result);
    }

    private Channel[] getAllChannelsById(int id) {

        Channel[] result = null;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            final int cid = id;

            List<Channel> channels = oc.query(new Predicate<Channel>() {

                public boolean match(Channel c) {

                    return (cid == c.getId());
                }
            });

            if ((channels != null) && (channels.size() > 0)) {

                result = channels.toArray(new Channel[channels.size()]);
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
            final String lid = c.getListingId();

            List<Airing> airings = oc.query(new Predicate<Airing>() {

                public boolean match(Airing a) {

                    return ((id == a.getChannelId())
                        && (lid.equals(a.getListingId())));
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
                    case NMSConstants.SEARCH_TITLE_STARTS_WITH:
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
                            if (type == NMSConstants.SEARCH_TITLE_STARTS_WITH) {
                                result = search.startsWith(pat);
                            } else {
                                result = search.indexOf(pat) != -1;
                            }
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

                    log(DEBUG, "SD station count <" + coll.size() + ">");
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
                    log(DEBUG, "Channel count <" + clist.size() + ">");
                }
            }

            ArrayList<Channel> multiple = new ArrayList<Channel>();
            Map<String, Lineup> lmap = xtvd.getLineups();
            if (lmap != null) {

                Collection<Lineup> coll = lmap.values();
                if (coll != null) {

                    purge(oc, Listing.class);

                    // First go through the lineups and set the proper
                    // reference number.  A problem here could be that
                    // the user doesn't have an OTA lineup which would
                    // probably break things.  I guess we will have to
                    // use the callsign as a backup.
                    Iterator<Lineup> iter = coll.iterator();
                    int count = 0;
                    while (iter.hasNext()) {

                        Lineup lineup = iter.next();
                        count += processReferenceChannels(lineup, clist);
                    }
                    log(DEBUG, "Found <" + count + "> channels to reference");
                    log(DEBUG, "Channel count <" + clist.size() + ">");

                    // Now we go through again and if we have
                    // set the reference number we should be
                    // good to go.
                    iter = coll.iterator();
                    while (iter.hasNext()) {

                        Lineup lineup = iter.next();
                        Listing listing =
                            processLineup(lineup, clist, multiple);
                        oc.store(listing);
                    }
                    oc.commit();
                }
            }

            // We have built all the fields to the Channels we know about.
            // Now lets write then to the DB.
            log(DEBUG, "Multiple Channel count <" + multiple.size() + ">");
            for (int i = 0; i < multiple.size(); i++) {

                Channel tmp = multiple.get(i);
                oc.store(tmp);
            }
            oc.commit();

            // Process the Programs and put new Show instances into the
            // database.
            Map<String, Program> pmap = xtvd.getPrograms();
            log(DEBUG, "Program map <" + pmap + ">");
            if (pmap != null) {

                log(DEBUG, "Program map count <" + pmap.size() + ">");
                Collection<Program> coll = pmap.values();
                if (coll != null) {

                    log(DEBUG, "Program count <" + coll.size() + ">");
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

                log(DEBUG, "Schedule count <" + scheds.size() + ">");
                purge(oc, Airing.class);
                Iterator<Schedule> iter = scheds.iterator();
                while (iter.hasNext()) {

                    Schedule s = iter.next();

                    Channel[] array = getAllChannelsById(s.getStation());
                    if (array != null) {

                        for (int i = 0; i < array.length; i++) {

                            Airing airing = new Airing();
                            airing.setShowId(s.getProgram());
                            airing.setChannelId(array[i].getId());
                            airing.setListingId(array[i].getListingId());
                            airing.setAirDate(s.getTime().getLocalDate());
                            Duration dur = s.getDuration();
                            if (dur != null) {

                                int hours =
                                    Util.str2int(dur.getHours(), 0) * 3600;
                                int mins =
                                    Util.str2int(dur.getMinutes(), 0) * 60;
                                airing.setDuration((long) (hours + mins));
                                oc.store(airing);
                            }
                        }
                    }
                }

                oc.commit();
            }

            log(INFO, "Schedules Direct data process complete!");

            // Let others know...
            fireDataUpdateEvent();
        }
    }

    private int processReferenceChannels(Lineup l, ArrayList<Channel> list) {

        int result = 0;

        if ((l != null) && (list != null)) {

            Collection<net.sf.xtvdclient.xtvd.datatypes.Map> coll = l.getMaps();
            if (coll != null) {

                Iterator<net.sf.xtvdclient.xtvd.datatypes.Map> iter =
                    coll.iterator();
                while (iter.hasNext()) {

                    net.sf.xtvdclient.xtvd.datatypes.Map map = iter.next();
                    Channel found = findChannel(list, map.getStation());
                    if (found != null) {

                        if (found.getFrequency() != 0) {

                            int minor = map.getChannelMinor();
                            if (minor != 0) {

                                found.setNumber(map.getChannel() + "." + minor);
                                result++;
                            }
                        }
                    }
                }
            }
        }

        return (result);
    }

    private Listing processLineup(Lineup l, ArrayList<Channel> list,
        ArrayList<Channel> all) {

        Listing result = null;

        if ((l != null) && (list != null) && (all != null)) {

            result = new Listing();
            result.setName(l.getName());
            result.setId(l.getId());

            log(DEBUG, "Listing name <" + result.getName() + ">");
            log(DEBUG, "Listing id <" + result.getId() + ">");

            Collection<net.sf.xtvdclient.xtvd.datatypes.Map> coll = l.getMaps();
            if (coll != null) {

                Iterator<net.sf.xtvdclient.xtvd.datatypes.Map> iter =
                    coll.iterator();
                while (iter.hasNext()) {

                    net.sf.xtvdclient.xtvd.datatypes.Map map = iter.next();
                    Channel found = findChannel(list, map.getStation());
                    if (found != null) {

                        log(DEBUG, "Channel found " + found);
                        Channel copy = new Channel(found);
                        copy.setListingId(l.getId());
                        if (copy.getFrequency() != 0) {

                            int minor = map.getChannelMinor();
                            if (minor != 0) {

                                copy.setNumber(map.getChannel() + "." + minor);
                                copy.setReferenceNumber(copy.getNumber());

                            } else {

                                copy.setNumber(map.getChannel());
                                copy.setReferenceNumber(found.getNumber());
                            }

                        } else {

                            copy.setNumber(map.getChannel());
                            copy.setReferenceNumber(found.getNumber());
                        }

                        all.add(copy);

                    } else {

                        log(DEBUG, "Channel not found " + map.getStation());
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

    /**
     * {@inheritDoc}
     */
    public long getNextTimeToRun() {

        long result = -1L;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            ObjectSet<Status> os = oc.queryByExample(Status.class);
            if (os != null) {

                if (os.size() > 0) {

                    Status status = os.next();
                    result = status.getNextUpdate();
                }
            }
        }

        return (result);
    }

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

            if (isOverrideTimeToUpdate()) {

                setOverrideTimeToUpdate(false);
                status = new Status();
                status.setNextUpdate(System.currentTimeMillis());
                oc.store(status);
                oc.commit();
                log(INFO, "Time to update by override!");
                result = true;

            } else {

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

                // We want to update next tomorrow at around the
                // preferred update hour.
                int hcount = 24;
                int hour = getConfiguredUpdateHour();
                Calendar now = Calendar.getInstance();
                int hod = now.get(Calendar.HOUR_OF_DAY);
                if (hour > hod) {
                    hcount = 24 + (hour - hod);
                } else if (hod > hour) {
                    hcount = 24 - (hod - hour);
                }

                status.setNextUpdate(System.currentTimeMillis()
                    + (1000 * 3600 * hcount));
                purge(oc, Status.class);
                oc.store(status);
                oc.commit();
            }
        }
    }

}


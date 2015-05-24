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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sf.xtvdclient.xtvd.datatypes.Xtvd;
import net.sf.xtvdclient.xtvd.parser.Parser;
import net.sf.xtvdclient.xtvd.parser.ParserFactory;

import org.apache.commons.codec.binary.Hex;

import org.jflicks.tv.programdata.sd.json.Client;
import org.jflicks.tv.programdata.sd.json.GuideRequest;
import org.jflicks.tv.programdata.sd.json.Mapping;
import org.jflicks.tv.programdata.sd.json.Program;
import org.jflicks.tv.programdata.sd.json.StationID;
import org.jflicks.tv.programdata.sd.json.StationSchedule;
import org.jflicks.tv.programdata.sd.json.UserLineup;
import org.jflicks.util.Util;

/**
 * This class gets data from Schedules Direct.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SchedulesDirect {

    private File workingFile;
    private static HashMap<String, Program> programMap;
    private static SchedulesDirect instance;

    static {

        System.setProperty("propertiesDirectory", "conf");
        instance = new SchedulesDirect();
        if (!instance.readCache()) {

            instance.programMap = new HashMap<String, Program>();
        }
    }

    private SchedulesDirect() {
    }

    public static SchedulesDirect getInstance() {
        return (instance);
    }

    private boolean readCache() {

        boolean result = false;

        ObjectInputStream ois = null;

        try {

            File f = new File("programMap.ser");
            if (f.exists()) {

                FileInputStream fis = new FileInputStream(f);
                ois = new ObjectInputStream(fis);
                programMap = (HashMap<String, Program>) ois.readObject();
                result = true;
            }

        }  catch (IOException | ClassNotFoundException ex) {

            ex.printStackTrace();

        }  finally {
            
            if (ois != null) {

                try {

                    ois.close();

                } catch (IOException ex) {
                }
            }
        }

        return (result);
    }

    private void writeCache() {

        ObjectOutputStream oos = null;

        try {

            File f = new File("programMap.ser");
            FileOutputStream fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(programMap);

        }  catch (IOException ex) {

            System.out.println("writeCache: " + ex.getMessage());

        }  finally {
            
            if (oos != null) {

                try {

                    oos.close();

                } catch (IOException ex) {
                }
            }
        }
    }

    /**
     * The XML data is first downloaded and then parsed for information.
     *
     * @return A File instance.
     */
    public File getWorkingFile() {
        return (workingFile);
    }

    private void setWorkingFile(File f) {
        workingFile = f;
    }

    private HashMap<String, Program> getProgramMap() {
        return (programMap);
    }

    private void unmark() {

        Set<Map.Entry<String, Program>> set = programMap.entrySet();
        Iterator<Map.Entry<String, Program>> iter = set.iterator();
        while (iter.hasNext()) {

            Map.Entry<String, Program> me = iter.next();
            Program p = me.getValue();
            p.setMarked(false);
        }
    }

    private void mark(String pid) {

        if (pid != null) {

            Set<Map.Entry<String, Program>> set = programMap.entrySet();
            Iterator<Map.Entry<String, Program>> iter = set.iterator();
            while (iter.hasNext()) {

                Map.Entry<String, Program> me = iter.next();
                String key = me.getKey();
                if (pid.equals(key)) {

                    Program p = me.getValue();
                    p.setMarked(true);
                }
            }
        }
    }

    private void mark(String[] array) {

        if ((array != null) && (array.length > 0)) {

            for (int i = 0; i < array.length; i++) {

                mark(array[i]);
            }
        }
    }

    private void purge() {

        ArrayList<String> purgeList = new ArrayList<String>();
        Set<Map.Entry<String, Program>> set = programMap.entrySet();
        Iterator<Map.Entry<String, Program>> iter = set.iterator();
        while (iter.hasNext()) {

            Map.Entry<String, Program> me = iter.next();
            String pid = me.getKey();
            Program p = me.getValue();
            if (!p.isMarked()) {

                purgeList.add(pid);
            }
        }

        if (purgeList.size() > 0) {

            for (int i = 0; i < purgeList.size(); i++) {

                String pid = purgeList.get(i);
                programMap.remove(pid);
            }
        }
    }

    private boolean haveInCache(String pid, String md5) {

        boolean result = false;

        if ((pid != null) && (md5 != null)) {

            Set<Map.Entry<String, Program>> set = programMap.entrySet();
            Iterator<Map.Entry<String, Program>> iter = set.iterator();
            while (iter.hasNext()) {

                Map.Entry<String, Program> me = iter.next();
                String key = me.getKey();
                if (pid.equals(key)) {

                    Program p = me.getValue();
                    if (md5.equals(p.getMd5())) {

                        result = true;
                    }

                    break;
                }
            }
        }

        return (result);
    }

    private void putInCache(Program p) {

        if (p != null) {

            programMap.put(p.getProgramID(), p);
        }
    }

    private boolean isValidXtvdXml() {

        boolean result = false;

        File f = new File("conf/XTVD.xml");
        if ((f.exists()) && (f.isFile())) {

            byte[] data = Util.read(f);
            if (data != null) {

                String tmp = new String(data);
                if (tmp.indexOf("USER_NAME_FOR_SD") == -1) {

                    result = true;
                }
            }
        }

        return (result);
    }

    private StationID[] getStationIDsByLineupName(String name) {

        StationID[] result = null;

        if (name != null) {

            name = name + ".properties";
            System.out.println("looking to conf file <" + name + ">");
            File conf = new File("conf");
            File pfile = new File(conf, name);
            Properties p = Util.findProperties(pfile);
            if (p != null) {

                Set<String> set = p.stringPropertyNames();
                if (set != null) {

                    String[] tags = set.toArray(new String[set.size()]);
                    if ((tags != null) && (tags.length > 0)) {

                        result = new StationID[tags.length];
                        for (int i = 0; i < result.length; i++) {

                            String val = p.getProperty(tags[i]);
                            if (val != null) {

                                int index = val.indexOf("|");
                                if (index >= 0) {

                                    StationID tmp = new StationID();
                                    tmp.setStationID(tags[i]);
                                    tmp.setChannel(val.substring(0, index));
                                    result[i] = tmp;
                                }
                            }
                        }
                    }
                }
            }
        }

        return (result);
    }

    private boolean isWanted(StationID[] array, String sid, String channel) {

        boolean result = false;

        if ((array != null) && (array.length > 0) && (sid != null) && (channel != null)) {

            for (int i = 0; i < array.length; i++) {

                if ((sid.equals(array[i].getStationID())) && (channel.equals(array[i].getChannel()))) {

                    result = true;
                    break;
                }
            }
        }

        return (result);
    }

    private String[] getStationsByLineupName(String name) {

        String[] result = null;

        if (name != null) {

            name = name + ".properties";
            System.out.println("looking to conf file <" + name + ">");
            File conf = new File("conf");
            File pfile = new File(conf, name);
            Properties p = Util.findProperties(pfile);
            if (p != null) {

                Set<String> set = p.stringPropertyNames();
                if (set != null) {

                    result = set.toArray(new String[set.size()]);
                }
            }
        }

        return (result);
    }

    private ArrayList<String[]> computeList(String[] array, int max) {

        ArrayList<String[]> result = null;

        if ((array != null) && (array.length > 0)) {

            result = new ArrayList<String[]>();
            int count = array.length / max;
            if ((array.length % max) > 0) {
                count++;
            }

            int broken = 0;
            int start = 0;
            int end = max;
            for (int i = 0; i < count; i++) {

                if (end > array.length) {
                    end = array.length;
                }
                String[] sub = Arrays.copyOfRange(array, start, end);
                broken += sub.length;
                result.add(sub);
                end += max;
                start += max;
            }
        }

        return (result);
    }

    private Client getClient(String user, String password, String country, String zip) {

        Client result = null;

        if ((user != null) && (password != null) && (country != null) && (zip != null)) {

            try {

                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.reset();
                md.update(password.getBytes());
                String sha = new String(Hex.encodeHex(md.digest()));

                Client c = new Client();
                if (c.doToken(user, sha)) {

                    if (c.doStatus()) {

                        System.out.println("SD json server status ok\n");
                        if (c.doHeadend(country, zip)) {

                            result = c;
                        }
                    }
                }

            } catch (Exception ex) {

                System.out.println("getClient: " + ex.getMessage());
            }
        }

        return (result);
    }

    private String secondsToDuration(int seconds) {

        String result = null;

        StringBuilder sb = new StringBuilder("PT");
        int lsec = seconds;
        int hours = lsec / 3600;
        lsec = lsec % 3600;
        int minutes = lsec / 60;

        if (hours < 10) {
            sb.append("0");
        }

        sb.append(hours);
        sb.append("H");

        if (minutes < 10) {
            sb.append("0");
        }

        sb.append(minutes);
        sb.append("M");

        result = sb.toString();

        return (result);
    }

    /**
     * The major work method here will trigger a call to the web service
     * and parse the resulting data into the Xtvd object model.
     *
     * @return A Xtvd instance.
     */
    public Xtvd getXtvd(String user, String password, String country, String zipcode) {

        Xtvd result = null;

        if (isValidXtvdXml()) {

            // We now use the JSON service and make our own Xtvd
            // instance.  Then very little other code needs to change.
            result = new Xtvd();

            Client c = getClient(user, password, country, zipcode);
            if (c != null) {

                UserLineup ul = c.getUserLineup();
                if (ul != null) {

                    String[] lineupNames = handleLineup(result, c, ul.getLineups());
                    if ((lineupNames != null) && (lineupNames.length > 0)) {

                        ArrayList<net.sf.xtvdclient.xtvd.datatypes.Schedule> schedlist =
                            new ArrayList<net.sf.xtvdclient.xtvd.datatypes.Schedule>();
                        ArrayList<String> md5list = new ArrayList<String>();
                        for (int i = 0; i < lineupNames.length; i++) {

                            System.out.println("DEBUG: " + lineupNames[i]);
                            String[] sids = getStationsByLineupName(lineupNames[i]);
                            if ((sids != null) && (sids.length > 0)) {

                                GuideRequest[] grs = new GuideRequest[sids.length];
                                for (int j = 0; j < grs.length; j++) {

                                    grs[j] = new GuideRequest(sids[j]);
                                }

                                StationSchedule[] sched = c.getGuide(grs);
                                System.out.println("DEBUG: " + sched);
                                if ((sched != null) && (sched.length > 0)) {

                                    // Make an xtvd Schedule.
                                    for (int j = 0; j < sched.length; j++) {

                                        System.out.println("DEBUG: " + sched[j]);
                                        Program[] progs = sched[j].getPrograms();
                                        if ((progs != null) && (progs.length > 0)) {

                                            for (int k = 0; k < progs.length; k++) {

                                                net.sf.xtvdclient.xtvd.datatypes.Schedule xsched =
                                                    new net.sf.xtvdclient.xtvd.datatypes.Schedule();

                                                xsched.setStation(Util.str2int(sched[j].getStationID(), 0));

                                                xsched.setProgram(progs[k].getProgramID());

                                                try {

                                                    net.sf.xtvdclient.xtvd.datatypes.DateTime dt =
                                                        new net.sf.xtvdclient.xtvd.datatypes.DateTime(progs[k].getAirDateTime());
                                                    xsched.setTime(dt);

                                                } catch (Exception ex) {

                                                    System.out.println("getXtvd: " + ex.getMessage());
                                                }

                                                String durformat = secondsToDuration(progs[k].getDuration());
                                                net.sf.xtvdclient.xtvd.datatypes.Duration dur =
                                                    new net.sf.xtvdclient.xtvd.datatypes.Duration(durformat);

                                                xsched.setDuration(dur);
                                                schedlist.add(xsched);
                                                md5list.add(progs[k].getMd5());
                                            }
                                        }
                                    }

                                } else {

                                    System.out.println("NOT found sched");
                                }

                            } else {
                                System.out.println("NOT found conf file");
                            }
                        }

                        result.setSchedules(schedlist);

                        // First unmark all Programs in our map.
                        unmark();

                        // Now we have to get the programs
                        ArrayList<String> allPidlist = new ArrayList<String>();
                        ArrayList<String> pidlist = new ArrayList<String>();
                        for (int i = 0; i < schedlist.size(); i++) {

                            String pid = schedlist.get(i).getProgram();
                            String md5 = md5list.get(i);
                            allPidlist.add(pid);

                            //System.out.println("pid <" + pid + "><" + md5 + ">");
                            if (!haveInCache(pid, md5)) {

                                pidlist.add(pid);
                            }
                        }

                        // The pidlist has all the Programs we have to fetch.
                        System.out.println("we have to fetch " + pidlist.size() + " programs.");
                        if (pidlist.size() > 0) {

                            String[] parray = pidlist.toArray(new String[pidlist.size()]);
                            ArrayList<String[]> alist = computeList(parray, 1000);
                            if ((alist != null) && (alist.size() > 0)) {

                                for (int i = 0; i < alist.size(); i++) {

                                    String[] sub = alist.get(i);

                                    Program[] progs = c.getPrograms(sub);
                                    if ((progs != null) && (progs.length > 0)) {

                                        for (int j = 0; j < progs.length; j++) {

                                            putInCache(progs[j]);
                                        }
                                    }
                                }
                            }
                        }

                        // Next mark all Programs in our map.
                        String[] parray = allPidlist.toArray(new String[allPidlist.size()]);
                        mark(parray);

                        // Now purge old programs.
                        purge();

                        // We have to convert our cache programs to xtvd programs.
                        result.setPrograms(handlePrograms());

                        // Finally lets write the cache.
                        writeCache();
                    }
                }
            }

            /*
            try {

                buildInterface();
                getData();

                BufferedReader reader =
                    new BufferedReader(new InputStreamReader(
                        new FileInputStream(getWorkingFile()), "UTF-8"));

                result = new Xtvd();
                Parser parser = ParserFactory.getXtvdParser(reader, result);
                parser.parseXTVD();
                reader.close();

            } catch (DataDirectException ex) {
            } catch (FileNotFoundException ex) {
            } catch (UnsupportedEncodingException ex) {
            } catch (IOException ex) {
            } catch (Exception ex) {
            }
            */

        }

        return (result);
    }

    private String[] handleLineup(Xtvd xtvd, Client c, org.jflicks.tv.programdata.sd.json.Lineup[] array) {

        String[] result = null;

        if ((c != null) && (array != null) && (array.length > 0)) {

            ArrayList<String> lineuplist = new ArrayList<String>();
            HashMap<String, net.sf.xtvdclient.xtvd.datatypes.Lineup> hm =
                new HashMap<String, net.sf.xtvdclient.xtvd.datatypes.Lineup>();
            HashMap<Integer, net.sf.xtvdclient.xtvd.datatypes.Station> smap =
                new HashMap<Integer, net.sf.xtvdclient.xtvd.datatypes.Station>();

            for (int i = 0; i < array.length; i++) {

                net.sf.xtvdclient.xtvd.datatypes.Lineup l = new net.sf.xtvdclient.xtvd.datatypes.Lineup();

                System.out.println("BBBBBBBBBBBBB " + array[i].getName());
                String mapName = array[i].getName();
                System.out.println("BBBBBBBBBBBBB " + mapName);
                Mapping jmap = c.getMapping(mapName);
                String lineupValidName = null;
                lineuplist.add(array[i].toString());

                l.setId(array[i].getId());
                l.setName(array[i].toString());
                l.setLocation(array[i].getLocation());

                String type = array[i].getTransport();
                if (type != null) {

                    if (type.equalsIgnoreCase("cable")) {
                        l.setType(net.sf.xtvdclient.xtvd.datatypes.LineupTypes.CABLE);
                    } else if (type.equalsIgnoreCase("cable digital")) {
                        l.setType(net.sf.xtvdclient.xtvd.datatypes.LineupTypes.CABLE_DIGITAL);
                    } else if (type.equalsIgnoreCase("satellite")) {
                        l.setType(net.sf.xtvdclient.xtvd.datatypes.LineupTypes.SATELLITE);
                    } else if (type.equalsIgnoreCase("local broadcast")) {
                        l.setType(net.sf.xtvdclient.xtvd.datatypes.LineupTypes.LOCAL_BROADCAST);
                    } else {
                        l.setType(net.sf.xtvdclient.xtvd.datatypes.LineupTypes.LOCAL_BROADCAST);
                    }
                }
                if (jmap != null) {

                    ArrayList<net.sf.xtvdclient.xtvd.datatypes.Map> mlist = handleStationMap(jmap, array[i]);
                    l.setMaps(mlist);
                }
                hm.put(array[i].toString(), l);

                // Now set all the stations.
                handleStations(smap, jmap, l.getName());
            }

            xtvd.setLineups(hm);
            xtvd.setStations(smap);

            if (lineuplist.size() > 0) {

                result = lineuplist.toArray(new String[lineuplist.size()]);
            }
        }

        return (result);
    }

    private ArrayList<net.sf.xtvdclient.xtvd.datatypes.Map> handleStationMap(Mapping m,
        org.jflicks.tv.programdata.sd.json.Lineup l) {

        ArrayList<net.sf.xtvdclient.xtvd.datatypes.Map> result = null;

        if (m != null) {

            StationID[] sids = m.getMap();
            if ((sids != null) && (sids.length > 0)) {

                StationID[] wanted = getStationIDsByLineupName(l.toString());
                if ((wanted != null) && (wanted.length > 0)) {

                    result = new ArrayList<net.sf.xtvdclient.xtvd.datatypes.Map>();
                    for (int i = 0; i < sids.length; i++) {
                    
                        String currentsid = sids[i].getStationID();
                        String currentch = sids[i].getChannel();
                        if (currentch == null) {

                            currentch = sids[i].getAtscMajor() + "." + sids[i].getAtscMinor();
                        }
                        if (isWanted(wanted, currentsid, currentch)) {

                            net.sf.xtvdclient.xtvd.datatypes.Map map = new net.sf.xtvdclient.xtvd.datatypes.Map();
                            map.setStation(Util.str2int(currentsid, 0));
                            String ch = sids[i].getChannel();
                            if (ch == null) {
                                map.setChannel("" + sids[i].getAtscMajor());
                                map.setChannelMinor(sids[i].getAtscMinor());
                            } else {
                                map.setChannel(ch);
                            }

                            result.add(map);
                        }
                    }
                }
            }
        }

        return (result);
    }

    private void handleStations(HashMap<Integer, net.sf.xtvdclient.xtvd.datatypes.Station> hm, Mapping m, String lineupName) {

        System.out.println("BBBBBBBBBBBBB m " + m);
        System.out.println("BBBBBBBBBBBBB lineupName " + lineupName);
        if (m != null) {

            org.jflicks.tv.programdata.sd.json.Station[] array = getStations(m, lineupName);
            System.out.println("BBBBBBBBBBBBB array " + array);
            if ((array != null) && (array.length > 0)) {

                for (int i = 0; i < array.length; i++) {

                    int sid = Util.str2int(array[i].getStationID(), 0);
                    net.sf.xtvdclient.xtvd.datatypes.Station station =
                        new net.sf.xtvdclient.xtvd.datatypes.Station();
                    station.setId(sid);
                    station.setCallSign(array[i].getCallsign());
                    station.setName(array[i].getName());

                    org.jflicks.tv.programdata.sd.json.StationID sidobj = getStationID(m, array[i].getStationID());
                    if (sidobj != null) {

                        station.setFccChannelNumber(sidobj.getUhfVhf());
                    }
                    hm.put(Integer.valueOf(sid), station);
                }
            }
        }
    }

    private org.jflicks.tv.programdata.sd.json.Station[] getStations(Mapping m, String lineupName) {

        org.jflicks.tv.programdata.sd.json.Station[] result = null;

        if ((m != null) && (lineupName != null)) {

            String[] sids = getStationsByLineupName(lineupName);
            if ((sids != null) && (sids.length > 0)) {

                ArrayList<org.jflicks.tv.programdata.sd.json.Station> list =
                    new ArrayList<org.jflicks.tv.programdata.sd.json.Station>();
                for (int i = 0; i < sids.length; i++) {

                    org.jflicks.tv.programdata.sd.json.Station station = getStationById(m.getStations(), sids[i]);
                    if (station != null) {

                        list.add(station);
                    }
                }

                if (list.size() > 0) {

                    result = list.toArray(new org.jflicks.tv.programdata.sd.json.Station[list.size()]);
                }
            }
        }

        return (result);
    }

    private org.jflicks.tv.programdata.sd.json.Station getStationById(org.jflicks.tv.programdata.sd.json.Station[] array, String sid) {

        org.jflicks.tv.programdata.sd.json.Station result = null;

        if ((array != null) && (array.length > 0) && (sid != null)) {

            for (int i = 0; i < array.length; i++) {

                if (sid.equals(array[i].getStationID())) {

                    result = array[i];
                    break;
                }
            }
        }

        return (result);
    }

    private org.jflicks.tv.programdata.sd.json.StationID getStationID(Mapping m, String sid) {

        org.jflicks.tv.programdata.sd.json.StationID result = null;

        if ((m != null) && (sid != null)) {

            org.jflicks.tv.programdata.sd.json.StationID[] all = m.getMap();
            if ((all != null) && (all.length > 0)) {

                for (int i = 0; i < all.length; i++) {

                    if (sid.equals(all[i].getStationID())) {

                        result = all[i];
                        break;
                    }
                }
            }
        }

        return (result);
    }

    private HashMap<String, net.sf.xtvdclient.xtvd.datatypes.Program> handlePrograms() {

        HashMap<String, net.sf.xtvdclient.xtvd.datatypes.Program> result =
            new HashMap<String, net.sf.xtvdclient.xtvd.datatypes.Program>();

        Set<Map.Entry<String, Program>> set = programMap.entrySet();
        Iterator<Map.Entry<String, Program>> iter = set.iterator();
        while (iter.hasNext()) {

            Map.Entry<String, Program> me = iter.next();
            String pid = me.getKey();
            Program p = me.getValue();

            net.sf.xtvdclient.xtvd.datatypes.XtvdDate xdate = null;
            String orig = p.getOriginalDateTime();
            if (orig != null) {

                try {

                    xdate = new net.sf.xtvdclient.xtvd.datatypes.XtvdDate(orig);

                } catch (Exception ex) {

                    System.out.println("handlePrograms: " + ex.getMessage());
                }
            }

            net.sf.xtvdclient.xtvd.datatypes.Program xp = new net.sf.xtvdclient.xtvd.datatypes.Program();
            xp.setId(p.getProgramID());
            xp.setTitle(p.getBestTitle());
            xp.setSubtitle(p.getEpisodeTitle150());
            xp.setDescription(p.getBestDescription());
            xp.setShowType(p.getShowType());
            //xp.setSyndicatedEpisodeNumber(p.getEpisode());
            xp.setSeries(p.getSeriesId());
            xp.setOriginalAirDate(xdate);

            result.put(pid, xp);
        }

        return (result);
    }

    /**
     * Simple test main method.
     *
     * @param args Ignored arguments.
     * @throws Exception on any error.
     */
    public static void main(String[] args) throws Exception {

        SchedulesDirect sd = new SchedulesDirect();
        Xtvd xtvd = sd.getXtvd("djb61230@yahoo.com", "sd8662", "USA", "12095");

        Map map = xtvd.getStations();
        if (map != null) {

            Collection coll = map.values();
            if (coll != null) {

                System.out.println("SD station count <" + coll.size() + ">");
            }
        }
    }

}


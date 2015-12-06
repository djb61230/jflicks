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
package org.jflicks.tv.programdata.sd.json;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;

/**
 * Manage your settings at Schedules Direct JSON server..
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Manage {

    /**
     * Simple empty constructor.
     */
    public Manage() {
    }

    public Client connect(String user, String password) throws NoSuchAlgorithmException {

        Client result = null;

        if ((user != null) && (password != null)) {

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.reset();
            md.update(password.getBytes());
            String sha = new String(Hex.encodeHex(md.digest()));

            Client c = new Client();
            if (c.doToken(user, sha)) {

                if (c.doStatus()) {

                    System.err.println("status ok\n");
                    result = c;
                }
            }
        }

        return (result);
    }

    public Lineup[] listAllLineups(Client c, String country, String zip) {

        Lineup[] result = null;

        if (c != null) {

            if (c.doHeadend(country, zip)) {

                HeadendObject[] array = c.getHeadendObjects();
                if ((array != null) && (array.length > 0)) {

                    ArrayList<Lineup> l = new ArrayList<Lineup>();
                    for (int i = 0; i < array.length; i++) {

                        Lineup[] lups = array[i].getLineups();
                        if ((lups != null) && (lups.length > 0)) {

                            for (int j = 0; j < lups.length; j++) {

                                l.add(lups[j]);
                            }
                        }
                    }

                    if (l.size() > 0) {

                        result = l.toArray(new Lineup[l.size()]);
                    }
                }

            } else {

                throw new RuntimeException("No headends found!");
            }
        }

        return (result);
    }

    public Lineup[] listAddedLineups(Client c) {

        Lineup[] result = null;

        if (c != null) {

            UserLineup ul = c.getUserLineup();
            if (ul != null) {

                result = ul.getLineups();
            }
        }

        return (result);
    }

    public LineupResponse addAntennaLineup(Client c, String country, String zip) {

        LineupResponse result = null;

        if (c != null) {

            if (c.doHeadend(country, zip)) {

                if (c.doAddLineup("Antenna", zip)) {

                    result = c.getLineupResponse();
                }
            }
        }

        return (result);
    }

    public String getAntennaStationConfig(Client c, String country, String zip) {

        String result = null;

        if (c != null) {

            if (c.doHeadend(country, zip)) {

                StringBuilder text = new StringBuilder();
                Mapping mapping = c.getMapping("Local Over the Air Broadcast");
                if (mapping != null) {

                    Station[] sarray = mapping.getStations();
                    if ((sarray != null) && (sarray.length > 0)) {

                        for (int i = 0; i < sarray.length; i++) {

                            StationID sid = mapping.getStationID(sarray[i].getStationID());
                            if (sid != null) {

                                String chan = sid.getChannel();
                                if (chan == null) {

                                    chan = sid.getAtscMajor() + "." + sid.getAtscMinor();
                                }
                                if ((!chan.equals("0.0"))) {

                                    text.append(sid.getStationID());
                                    text.append("=");
                                    text.append(chan);
                                    text.append("|");
                                    text.append(sarray[i].getName());
                                    text.append("\n");
                                }
                            }
                        }

                    } else {

                        throw new RuntimeException("Failed to get stations.");
                    }

                } else {

                    throw new RuntimeException("Failed to get station mapping.");
                }

                if (text.length() > 0) {

                    result = text.toString();
                }

            } else {

                throw new RuntimeException("Failed check country or zip");
            }
        }

        return (result);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {

        final String LIST_ALL_LINEUPS = "listAllLineups";
        final String LIST_ADDED_LINEUPS = "listAddedLineups";
        final String ADD_LINEUP = "addLineup";
        final String DELETE_LINEUP = "deleteLineup";
        final String LIST_STATIONS = "listStations";
        final String GUIDE = "guide";
        final String PROGRAM = "program";
        final String AUTOMAP = "automap";

        String user = null;
        String password = null;
        //String action = LIST_ALL_LINEUPS;
        //String action = LIST_ADDED_LINEUPS;
        //String action = ADD_LINEUP;
        //String action = DELETE_LINEUP;
        //String action = LIST_STATIONS;
        //String action = GUIDE;
        String action = PROGRAM;
        String country = "USA";
        String zip = "12095";
        String lineup = null;
        String location = null;
        String automapJson = null;
        ArrayList<GuideRequest> glist = new ArrayList<GuideRequest>();
        ArrayList<String> plist = new ArrayList<String>();

        for (int i = 0; i < args.length; i += 2) {

            if (args[i].equalsIgnoreCase("-u")) {

                user = args[i + 1];

            } else if (args[i].equalsIgnoreCase("-p")) {

                password = args[i + 1];

            } else if (args[i].equalsIgnoreCase("-l")) {

                location = args[i + 1];

            } else if (args[i].equalsIgnoreCase("-action")) {

                action = args[i + 1];

            } else if (args[i].equalsIgnoreCase("-country")) {

                country = args[i + 1];

            } else if (args[i].equalsIgnoreCase("-zipcode")) {

                zip = args[i + 1];

            } else if (args[i].equalsIgnoreCase("-lineup")) {

                lineup = args[i + 1];

            } else if (args[i].equalsIgnoreCase("-automapFile")) {

                File jsonData = new File(args[i + 1]);
                if ((jsonData.exists()) && (jsonData.isFile())) {

                    try {

                        automapJson = FileUtils.readFileToString(jsonData);

                    } catch (IOException ex) {
                    }
                }

            } else if (args[i].equalsIgnoreCase("-sid")) {

                String sid = args[i + 1];
                GuideRequest gr = new GuideRequest();
                gr.setStationID(sid);
                glist.add(gr);

            } else if (args[i].equalsIgnoreCase("-pid")) {

                String pid = args[i + 1];
                plist.add(pid);
            }
        }

        if ((user != null) && (password != null)) {

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.reset();
            md.update(password.getBytes());
            String sha = new String(Hex.encodeHex(md.digest()));

            Client c = new Client();
            if (c.doToken(user, sha)) {

                if (c.doStatus()) {

                    System.err.println("status ok\n");

                    // Now perform the desired action.
                    switch (action) {

                    case LIST_ALL_LINEUPS:
                        if (c.doHeadend(country, zip)) {

                            HeadendObject[] array = c.getHeadendObjects();
                            if ((array != null) && (array.length > 0)) {

                                for (int i = 0; i < array.length; i++) {

                                    Lineup[] lups = array[i].getLineups();
                                    if ((lups != null) && (lups.length > 0)) {

                                        for (int j = 0; j < lups.length; j++) {

                                            String lupname = lups[j].getName();
                                            lupname = lupname.replaceAll(" ", "-");
                                            System.err.println("lineup=" + lups[j].getLineup() + " name=" + lupname + " location=" + array[i].getLocation());
                                        }
                                    }
                                }

                                System.err.println("\nUse the name and location to add the lineup.");
                                System.err.println("Plus use the name and location to remove it later if you want.");
                            }

                        } else {

                            System.err.println("No headends found!");
                        }

                        break;

                    case LIST_ADDED_LINEUPS:

                        UserLineup ul = c.getUserLineup();
                        if (ul != null) {

                            Lineup[] lups = ul.getLineups();
                            if ((lups != null) && (lups.length > 0)) {

                                for (int i = 0; i < lups.length; i++) {

                                    String lupname = lups[i].getName();
                                    lupname = lupname.replaceAll(" ", "-");
                                    System.err.println(lups[i].getLineup() + " name=" + lupname + " transport=" + lups[i].getTransport() + " location=" + lups[i].getLocation());
                                }
                            }

                        } else {

                            System.err.println("No user lineups configured.");
                        }

                        break;

                    case ADD_LINEUP:

                        if (c.doHeadend(country, zip)) {

                            if (c.doAddLineup(lineup, location)) {

                                LineupResponse lr = c.getLineupResponse();
                                if (lr != null) {

                                    System.err.println(lr.getMessage());
                                }

                            } else {

                                System.err.println("Failed to add lineup.");
                            }

                        } else {

                            System.err.println("Failed check country or zip");
                        }

                        break;

                    case DELETE_LINEUP:

                        if (c.doHeadend(country, zip)) {

                            if (c.doDeleteLineup(lineup, location)) {

                                LineupResponse lr = c.getLineupResponse();
                                if (lr != null) {

                                    System.err.println(lr.getMessage());
                                }

                            } else {

                                System.err.println("Failed to delete lineup.");
                            }

                        } else {

                            System.err.println("Failed check country or zip");
                        }

                        break;

                    case LIST_STATIONS:

                        if (c.doHeadend(country, zip)) {

                            Mapping mapping = c.getMapping(lineup);
                            if (mapping != null) {

                                Station[] sarray = mapping.getStations();
                                if ((sarray != null) && (sarray.length > 0)) {

                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 0; i < sarray.length; i++) {

                                        StationID sid =
                                            mapping.getStationID(sarray[i].getStationID());
                                        if (sid != null) {

                                            sb.setLength(0);
                                            sb.append(sid.getStationID());
                                            sb.append("=");
                                            String chan = sid.getChannel();
                                            if (chan == null) {

                                                chan = sid.getAtscMajor() + "."
                                                    + sid.getAtscMinor();
                                            }
                                            sb.append(chan);
                                            sb.append("|");
                                            sb.append(sarray[i].getName());
                                            System.err.println(sb.toString());
                                        }
                                    }

                                } else {

                                    System.err.println("Failed to get stations.");
                                }

                            } else {

                                System.err.println("Failed to get station mapping.");
                            }

                        } else {

                            System.err.println("Failed check country or zip");
                        }

                        break;

                    case GUIDE:

                        if (glist.size() > 0) {

                            GuideRequest[] garray = glist.toArray(new GuideRequest[glist.size()]);
                            StationSchedule[] scheds = c.getGuide(garray);

                        } else {

                            System.err.println("Need Station IDs to get guide.");
                        }

                        break;

                    case PROGRAM:

                        if (plist.size() > 0) {

                            System.err.println(c.getToken());
                            String[] parray = plist.toArray(new String[plist.size()]);
                            Program[] progs = c.getPrograms(parray);
                            System.err.println(progs);

                        } else {

                            System.err.println("Need Program IDs to get them.");
                        }

                        break;

                    case AUTOMAP:

                        if (automapJson != null) {
                            System.out.println(c.doAutomap(automapJson));
                        }

                        break;
                    }

                } else {

                    System.err.println("SD server OFFLINE, try later.");
                }

            } else {

                System.err.println("Failed to get token check user/password");
            }

        } else {

            System.err.println("user or password null.");
        }
    }

}


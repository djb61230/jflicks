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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Hex;

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

    public static void main(String[] args) throws NoSuchAlgorithmException {

        final String LIST_ALL_LINEUPS = "listAllLineups";
        final String LIST_ADDED_LINEUPS = "listAddedLineups";
        final String ADD_LINEUP = "addLineup";
        final String DELETE_LINEUP = "deleteLineup";
        final String LIST_STATIONS = "listStations";
        final String GUIDE = "guide";
        final String PROGRAM = "program";

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

                    System.out.println("status ok\n");

                    // Now perform the desired action.
                    switch (action) {

                    case LIST_ALL_LINEUPS:
                        if (c.doHeadend(country, zip)) {

                            Headend[] array = c.getHeadends();
                            if ((array != null) && (array.length > 0)) {

                                for (int i = 0; i < array.length; i++) {

                                    Lineup[] lups = array[i].getLineups();
                                    if ((lups != null) && (lups.length > 0)) {

                                        for (int j = 0; j < lups.length; j++) {

                                            System.out.println(lups[j] + " location=" + array[i].getLocation());
                                        }
                                    }
                                }
                            }

                        } else {

                            System.out.println("No headends found!");
                        }

                        break;

                    case LIST_ADDED_LINEUPS:

                        UserLineup ul = c.getUserLineup();
                        if (ul != null) {

                            Lineup[] lups = ul.getLineups();
                            if ((lups != null) && (lups.length > 0)) {

                                for (int i = 0; i < lups.length; i++) {

                                    System.out.println(lups[i] + " name=" + lups[i].getName() + " id=" + lups[i].getId());
                                }
                            }

                        } else {

                            System.out.println("No user lineups configured.");
                        }

                        break;

                    case ADD_LINEUP:

                        if (c.doHeadend(country, zip)) {

                            if (c.doAddLineup(lineup, location)) {

                                LineupResponse lr = c.getLineupResponse();
                                if (lr != null) {

                                    System.out.println(lr.getMessage());
                                }

                            } else {

                                System.out.println("Failed to add lineup.");
                            }

                        } else {

                            System.out.println("Failed check country or zip");
                        }

                        break;

                    case DELETE_LINEUP:

                        if (c.doHeadend(country, zip)) {

                            if (c.doDeleteLineup(lineup, location)) {

                                LineupResponse lr = c.getLineupResponse();
                                if (lr != null) {

                                    System.out.println(lr.getMessage());
                                }

                            } else {

                                System.out.println("Failed to delete lineup.");
                            }

                        } else {

                            System.out.println("Failed check country or zip");
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
                                            System.out.println(sb.toString());
                                        }
                                    }

                                } else {

                                    System.out.println("Failed to get stations.");
                                }

                            } else {

                                System.out.println("Failed to get station mapping.");
                            }

                        } else {

                            System.out.println("Failed check country or zip");
                        }

                        break;

                    case GUIDE:

                        if (glist.size() > 0) {

                            GuideRequest[] garray = glist.toArray(new GuideRequest[glist.size()]);
                            StationSchedule[] scheds = c.getGuide(garray);

                        } else {

                            System.out.println("Need Station IDs to get guide.");
                        }

                        break;

                    case PROGRAM:

                        if (plist.size() > 0) {

                            System.out.println(c.getToken());
                            String[] parray = plist.toArray(new String[plist.size()]);
                            Program[] progs = c.getPrograms(parray);
                            System.out.println(progs);

                        } else {

                            System.out.println("Need Program IDs to get them.");
                        }

                        break;
                    }

                } else {

                    System.out.println("SD server OFFLINE, try later.");
                }

            } else {

                System.out.println("Failed to get token check user/password");
            }

        } else {

            System.out.println("user or password null.");
        }
    }

}


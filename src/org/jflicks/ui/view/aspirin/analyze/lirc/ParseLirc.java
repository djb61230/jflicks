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
package org.jflicks.ui.view.aspirin.analyze.lirc;

import java.io.File;
import java.util.ArrayList;

import org.jflicks.util.Util;

/**
 * This is a utility class to parse a lircd.conf file.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ParseLirc {

    private ArrayList<Remote> remoteList;

    /**
     * Simple one argument constructor.
     *
     * @param path The file path to read.
     */
    public ParseLirc(String path) {

        setRemoteList(new ArrayList<Remote>());
        parse(path);
    }

    private ArrayList<Remote> getRemoteList() {
        return (remoteList);
    }

    private void setRemoteList(ArrayList<Remote> l) {
        remoteList = l;
    }

    /**
     * All the current Remote instances defined.
     *
     * @return An array of Remote instances.
     */
    public Remote[] getRemotes() {

        Remote[] result = null;

        ArrayList<Remote> l = getRemoteList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Remote[l.size()]);
        }

        return (result);
    }

    /**
     * Convenience method to add a Remote.
     *
     * @param r A given Remote to add.
     */
    public void addRemote(Remote r) {

        ArrayList<Remote> l = getRemoteList();
        if ((l != null) && (r != null)) {

            if (!l.contains(r)) {

                l.add(r);
            }
        }
    }

    private void parse(String path) {

        if (path != null) {

            File fpath = new File(path);
            if ((fpath.exists()) && (fpath.isFile())) {

                String[] lines = Util.readTextFile(fpath);
                if ((lines != null) && (lines.length > 0)) {

                    Remote current = null;
                    boolean remoteMode = false;
                    boolean codeMode = false;
                    for (int i = 0; i < lines.length; i++) {

                        String line = lines[i].trim();
                        if (!line.startsWith("#")) {

                            // We have a non-comment line...
                            // First see if it's an include line...
                            if (line.startsWith("include")) {

                                // We have to recursely call parse...
                                line = line.substring(7);
                                line = line.trim();
                                if (line.startsWith("\"")) {

                                    line = line.substring(1, line.length() - 1);
                                    parse(line);
                                }

                            } else if ((line.startsWith("begin"))
                                && (line.indexOf("remote") != -1)) {

                                // We have found a Remote...
                                remoteMode = true;
                                current = new Remote();

                            } else if ((line.startsWith("begin"))
                                && (line.indexOf("codes") != -1)) {

                                codeMode = true;

                            } else if ((line.startsWith("end"))
                                && (line.indexOf("codes") != -1)) {

                                codeMode = false;

                            } else if ((line.startsWith("end"))
                                && (line.indexOf("remote") != -1)) {

                                codeMode = false;
                                remoteMode = false;
                                if (current != null) {

                                    addRemote(current);
                                }

                            } else {

                                // We have another line we need to process.
                                if ((remoteMode) && (!codeMode)) {

                                    // We have parse the beginning of a
                                    // remote but have not reached the
                                    // codes.  We only care about the
                                    // name of the remote.
                                    if (line.startsWith("name")) {

                                        line = line.substring(4);
                                        line = line.trim();
                                        if (current != null) {

                                            current.setName(line);
                                        }
                                    }

                                } else if ((remoteMode) && (codeMode)) {

                                    // We are in code mode so add the
                                    // button.
                                    line = line.substring(0, line.indexOf(" "));
                                    if (current != null) {

                                        current.addButton(line.trim());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}


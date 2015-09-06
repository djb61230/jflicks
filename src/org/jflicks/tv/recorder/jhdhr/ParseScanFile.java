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
package org.jflicks.tv.recorder.jhdhr;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.jflicks.util.Util;

/**
 * Class that can parse a scan log file from an HDHR
 * and create a channel conf file.  Best used for cable/QAM
 * as OTA channels don't need to be scanned.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ParseScanFile {

    private HashMap<String, Integer> channelHashMap;

    /**
     * Constructor with one argument.
     *
     * @param file The File pointing to a scan log file.
     */
    public ParseScanFile(File file) {

        build(file);
    }

    private HashMap<String, Integer> getChannelHashMap() {
        return (channelHashMap);
    }

    private void setChannelHashMap(HashMap<String, Integer> hm) {
        channelHashMap = hm;
    }

    public String[] getKeys() {

        String[] result = null;

        if ((channelHashMap != null) && (channelHashMap.size() > 0)) {

            Set<String> set = channelHashMap.keySet();
            result = set.toArray(new String[channelHashMap.size()]);
        }

        return (result);
    }

    /**
     * Convenience method to get a frequency given a channel name.
     *
     * @param s A given channel as a String.
     * @return An int representting a frequency.
     */
    public int get(String s) {

        int result = -1;

        HashMap<String, Integer> hm = getChannelHashMap();
        if ((hm != null) && (s != null)) {

            Integer iobj = hm.get(s);
            if (iobj != null) {

                result = iobj.intValue();
            }
        }

        return (result);
    }

    private void build(File scan) {

        // We read a file in a path scan log file.
        if ((scan != null) && (scan.exists()) && (scan.isFile())) {

            // Ok it exists...
            String[] lines = Util.readTextFile(scan);
            if (lines != null) {

                HashMap<String, Integer> hm = new HashMap<String, Integer>();
                String lastScanning = null;
                String lastLock = null;
                for (int i = 0; i < lines.length; i++) {

                    if (lines[i].startsWith("SCANNING:")) {
                        lastScanning = lines[i];
                    } else if (lines[i].startsWith("LOCK:")) {
                        lastLock = lines[i];
                    } else if (lines[i].startsWith("PROGRAM")) {

                        // We found a potential channel.
                        int index = lines[i].indexOf("encrypted");
                        if (index == -1) {

                            // We could have an unencrypted one...
                            index = lines[i].indexOf("control");
                            if (index == -1) {

                                // Isn't a control...in our test
                                // file some program lines just had
                                // a zero.  We won't worry about them
                                // as we should not be given a channel
                                // 0.
                                String third = parseProgram(lines[i]);
                                if (third != null) {

                                    // Looks like we have a channel
                                    Integer freq = parseScanning(lastScanning);
                                    if (freq != null) {

                                        // Put in HashMap...
                                        if (!third.equals("0")) {

                                            hm.put(third, freq);
                                            System.out.println("key: <" + third + ">");
                                            System.out.println("val: <" + freq + ">");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (hm.size() > 0) {

                    setChannelHashMap(hm);
                }

            } else {

                if (scan  != null) {

                    System.out.println(scan.getPath() + " not found");
                }
            }
        }
    }

    private String parseProgram(String s) {

        String result = null;

        if (s != null) {

            StringTokenizer st = new StringTokenizer(s);
            if (st.countTokens() >= 3) {

                // Throw away...
                result = st.nextToken();

                // Throw away...
                result = st.nextToken();

                // Keep...
                result = st.nextToken();
            }
        }

        return (result);
    }

    private Integer parseScanning(String s) {

        Integer result = null;

        if (s != null) {

            StringTokenizer st = new StringTokenizer(s);
            if (st.countTokens() >= 2) {

                // Throw away...
                String tmp = st.nextToken();

                // Keep...
                tmp = st.nextToken();
                int val = Util.str2int(tmp, -1);
                if (val != -1) {

                    result = Integer.valueOf(val);
                }
            }
        }

        return (result);
    }

    public static void main(String[] args) {

        File f = new File("/home/djb/dave_tw.txt");
        ParseScanFile psf = new ParseScanFile(f);
    }

}


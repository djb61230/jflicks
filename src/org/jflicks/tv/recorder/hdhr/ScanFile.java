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
package org.jflicks.tv.recorder.hdhr;

import java.io.File;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.jflicks.util.Util;

/**
 * Class that can record from an HDHR.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ScanFile {

    private HashMap<String, Integer> channelHashMap;

    /**
     * Simple default constructor.
     */
    public ScanFile(String device) {

        build(device);
    }

    private HashMap<String, Integer> getChannelHashMap() {
        return (channelHashMap);
    }

    private void setChannelHashMap(HashMap<String, Integer> hm) {
        channelHashMap = hm;
    }

    /**
     * Given a channel number as a String (2.1, 6.2 etc) return the
     * frequency number.
     *
     * @param number The channel number as a String.
     * @return An int representing the cable system frequency.
     */
    public int getFrequency(String number) {

        int result = 0;

        HashMap<String, Integer> hm = getChannelHashMap();
        if ((number != null) && (hm != null)) {

            Integer iobj = hm.get(number);
            if (iobj != null) {

                result = iobj.intValue();
            }
        }

        return (result);
    }

    private void build(String device) {

        // We read a file in a path conf/device-scan.log
        if (device != null) {

            File conf = new File("conf");
            if ((conf.exists()) && (conf.isDirectory())) {

                File scan = getScanFile(conf, device);
                if ((scan.exists()) && (scan.isFile())) {

                    // Ok it exists...
                    String[] lines = Util.readTextFile(scan);
                    if (lines != null) {

                        HashMap<String, Integer> hm =
                            new HashMap<String, Integer>();
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
                                            Integer freq =
                                                parseScanning(lastScanning);
                                            if (freq != null) {

                                                // Put in HashMap...
                                                hm.put(third, freq);
                                                System.out.println("put in <"
                                                    + third + "> " + freq);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (hm.size() > 0) {

                            setChannelHashMap(hm);
                        }
                    }

                } else {

                    System.out.println(scan.getPath() + " not found");
                }
            }
        }
    }

    private File getScanFile(File dir, String device) {

        File result = null;

        boolean generic = true;
        result = new File(dir, device + "-scan.log");
        if ((result.exists()) && (result.isFile())) {

            generic = false;
        }

        if (generic) {

            result = new File(dir, "hdhr-scan.log");
        }

        return (result);
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

        new ScanFile("fred");
    }

}


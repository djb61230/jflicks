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

import org.jflicks.util.Util;

/**
 * Class that can get frequency and program names from a config file for
 * an HDHR.  This is our own invention but inspired by similar files for
 * DVB devices.  The config file is not needed for OTA, just cable/QAM.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ScanFile {

    private HashMap<String, String> channelHashMap;

    /**
     * Constructor with one argument.
     *
     * @param device The HDHomrun device or ID.
     */
    public ScanFile(String device) {

        build(device);
    }

    private HashMap<String, String> getChannelHashMap() {
        return (channelHashMap);
    }

    private void setChannelHashMap(HashMap<String, String> hm) {
        channelHashMap = hm;
    }

    /**
     * Given a channel number as a String (866, 867 etc) return the
     * frequency number.
     *
     * @param number The channel number as a String.
     * @return An int representing the cable system frequency.
     */
    public int getFrequency(String number) {

        int result = 0;

        HashMap<String, String> hm = getChannelHashMap();
        if ((number != null) && (hm != null)) {

            String val = hm.get(number);
            if (val != null) {

                int index = val.indexOf(":");
                if (index != -1) {

                    index++;
                    String tmp = val.substring(index);
                    if (tmp != null) {

                        tmp = tmp.trim();
                        result = Util.str2int(tmp, result);
                    }
                }
            }
        }

        return (result);
    }

    /**
     * Given a channel number as a String (866, 867 etc) return the
     * program as a String.
     *
     * @param number The channel number as a String.
     * @return A String representing the program.
     */
    public String getProgram(String number) {

        String result = null;

        HashMap<String, String> hm = getChannelHashMap();
        if ((number != null) && (hm != null)) {

            String val = hm.get(number);
            if (val != null) {

                int index = val.indexOf(":");
                if (index != -1) {

                    result = val.substring(0, index);
                    if (result != null) {

                        result = result.trim();
                    }
                }
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

                        HashMap<String, String> hm =
                            new HashMap<String, String>();
                        for (int i = 0; i < lines.length; i++) {

                            int index = lines[i].indexOf("=");
                            if (index != -1) {

                                String tag = lines[i].substring(0, index);
                                String val = lines[i].substring(index + 1);
                                tag = tag.trim();
                                val = val.trim();
                                hm.put(tag, val);
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
        result = new File(dir, device + "-scan.conf");
        if ((result.exists()) && (result.isFile())) {

            generic = false;
        }

        if (generic) {

            result = new File(dir, "hdhr-scan.conf");
        }

        return (result);
    }

}


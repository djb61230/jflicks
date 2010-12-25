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
package org.jflicks.ui.view.aspirin.analyze.dvbscan;

import org.jflicks.util.Util;

/**
 * This bean encapsulates the basic properties for a found dvb device.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DvbPath {

    private String path;

    /**
     * Simple no argument constructor.
     */
    public DvbPath() {
    }

    /**
     * Dvb devices are in /dev/dvb/adapterN/dvrM.
     *
     * @return the path of the device.
     */
    public String getPath() {
        return (path);
    }

    /**
     * Dvb devices are in /dev/dvb/adapterN/dvrM.
     *
     * @param s the path of the device.
     */
    public void setPath(String s) {
        path = s;
    }

    public String getChannelScanFileName() {

        String result = null;

        String dname = getPath();
        if (dname != null) {

            int adapter = getAdapterNumber(dname);
            int dvr = getDvrNumber(dname);
            if ((adapter != -1) && (dvr != -1)) {

                result = "adapter" + adapter + "_dvr" + dvr + "_channels.conf";
            }
        }

        return (result);
    }

    private int getAdapterNumber(String s) {

        int result = -1;

        if (s != null) {

            int index = s.indexOf("adapter");
            if (index != -1) {

                index += 7;
                int lastIndex = s.indexOf("/", index);
                if ((lastIndex != -1) && (lastIndex >= index)) {

                    result =
                        Util.str2int(s.substring(index, lastIndex), result);
                }
            }
        }

        return (result);
    }

    private int getDvrNumber(String s) {

        int result = -1;

        if (s != null) {

            int index = s.indexOf("dvr");
            if (index != -1) {

                index += 3;
                result = Util.str2int(s.substring(index), result);
            }
        }

        return (result);
    }

}

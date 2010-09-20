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
package org.jflicks.tv.recorder.v4l2;

import java.io.File;
import java.io.FilenameFilter;

import org.jflicks.util.Util;

/**
 * Given a set of extensions, create a FilenameFilter.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DevVideoFilter implements FilenameFilter {

    private int max;

    /**
     * Simple constructor.
     */
    public DevVideoFilter() {

        setMax(4);
    }

    /**
     * Contructor accepting one argument.
     *
     * @param max The Max video device to accept.
     */
    public DevVideoFilter(int max) {

        setMax(max);
    }

    /**
     * Some video devices are actually FM or other things.  Using this
     * property can eliminate those.
     *
     * @return A max device number.
     */
    public int getMax() {
        return (max);
    }

    /**
     * Some video devices are actually FM or other things.  Using this
     * property can eliminate those.
     *
     * @param i A max device number.
     */
    public void setMax(int i) {
        max = i;
    }

    /**
     * Check a File.
     *
     * @param dir The directory where is located (we assume /dev).
     * @param name The name of the file.
     * @return True if in the form videoN where N is < Max.
     */
    public boolean accept(File dir, String name) {

        boolean result = false;

        if (name != null) {

            if (name.startsWith("video")) {

                int m = getMax();
                String rest = name.substring(5);
                int num = Util.str2int(rest, m);
                result = (num < m);
            }
        }

        return (result);
    }

}

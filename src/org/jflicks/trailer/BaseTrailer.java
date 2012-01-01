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
package org.jflicks.trailer;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import org.jflicks.configure.BaseConfig;
import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.util.Util;

/**
 * This class is a base implementation of the Trailer interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseTrailer extends BaseConfig implements Trailer {

    private String title;
    private NMS nms;
    private int maxTrailerCount;

    /**
     * Simple empty constructor.
     */
    public BaseTrailer() {
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return (title);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given title value.
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * {@inheritDoc}
     */
    public NMS getNMS() {
        return (nms);
    }

    /**
     * {@inheritDoc}
     */
    public void setNMS(NMS n) {
        nms = n;
    }

    /**
     * Convenience method to find any RSS feeds configured for this Web
     * implementation.
     *
     * @return An Array of URL strings.
     */
    public String[] getConfiguredFeeds() {

        String[] result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(NMSConstants.WEB_RSS_FEEDS);
            if (nv != null) {

                result = nv.valueToArray();
            }
        }

        return (result);
    }

    /**
     * Convenience method to find the configured max trailer count
     * property.
     *
     * @return An int.
     */
    public int getConfiguredMaxTrailerCount() {

        int result = 50;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv =
                c.findNameValueByName(NMSConstants.MAX_TRAILER_COUNT);
            if (nv != null) {

                result = Util.str2int(nv.getValue(), result);
            }
        }

        return (result);
    }

    /**
     * Convenience method that extensions can use to get the Trailer home
     * from the NMS.
     *
     * @return A trailer home path.
     */
    public String getTrailerHome() {

        String result = null;

        NMS n = getNMS();
        if (n != null) {

            result = n.getTrailerHome();
        }

        return (result);
    }

    public void autoExpire() {

        // We will do the deleting straight away hopefully without
        // disrupting things too much.
        String thome = getTrailerHome();
        if (thome != null) {

            int max = getConfiguredMaxTrailerCount();
            File dir = new File(thome);
            File[] all = dir.listFiles();
            if ((all != null) && (all.length > 0) && (all.length > max)) {

                Arrays.sort(all, new FileOldestSort());
                int count = all.length - max;
                for (int i = 0; i < count; i++) {

                    if (!all[i].delete()) {

                        log(WARNING, all[i].getPath() + " delete fail");
                    }
                }
            }
        }
    }

    static class FileOldestSort implements Comparator<File>, Serializable {

        public int compare(File f0, File f1) {

            Long l0 = Long.valueOf(f0.lastModified());
            Long l1 = Long.valueOf(f1.lastModified());

            return (l0.compareTo(l1));
        }
    }

}


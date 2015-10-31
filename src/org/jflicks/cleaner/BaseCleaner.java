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
package org.jflicks.cleaner;

import org.jflicks.configure.BaseConfig;
import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.util.Util;

/**
 * A base implementation of the Photo interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseCleaner extends BaseConfig implements Cleaner {

    private String title;
    private NMS nms;

    /**
     * Simple constructor.
     */
    public BaseCleaner() {
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return (title);
    }

    /**
     * Convenience method tp set the title property.
     *
     * @param s A given title as a String.
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
     * {@inheritDoc}
     */
    public int getTimeBetweenCleanings() {
        return (getConfiguredTimeBetweenCleanings());
    }

    protected int getConfiguredTimeBetweenCleanings() {

        int result = 3600;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(NMSConstants.CLEANER_TIME_BETWEEN_CLEANINGS);
            if (nv != null) {

                result = Util.str2int(nv.getValue(), result);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public int getRecordingMinimumAge() {
        return (getConfiguredRecordingMinimumAge());
    }

    protected int getConfiguredRecordingMinimumAge() {

        int result = 28800;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(NMSConstants.CLEANER_RECORDING_MINIMUM_AGE);
            if (nv != null) {

                result = Util.str2int(nv.getValue(), result);
            }
        }

        return (result);
    }

}


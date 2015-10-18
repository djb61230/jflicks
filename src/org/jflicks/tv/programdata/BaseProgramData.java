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
package org.jflicks.tv.programdata;

import java.util.ArrayList;

import org.jflicks.configure.BaseConfig;
import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.nms.NMSConstants;
import org.jflicks.util.Util;

/**
 * This class is a base implementation of the ProgramData interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseProgramData extends BaseConfig
    implements ProgramData {

    private ArrayList<DataUpdateListener> dataUpdateList = new ArrayList<DataUpdateListener>();

    private String title;

    /**
     * Simple empty constructor.
     */
    public BaseProgramData() {
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

    public String getConfiguredUserName() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv =
                c.findNameValueByName(NMSConstants.USER_NAME);
            if (nv != null) {

                result = nv.getValue();
            }
        }

        return (result);
    }

    public String getConfiguredPassword() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv =
                c.findNameValueByName(NMSConstants.PASSWORD);
            if (nv != null) {

                result = nv.getValue();
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public int getConfiguredUpdateHour() {

        int result = 4;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv =
                c.findNameValueByName(NMSConstants.UPDATE_HOUR);
            if (nv != null) {

                result = Util.str2int(nv.getValue(), result);
            }
        }

        return (result);
    }

    public String getConfiguredCountry() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv =
                c.findNameValueByName(NMSConstants.COUNTRY);
            if (nv != null) {

                result = nv.getValue();
            }
        }

        return (result);
    }

    public String getConfiguredZipCode() {

        String result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv =
                c.findNameValueByName(NMSConstants.ZIP_CODE);
            if (nv != null) {

                result = nv.getValue();
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void addDataUpdateListener(DataUpdateListener l) {
        dataUpdateList.add(l);
    }

    /**
     * {@inheritDoc}
     */
    public void removeDataUpdateListener(DataUpdateListener l) {
        dataUpdateList.remove(l);
    }

    /**
     * Convenience method to fire an event with a certain type and state.
     */
    public void fireDataUpdateEvent() {
        processDataUpdateEvent(new DataUpdateEvent(this));
    }

    /**
     * Convenience method to fire a given event instance.
     *
     * @param event A given event.
     */
    public void fireDataUpdateEvent(DataUpdateEvent event) {
        processDataUpdateEvent(event);
    }

    protected synchronized void processDataUpdateEvent(DataUpdateEvent event) {

        synchronized (dataUpdateList) {

            for (int i = 0; i < dataUpdateList.size(); i++) {

                DataUpdateListener l = dataUpdateList.get(i);
                l.dataUpdate(event);
            }
        }
    }

}


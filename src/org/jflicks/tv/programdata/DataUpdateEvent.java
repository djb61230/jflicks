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

import java.awt.AWTEvent;

/**
 * We try to capture all the properties one needs to manage a data update
 * event.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DataUpdateEvent extends AWTEvent {

    private boolean freshData;

    /**
     * Constructor to take the source.
     *
     * @param source The source of the event.
     */
    public DataUpdateEvent(DataUpdateable source) {

        super(source, -1);
        setFreshData(true);
    }

    /**
     * Constructor to supply source and freshness flag.
     *
     * @param source The source of the event.
     * @param freshData True when the data is all fresh.
     */
    public DataUpdateEvent(DataUpdateable source, boolean freshData) {

        super(source, -1);
        setFreshData(freshData);
    }

    public boolean isFreshData() {
        return (freshData);
    }

    private void setFreshData(boolean b) {
        freshData = b;
    }

}

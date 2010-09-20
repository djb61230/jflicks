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
package org.jflicks.trailer.apple;

/**
 * This class maintains when we should and when we last updated from
 * Apple.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Status {

    private long nextUpdate;
    private long lastUpdate;

    /**
     * Simple empty constructor.
     */
    public Status() {
    }

    /**
     * This is the number of milliseconds defining the next time we need to
     * update from Schedules Direct.  The time is the standard Unix time
     * format.
     *
     * @return A long value.
     */
    public long getNextUpdate() {
        return (nextUpdate);
    }

    /**
     * This is the number of milliseconds defining the next time we need to
     * update from Schedules Direct.  The time is the standard Unix time
     * format.
     *
     * @param l A long value.
     */
    public void setNextUpdate(long l) {
        nextUpdate = l;
    }

    /**
     * This is the number of milliseconds defining the last time we
     * updated from Schedules Direct.  The time is the standard Unix time
     * format and we keep this for informational purposes.
     *
     * @return A long value.
     */
    public long getLastUpdate() {
        return (lastUpdate);
    }

    /**
     * This is the number of milliseconds defining the last time we
     * updated from Schedules Direct.  The time is the standard Unix time
     * format and we keep this for informational purposes.
     *
     * @param l A long value.
     */
    public void setLastUpdate(long l) {
        lastUpdate = l;
    }

    /**
     * Just figure out if NOW is the time to update.
     *
     * @return True if it's time to update.
     */
    public boolean isTimeToUpdate() {

        boolean result = false;

        long now = System.currentTimeMillis();
        if (now > getNextUpdate()) {

            result = true;
        }

        return (result);
    }

}


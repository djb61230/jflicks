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
package org.jflicks.tv.scheduler;

import java.awt.geom.Line2D;

/**
 * This class defines a range of time between two Date instances.  Their
 * are methods to determine if any two time ranges "intersect" and by
 * how much.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class TimeRange implements Comparable<TimeRange> {

    private long startTime;
    private long endTime;

    /**
     * Simple constructor with required arguments.
     *
     * @param start A given start date.
     * @param end A given end date.
     */
    public TimeRange(long start, long end) {

        setStartTime(start);
        setEndTime(end);
    }

    /**
     * A time range needs a beginning date.
     *
     * @return A start date.
     */
    public long getStartTime() {
        return (startTime);
    }

    /**
     * A time range needs a beginning date.
     *
     * @param l A start date.
     */
    public void setStartTime(long l) {
        startTime = l;
    }

    /**
     * A time range needs a ending date.
     *
     * @return An end date.
     */
    public long getEndTime() {
        return (endTime);
    }

    /**
     * A time range needs a ending date.
     *
     * @param l An end date.
     */
    public void setEndTime(long l) {
        endTime = l;
    }

    /**
     * Does the given time range overlap with us.
     *
     * @param tr A given TimeRange instance.
     * @return True if any time is shared between the two time ranges.
     */
    public boolean overlaps(TimeRange tr) {

        boolean result = false;

        if (tr != null) {

            // Lets use the Line2D stuff to figure this out, no sense in
            // reinventing what was is already done for us.
            long start = getStartTime();
            long end = getEndTime();
            long trstart = tr.getStartTime();
            long trend = tr.getEndTime();

            result = Line2D.linesIntersect((double) start, 0.0, (double) end,
                0.0, (double) trstart, 0.0, (double) trend, 0.0);
        }

        return (result);
    }

    /**
     * Override the hashcode.
     *
     * @return An int value.
     */
    public int hashCode() {

        Long obj = Long.valueOf(getStartTime());
        return (obj.hashCode());
    }

    /**
     * The equals override method.
     *
     * @param o A gven object to check.
     * @return True if the objects are equal.
     */
    public boolean equals(Object o) {

        boolean result = false;

        if (o == this) {

            result = true;

        } else if (!(o instanceof TimeRange)) {

            result = false;

        } else {

            TimeRange tr = (TimeRange) o;

            Long start0 = Long.valueOf(getStartTime());
            Long start1 = Long.valueOf(tr.getStartTime());
            if ((start0 != null) && (start1 != null)) {

                result = start0.equals(start1);
            }
        }

        return (result);
    }

    /**
     * The comparable interface.
     *
     * @param tr The given TimeRange instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(TimeRange tr) throws ClassCastException {

        int result = 0;

        if (tr == null) {

            throw new NullPointerException();
        }

        if (tr == this) {

            result = 0;

        } else {

            Long start0 = Long.valueOf(getStartTime());
            Long start1 = Long.valueOf(tr.getStartTime());
            if ((start0 != null) && (start1 != null)) {

                result = start0.compareTo(start1);
            }
        }

        return (result);
    }

}


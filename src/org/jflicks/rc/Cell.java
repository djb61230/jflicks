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
package org.jflicks.rc;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * Currently we define a new object with a particular hot point inside
 * of it.  Currently we do not use it and perhaps with can go away and
 * we use Rectangle directly.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Cell extends Rectangle {

    private Point hotPoint;

    /**
     * Default empty constructor.
     */
    public Cell() {
    }

    /**
     * A hot point inside this cell.
     *
     * @return A Point.
     */
    public Point getHotPoint() {
        return (hotPoint);
    }

    /**
     * A hot point inside this cell.
     *
     * @param p A Point.
     */
    public void setHotPoint(Point p) {
        hotPoint = p;
    }

    /**
     * Just user the super hashCode.
     *
     * @return A hash code value.
     */
    public int hashCode() {
        return (super.hashCode());
    }

    /**
     * Check to see if our hot point property is equals also.
     *
     * @param o Cell instance to compare.
     * @return True if equal.
     * @throws ClassCastException When given a bogus object.
     */
    public boolean equals(Object o) throws ClassCastException {

        boolean result = super.equals(o);

        if ((result) && (o instanceof Cell)) {

            Cell cell = (Cell) o;
            Point p = getHotPoint();
            Point cellp = cell.getHotPoint();
            if ((p != null) && (cellp != null)) {

                result = p.equals(cell.getHotPoint());
            }
        }

        return (result);
    }

}


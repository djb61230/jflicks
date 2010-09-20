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
package org.jflicks.ui.view.fe;

import org.jflicks.tv.Upcoming;

/**
 * A screen that can disply upcoming recording information needs to implement
 * this interface so the front end can notify it of the upcoming recordings.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface UpcomingProperty {

    /**
     * A Screen might support the display of future recording.
     *
     * @return The Upcoming instances.
     */
    Upcoming[] getUpcomings();

    /**
     * A Screen might support the display of future recording.
     *
     * @param array The Recording instances.
     */
    void setUpcomings(Upcoming[] array);
}


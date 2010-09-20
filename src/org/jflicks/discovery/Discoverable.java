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
package org.jflicks.discovery;

/**
 * The event interface for receiving discover events.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Discoverable {

    /**
     * Add a listener.
     *
     * @param l The given listener object.
     */
    void addDiscoverListener(DiscoverListener l);

    /**
     * Remove a listener.
     *
     * @param l The given listener object.
     */
    void removeDiscoverListener(DiscoverListener l);
}

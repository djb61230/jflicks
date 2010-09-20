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
package org.jflicks.db;

import com.db4o.osgi.Db4oService;

/**
 * An interface objects can implement to get access to Db4o services.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface DbWorker {

    /**
     * The Db4o service instance.
     *
     * @return The Db4o service instance.
     */
    Db4oService getDb4oService();

    /**
     * The Db4o service instance.
     *
     * @param s The Db4o service instance.
     */
    void setDb4oService(Db4oService s);
}

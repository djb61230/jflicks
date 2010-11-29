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
package org.jflicks.log;

import org.osgi.util.tracker.ServiceTracker;

/**
 * This interface gives objects an easier way to log.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Log {

    /**
     * We need a service tracker to maintain a connection to the log service.
     *
     * @return A ServiceTracker instance.
     */
    ServiceTracker getLogServiceTracker();

    /**
     * We need a service tracker to maintain a connection to the log service.
     *
     * @param st A ServiceTracker instance.
     */
    void setLogServiceTracker(ServiceTracker st);

    /**
     * @param level The log level to use.
     * @param message The message to log.
     */
    void log(int level, String message);
}


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

import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This class implements the Log interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class BaseLog implements Log {

    /**
     * As a convenience we have a constant that maps to LogService.LOG_DEBUG.
     */
    public static final int DEBUG = LogService.LOG_DEBUG;

    /**
     * As a convenience we have a constant that maps to LogService.LOG_INFO.
     */
    public static final int INFO = LogService.LOG_INFO;

    /**
     * As a convenience we have a constant that maps to LogService.LOG_WARNING.
     */
    public static final int WARNING = LogService.LOG_WARNING;

    /**
     * As a convenience we have a constant that maps to LogService.LOG_ERROR.
     */
    public static final int ERROR = LogService.LOG_ERROR;

    private ServiceTracker logServiceTracker;

    /**
     * Simple empty constructor.
     */
    public BaseLog() {
    }

    /**
     * {@inheritDoc}
     */
    public ServiceTracker getLogServiceTracker() {
        return (logServiceTracker);
    }

    /**
     * {@inheritDoc}
     */
    public void setLogServiceTracker(ServiceTracker st) {
        logServiceTracker = st;
    }

    /**
     * {@inheritDoc}
     */
    public void log(int level, String message) {

        ServiceTracker st = getLogServiceTracker();
        if ((st != null) && (message != null)) {

            LogService ls = (LogService) st.getService();
            if (ls != null) {

                ls.log(level, message);
            }
        }
    }

}

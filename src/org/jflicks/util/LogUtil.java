package org.jflicks.util;

import org.osgi.service.log.LogService;

/**
 * A LogService utility class.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class LogUtil {

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

    private LogUtil() {
    }

    public static void log(int level, String message) {

        LogService ls = BundleUtil.getLogService();
        if (ls != null) {

            ls.log(level, message);

        } else {

            System.err.println("No LogService: " + message);
        }
    }

}

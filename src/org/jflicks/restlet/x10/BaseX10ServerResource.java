package org.jflicks.restlet.x10;

import org.jflicks.nms.NMS;
import org.jflicks.restlet.BaseServerResource;

/**
 * This base class has a been of methods that interact with the X10
 * class, which in turn interacts with OSGi and our IOTA modules.  This
 * insulates extensions from worrying all about those things so they can
 * just concentrate on doing the right RESTlet stuff.
 *
 * @author Doug Barnum
 * @version 3.0
 */
public abstract class BaseX10ServerResource extends BaseServerResource {

    private static X10 x10;

    /**
     * Simple empty constructor.
     */
    public BaseX10ServerResource() {
    }

    /**
     * We use the X10 instance to do the real work.
     *
     * @return An X10 instance.
     */
    public static X10 getX10() {
        return (x10);
    }

    /**
     * We use the X10 instance to do the real work.
     *
     * @param x An X10 instance.
     */
    public static void setX10(X10 x) {
        x10 = x;
    }

    public NMS[] getNMS() {

        NMS[] result = null;

        if (x10 != null) {

            result = x10.getNMS();
        }

        return (result);
    }

    public String getBaseURI() {

        String result = null;

        if (x10 != null) {

            result = x10.getBaseURI();
        }

        return (result);
    }

    public void log(int level, String message) {

        X10 app = getX10();
        if (app != null) {

            app.log(level, message);
        }
    }

    public void openTheater() {

        X10 app = getX10();
        if (app != null) {

            app.openTheater();
        }
    }

    public void closeTheater() {

        X10 app = getX10();
        if (app != null) {

            app.closeTheater();
        }
    }

    public void startMovie() {

        X10 app = getX10();
        if (app != null) {

            app.startMovie();
        }
    }

    public void stopMovie() {

        X10 app = getX10();
        if (app != null) {

            app.stopMovie();
        }
    }

}

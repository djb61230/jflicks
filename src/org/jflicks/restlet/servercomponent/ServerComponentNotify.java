package org.jflicks.restlet.servercomponent;

/**
 * The ServerComponentNotify is a get/set pair for classes that
 * want to be notified by an ServerComponentTracker.
 *
 * @author Doug Barnum
 * @version 3.0
 */
public interface ServerComponentNotify {

    /**
     * The ServerComponent property.
     *
     * @return The ServerComponent instance.
     */
    ServerComponent getServerComponent();

    /**
     * The ServerComponent property.
     *
     * @param sc The ServerComponent instance.
     */
    void setServerComponent(ServerComponent sc);
}

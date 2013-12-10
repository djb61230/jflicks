package org.jflicks.restlet.servercomponent;

import org.restlet.Component;

/**
 * A ServerComponent service supplies a Component that can be used by jflicks
 * restlets to attach.  The idea is we would have a single restlet server
 * that could handle more than one restlet application.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface ServerComponent {

    /**
     * A unique Id for the server component.
     *
     * @return A String instance.
     */
    String getId();

    /**
     * The actual Verifier instance that does the job.
     *
     * @return A Verifier instance.
     */
    Component getComponent();

    /**
     * A ServerComponent can supply it's current base URI.
     *
     * @return A String instance.
     */
    String getBaseURI();
}

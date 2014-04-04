package org.jflicks.restlet.admin;

import java.io.File;
import java.net.URI;

import org.jflicks.restlet.BaseApplication;

import org.restlet.Request;
import org.restlet.Restlet;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;

/**
 * This class is the main RESTlet application for testing FreeMarker.
 *
 * @author Doug Barnum
 * @version 3.0
 */
public class Admin extends BaseApplication {

    /**
     * Simple default constructor.
     */
    public Admin() {

        setName("Web Admin Application");
        setDescription("Using jquerymobile.");
        setOwner("jflicks");
        setAuthor("The jflicks team");
        setAlias("admin");
    }

    /**
     * Our create method we need to override to start up our
     * RESTlet application.
     *
     * @return A Restlet instance.
     */
    @Override
    public Restlet createInboundRoot() {

        // Create a router
        Router router = new Router(getContext());

        File admin = new File("admin");
        File jqm = new File(admin, "jqm");

        jqm = jqm.getAbsoluteFile();

        URI uri = jqm.toURI();
        router.attach("/jqm", new Directory(getContext(), uri.toString()));

        return (router);
    }

}

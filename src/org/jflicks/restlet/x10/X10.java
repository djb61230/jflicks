package org.jflicks.restlet.x10;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jflicks.restlet.BaseApplication;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.restlet.Request;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;

/**
 * This class is the main RESTlet application for testing FreeMarker.
 *
 * @author Doug Barnum
 * @version 3.0
 */
public class X10 extends BaseApplication {

    /**
     * Simple default constructor.
     */
    public X10() {

        setName("X10 Application");
        setDescription("Control lights via X10");
        setOwner("jflicks");
        setAuthor("The jflicks team");
        setAlias("x10");
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

        // Attach the resources to the router
        router.attach("/{ver}/open", OpenResource.class);
        router.attach("/{ver}/close", CloseResource.class);
        router.attach("/{ver}/start", StartResource.class);
        router.attach("/{ver}/stop", StopResource.class);

        return (router);
    }

    public void openTheater() {

        String[] array = {

            "bin/x10-theater-open.sh",
        };

        Heyu heyu = new Heyu(array);
        heyu.process();
    }

    public void closeTheater() {

        String[] array = {

            "bin/x10-theater-close.sh",
        };

        Heyu heyu = new Heyu(array);
        heyu.process();
    }

    public void startMovie() {

        String[] array = {

            "bin/x10-movie-start.sh",
        };

        Heyu heyu = new Heyu(array);
        heyu.process();
    }

    public void stopMovie() {

        String[] array = {

            "bin/x10-movie-stop.sh",
        };

        Heyu heyu = new Heyu(array);
        heyu.process();
    }

}

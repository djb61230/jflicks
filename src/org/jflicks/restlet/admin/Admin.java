package org.jflicks.restlet.admin;

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
public class Admin extends BaseApplication {

    private Configuration configuration;

    /**
     * Simple default constructor.
     */
    public Admin() {

        setName("Web Admin Application");
        setDescription("Using FreeMarker.");
        setOwner("jflicks");
        setAuthor("The jflicks team");
        setAlias("admin");

        Configuration cfg = new Configuration();
        cfg.addAutoImport("layout", "layout/defaultLayout.ftl");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);

        File admin = new File("admin");

        try {

            cfg.setDirectoryForTemplateLoading(admin);
            setConfiguration(cfg);

        } catch (Exception ex) {

            log(DEBUG, ex.getMessage());
        }
    }

    public Configuration getConfiguration() {
        return (configuration);
    }

    private void setConfiguration(Configuration c) {
        configuration = c;
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
        router.attach("/home", HomeResource.class);
        router.attach("/config", ConfigResource.class);
        router.attach("/vm", VideoManagerResource.class);
        router.attach("/recordings", RecordingsResource.class);
        router.attach("/upcoming", UpcomingResource.class);
        router.attach("/about", AboutResource.class);

        File admin = new File("admin");
        File common = new File(admin, "common");
        File theme = new File(admin, "theme");
        File images = new File(admin, "images");
        File css = new File(admin, "css");
        File js = new File(admin, "js");

        common = common.getAbsoluteFile();
        theme = theme.getAbsoluteFile();
        images = images.getAbsoluteFile();
        css = css.getAbsoluteFile();
        js = js.getAbsoluteFile();

        URI uri = common.toURI();
        router.attach("/common", new Directory(getContext(), uri.toString()));

        uri = theme.toURI();
        router.attach("/theme", new Directory(getContext(), uri.toString()));

        uri = images.toURI();
        router.attach("/images", new Directory(getContext(), uri.toString()));

        uri = css.toURI();
        router.attach("/css", new Directory(getContext(), uri.toString()));

        uri = js.toURI();
        router.attach("/js", new Directory(getContext(), uri.toString()));

        return (router);
    }

}

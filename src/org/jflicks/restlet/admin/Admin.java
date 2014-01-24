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

        File templates = new File("templates");
        File admin = new File(templates, "admin");

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
        router.attach("/{ver}/home.html", HomeResource.class);
        router.attach("/{ver}/config.html", ConfigResource.class);
        router.attach("/{ver}/recordings.html", RecordingsResource.class);

        File templates = new File("templates");
        File admin = new File(templates, "admin");
        File resources = new File(admin, "resources");
        File images = new File(resources, "images");
        File css = new File(resources, "css");
        File js = new File(resources, "js");

        images = images.getAbsoluteFile();
        css = css.getAbsoluteFile();
        js = js.getAbsoluteFile();

        URI uri = images.toURI();
        router.attach("/{ver}/images",
            new Directory(getContext(), uri.toString()));

        uri = css.toURI();
        router.attach("/{ver}/css",
            new Directory(getContext(), uri.toString()));

        uri = js.toURI();
        router.attach("/{ver}/js",
            new Directory(getContext(), uri.toString()));

        return (router);
    }

}

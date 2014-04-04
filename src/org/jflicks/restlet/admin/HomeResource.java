package org.jflicks.restlet.admin;

import java.util.HashMap;

import org.jflicks.nms.State;
import org.jflicks.restlet.NMSSupport;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.resource.Get;
import org.restlet.representation.Representation;

/**
 * We display state information on the home page.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class HomeResource extends BaseAdminServerResource {

    /**
     * Simple default constructor.
     */
    public HomeResource() {

        setName("home");
        setDescription("home");
    }

    /**
     * Get the currently defined Sources represented in json or xml.
     *
     * @return A Representation instance.
     */
    @Get
    public Representation get() {

        Representation result = null;

        Configuration c = getConfiguration();  
        if (c != null) {

            HashMap<String, Object> root = new HashMap<String, Object>();

            NMSSupport nsup = NMSSupport.getInstance();

            State state = nsup.getState();

            if (state != null) {

                root.put("state", state);
            }

            root.put("homeClass", "class=\"selected\"");
            root.put("configClass", "");
            root.put("vmClass", "");
            root.put("recordingsClass", "");
            root.put("upcomingClass", "");
            root.put("aboutClass", "");

            Template temp = TemplateRepresentation.getTemplate(c, "home.ftl");
            TemplateRepresentation rep = new TemplateRepresentation(temp,
                root, MediaType.TEXT_HTML); 
            result = rep;
        }

        return (result);
    }

}

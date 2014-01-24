package org.jflicks.restlet.admin;

import java.util.HashMap;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.resource.Get;
import org.restlet.representation.Representation;

/**
 * This resource handles User requests.  The request returns info
 * about the current user.
 *
 * @author Doug Barnum
 * @version 3.0
 */
public class ConfigResource extends BaseAdminServerResource {

    /**
     * Simple default constructor.
     */
    public ConfigResource() {

        setName("config");
        setDescription("config");
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
            Template temp = TemplateRepresentation.getTemplate(c, "config.ftl");
            TemplateRepresentation rep = new TemplateRepresentation(temp,
                root, MediaType.TEXT_HTML); 
            result = rep;
        }

        return (result);
    }

}

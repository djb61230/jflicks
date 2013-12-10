package org.jflicks.restlet.admin;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.resource.Get;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;

/**
 * This resource handles User requests.  The request returns info
 * about the current user.
 *
 * @author Doug Barnum
 * @version 3.0
 */
public class TestResource extends BaseAdminServerResource {

    /**
     * Simple default constructor.
     */
    public TestResource() {

        setName("test");
        setDescription("test");
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

            Template temp = TemplateRepresentation.getTemplate(c, "home.ftl");
            TemplateRepresentation rep = new TemplateRepresentation(temp,
                null, MediaType.TEXT_HTML); 
            //rep.setCharacterSet(CharacterSet.UTF_8); 
            result = rep;
        }

        return (result);
    }

    /**
     * Improve RESTlet API documentation generation for our get
     * method.
     *
     * @param info A MethodInfo instance to configure.
     */
    public void describeGet(MethodInfo info) {

        info.setIdentifier("user");
        info.setDocumentation("Retrieve info.");

        RepresentationInfo repInfo =
            new RepresentationInfo(MediaType.TEXT_HTML);
        repInfo.setXmlElement("user");
        repInfo.setDocumentation("User information");
        info.getResponse().getRepresentations().add(repInfo);
    }

}

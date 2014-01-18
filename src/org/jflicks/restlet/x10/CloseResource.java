package org.jflicks.restlet.x10;

import org.restlet.resource.Get;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

/**
 *
 * @author Doug Barnum
 * @version 3.0
 */
public class CloseResource extends BaseX10ServerResource {

    /**
     * Simple default constructor.
     */
    public CloseResource() {

        setName("close");
        setDescription("close");
    }

    /**
     * Get the currently defined Sources represented in json or xml.
     *
     * @return A Representation instance.
     */
    @Get
    public Representation get() {

        Representation result = new StringRepresentation("OK");

        closeTheater();

        return (result);
    }

}

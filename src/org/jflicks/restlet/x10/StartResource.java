package org.jflicks.restlet.x10;

import org.jflicks.restlet.BaseServerResource;

import org.restlet.resource.Get;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

/**
 *
 * @author Doug Barnum
 * @version 3.0
 */
public class StartResource extends BaseServerResource {

    /**
     * Simple default constructor.
     */
    public StartResource() {

        setName("start");
        setDescription("start");
    }

    /**
     * Get the currently defined Sources represented in json or xml.
     *
     * @return A Representation instance.
     */
    @Get
    public Representation get() {

        Representation result = new StringRepresentation("OK");

        String[] array = {

            "bin/x10-movie-start.sh",
        };

        Heyu heyu = new Heyu(array);
        heyu.process();

        return (result);
    }

}

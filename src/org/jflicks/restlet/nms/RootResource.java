/*
    This file is part of JFLICKS.

    JFLICKS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JFLICKS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JFLICKS.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.jflicks.restlet.nms;

import org.restlet.data.MediaType;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

/**
 * This class will return the current recordings as XML or JSON.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RootResource extends BaseNMSApplicationServerResource {

    /**
     * Simple empty constructor.
     */
    public RootResource() {
    }

    @Override
    protected RepresentationInfo describe(MethodInfo methodInfo,
        Class<?> representationClass, Variant variant) {

        RepresentationInfo result = super.describe(methodInfo,
            representationClass, variant);
        result.setMediaType(MediaType.TEXT_PLAIN);
        result.setIdentifier("root");

        DocumentationInfo doc = new DocumentationInfo();
        doc.setTitle("jflicks media system application");
        doc.setTextContent("Simple string welcoming the user to the"
            + " jflicks media system application");
        result.getDocumentations().add(doc);

        return (result);
    }

    @Override

    protected void doInit() throws ResourceException {

        setAutoDescribing(false);
        setName("Root resource");
        setDescription("The root resource of the jflicks media system"
            + " application");
    }

    public String represent() {

        return "Welcome to the " + getApplication().getName() + " !";
    }


}


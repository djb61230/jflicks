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
package org.jflicks.stream.http;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.jflicks.nms.NMS;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

/**
 * This is a base class servlet that will handle the lower level chores.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseFeed extends HttpServlet
    implements HttpServiceProperty {

    private String alias;
    private HttpService httpService;
    private BundleContext bundleContext;

    /**
     * Default constructor.
     */
    public BaseFeed() {
    }

    /**
     * Constructor with one required argument.
     *
     * @param s A given alias name.
     */
    public BaseFeed(String s) {

        setAlias(s);
    }

    public BundleContext getBundleContext() {
        return (bundleContext);
    }

    public void setBundleContext(BundleContext bc) {
        bundleContext = bc;
    }

    /**
     * A feed servlet has an alias name property.
     *
     * @return A String instance.
     */
    public String getAlias() {
        return (alias);
    }

    /**
     * A feed servlet has an alias name property.
     *
     * @param s A String instance.
     */
    public void setAlias(String s) {
        alias = s;
    }

    /**
     * A tracker updates us when the http service comes and goes.
     *
     * @return The OSGi HttpService instance.
     */
    public HttpService getHttpService() {
        return (httpService);
    }

    /**
     * A tracker updates us when the http service comes and goes.
     *
     * @param hs The OSGi HttpService instance.
     */
    public void setHttpService(HttpService hs) {

        httpService = hs;
        if (httpService != null) {

            try {

                httpService.registerServlet("/" + getAlias(), this, null, null);

            } catch (ServletException ex) {

                System.out.println("BaseFeed: " + ex.getMessage());

            } catch (NamespaceException ex) {

                System.out.println("NamespaceException: " + ex.getMessage());
            }
        }
    }

    public NMS getNMS() {

        NMS result = null;

        BundleContext bc = getBundleContext();
        if (bc != null) {

            ServiceReference ref = bc.getServiceReference(NMS.class.getName());
            if (ref != null) {

                result = (NMS) bc.getService(ref);
            }
        }

        return (result);
    }

    public String getBaseURL(HttpServletRequest req) {

        String result = null;

        if (req != null) {

            String uri = req.getRequestURI();
            String url = req.getRequestURL().toString();
            if ((uri != null) && (url != null)) {

                int index = url.indexOf(uri);

                if (index != -1) {

                    result = url.substring(0, index);

                } else {

                    result = url;
                }
            }
        }

        return (result);
    }

}

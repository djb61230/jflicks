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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

/**
 * This is a servlet that will return an RSS feed of our recordings which
 * should be consumable by boxee.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class BoxeeFeed extends HttpServlet implements HttpServiceProperty {

    private HttpService httpService;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sb.append("<rss version=\"2.0\" xmlns:boxee=\"http://boxee.tv/rss\"");
        sb.append(" xmlns:media=\"http://search.yahoo.com/mrss/\">\n");
        sb.append("    <channel>\n");
        sb.append("        <title>JFLICKS</title>\n");
        sb.append("        <link>http://192.168.2.2/boxeefeed</link>\n");
        sb.append("        <description>TV from JFLICKS</description>\n");
        sb.append("        <language>en-us</language>\n");
        sb.append("        <item>\n");
        sb.append("            <title>Seinfeld</title>\n");
        sb.append("            <guid>burp</guid>\n");
        sb.append("            <description>Jerry is funny</description>\n");
        sb.append("            <media:content url=\"");
        sb.append("/home/djb/vtest/EP000169160028_2010_02_26_22_10.mpg\"");
        sb.append(" type=\"video/mpeg\"/>\n");
        sb.append("            <boxee:runtime>0:29:00</boxee:runtime>\n");
        sb.append("            <boxee:release-date>1993");
        sb.append("</boxee:release-date>\n");
        sb.append("            <media:credit role=\"actor\">");
        sb.append("Jerry Seinfeld</media:credit>\n");
        sb.append("            <media:credit role=\"actor\">");
        sb.append("Kramer</media:credit>\n");
        sb.append("            <media:category scheme=\"urn:boxee:genre\">");
        sb.append("Comedy</media:category>\n");
        sb.append("        </item>\n");
        sb.append("    </channel>\n");
        sb.append("</rss>\n");

        resp.getWriter().write(sb.toString());
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

                httpService.registerServlet("/boxeefeed", this, null, null);

            } catch (ServletException ex) {

                System.out.println("BoxeeFeed: " + ex.getMessage());

            } catch (NamespaceException ex) {

                System.out.println("NamespaceException: " + ex.getMessage());
            }
        }
    }

}

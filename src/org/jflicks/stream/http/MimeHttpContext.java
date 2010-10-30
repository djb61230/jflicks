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

import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;

/**
 * Simple override to set up mime types.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class MimeHttpContext implements HttpContext {

    /**
     * Simple empty constructor.
     */
    public MimeHttpContext() {
    }

    /**
     * We handle all the audio and image mime types we might run into.
     *
     * @param fileName A file name that is examined.
     * @return A String representing a mime type.
     */
    public String getMimeType(String fileName) {

        String result = null;

        if (fileName != null) {

            if (fileName.endsWith(".wav")) {

                result = "audio/wav";

            } else if (fileName.endsWith(".snd")) {

                result = "audio/basic";

            } else if (fileName.endsWith(".mp3")) {

                result = "audio/mpeg";

            } else if (fileName.endsWith(".m3u")) {

                result = "audio/x-mpegurl";

            } else if (fileName.endsWith(".mp4")) {

                result = "video/mp4";

            } else if (fileName.endsWith(".jpg")) {

                result = "image/jpeg";

            } else if (fileName.endsWith(".gif")) {

                result = "image/gif";

            } else if (fileName.endsWith(".png")) {

                result = "image/png";
            }
        }

        return (result);
    }

    /**
     * Turn the given file name into a file URL.
     *
     * @param name A given name of a file.
     * @return A URL "pointing" at the file.
     */
    public URL getResource(String name) {

        URL result = null;

        try {

            result = new URL("file:///" + name);

        } catch (MalformedURLException ex) {
            System.out.println("bonage: " + ex.getMessage());
        }

        return (result);
    }

    /**
     * We do not worry about security at this point and just return
     * True.
     *
     * @param req A given request.
     * @param rep A given response.
     * @return True
     */
    public boolean handleSecurity(HttpServletRequest req,
        HttpServletResponse rep) {

        return (true);
    }

}


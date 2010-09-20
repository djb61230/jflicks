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
package org.jflicks.web;

import org.jflicks.configure.Config;
import org.jflicks.nms.WebVideo;

/**
 * This interface defines the methods that allow for the access to web
 * media.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Web extends Config {

    /**
     * The Stream interface needs a title property.
     */
    String TITLE_PROPERTY = "Web-Title";

    /**
     * The title of this stream service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * The currently available web media.
     *
     * @return An array of WebVideo instances.
     */
    WebVideo[] getWebVideos();
}


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
package org.jflicks.stream;

/**
 * This interface defines the methods that allow for the creation of streaming
 * services.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Stream {

    /**
     * The Stream interface needs a title property.
     */
    String TITLE_PROPERTY = "Stream-Title";

    /**
     * The title of this stream service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * The type of this stream service.  Usually audio or video.
     *
     * @return The type as a String.
     */
    String getType();

    /**
     * A stream is presumed to be streaming from a particular host.
     *
     * @return The host as a String.
     */
    String getHost();

    /**
     * A stream is presumed to be streaming from a particular port.
     *
     * @return The port as an int.
     */
    int getPort();

    /**
     * Start streaming.
     */
    void startStream();

    /**
     * Stop streaming.
     */
    void stopStream();

    /**
     * Simple method to find out if the Stream is currently streaming.
     *
     * @return True if in stream mode.
     */
    boolean isStreaming();
}


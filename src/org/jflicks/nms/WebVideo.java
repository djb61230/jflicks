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
package org.jflicks.nms;

import org.jflicks.util.RandomGUID;

/**
 * This class contains all the properties to describe web based video.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class WebVideo extends Video {

    private String url;
    private String source;

    /**
     * Simple empty constructor.
     */
    public WebVideo() {

        setId(RandomGUID.createGUID());
    }

    /**
     * Constructor to "clone" a WebVideo instance.
     *
     * @param wv A given WebVideo.
     */
    public WebVideo(WebVideo wv) {

        super(wv);

        setURL(wv.getURL());
        setSource(wv.getSource());
    }

    /**
     * The actual video is located at some web address.
     *
     * @return A URL as a String.
     */
    public String getURL() {
        return (url);
    }

    /**
     * The actual video is located at some web address.
     *
     * @param s A URL as a String.
     */
    public void setURL(String s) {
        url = s;
    }

    /**
     * The name of the sourc e of the video - perhaps Hulu, CBS etc.
     *
     * @return The name of the source of the video.
     */
    public String getSource() {
        return (source);
    }

    /**
     * The name of the sourc e of the video - perhaps Hulu, CBS etc.
     *
     * @param s The name of the source of the video.
     */
    public void setSource(String s) {
        source = s;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (super.hashCode());
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        return (super.equals(o));
    }

}


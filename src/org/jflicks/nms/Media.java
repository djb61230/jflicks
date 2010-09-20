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

/**
 * This class contains all the properties to describe a unit of media.
 * This media could be TV, video, photo or music.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Media {

    /**
     * A unique ID is associated with this object.
     *
     * @return An ID value as a String.
     */
    String getId();

    /**
     * A unique ID is associated with this object.
     *
     * @param s An ID value as a String.
     */
    void setId(String s);

    /**
     * A Video has a file path where it is located.
     *
     * @return The path to the video.
     */
    String getPath();

    /**
     * A Video has a file path where it is located.
     *
     * @param s The path to the video.
     */
    void setPath(String s);

    /**
     * There is a Title property.
     *
     * @return The title.
     */
    String getTitle();

    /**
     * There is a Title property.
     *
     * @param s The title.
     */
    void setTitle(String s);

    /**
     * A description of the recording.
     *
     * @return The description as a String instance.
     */
    String getDescription();

    /**
     * A description of the recording.
     *
     * @param s The description as a String instance.
     */
    void setDescription(String s);

    /**
     * URL as a String where a banner image can be found if it exists.
     *
     * @return A String instance.
     */
    String getBannerURL();

    /**
     * URL as a String where a banner image can be found if it exists.
     *
     * @param s A String instance.
     */
    void setBannerURL(String s);

    /**
     * URL as a String where a poster image can be found if it exists.
     *
     * @return A String instance.
     */
    String getPosterURL();

    /**
     * URL as a String where a poster image can be found if it exists.
     *
     * @param s A String instance.
     */
    void setPosterURL(String s);

    /**
     * URL as a String where a fanart image can be found if it exists.
     *
     * @return A String instance.
     */
    String getFanartURL();

    /**
     * URL as a String where a fanart image can be found if it exists.
     *
     * @param s A String instance.
     */
    void setFanartURL(String s);

    /**
     * The length of the media in seconds.
     *
     * @return A long value representing the number of seconds of the media.
     */
    long getDuration();

    /**
     * The length of the media in seconds.
     *
     * @param l A long value representing the number of seconds of the media.
     */
    void setDuration(long l);
}


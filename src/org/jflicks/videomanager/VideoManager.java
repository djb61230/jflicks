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
package org.jflicks.videomanager;

import org.jflicks.configure.Config;
import org.jflicks.nms.NMS;
import org.jflicks.nms.Video;

/**
 * The VideoManager interface defines a service to manage videos.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface VideoManager extends Config {

    /**
     * The VideoManager interface needs a title property.
     */
    String TITLE_PROPERTY = "VideoManager-Title";

    /**
     * The title of this video manager service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * The video manager needs access to the NMS.
     *
     * @return A NMS instance.
     */
    NMS getNMS();

    /**
     * The video manager needs access to the NMS.
     *
     * @param n A NMS instance.
     */
    void setNMS(NMS n);

    /**
     * Acquire all the Video instances currently defined.
     *
     * @return An array of Video instances.
     */
    Video[] getVideos();

    /**
     * Get a particular Video by Id.
     *
     * @param id A given Id.
     * @return A Video if found.
     */
    Video getVideoById(String id);

    /**
     * The NMS can save a Video whose properties have been edited by the
     * user.
     *
     * @param v A given Video to save.
     */
    void save(Video v);

    /**
     * We allow the user to delete a video via this method.
     *
     * @param v A given Video to remove.
     */
    void removeVideo(Video v);

    /**
     * Perform a video scan.  Depending on the implementation an import may
     * be done from some third party video management system.
     */
    void videoScan();

    /**
     * Not every video has available artwork from the Internet and of
     * course HomeVideo certainly won't.  Here we can have artwork generated
     * by grabbing frames from the actual video.  The user can give a specific
     * offset in seconds in case they have an idea where they want to grab.
     *
     * @param v A given Video instance.
     * @param seconds The number of seconds into the Video.
     */
    void generateArtwork(Video v, int seconds);
}

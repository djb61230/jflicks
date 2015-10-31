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
package org.jflicks.cleaner;

import org.jflicks.configure.Config;
import org.jflicks.nms.NMS;

/**
 * The Cleaner interface defines a service to manage photos.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Cleaner extends Config {

    /**
     * The Cleaner interface needs a title property.
     */
    String TITLE_PROPERTY = "Cleaner-Title";

    /**
     * The title of this photo manager service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * The photo manager needs access to the NMS.
     *
     * @return A NMS instance.
     */
    NMS getNMS();

    /**
     * The photo manager needs access to the NMS.
     *
     * @param n A NMS instance.
     */
    void setNMS(NMS n);

    /**
     * This determines how often we check recordings.
     *
     * @return An int value in seconds.
     */
    int getTimeBetweenCleanings();

    /**
     * This determines how old a recording needs to be before cleaning.
     *
     * @return An int value in seconds.
     */
    int getRecordingMinimumAge();
}

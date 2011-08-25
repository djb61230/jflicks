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
package org.jflicks.autoart;

import org.jflicks.configure.Config;
import org.jflicks.nms.NMS;

/**
 * This interface defines the methods that allow for automatically getting
 * art for Recordings and Video files.  Until this interface all art was
 * acquired via the user running the metadata application.  We would like
 * to automate this as much as we can.  The user can then customize
 * (or correct) art they do not like.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface AutoArt extends Config {

    /**
     * The Recorder interface needs a title property.
     */
    String TITLE_PROPERTY = "AutoArt-Title";

    /**
     * The title of this AutoArt service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * An AutoArt service needs access to the NMS.
     *
     * @return A NMS instance.
     */
    NMS getNMS();

    /**
     * An AutoArt service needs access to the NMS.
     *
     * @param n A NMS instance.
     */
    void setNMS(NMS n);

    /**
     * Actually check the current state and update what we can
     */
    void performUpdate();
}


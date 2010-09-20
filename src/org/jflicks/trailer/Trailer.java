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
package org.jflicks.trailer;

import org.jflicks.configure.Config;
import org.jflicks.nms.NMS;

/**
 * This interface defines the methods that allow for a service for the
 * acquisition of movie trailers.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Trailer extends Config {

    /**
     * The Trailer interface needs a title property.
     */
    String TITLE_PROPERTY = "Trailer-Title";

    /**
     * The title of this record service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * The trailer needs access to the NMS since it has some convenience
     * methods to get path information.
     *
     * @return A NMS instance.
     */
    NMS getNMS();

    /**
     * The live needs access to the NMS since it has some convenience
     * methods to get path information.  On discovery of a Trailer, a
     * NMS should set this property.
     *
     * @param n A NMS instance.
     */
    void setNMS(NMS n);
}


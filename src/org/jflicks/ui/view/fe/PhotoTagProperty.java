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
package org.jflicks.ui.view.fe;

import org.jflicks.photomanager.Photo;
import org.jflicks.photomanager.Tag;

/**
 * A screen that can handle photos needs to implement this interface so
 * the front end can notify it of the known Tags.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface PhotoTagProperty {

    /**
     * A Screen might support the handling of Photo and Tag instances.
     *
     * @return The Tag instances.
     */
    Tag[] getTags();

    /**
     * A Screen might support the handling of Photo and Tag instances.
     *
     * @param array The Tag instances.
     */
    void setTags(Tag[] array);

    /**
     * A Screen might support the handling of Photo and Tag instances.
     *
     * @return The Photo instances.
     */
    Photo[] getPhotos();

    /**
     * A Screen might support the handling of Photo and Tag instances.
     *
     * @param array The Photo instances.
     */
    void setPhotos(Photo[] array);
}


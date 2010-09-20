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

import org.jflicks.tv.Recording;

/**
 * A screen that can play recordings needs to implement this interface so
 * the front end can notify it of the known recordings.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface RecordingProperty {

    /**
     * A Screen might support the playing of Recording instances.
     *
     * @return The Recording instances.
     */
    Recording[] getRecordings();

    /**
     * A Screen might support the playing of Recording instances.
     *
     * @param array The Recording instances.
     */
    void setRecordings(Recording[] array);

    /**
     * Some property of the given recording has been updated.  This might
     * result in some display change or other action needed to be done by
     * implementers.
     *
     * @param r A given Recording to update.
     */
    void updateRecording(Recording r);
}


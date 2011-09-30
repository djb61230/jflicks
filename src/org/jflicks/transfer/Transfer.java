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
package org.jflicks.transfer;

import org.jflicks.tv.Recording;

/**
 * This interface defines the methods that allow for the creation of a
 * service that specializes in transfering a Recording file from a
 * remote machine to a local machine.  With the added feature that the
 * Recording is a file that is currently being added or is growing.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Transfer {

    /**
     * The Transfer interface needs a title property.
     */
    String TITLE_PROPERTY = "Transfer-Title";

    /**
     * The title of this transfer service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * Initiate a transfer based upon the given Recording.  When this method
     * returns the user can be assured that the file has begun transferring
     * and can be played.
     *
     * @return The local path as a String.
     */
    String transfer(Recording r);

    /**
     * Stop all transfers and clean up all local files.
     */
    void close();
}


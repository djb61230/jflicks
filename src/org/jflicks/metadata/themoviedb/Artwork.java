/*
    This file is part of ATM.

    ATM is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ATM is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ATM.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.jflicks.metadata.themoviedb;

import java.io.Serializable;

/**
 * This class captures the Artwork information available from themoviedb.org.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Artwork {

    private int id;
    private Image[] backdrops;
    private Image[] posters;

    /**
     * Empty constructor.
     */
    public Artwork() {
    }

    public int getId() {
        return (id);
    }

    public void setId(int i) {
        id = i;
    }

    public Image[] getBackdrops() {
        return (backdrops);
    }

    public void setBackdrops(Image[] array) {
        backdrops = array;
    }

    public Image[] getPosters() {
        return (posters);
    }

    public void setPosters(Image[] array) {
        posters = array;
    }

    /**
     * Override this method to return the id property.
     *
     * @return The id as a String.
     */
    public String toString() {
        return ("" + getId());
    }

}


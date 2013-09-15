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
 * This is an object that encapsulates the information about a genre
 * available from themoviedb.org.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Genre implements Serializable {

    private int id;
    private String name;

    /**
     * Empty constructor.
     */
    public Genre() {
    }

    public int getId() {
        return (id);
    }

    public void setId(int i) {
        id = i;
    }

    public String getName() {
        return (name);
    }

    public void setName(String s) {
        name = s;
    }

    public String toString() {
        return (getName());
    }

}


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
package org.jflicks.tv.programdata.sd.json;

import java.io.Serializable;

/**
 * A class to capture the JSON defining a program description.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Descriptions implements Serializable {

    private Description100[] description100;
    private Description1000[] description1000;

    /**
     * Simple empty constructor.
     */
    public Descriptions() {
    }

    public Description100[] getDescription100() {
        return (description100);
    }

    public void setDescription100(Description100[] array) {
        description100 = array;
    }

    public Description1000[] getDescription1000() {
        return (description1000);
    }

    public void setDescription1000(Description1000[] array) {
        description1000 = array;
    }

}


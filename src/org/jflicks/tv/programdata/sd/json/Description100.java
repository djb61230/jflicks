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
public class Description100 implements Serializable {

    private String descriptionLanguage;
    private String description;

    /**
     * Simple empty constructor.
     */
    public Description100() {
    }

    public String getDescriptionLanguage() {
        return (descriptionLanguage);
    }

    public void setDescriptionLanguage(String s) {
        descriptionLanguage = s;
    }

    public String getDescription() {
        return (description);
    }

    public void setDescription(String s) {
        description = s;
    }

}


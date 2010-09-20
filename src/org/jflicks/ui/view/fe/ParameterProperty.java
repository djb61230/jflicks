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

/**
 * A Screen might need to display certain information based upon a
 * particular property called a "parameter".  This parameter is a String
 * object.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface ParameterProperty {

    /**
     * A Screen might support the idea of different "states" that are defined
     * by a set of parameter String instances.
     *
     * @return The parameter String instances.
     */
    String[] getParameters();

    /**
     * The current parameter value.
     *
     * @return The "selected" parameter String.
     */
    String getSelectedParameter();

    /**
     * The current parameter value.
     *
     * @param s The "selected" parameter String.
     */
    void setSelectedParameter(String s);
}


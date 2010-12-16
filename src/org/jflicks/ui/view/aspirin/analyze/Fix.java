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
package org.jflicks.ui.view.aspirin.analyze;

import java.io.Serializable;

/**
 * The Fix interface defines a method that can fix a problem.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Fix extends Serializable {

    /**
     * Perform an analysis of the current system and return a Finding.
     *
     * @return A Finding instance.
     */
    boolean fix();
}


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
 * This class contains all the properties representing a finding.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Finding implements Serializable {

    private String title;
    private String description;
    private boolean passed;
    private Fix fix;

    /**
     * Simple empty constructor.
     */
    public Finding() {
    }

    /**
     * A title for the finding.
     *
     * @return The description as a String instance.
     */
    public String getTitle() {
        return (title);
    }

    /**
     * A title for the finding.
     *
     * @param s The description as a String instance.
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * A description of the finding.
     *
     * @return The description as a String instance.
     */
    public String getDescription() {
        return (description);
    }

    /**
     * A description of the finding.
     *
     * @param s The description as a String instance.
     */
    public void setDescription(String s) {
        description = s;
    }

    /**
     * The Finding needs to flag whether it passes.
     *
     * @return True if all is well.
     */
    public boolean isPassed() {
        return (passed);
    }

    /**
     * The Finding needs to flag whether it passes.
     *
     * @param b True if all is well.
     */
    public void setPassed(boolean b) {
        passed = b;
    }

    /**
     * The Finding may have a Fix in case things did not pass.
     *
     * @return A Fix instance.
     */
    public Fix getFix() {
        return (fix);
    }

    /**
     * The Finding may have a Fix in case things did not pass.
     *
     * @param f A Fix instance.
     */
    public void setFix(Fix f) {
        fix = f;
    }

    /**
     * Override so we look good in UI components.
     *
     * @return A String that is the title property.
     */
    public String toString() {
        return (getTitle());
    }

}


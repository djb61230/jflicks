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

/**
 * The Analyze interface defines the methods that a service needs to
 * implement to be able to analyze some aspect of the computer system
 * to determine if it meets some sort of criteria.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Analyze {

    /**
     * The Analyze interface needs a title property.
     */
    String TITLE_PROPERTY = "Analyze-Title";

    /**
     * The title of this Analyze service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * A short textual description of the Analyze implementation.
     *
     * @return A short (one sentence perhaps) description.
     */
    String getShortDescription();

    /**
     * A long textual description of the Analyze implementation.
     *
     * @return A long (one paragraph perhaps) description.
     */
    String getLongDescription();

    /**
     * An Analyze instance is valid against a set of deployed bundles.
     *
     * @return An array of bundle names.
     */
    String[] getBundles();

    /**
     * Perform an analysis of the current system and return a Finding.
     *
     * @return A Finding instance.
     */
    Finding analyze();

    /**
     * Convenience method to determine if an Analyze instance is actually
     * needed given the set of bundles that are being deployed.  No sense
     * in running an Analyce if it is never needed.
     *
     * @param bundleNames The names of deployed bundles.
     * @return True if at least one of the bundles is dependent upon this
     * instance.
     */
    boolean isNeeded(String[] bundleNames);

    /**
     * Sometimes an Analyze instance needs a directory path to do it's
     * work.  Perhaps it needs to know the existance of some file.  This
     * property tells the Analyze instance this information.
     *
     * @return A String representing a directory.
     */
    String getInstallationPath();

    /**
     * Sometimes an Analyze instance needs a directory path to do it's
     * work.  Perhaps it needs to know the existance of some file.  This
     * property tells the Analyze instance this information.
     *
     * @param s A String representing a directory.
     */
    void setInstallationPath(String s);
}


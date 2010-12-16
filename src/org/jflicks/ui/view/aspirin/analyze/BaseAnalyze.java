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
 * This class is a base implementation of the Analyze interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseAnalyze implements Analyze {

    private String title;
    private String shortDescription;
    private String longDescription;
    private String[] bundles;

    /**
     * Simple empty constructor.
     */
    public BaseAnalyze() {
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return (title);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given title value.
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * {@inheritDoc}
     */
    public String getShortDescription() {
        return (shortDescription);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given short description value.
     */
    public void setShortDescription(String s) {
        shortDescription = s;
    }

    /**
     * {@inheritDoc}
     */
    public String getLongDescription() {
        return (longDescription);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given long description value.
     */
    public void setLongDescription(String s) {
        longDescription = s;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getBundles() {
        return (bundles);
    }

    /**
     * Convenience method to set this property.
     *
     * @param array The given array of bundles.
     */
    public void setBundles(String[] array) {
        bundles = array;
    }

}


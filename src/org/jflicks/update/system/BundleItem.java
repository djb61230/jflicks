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
package org.jflicks.update.system;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class BundleItem {

    private String name;
    private boolean feature;
    private String[] needNames;

    /**
     * Simple empty constructor.
     */
    public BundleItem() {
    }

    /**
     * A BundleItem has an associated name.
     *
     * @return The BundleItem name.
     */
    public String getName() {
        return (name);
    }

    /**
     * A BundleItem has an associated name.
     *
     * @param s The BundleItem name.
     */
    public void setName(String s) {
        name = s;
    }

    /**
     * A BundleItem has an associated feature property.
     *
     * @return The BundleItem feature property.
     */
    public boolean isFeature() {
        return (feature);
    }

    /**
     * A BundleItem has an associated feature property.
     *
     * @param b The BundleItem feature property.
     */
    public void setFeature(boolean b) {
        feature = b;
    }

    /**
     * A BundleItem has an associated array of BundleItem names.
     *
     * @return The String array of needed BundleItem names.
     */
    public String[] getNeedNames() {
        return (needNames);
    }

    /**
     * A BundleItem has an associated array of BundleItem names.
     *
     * @param array The String array of needed BundleItem names.
     */
    public void setNeedNames(String[] array) {
        needNames = array;
    }

    public boolean needs(BundleItem bi) {

        boolean result = false;

        String[] array = getNeedNames();
        if ((bi != null) && (array != null) && (array.length > 0)) {

            String biname = bi.getName();
            if (biname != null) {

                for (int i = 0; i < array.length; i++) {

                    if (biname.equals(array[i])) {

                        result = true;
                        break;
                    }
                }
            }
        }

        return (result);
    }

}


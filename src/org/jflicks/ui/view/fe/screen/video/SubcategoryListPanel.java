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
package org.jflicks.ui.view.fe.screen.video;

import java.util.ArrayList;

import org.jflicks.ui.view.fe.BaseListPanel;

/**
 * This is a display of video subcategories in a list.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SubcategoryListPanel extends BaseListPanel {

    private ArrayList<String> subcategoryList;

    /**
     * Simple empty constructor.
     */
    public SubcategoryListPanel() {

        setSubcategoryList(new ArrayList<String>());
        setPropertyName("SelectedSubcategory");
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getObjects() {

        Object[] result = null;

        ArrayList<String> l = getSubcategoryList();
        if (l != null) {

            result = l.toArray(new Object[l.size()]);
        }

        return (result);
    }

    private ArrayList<String> getSubcategoryList() {
        return (subcategoryList);
    }

    private void setSubcategoryList(ArrayList<String> l) {
        subcategoryList = l;
    }

    /**
     * We list subcategories in our panel.
     *
     * @return An array of String instances.
     */
    public String[] getSubcategories() {

        String[] result = null;

        return (result);
    }

    /**
     * We list subcategories in our panel.
     *
     * @param array An array of String instances.
     */
    public void setSubcategories(String[] array) {

        ArrayList<String> l = getSubcategoryList();
        if (l != null) {

            l.clear();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    l.add(array[i]);
                }

                setSelectedObject(null);
                setStartIndex(0);
            }
        }
    }

    /**
     * Convenience method to return the selected object as a String instance.
     *
     * @return A String instance.
     */
    public String getSelectedSubcategory() {
        return ((String) getSelectedObject());
    }

}


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

import java.util.ArrayList;

/**
 * Display Theme instances in a list.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ThemeListPanel extends BaseListPanel {

    private ArrayList<Theme> themeList;

    /**
     * Simple empty constructor.
     */
    public ThemeListPanel() {

        setThemeList(new ArrayList<Theme>());
        setPropertyName("SelectedTheme");
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getObjects() {

        Object[] result = null;

        ArrayList<Theme> l = getThemeList();
        if (l != null) {

            result = l.toArray(new Object[l.size()]);
        }

        return (result);
    }

    /**
     * Convenience method to return the selected Theme so users do not have
     * to cast from an Object.
     *
     * @return A Theme instance.
     */
    public Theme getSelectedTheme() {
        return ((Theme) getSelectedObject());
    }

    private ArrayList<Theme> getThemeList() {
        return (themeList);
    }

    private void setThemeList(ArrayList<Theme> l) {
        themeList = l;
    }

    /**
     * We list theme in our panel.
     *
     * @return An array of Theme instances.
     */
    public Theme[] getThemes() {

        Theme[] result = null;

        return (result);
    }

    /**
     * We list theme in our panel.
     *
     * @param array An array of Theme instances.
     */
    public void setThemes(Theme[] array) {

        ArrayList<Theme> l = getThemeList();
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

}


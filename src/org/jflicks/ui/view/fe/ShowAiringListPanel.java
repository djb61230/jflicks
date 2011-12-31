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

import org.jflicks.tv.ShowAiring;

/**
 * This is a display of ShowAiring instances in a list.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ShowAiringListPanel extends BaseListPanel {

    private ArrayList<ShowAiring> showAiringList;

    /**
     * Simple empty constructor.
     */
    public ShowAiringListPanel() {

        setShowAiringList(new ArrayList<ShowAiring>());
        setPropertyName("SelectedShowAiring");
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getObjects() {

        Object[] result = null;

        ArrayList<ShowAiring> l = getShowAiringList();
        if (l != null) {

            result = l.toArray(new Object[l.size()]);
        }

        return (result);
    }

    private ArrayList<ShowAiring> getShowAiringList() {
        return (showAiringList);
    }

    private void setShowAiringList(ArrayList<ShowAiring> l) {
        showAiringList = l;
    }

    /**
     * We list showAiring in our panel.
     *
     * @return An array of ShowAiring instances.
     */
    public ShowAiring[] getShowAirings() {

        ShowAiring[] result = null;

        ArrayList<ShowAiring> l = getShowAiringList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new ShowAiring[l.size()]);
        }

        return (result);
    }

    /**
     * We list showAiring in our panel.
     *
     * @param array An array of ShowAiring instances.
     */
    public void setShowAirings(ShowAiring[] array) {

        ArrayList<ShowAiring> l = getShowAiringList();
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


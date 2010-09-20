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

import org.jflicks.tv.Upcoming;

/**
 * Display Upcoming instances in a list.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class UpcomingListPanel extends BaseListPanel {

    private ArrayList<Upcoming> upcomingList;

    /**
     * Simple empty constructor.
     */
    public UpcomingListPanel() {

        setUpcomingList(new ArrayList<Upcoming>());
        setPropertyName("SelectedUpcoming");
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getObjects() {

        Object[] result = null;

        ArrayList<Upcoming> l = getUpcomingList();
        if (l != null) {

            result = l.toArray(new Object[l.size()]);
        }

        return (result);
    }

    /**
     * Convenience method to return the selected Upcoming so users do not have
     * to cast from an Object.
     *
     * @return An Upcoming instance.
     */
    public Upcoming getSelectedUpcoming() {
        return ((Upcoming) getSelectedObject());
    }

    private ArrayList<Upcoming> getUpcomingList() {
        return (upcomingList);
    }

    private void setUpcomingList(ArrayList<Upcoming> l) {
        upcomingList = l;
    }

    /**
     * We list upcoming in our panel.
     *
     * @return An array of Upcoming instances.
     */
    public Upcoming[] getUpcomings() {

        Upcoming[] result = null;

        return (result);
    }

    /**
     * We list upcoming in our panel.
     *
     * @param array An array of Upcoming instances.
     */
    public void setUpcomings(Upcoming[] array) {

        ArrayList<Upcoming> l = getUpcomingList();
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


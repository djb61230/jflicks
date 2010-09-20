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
package org.jflicks.metadata;

import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * A Base search panel that implementations need to extend.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class SearchPanel extends JPanel implements Searchable,
    SearchListener {

    private ArrayList<SearchListener> searchList =
        new ArrayList<SearchListener>();

    /**
     * A SearchPanel needs to accept search terms.
     *
     * @return Search terms as a String.
     */
    public abstract String getSearchTerms();

    /**
     * A SearchPanel needs to accept search terms.
     *
     * @param s Search terms as a String.
     */
    public abstract void setSearchTerms(String s);

    private Hit hit;

    /**
     * Simple empty constructor.
     */
    public SearchPanel() {
    }

    /**
     * The "state" of information selected by the user is maintained in a Hit
     * instance.
     *
     * @return The current user selections encapsulated in a Hit.
     */
    public Hit getHit() {
        return (hit);
    }

    /**
     * The "state" of information selected by the user is maintained in a Hit
     * instance.
     *
     * @param h The current user selections encapsulated in a Hit.
     */
    public void setHit(Hit h) {
        hit = h;
    }

    /**
     * {@inheritDoc}
     */
    public void addSearchListener(SearchListener l) {
        searchList.add(l);
    }

    /**
     * {@inheritDoc}
     */
    public void removeSearchListener(SearchListener l) {
        searchList.remove(l);
    }

    /**
     * Convenience method to fire an event with a certain type and Hit.
     *
     * @param type A given type.
     * @param hit A given Hit.
     */
    public void fireSearchEvent(int type, Hit hit) {
        processSearchEvent(new SearchEvent(this, type, hit));
    }

    protected synchronized void processSearchEvent(SearchEvent event) {

        synchronized (searchList) {

            for (int i = 0; i < searchList.size(); i++) {

                SearchListener l = searchList.get(i);
                l.searchUpdate(event);
            }
        }
    }

}


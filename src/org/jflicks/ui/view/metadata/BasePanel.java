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
package org.jflicks.ui.view.metadata;

import java.util.ArrayList;
import javax.swing.JPanel;

import org.jflicks.metadata.Searchable;
import org.jflicks.metadata.SearchEvent;
import org.jflicks.metadata.SearchListener;

/**
 * A Base search panel that implementations need to extend.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BasePanel extends JPanel implements Searchable {

    private ArrayList<SearchListener> searchList =
        new ArrayList<SearchListener>();

    /**
     * Simple empty constructor.
     */
    public BasePanel() {
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
     * Convenience method to fire an event with a certain type and terms.
     *
     * @param searchType A given searchType.
     * @param terms A given Hit.
     */
    public void fireSearchEvent(int searchType, String terms) {
        processSearchEvent(new SearchEvent(this, searchType, terms));
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


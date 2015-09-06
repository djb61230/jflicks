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
package org.jflicks.ui.view.j4cc.scheduler;

import org.jflicks.tv.ShowAiring;
import org.jflicks.ui.view.j4cc.AbstractPanel;

/**
 * Base panel that deals with ShowAiring selection.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseSchedulePanel extends AbstractPanel {

    private ShowAiring showAiring;

    /**
     * Default constructor.
     */
    public BaseSchedulePanel() {

        setAutoTimer(false);
    }

    /**
     * A ShowAiring instance is needed as an input to extensions.
     *
     * @return A ShowAiring instance.
     */
    public ShowAiring getShowAiring() {
        return (showAiring);
    }

    /**
     * A ShowAiring instance is needed as an input to extensions.  On setting
     * we fire a property change event.
     *
     * @param sa A ShowAiring instance.
     */
    public void setShowAiring(ShowAiring sa) {

        ShowAiring old = showAiring;
        showAiring = sa;
        firePropertyChange("ShowAiring", old, showAiring);
    }

}

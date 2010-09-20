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
package org.jflicks.ui.view.scheduler;

import javax.swing.JPanel;

import org.jflicks.nms.NMS;
import org.jflicks.tv.ShowAiring;

/**
 * Base panel that deals with ShowAiring selection.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseSchedulePanel extends JPanel {

    private NMS nms;
    private ShowAiring showAiring;

    /**
     * Extensions need to be notified when they are using a new NMS
     * instance.
     */
    public abstract void nmsAction();

    /**
     * Default constructor.
     */
    public BaseSchedulePanel() {
    }

    /**
     * A NMS instance is needed to access data.
     *
     * @return A NMS instance.
     */
    public NMS getNMS() {
        return (nms);
    }

    /**
     * A NMS instance is needed to access data.  We notify extensions that
     * this property has been updated.
     *
     * @param n A NMS instance.
     */
    public void setNMS(NMS n) {

        nms = n;
        nmsAction();
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

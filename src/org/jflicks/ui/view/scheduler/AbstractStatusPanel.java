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

/**
 * A base class that defines a panel for this app.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class AbstractStatusPanel extends JPanel {

    private NMS nms;

    /**
     * Default constructor.
     */
    public AbstractStatusPanel() {
    }

    /**
     * A refernce to NMS is needed to do the work of this UI component.
     *
     * @return An NMS instance.
     */
    public NMS getNMS() {
        return (nms);
    }

    /**
     * A refernce to NMS is needed to do the work of this UI component.
     *
     * @param n An NMS instance.
     */
    public void setNMS(NMS n) {
        nms = n;
    }

}

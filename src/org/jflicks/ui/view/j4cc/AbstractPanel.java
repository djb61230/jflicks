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
package org.jflicks.ui.view.j4cc;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.Timer;
import java.util.TimerTask;

import org.jflicks.nms.NMS;
import org.jflicks.ui.view.JFlicksView;

import org.jdesktop.swingx.JXPanel;

/**
 * A base class that defines a panel for this app.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class AbstractPanel extends JXPanel {

    private NMS nms;
    private JFlicksView jflicksView;
    private boolean autoTimer;
    private boolean refreshOnSelection;

    public abstract void populate();

    /**
     * Default constructor.
     */
    public AbstractPanel() {

        setAutoTimer(true);
        setRefreshOnSelection(true);
        Timer timer = new Timer("autoRefresh");
        RefreshTimerTask rtt = new RefreshTimerTask(this);
        timer.scheduleAtFixedRate(rtt, 7000, 30000);
    }

    public NMS getNMS() {
        return (nms);
    }

    public void setNMS(NMS n) {

        nms = n;
        populate();
    }

    public JFlicksView getJFlicksView() {
        return (jflicksView);
    }

    public void setJFlicksView(JFlicksView v) {
        jflicksView = v;
    }

    public boolean isAutoTimer() {
        return (autoTimer);
    }

    public void setAutoTimer(boolean b) {
        autoTimer = b;
    }

    public boolean isRefreshOnSelection() {
        return (refreshOnSelection);
    }

    public void setRefreshOnSelection(boolean b) {
        refreshOnSelection = b;
    }

    public Rectangle getBounds(String s) {

        Rectangle result = null;

        JFlicksView v = getJFlicksView();
        if ((v != null) && (s != null)) {

            result = v.getBounds(s);
        }

        return (result);
    }

    class RefreshTimerTask extends TimerTask {

        private Component component;

        public RefreshTimerTask(Component c) {
            component = c;
        }

        public void run() {

            if ((component.isVisible()) && (isAutoTimer())) {

                populate();
            }
        }
    }

}

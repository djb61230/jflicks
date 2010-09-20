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

import java.awt.Dimension;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports Labels in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class MessagePanel extends BaseCustomizePanel {

    private String message;
    private JXLabel label;

    /**
     * Simple empty constructor.
     */
    public MessagePanel() {

        JXLabel l = new JXLabel();
        l.setHorizontalTextPosition(SwingConstants.CENTER);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setOpaque(false);
        l.setFont(getSmallFont());
        l.setForeground(getSelectedColor());
        setLabel(l);

        MattePainter mpainter = new MattePainter(getPanelColor());
        setBackgroundPainter(mpainter);
        setAlpha((float) getPanelAlpha());
    }

    /**
     * We display message text.
     *
     * @return A String instance.
     */
    public String getMessage() {
        return (message);
    }

    /**
     * We display message text.
     *
     * @param s A String instance.
     */
    public void setMessage(String s) {

        message = s;
        JXLabel l = getLabel();
        if (l != null) {

            if (message != null) {

                l.setText(message);

            } else {

                l.setText("");
            }
        }
    }

    private JXLabel getLabel() {
        return (label);
    }

    private void setLabel(JXLabel l) {
        label = l;
    }

    /**
     * {@inheritDoc}
     */
    public void performControl() {
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {

        JLayeredPane pane = getLayeredPane();
        JXLabel l = getLabel();
        if ((d != null) && (pane != null) && (l != null)) {

            double width = d.getWidth();
            double height = d.getHeight();

            l.setBounds(0, 0, (int) width, (int) height);

            pane.add(l, Integer.valueOf(100));
        }
    }

}


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
import javax.swing.JEditorPane;
import javax.swing.JLayeredPane;

import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports Labels in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class HtmlDetailPanel extends BaseCustomizePanel {

    private String markup;
    private JEditorPane editorPane;

    /**
     * Simple empty constructor.
     */
    public HtmlDetailPanel() {

        JEditorPane ep = new JEditorPane();
        ep.setOpaque(false);
        ep.setEditable(false);
        ep.setContentType("text/html");
        ep.putClientProperty(ep.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        ep.setFont(getSmallFont());
        ep.setForeground(getSelectedColor());
        setEditorPane(ep);

        MattePainter mpainter = new MattePainter(getPanelColor());
        setBackgroundPainter(mpainter);
        setAlpha((float) getPanelAlpha());
    }

    /**
     * We display html information about.
     *
     * @return A String instance.
     */
    public String getMarkup() {
        return (markup);
    }

    /**
     * We display html information about.
     *
     * @param s A String instance.
     */
    public void setMarkup(String s) {

        markup = s;
        JEditorPane ep = getEditorPane();
        if (ep != null) {

            if (markup != null) {

                ep.setText(markup);

            } else {

                ep.setText("");
            }
        }
    }

    private JEditorPane getEditorPane() {
        return (editorPane);
    }

    private void setEditorPane(JEditorPane ep) {
        editorPane = ep;
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
        JEditorPane ep = getEditorPane();
        if ((d != null) && (pane != null) && (ep != null)) {

            double width = d.getWidth();
            double height = d.getHeight();

            ep.setBounds(0, 0, (int) width, (int) height);

            pane.add(ep, Integer.valueOf(100));
        }
    }

}


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
package org.jflicks.util;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

/**
 * This class adds a button to the tab of a JTabbedPane so the user can
 * close the tab.  Most tab UI components these days have this and we need
 * this class to add the capability to the Java Swing JTabbedPane component.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class TabClose extends JPanel {

    private JTabbedPane pane;
    private JLabel label;

    /**
     * Contructor accepting two arguments.
     *
     * @param p The JTabbedPane instance this close is associated with.
     * @param text The text to be displayed on the tab.
     */
    public TabClose(JTabbedPane p, String text) {

        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pane = p;
        setOpaque(false);

        JLabel l = new JLabel(text);
        setLabel(l);

        add(l);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        JButton button = new JButton(new ExitAction());
        button.setBorder(new EmptyBorder(2, 2, 2, 2));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setRolloverEnabled(true);
        button.addMouseListener(new RolloverMouseAdapter());
        add(button);
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }

    private JLabel getLabel() {
        return (label);
    }

    private void setLabel(JLabel l) {
        label = l;
    }

    /**
     * The tab currently has a defined text value.
     *
     * @return A String.
     */
    public String getText() {

        String result = null;

        JLabel l = getLabel();
        if (l != null) {
            result = l.getText();
        }

        return (result);
    }

    /**
     * The tab currently has a defined text value.
     *
     * @param s A String.
     */
    public void setText(String s) {

        JLabel l = getLabel();
        if (l != null) {
            l.setText(s);
        }
    }

    class ExitAction extends AbstractAction {

        public ExitAction() {

            putValue(NAME, "X");
            putValue(SHORT_DESCRIPTION, "Close this tab");
        }

        public void actionPerformed(ActionEvent e) {

            int i = pane.indexOfTabComponent(TabClose.this);
            if (i != -1) {
                pane.remove(i);
            }
        }
    }

    static class RolloverMouseAdapter extends MouseAdapter {

        public RolloverMouseAdapter() {
        }

        public void mouseEntered(MouseEvent e) {

            Object source = e.getSource();
            if (source instanceof AbstractButton) {

                AbstractButton button = (AbstractButton) source;
                button.setContentAreaFilled(true);
            }
        }

        public void mouseExited(MouseEvent e) {

            Object source = e.getSource();
            if (source instanceof AbstractButton) {

                AbstractButton button = (AbstractButton) source;
                button.setContentAreaFilled(false);
            }
        }
    }

}



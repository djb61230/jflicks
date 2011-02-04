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

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.jflicks.util.ColumnPanel;
import org.jflicks.util.RowPanel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.ImagePainter;

/**
 * This panel will display an array of JButtons based upon the given
 * JButton objects passed in the constructor.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ButtonPanel extends JXPanel implements ActionListener {

    private static final double FONT_SIZE = 24.0;

    private ArrayList<ActionListener> actionList =
        new ArrayList<ActionListener>();
    private int orientation;
    private JButton escapeButton;

    /**
     * Constructor that takes an array of JButton objects.
     *
     * @param array An array of defined JButton objects.
     */
    public ButtonPanel(JButton[] array) {

        this(array, null, FONT_SIZE, SwingConstants.VERTICAL);
    }

    /**
     * Constructor that takes an array of JButton objects and an
     * orientation.
     *
     * @param array An array of defined JButton objects.
     * @param orientation Either SwingConstants.VERTICAL or
     * SwingConstants.HORIZONTAL.
     */
    public ButtonPanel(JButton[] array, int orientation) {

        this(array, null, FONT_SIZE, orientation);
    }

    /**
     * Constructor that takes an array of JButton objects.
     *
     * @param array An array of defined JButton objects.
     * @param bi The background image.
     */
    public ButtonPanel(JButton[] array, BufferedImage bi) {

        this(array, bi, FONT_SIZE, SwingConstants.VERTICAL);
    }

    /**
     * Constructor that takes an array of JButton objects.
     *
     * @param array An array of defined JButton objects.
     * @param bi The background image.
     * @param fontSize The font size.
     * @param orientation A row or column.
     */
    public ButtonPanel(JButton[] array, BufferedImage bi, double fontSize,
        int orientation) {

        UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);

        if (bi != null) {

            ImagePainter painter = new ImagePainter(bi);
            painter.setScaleToFit(true);
            setBackgroundPainter(painter);
        }

        setLayout(new BorderLayout());
        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                JButton b = array[i];
                b.setBorderPainted(false);
                b.setContentAreaFilled(false);
                b.setRolloverEnabled(true);
                b.addFocusListener(new HighlightFocusAdapter(null, null));
                b.addActionListener(this);
                b.setFont(b.getFont().deriveFont((float) fontSize));
                focusTraversal(b);

                if ((i + 1) == array.length) {

                    setEscapeButton(b);
                }
            }

            setLayout(new BorderLayout());

            setOrientation(orientation);
            if (orientation == SwingConstants.HORIZONTAL) {

                RowPanel rp = new RowPanel(array);
                rp.setOpaque(false);
                add(rp, BorderLayout.CENTER);

            } else {

                ColumnPanel cp = new ColumnPanel(array);
                cp.setOpaque(false);
                add(cp, BorderLayout.CENTER);
            }
        }

        EscapeAction escapeAction = new EscapeAction();
        InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        map.put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
        getActionMap().put("escape", escapeAction);
    }

    private JButton getEscapeButton() {
        return (escapeButton);
    }

    private void setEscapeButton(JButton b) {
        escapeButton = b;
    }

    /**
     * The buttons are either in a row or column.
     *
     * @return The orientation value.
     */
    public int getOrientation() {
        return (orientation);
    }

    /**
     * The buttons are either in a row or column.
     *
     * @param i The orientation value.
     */
    public void setOrientation(int i) {
        orientation = i;
    }

    /**
     * Convenience method to see if the buttons are laid out in a row.
     *
     * @return True if the buttons are in a row.
     */
    public boolean isHorizontal() {
        return (getOrientation() == SwingConstants.HORIZONTAL);
    }

    /**
     * Convenience method to see if the buttons are laid out in a column.
     *
     * @return True if the buttons are in a column.
     */
    public boolean isVertical() {
        return (getOrientation() == SwingConstants.VERTICAL);
    }

    /**
     * Add a listener.
     *
     * @param l A given listener.
     */
    public void addActionListener(ActionListener l) {
        actionList.add(l);
    }

    /**
     * Remove a listener.
     *
     * @param l A given listener.
     */
    public void removeActionListener(ActionListener l) {
        actionList.remove(l);
    }
    /**
     * Send out an action event.
     *
     * @param event The event to propagate.
     */
    public void fireActionEvent(ActionEvent event) {
        processActionEvent(event);
    }

    protected synchronized void processActionEvent(ActionEvent event) {

        for (int i = 0; i < actionList.size(); i++) {

            ActionListener l = actionList.get(i);
            l.actionPerformed(event);
        }
    }

    private void focusTraversal(JButton b) {

        // We need to update the focus keys for this panel
        Set<AWTKeyStroke> set = new HashSet<AWTKeyStroke>(b.
            getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        set.clear();
        set.add(KeyStroke.getKeyStroke("DOWN"));
        b.setFocusTraversalKeys(
            KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, set);

        set = new HashSet<AWTKeyStroke>(b.getFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        set.clear();
        set.add(KeyStroke.getKeyStroke("UP"));
        b.setFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, set);
    }

    /**
     * Just propogate all events to our listeners.
     *
     * @param event The event to send along.
     */
    public void actionPerformed(ActionEvent event) {
        fireActionEvent(event);
    }

    class EscapeAction extends AbstractAction {

        public EscapeAction() {
        }

        public void actionPerformed(ActionEvent e) {

            System.out.println("EscapeAction.actionPerformed");
            JButton b = getEscapeButton();
            if (b != null) {

                fireActionEvent(new ActionEvent(b, 1, "last"));
            }
        }
    }

}

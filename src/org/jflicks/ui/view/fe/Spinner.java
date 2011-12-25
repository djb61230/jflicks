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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.painter.MattePainter;

import org.jflicks.ui.view.fe.BaseCustomizePanel;

/**
 * Panel that is similar to a number spinner.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Spinner extends BaseCustomizePanel implements ActionListener,
    FocusListener {

    private JLabel leftLabel;
    private JLabel amountLabel;
    private JLabel rightLabel;
    private int amount;
    private EmptyBorder emptyBorder;
    private TitledBorder titledBorder;

    /**
     * Simple constructor with one argument.
     *
     * @param f The font to use for the text.
     */
    public Spinner(Font f) {

        Color back = getPanelColor();
        back = new Color(back.getRed(), back.getGreen(),
            back.getBlue(), (int) (getPanelAlpha() * 255));
        setPanelColor(back);
        MattePainter mpainter = new MattePainter(getPanelColor());
        setBackgroundPainter(mpainter);

        JLabel left = new JLabel("+");
        left.setForeground(getUnselectedColor());
        left.setFont(f);
        setLeftLabel(left);

        JLabel l = new JLabel("0");
        l.setForeground(getUnselectedColor());
        l.setHorizontalTextPosition(SwingConstants.CENTER);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setFont(f);
        setAmountLabel(l);

        JLabel right = new JLabel("-");
        right.setForeground(getUnselectedColor());
        right.setFont(f);
        setRightLabel(right);

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(left, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(l, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(right, gbc);

        setFocusable(true);

        InputMap map = getInputMap(JComponent.WHEN_FOCUSED);
        LeftAction la = new LeftAction();
        map.put(KeyStroke.getKeyStroke("LEFT"), "left");
        getActionMap().put("left", la);
        RightAction ra = new RightAction();
        map.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        getActionMap().put("right", ra);

        titledBorder = new TitledBorder("");
        emptyBorder = new EmptyBorder(titledBorder.getBorderInsets(this));

        setBorder(emptyBorder);

        addFocusListener(this);
    }

    private JLabel getLeftLabel() {
        return (leftLabel);
    }

    private void setLeftLabel(JLabel l) {
        leftLabel = l;
    }

    private JLabel getAmountLabel() {
        return (amountLabel);
    }

    private void setAmountLabel(JLabel l) {
        amountLabel = l;
    }

    private JLabel getRightLabel() {
        return (rightLabel);
    }

    private void setRightLabel(JLabel l) {
        rightLabel = l;
    }

    /**
     * We spin through number and keep track of an amount.
     *
     * @return The amount.
     */
    public int getAmount() {
        return (amount);
    }

    /**
     * We spin through number and keep track of an amount.
     *
     * @param i The amount.
     */
    public void setAmount(int i) {

        int old = amount;
        amount = i;
        firePropertyChange("Amount", old, amount);
        updateLabel();
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
    }

    /**
     * Pay attention to focus so we can update our border.
     *
     * @param event A FocusEvent instance.
     */
    public void focusGained(FocusEvent event) {

        JLabel l = getLeftLabel();
        if (l != null) {

            l.setForeground(getHighlightColor());
        }

        l = getAmountLabel();
        if (l != null) {

            l.setForeground(getHighlightColor());
        }

        l = getRightLabel();
        if (l != null) {

            l.setForeground(getHighlightColor());
        }
    }

    /**
     * Pay attention to focus so we can update our border.
     *
     * @param event A FocusEvent instance.
     */
    public void focusLost(FocusEvent event) {

        JLabel l = getLeftLabel();
        if (l != null) {

            l.setForeground(getUnselectedColor());
        }

        l = getAmountLabel();
        if (l != null) {

            l.setForeground(getUnselectedColor());
        }

        l = getRightLabel();
        if (l != null) {

            l.setForeground(getUnselectedColor());
        }
    }

    private boolean hasTheFocus() {

        return (isFocusOwner());
    }

    private void updateLabel() {

        JLabel l = getAmountLabel();
        if (l != null) {

            l.setText("" + getAmount());
        }
    }

    /**
     * We need to listen to action events to update from user actions
     * with the UI.
     *
     * @param event A given action event.
     */
    public void actionPerformed(ActionEvent event) {
    }

    class LeftAction extends AbstractAction {

        public LeftAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (hasFocus()) {

                int val = getAmount();
                setAmount(val + 1);

                updateLabel();
            }
        }
    }

    class RightAction extends AbstractAction {

        public RightAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (hasTheFocus()) {

                int val = getAmount();
                setAmount(val - 1);

                updateLabel();
            }
        }
    }

}

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

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JLabel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Simple class that hignlights a component when it gets the focus.
 * Also given a JLabel and an Icon, the label will be updated with the
 * icon during this same event.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class HighlightFocusAdapter extends FocusAdapter {

    private JLabel associatedLabel;
    private Icon associatedIcon;

    /**
     * Create a highlight focus adapter.  The non-null arguments will
     * result in the label being updated with the icon when the focus
     * occurs.
     *
     * @param l A given JLabel.
     * @param icon A given Icon.
     */
    public HighlightFocusAdapter(JLabel l, Icon icon) {

        setAssociatedLabel(l);
        setAssociatedIcon(icon);
    }

    /**
     * The focus has come, highlight the component, and set the JLabel's
     * Icon.
     *
     * @param event A given focus event for this component.
     */
    public void focusGained(FocusEvent event) {

        Object source = event.getSource();
        if (source instanceof AbstractButton) {

            AbstractButton button = (AbstractButton) source;
            button.setContentAreaFilled(true);
            updateLabel();
        }
    }

    /**
     * The focus has moved, unhighlight the component.
     *
     * @param event A given focus event for this component.
     */
    public void focusLost(FocusEvent event) {

        Object source = event.getSource();
        if (source instanceof AbstractButton) {

            AbstractButton button = (AbstractButton) source;
            button.setContentAreaFilled(false);
        }
    }

    /**
     * A JLabel can be associated with the component that will receive the
     * highlighting.
     *
     * @return The JLabel associated with the focused component.
     */
    public JLabel getAssociatedLabel() {
        return (associatedLabel);
    }

    /**
     * A JLabel can be associated with the component that will receive the
     * highlighting.
     *
     * @param l The JLabel associated with the focused component.
     */
    public void setAssociatedLabel(JLabel l) {
        associatedLabel = l;
    }

    /**
     * An Icon can be associated with the component.  The associated JLabel
     * will receive the Icon as a property, hence updating the look of the
     * JLabel.
     *
     * @return The Icon associated with the focused component.
     */
    public Icon getAssociatedIcon() {
        return (associatedIcon);
    }

    /**
     * An Icon can be associated with the component.  The associated JLabel
     * will receive the Icon as a property, hence updating the look of the
     * JLabel.
     *
     * @param i The Icon associated with the focused component.
     */
    public void setAssociatedIcon(Icon i) {
        associatedIcon = i;
    }

    private void updateLabel() {

        JLabel l = getAssociatedLabel();
        Icon i = getAssociatedIcon();
        if ((l != null) && (i != null)) {

            l.setIcon(i);
        }
    }

}

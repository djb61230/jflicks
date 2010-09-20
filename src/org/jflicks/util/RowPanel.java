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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Handy class that lays out a row of components.  Uses GridBag so it is
 * fairly robust and controllable to achieve pleasant results.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RowPanel extends JPanel {

    private static final int INSET = 4;

    /**
     * An array or varargs of components to put in a row.
     *
     * @param components The components to lay out nicely.
     */
    public RowPanel(JComponent ... components) {

        this(null, -1, GridBagConstraints.HORIZONTAL, components);
    }

    /**
     * A title and an array or varargs of components to put in a row.
     *
     * @param title A title to place around the panel.
     * @param components The components to lay out nicely.
     */
    public RowPanel(String title, JComponent ... components) {

        this(title, -1, GridBagConstraints.HORIZONTAL, components);
    }

    /**
     * A title and an array or varargs of components to put in a row.  Also
     * a "weight" value that enables one component to take all space of a
     * resize.
     *
     * @param title A title to place around the panel.
     * @param colWeight This "column" in the row will receive all "extra"
     * space.
     * @param components The components to lay out nicely.
     */
    public RowPanel(String title, int colWeight, JComponent ... components) {

        this(title, colWeight, GridBagConstraints.HORIZONTAL, components);
    }

    /**
     * A title and an array or varargs of components to put in a row.  Also
     * a "weight" value that enables one component to take all space of a
     * resize.  And a fill policy (ex - GridBagConstraints.HORIZONTAL).
     *
     * @param title A title to place around the panel.
     * @param colWeight This "column" in the row will receive all "extra"
     * space.
     * @param fill GridBag fill policy.
     * @param components The components to lay out nicely.
     */
    public RowPanel(String title, int colWeight, int fill,
        JComponent ... components) {

        if (title != null) {
            setBorder(BorderFactory.createTitledBorder(title));
        } else {
            setBorder(BorderFactory.createEmptyBorder());
        }
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = null;
        double weight = 0.0;
        if (colWeight == -1) {
            weight = 1.0 / ((double) components.length);
            //weight = ((double) components.length) / 2.0;
        }
        for (int i = 0; i < components.length; i++) {

            gbc = new GridBagConstraints();
            gbc.weighty = 1.0;
            if (colWeight == i) {
                gbc.weightx = 1.0;
            } else {
                gbc.weightx = weight;
            }
            gbc.gridx = i;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = fill;
            gbc.insets = new Insets(INSET, INSET, INSET, INSET);
            add(components[i], gbc);
        }
    }

}

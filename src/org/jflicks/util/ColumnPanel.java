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
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Handy class that lays out a column of components.  Uses GridBag so it is
 * fairly robust and controllable to achieve pleasant results.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ColumnPanel extends JPanel {

    private static final int INSET = 4;

    /**
     * An array or varargs of components to put in a column.
     *
     * @param components The components to lay out nicely.
     */
    public ColumnPanel(JComponent ... components) {

        this(null, false, -1, GridBagConstraints.HORIZONTAL, components);
    }

    /**
     * A title and an array or varargs of components to put in a column.
     *
     * @param title A title to place around the panel.
     * @param components The components to lay out nicely.
     */
    public ColumnPanel(String title, JComponent ... components) {

        this(title, false, -1, GridBagConstraints.HORIZONTAL, components);
    }

    /**
     * A title and an array or varargs of components to put in a column.
     *
     * @param title A title to place around the panel.
     * @param anchor Where to anchor the components.  Perhaps
     * GridBagConstraints.CENTER.
     * @param components The components to lay out nicely.
     */
    public ColumnPanel(String title, boolean anchor,
        JComponent ... components) {

        this(title, anchor, -1, GridBagConstraints.HORIZONTAL, components);
    }

    /**
     * A title and an array or varargs of components to put in a column.  Also
     * a "weight" value that enables one component to take all space of a
     * resize.  And a fill policy (ex - GridBagConstraints.HORIZONTAL).
     *
     * @param title A title to place around the panel.
     * @param anchor Where to anchor the components.  Perhaps
     * GridBagConstraints.CENTER.
     * @param rowWeight This "row" in the column will receive all "extra"
     * space.
     * @param components The components to lay out nicely.
     */
    public ColumnPanel(String title, boolean anchor, int rowWeight,
        JComponent ... components) {

        this(title, anchor, rowWeight, GridBagConstraints.HORIZONTAL,
            components);
    }

    /**
     * A title and an array or varargs of components to put in a column.  Also
     * a "weight" value that enables one component to take all space of a
     * resize.
     *
     * @param title A title to place around the panel.
     * @param rowWeight This "row" in the column will receive all "extra"
     * space.
     * @param components The components to lay out nicely.
     */
    public ColumnPanel(String title, int rowWeight, JComponent ... components) {

        this(title, false, rowWeight, GridBagConstraints.HORIZONTAL,
            components);
    }

    /**
     * A title and an array or varargs of components to put in a column.  Also
     * a "weight" value that enables one component to take all space of a
     * resize.
     *
     * @param title A title to place around the panel.
     * @param rowWeight This "row" in the column will receive all "extra"
     * space.
     * @param fill GridBag fill policy.
     * @param components The components to lay out nicely.
     */
    public ColumnPanel(String title, int rowWeight, int fill,
        JComponent ... components) {

        this(title, false, rowWeight, fill, components);
    }

    /**
     * A title and an array or varargs of components to put in a column.  Also
     * a "weight" value that enables one component to take all space of a
     * resize.  And a fill policy (ex - GridBagConstraints.HORIZONTAL).
     *
     * @param title A title to place around the panel.
     * @param anchor Where to anchor the components.  Perhaps
     * GridBagConstraints.CENTER.
     * @param rowWeight This "row" in the column will receive all "extra"
     * space.
     * @param fill GridBag fill policy.
     * @param components The components to lay out nicely.
     */
    public ColumnPanel(String title, boolean anchor, int rowWeight, int fill,
        JComponent ... components) {

        if (title != null) {
            setBorder(BorderFactory.createTitledBorder(title));
        } else {
            setBorder(BorderFactory.createEmptyBorder());
        }
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = null;
        double weight = 0.0;
        if (rowWeight == -1) {
            weight = 1.0 / ((double) components.length);
            //weight = ((double) components.length) / 2.0;
        }
        int cnt = components.length;
        if (anchor) {
            cnt++;
        }
        for (int i = 0; i < cnt; i++) {

            gbc = new GridBagConstraints();
            gbc.weightx = 1.0;
            if (rowWeight == i) {
                gbc.weighty = 1.0;
            } else {
                gbc.weighty = weight;
            }
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = fill;
            gbc.insets = new Insets(INSET, INSET, INSET, INSET);
            if (i == components.length) {
                add(new JLabel(""), gbc);
            } else {
                add(components[i], gbc);
            }
        }
    }

}

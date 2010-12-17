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
 * This is a simple panel that accepts string and component arrays
 * to form a grid of text prompts and components.  The prompts anchor
 * to the east and the components anchor to the west.  The components
 * will line up and will take any extra space on a resize.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class PromptPanel extends JPanel {

    private static final int INSET = 4;

    /**
     * An array or varargs of components with prompts to put in a 2 column
     * grid.  The rows are defined by the length of the arrays and it is
     * assumed the array lengths are equal.
     *
     * @param prompts Text used as a prompt to the components.
     * @param components The components to lay out nicely.
     */
    public PromptPanel(String[] prompts, JComponent[] components) {

        this(null, -1, prompts, components);
    }

    /**
     * An array or varargs of components with prompts to put in a 2 column
     * grid.  The rows are defined by the length of the arrays and it is
     * assumed the array lengths are equal.
     *
     * @param title The title border text.
     * @param rowWeight This "row" in the column will receive all "extra"
     * space.
     * @param prompts Text used as a prompt to the components.
     * @param components The components to lay out nicely.
     */
    public PromptPanel(String title, int rowWeight, String[] prompts,
        JComponent[] components) {

        setLayout(new GridBagLayout());

        if (title != null) {
            setBorder(BorderFactory.createTitledBorder(title));
        } else {
            setBorder(BorderFactory.createEmptyBorder());
        }

        double weight = 0.0;
        if (rowWeight == -1) {
            weight = ((double) components.length) / 2.0;
        }

        GridBagConstraints gbc = null;
        for (int i = 0; i < components.length; i++) {

            gbc = new GridBagConstraints();

            // First do do the prompt.
            gbc.weightx = 0.0;

            if (rowWeight == i) {
                gbc.weighty = 1.0;
            } else {
                gbc.weighty = weight;
            }
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.insets = new Insets(INSET, INSET, INSET, INSET);
            add(new JLabel(prompts[i]), gbc);

            // Now the component.
            gbc = new GridBagConstraints();
            gbc.weightx = 1.0;

            if (rowWeight == i) {
                gbc.weighty = 1.0;
            } else {
                gbc.weighty = weight;
            }
            gbc.gridx = 1;
            gbc.gridy = i;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(INSET, INSET, INSET, INSET);
            add(components[i], gbc);
        }
    }

    public PromptPanel(String[] prompts, JComponent[] components,
        double[] yweights) {

        this(null, prompts, components, yweights);
    }

    public PromptPanel(String title, String[] prompts, JComponent[] components,
        double[] yweights) {

        setLayout(new GridBagLayout());

        if (title != null) {
            setBorder(BorderFactory.createTitledBorder(title));
        } else {
            setBorder(BorderFactory.createEmptyBorder());
        }

        GridBagConstraints gbc = null;
        for (int i = 0; i < components.length; i++) {

            gbc = new GridBagConstraints();

            // First do do the prompt.
            gbc.weightx = 0.0;
            gbc.weighty = yweights[i];
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.insets = new Insets(INSET, INSET, INSET, INSET);
            add(new JLabel(prompts[i]), gbc);

            // Now the component.
            gbc = new GridBagConstraints();
            gbc.weightx = 1.0;
            gbc.weighty = yweights[i];
            gbc.gridx = 1;
            gbc.gridy = i;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(INSET, INSET, INSET, INSET);
            add(components[i], gbc);
        }
    }

}

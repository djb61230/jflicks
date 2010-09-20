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
package org.jflicks.configure;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 * This class allows the user to edit a list style configuration NameValue
 * instance.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class FromChoiceTypePanel extends BaseTypePanel {

    private JComboBox valueComboBox;

    /**
     * Simple constructor with required NameValue argument.
     *
     * @param nv A given NameValue instance.
     */
    public FromChoiceTypePanel(NameValue nv) {

        super(nv);
        if (nv != null) {

            JComboBox cb = new JComboBox(nv.getChoices());
            cb.setEditable(false);
            cb.setSelectedItem(nv.getValue());
            setValueComboBox(cb);

            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.weightx = 0.0;
            gbc.weighty = 0.0;
            gbc.insets = new Insets(4, 4, 4, 4);

            add(new JLabel(nv.getName()), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
            gbc.insets = new Insets(4, 4, 4, 4);

            add(cb, gbc);
        }
    }

    private JComboBox getValueComboBox() {
        return (valueComboBox);
    }

    private void setValueComboBox(JComboBox cb) {
        valueComboBox = cb;
    }

    /**
     * {@inheritDoc}
     */
    public String getEditedValue() {

        String result = null;

        JComboBox cb = getValueComboBox();
        if (cb != null) {

            result = (String) cb.getSelectedItem();
        }

        return (result);
    }

}


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
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * This class displays the summary details of the current configuration
 * of some component.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class StringTypePanel extends BaseTypePanel {

    private JTextField valueTextField;

    /**
     * Simple constructor with NameValue argument.
     *
     * @param nv A given NameValue to edit.
     */
    public StringTypePanel(NameValue nv) {

        super(nv);

        if (nv != null) {

            JTextField vtf = new JTextField(20);
            vtf.setText((String) nv.getValue());
            setValueTextField(vtf);

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

            add(vtf, gbc);
        }
    }

    private JTextField getValueTextField() {
        return (valueTextField);
    }

    private void setValueTextField(JTextField tf) {
        valueTextField = tf;
    }

    /**
     * {@inheritDoc}
     */
    public String getEditedValue() {

        String result = null;

        JTextField tf = getValueTextField();
        if (tf != null) {

            result = tf.getText().trim();
        }

        return (result);
    }

}


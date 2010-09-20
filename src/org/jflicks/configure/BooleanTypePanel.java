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
import javax.swing.JCheckBox;

import org.jflicks.util.Util;

/**
 * This class displays the summary details of the current configuration
 * of some component.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class BooleanTypePanel extends BaseTypePanel {

    private JCheckBox valueCheckBox;

    /**
     * Simple constructor with NameValue argument.
     *
     * @param nv A given NameValue to edit.
     */
    public BooleanTypePanel(NameValue nv) {

        super(nv);

        if (nv != null) {

            JCheckBox vcb = new JCheckBox(nv.getName());
            vcb.setSelected(Util.str2boolean(nv.getValue(), false));
            setValueCheckBox(vcb);

            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
            gbc.insets = new Insets(4, 4, 4, 4);

            add(vcb, gbc);
        }
    }

    private JCheckBox getValueCheckBox() {
        return (valueCheckBox);
    }

    private void setValueCheckBox(JCheckBox cb) {
        valueCheckBox = cb;
    }

    /**
     * {@inheritDoc}
     */
    public String getEditedValue() {

        String result = null;

        JCheckBox cb = getValueCheckBox();
        if (cb != null) {

            if (cb.isSelected()) {
                result = "true";
            } else {
                result = "false";
            }
        }

        return (result);
    }

}


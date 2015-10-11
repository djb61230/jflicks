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
package org.jflicks.ui.view.scheduler;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;

import org.jflicks.nms.NMS;
import org.jflicks.nms.State;

/**
 * A base class that defines a panel for this app.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DiskPanel extends AbstractStatusPanel {

    private StateLabel capacityStateLabel;
    private StateLabel freeStateLabel;

    /**
     * Default constructor.
     */
    public DiskPanel() {

        setCapacityStateLabel(new StateLabel());
        setFreeStateLabel(new StateLabel());

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getCapacityStateLabel(), gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getFreeStateLabel(), gbc);

        setBorder(BorderFactory.createTitledBorder("Disk Space"));
    }

    private StateLabel getCapacityStateLabel() {
        return (capacityStateLabel);
    }

    private void setCapacityStateLabel(StateLabel l) {
        capacityStateLabel = l;
    }

    private StateLabel getFreeStateLabel() {
        return (freeStateLabel);
    }

    private void setFreeStateLabel(StateLabel l) {
        freeStateLabel = l;
    }

    public void populate() {

        StateLabel csl = getCapacityStateLabel();
        StateLabel fsl = getFreeStateLabel();
        if ((csl != null) && (fsl != null)) {

            NMS n = getNMS();
            if (n != null) {

                State state = n.getState();
                if (state != null) {

                    double cap = round((double) state.getCapacity());
                    csl.setWarning(false);
                    csl.setText("Capacity is " + cap + " GB");

                    double free = round((double) state.getFree());
                    fsl.setWarning(false);
                    fsl.setText("Free space is " + free + " GB");

                    if (free < (cap * 0.10)) {
                        fsl.setWarning(true);
                    } else {
                        fsl.setWarning(false);
                    }

                } else {

                    csl.setText("");
                    csl.setIcon(null);
                }

            } else {

                csl.setText("");
                csl.setIcon(null);
                fsl.setText("");
                fsl.setIcon(null);
            }
        }
    }

    private double round(double d) {

        double result = d;

        result /= 1073741824;
        result *= 10.0;
        int iresult = (int) result;
        result = (double) iresult;
        result /= 10.0;

        return (result);
    }

}

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
package org.jflicks.wizard;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.Map;
import javax.swing.JDialog;

/**
 * This class does all the display hard work.  Users just need to supply
 * their parent frame so it is centered over it.  And a Wizard instance.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class WizardDisplayer {

    private WizardDisplayer() {
    }

    /**
     * Show the wizard.  It will show up somewhere on the screen since the
     * user has not given their parent frame.
     *
     * @param w A Wizard instance.
     * @return A Map instance on completion, null on cancel.
     */
    public static Map showWizard(Wizard w) {
        return (showWizard(null, w));
    }

    /**
     * Show the wizard.  It will show up centered on the parent frame.
     *
     * @param parent A frame to center upon.
     * @param w A Wizard instance.
     * @return A Map instance on completion, null on cancel.
     */
    public static Map showWizard(Frame parent, Wizard w) {

        Map result = null;

        if (w != null) {

            final JDialog dialog = new JDialog(parent, w.getTitle(), true);
            dialog.getContentPane().setLayout(new BorderLayout());
            dialog.getContentPane().add(BorderLayout.CENTER, w);
            w.addWizardListener(
                new WizardListener() {

                    public void stateChanged(WizardEvent we) {

                        dialog.setVisible(false);
                        dialog.dispose();
                    }
                }
            );
            dialog.pack();
            dialog.setLocationRelativeTo(parent);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
            result = w.getMap();
        }

        return (result);
    }

}


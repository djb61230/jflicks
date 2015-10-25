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
package org.jflicks.ui.view.status;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.JXLabel;

/**
 * A simple label that display a message with a state.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class StateLabel extends JXLabel {

    private ImageIcon goImageIcon;
    private ImageIcon warnImageIcon;
    private boolean warning;

    /**
     * Default constructor.
     */
    public StateLabel() {

        setWarning(false);
        setLineWrap(true);
        setGoImageIcon(new ImageIcon(getClass().getResource("green.png")));
        setWarnImageIcon(new ImageIcon(getClass().getResource("red.png")));
    }

    public boolean isWarning() {
        return (warning);
    }

    public void setWarning(boolean b) {
        warning = b;

        if (warning) {
            setIcon(getWarnImageIcon());
        } else {
            setIcon(getGoImageIcon());
        }
    }

    private ImageIcon getGoImageIcon() {
        return (goImageIcon);
    }

    private void setGoImageIcon(ImageIcon ii) {
        goImageIcon = ii;
    }

    private ImageIcon getWarnImageIcon() {
        return (warnImageIcon);
    }

    private void setWarnImageIcon(ImageIcon ii) {
        warnImageIcon = ii;
    }

}

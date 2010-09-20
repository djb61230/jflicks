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
package org.jflicks.ui.view.fe.screen.preference;

import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.io.File;

import org.jflicks.ui.view.fe.BaseCustomizePanel;

/**
 * Simple class to load a theme property file.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class PropertyInfo extends BaseCustomizePanel {

    /**
     * Simple empty constructor.
     */
    public PropertyInfo() {
    }

    /**
     * {@inheritDoc}
     */
    public void performControl() {
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {
    }

    /**
     * Be able to load any properties file in any directory.
     *
     * @param dir A given directory File.
     * @param name A file name to load.
     */
    public void loadProperties(File dir, String name) {

        super.loadProperties(dir, name);

        // At this point we compute our fonts...
        setSmallFont(new Font(getSmallFontFamily(), getSmallFontStyle(),
            (int) getSmallFontSize()));
        setMediumFont(new Font(getMediumFontFamily(), getMediumFontStyle(),
            (int) getMediumFontSize()));
        setLargeFont(new Font(getLargeFontFamily(), getLargeFontStyle(),
            (int) getLargeFontSize()));

        // Apply our transparency to our panel color.
        Color panelc = getPanelColor();
        panelc = new Color(panelc.getRed(), panelc.getGreen(),
            panelc.getBlue(), (int) (getPanelAlpha() * 255));
        setPanelColor(panelc);
    }

}


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

import java.awt.Dimension;
import java.util.Map;
import javax.swing.JPanel;

/**
 * @author Doug Barnum
 * @version 1.0
 */
public interface WizardInterface {

    /**
     * A panel provider has a title property.
     *
     * @return The title as a String instance.
     */
    String getTitle();

    /**
     * A panel provider has a title property.
     *
     * @param s The title as a String instance.
     */
    void setTitle(String s);

    /**
     * Each panel in the set of panels has a unique ID.
     *
     * @return An array of String instances.
     */
    String[] getPanelIds();

    /**
     * Each panel in the set of panels has a unique ID.
     *
     * @param array An array of String instances.
     */
    void setPanelIds(String[] array);

    /**
     * Each panel has a short description.
     *
     * @return An array of String instances.
     */
    String[] getPanelDescriptions();

    /**
     * Each panel has a short description.
     *
     * @param array An array of String instances.
     */
    void setPanelDescriptions(String[] array);

    /**
     * Set the preferred size of the panels.  Defaults to 400x300.
     *
     * @return A Dimension instance.
     */
    Dimension getPreferredSize();

    /**
     * Is there a next panel?
     *
     * @param p A given panel.
     * @return True when the given panel can do next.
     */
    boolean canPanelNext(JPanel p);

    /**
     * Create a wizard that can be shown to the user.
     *
     * @return A Wizard instance.
     */
    Wizard createWizard();

    /**
     * Create the panel given it's ID.
     *
     * @param id A given ID.
     * @return A JPanel instance.
     */
    JPanel createPanel(String id);

    /**
     * The user clicked through the finish so there edited data needs to be
     * fetched.
     *
     * @return A Map instance with the users data.
     */
    Map finish();
}


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
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JPanel;

/**
 * Extend this class to build a set of wizrd panels.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class WizardPanelProvider implements WizardInterface,
    Wizardable {

    private ArrayList<WizardListener> wizardList =
        new ArrayList<WizardListener>();

    private String title;
    private String[] panelIds;
    private String[] panelDescriptions;

    /**
     * Simple empty constructor.
     */
    public WizardPanelProvider() {
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return (title);
    }

    /**
     * {@inheritDoc}
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getPanelIds() {

        String[] result = null;

        if (panelIds != null) {

            result = Arrays.copyOf(panelIds, panelIds.length);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void setPanelIds(String[] array) {

        if (array != null) {
            panelIds = Arrays.copyOf(array, array.length);
        } else {
            panelIds = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String[] getPanelDescriptions() {

        String[] result = null;

        if (panelDescriptions != null) {

            result = Arrays.copyOf(panelDescriptions, panelDescriptions.length);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void setPanelDescriptions(String[] array) {

        if (array != null) {
            panelDescriptions = Arrays.copyOf(array, array.length);
        } else {
            panelDescriptions = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize() {
        return (new Dimension(400, 300));
    }

    /**
     * {@inheritDoc}
     */
    public Wizard createWizard() {

        return (new Wizard(this));
    }

    /**
     * {@inheritDoc}
     */
    public void addWizardListener(WizardListener l) {
        wizardList.add(l);
    }

    /**
     * {@inheritDoc}
     */
    public void removeWizardListener(WizardListener l) {
        wizardList.remove(l);
    }

    private void fireEvent(int type) {

        WizardEvent we = new WizardEvent(this, type);
        processWizardEvent(we);
    }

    protected synchronized void processWizardEvent(WizardEvent event) {

        for (int i = 0; i < wizardList.size(); i++) {

            WizardListener l = wizardList.get(i);
            l.stateChanged(event);
        }
    }

    protected void fireNextEvent() {

        fireEvent(WizardEvent.NEXT);
    }

    /**
     * {@inheritDoc}
     */
    public boolean canPanelNext(JPanel p) {
        return (false);
    }

}


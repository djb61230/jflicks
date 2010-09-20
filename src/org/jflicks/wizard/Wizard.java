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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jflicks.util.ColumnPanel;
import org.jflicks.util.RowPanel;

/**
 * A wizard performs based upon it's supplied WizardPanelProvider instance.
 * Users create (extend) the WizardPanelProvider to create the desired
 * panels and to then create the proper edited data in a Map instance.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Wizard extends JPanel implements ActionListener, Wizardable,
    WizardListener {

    private ArrayList<WizardListener> wizardList =
        new ArrayList<WizardListener>();

    private WizardPanelProvider wizardPanelProvider;
    private JLabel[] stepLabels;
    private JButton previousButton;
    private JButton nextButton;
    private JButton finishButton;
    private JButton cancelButton;
    private int panelIndex;
    private int panelCount;
    private JPanel currentPanel;
    private Map map;

    /**
     * Create a wizard based upon the given WizardPanelProvider.
     *
     * @param wpp A given WizardPanelProvider instance.
     */
    public Wizard(WizardPanelProvider wpp) {

        setPanelIndex(0);
        setPanelCount(0);
        if (wpp != null) {

            wpp.addWizardListener(this);
            setWizardPanelProvider(wpp);
            String[] descArray = wpp.getPanelDescriptions();
            String[] idArray = wpp.getPanelIds();
            if ((descArray != null) && (descArray.length > 0)) {

                setPanelCount(descArray.length);
                JLabel[] labels = new JLabel[descArray.length + 1];
                for (int i = 0; i < descArray.length; i++) {

                    labels[i] = new JLabel((i + 1) + ". " + descArray[i]);
                }
                labels[descArray.length] = new JLabel("");
                setStepLabels(labels);

                ColumnPanel labelCP = new ColumnPanel("Steps",
                    labels.length - 1, labels);

                setPreviousButton(new JButton("Prev"));
                getPreviousButton().addActionListener(this);
                setNextButton(new JButton("Next"));
                getNextButton().addActionListener(this);
                setFinishButton(new JButton("Finish"));
                getFinishButton().addActionListener(this);
                setCancelButton(new JButton("Cancel"));
                getCancelButton().addActionListener(this);
                RowPanel buttonRP = new RowPanel(
                    getPreviousButton(), getNextButton(),
                    getFinishButton(), getCancelButton());

                JPanel first = wpp.createPanel(idArray[0]);
                ColumnPanel stepPanel = new ColumnPanel(descArray[0], 0, first);
                setCurrentPanel(stepPanel);

                setLayout(new BorderLayout(4, 4));
                add(BorderLayout.WEST, labelCP);
                add(BorderLayout.CENTER, stepPanel);
                add(BorderLayout.SOUTH, buttonRP);
                makeState();

                Dimension d = wpp.getPreferredSize();
                if (d != null) {
                    setPreferredSize(new Dimension(d));
                }
            }
        }
    }

    /**
     * Convenience method to access the WizardPanelProvider title property.
     *
     * @return The WizardPanelProvider title property.
     */
    public String getTitle() {

        String result = null;
        WizardPanelProvider wpp = getWizardPanelProvider();
        if (wpp != null) {
            result = wpp.getTitle();
        }

        return (result);
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

    private WizardPanelProvider getWizardPanelProvider() {
        return (wizardPanelProvider);
    }

    private void setWizardPanelProvider(WizardPanelProvider wpp) {
        wizardPanelProvider = wpp;
    }

    private JLabel[] getStepLabels() {
        return (stepLabels);
    }

    private void setStepLabels(JLabel[] array) {
        stepLabels = array;
    }

    private JButton getPreviousButton() {
        return (previousButton);
    }

    private void setPreviousButton(JButton b) {
        previousButton = b;
    }

    private JButton getNextButton() {
        return (nextButton);
    }

    private void setNextButton(JButton b) {
        nextButton = b;
    }

    private JButton getFinishButton() {
        return (finishButton);
    }

    private void setFinishButton(JButton b) {
        finishButton = b;
    }

    private JButton getCancelButton() {
        return (cancelButton);
    }

    private void setCancelButton(JButton b) {
        cancelButton = b;
    }

    /**
     * Get the map.
     *
     * @return a Map instance.
     */
    public Map getMap() {
        return (map);
    }

    private void setMap(Map m) {
        map = m;
    }

    private JPanel getCurrentPanel() {
        return (currentPanel);
    }

    private void setCurrentPanel(JPanel p) {
        currentPanel = p;
    }

    private int getPanelCount() {
        return (panelCount);
    }

    private void setPanelCount(int i) {
        panelCount = i;
    }

    private int getPanelIndex() {
        return (panelIndex);
    }

    private void setPanelIndex(int i) {
        panelIndex = i;
    }

    private void incrPanelIndex() {

        setPanelIndex(getPanelIndex() + 1);
        if (getPanelIndex() == getPanelCount()) {
            setPanelIndex(getPanelCount() - 1);
        }
    }

    private void decrPanelIndex() {

        setPanelIndex(getPanelIndex() - 1);
        if (getPanelIndex() < 0) {
            setPanelIndex(0);
        }
    }

    /**
     * React to the button presses.
     *
     * @param e The given ActionEvent instance.
     */
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == getPreviousButton()) {
            previousAction();
        } else if (e.getSource() == getNextButton()) {

            WizardPanelProvider wpp = getWizardPanelProvider();
            if (wpp != null) {
                if (!wpp.canPanelNext(getCurrentPanel())) {
                    nextAction();
                }

            } else {
                nextAction();
            }

        } else if (e.getSource() == getFinishButton()) {
            finishAction();
        } else if (e.getSource() == getCancelButton()) {
            cancelAction();
        }
    }

    private void previousAction() {

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        decrPanelIndex();
        displayPanel();
        setCursor(Cursor.getDefaultCursor());
    }

    private void nextAction() {

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        incrPanelIndex();
        displayPanel();
        setCursor(Cursor.getDefaultCursor());
    }

    private void finishAction() {

        WizardPanelProvider wpp = getWizardPanelProvider();
        if (wpp != null) {

            setMap(wpp.finish());
        }
        fireEvent(WizardEvent.FINISH);
    }

    private void cancelAction() {
        fireEvent(WizardEvent.CANCEL);
    }

    private void displayPanel() {

        WizardPanelProvider wpp = getWizardPanelProvider();
        if (wpp != null) {

            String[] idArray = wpp.getPanelIds();
            String[] descArray = wpp.getPanelDescriptions();
            if (idArray != null) {

                int pi = getPanelIndex();
                if (pi < idArray.length) {

                    JPanel p = wpp.createPanel(idArray[pi]);
                    JPanel current = getCurrentPanel();
                    if ((p != null) && (current != null)) {

                        ColumnPanel stepPanel =
                            new ColumnPanel(descArray[pi], 0, p);
                        remove(current);
                        add(BorderLayout.CENTER, stepPanel);
                        setCurrentPanel(stepPanel);
                        validate();
                    }
                }
            }
        }
        makeState();
    }

    private void makeState() {

        int index = getPanelIndex();
        int count = getPanelCount();
        JButton b = getPreviousButton();
        if (b != null) {

            if (index > 0) {
                b.setEnabled(true);
            } else {
                b.setEnabled(false);
            }
        }
        b = getNextButton();
        if (b != null) {

            if ((index + 1) == count) {
                b.setEnabled(false);
            } else {
                b.setEnabled(true);
            }
        }
        b = getFinishButton();
        if (b != null) {

            if ((index + 1) == count) {
                b.setEnabled(true);
            } else {
                b.setEnabled(false);
            }
        }

        JLabel[] labels = getStepLabels();
        if (labels != null) {

            for (int i = 0; i < labels.length; i++) {

                if (i == index) {
                    labels[i].setForeground(Color.red);
                } else {
                    labels[i].setForeground(Color.black);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(WizardEvent e) {

        if (e.getState() == WizardEvent.NEXT) {

            nextAction();
        }

    }

}


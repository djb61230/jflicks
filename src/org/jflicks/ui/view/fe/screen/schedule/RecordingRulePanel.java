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
package org.jflicks.ui.view.fe.screen.schedule;

import java.awt.AWTKeyStroke;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jflicks.nms.NMS;
import org.jflicks.tv.Channel;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Task;
import org.jflicks.util.ColumnPanel;
import org.jflicks.ui.view.fe.Dialog;
import org.jflicks.ui.view.fe.BaseCustomizePanel;

import org.jdesktop.swingx.JXLabel;

/**
 * Panel that deals with adding a RecordingRule.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordingRulePanel extends BaseCustomizePanel
    implements ActionListener, PropertyChangeListener {

    private static final String ADVANCED_TEXT = "Advanced Settings";

    private RecordingRule recordingRule;
    private NMS nms;
    private JXLabel nameLabel;
    private JXLabel channelLabel;
    private JXLabel durationLabel;
    private JRadioButton[] typeRadioButtons;
    private JRadioButton[] priorityRadioButtons;
    private Spinner beginSpinner;
    private Spinner endSpinner;
    private JButton advancedButton;
    private JButton okButton;
    private JButton cancelButton;
    private JButton advancedOkButton;
    private JButton advancedCancelButton;
    private boolean accept;
    private boolean advancedAccept;

    /**
     * Simple constructor.
     */
    public RecordingRulePanel() {

        JXLabel nameprompt = new JXLabel("Name");
        nameprompt.setHorizontalTextPosition(SwingConstants.RIGHT);
        nameprompt.setHorizontalAlignment(SwingConstants.RIGHT);
        nameprompt.setFont(getSmallFont());

        JXLabel namelab = new JXLabel();
        namelab.setFont(getSmallFont());
        setNameLabel(namelab);

        JXLabel channelprompt = new JXLabel("Channel");
        channelprompt.setHorizontalTextPosition(SwingConstants.RIGHT);
        channelprompt.setHorizontalAlignment(SwingConstants.RIGHT);
        channelprompt.setFont(getSmallFont());

        JXLabel channellab = new JXLabel();
        channellab.setFont(getSmallFont());
        setChannelLabel(channellab);

        JXLabel durationprompt = new JXLabel("Duration");
        durationprompt.setHorizontalTextPosition(SwingConstants.RIGHT);
        durationprompt.setHorizontalAlignment(SwingConstants.RIGHT);
        durationprompt.setFont(getSmallFont());

        JXLabel durationlab = new JXLabel();
        durationlab.setFont(getSmallFont());
        setDurationLabel(durationlab);

        JXLabel typeprompt = new JXLabel("Type");
        typeprompt.setHorizontalTextPosition(SwingConstants.RIGHT);
        typeprompt.setHorizontalAlignment(SwingConstants.RIGHT);
        typeprompt.setFont(getSmallFont());

        JRadioButton[] typeRadio = createRadioButtons(getSmallFont(),
            RecordingRule.getTypeNames(), new ButtonGroup());
        setTypeRadioButtons(typeRadio);

        JXLabel priorityprompt = new JXLabel("Priority");
        priorityprompt.setHorizontalTextPosition(SwingConstants.RIGHT);
        priorityprompt.setHorizontalAlignment(SwingConstants.RIGHT);
        priorityprompt.setFont(getSmallFont());

        JRadioButton[] priorityRadio = createRadioButtons(getSmallFont(),
            RecordingRule.getPriorityNames(), new ButtonGroup());
        setPriorityRadioButtons(priorityRadio);

        JXLabel beginprompt = new JXLabel("Begin Padding (min)");
        beginprompt.setHorizontalTextPosition(SwingConstants.RIGHT);
        beginprompt.setHorizontalAlignment(SwingConstants.RIGHT);
        beginprompt.setFont(getSmallFont());

        /*
        SpinnerNumberModel bmodel = new SpinnerNumberModel(0, -120, 120, 1);
        JSpinner bspinner = new JSpinner(bmodel);
        bspinner.addChangeListener(this);
        bspinner.setFont(getSmallFont());
        setBeginSpinner(bspinner);
        */

        Spinner bspinner = new Spinner(getSmallFont());
        bspinner.addPropertyChangeListener("Amount", this);
        setBeginSpinner(bspinner);

        JXLabel endprompt = new JXLabel("End Padding (min)");
        endprompt.setHorizontalTextPosition(SwingConstants.RIGHT);
        endprompt.setHorizontalAlignment(SwingConstants.RIGHT);
        endprompt.setFont(getSmallFont());

        /*
        SpinnerNumberModel emodel = new SpinnerNumberModel(0, -120, 120, 1);
        JSpinner espinner = new JSpinner(emodel);
        espinner.addChangeListener(this);
        espinner.setFont(getSmallFont());
        setEndSpinner(espinner);
        */

        Spinner espinner = new Spinner(getSmallFont());
        espinner.addPropertyChangeListener("Amount", this);
        setEndSpinner(espinner);

        JButton advanced = new JButton(ADVANCED_TEXT);
        advanced.addActionListener(this);
        advanced.setEnabled(false);
        advanced.setFont(getSmallFont());
        setAdvancedButton(advanced);

        JButton ok = new JButton("Ok");
        ok.addActionListener(this);
        ok.setFont(getSmallFont());
        setOkButton(ok);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        cancel.setFont(getSmallFont());
        setCancelButton(cancel);

        JButton aok = new JButton("Ok");
        aok.addActionListener(this);
        aok.setFont(getSmallFont());
        setAdvancedOkButton(aok);

        JButton acancel = new JButton("Cancel");
        acancel.addActionListener(this);
        acancel.setFont(getSmallFont());
        setAdvancedCancelButton(acancel);

        setLayout(new GridBagLayout());

        int yindex = 0;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = yindex;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(nameprompt, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridy = yindex;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(namelab, gbc);

        yindex++;

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = yindex;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(channelprompt, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridy = yindex;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(channellab, gbc);

        yindex++;

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = yindex;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(durationprompt, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridy = yindex;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(durationlab, gbc);

        yindex++;

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = yindex;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(typeprompt, gbc);

        for (int i = 0; i < typeRadio.length; i++) {

            gbc = new GridBagConstraints();
            gbc.weightx = 0.5;
            gbc.weighty = 0.0;
            gbc.gridx = 1;
            gbc.gridy = yindex++;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(4, 4, 4, 4);

            add(typeRadio[i], gbc);
        }

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = yindex;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(priorityprompt, gbc);

        for (int i = 0; i < priorityRadio.length; i++) {

            gbc = new GridBagConstraints();
            gbc.weightx = 0.5;
            gbc.weighty = 0.0;
            gbc.gridx = 1;
            gbc.gridy = yindex++;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(4, 4, 4, 4);

            add(priorityRadio[i], gbc);
        }

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = yindex;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(beginprompt, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridy = yindex;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(bspinner, gbc);

        yindex++;

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = yindex;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(endprompt, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridy = yindex;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(espinner, gbc);

        yindex++;

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = yindex;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(advanced, gbc);

        yindex++;

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = yindex;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(ok, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridy = yindex;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(cancel, gbc);
        setBorder(BorderFactory.createLineBorder(getHighlightColor()));

        setFocusable(true);
        requestFocus();

        InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        CancelAction ca = new CancelAction(getCancelButton());
        map.put(KeyStroke.getKeyStroke("ESC"), "cancel");
        getActionMap().put("cancel", ca);
    }

    /**
     * All UI components show data from a RecordingRule instance.
     *
     * @return A RecordingRule object.
     */
    public RecordingRule getRecordingRule() {
        return (recordingRule);
    }

    /**
     * All UI components show data from a RecordingRule instance.
     *
     * @param rr A RecordingRule object.
     */
    public void setRecordingRule(RecordingRule rr) {

        if (rr != null) {

            rr = new RecordingRule(rr);
            apply(getNameLabel(), rr.getName());
            NMS n = getNMS();
            if (n != null) {

                Channel c = n.getChannelById(rr.getChannelId());
                if (c != null) {
                    apply(getChannelLabel(), c.getNumber());
                } else {
                    apply(getChannelLabel(), null);
                }

            } else {
                apply(getChannelLabel(), null);
            }

            apply(getDurationLabel(), durationToString(rr.getDuration()));
            apply(getTypeRadioButtons(), rr.getType());
            apply(getPriorityRadioButtons(), rr.getPriority());
            apply(getBeginSpinner(), rr.getBeginPadding() / 60);
            apply(getEndSpinner(), rr.getEndPadding() / 60);
            getAdvancedButton().setEnabled(rr.getTasks() != null);

        } else {

            apply(getNameLabel(), null);
            apply(getChannelLabel(), null);
            apply(getDurationLabel(), null);
            apply(getTypeRadioButtons(), RecordingRule.SERIES_TYPE);
            apply(getPriorityRadioButtons(), RecordingRule.NORMAL_PRIORITY);
            apply(getBeginSpinner(), 0);
            apply(getEndSpinner(), 0);
            getAdvancedButton().setEnabled(false);
        }

        recordingRule = rr;
    }

    /**
     * A refernce to NMS is needed to do the work of this UI component.
     *
     * @return An NMS instance.
     */
    public NMS getNMS() {
        return (nms);
    }

    /**
     * A refernce to NMS is needed to do the work of this UI component.
     *
     * @param n An NMS instance.
     */
    public void setNMS(NMS n) {
        nms = n;
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
     * True if  the user decided to accept what they have changed/edited
     * in the UI panel.
     *
     * @return True when the user clicks the OK button.
     */
    public boolean isAccept() {
        return (accept);
    }

    private void setAccept(boolean b) {
        accept = b;
    }

    private boolean isAdvancedAccept() {
        return (advancedAccept);
    }

    private void setAdvancedAccept(boolean b) {
        advancedAccept = b;
    }

    private JXLabel getNameLabel() {
        return (nameLabel);
    }

    private void setNameLabel(JXLabel l) {
        nameLabel = l;
    }

    private JXLabel getChannelLabel() {
        return (channelLabel);
    }

    private void setChannelLabel(JXLabel l) {
        channelLabel = l;
    }

    private JXLabel getDurationLabel() {
        return (durationLabel);
    }

    private void setDurationLabel(JXLabel l) {
        durationLabel = l;
    }

    private JRadioButton[] getTypeRadioButtons() {
        return (typeRadioButtons);
    }

    private void setTypeRadioButtons(JRadioButton[] array) {
        typeRadioButtons = array;
    }

    private JRadioButton[] getPriorityRadioButtons() {
        return (priorityRadioButtons);
    }

    private void setPriorityRadioButtons(JRadioButton[] array) {
        priorityRadioButtons = array;
    }

    private Spinner getBeginSpinner() {
        return (beginSpinner);
    }

    private void setBeginSpinner(Spinner s) {
        beginSpinner = s;
    }

    private Spinner getEndSpinner() {
        return (endSpinner);
    }

    private void setEndSpinner(Spinner s) {
        endSpinner = s;
    }

    private JButton getAdvancedButton() {
        return (advancedButton);
    }

    private void setAdvancedButton(JButton b) {
        advancedButton = b;
    }

    /**
     * This panel has an ok button so the user can choose to accept their
     * action.
     *
     * @return A JButton instance.
     */
    public JButton getOkButton() {
        return (okButton);
    }

    private void setOkButton(JButton b) {
        okButton = b;
    }

    /**
     * This panel has a cancel button so the user can choose to do no
     * action.
     *
     * @return A JButton instance.
     */
    public JButton getCancelButton() {
        return (cancelButton);
    }

    private void setCancelButton(JButton b) {
        cancelButton = b;
    }

    private JButton getAdvancedOkButton() {
        return (advancedOkButton);
    }

    private void setAdvancedOkButton(JButton b) {
        advancedOkButton = b;
    }

    private JButton getAdvancedCancelButton() {
        return (advancedCancelButton);
    }

    private void setAdvancedCancelButton(JButton b) {
        advancedCancelButton = b;
    }

    private String durationToString(long l) {

        return ((l / 60) + " minutes");
    }

    private void apply(JXLabel l, String s) {

        if ((l != null) && (s != null)) {

            l.setText(s);
        }
    }

    private void apply(JRadioButton[] array, int index) {

        if ((array != null) && (array.length > index)) {

            array[index].setSelected(true);
        }
    }

    private void apply(Spinner s, int value) {

        if (s != null) {

            s.setAmount(value);
        }
    }

    private JRadioButton[] createRadioButtons(Font f, String[] array,
        ButtonGroup bg) {

        JRadioButton[] result = null;

        if ((array != null) && (array.length > 0)) {

            result = new JRadioButton[array.length];
            for (int i = 0; i < result.length; i++) {

                result[i] = new JRadioButton(array[i]);
                result[i].setFont(f);
                result[i].getInputMap().put(KeyStroke.getKeyStroke("ENTER"),
                    "toggle");
                result[i].getActionMap().put("toggle", new RadioAction());
                result[i].getInputMap().put(KeyStroke.getKeyStroke("SPACE"),
                    "toggle");
                result[i].getActionMap().put("toggle", new RadioAction());
                result[i].addActionListener(this);

                if (bg != null) {

                    bg.add(result[i]);
                }
            }
        }

        return (result);
    }

    private void advancedAction() {

        RecordingRule rr = getRecordingRule();
        if (rr != null) {

            Task[] tasks = rr.getTasks();
            if (tasks != null) {

                JComponent[] cbuts = new JComponent[tasks.length + 2];
                for (int i = 0; i < tasks.length; i++) {

                    JCheckBox cb = new JCheckBox(tasks[i].getDescription());
                    cb.setFont(getSmallFont());

                    cb.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),
                        "toggle");
                    cb.getActionMap().put("toggle", new CheckAction());

                    cb.setSelected(tasks[i].isRun());
                    cbuts[i] = cb;
                }

                cbuts[tasks.length] = getAdvancedOkButton();
                cbuts[tasks.length + 1] = getAdvancedCancelButton();

                setAdvancedAccept(false);
                ColumnPanel cp = new ColumnPanel(cbuts);
                cp.setBorder(BorderFactory.createLineBorder(
                    getHighlightColor()));
                HashSet<AWTKeyStroke> set =
                    new HashSet<AWTKeyStroke>(cp.getFocusTraversalKeys(
                        KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
                set.clear();
                set.add(KeyStroke.getKeyStroke("DOWN"));
                cp.setFocusTraversalKeys(
                    KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, set);

                set = new HashSet<AWTKeyStroke>(cp.getFocusTraversalKeys(
                        KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
                set.clear();
                set.add(KeyStroke.getKeyStroke("UP"));
                cp.setFocusTraversalKeys(
                    KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, set);

                InputMap map =
                    cp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                map.put(KeyStroke.getKeyStroke("ESC"), "cancel");
                cp.getActionMap().put("cancel",
                    new CancelAction(getAdvancedCancelButton()));

                Dialog.showPanel(null, cp, getAdvancedOkButton(),
                    getAdvancedCancelButton());

                requestFocus();
                if (isAdvancedAccept()) {

                    for (int i = 0; i < tasks.length; i++) {

                        JCheckBox cb = (JCheckBox) cbuts[i];
                        tasks[i].setRun(cb.isSelected());
                    }
                }
            }
        }
    }

    private int member(JRadioButton[] array, JRadioButton b) {

        int result = -1;

        if ((array != null) && (b != null)) {

            for (int i = 0; i < array.length; i++) {

                if (b == array[i]) {

                    result = i;
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * We need to listen to action events to update from user actions
     * with the UI.
     *
     * @param event A given action event.
     */
    public void actionPerformed(ActionEvent event) {

        RecordingRule rr = getRecordingRule();
        if (rr != null) {

            if (event.getSource() == getAdvancedButton()) {
                advancedAction();
            } else if (event.getSource() == getOkButton()) {
                setAccept(true);
            } else if (event.getSource() == getCancelButton()) {
                setAccept(false);
            } else if (event.getSource() == getAdvancedOkButton()) {
                setAdvancedAccept(true);
            } else if (event.getSource() == getAdvancedCancelButton()) {
                setAdvancedAccept(false);
            }
        }
    }

    /**
     * We listen for changes in our Spinner UI elements.
     *
     * @param event A ChangeEvent instance.
     */
    public void propertyChange(PropertyChangeEvent event) {

        RecordingRule rr = getRecordingRule();
        if (rr != null) {

            if (event.getSource() == getBeginSpinner()) {

                rr.setBeginPadding(getBeginSpinner().getAmount() * 60);

            } else if (event.getSource() == getEndSpinner()) {

                rr.setEndPadding(getEndSpinner().getAmount() * 60);
            }
        }
    }

    static class CheckAction extends AbstractAction {

        public CheckAction() {
        }

        public void actionPerformed(ActionEvent e) {

            JCheckBox cb = (JCheckBox) e.getSource();
            cb.setSelected(!cb.isSelected());
        }
    }

    class RadioAction extends AbstractAction {

        public RadioAction() {
        }

        public void actionPerformed(ActionEvent e) {

            JRadioButton rb = (JRadioButton) e.getSource();
            rb.setSelected(true);
            RecordingRule rr = getRecordingRule();
            if (rr != null) {

                int index = member(getTypeRadioButtons(), rb);
                if (index != -1) {

                    rr.setType(index);

                } else {

                    index = member(getPriorityRadioButtons(), rb);
                    if (index != -1) {

                        rr.setPriority(index);
                    }
                }
            }
        }
    }

    static class CancelAction extends AbstractAction {

        private JButton button;

        public CancelAction(JButton b) {
            button = b;
        }

        public void actionPerformed(ActionEvent e) {

            if (button != null) {

                button.doClick();
            }
        }
    }

}

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
package org.jflicks.ui.view.fe;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.jflicks.nms.NMS;
import org.jflicks.tv.Channel;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Task;
import org.jflicks.ui.view.fe.Dialog;
import org.jflicks.ui.view.fe.BaseCustomizePanel;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * Panel that deals with adding a RecordingRule.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordingRulePanel extends BaseCustomizePanel
    implements ActionListener, FocusListener, PropertyChangeListener {

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
    private JXButton advancedButton;
    private JXButton okButton;
    private JXButton cancelButton;
    private JXButton advancedOkButton;
    private JXButton advancedCancelButton;
    private boolean accept;
    private boolean advancedAccept;
    private Frame frame;

    /**
     * Simple constructor.
     */
    public RecordingRulePanel() {

        Color back = getPanelColor();
        back = new Color(back.getRed(), back.getGreen(),
            back.getBlue(), (int) (getPanelAlpha() * 255));
        setPanelColor(back);
        MattePainter mpainter = new MattePainter(getPanelColor());
        setBackgroundPainter(mpainter);

        JXLabel nameprompt = new JXLabel("Name");
        nameprompt.setForeground(getUnselectedColor());
        nameprompt.setHorizontalTextPosition(SwingConstants.RIGHT);
        nameprompt.setHorizontalAlignment(SwingConstants.RIGHT);
        nameprompt.setFont(getSmallFont());

        JXLabel namelab = new JXLabel();
        namelab.setForeground(getUnselectedColor());
        namelab.setFont(getSmallFont());
        setNameLabel(namelab);

        JXLabel channelprompt = new JXLabel("Channel");
        channelprompt.setForeground(getUnselectedColor());
        channelprompt.setHorizontalTextPosition(SwingConstants.RIGHT);
        channelprompt.setHorizontalAlignment(SwingConstants.RIGHT);
        channelprompt.setFont(getSmallFont());

        JXLabel channellab = new JXLabel();
        channellab.setForeground(getUnselectedColor());
        channellab.setFont(getSmallFont());
        setChannelLabel(channellab);

        JXLabel durationprompt = new JXLabel("Duration");
        durationprompt.setForeground(getUnselectedColor());
        durationprompt.setHorizontalTextPosition(SwingConstants.RIGHT);
        durationprompt.setHorizontalAlignment(SwingConstants.RIGHT);
        durationprompt.setFont(getSmallFont());

        JXLabel durationlab = new JXLabel();
        durationlab.setForeground(getUnselectedColor());
        durationlab.setFont(getSmallFont());
        setDurationLabel(durationlab);

        JXLabel typeprompt = new JXLabel("Type");
        typeprompt.setForeground(getUnselectedColor());
        typeprompt.setHorizontalTextPosition(SwingConstants.RIGHT);
        typeprompt.setHorizontalAlignment(SwingConstants.RIGHT);
        typeprompt.setFont(getSmallFont());

        JRadioButton[] typeRadio = createRadioButtons(getSmallFont(),
            RecordingRule.getTypeNames(), new ButtonGroup());
        setTypeRadioButtons(typeRadio);

        JXLabel priorityprompt = new JXLabel("Priority");
        priorityprompt.setForeground(getUnselectedColor());
        priorityprompt.setHorizontalTextPosition(SwingConstants.RIGHT);
        priorityprompt.setHorizontalAlignment(SwingConstants.RIGHT);
        priorityprompt.setFont(getSmallFont());

        JRadioButton[] priorityRadio = createRadioButtons(getSmallFont(),
            RecordingRule.getPriorityNames(), new ButtonGroup());
        setPriorityRadioButtons(priorityRadio);

        JXLabel beginprompt = new JXLabel("Begin Padding (min)");
        beginprompt.setForeground(getUnselectedColor());
        beginprompt.setHorizontalTextPosition(SwingConstants.RIGHT);
        beginprompt.setHorizontalAlignment(SwingConstants.RIGHT);
        beginprompt.setFont(getSmallFont());

        Spinner bspinner = new Spinner(getSmallFont());
        bspinner.addPropertyChangeListener("Amount", this);
        setBeginSpinner(bspinner);

        JXLabel endprompt = new JXLabel("End Padding (min)");
        endprompt.setForeground(getUnselectedColor());
        endprompt.setHorizontalTextPosition(SwingConstants.RIGHT);
        endprompt.setHorizontalAlignment(SwingConstants.RIGHT);
        endprompt.setFont(getSmallFont());

        Spinner espinner = new Spinner(getSmallFont());
        espinner.addPropertyChangeListener("Amount", this);
        setEndSpinner(espinner);

        JXButton advanced = new JXButton(ADVANCED_TEXT);
        advanced.setForeground(getUnselectedColor());
        advanced.setBackground(getPanelColor());
        advanced.addActionListener(this);
        advanced.setEnabled(false);
        advanced.setFont(getSmallFont());
        advanced.addFocusListener(this);
        advanced.setFocusPainted(false);
        setAdvancedButton(advanced);

        JXButton ok = new JXButton("Ok");
        ok.setForeground(getUnselectedColor());
        ok.setBackground(getPanelColor());
        ok.addActionListener(this);
        ok.setFont(getSmallFont());
        ok.addFocusListener(this);
        ok.setFocusPainted(false);
        setOkButton(ok);

        JXButton cancel = new JXButton("Cancel");
        cancel.setForeground(getUnselectedColor());
        cancel.setBackground(getPanelColor());
        cancel.addActionListener(this);
        cancel.setFont(getSmallFont());
        cancel.addFocusListener(this);
        cancel.setFocusPainted(false);
        setCancelButton(cancel);

        JXButton aok = new JXButton("Ok");
        aok.setForeground(getUnselectedColor());
        aok.setBackground(getPanelColor());
        aok.addActionListener(this);
        aok.setFont(getSmallFont());
        aok.addFocusListener(this);
        aok.setFocusPainted(false);
        setAdvancedOkButton(aok);

        JXButton acancel = new JXButton("Cancel");
        acancel.setForeground(getUnselectedColor());
        acancel.setBackground(getPanelColor());
        acancel.addActionListener(this);
        acancel.setFont(getSmallFont());
        acancel.addFocusListener(this);
        acancel.setFocusPainted(false);
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

        InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        CancelAction ca = new CancelAction(getCancelButton());
        map.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        getActionMap().put("cancel", ca);
    }

    /**
     * Be aware of our top level frame so we can center our
     * dialogs when we use them.
     *
     * @return A Frame instance.
     */
    public Frame getFrame() {
        return (frame);
    }

    /**
     * Be aware of our top level frame so we can center our
     * dialogs when we use them.
     *
     * @param f A Frame instance.
     */
    public void setFrame(Frame f) {
        frame = f;
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

                Channel c = n.getChannelById(rr.getChannelId(),
                    rr.getListingId());
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
            getAdvancedButton().setEnabled(hasTasksToSelect(rr.getTasks()));

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

    private JXButton getAdvancedButton() {
        return (advancedButton);
    }

    private void setAdvancedButton(JXButton b) {
        advancedButton = b;
    }

    /**
     * This panel has an ok button so the user can choose to accept their
     * action.
     *
     * @return A JXButton instance.
     */
    public JXButton getOkButton() {
        return (okButton);
    }

    private void setOkButton(JXButton b) {
        okButton = b;
    }

    /**
     * This panel has a cancel button so the user can choose to do no
     * action.
     *
     * @return A JXButton instance.
     */
    public JXButton getCancelButton() {
        return (cancelButton);
    }

    private void setCancelButton(JXButton b) {
        cancelButton = b;
    }

    private JXButton getAdvancedOkButton() {
        return (advancedOkButton);
    }

    private void setAdvancedOkButton(JXButton b) {
        advancedOkButton = b;
    }

    private JXButton getAdvancedCancelButton() {
        return (advancedCancelButton);
    }

    private void setAdvancedCancelButton(JXButton b) {
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

    private boolean hasTasksToSelect(Task[] array) {

        boolean result = false;

        if ((array != null) && (array.length > 0)) {

            for (int i = 0; i < array.length; i++) {

                if (array[i].isSelectable()) {

                    result = true;
                    break;
                }
            }
        }

        return (result);
    }

    private JRadioButton[] createRadioButtons(Font f, String[] array,
        ButtonGroup bg) {

        JRadioButton[] result = null;

        if ((array != null) && (array.length > 0)) {

            result = new JRadioButton[array.length];
            for (int i = 0; i < result.length; i++) {

                result[i] = new JRadioButton(array[i]);
                result[i].setForeground(getUnselectedColor());
                result[i].setBackground(getPanelColor());
                result[i].setFont(f);
                result[i].getInputMap().put(KeyStroke.getKeyStroke("ENTER"),
                    "toggle");
                result[i].getActionMap().put("toggle", new RadioAction());
                result[i].getInputMap().put(KeyStroke.getKeyStroke("SPACE"),
                    "toggle");
                result[i].getActionMap().put("toggle", new RadioAction());
                result[i].addActionListener(this);
                result[i].addFocusListener(this);
                result[i].setFocusPainted(false);

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

                ArrayList<JComponent> list = new ArrayList<JComponent>();
                ArrayList<Integer> ilist = new ArrayList<Integer>();
                for (int i = 0; i < tasks.length; i++) {

                    if (tasks[i].isSelectable()) {

                        JCheckBox cb = new JCheckBox(tasks[i].getDescription());
                        cb.setForeground(getUnselectedColor());
                        cb.setBackground(getPanelColor());
                        cb.setFont(getSmallFont());

                        cb.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),
                            "toggle");
                        cb.getActionMap().put("toggle", new CheckAction());

                        cb.setSelected(tasks[i].isRun());
                        cb.addFocusListener(this);
                        cb.setFocusPainted(false);
                        list.add(cb);
                        ilist.add(Integer.valueOf(i));
                    }
                }

                list.add(getAdvancedOkButton());
                list.add(getAdvancedCancelButton());
                JComponent[] cbuts = list.toArray(new JComponent[list.size()]);
                Integer[] tindexes = ilist.toArray(new Integer[ilist.size()]);

                setAdvancedAccept(false);

                JXPanel cp = new JXPanel();
                cp.setBackgroundPainter(getBackgroundPainter());
                cp.setLayout(new GridBagLayout());

                for (int i = 0; i < cbuts.length; i++) {

                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.weightx = 1.0;
                    gbc.weighty = 1.0;
                    gbc.gridx = 0;
                    gbc.gridy = i;
                    gbc.gridwidth = 1;
                    gbc.gridheight = 1;
                    gbc.anchor = GridBagConstraints.CENTER;
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.insets = new Insets(4, 4, 4, 4);
                    cp.add(cbuts[i], gbc);
                }

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
                map.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
                cp.getActionMap().put("cancel",
                    new CancelAction(getAdvancedCancelButton()));

                Dialog.showPanel(getFrame(), cp, getAdvancedOkButton(),
                    getAdvancedCancelButton());

                requestFocus();
                if (isAdvancedAccept()) {

                    for (int i = 0; i < cbuts.length; i++) {

                        if (cbuts[i] instanceof JCheckBox) {

                            int tindex = tindexes[i].intValue();
                            JCheckBox cb = (JCheckBox) cbuts[i];
                            tasks[tindex].setRun(cb.isSelected());
                        }
                    }
                }

                requestFocus();
                getAdvancedButton().requestFocus();
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
     * Keep track of the focus so we can control the text color.
     *
     * @param event The given focus event.
     */
    public void focusGained(FocusEvent event) {

        if (event.getSource() instanceof AbstractButton) {

            AbstractButton b = (AbstractButton) event.getSource();
            b.setForeground(getHighlightColor());
        }
    }

    /**
     * Keep track of the focus so we can control the text color.
     *
     * @param event The given focus event.
     */
    public void focusLost(FocusEvent event) {

        if (event.getSource() instanceof AbstractButton) {

            AbstractButton b = (AbstractButton) event.getSource();
            b.setForeground(getUnselectedColor());
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

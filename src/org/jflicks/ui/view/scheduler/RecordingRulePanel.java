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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import org.jflicks.nms.NMS;
import org.jflicks.tv.Channel;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Task;
import org.jflicks.util.ColumnPanel;
import org.jflicks.util.PromptPanel;
import org.jflicks.util.Util;

/**
 * Panel that deals with adding a RecordingRule.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordingRulePanel extends JPanel implements ActionListener,
    ChangeListener {

    private static final String ADVANCED_TEXT = "Advanced Settings";

    private RecordingRule recordingRule;
    private NMS nms;
    private JTextField nameTextField;
    private JTextField channelTextField;
    private JTextField durationTextField;
    private JComboBox typeComboBox;
    private JComboBox priorityComboBox;
    private JSpinner beginSpinner;
    private JSpinner endSpinner;
    private JButton advancedButton;

    /**
     * Simple constructor.
     */
    public RecordingRulePanel() {

        JTextField nametf = new JTextField(20);
        nametf.setEditable(false);
        setNameTextField(nametf);

        JTextField channeltf = new JTextField(20);
        channeltf.setEditable(false);
        setChannelTextField(channeltf);

        JTextField durationtf = new JTextField(20);
        durationtf.setEditable(false);
        setDurationTextField(durationtf);

        JComboBox tcb = new JComboBox(RecordingRule.getTypeNames());
        tcb.setSelectedIndex(RecordingRule.SERIES_TYPE);
        tcb.addActionListener(this);
        setTypeComboBox(tcb);

        JComboBox pcb = new JComboBox(RecordingRule.getPriorityNames());
        pcb.setSelectedIndex(RecordingRule.NORMAL_PRIORITY);
        pcb.addActionListener(this);
        setPriorityComboBox(pcb);

        SpinnerNumberModel bmodel = new SpinnerNumberModel(0, -120, 120, 1);
        JSpinner bspinner = new JSpinner(bmodel);
        bspinner.addChangeListener(this);
        setBeginSpinner(bspinner);

        SpinnerNumberModel emodel = new SpinnerNumberModel(0, -120, 120, 1);
        JSpinner espinner = new JSpinner(emodel);
        espinner.addChangeListener(this);
        setEndSpinner(espinner);

        JButton advanced = new JButton(ADVANCED_TEXT);
        advanced.addActionListener(this);
        advanced.setEnabled(false);
        setAdvancedButton(advanced);

        String[] prompts = {
            "Name", "Channel", "Duration", "Type", "Priority",
            "Begin Padding (min)", "End Padding (min)", ""
        };

        JComponent[] comps = {
            nametf, channeltf, durationtf, tcb, pcb, bspinner, espinner,
            advanced
        };

        PromptPanel pp = new PromptPanel(prompts, comps);

        setLayout(new BorderLayout());
        add(pp, BorderLayout.CENTER);
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
        recordingRule = rr;

        if (rr != null) {

            apply(getNameTextField(), rr.getName());
            NMS n = getNMS();
            if (n != null) {

                Channel c = n.getChannelById(rr.getChannelId());
                if (c != null) {
                    apply(getChannelTextField(), c.getNumber());
                } else {
                    apply(getChannelTextField(), null);
                }

            } else {
                apply(getChannelTextField(), null);
            }

            apply(getDurationTextField(), durationToString(rr.getDuration()));
            apply(getTypeComboBox(), rr.getType());
            apply(getPriorityComboBox(), rr.getPriority());
            apply(getBeginSpinner(), rr.getBeginPadding() / 60);
            apply(getEndSpinner(), rr.getEndPadding() / 60);
            getAdvancedButton().setEnabled(hasTasksToSelect(rr.getTasks()));

        } else {

            apply(getNameTextField(), null);
            apply(getChannelTextField(), null);
            apply(getDurationTextField(), null);
            apply(getTypeComboBox(), RecordingRule.SERIES_TYPE);
            apply(getPriorityComboBox(), RecordingRule.NORMAL_PRIORITY);
            apply(getBeginSpinner(), 0);
            apply(getEndSpinner(), 0);
            getAdvancedButton().setEnabled(false);
        }
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

    private JTextField getNameTextField() {
        return (nameTextField);
    }

    private void setNameTextField(JTextField tf) {
        nameTextField = tf;
    }

    private JTextField getChannelTextField() {
        return (channelTextField);
    }

    private void setChannelTextField(JTextField tf) {
        channelTextField = tf;
    }

    private JTextField getDurationTextField() {
        return (durationTextField);
    }

    private void setDurationTextField(JTextField tf) {
        durationTextField = tf;
    }

    private JComboBox getTypeComboBox() {
        return (typeComboBox);
    }

    private void setTypeComboBox(JComboBox cb) {
        typeComboBox = cb;
    }

    private JComboBox getPriorityComboBox() {
        return (priorityComboBox);
    }

    private void setPriorityComboBox(JComboBox cb) {
        priorityComboBox = cb;
    }

    private JSpinner getBeginSpinner() {
        return (beginSpinner);
    }

    private void setBeginSpinner(JSpinner s) {
        beginSpinner = s;
    }

    private JSpinner getEndSpinner() {
        return (endSpinner);
    }

    private void setEndSpinner(JSpinner s) {
        endSpinner = s;
    }

    private JButton getAdvancedButton() {
        return (advancedButton);
    }

    private void setAdvancedButton(JButton b) {
        advancedButton = b;
    }

    private String durationToString(long l) {

        return ((l / 60) + " minutes");
    }

    private void apply(JComboBox cb, int i) {

        if (cb != null) {

            cb.setSelectedIndex(i);
        }
    }

    private void apply(JTextComponent c, String s) {

        if (c != null) {

            if (s != null) {

                c.setText(s.trim());

            } else {

                c.setText("");
            }
        }
    }

    private void apply(JSpinner s, int value) {

        if (s != null) {

            s.setValue(value);
        }
    }

    private int spinnerToInt(JSpinner s) {

        int result = 0;

        if (s != null) {

            Integer iobj = (Integer) s.getValue();
            if (iobj != null) {

                result = iobj.intValue() * 60;
            }
        }

        return (result);
    }

    private boolean hasTasksToSelect(Task[] array) {

        boolean result = false;

        System.out.println("hasTasksToSelect: " + array);
        if ((array != null) && (array.length > 0)) {

            for (int i = 0; i < array.length; i++) {

                System.out.println("hasTasksToSelect: " + array[i].getTitle());
                System.out.println("hasTasksToSelect: " + array[i].isSelectable());
                if (array[i].isSelectable()) {

                    result = true;
                    break;
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

                ArrayList<JCheckBox> clist = new ArrayList<JCheckBox>();
                for (int i = 0; i < tasks.length; i++) {

                    if (tasks[i].isSelectable()) {

                        JCheckBox b = new JCheckBox(tasks[i].getDescription());
                        b.setToolTipText(tasks[i].getTitle());
                        b.setSelected(tasks[i].isRun());
                        clist.add(b);
                    }
                }

                JCheckBox[] cbuts = null;
                if (clist.size() > 0) {

                    cbuts = clist.toArray(new JCheckBox[clist.size()]);
                }

                ColumnPanel cp = new ColumnPanel(cbuts);
                if (Util.showDialog(Util.findFrame(this), ADVANCED_TEXT, cp)) {

                    for (int i = 0; i < cbuts.length; i++) {

                        tasks[i].setRun(cbuts[i].isSelected());
                    }
                }
            }
        }
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

            if (event.getSource() == getTypeComboBox()) {
                rr.setType(getTypeComboBox().getSelectedIndex());
            } else if (event.getSource() == getPriorityComboBox()) {
                rr.setPriority(getPriorityComboBox().getSelectedIndex());
            } else if (event.getSource() == getAdvancedButton()) {
                advancedAction();
            }
        }
    }

    /**
     * We listen for changes in our Spinner UI elements.
     *
     * @param event A ChangeEvent instance.
     */
    public void stateChanged(ChangeEvent event) {

        RecordingRule rr = getRecordingRule();
        if (rr != null) {

            if (event.getSource() == getBeginSpinner()) {

                rr.setBeginPadding(spinnerToInt(getBeginSpinner()));

            } else if (event.getSource() == getEndSpinner()) {

                rr.setEndPadding(spinnerToInt(getEndSpinner()));
            }
        }
    }

}

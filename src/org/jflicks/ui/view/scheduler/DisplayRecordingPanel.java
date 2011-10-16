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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jflicks.tv.Recording;
import org.jflicks.nms.NMS;

/**
 * A class that implements a UI to display the recording recordings.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DisplayRecordingPanel extends JPanel
    implements ListSelectionListener  {

    private JList recordingList;
    private RecordingPanel recordingPanel;
    private Recording[] recordings;
    private SchedulerView schedulerView;
    private StopAction stopAction;
    private DeleteAction deleteAction;
    private DeleteAllowAction deleteAllowAction;

    /**
     * Empty constructor.
     */
    public DisplayRecordingPanel(SchedulerView v) {

        setSchedulerView(v);

        JList l = new JList();
        l.setPrototypeCellValue("012345678901234567890123456789");
        l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        l.setVisibleRowCount(10);
        l.addListSelectionListener(this);
        setRecordingList(l);
        JScrollPane scroller = new JScrollPane(l,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        RecordingPanel recp = new RecordingPanel();
        setRecordingPanel(recp);

        StopAction sa = new StopAction();
        sa.setEnabled(false);
        setStopAction(sa);
        JButton stopb = new JButton(sa);

        DeleteAction da = new DeleteAction();
        da.setEnabled(false);
        setDeleteAction(da);
        JButton deleteb = new JButton(da);

        DeleteAllowAction daa = new DeleteAllowAction();
        daa.setEnabled(false);
        setDeleteAllowAction(daa);
        JButton deleteAllowb = new JButton(daa);

        setLayout(new GridBagLayout());

        JPanel recPanel = new JPanel(new BorderLayout());
        recPanel.add(BorderLayout.CENTER, scroller);
        recPanel.setBorder(BorderFactory.createTitledBorder("Recordings"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(recPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(recp, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(stopb, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(deleteb, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(deleteAllowb, gbc);
    }

    private SchedulerView getSchedulerView() {
        return (schedulerView);
    }

    private void setSchedulerView(SchedulerView v) {
        schedulerView = v;
    }

    /**
     * This UI displays an array of Recording instances.
     *
     * @return An array of Recording objects.
     */
    public Recording[] getRecordings() {

        Recording[] result = null;

        if (recordings != null) {

            result = Arrays.copyOf(recordings, recordings.length);
        }

        return (result);
    }

    /**
     * This UI displays an array of Recording instances.
     *
     * @param array An array of Recording objects.
     */
    public void setRecordings(Recording[] array) {

        if (array != null) {
            recordings = Arrays.copyOf(array, array.length);
        } else {
            recordings = null;
        }

        JList list = getRecordingList();
        if (list != null) {

            list.setListData(recordings);
        }

        RecordingPanel rp = getRecordingPanel();
        if (rp != null) {

            rp.setRecording(null);
        }
    }

    private JList getRecordingList() {
        return (recordingList);
    }

    private void setRecordingList(JList l) {
        recordingList = l;
    }

    private RecordingPanel getRecordingPanel() {
        return (recordingPanel);
    }

    private void setRecordingPanel(RecordingPanel p) {
        recordingPanel = p;
    }

    private StopAction getStopAction() {
        return (stopAction);
    }

    private void setStopAction(StopAction a) {
        stopAction = a;
    }

    private DeleteAction getDeleteAction() {
        return (deleteAction);
    }

    private void setDeleteAction(DeleteAction a) {
        deleteAction = a;
    }

    private DeleteAllowAction getDeleteAllowAction() {
        return (deleteAllowAction);
    }

    private void setDeleteAllowAction(DeleteAllowAction a) {
        deleteAllowAction = a;
    }

    /**
     * We listen for selection on the rule list box.
     *
     * @param event The given list selection event.
     */
    public void valueChanged(ListSelectionEvent event) {

        if (!event.getValueIsAdjusting()) {

            if (event.getSource() == getRecordingList()) {

                JList l = getRecordingList();
                int index = l.getSelectedIndex();
                if (index != -1) {

                    Recording r = (Recording) l.getSelectedValue();
                    RecordingPanel rp = getRecordingPanel();
                    if ((rp != null) && (r != null)) {

                        rp.setRecording(r);
                        getStopAction().setEnabled(r.isCurrentlyRecording());
                        getDeleteAction().setEnabled(!r.isCurrentlyRecording());
                        getDeleteAllowAction().setEnabled(
                            !r.isCurrentlyRecording());
                    }

                } else {

                    getStopAction().setEnabled(false);
                    getDeleteAction().setEnabled(false);
                    getDeleteAllowAction().setEnabled(false);
                }
            }
        }
    }

    class StopAction extends AbstractAction {

        public StopAction() {

            ImageIcon sm = new ImageIcon(getClass().getResource("stop16.png"));
            ImageIcon lge = new ImageIcon(getClass().getResource("stop32.png"));
            putValue(NAME, "Stop");
            putValue(SHORT_DESCRIPTION, "Stop this Recording Now");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_S));
        }

        public void actionPerformed(ActionEvent e) {

            RecordingPanel rp = getRecordingPanel();
            SchedulerView v = getSchedulerView();
            if ((v != null) && (rp != null)) {

                Recording r = rp.getRecording();
                if ((r != null) && (r.isCurrentlyRecording())) {

                    NMS n = v.getNMSByHostPort(r.getHostPort());
                    if (n != null) {

                        n.stopRecording(r);
                    }
                }
            }
        }
    }

    class DeleteAction extends AbstractAction {

        public DeleteAction() {

            ImageIcon sm =
                new ImageIcon(getClass().getResource("delete16.png"));
            ImageIcon lge =
                new ImageIcon(getClass().getResource("delete32.png"));
            putValue(NAME, "Delete");
            putValue(SHORT_DESCRIPTION, "Delete this Recording");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_D));
        }

        public void actionPerformed(ActionEvent e) {

            RecordingPanel rp = getRecordingPanel();
            SchedulerView v = getSchedulerView();
            if ((v != null) && (rp != null)) {

                Recording r = rp.getRecording();
                if (r != null) {

                    NMS n = v.getNMSByHostPort(r.getHostPort());
                    if (n != null) {

                        n.removeRecording(r, false);
                    }
                }
            }
        }
    }

    class DeleteAllowAction extends AbstractAction {

        public DeleteAllowAction() {

            ImageIcon sm =
                new ImageIcon(getClass().getResource("delete16.png"));
            ImageIcon lge =
                new ImageIcon(getClass().getResource("delete32.png"));
            putValue(NAME, "Delete (Allow re-recording)");
            putValue(SHORT_DESCRIPTION,
                "Delete this Recording (Allow re-recording)");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_A));
        }

        public void actionPerformed(ActionEvent e) {

            RecordingPanel rp = getRecordingPanel();
            SchedulerView v = getSchedulerView();
            if ((v != null) && (rp != null)) {

                Recording r = rp.getRecording();
                if (r != null) {

                    NMS n = v.getNMSByHostPort(r.getHostPort());
                    if (n != null) {

                        n.removeRecording(r, true);
                    }
                }
            }
        }
    }

}

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
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jflicks.tv.Recording;

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

    /**
     * Empty constructor.
     */
    public DisplayRecordingPanel() {

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

        setLayout(new GridBagLayout());

        JPanel recPanel = new JPanel(new BorderLayout());
        recPanel.add(BorderLayout.CENTER, scroller);
        recPanel.setBorder(BorderFactory.createTitledBorder("Recordings"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(recPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(recp, gbc);
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
                    }
                }
            }
        }
    }

}

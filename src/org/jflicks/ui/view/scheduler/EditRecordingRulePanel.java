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
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.nms.NMS;
import org.jflicks.util.ProgressBar;
import org.jflicks.tv.Channel;
import org.jflicks.tv.RecordingRule;

/**
 * A implements a View so a user can control the scheduling of
 * recordings.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class EditRecordingRulePanel extends JPanel
    implements ListSelectionListener  {

    private NMS nms;
    private JList ruleList;
    private RecordingRulePanel recordingRulePanel;
    private Channel channel;
    private RecordingRule[] recordingRules;

    /**
     * Constructor with one required argument.
     *
     * @param n A given NMS to communicate updates.
     */
    public EditRecordingRulePanel(NMS n) {

        setNMS(n);
        JList l = new JList();
        l.setPrototypeCellValue("012345678901234567890123456789");
        l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        l.setVisibleRowCount(14);
        l.addListSelectionListener(this);
        setRuleList(l);
        JScrollPane rulelistScroller = new JScrollPane(l,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        RecordingRulePanel rrp = new RecordingRulePanel();
        rrp.setNMS(n);
        setRecordingRulePanel(rrp);

        UpdateAction updateAction = new UpdateAction(this);
        JButton button = new JButton(updateAction);

        JPanel rrPanel = new JPanel(new BorderLayout());
        rrPanel.add(BorderLayout.CENTER, rulelistScroller);
        rrPanel.setBorder(BorderFactory.createTitledBorder("Recording Rules"));

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(rrPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(rrp, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(button, gbc);
    }

    private NMS getNMS() {
        return (nms);
    }

    private void setNMS(NMS n) {
        nms = n;
    }

    /**
     * This UI displays an array of RecordingRule instances.
     *
     * @return An array of RecordingRule objects.
     */
    public RecordingRule[] getRecordingRules() {

        RecordingRule[] result = null;

        if (recordingRules != null) {

            result = Arrays.copyOf(recordingRules, recordingRules.length);
        }

        return (result);
    }

    /**
     * This UI displays an array of RecordingRule instances.
     *
     * @param array An array of RecordingRule objects.
     */
    public void setRecordingRules(RecordingRule[] array) {

        if (array != null) {
            recordingRules = Arrays.copyOf(array, array.length);
        } else {
            recordingRules = null;
        }

        JList list = getRuleList();
        if (list != null) {

            list.setListData(recordingRules);
        }

        RecordingRulePanel rrp = getRecordingRulePanel();
        if (rrp != null) {

            rrp.setRecordingRule(null);
        }
    }

    private JList getRuleList() {
        return (ruleList);
    }

    private void setRuleList(JList l) {
        ruleList = l;
    }

    private RecordingRulePanel getRecordingRulePanel() {
        return (recordingRulePanel);
    }

    private void setRecordingRulePanel(RecordingRulePanel p) {
        recordingRulePanel = p;
    }

    /**
     * We listen for selection on the rule list box.
     *
     * @param event The given list selection event.
     */
    public void valueChanged(ListSelectionEvent event) {

        if (!event.getValueIsAdjusting()) {

            if (event.getSource() == getRuleList()) {

                JList l = getRuleList();
                int index = l.getSelectedIndex();
                if (index != -1) {

                    RecordingRule rr = (RecordingRule) l.getSelectedValue();
                    RecordingRulePanel rrp = getRecordingRulePanel();
                    if ((rrp != null) && (rr != null)) {

                        rrp.setRecordingRule(rr);
                    }
                }
            }
        }
    }

    class UpdateAction extends AbstractAction implements JobListener {

        private JComponent component;

        public UpdateAction(JComponent c) {

            component = c;
            ImageIcon sm = new ImageIcon(getClass().getResource("save16.png"));
            ImageIcon lge = new ImageIcon(getClass().getResource("save32.png"));
            putValue(NAME, "Update Rule");
            putValue(SHORT_DESCRIPTION, "Update Rule");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_U));
        }

        public void jobUpdate(JobEvent event) {

            if (event.getType() == JobEvent.COMPLETE) {

                System.out.println("rule updated!");
            }
        }

        public void actionPerformed(ActionEvent e) {

            NMS n = getNMS();
            RecordingRulePanel rrp = getRecordingRulePanel();
            if ((n != null) && (rrp != null)) {

                RecordingRule rr = rrp.getRecordingRule();
                if (rr != null) {

                    AddRuleJob arj = new AddRuleJob(n, rr);
                    ProgressBar pbar =
                        new ProgressBar(component, "Update Rule...", arj);
                    pbar.addJobListener(this);
                    pbar.execute();
                }
            }
        }
    }

}

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
package org.jflicks.ui.view.aspirin;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.ui.view.aspirin.analyze.Analyze;
import org.jflicks.ui.view.aspirin.analyze.Finding;
import org.jflicks.ui.view.aspirin.analyze.Fix;
import org.jflicks.util.MessagePanel;

/**
 * Main panel that allows the user to run Analyze instances against a
 * jflicks installation.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ExecutePanel extends JPanel implements ActionListener,
    ListSelectionListener, JobListener {

    private Analyze[] analyzes;
    private JList findingList;
    private FindingPanel findingPanel;
    private MessagePanel messagePanel;
    private JButton fixButton;
    private JButton cancelButton;
    private ExecuteJob executeJob;
    private JobContainer executeJobContainer;
    private Fix fix;
    private JobContainer fixJobContainer;

    /**
     * Simple constructor.
     */
    public ExecutePanel() {

        JList flist = new JList();
        flist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flist.addListSelectionListener(this);
        setFindingList(flist);

        JScrollPane flistScroller = new JScrollPane(flist,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder(
            "Analyzers Executed"));
        listPanel.add(flistScroller, BorderLayout.CENTER);

        FindingPanel fp = new FindingPanel();
        setFindingPanel(fp);

        MessagePanel mp = new MessagePanel("Log");
        setMessagePanel(mp);

        JButton fixb = new JButton("Fix");
        fixb.addActionListener(this);
        fixb.setEnabled(false);
        setFixButton(fixb);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        setCancelButton(cancel);

        JPanel bpanel = new JPanel();
        bpanel.add(fixb);
        bpanel.add(cancel);

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(listPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(fp, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(mp, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(bpanel, gbc);
    }

    /**
     * We keep track of the Analyze instances we are to execute.
     *
     * @return An array of Analyze instances.
     */
    public Analyze[] getAnalyzes() {

        Analyze[] result = null;

        if (analyzes != null) {

            result = Arrays.copyOf(analyzes, analyzes.length);
        }

        return (result);
    }

    /**
     * We keep track of the Analyze instances we are to execute.
     *
     * @param array An array of Analyze instances.
     */
    public void setAnalyzes(Analyze[] array) {

        if (array != null) {
            analyzes = Arrays.copyOf(array, array.length);
        } else {
            analyzes = null;
        }
    }

    private JList getFindingList() {
        return (findingList);
    }

    private void setFindingList(JList l) {
        findingList = l;
    }

    private FindingPanel getFindingPanel() {
        return (findingPanel);
    }

    private void setFindingPanel(FindingPanel p) {
        findingPanel = p;
    }

    private MessagePanel getMessagePanel() {
        return (messagePanel);
    }

    private void setMessagePanel(MessagePanel mp) {
        messagePanel = mp;
    }

    private JButton getFixButton() {
        return (fixButton);
    }

    private void setFixButton(JButton b) {
        fixButton = b;
    }

    private JButton getCancelButton() {
        return (cancelButton);
    }

    private void setCancelButton(JButton b) {
        cancelButton = b;
    }

    private ExecuteJob getExecuteJob() {
        return (executeJob);
    }

    private void setExecuteJob(ExecuteJob j) {
        executeJob = j;
    }

    private JobContainer getExecuteJobContainer() {
        return (executeJobContainer);
    }

    private void setExecuteJobContainer(JobContainer jc) {
        executeJobContainer = jc;
    }

    private Fix getFix() {
        return (fix);
    }

    private void setFix(Fix f) {
        fix = f;
    }

    private JobContainer getFixJobContainer() {
        return (fixJobContainer);
    }

    private void setFixJobContainer(JobContainer jc) {
        fixJobContainer = jc;
    }

    /**
     * Convenience method to execute all the Analyze instances.
     */
    public void execute() {

        Analyze[] array = getAnalyzes();
        if (array != null) {

            JButton b = getCancelButton();
            if (b != null) {

                b.setEnabled(true);
            }
            ExecuteJob job = new ExecuteJob(array);
            setExecuteJob(job);
            job.addJobListener(this);
            JobContainer jc = JobManager.getJobContainer(job);
            setExecuteJobContainer(jc);
            jc.start();
        }
    }

    private void fixAction() {

        FindingPanel p = getFindingPanel();
        if (p != null) {

            Finding f = p.getFinding();
            if (f != null) {

                Fix myfix = f.getFix();
                if (myfix != null) {

                    getFixButton().setEnabled(false);
                    getCancelButton().setEnabled(true);
                    myfix.addJobListener(this);
                    setFix(myfix);
                    JobContainer jc = JobManager.getJobContainer(myfix);
                    setFixJobContainer(jc);
                    jc.start();
                }
            }
        }
    }

    private void cancelAction() {

        JobContainer jc = getExecuteJobContainer();
        if (jc != null) {

            jc.stop();
            setExecuteJobContainer(null);
        }

        jc = getFixJobContainer();
        if (jc != null) {

            jc.stop();
            setFixJobContainer(null);
        }
    }

    /**
     * Allow the user to cancel the checks.
     *
     * @param event An event from the cancel button.
     */
    public void actionPerformed(ActionEvent event) {

        if (event.getSource() == getFixButton()) {
            fixAction();
        } else if (event.getSource() == getCancelButton()) {
            cancelAction();
        }
    }

    /**
     * We listen for clicks on the JList so we can update the UI if
     * necessary.
     *
     * @param event A given ListSelectionEvent instance.
     */
    public void valueChanged(ListSelectionEvent event) {

        if (!event.getValueIsAdjusting()) {

            if (event.getSource() == getFindingList()) {

                FindingPanel fp = getFindingPanel();
                if (fp != null) {

                    JList l = getFindingList();
                    int index = l.getSelectedIndex();
                    if (index != -1) {

                        Finding f = (Finding) l.getSelectedValue();
                        fp.setFinding(f);
                        getFixButton().setEnabled(f.getFix() != null);

                    } else {

                        fp.setFinding(null);
                        getFixButton().setEnabled(false);
                    }
                }
            }
        }
    }

    /**
     * We listen for job updates from running Analyze instances.
     *
     * @param event The given job event instance.
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            if (event.getSource() == getExecuteJob()) {

                setExecuteJob(null);
                setExecuteJobContainer(null);
                Serializable state = event.getState();
                if (state instanceof Finding[]) {

                    Finding[] array = (Finding[]) state;
                    JList l = getFindingList();
                    if (l != null) {

                        l.setListData(array);
                    }
                }

                JButton b = getCancelButton();
                if (b != null) {
                    b.setEnabled(false);
                }

            } else if (event.getSource() == getFix()) {

                setFix(null);
                setFixJobContainer(null);
                JButton b = getCancelButton();
                if (b != null) {
                    b.setEnabled(false);
                }
                b = getFixButton();
                if (b != null) {

                    FindingPanel fp = getFindingPanel();
                    if (fp != null) {

                        Finding f = fp.getFinding();
                        if (f != null) {
                            b.setEnabled(f.getFix() != null);
                        } else {
                            b.setEnabled(false);
                        }

                    } else {
                        b.setEnabled(false);
                    }
                }
            }

        } else if (event.getType() == JobEvent.UPDATE) {

            MessagePanel mp = getMessagePanel();
            if (mp != null) {

                mp.addMessage(event.getMessage());
            }
        }
    }

}

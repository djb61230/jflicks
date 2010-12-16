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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jflicks.ui.view.aspirin.analyze.Analyze;
import org.jflicks.ui.view.aspirin.analyze.Finding;

/**
 * Main panel that allows the user to run Analyze instances against a
 * jflicks installation.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ExecutePanel extends JPanel implements ActionListener,
    ListSelectionListener {

    private Analyze[] analyzes;
    private JList findingList;
    private FindingPanel findingPanel;
    private JButton cancelButton;

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

        FindingPanel fp = new FindingPanel();
        setFindingPanel(fp);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        setCancelButton(cancel);

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(flistScroller, gbc);

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
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(cancel, gbc);
    }

    public Analyze[] getAnalyzes() {
        return (analyzes);
    }

    public void setAnalyzes(Analyze[] array) {
        analyzes = array;
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

    private JButton getCancelButton() {
        return (cancelButton);
    }

    private void setCancelButton(JButton b) {
        cancelButton = b;
    }

    private void cancelAction() {
    }

    public void actionPerformed(ActionEvent event) {

        if (event.getSource() == getCancelButton()) {
            cancelAction();
        }
    }

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

                    } else {

                        fp.setFinding(null);
                    }
                }
            }
        }
    }

}

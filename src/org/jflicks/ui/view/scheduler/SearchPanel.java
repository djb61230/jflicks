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
import java.awt.event.ActionListener;
import java.io.Serializable;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.util.ProgressBar;
import org.jflicks.tv.ShowAiring;

/**
 * A implements a View so a user can control the scheduling of
 * recordings.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SearchPanel extends BaseSchedulePanel
    implements ActionListener, ListSelectionListener, JobListener {

    private JTextField titleTextField;
    private JButton searchButton;
    private JRadioButton titleRadioButton;
    private JRadioButton descriptionRadioButton;
    private JRadioButton bothRadioButton;
    private JList showList;

    /**
     * Default constructor.
     */
    public SearchPanel() {

        JTextField tf = new JTextField(14);
        tf.addActionListener(this);
        setTitleTextField(tf);

        JButton sbutton = new JButton("Search");
        sbutton.addActionListener(this);
        setSearchButton(sbutton);

        JRadioButton titleButton = new JRadioButton("Title Only");
        titleButton.setSelected(true);
        setTitleRadioButton(titleButton);

        JRadioButton descriptionButton = new JRadioButton("Description Only");
        descriptionButton.setSelected(false);
        setDescriptionRadioButton(descriptionButton);

        JRadioButton bothButton =
            new JRadioButton("Both Title and Description");
        bothButton.setSelected(false);
        setBothRadioButton(bothButton);

        ButtonGroup group = new ButtonGroup();
        group.add(titleButton);
        group.add(descriptionButton);
        group.add(bothButton);

        JList l = new JList();
        l.setPrototypeCellValue("01234567890123456789");
        l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        l.setVisibleRowCount(8);
        l.addListSelectionListener(this);
        setShowList(l);
        JScrollPane showlistScroller = new JScrollPane(l,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel showPanel = new JPanel(new BorderLayout());
        showPanel.add(BorderLayout.CENTER, showlistScroller);
        showPanel.setBorder(BorderFactory.createTitledBorder("Programs"));

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(tf, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(sbutton, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(titleButton, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(descriptionButton, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(bothButton, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.gridheight = 4;
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(showPanel, gbc);
    }

    private JTextField getTitleTextField() {
        return (titleTextField);
    }

    private void setTitleTextField(JTextField tf) {
        titleTextField = tf;
    }

    private JButton getSearchButton() {
        return (searchButton);
    }

    private void setSearchButton(JButton b) {
        searchButton = b;
    }

    private JRadioButton getTitleRadioButton() {
        return (titleRadioButton);
    }

    private void setTitleRadioButton(JRadioButton b) {
        titleRadioButton = b;
    }

    private JRadioButton getDescriptionRadioButton() {
        return (descriptionRadioButton);
    }

    private void setDescriptionRadioButton(JRadioButton b) {
        descriptionRadioButton = b;
    }

    private JRadioButton getBothRadioButton() {
        return (bothRadioButton);
    }

    private void setBothRadioButton(JRadioButton b) {
        bothRadioButton = b;
    }

    private JList getShowList() {
        return (showList);
    }

    private void setShowList(JList l) {
        showList = l;
    }

    private int getSearchType() {

        int result = NMSConstants.SEARCH_TITLE;

        JRadioButton desc = getDescriptionRadioButton();
        JRadioButton both = getBothRadioButton();
        if ((desc != null) && (both != null)) {

            if (desc.isSelected()) {
                result = NMSConstants.SEARCH_DESCRIPTION;
            } else if (both.isSelected()) {
                result = NMSConstants.SEARCH_TITLE_DESCRIPTION;
            }
        }

        return (result);
    }

    private void searchAction() {

        JTextField tf = getTitleTextField();
        String tmp = tf.getText().trim();
        if (tmp.length() > 0) {

            NMS n = getNMS();
            if (n != null) {

                ShowAiringSearchJob job =
                    new ShowAiringSearchJob(n, tmp, getSearchType());
                ProgressBar pbar = new ProgressBar(this, "Searching...", job);
                pbar.addJobListener(this);
                pbar.execute();
            }
        }
    }

    /**
     * We listen for enter key on the text field.
     *
     * @param event The given action event.
     */
    public void actionPerformed(ActionEvent event) {

        if (event.getSource() == getTitleTextField()) {
            searchAction();
        } else if (event.getSource() == getSearchButton()) {
            searchAction();
        }
    }

    /**
     * We listen for selection on the movie list box.
     *
     * @param event The given list selection event.
     */
    public void valueChanged(ListSelectionEvent event) {

        if (!event.getValueIsAdjusting()) {

            if (event.getSource() == getShowList()) {

                JList l = getShowList();
                int index = l.getSelectedIndex();
                if (index != -1) {

                    setShowAiring((ShowAiring) l.getSelectedValue());

                } else {

                    setShowAiring(null);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void nmsAction() {

        /*
        NMS n = getNMS();
        if (n != null) {

            Channel[] chans = n.getRecordableChannels();
            if (chans != null) {

                JList list = getChannelList();
                if (list != null) {

                    list.setListData(chans);
                }
            }
        }
        */
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            Serializable s = event.getState();
            if (s instanceof ShowAiring[]) {

                ShowAiring[] sas = (ShowAiring[]) s;
                JList slist = getShowList();
                if (slist != null) {
                    slist.setListData(sas);
                }
            }
        }
    }

}

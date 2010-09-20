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
import java.io.Serializable;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.nms.NMS;
import org.jflicks.util.ProgressBar;
import org.jflicks.tv.Channel;
import org.jflicks.tv.ShowAiring;

/**
 * A implements a View so a user can control the scheduling of
 * recordings.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ChannelGuidePanel extends BaseSchedulePanel
    implements ListSelectionListener, JobListener {

    private JList channelList;
    private JList showList;

    /**
     * Default constructor.
     */
    public ChannelGuidePanel() {

        JList l = new JList();
        l.setPrototypeCellValue("01234567890123456789");
        l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        l.setVisibleRowCount(8);
        l.addListSelectionListener(this);
        setChannelList(l);
        JScrollPane channellistScroller = new JScrollPane(l,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        l = new JList();
        l.setPrototypeCellValue("01234567890123456789");
        l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        l.setVisibleRowCount(8);
        l.addListSelectionListener(this);
        setShowList(l);
        JScrollPane showlistScroller = new JScrollPane(l,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel chanPanel = new JPanel(new BorderLayout());
        chanPanel.add(BorderLayout.CENTER, channellistScroller);
        chanPanel.setBorder(BorderFactory.createTitledBorder("Channels"));

        JPanel showPanel = new JPanel(new BorderLayout());
        showPanel.add(BorderLayout.CENTER, showlistScroller);
        showPanel.setBorder(BorderFactory.createTitledBorder("Programs"));

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(chanPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(showPanel, gbc);
    }

    private JList getChannelList() {
        return (channelList);
    }

    private void setChannelList(JList l) {
        channelList = l;
    }

    private JList getShowList() {
        return (showList);
    }

    private void setShowList(JList l) {
        showList = l;
    }

    /**
     * Convenience method to get the currently selected Channel.
     *
     * @return A Channel instance if one is selected.
     */
    public Channel getSelectedChannel() {

        Channel result = null;

        JList l = getChannelList();
        if (l != null) {

            result = (Channel) l.getSelectedValue();
        }

        return (result);
    }

    /**
     * Convenience method to get a channel by it's Id.
     *
     * @param id A given Id.
     * @return A Channel instance if it exists.
     */
    public Channel getChannelById(int id) {

        Channel result = null;

        JList l = getChannelList();
        if (l != null) {

            ListModel lm = l.getModel();
            if (lm != null) {

                int count = lm.getSize();
                for (int i = 0; i < count; i++) {

                    Channel c = (Channel) lm.getElementAt(i);
                    if (c.getId() == id) {

                        result = c;
                        break;
                    }
                }
            }
        }

        return (result);
    }

    /**
     * We listen for selection on the movie list box.
     *
     * @param event The given list selection event.
     */
    public void valueChanged(ListSelectionEvent event) {

        if (!event.getValueIsAdjusting()) {

            if (event.getSource() == getChannelList()) {

                JList l = getChannelList();
                int index = l.getSelectedIndex();
                if (index != -1) {

                    Channel c = (Channel) l.getSelectedValue();
                    NMS n = getNMS();
                    if ((c != null) && (n != null)) {

                        ShowAiringJob job = new ShowAiringJob(n, c);
                        ProgressBar pbar =
                            new ProgressBar(this, "Searching...", job);
                        pbar.addJobListener(this);
                        pbar.execute();
                    }
                }

            } else if (event.getSource() == getShowList()) {

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

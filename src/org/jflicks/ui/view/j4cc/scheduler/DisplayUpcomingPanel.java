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
package org.jflicks.ui.view.j4cc.scheduler;

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

import org.jflicks.tv.Upcoming;

/**
 * A class that implements a UI to display the upcoming recordings.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DisplayUpcomingPanel extends JPanel
    implements ListSelectionListener  {

    private JList upcomingList;
    private UpcomingPanel upcomingPanel;
    private Upcoming[] upcomings;

    /**
     * Empty constructor.
     */
    public DisplayUpcomingPanel() {

        JList l = new JList();
        l.setPrototypeCellValue("012345678901234567890123456789");
        l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        l.setVisibleRowCount(10);
        l.addListSelectionListener(this);
        setUpcomingList(l);
        JScrollPane scroller = new JScrollPane(l,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        UpcomingPanel up = new UpcomingPanel();
        setUpcomingPanel(up);

        setLayout(new GridBagLayout());
        JPanel recPanel = new JPanel(new BorderLayout());
        recPanel.add(BorderLayout.CENTER, scroller);
        recPanel.setBorder(
            BorderFactory.createTitledBorder("Upcoming Recordings"));

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

        add(up, gbc);
    }

    /**
     * This UI displays an array of Upcoming instances.
     *
     * @return An array of Upcoming objects.
     */
    public Upcoming[] getUpcomings() {

        Upcoming[] result = null;

        if (upcomings != null) {

            result = Arrays.copyOf(upcomings, upcomings.length);
        }

        return (result);
    }

    /**
     * This UI displays an array of Upcoming instances.
     *
     * @param array An array of Upcoming objects.
     */
    public void setUpcomings(Upcoming[] array) {

        if (array != null) {
            upcomings = Arrays.copyOf(array, array.length);
        } else {
            upcomings = null;
        }

        JList list = getUpcomingList();
        if (list != null) {

            list.setListData(upcomings);
        }

        UpcomingPanel up = getUpcomingPanel();
        if (up != null) {

            up.setUpcoming(null);
        }
    }

    private JList getUpcomingList() {
        return (upcomingList);
    }

    private void setUpcomingList(JList l) {
        upcomingList = l;
    }

    private UpcomingPanel getUpcomingPanel() {
        return (upcomingPanel);
    }

    private void setUpcomingPanel(UpcomingPanel p) {
        upcomingPanel = p;
    }

    /**
     * We listen for selection on the rule list box.
     *
     * @param event The given list selection event.
     */
    public void valueChanged(ListSelectionEvent event) {

        if (!event.getValueIsAdjusting()) {

            if (event.getSource() == getUpcomingList()) {

                JList l = getUpcomingList();
                int index = l.getSelectedIndex();
                if (index != -1) {

                    Upcoming rr = (Upcoming) l.getSelectedValue();
                    UpcomingPanel rrp = getUpcomingPanel();
                    if ((rrp != null) && (rr != null)) {

                        rrp.setUpcoming(rr);
                    }
                }
            }
        }
    }

}

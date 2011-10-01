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
package org.jflicks.ui.view.ez;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import org.jflicks.tv.Channel;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class EZChannelSelect extends JXPanel implements ListSelectionListener {

    private ArrayList<Channel> leftChannelList;
    private ArrayList<Channel> rightChannelList;
    private JList leftList;
    private JList rightList;
    private JButton moveLeftButton;
    private JButton moveRightButton;

    private EZChannelSelect() {
    }

    public EZChannelSelect(Channel[] left, Channel[] right) {

        ArrayList<Channel> llist = new ArrayList<Channel>();
        if (left != null) {

            for (int i = 0; i < left.length; i++) {

                llist.add(left[i]);
            }
        }

        setLeftChannelList(llist);

        ArrayList<Channel> rlist = new ArrayList<Channel>();
        if (right != null) {

            for (int i = 0; i < right.length; i++) {

                rlist.add(right[i]);
            }
        }

        setRightChannelList(rlist);

        performLayout();
    }

    private ArrayList<Channel> getLeftChannelList() {
        return (leftChannelList);
    }

    private void setLeftChannelList(ArrayList<Channel> l) {
        leftChannelList = l;
    }

    public Channel[] getLeftChannels() {

        Channel[] result = null;

        ArrayList<Channel> l = getLeftChannelList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Channel[l.size()]);
        }

        return (result);
    }

    private ArrayList<Channel> getRightChannelList() {
        return (rightChannelList);
    }

    private void setRightChannelList(ArrayList<Channel> l) {
        rightChannelList = l;
    }

    public Channel[] getRightChannels() {

        Channel[] result = null;

        ArrayList<Channel> l = getRightChannelList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Channel[l.size()]);
        }

        return (result);
    }

    private JList getLeftList() {
        return (leftList);
    }

    private void setLeftList(JList l) {
        leftList = l;
    }

    private JList getRightList() {
        return (rightList);
    }

    private void setRightList(JList l) {
        rightList = l;
    }

    private JButton getMoveLeftButton() {
        return (moveLeftButton);
    }

    private void setMoveLeftButton(JButton b) {
        moveLeftButton = b;
    }

    private JButton getMoveRightButton() {
        return (moveRightButton);
    }

    private void setMoveRightButton(JButton b) {
        moveRightButton = b;
    }

    private void performLayout() {

        setLayout(new GridBagLayout());

        JList llist = new JList();
        llist.addListSelectionListener(this);
        llist.setPrototypeCellValue("01234567890123456789");
        llist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setLeftList(llist);
        llist.setToolTipText("<html>"
            + "The Channels you are interested in."
            + "</html>");
        JScrollPane leftScroller = new JScrollPane(llist,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JXPanel leftScrollerPanel = new JXPanel(new BorderLayout(4, 4));
        leftScrollerPanel.add(leftScroller, BorderLayout.CENTER);
        leftScrollerPanel.setBorder(BorderFactory.createTitledBorder(
            "Selected Channel(s)"));

        JList rlist = new JList();
        rlist.addListSelectionListener(this);
        rlist.setPrototypeCellValue("01234567890123456789");
        rlist.setToolTipText("<html>"
            + "The Channels you are NOT interested in."
            + "</html>");
        setRightList(rlist);
        rlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane rightScroller = new JScrollPane(rlist,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JXPanel rightScrollerPanel = new JXPanel(new BorderLayout(4, 4));
        rightScrollerPanel.add(rightScroller, BorderLayout.CENTER);
        rightScrollerPanel.setBorder(BorderFactory.createTitledBorder(
            "Unselected Channel(s)"));

        MoveLeftAction moveLeftAction = new MoveLeftAction();
        JButton mlbutton = new JButton(moveLeftAction);
        setMoveLeftButton(mlbutton);

        MoveRightAction moveRightAction = new MoveRightAction();
        JButton mrbutton = new JButton(moveRightAction);
        setMoveRightButton(mrbutton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(leftScrollerPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(mlbutton, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(mrbutton, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(rightScrollerPanel, gbc);

        updateLists();
        updateState();
    }

    private void updateLists() {

        ArrayList<Channel> lclist = getLeftChannelList();
        ArrayList<Channel> rclist = getRightChannelList();
        JList llist = getLeftList();
        JList rlist = getRightList();
        if ((lclist != null) && (rclist != null) && (llist != null)
            && (rlist != null)) {

            llist.setListData(lclist.toArray(new Channel[lclist.size()]));
            rlist.setListData(rclist.toArray(new Channel[rclist.size()]));
        }
    }

    private void updateState() {

        JButton rb = getMoveRightButton();
        if (rb != null) {

            JList left = getLeftList();
            if ((left != null) && (left.getSelectedIndices() != null)) {

                int[] array = left.getSelectedIndices();
                if ((array != null) && (array.length > 0)) {
                    rb.setEnabled(true);
                } else {
                    rb.setEnabled(false);
                }

            } else {

                rb.setEnabled(false);
            }
        }

        JButton lb = getMoveLeftButton();
        if (lb != null) {

            JList right = getRightList();
            if ((right != null) && (right.getSelectedIndices() != null)) {

                int[] array = right.getSelectedIndices();
                if ((array != null) && (array.length > 0)) {
                    lb.setEnabled(true);
                } else {
                    lb.setEnabled(false);
                }

            } else {

                lb.setEnabled(false);
            }
        }
    }

    private void reconcileLeftToRight(int[] array) {

        ArrayList<Channel> lclist = getLeftChannelList();
        ArrayList<Channel> rclist = getRightChannelList();

        if ((lclist != null) && (rclist != null) && (array != null)
            && (array.length > 0)) {

            ArrayList<Channel> tomove = new ArrayList<Channel>();
            for (int i = 0; i < array.length; i++) {

                tomove.add(lclist.get(array[i]));
            }

            // Now add to right list.
            for (int i = 0; i < tomove.size(); i++) {

                rclist.add(tomove.get(i));
            }
            Collections.sort(rclist);

            // Now remove from left list.
            for (int i = 0; i < tomove.size(); i++) {

                lclist.remove(tomove.get(i));
            }
            Collections.sort(lclist);
        }
    }

    private void reconcileRightToLeft(int[] array) {

        ArrayList<Channel> lclist = getLeftChannelList();
        ArrayList<Channel> rclist = getRightChannelList();

        if ((lclist != null) && (rclist != null) && (array != null)
            && (array.length > 0)) {

            ArrayList<Channel> tomove = new ArrayList<Channel>();
            for (int i = 0; i < array.length; i++) {

                tomove.add(rclist.get(array[i]));
            }

            // Now add to left list.
            for (int i = 0; i < tomove.size(); i++) {

                lclist.add(tomove.get(i));
            }
            Collections.sort(lclist);

            // Now remove from right list.
            for (int i = 0; i < tomove.size(); i++) {

                rclist.remove(tomove.get(i));
            }
            Collections.sort(rclist);
        }
    }

    /**
     * We listen for selection on the recorder list box.
     *
     * @param event The given list selection event.
     */
    public void valueChanged(ListSelectionEvent event) {

        if (!event.getValueIsAdjusting()) {

            updateState();
        }
    }

    class MoveLeftAction extends AbstractAction {

        public MoveLeftAction() {

            putValue(NAME, "<-");
        }

        public void actionPerformed(ActionEvent e) {

            JList rlist = getRightList();
            if (rlist != null) {

                reconcileRightToLeft(rlist.getSelectedIndices());
                updateLists();
            }
        }

    }

    class MoveRightAction extends AbstractAction {

        public MoveRightAction() {

            putValue(NAME, "->");
        }

        public void actionPerformed(ActionEvent e) {

            JList llist = getLeftList();
            if (llist != null) {

                reconcileLeftToRight(llist.getSelectedIndices());
                updateLists();
            }
        }

    }

}

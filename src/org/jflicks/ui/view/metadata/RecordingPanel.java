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
package org.jflicks.ui.view.metadata;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.metadata.Hit;
import org.jflicks.metadata.SearchEvent;
import org.jflicks.metadata.SearchListener;
import org.jflicks.nms.NMS;
import org.jflicks.tv.Recording;
import org.jflicks.util.ProgressBar;
import org.jflicks.util.Util;

/**
 * Implements a View so a user can control the metadata of videos.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordingPanel extends BasePanel implements ListSelectionListener,
    SearchListener {

    private NMS nms;
    private JList recordingDisplayList;
    private RecordingDisplayPanel recordingDisplayPanel;
    private JButton viewImagesButton;
    private JButton saveButton;
    private ViewImagesAction viewImagesAction;
    private SaveAction saveAction;
    private ArrayList<Recording> recordingList;

    /**
     * Default constructor.
     */
    public RecordingPanel() {

        setRecordingList(new ArrayList<Recording>());

        JList l = new JList();
        l.setPrototypeCellValue("0123456789012345678901234567890123456789");
        l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        l.setVisibleRowCount(22);
        l.addListSelectionListener(this);
        setRecordingDisplayList(l);
        JScrollPane videolistScroller = new JScrollPane(l,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        ViewImagesAction view = new ViewImagesAction();
        view.setEnabled(false);
        setViewImagesAction(view);
        JButton viewb = new JButton(view);
        setViewImagesButton(viewb);

        SaveAction save = new SaveAction();
        save.setEnabled(false);
        setSaveAction(save);
        JButton saveb = new JButton(save);
        setSaveButton(saveb);

        RecordingDisplayPanel rdp = new RecordingDisplayPanel();
        setRecordingDisplayPanel(rdp);

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(videolistScroller, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(viewb, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(saveb, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(rdp, gbc);
    }

    private ArrayList<Recording> getRecordingList() {
        return (recordingList);
    }

    private void setRecordingList(ArrayList<Recording> l) {
        recordingList = l;
    }

    private void addRecording(Recording r) {

         ArrayList<Recording> l = getRecordingList();
         if ((l != null) && (r != null)) {

             if (l.contains(r)) {

                 l.remove(r);
                 l.add(r);

             } else {

                 l.add(r);
             }

             getSaveAction().setEnabled(true);
         }
    }

    /**
     * A NMS instance is needed to access data.
     *
     * @return A NMS instance.
     */
    public NMS getNMS() {
        return (nms);
    }

    /**
     * A NMS instance is needed to access data.  We notify extensions that
     * this property has been updated.
     *
     * @param n A NMS instance.
     */
    public void setNMS(NMS n) {

        nms = n;
        nmsAction();
    }

    private JList getRecordingDisplayList() {
        return (recordingDisplayList);
    }

    private void setRecordingDisplayList(JList l) {
        recordingDisplayList = l;
    }

    private RecordingDisplayPanel getRecordingDisplayPanel() {
        return (recordingDisplayPanel);
    }

    private void setRecordingDisplayPanel(RecordingDisplayPanel p) {
        recordingDisplayPanel = p;
    }

    private JButton getViewImagesButton() {
        return (viewImagesButton);
    }

    private void setViewImagesButton(JButton b) {
        viewImagesButton = b;
    }

    private JButton getSaveButton() {
        return (saveButton);
    }

    private void setSaveButton(JButton b) {
        saveButton = b;
    }

    private ViewImagesAction getViewImagesAction() {
        return (viewImagesAction);
    }

    private void setViewImagesAction(ViewImagesAction a) {
        viewImagesAction = a;
    }

    private SaveAction getSaveAction() {
        return (saveAction);
    }

    private void setSaveAction(SaveAction a) {
        saveAction = a;
    }

    private Frame getFrame() {
        return (Util.findFrame(this));
    }

    private JPanel getPanel() {
        return (this);
    }

    /**
     * Convenience method to get the currently selected Recording.
     *
     * @return A Recording instance if one is selected.
     */
    public Recording getSelectedRecording() {

        Recording result = null;

        JList l = getRecordingDisplayList();
        if (l != null) {

            result = (Recording) l.getSelectedValue();
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void searchUpdate(SearchEvent event) {

        if (event.getType() == SearchEvent.UPDATE) {

            if (isVisible()) {

                // Extract and update.
                Hit h = event.getHit();
                Recording r = getSelectedRecording();
                if ((r != null) && (h != null)) {

                    r.setBannerURL(h.getBannerURL());
                    r.setPosterURL(h.getPosterURL());
                    r.setFanartURL(h.getFanartURL());

                    addRecording(r);
                }
            }
        }
    }

    /**
     * We listen for selection on the recording list box.
     *
     * @param event The given list selection event.
     */
    public void valueChanged(ListSelectionEvent event) {

        if (!event.getValueIsAdjusting()) {

            if (event.getSource() == getRecordingDisplayList()) {

                JList l = getRecordingDisplayList();
                int index = l.getSelectedIndex();
                if (index != -1) {

                    Recording r = (Recording) l.getSelectedValue();
                    RecordingDisplayPanel p = getRecordingDisplayPanel();
                    if ((r != null) && (p != null)) {

                        p.setRecording(r);
                        getViewImagesAction().setEnabled(true);

                        fireSearchEvent(SearchEvent.SEARCH_TV, r.getTitle());
                    }
                }
            }
        }
    }

    private void nmsAction() {

        NMS n = getNMS();
        if (n != null) {

            Recording[] recs = n.getRecordings();
            if (recs != null) {

                JList list = getRecordingDisplayList();
                if (list != null) {

                    Arrays.sort(recs);
                    list.setListData(recs);
                }
            }
        }
    }

    class ViewImagesAction extends AbstractAction {

        public ViewImagesAction() {

            ImageIcon sm = new ImageIcon(getClass().getResource("view16.png"));
            ImageIcon lge = new ImageIcon(getClass().getResource("view32.png"));
            putValue(NAME, "View Images");
            putValue(SHORT_DESCRIPTION, "View Images");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_V));
        }

        public void actionPerformed(ActionEvent event) {

            Recording r = getSelectedRecording();
            if (r != null) {

                JScrollPane scroll = new JScrollPane(new ImagePanel(r),
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                JPanel p = new JPanel(new BorderLayout());
                p.add(scroll, BorderLayout.CENTER);
                p.setPreferredSize(new java.awt.Dimension(800, 600));
                Util.showDoneDialog(getFrame(), "Images", p);
            }
        }

    }

    class SaveAction extends AbstractAction implements JobListener {

        public SaveAction() {

            ImageIcon sm = new ImageIcon(getClass().getResource("save16.png"));
            ImageIcon lge = new ImageIcon(getClass().getResource("save32.png"));
            putValue(NAME, "Save");
            putValue(SHORT_DESCRIPTION, "Save");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_S));
        }

        public void jobUpdate(JobEvent event) {

            if (event.getType() == JobEvent.COMPLETE) {

                getSaveAction().setEnabled(false);
            }
        }

        public void actionPerformed(ActionEvent event) {

            // Time to save off all the changes the user has made...
            NMS n = getNMS();
            ArrayList<Recording> l = getRecordingList();
            if ((n != null) && (l != null) && (l.size() > 0)) {

                Recording[] array = l.toArray(new Recording[l.size()]);
                SaveRecordingJob srj = new SaveRecordingJob(n, array);
                ProgressBar pbar =
                    new ProgressBar(getPanel(), "Saving...", srj);
                pbar.addJobListener(this);
                pbar.execute();
                l.clear();
            }
        }

    }

}

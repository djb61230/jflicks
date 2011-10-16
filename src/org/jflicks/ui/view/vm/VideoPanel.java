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
package org.jflicks.ui.view.vm;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.nms.NMS;
import org.jflicks.nms.Video;
import org.jflicks.util.ProgressBar;
import org.jflicks.util.Util;

/**
 * Implements a View so a user can control the metadata of videos.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class VideoPanel extends BasePanel implements ListSelectionListener {

    private NMS nms;
    private VideoManagerView videoManagerView;
    private JList videoDisplayList;
    private VideoDetailPanel videoDetailPanel;
    private RefreshAction refreshAction;
    private EditAction editAction;
    private GenerateAction generateAction;
    private ArrayList<Video> videoList;

    /**
     * Constructor with one argument.
     *
     * @param v The View controlling this panel.
     */
    public VideoPanel(VideoManagerView v) {

        setVideoList(new ArrayList<Video>());
        setVideoManagerView(v);

        JList l = new JList();
        l.setPrototypeCellValue("0123456789012345678901234567890123456789");
        l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        l.setVisibleRowCount(20);
        l.addListSelectionListener(this);
        setVideoDisplayList(l);
        JScrollPane videolistScroller = new JScrollPane(l,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        setVideoDetailPanel(new VideoDetailPanel());

        RefreshAction ra = new RefreshAction();
        setRefreshAction(ra);
        JButton refreshb = new JButton(ra);

        GenerateAction ga = new GenerateAction();
        ga.setEnabled(false);
        setGenerateAction(ga);
        JButton genb = new JButton(ga);

        EditAction ea = new EditAction();
        ea.setEnabled(false);
        setEditAction(ea);
        JButton editb = new JButton(ea);

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(videolistScroller, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getVideoDetailPanel(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(refreshb, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(genb, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(editb, gbc);
    }

    private ArrayList<Video> getVideoList() {
        return (videoList);
    }

    private void setVideoList(ArrayList<Video> l) {
        videoList = l;
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

    private VideoManagerView getVideoManagerView() {
        return (videoManagerView);
    }

    private void setVideoManagerView(VideoManagerView v) {
        videoManagerView = v;
    }

    private JList getVideoDisplayList() {
        return (videoDisplayList);
    }

    private void setVideoDisplayList(JList l) {
        videoDisplayList = l;
    }

    private VideoDetailPanel getVideoDetailPanel() {
        return (videoDetailPanel);
    }

    private void setVideoDetailPanel(VideoDetailPanel p) {
        videoDetailPanel = p;
    }

    private RefreshAction getRefreshAction() {
        return (refreshAction);
    }

    private void setRefreshAction(RefreshAction a) {
        refreshAction = a;
    }

    private GenerateAction getGenerateAction() {
        return (generateAction);
    }

    private void setGenerateAction(GenerateAction a) {
        generateAction = a;
    }

    private EditAction getEditAction() {
        return (editAction);
    }

    private void setEditAction(EditAction a) {
        editAction = a;
    }

    private Frame getFrame() {
        return (Util.findFrame(this));
    }

    private JPanel getPanel() {
        return (this);
    }

    /**
     * Convenience method to get the currently selected Video.
     *
     * @return A Video instance if one is selected.
     */
    public Video getSelectedVideo() {

        Video result = null;

        JList l = getVideoDisplayList();
        if (l != null) {

            result = (Video) l.getSelectedValue();
        }

        return (result);
    }

    /**
     * We listen for selection on the video list box.
     *
     * @param event The given list selection event.
     */
    public void valueChanged(ListSelectionEvent event) {

        if (!event.getValueIsAdjusting()) {

            if (event.getSource() == getVideoDisplayList()) {

                VideoDetailPanel p = getVideoDetailPanel();
                if (p != null) {

                    JList l = getVideoDisplayList();
                    int index = l.getSelectedIndex();
                    if (index != -1) {

                        Video v = (Video) l.getSelectedValue();
                        getGenerateAction().setEnabled(true);
                        getEditAction().setEnabled(true);
                        p.setVideo(v);

                    } else {

                        getGenerateAction().setEnabled(false);
                        getEditAction().setEnabled(false);
                        p.setVideo(null);
                    }
                }
            }
        }
    }

    private void nmsAction() {

        NMS n = getNMS();
        if (n != null) {

            Video[] vids = n.getVideos();
            if (vids != null) {

                JList list = getVideoDisplayList();
                if (list != null) {

                    Arrays.sort(vids);
                    list.setListData(vids);
                }
            }
        }
    }

    class RefreshAction extends AbstractAction {

        public RefreshAction() {

            ImageIcon sm =
                new ImageIcon(getClass().getResource("refresh16.png"));
            ImageIcon lge =
                new ImageIcon(getClass().getResource("refresh32.png"));
            putValue(NAME, "Refresh List");
            putValue(SHORT_DESCRIPTION, "Refresh List of Videos");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_R));
        }

        public void actionPerformed(ActionEvent event) {

            nmsAction();
        }

    }

    class GenerateAction extends AbstractAction implements JobListener {

        private Integer last = Integer.valueOf(60);
        private Integer[] choices;

        public GenerateAction() {

            ImageIcon sm = new ImageIcon(getClass().getResource("view16.png"));
            ImageIcon lge = new ImageIcon(getClass().getResource("view32.png"));
            putValue(NAME, "Generate Artwork");
            putValue(SHORT_DESCRIPTION, "Generate artwork from file");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_G));

            choices = new Integer[50];
            for (int i = 0; i < choices.length; i++) {

                choices[i] = Integer.valueOf((i + 1) * 10);
            }

        }

        public void jobUpdate(JobEvent event) {

            if (event.getType() == JobEvent.COMPLETE) {
                nmsAction();
            }
        }

        public void actionPerformed(ActionEvent event) {

            NMS n = getNMS();
            Video v = getSelectedVideo();
            if ((n != null) && (v != null)) {

                String message = "This will overwrite the current artwork."
                    + "  Select the number of seek seconds into the video.";
                String title = "Generate Artwork for \"" + v.getTitle() + "\"";
                Object result = JOptionPane.showInputDialog(getFrame(),
                    message, title, JOptionPane.QUESTION_MESSAGE,
                    null, choices, last);

                if (result != null) {

                    // The user wants to generate!!
                    last = (Integer) result;
                    GenerateArtworkJob gaj =
                        new GenerateArtworkJob(n, v,last.intValue());
                    ProgressBar pbar =
                        new ProgressBar(getPanel(), "Generating...", gaj);
                    pbar.addJobListener(this);
                    pbar.execute();
                }
            }
        }

    }

    class EditAction extends AbstractAction implements JobListener {

        public EditAction() {

            ImageIcon sm = new ImageIcon(getClass().getResource("movie16.png"));
            ImageIcon lge =
                new ImageIcon(getClass().getResource("movie32.png"));
            putValue(NAME, "Edit");
            putValue(SHORT_DESCRIPTION, "Edit Metadata");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_E));
        }

        public void jobUpdate(JobEvent event) {

            if (event.getType() == JobEvent.COMPLETE) {
                nmsAction();
            }
        }

        public void actionPerformed(ActionEvent event) {

            NMS n = getNMS();
            Video v = getSelectedVideo();
            VideoManagerView vmv = getVideoManagerView();
            if ((n != null) && (v != null) && (vmv != null)) {

                EditVideoPanel p =
                    new EditVideoPanel(n, new Video(v), vmv.getMetadata());

                if (Util.showDialog(getFrame(), "Edit", p)) {

                    // The user wants to save!!
                    SaveVideoJob svj = new SaveVideoJob(n, p.getVideo());
                    ProgressBar pbar =
                        new ProgressBar(getPanel(), "Saving...", svj);
                    pbar.addJobListener(this);
                    pbar.execute();
                }
            }
        }

    }

}

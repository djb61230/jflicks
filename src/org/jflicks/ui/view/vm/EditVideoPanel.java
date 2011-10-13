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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.SpinnerNumberModel;

import org.jflicks.metadata.Hit;
import org.jflicks.metadata.Metadata;
import org.jflicks.metadata.SearchEvent;
import org.jflicks.metadata.SearchListener;
import org.jflicks.metadata.SearchPanel;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.Video;
import org.jflicks.util.Util;

/**
 * Implements a View so a user can control the metadata of videos.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class EditVideoPanel extends BasePanel implements ActionListener,
    SearchListener {

    private NMS nms;
    private Video video;
    private JComboBox categoryComboBox;
    private JComboBox subcategoryComboBox;
    private JTextField subcategoryTextField;
    private JSpinner seasonSpinner;
    private JSpinner episodeSpinner;
    private JTextField titleTextField;
    private JTextArea descriptionTextArea;
    private JTextField releasedTextField;
    private JTextField durationTextField;
    private JButton saveButton;
    private JComboBox aspectRatioComboBox;
    private JCheckBox playIntroCheckBox;
    private ThumbnailPanel thumbnailPanel;

    /**
     * Constructor with three arguments.
     *
     * @param n A given NMS instance.
     * @param v A given Video instance.
     * @param array An array of Metadata services.
     */
    public EditVideoPanel(NMS n, Video v, Metadata[] array) {

        JTextField ttf = new JTextField(30);
        setTitleTextField(ttf);
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createTitledBorder("Title"));
        titlePanel.add(ttf, BorderLayout.CENTER);

        JTextArea dta = new JTextArea(6, 30);
        dta.setLineWrap(true);
        dta.setWrapStyleWord(true);
        setDescriptionTextArea(dta);
        JScrollPane taScroll = new JScrollPane(dta,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBorder(BorderFactory.createTitledBorder("Description"));
        descPanel.add(taScroll, BorderLayout.CENTER);

        JTextField releasedtf = new JTextField(30);
        setReleasedTextField(releasedtf);
        JPanel releasedPanel = new JPanel(new BorderLayout());
        releasedPanel.setBorder(BorderFactory.createTitledBorder("Released"));
        releasedPanel.add(releasedtf, BorderLayout.CENTER);

        JComboBox cb = new JComboBox();
        cb.setEditable(true);
        cb.addItem(NMSConstants.VIDEO_MOVIE);
        cb.addItem(NMSConstants.VIDEO_TV);
        cb.addItem(NMSConstants.VIDEO_HOME);
        cb.addItem(NMSConstants.VIDEO_EXERCISE);
        cb.addActionListener(this);
        setCategoryComboBox(cb);
        JPanel catPanel = new JPanel(new BorderLayout());
        catPanel.setBorder(BorderFactory.createTitledBorder("Category"));
        catPanel.add(cb, BorderLayout.CENTER);

        JComboBox subcb = new JComboBox();
        subcb.setEditable(true);
        subcb.addItem(NMSConstants.ADVENTURE_GENRE);
        subcb.addItem(NMSConstants.CHRISTMAS_GENRE);
        subcb.addItem(NMSConstants.COMEDY_GENRE);
        subcb.addItem(NMSConstants.DRAMA_GENRE);
        subcb.addItem(NMSConstants.FAMILY_GENRE);
        subcb.addItem(NMSConstants.HORROR_GENRE);
        subcb.addItem(NMSConstants.MUSICAL_GENRE);
        subcb.addItem(NMSConstants.MYSTERY_GENRE);
        subcb.addItem(NMSConstants.NOW_SHOWING_GENRE);
        subcb.addItem(NMSConstants.ROMANTIC_COMEDY_GENRE);
        subcb.addItem(NMSConstants.SCIFI_GENRE);
        subcb.addItem(NMSConstants.THRILLER_GENRE);
        subcb.addItem(NMSConstants.WAR_GENRE);
        subcb.addItem(NMSConstants.WESTERN_GENRE);
        setSubcategoryComboBox(subcb);

        JTextField subtf = new JTextField(24);
        subtf.setEditable(false);
        setSubcategoryTextField(subtf);

        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 20, 1);
        JSpinner seasonsp = new JSpinner(model);
        setSeasonSpinner(seasonsp);

        model = new SpinnerNumberModel(1, 1, 40, 1);
        JSpinner episodesp = new JSpinner(model);
        setEpisodeSpinner(episodesp);

        JTextField durtf = new JTextField(10);
        setDurationTextField(durtf);

        JComboBox arcb = new JComboBox();
        arcb.setEditable(false);
        arcb.addItem(NMSConstants.ASPECT_RATIO_16X9);
        arcb.addItem(NMSConstants.ASPECT_RATIO_4X3);
        arcb.addItem(NMSConstants.ASPECT_RATIO_235X1);
        setAspectRatioComboBox(arcb);

        JCheckBox introcb = new JCheckBox("Play Intro");
        setPlayIntroCheckBox(introcb);

        ThumbnailPanel tp = new ThumbnailPanel();
        setThumbnailPanel(tp);

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(titlePanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(descPanel, gbc);

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

        add(releasedPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(catPanel, gbc);

        JPanel subcatPanel = new JPanel();
        subcatPanel.setBorder(BorderFactory.createTitledBorder("Subcategory"));
        subcatPanel.setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        subcatPanel.add(subcb, gbc);

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

        subcatPanel.add(subtf, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        subcatPanel.add(seasonsp, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        subcatPanel.add(episodesp, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(subcatPanel, gbc);

        JPanel durPanel = new JPanel();
        durPanel.setBorder(
            BorderFactory.createTitledBorder("Duration (secs)"));
        durPanel.setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        durPanel.add(durtf, gbc);

        JPanel arPanel = new JPanel();
        arPanel.setBorder(BorderFactory.createTitledBorder("Aspect Ratio"));
        arPanel.setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        arPanel.add(arcb, gbc);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        middlePanel.add(introcb, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        middlePanel.add(durPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        middlePanel.add(arPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(middlePanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getThumbnailPanel(), gbc);

        // Now add any Metadata actions we have been given...
        if ((array != null) && (array.length > 0)) {

            SearchAction[] sarray = new SearchAction[array.length];
            for (int i = 0; i < sarray.length; i++) {

                sarray[i] = new SearchAction(array[i], this);
            }

            JPanel searchPanel = new JPanel();
            searchPanel.setLayout(new GridBagLayout());

            for (int i = 0; i < sarray.length; i++) {

                JButton button = new JButton(sarray[i]);

                gbc = new GridBagConstraints();
                gbc.gridx = i;
                gbc.gridy = 0;
                gbc.gridwidth = 1;
                gbc.gridheight = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.anchor = GridBagConstraints.CENTER;
                gbc.weightx = 0.0;
                gbc.weighty = 0.0;
                gbc.insets = new Insets(4, 4, 4, 4);

                searchPanel.add(button, gbc);
            }

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 7;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.weightx = 0.0;
            gbc.weighty = 0.0;
            gbc.insets = new Insets(4, 4, 4, 4);

            add(searchPanel, gbc);
        }

        setNMS(n);
        setVideo(v);
    }

    private NMS getNMS() {
        return (nms);
    }

    private void setNMS(NMS n) {
        nms = n;
    }

    /**
     * A Video instance reflecting the current state of the UI.
     *
     * @return A Video instance.
     */
    public Video getVideo() {

        if (video != null) {

            video.setCategory((String) getCategoryComboBox().getSelectedItem());
            video.setTitle(getTitleTextField().getText());
            video.setDescription(getDescriptionTextArea().getText());
            video.setReleased(getReleasedTextField().getText());
            video.setDuration(Util.str2long(getDurationTextField().getText(),
                video.getDuration()));
            video.setAspectRatio(
                (String) getAspectRatioComboBox().getSelectedItem());
            video.setPlayIntro(getPlayIntroCheckBox().isSelected());

            if (video.isTV()) {

                video.setSubcategory(null);
                Integer iobj = (Integer) getSeasonSpinner().getValue();
                if (iobj != null) {
                    video.setSeason(iobj.intValue());
                }

                iobj = (Integer) getEpisodeSpinner().getValue();
                if (iobj != null) {
                    video.setEpisode(iobj.intValue());
                }

            } else {

                video.setSubcategory(
                    (String) getSubcategoryComboBox().getSelectedItem());
            }
        }

        return (video);
    }

    private void setVideo(Video v) {
        video = v;

        updateState(v);
        if (v != null) {

            if (v.isTV()) {
                fireSearchEvent(SearchEvent.SEARCH_TV, v.getTitle());
            } else {
                fireSearchEvent(SearchEvent.SEARCH_MOVIE, v.getTitle());
            }
        }
    }

    private void updateState(Video v) {

        if (v != null) {

            getCategoryComboBox().setSelectedItem(v.getCategory());
            updateSubcategoryState(v.isTV(), v);
            getTitleTextField().setText(v.getTitle());
            getDescriptionTextArea().setText(v.getDescription());
            getReleasedTextField().setText(v.getReleased());
            getDurationTextField().setText(v.getDuration() + "");
            getAspectRatioComboBox().setSelectedItem(v.getAspectRatio());
            getPlayIntroCheckBox().setSelected(v.isPlayIntro());
            getThumbnailPanel().setVideo(v);
        }
    }

    private void updateSubcategoryState(boolean tv, Video v) {

        if (tv) {

            getSubcategoryComboBox().setSelectedItem("");
            getSubcategoryComboBox().setEnabled(false);
            getSeasonSpinner().setValue(Integer.valueOf(v.getSeason()));
            getSeasonSpinner().setEnabled(true);
            getEpisodeSpinner().setValue(Integer.valueOf(v.getEpisode()));
            getEpisodeSpinner().setEnabled(true);
            getSubcategoryTextField().setText(v.getTitle() + " Season "
                + getSeasonSpinner().getValue());

        } else {

            getSubcategoryComboBox().setSelectedItem(v.getSubcategory());
            getSubcategoryComboBox().setEnabled(true);
            getSeasonSpinner().setValue(Integer.valueOf(1));
            getSeasonSpinner().setEnabled(false);
            getEpisodeSpinner().setValue(Integer.valueOf(1));
            getEpisodeSpinner().setEnabled(false);
            getSubcategoryTextField().setText("");
        }
    }

    private JComboBox getCategoryComboBox() {
        return (categoryComboBox);
    }

    private void setCategoryComboBox(JComboBox cb) {
        categoryComboBox = cb;
    }

    private JComboBox getSubcategoryComboBox() {
        return (subcategoryComboBox);
    }

    private void setSubcategoryComboBox(JComboBox cb) {
        subcategoryComboBox = cb;
    }

    private JTextField getSubcategoryTextField() {
        return (subcategoryTextField);
    }

    private void setSubcategoryTextField(JTextField tf) {
        subcategoryTextField = tf;
    }

    private JSpinner getSeasonSpinner() {
        return (seasonSpinner);
    }

    private void setSeasonSpinner(JSpinner s) {
        seasonSpinner = s;
    }

    private JSpinner getEpisodeSpinner() {
        return (episodeSpinner);
    }

    private void setEpisodeSpinner(JSpinner s) {
        episodeSpinner = s;
    }

    private JTextField getTitleTextField() {
        return (titleTextField);
    }

    private void setTitleTextField(JTextField tf) {
        titleTextField = tf;
    }

    private JTextArea getDescriptionTextArea() {
        return (descriptionTextArea);
    }

    private void setDescriptionTextArea(JTextArea ta) {
        descriptionTextArea = ta;
    }

    private JTextField getReleasedTextField() {
        return (releasedTextField);
    }

    private void setReleasedTextField(JTextField tf) {
        releasedTextField = tf;
    }

    private JTextField getDurationTextField() {
        return (durationTextField);
    }

    private void setDurationTextField(JTextField tf) {
        durationTextField = tf;
    }

    private JComboBox getAspectRatioComboBox() {
        return (aspectRatioComboBox);
    }

    private void setAspectRatioComboBox(JComboBox cb) {
        aspectRatioComboBox = cb;
    }

    private JCheckBox getPlayIntroCheckBox() {
        return (playIntroCheckBox);
    }

    private void setPlayIntroCheckBox(JCheckBox cb) {
        playIntroCheckBox = cb;
    }

    private ThumbnailPanel getThumbnailPanel() {
        return (thumbnailPanel);
    }

    private void setThumbnailPanel(ThumbnailPanel tp) {
        thumbnailPanel = tp;
    }

    private Frame getFrame() {
        return (Util.findFrame(this));
    }

    private JPanel getPanel() {
        return (this);
    }

    /**
     * We listen for category combo box events so we can set the subcategory
     * state correctly.
     *
     * @param event A given ActionEvent.
     */
    public void actionPerformed(ActionEvent event) {

        if (event.getSource() == getCategoryComboBox()) {

            JComboBox cb = getCategoryComboBox();
            String old = video.getCategory();
            String value = (String) cb.getSelectedItem();
            if (!Util.equalOrNull(old, value)) {

                video.setCategory(value);
            }

            updateSubcategoryState(NMSConstants.VIDEO_TV.equals(value), video);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void searchUpdate(SearchEvent event) {

        if (event.getType() == SearchEvent.UPDATE) {

            if (isVisible()) {

                // Extract and update.
                Hit h = event.getHit();
                Video v = getVideo();
                if ((v != null) && (h != null)) {

                    v.setTitle(h.getTitle());
                    v.setDescription(h.getDescription());
                    v.setReleased(h.getReleased());
                    v.setPosterURL(h.getPosterURL());
                    v.setFanartURL(h.getFanartURL());
                    updateState(v);
                }
            }
        }
    }

    class SearchAction extends AbstractAction {

        private Metadata metadata;
        private EditVideoPanel editVideoPanel;
        private SearchPanel searchPanel;

        public SearchAction(Metadata m, EditVideoPanel p) {

            editVideoPanel = p;
            metadata = m;
            if (m != null) {

                putValue(NAME, m.getTitle());

                SearchPanel sp = m.getSearchPanel();
                if (sp != null) {

                    sp.addSearchListener(editVideoPanel);
                    editVideoPanel.addSearchListener(sp);
                    searchPanel = sp;
                }
            }
        }

        public void actionPerformed(ActionEvent e) {

            if ((metadata != null) && (searchPanel != null)) {

                Util.showDoneDialog(getFrame(), "Search", searchPanel);
            }
        }
    }

}

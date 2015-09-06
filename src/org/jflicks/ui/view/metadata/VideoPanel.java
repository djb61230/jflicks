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
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.metadata.Hit;
import org.jflicks.metadata.SearchEvent;
import org.jflicks.metadata.SearchListener;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.Video;
import org.jflicks.util.ProgressBar;
import org.jflicks.util.Util;

/**
 * Implements a View so a user can control the metadata of videos.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class VideoPanel extends BasePanel implements ActionListener,
    FocusListener, ListSelectionListener, SearchListener, ChangeListener {

    private NMS nms;
    private JList videoDisplayList;
    private JComboBox categoryComboBox;
    private JComboBox subcategoryComboBox;
    private JTextField subcategoryTextField;
    private JSpinner seasonSpinner;
    private JSpinner episodeSpinner;
    private JTextField titleTextField;
    private JTextArea descriptionTextArea;
    private JTextField releasedTextField;
    private JTextField filenameTextField;
    private JTextField pathTextField;
    private JTextField durationTextField;
    private JButton durationButton;
    private JButton generateImageButton;
    private JButton viewImagesButton;
    private JButton saveButton;
    private DurationAction durationAction;
    private GenerateImageAction generateImageAction;
    private JComboBox aspectRatioComboBox;
    private QuickScreenAction quickScreenAction;
    private JButton quickScreenButton;
    private JCheckBox playIntroCheckBox;
    private ViewImagesAction viewImagesAction;
    private SaveAction saveAction;
    private ArrayList<Video> videoList;

    /**
     * Default constructor.
     */
    public VideoPanel() {

        setVideoList(new ArrayList<Video>());

        JList l = new JList();
        l.setPrototypeCellValue("0123456789012345678901234567890123456789");
        l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        l.setVisibleRowCount(28);
        l.addListSelectionListener(this);
        setVideoDisplayList(l);
        JScrollPane videolistScroller = new JScrollPane(l,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        ViewImagesAction view = new ViewImagesAction();
        view.setEnabled(false);
        setViewImagesAction(view);
        JButton viewb = new JButton(view);
        setViewImagesButton(viewb);

        GenerateImageAction gen = new GenerateImageAction();
        gen.setEnabled(false);
        setGenerateImageAction(gen);
        JButton genb = new JButton(gen);
        setGenerateImageButton(genb);

        SaveAction save = new SaveAction();
        save.setEnabled(false);
        setSaveAction(save);
        JButton saveb = new JButton(save);
        setSaveButton(saveb);

        JTextField ttf = new JTextField(30);
        ttf.addActionListener(this);
        ttf.addFocusListener(this);
        setTitleTextField(ttf);
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createTitledBorder("Title"));
        titlePanel.add(ttf, BorderLayout.CENTER);

        JTextArea dta = new JTextArea(6, 30);
        dta.setLineWrap(true);
        dta.setWrapStyleWord(true);
        dta.addFocusListener(this);
        setDescriptionTextArea(dta);
        JScrollPane taScroll = new JScrollPane(dta,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBorder(BorderFactory.createTitledBorder("Description"));
        descPanel.add(taScroll, BorderLayout.CENTER);

        JTextField releasedtf = new JTextField(30);
        releasedtf.addActionListener(this);
        releasedtf.addFocusListener(this);
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
        subcb.addActionListener(this);
        setSubcategoryComboBox(subcb);

        JTextField subtf = new JTextField(24);
        subtf.setEditable(false);
        subtf.addFocusListener(this);
        subtf.addActionListener(this);
        setSubcategoryTextField(subtf);

        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 20, 1);
        JSpinner seasonsp = new JSpinner(model);
        seasonsp.addChangeListener(this);
        setSeasonSpinner(seasonsp);

        model = new SpinnerNumberModel(1, 1, 40, 1);
        JSpinner episodesp = new JSpinner(model);
        episodesp.addChangeListener(this);
        setEpisodeSpinner(episodesp);

        JTextField fntf = new JTextField(30);
        fntf.setEditable(false);
        fntf.setBorder(null);
        setFilenameTextField(fntf);
        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.setBorder(
            BorderFactory.createTitledBorder("Unique File Name"));
        filePanel.add(fntf, BorderLayout.CENTER);

        JTextField ptf = new JTextField(30);
        ptf.setEditable(false);
        ptf.setBorder(null);
        setPathTextField(ptf);
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.setBorder(BorderFactory.createTitledBorder("Path"));
        pathPanel.add(ptf, BorderLayout.CENTER);

        JTextField durtf = new JTextField(10);
        durtf.addFocusListener(this);
        durtf.addActionListener(this);
        setDurationTextField(durtf);
        DurationAction duration = new DurationAction();
        setDurationAction(duration);
        JButton durationb = new JButton(duration);
        setDurationButton(durationb);

        QuickScreenAction quick = new QuickScreenAction();
        setQuickScreenAction(quick);
        JButton quickb = new JButton(quick);
        setQuickScreenButton(quickb);
        JComboBox arcb = new JComboBox();
        arcb.setEditable(false);
        arcb.addItem(NMSConstants.ASPECT_RATIO_16X9);
        arcb.addItem(NMSConstants.ASPECT_RATIO_4X3);
        arcb.addItem(NMSConstants.ASPECT_RATIO_235X1);
        arcb.addActionListener(this);
        setAspectRatioComboBox(arcb);

        JCheckBox introcb = new JCheckBox("Play Intro");
        introcb.addActionListener(this);
        setPlayIntroCheckBox(introcb);

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
        gbc.gridx = 0;
        gbc.gridy = 6;
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
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(genb, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
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
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(titlePanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
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
        gbc.gridx = 1;
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
        gbc.gridx = 1;
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
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(subcatPanel, gbc);

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

        middlePanel.add(filePanel, gbc);

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

        middlePanel.add(introcb, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
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
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(pathPanel, gbc);

        JPanel durPanel = new JPanel();
        durPanel.setBorder(
            BorderFactory.createTitledBorder("Duration in Seconds"));
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

        durPanel.add(durationb, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(durPanel, gbc);

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

        arPanel.add(quickb, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(arPanel, gbc);
    }

    private ArrayList<Video> getVideoList() {
        return (videoList);
    }

    private void setVideoList(ArrayList<Video> l) {
        videoList = l;
    }

    private void addVideo(Video v) {

         ArrayList<Video> l = getVideoList();
         if ((l != null) && (v != null)) {

             if (l.contains(v)) {

                 l.remove(v);
                 l.add(v);

             } else {

                 l.add(v);
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

    private JList getVideoDisplayList() {
        return (videoDisplayList);
    }

    private void setVideoDisplayList(JList l) {
        videoDisplayList = l;
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

    private JTextField getFilenameTextField() {
        return (filenameTextField);
    }

    private void setFilenameTextField(JTextField tf) {
        filenameTextField = tf;
    }

    private JTextField getPathTextField() {
        return (pathTextField);
    }

    private void setPathTextField(JTextField tf) {
        pathTextField = tf;
    }

    private JTextField getDurationTextField() {
        return (durationTextField);
    }

    private void setDurationTextField(JTextField tf) {
        durationTextField = tf;
    }

    private JButton getDurationButton() {
        return (durationButton);
    }

    private void setDurationButton(JButton b) {
        durationButton = b;
    }

    private JButton getGenerateImageButton() {
        return (generateImageButton);
    }

    private void setGenerateImageButton(JButton b) {
        generateImageButton = b;
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

    private DurationAction getDurationAction() {
        return (durationAction);
    }

    private void setDurationAction(DurationAction a) {
        durationAction = a;
    }

    private GenerateImageAction getGenerateImageAction() {
        return (generateImageAction);
    }

    private void setGenerateImageAction(GenerateImageAction a) {
        generateImageAction = a;
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

    private JComboBox getAspectRatioComboBox() {
        return (aspectRatioComboBox);
    }

    private void setAspectRatioComboBox(JComboBox cb) {
        aspectRatioComboBox = cb;
    }

    private JButton getQuickScreenButton() {
        return (quickScreenButton);
    }

    private void setQuickScreenButton(JButton b) {
        quickScreenButton = b;
    }

    private QuickScreenAction getQuickScreenAction() {
        return (quickScreenAction);
    }

    private void setQuickScreenAction(QuickScreenAction a) {
        quickScreenAction = a;
    }

    private JCheckBox getPlayIntroCheckBox() {
        return (playIntroCheckBox);
    }

    private void setPlayIntroCheckBox(JCheckBox cb) {
        playIntroCheckBox = cb;
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

    private void updateVideo(Video v) {

        if (v != null) {

            getCategoryComboBox().setSelectedItem(v.getCategory());
            updateSubcategoryState(v.isTV(), v);
            getTitleTextField().setText(v.getTitle());
            getDescriptionTextArea().setText(v.getDescription());
            getReleasedTextField().setText(v.getReleased());
            getFilenameTextField().setText(v.getFilename());
            getPathTextField().setText(v.getPath());
            getDurationTextField().setText(v.getDuration() + "");
            getViewImagesAction().setEnabled(true);
            getGenerateImageAction().setEnabled(true);
            getAspectRatioComboBox().setSelectedItem(v.getAspectRatio());
            getPlayIntroCheckBox().setSelected(v.isPlayIntro());

            if (v.isTV()) {
                fireSearchEvent(SearchEvent.SEARCH_TV, v.getTitle());
            } else {
                fireSearchEvent(SearchEvent.SEARCH_MOVIE, v.getTitle());
            }
        }
    }

    private void updateTitle() {

        Video v = getSelectedVideo();
        if (v != null) {

            JTextField tf = getTitleTextField();
            String old = v.getTitle();
            String value = tf.getText();
            if (!Util.equalOrNull(old, value)) {

                v.setTitle(value);
                addVideo(v);
            }

            if (v.isTV()) {

                updateSubcategoryState(true, v);
            }
        }
    }

    private void updateDescription() {

        Video v = getSelectedVideo();
        if (v != null) {

            JTextArea tf = getDescriptionTextArea();
            String old = v.getDescription();
            String value = tf.getText();
            if (!Util.equalOrNull(old, value)) {

                v.setDescription(value);
                addVideo(v);
            }
        }
    }

    private void updateSubcategory() {

        Video v = getSelectedVideo();
        if (v != null) {

            if (v.isTV()) {

                JTextField tf = getSubcategoryTextField();
                String old = v.getSubcategory();
                String value = tf.getText();
                if (!Util.equalOrNull(old, value)) {

                    v.setSubcategory(value);
                    addVideo(v);
                }

            } else {

                JComboBox cb = getSubcategoryComboBox();
                String old = v.getSubcategory();
                String value = (String) cb.getSelectedItem();
                if (!Util.equalOrNull(old, value)) {

                    v.setSubcategory(value);
                    addVideo(v);
                }

            }
        }
    }

    private void updateReleased() {

        Video v = getSelectedVideo();
        if (v != null) {

            JTextField tf = getReleasedTextField();
            String old = v.getReleased();
            String value = tf.getText();
            if (!Util.equalOrNull(old, value)) {

                v.setReleased(value);
                addVideo(v);
            }
        }
    }

    private void updateDuration() {

        Video v = getSelectedVideo();
        if (v != null) {

            JTextField tf = getDurationTextField();
            String old = v.getDuration() + "";
            String value = tf.getText();
            if (!Util.equalOrNull(old, value)) {

                v.setDuration(Util.str2long(value, v.getDuration()));
                addVideo(v);
            }
        }
    }

    private void updateAspectRatio() {

        Video v = getSelectedVideo();
        if (v != null) {

            JComboBox cb = getAspectRatioComboBox();
            String old = v.getAspectRatio();
            String value = (String) cb.getSelectedItem();
            if (!Util.equalOrNull(old, value)) {

                v.setAspectRatio(value);
                addVideo(v);
            }
        }
    }

    private void updatePlayIntro() {

        Video v = getSelectedVideo();
        if (v != null) {

            JCheckBox cb = getPlayIntroCheckBox();
            boolean old = v.isPlayIntro();
            boolean value = cb.isSelected();
            if (old != value) {

                v.setPlayIntro(value);
                addVideo(v);
            }
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
                Video v = getSelectedVideo();
                if ((v != null) && (h != null)) {

                    v.setTitle(h.getTitle());
                    v.setDescription(h.getDescription());
                    v.setReleased(h.getReleased());
                    v.setPosterURL(h.getPosterURL());
                    v.setFanartURL(h.getFanartURL());
                    updateVideo(v);

                    addVideo(v);
                }
            }
        }
    }

    /**
     * We listen for Spinner events to update the proper Video values.
     *
     * @param event The given ChangeEvent.
     */
    public void stateChanged(ChangeEvent event) {

        Video v = getSelectedVideo();
        if (v != null) {

            if (event.getSource() == getSeasonSpinner()) {

                JSpinner spin = getSeasonSpinner();
                int old = v.getSeason();
                Integer value = (Integer) spin.getValue();
                if (old != value.intValue()) {

                    v.setSeason(value.intValue());
                    addVideo(v);
                }

                updateSubcategoryState(true, v);

            } else if (event.getSource() == getEpisodeSpinner()) {

                JSpinner spin = getEpisodeSpinner();
                int old = v.getEpisode();
                Integer value = (Integer) spin.getValue();
                if (old != value.intValue()) {

                    v.setEpisode(value.intValue());
                    addVideo(v);
                }
            }
        }
    }

    /**
     * Update Video from UI components.
     *
     * @param event A given ActionEvent instance.
     */
    public void actionPerformed(ActionEvent event) {

        Video v = getSelectedVideo();
        if (v != null) {

            if (event.getSource() == getCategoryComboBox()) {

                JComboBox cb = getCategoryComboBox();
                String old = v.getCategory();
                String value = (String) cb.getSelectedItem();
                if (!Util.equalOrNull(old, value)) {

                    v.setCategory(value);
                    addVideo(v);
                }

                updateSubcategoryState(NMSConstants.VIDEO_TV.equals(value), v);

            } else if (event.getSource() == getSubcategoryComboBox()) {

                updateSubcategory();

            } else if (event.getSource() == getSubcategoryTextField()) {

                updateSubcategory();

            } else if (event.getSource() == getTitleTextField()) {

                updateTitle();

            } else if (event.getSource() == getReleasedTextField()) {

                updateReleased();

            } else if (event.getSource() == getDurationTextField()) {

                updateDuration();

            } else if (event.getSource() == getAspectRatioComboBox()) {

                updateAspectRatio();

            } else if (event.getSource() == getPlayIntroCheckBox()) {

                updatePlayIntro();
            }
        }
    }

    /**
     * We need to check focus events to update the Video instance when the
     * user edits it.
     *
     * @param event The given FocusEvent.
     */
    public void focusGained(FocusEvent event) {
    }

    /**
     * We need to check focus events to update the Video instance when the
     * user edits it.
     *
     * @param event The given FocusEvent.
     */
    public void focusLost(FocusEvent event) {

        if (event.getSource() == getSubcategoryTextField()) {

            updateSubcategory();

        } else if (event.getSource() == getTitleTextField()) {

            updateTitle();

        } else if (event.getSource() == getReleasedTextField()) {

            updateReleased();

        } else if (event.getSource() == getDescriptionTextArea()) {

            updateDescription();
        }
    }

    /**
     * We listen for selection on the video list box.
     *
     * @param event The given list selection event.
     */
    public void valueChanged(ListSelectionEvent event) {

        if (!event.getValueIsAdjusting()) {

            if (event.getSource() == getVideoDisplayList()) {

                JList l = getVideoDisplayList();
                int index = l.getSelectedIndex();
                if (index != -1) {

                    Video v = (Video) l.getSelectedValue();
                    if (v != null) {

                        updateVideo(v);
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

            Video v = getSelectedVideo();
            if (v != null) {

                JScrollPane scroll = new JScrollPane(new ImagePanel(v),
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
            ArrayList<Video> l = getVideoList();
            if ((n != null) && (l != null) && (l.size() > 0)) {

                Video[] array = l.toArray(new Video[l.size()]);
                SaveVideoJob svj = new SaveVideoJob(n, array);
                ProgressBar pbar =
                    new ProgressBar(getPanel(), "Saving...", svj);
                pbar.addJobListener(this);
                pbar.execute();
                l.clear();
            }
        }

    }

    class DurationAction extends AbstractAction implements JobListener {

        private MediainfoJob mediainfoJob;

        public DurationAction() {

            putValue(NAME, "Compute Duration");
            putValue(SHORT_DESCRIPTION, "Compute Duration");
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_C));
        }

        private MediainfoJob getMediainfoJob() {
            return (mediainfoJob);
        }

        private void setMediainfoJob(MediainfoJob j) {
            mediainfoJob = j;
        }

        public void jobUpdate(JobEvent event) {

            if (event.getType() == JobEvent.COMPLETE) {

                Video v = getSelectedVideo();
                MediainfoJob mij = getMediainfoJob();
                if ((v != null) && (mij != null)) {

                    v.setDuration(mij.getSeconds());
                    updateVideo(v);
                    addVideo(v);
                    setMediainfoJob(null);
                }
            }
        }

        public void actionPerformed(ActionEvent event) {

            Video v = getSelectedVideo();
            if (v != null) {

                MediainfoJob mij = new MediainfoJob(v);
                setMediainfoJob(mij);
                ProgressBar pbar =
                    new ProgressBar(getPanel(), "Computing...", mij);
                pbar.addJobListener(this);
                pbar.execute();
            }
        }

    }

    class GenerateImageAction extends AbstractAction implements JobListener {

        private GenerateImageJob generateImageJob;

        public GenerateImageAction() {

            putValue(NAME, "Generate Images From Video");
            putValue(SHORT_DESCRIPTION, "Generate Images From Video");
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_G));
        }

        private GenerateImageJob getGenerateImageJob() {
            return (generateImageJob);
        }

        private void setGenerateImageJob(GenerateImageJob j) {
            generateImageJob = j;
        }

        public void jobUpdate(JobEvent event) {

            if (event.getType() == JobEvent.COMPLETE) {

                Video v = getSelectedVideo();
                GenerateImageJob gij = getGenerateImageJob();
                if ((v != null) && (gij != null)) {

                    updateVideo(v);
                    addVideo(v);
                    setGenerateImageJob(null);
                }
            }
        }

        public void actionPerformed(ActionEvent event) {

            Video v = getSelectedVideo();
            if (v != null) {

                GenerateImageJob gij = new GenerateImageJob(v);
                setGenerateImageJob(gij);
                ProgressBar pbar =
                    new ProgressBar(getPanel(), "Generating...", gij);
                pbar.addJobListener(this);
                pbar.execute();
            }
        }

    }

    class QuickScreenAction extends AbstractAction implements JobListener {

        private QuickScreenJob quickScreenJob;

        public QuickScreenAction() {

            putValue(NAME, "Quick Screenshot");
            putValue(SHORT_DESCRIPTION, "Quick Screenshot");
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_Q));
        }

        private QuickScreenJob getQuickScreenJob() {
            return (quickScreenJob);
        }

        private void setQuickScreenJob(QuickScreenJob j) {
            quickScreenJob = j;
        }

        public void jobUpdate(JobEvent event) {

            if (event.getType() == JobEvent.COMPLETE) {

                Video v = getSelectedVideo();
                QuickScreenJob qsj = getQuickScreenJob();
                if ((v != null) && (qsj != null)) {

                    setQuickScreenJob(null);
                    File imagef = qsj.getImageFile();
                    if (imagef != null) {

                        try {

                            BufferedImage bi = ImageIO.read(imagef);
                            ImageIcon ii = new ImageIcon(bi);
                            JLabel label = new JLabel(ii);
                            JPanel panel = new JPanel();
                            panel.add(label);
                            Util.showDialog(getFrame(), "Quick Screenshot",
                                panel, false);

                        } catch (IOException ex) {

                            JOptionPane.showMessageDialog(getFrame(),
                                "Cannot display Quick Screenshot",
                                "alert", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }

        public void actionPerformed(ActionEvent event) {

            Video v = getSelectedVideo();
            if (v != null) {

                QuickScreenJob qsj = new QuickScreenJob(v);
                setQuickScreenJob(qsj);
                ProgressBar pbar =
                    new ProgressBar(getPanel(), "Generating...", qsj);
                pbar.addJobListener(this);
                pbar.execute();
            }
        }

    }

}

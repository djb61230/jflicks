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
import java.awt.Frame;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;

import org.jflicks.nms.Video;
import org.jflicks.util.PromptPanel;
import org.jflicks.util.Util;

/**
 * Implements a View so a user can control the metadata of videos.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class VideoDetailPanel extends JPanel {

    private Video video;
    private JTextField titleTextField;
    private JTextArea descriptionTextArea;
    private JTextField releasedTextField;
    private JTextField categoryTextField;
    private JTextField subcategoryTextField;
    private JTextField filenameTextField;
    private JTextField pathTextField;
    private JTextField durationTextField;
    private JTextField aspectRatioTextField;
    private JTextField playIntroTextField;
    private ThumbnailPanel thumbnailPanel;

    /**
     * Default constructor.
     */
    public VideoDetailPanel() {

        JTextField ttf = new JTextField(30);
        ttf.setEditable(false);
        setTitleTextField(ttf);

        JTextArea dta = new JTextArea(6, 30);
        dta.setLineWrap(true);
        dta.setWrapStyleWord(true);
        dta.setEditable(false);
        setDescriptionTextArea(dta);
        JScrollPane taScroll = new JScrollPane(dta,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JTextField releasedtf = new JTextField(30);
        releasedtf.setEditable(false);
        setReleasedTextField(releasedtf);

        JTextField cattf = new JTextField(30);
        cattf.setEditable(false);
        setCategoryTextField(cattf);

        JTextField subcattf = new JTextField(30);
        subcattf.setEditable(false);
        setSubcategoryTextField(subcattf);

        JTextField fntf = new JTextField(30);
        fntf.setEditable(false);
        setFilenameTextField(fntf);

        JTextField ptf = new JTextField(30);
        ptf.setEditable(false);
        setPathTextField(ptf);

        JTextField durtf = new JTextField(30);
        durtf.setEditable(false);
        setDurationTextField(durtf);

        JTextField artf = new JTextField(30);
        artf.setEditable(false);
        setAspectRatioTextField(artf);

        JTextField pitf = new JTextField(30);
        pitf.setEditable(false);
        setPlayIntroTextField(pitf);

        ThumbnailPanel tp = new ThumbnailPanel();
        setThumbnailPanel(tp);

        setLayout(new BorderLayout());

        String[] prompts = {

            "Title",
            "Description",
            "Released",
            "Category",
            "Subcategory",
            "Unique File Name",
            "Path",
            "Duration (secs)",
            "Aspect Ratio",
            "Play Intro",
            "Thumbnails"
        };

        JComponent[] comps = {

            getTitleTextField(),
            taScroll,
            getReleasedTextField(),
            getCategoryTextField(),
            getSubcategoryTextField(),
            getFilenameTextField(),
            getPathTextField(),
            getDurationTextField(),
            getAspectRatioTextField(),
            getPlayIntroTextField(),
            tp
        };

        PromptPanel pp = new PromptPanel(prompts, comps);
        add(pp);
    }

    /**
     * The current Video object being displayed.
     *
     * @return A Video instance.
     */
    public Video getVideo() {
        return (video);
    }

    /**
     * The current Video object being displayed.
     *
     * @param v A Video instance.
     */
    public void setVideo(Video v) {
        video = v;
        updateVideo(v);
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

    private JTextField getCategoryTextField() {
        return (categoryTextField);
    }

    private void setCategoryTextField(JTextField tf) {
        categoryTextField = tf;
    }

    private JTextField getSubcategoryTextField() {
        return (subcategoryTextField);
    }

    private void setSubcategoryTextField(JTextField tf) {
        subcategoryTextField = tf;
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

    private JTextField getAspectRatioTextField() {
        return (aspectRatioTextField);
    }

    private void setAspectRatioTextField(JTextField tf) {
        aspectRatioTextField = tf;
    }

    private JTextField getPlayIntroTextField() {
        return (playIntroTextField);
    }

    private void setPlayIntroTextField(JTextField tf) {
        playIntroTextField = tf;
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

    private void updateVideo(Video v) {

        if (v != null) {

            getTitleTextField().setText(v.getTitle());
            getDescriptionTextArea().setText(v.getDescription());
            getReleasedTextField().setText(v.getReleased());
            getCategoryTextField().setText(v.getCategory());

            if (v.isTV()) {

                getSubcategoryTextField().setText("Season "
                    + v.getSeason() + " Episode " + v.getEpisode());

            } else {

                getSubcategoryTextField().setText(v.getSubcategory());
            }

            getFilenameTextField().setText(v.getFilename());
            getPathTextField().setText(v.getPath());
            getDurationTextField().setText(v.getDuration() + "");
            getAspectRatioTextField().setText(v.getAspectRatio());
            if (v.isPlayIntro()) {
                getPlayIntroTextField().setText("Yes");
            } else {
                getPlayIntroTextField().setText("No");
            }

            getThumbnailPanel().setVideo(v);

        } else {

            getTitleTextField().setText("");
            getDescriptionTextArea().setText("");
            getReleasedTextField().setText("");
            getCategoryTextField().setText("");
            getFilenameTextField().setText("");
            getPathTextField().setText("");
            getDurationTextField().setText("");
            getAspectRatioTextField().setText("");
            getPlayIntroTextField().setText("");

            getThumbnailPanel().setVideo(null);
        }
    }

}

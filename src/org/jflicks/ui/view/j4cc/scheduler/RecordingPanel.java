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
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.jflicks.tv.Recording;
import org.jflicks.util.PromptPanel;

/**
 * Panel that deals with adding a Recording.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordingPanel extends JPanel {

    private Recording recording;
    private JTextField titleTextField;
    private JTextField subtitleTextField;
    private JTextField dateTextField;
    private JTextField durationTextField;
    private JTextArea descriptionTextArea;
    private JTextField flaggedTextField;

    /**
     * Simple constructor.
     */
    public RecordingPanel() {

        JTextField titletf = new JTextField(20);
        titletf.setEditable(false);
        setTitleTextField(titletf);

        JTextField subtitletf = new JTextField(20);
        subtitletf.setEditable(false);
        setSubtitleTextField(subtitletf);

        JTextField datetf = new JTextField(20);
        datetf.setEditable(false);
        setDateTextField(datetf);

        JTextField durationtf = new JTextField(20);
        durationtf.setEditable(false);
        setDurationTextField(durationtf);

        JTextArea descta = new JTextArea(8, 20);
        descta.setEditable(false);
        descta.setLineWrap(true);
        descta.setWrapStyleWord(true);
        setDescriptionTextArea(descta);

        JScrollPane scroller = new JScrollPane(descta,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JTextField flaggedtf = new JTextField(20);
        flaggedtf.setEditable(false);
        setFlaggedTextField(flaggedtf);

        String[] prompts = {
            "Title", "Subtitle", "Description", "Date", "Duration",
            "Commercial Flagged"
        };

        JComponent[] comps = {
            titletf, subtitletf, scroller, datetf, durationtf, flaggedtf
        };

        PromptPanel pp = new PromptPanel(null, 2, prompts, comps);

        setLayout(new BorderLayout());
        add(pp, BorderLayout.CENTER);
    }

    /**
     * All UI components show data from a Recording instance.
     *
     * @return A Recording object.
     */
    public Recording getRecording() {
        return (recording);
    }

    /**
     * All UI components show data from a Recording instance.
     *
     * @param r A Recording object.
     */
    public void setRecording(Recording r) {
        recording = r;

        if (r != null) {

            apply(getTitleTextField(), r.getTitle());
            apply(getSubtitleTextField(), r.getSubtitle());
            apply(getDescriptionTextArea(), r.getDescription());
            Date d = r.getDate();
            if (d != null) {
                apply(getDateTextField(), d.toString());
            } else {
                apply(getDateTextField(), null);
            }

            apply(getDurationTextField(), durationToString(r.getDuration()));

            if (r.getCommercials() != null) {
                apply(getFlaggedTextField(), "Yes");
            } else {
                apply(getFlaggedTextField(), "No");
            }

        } else {

            apply(getTitleTextField(), null);
            apply(getSubtitleTextField(), null);
            apply(getDescriptionTextArea(), null);
            apply(getDateTextField(), null);
            apply(getDurationTextField(), null);
            apply(getFlaggedTextField(), null);
        }
    }

    private JTextField getTitleTextField() {
        return (titleTextField);
    }

    private void setTitleTextField(JTextField tf) {
        titleTextField = tf;
    }

    private JTextField getSubtitleTextField() {
        return (subtitleTextField);
    }

    private void setSubtitleTextField(JTextField tf) {
        subtitleTextField = tf;
    }

    private JTextField getDateTextField() {
        return (dateTextField);
    }

    private void setDateTextField(JTextField tf) {
        dateTextField = tf;
    }

    private JTextField getDurationTextField() {
        return (durationTextField);
    }

    private void setDurationTextField(JTextField tf) {
        durationTextField = tf;
    }

    private JTextArea getDescriptionTextArea() {
        return (descriptionTextArea);
    }

    private void setDescriptionTextArea(JTextArea ta) {
        descriptionTextArea = ta;
    }

    private JTextField getFlaggedTextField() {
        return (flaggedTextField);
    }

    private void setFlaggedTextField(JTextField tf) {
        flaggedTextField = tf;
    }

    private String durationToString(long l) {

        return ((l / 60) + " minutes");
    }

    private void apply(JTextComponent c, String s) {

        if (c != null) {

            if (s != null) {

                c.setText(s.trim());

            } else {

                c.setText("");
            }
        }
    }

}

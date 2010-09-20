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
import java.awt.Insets;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.jflicks.tv.Recording;

/**
 * Panel that deals with adding a Recording.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordingDisplayPanel extends JPanel {

    private Recording recording;
    private JTextField titleTextField;
    private JTextField subtitleTextField;
    private JTextField dateTextField;
    private JTextField durationTextField;
    private JTextArea descriptionTextArea;

    /**
     * Simple constructor.
     */
    public RecordingDisplayPanel() {

        JTextField titletf = new JTextField(20);
        titletf.setEditable(false);
        titletf.setBorder(null);
        setTitleTextField(titletf);
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titletf, BorderLayout.CENTER);
        titlePanel.setBorder(BorderFactory.createTitledBorder("Title"));

        JTextField subtitletf = new JTextField(20);
        subtitletf.setEditable(false);
        subtitletf.setBorder(null);
        setSubtitleTextField(subtitletf);
        JPanel subtitlePanel = new JPanel(new BorderLayout());
        subtitlePanel.add(subtitletf, BorderLayout.CENTER);
        subtitlePanel.setBorder(BorderFactory.createTitledBorder("Subtitle"));

        JTextArea descta = new JTextArea(8, 20);
        descta.setEditable(false);
        descta.setLineWrap(true);
        descta.setWrapStyleWord(true);
        setDescriptionTextArea(descta);

        JScrollPane scroller = new JScrollPane(descta,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.add(scroller, BorderLayout.CENTER);
        descPanel.setBorder(BorderFactory.createTitledBorder("Description"));

        JTextField datetf = new JTextField(20);
        datetf.setEditable(false);
        datetf.setBorder(null);
        setDateTextField(datetf);
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.add(datetf, BorderLayout.CENTER);
        datePanel.setBorder(BorderFactory.createTitledBorder("Date"));

        JTextField durationtf = new JTextField(20);
        durationtf.setEditable(false);
        durationtf.setBorder(null);
        setDurationTextField(durationtf);
        JPanel durationPanel = new JPanel(new BorderLayout());
        durationPanel.add(durationtf, BorderLayout.CENTER);
        durationPanel.setBorder(BorderFactory.createTitledBorder("Duration"));

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
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
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(subtitlePanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(descPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(datePanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(durationPanel, gbc);
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

        } else {

            apply(getTitleTextField(), null);
            apply(getSubtitleTextField(), null);
            apply(getDescriptionTextArea(), null);
            apply(getDateTextField(), null);
            apply(getDurationTextField(), null);
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

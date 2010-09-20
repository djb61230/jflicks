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
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.jflicks.tv.Upcoming;
import org.jflicks.util.PromptPanel;

/**
 * Panel that deals with displaying an Upcoming object.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class UpcomingPanel extends JPanel {

    private Upcoming upcoming;
    private JTextField titleTextField;
    private JTextField subtitleTextField;
    private JTextArea descriptionTextArea;
    private JTextField channelTextField;
    private JTextField startTextField;
    private JTextField durationTextField;
    private JTextField priorityTextField;
    private JTextField statusTextField;
    private JTextField recorderNameTextField;

    /**
     * Simple constructor.
     */
    public UpcomingPanel() {

        JTextField titletf = new JTextField(20);
        titletf.setEditable(false);
        setTitleTextField(titletf);

        JTextField subtitletf = new JTextField(20);
        subtitletf.setEditable(false);
        setSubtitleTextField(subtitletf);

        JTextArea descta = new JTextArea(8, 20);
        descta.setEditable(false);
        descta.setLineWrap(true);
        descta.setWrapStyleWord(true);
        setDescriptionTextArea(descta);

        JScrollPane scroller = new JScrollPane(descta,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JTextField channeltf = new JTextField(20);
        channeltf.setEditable(false);
        setChannelTextField(channeltf);

        JTextField starttf = new JTextField(20);
        starttf.setEditable(false);
        setStartTextField(starttf);

        JTextField durationtf = new JTextField(20);
        durationtf.setEditable(false);
        setDurationTextField(durationtf);

        JTextField prioritytf = new JTextField(20);
        prioritytf.setEditable(false);
        setPriorityTextField(prioritytf);

        JTextField statustf = new JTextField(20);
        statustf.setEditable(false);
        setStatusTextField(statustf);

        JTextField recordnametf = new JTextField(20);
        recordnametf.setEditable(false);
        setRecorderNameTextField(recordnametf);

        String[] prompts = {
            "Title", "Subtitle", "Description", "Channel", "Start", "Duration",
            "Priority", "Status", "Recorder"
        };

        JComponent[] comps = {
            titletf, subtitletf, scroller, channeltf, starttf, durationtf,
            prioritytf, statustf, recordnametf
        };

        PromptPanel pp = new PromptPanel(null, -1, prompts, comps);

        setLayout(new BorderLayout());
        add(pp, BorderLayout.CENTER);
    }

    /**
     * All UI components show data from a Upcoming instance.
     *
     * @return A Upcoming object.
     */
    public Upcoming getUpcoming() {
        return (upcoming);
    }

    /**
     * All UI components show data from a Upcoming instance.
     *
     * @param u An Upcoming object.
     */
    public void setUpcoming(Upcoming u) {
        upcoming = u;

        if (u != null) {

            apply(getTitleTextField(), u.getTitle());
            apply(getSubtitleTextField(), u.getSubtitle());
            apply(getDescriptionTextArea(), u.getDescription());
            apply(getChannelTextField(), u.getChannelNumber() + " "
                + u.getChannelName());
            apply(getStartTextField(), u.getStart());
            apply(getDurationTextField(), u.getDuration());
            apply(getPriorityTextField(), u.getPriority());
            apply(getStatusTextField(), u.getStatus());
            apply(getRecorderNameTextField(), u.getRecorderName());

        } else {

            apply(getTitleTextField(), null);
            apply(getSubtitleTextField(), null);
            apply(getDescriptionTextArea(), null);
            apply(getChannelTextField(), null);
            apply(getStartTextField(), null);
            apply(getDurationTextField(), null);
            apply(getPriorityTextField(), null);
            apply(getStatusTextField(), null);
            apply(getRecorderNameTextField(), null);
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

    private JTextArea getDescriptionTextArea() {
        return (descriptionTextArea);
    }

    private void setDescriptionTextArea(JTextArea ta) {
        descriptionTextArea = ta;
    }

    private JTextField getChannelTextField() {
        return (channelTextField);
    }

    private void setChannelTextField(JTextField tf) {
        channelTextField = tf;
    }

    private JTextField getStartTextField() {
        return (startTextField);
    }

    private void setStartTextField(JTextField tf) {
        startTextField = tf;
    }

    private JTextField getDurationTextField() {
        return (durationTextField);
    }

    private void setDurationTextField(JTextField tf) {
        durationTextField = tf;
    }

    private JTextField getPriorityTextField() {
        return (priorityTextField);
    }

    private void setPriorityTextField(JTextField tf) {
        priorityTextField = tf;
    }

    private JTextField getStatusTextField() {
        return (statusTextField);
    }

    private void setStatusTextField(JTextField tf) {
        statusTextField = tf;
    }

    private JTextField getRecorderNameTextField() {
        return (recorderNameTextField);
    }

    private void setRecorderNameTextField(JTextField tf) {
        recorderNameTextField = tf;
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

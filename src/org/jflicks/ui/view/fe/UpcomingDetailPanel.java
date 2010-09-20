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
package org.jflicks.ui.view.fe;

import java.awt.Dimension;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import org.jflicks.tv.Upcoming;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports Labels in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class UpcomingDetailPanel extends BaseCustomizePanel {

    private Upcoming upcoming;
    private JXLabel titleLabel;
    private JXLabel subtitleLabel;
    private JXLabel descriptionLabel;
    private JXLabel channelNumberNameLabel;
    private JXLabel priorityLabel;
    private JXLabel startLabel;
    private JXLabel durationLabel;
    private JXLabel recorderNameLabel;
    private JXLabel statusLabel;

    /**
     * Simple empty constructor.
     */
    public UpcomingDetailPanel() {

        JXLabel title = new JXLabel();
        title.setFont(getLargeFont());
        title.setTextAlignment(JXLabel.TextAlignment.LEFT);
        title.setForeground(getSelectedColor());
        setTitleLabel(title);

        JXLabel channelNumberName = new JXLabel();
        channelNumberName.setFont(getSmallFont());
        channelNumberName.setTextAlignment(JXLabel.TextAlignment.LEFT);
        channelNumberName.setForeground(getSelectedColor());
        setChannelNumberNameLabel(channelNumberName);

        JXLabel description = new JXLabel();
        description.setFont(getSmallFont());
        description.setTextAlignment(JXLabel.TextAlignment.LEFT);
        description.setVerticalAlignment(SwingConstants.TOP);
        description.setForeground(getSelectedColor());
        description.setLineWrap(true);
        setDescriptionLabel(description);

        JXLabel priority = new JXLabel();
        priority.setFont(getSmallFont());
        priority.setTextAlignment(JXLabel.TextAlignment.RIGHT);
        priority.setForeground(getSelectedColor());
        setPriorityLabel(priority);

        JXLabel start = new JXLabel();
        start.setFont(getSmallFont());
        start.setTextAlignment(JXLabel.TextAlignment.RIGHT);
        start.setForeground(getSelectedColor());
        setStartLabel(start);

        JXLabel duration = new JXLabel();
        duration.setFont(getSmallFont());
        duration.setTextAlignment(JXLabel.TextAlignment.RIGHT);
        duration.setForeground(getSelectedColor());
        setDurationLabel(duration);

        JXLabel recorderName = new JXLabel();
        recorderName.setFont(getSmallFont());
        recorderName.setTextAlignment(JXLabel.TextAlignment.RIGHT);
        recorderName.setForeground(getSelectedColor());
        setRecorderNameLabel(recorderName);

        JXLabel status = new JXLabel();
        status.setFont(getSmallFont());
        status.setTextAlignment(JXLabel.TextAlignment.RIGHT);
        status.setForeground(getSelectedColor());
        setStatusLabel(status);

        MattePainter mpainter = new MattePainter(getPanelColor());
        setBackgroundPainter(mpainter);
    }

    /**
     * We display information about an Upcoming.
     *
     * @return An Upcoming instance.
     */
    public Upcoming getUpcoming() {
        return (upcoming);
    }

    /**
     * We display information about an Upcoming.
     *
     * @param u An Upcoming instance.
     */
    public void setUpcoming(Upcoming u) {

        upcoming = u;
        if (upcoming != null) {

            JXLabel l = getTitleLabel();
            if (l != null) {

                l.setText(u.getTitle());
            }

            l = getChannelNumberNameLabel();
            if (l != null) {

                l.setText(u.getChannelNumber() + " " + u.getChannelName());
            }

            l = getDescriptionLabel();
            if (l != null) {

                String sub = u.getSubtitle();
                String desc = u.getDescription();
                StringBuilder sb = new StringBuilder();
                if (sub != null) {

                    sb.append("\"");
                    sb.append(sub);
                    sb.append("\" ");
                }

                if (desc != null) {

                    sb.append(desc);
                }

                l.setText(sb.toString());
            }

            l = getPriorityLabel();
            if (l != null) {

                l.setText(u.getPriority() + " Priority");
            }

            l = getStartLabel();
            if (l != null) {

                l.setText(u.getStart());
            }

            l = getDurationLabel();
            if (l != null) {

                l.setText(u.getDuration());
            }

            l = getRecorderNameLabel();
            if (l != null) {

                String tmp = u.getRecorderName();
                if (tmp != null) {
                    l.setText("Recorder: " + u.getRecorderName());
                } else {
                    l.setText("Recorder: none");
                }
            }

            l = getStatusLabel();
            if (l != null) {

                l.setText("Status: " + u.getStatus());
            }

        } else {

            JXLabel l = getTitleLabel();
            if (l != null) {

                l.setText("");
            }

            l = getChannelNumberNameLabel();
            if (l != null) {

                l.setText("");
            }

            l = getDescriptionLabel();
            if (l != null) {

                l.setText("");
            }

            l = getPriorityLabel();
            if (l != null) {

                l.setText("");
            }

            l = getStartLabel();
            if (l != null) {

                l.setText("");
            }

            l = getDurationLabel();
            if (l != null) {

                l.setText("");
            }

            l = getRecorderNameLabel();
            if (l != null) {

                l.setText("Recorder: ");
            }

            l = getStatusLabel();
            if (l != null) {

                l.setText("Status: ");
            }
        }
    }

    private JXLabel getTitleLabel() {
        return (titleLabel);
    }

    private void setTitleLabel(JXLabel l) {
        titleLabel = l;
    }

    private JXLabel getSubtitleLabel() {
        return (subtitleLabel);
    }

    private void setSubtitleLabel(JXLabel l) {
        subtitleLabel = l;
    }

    private JXLabel getDescriptionLabel() {
        return (descriptionLabel);
    }

    private void setDescriptionLabel(JXLabel l) {
        descriptionLabel = l;
    }

    private JXLabel getChannelNumberNameLabel() {
        return (channelNumberNameLabel);
    }

    private void setChannelNumberNameLabel(JXLabel l) {
        channelNumberNameLabel = l;
    }

    private JXLabel getPriorityLabel() {
        return (priorityLabel);
    }

    private void setPriorityLabel(JXLabel l) {
        priorityLabel = l;
    }

    private JXLabel getStartLabel() {
        return (startLabel);
    }

    private void setStartLabel(JXLabel l) {
        startLabel = l;
    }

    private JXLabel getDurationLabel() {
        return (durationLabel);
    }

    private void setDurationLabel(JXLabel l) {
        durationLabel = l;
    }

    private JXLabel getRecorderNameLabel() {
        return (recorderNameLabel);
    }

    private void setRecorderNameLabel(JXLabel l) {
        recorderNameLabel = l;
    }

    private JXLabel getStatusLabel() {
        return (statusLabel);
    }

    private void setStatusLabel(JXLabel l) {
        statusLabel = l;
    }

    /**
     * {@inheritDoc}
     */
    public void performControl() {
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {

        JLayeredPane pane = getLayeredPane();
        JXLabel title = getTitleLabel();
        JXLabel description = getDescriptionLabel();
        JXLabel chanNumberName = getChannelNumberNameLabel();
        JXLabel priority = getPriorityLabel();
        JXLabel start = getStartLabel();
        JXLabel duration = getDurationLabel();
        JXLabel recorderName = getRecorderNameLabel();
        JXLabel status = getStatusLabel();
        if ((d != null) && (pane != null) && (title != null)
            && (chanNumberName != null) && (description != null)
            && (priority != null) && (start != null) && (duration != null)
            && (recorderName != null) && (status != null)) {

            double width = d.getWidth();
            double height = d.getHeight();

            double halfWidth = width / 2.0;
            double twoThirdWidth = width * 0.67;
            double oneThirdWidth = width - twoThirdWidth;
            double titleHeight = height * 0.2;
            double chanNumberNameHeight = height * 0.2;
            double descriptionHeight = height * 0.6;

            title.setBounds(0, 0, (int) halfWidth, (int) titleHeight);
            chanNumberName.setBounds(0, (int) titleHeight, (int) halfWidth,
                (int) chanNumberNameHeight);
            description.setBounds(0, (int) (titleHeight + chanNumberNameHeight),
                (int) twoThirdWidth, (int) descriptionHeight);

            priority.setBounds((int) twoThirdWidth, 0,
                (int) oneThirdWidth, (int) titleHeight);
            start.setBounds((int) twoThirdWidth, (int) titleHeight,
                (int) oneThirdWidth, (int) titleHeight);
            duration.setBounds((int) twoThirdWidth, (int) (titleHeight * 2.0),
                (int) oneThirdWidth, (int) titleHeight);
            recorderName.setBounds((int) twoThirdWidth,
                (int) (titleHeight * 3.0),
                (int) oneThirdWidth, (int) titleHeight);
            status.setBounds((int) twoThirdWidth, (int) (titleHeight * 4.0),
                (int) oneThirdWidth, (int) titleHeight);

            pane.add(title, Integer.valueOf(100));
            pane.add(chanNumberName, Integer.valueOf(100));
            pane.add(description, Integer.valueOf(100));
            pane.add(priority, Integer.valueOf(100));
            pane.add(start, Integer.valueOf(100));
            pane.add(duration, Integer.valueOf(100));
            pane.add(recorderName, Integer.valueOf(100));
            pane.add(status, Integer.valueOf(100));
        }
    }

}


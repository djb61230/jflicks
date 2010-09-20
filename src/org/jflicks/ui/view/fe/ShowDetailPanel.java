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
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import org.jflicks.tv.Channel;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports Labels in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ShowDetailPanel extends BaseCustomizePanel {

    private Channel channel;
    private ShowAiring showAiring;
    private String recordingStatus;
    private JXLabel titleLabel;
    private JXLabel descriptionLabel;
    private JXLabel channelNumberNameLabel;
    private JXLabel episodeLabel;
    private JXLabel originalAirDateLabel;
    private JXLabel recordingStatusLabel;
    private StringBuffer stringBuffer;
    private FieldPosition fieldPosition;
    private SimpleDateFormat simpleDateFormat;

    /**
     * Simple empty constructor.
     */
    public ShowDetailPanel() {

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

        JXLabel episode = new JXLabel();
        episode.setFont(getSmallFont());
        episode.setTextAlignment(JXLabel.TextAlignment.LEFT);
        episode.setForeground(getSelectedColor());
        setEpisodeLabel(episode);

        JXLabel original = new JXLabel();
        original.setFont(getSmallFont());
        original.setTextAlignment(JXLabel.TextAlignment.LEFT);
        original.setForeground(getSelectedColor());
        setOriginalAirDateLabel(original);

        JXLabel status = new JXLabel();
        status.setFont(getSmallFont());
        status.setTextAlignment(JXLabel.TextAlignment.LEFT);
        status.setVerticalAlignment(SwingConstants.TOP);
        status.setForeground(getSelectedColor());
        status.setLineWrap(true);
        setRecordingStatusLabel(status);

        MattePainter mpainter = new MattePainter(getPanelColor());
        setBackgroundPainter(mpainter);

        setStringBuffer(new StringBuffer());
        setFieldPosition(new FieldPosition(0));
        setSimpleDateFormat(new SimpleDateFormat("MMM d, yyyy"));
    }

    /**
     * We display information about a Channel.
     *
     * @return A Channel instance.
     */
    public Channel getChannel() {
        return (channel);
    }

    /**
     * We display information about a Channel.
     *
     * @param c A Channel instance.
     */
    public void setChannel(Channel c) {

        channel = c;
        if (channel != null) {

            JXLabel l = getChannelNumberNameLabel();
            if (l != null) {

                l.setText(c.getNumber() + " " + c.getName());
            }

        } else {

            JXLabel l = getChannelNumberNameLabel();
            if (l != null) {

                l.setText("");
            }
        }
    }

    /**
     * We display information about a ShowAiring.
     *
     * @return A ShowAiring instance.
     */
    public ShowAiring getShowAiring() {
        return (showAiring);
    }

    /**
     * We display information about a ShowAiring.
     *
     * @param sa A ShowAiring instance.
     */
    public void setShowAiring(ShowAiring sa) {

        showAiring = sa;
        if (showAiring != null) {

            Show s = showAiring.getShow();
            if (s != null) {

                JXLabel l = getTitleLabel();
                if (l != null) {

                    l.setText(s.getTitle());
                }

                l = getDescriptionLabel();
                if (l != null) {

                    String sub = s.getSubtitle();
                    String desc = s.getDescription();
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

                l = getEpisodeLabel();
                if (l != null) {

                    String tmp = s.getEpisodeNumber();
                    if (tmp != null) {
                        l.setText("Episode: " + tmp);
                    } else {
                        l.setText("");
                    }
                }

                l = getOriginalAirDateLabel();
                StringBuffer sb = getStringBuffer();
                FieldPosition fp = getFieldPosition();
                SimpleDateFormat sdf = getSimpleDateFormat();
                if ((l != null) && (sb != null) && (fp != null)
                    && (sdf != null)) {

                    Date tmp = s.getOriginalAirDate();
                    if (tmp != null) {

                        sb.setLength(0);
                        sdf.format(tmp, sb, fp);
                        l.setText("Original Air Date: " + sb.toString());

                    } else {
                        l.setText("");
                    }
                }
            }

        } else {

            JXLabel l = getTitleLabel();
            if (l != null) {

                l.setText("");
            }

            l = getDescriptionLabel();
            if (l != null) {

                l.setText("");
            }

            l = getEpisodeLabel();
            if (l != null) {

                l.setText("");
            }

            l = getOriginalAirDateLabel();
            if (l != null) {

                l.setText("");
            }
        }
    }

    /**
     * We display information about a ShowAiring whether it's recording.
     *
     * @return A String instance.
     */
    public String getRecordingStatus() {
        return (recordingStatus);
    }

    /**
     * We display information about a ShowAiring whether it's recording.
     *
     * @param s A String instance.
     */
    public void setRecordingStatus(String s) {

        recordingStatus = s;
        if (s != null) {

            JXLabel l = getRecordingStatusLabel();
            if (l != null) {

                l.setText(s);
            }

        } else {

            JXLabel l = getRecordingStatusLabel();
            if (l != null) {

                l.setText("");
            }
        }
    }

    private JXLabel getTitleLabel() {
        return (titleLabel);
    }

    private void setTitleLabel(JXLabel l) {
        titleLabel = l;
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

    private JXLabel getEpisodeLabel() {
        return (episodeLabel);
    }

    private void setEpisodeLabel(JXLabel l) {
        episodeLabel = l;
    }

    private JXLabel getOriginalAirDateLabel() {
        return (originalAirDateLabel);
    }

    private void setOriginalAirDateLabel(JXLabel l) {
        originalAirDateLabel = l;
    }

    private JXLabel getRecordingStatusLabel() {
        return (recordingStatusLabel);
    }

    private void setRecordingStatusLabel(JXLabel l) {
        recordingStatusLabel = l;
    }

    private StringBuffer getStringBuffer() {
        return (stringBuffer);
    }

    private void setStringBuffer(StringBuffer sb) {
        stringBuffer = sb;
    }

    private FieldPosition getFieldPosition() {
        return (fieldPosition);
    }

    private SimpleDateFormat getSimpleDateFormat() {
        return (simpleDateFormat);
    }

    private void setSimpleDateFormat(SimpleDateFormat sdf) {
        simpleDateFormat = sdf;
    }

    private void setFieldPosition(FieldPosition fp) {
        fieldPosition = fp;
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
        JXLabel episode = getEpisodeLabel();
        JXLabel original = getOriginalAirDateLabel();
        JXLabel status = getRecordingStatusLabel();
        if ((d != null) && (pane != null) && (title != null)
            && (chanNumberName != null) && (description != null)
            && (episode != null) && (original != null) && (status != null)) {

            double width = d.getWidth();
            double height = d.getHeight();

            double halfWidth = width / 2.0;
            double twoThirdWidth = width * 0.67;
            double oneThirdWidth = width - twoThirdWidth;
            double titleHeight = height * 0.2;
            double chanNumberNameHeight = height * 0.2;
            double descriptionHeight = height * 0.6;
            double episodeHeight = height * 0.2;
            double originalHeight = height * 0.2;
            double statusHeight = (height * 0.2) * 2;

            title.setBounds(0, 0, (int) twoThirdWidth, (int) titleHeight);
            chanNumberName.setBounds(0, (int) titleHeight, (int) halfWidth,
                (int) chanNumberNameHeight);
            description.setBounds(0,
                (int) (titleHeight + chanNumberNameHeight),
                (int) twoThirdWidth, (int) descriptionHeight);
            episode.setBounds((int) twoThirdWidth, (int) titleHeight,
                (int) oneThirdWidth, (int) episodeHeight);
            original.setBounds((int) twoThirdWidth,
                (int) (titleHeight + episodeHeight),
                (int) oneThirdWidth, (int) originalHeight);
            status.setBounds((int) twoThirdWidth,
                (int) (titleHeight + episodeHeight + originalHeight),
                (int) oneThirdWidth, (int) statusHeight);

            pane.add(title, Integer.valueOf(100));
            pane.add(chanNumberName, Integer.valueOf(100));
            pane.add(description, Integer.valueOf(100));
            pane.add(episode, Integer.valueOf(100));
            pane.add(original, Integer.valueOf(100));
            pane.add(status, Integer.valueOf(100));
        }
    }

}


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
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Recording;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports Labels in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordingDetailPanel extends BaseCustomizePanel {

    private Recording recording;
    private JXLabel titleLabel;
    private JXLabel channelDateLabel;
    private JXLabel descriptionLabel;
    private JXLabel recordingNowLabel;
    private JXLabel flaggedLabel;
    private JXLabel audioLabel;
    private JXLabel videoLabel;
    private ImageIcon recordingBlankImageIcon;
    private ImageIcon dolby20ImageIcon;
    private ImageIcon dolby51ImageIcon;
    private ImageIcon video1080ImageIcon;
    private ImageIcon video720ImageIcon;
    private ImageIcon videoFlaggedImageIcon;
    private ImageIcon videoRecordingImageIcon;

    /**
     * Simple empty constructor.
     */
    public RecordingDetailPanel() {

        setRecordingBlankImageIcon(
            new ImageIcon(getClass().getResource("recording_blank.png")));
        setDolby20ImageIcon(
            new ImageIcon(getClass().getResource("dolby20.png")));
        setDolby51ImageIcon(
            new ImageIcon(getClass().getResource("dolby51.png")));
        setVideo1080ImageIcon(
            new ImageIcon(getClass().getResource("video_1080.png")));
        setVideo720ImageIcon(
            new ImageIcon(getClass().getResource("video_720.png")));
        setVideoFlaggedImageIcon(
            new ImageIcon(getClass().getResource("video_flagged.png")));
        setVideoRecordingImageIcon(
            new ImageIcon(getClass().getResource("video_recording.png")));

        JXLabel title = new JXLabel();
        title.setFont(getLargeFont());
        title.setTextAlignment(JXLabel.TextAlignment.LEFT);
        title.setForeground(getSelectedColor());
        setTitleLabel(title);

        JXLabel channelDate = new JXLabel();
        channelDate.setFont(getSmallFont());
        channelDate.setTextAlignment(JXLabel.TextAlignment.LEFT);
        channelDate.setForeground(getSelectedColor());
        setChannelDateLabel(channelDate);

        JXLabel description = new JXLabel();
        description.setFont(getSmallFont());
        description.setTextAlignment(JXLabel.TextAlignment.LEFT);
        description.setVerticalAlignment(SwingConstants.TOP);
        description.setForeground(getSelectedColor());
        description.setLineWrap(true);
        setDescriptionLabel(description);

        JXLabel recnow = new JXLabel(getRecordingBlankImageIcon());
        setRecordingNowLabel(recnow);

        JXLabel flagged = new JXLabel(getRecordingBlankImageIcon());
        setFlaggedLabel(flagged);

        JXLabel audio = new JXLabel(getRecordingBlankImageIcon());
        setAudioLabel(audio);

        JXLabel video = new JXLabel(getRecordingBlankImageIcon());
        setVideoLabel(video);

        MattePainter mpainter = new MattePainter(getPanelColor());
        setBackgroundPainter(mpainter);
    }

    /**
     * We display information about a Recording.
     *
     * @return A Recording instance.
     */
    public Recording getRecording() {
        return (recording);
    }

    /**
     * We display information about a Recording.
     *
     * @param r A Recording instance.
     */
    public void setRecording(Recording r) {

        recording = r;
        if (recording != null) {

            if (r.isCurrentlyRecording()) {

                getRecordingNowLabel().setIcon(getVideoRecordingImageIcon());

            } else {

                getRecordingNowLabel().setIcon(getRecordingBlankImageIcon());
            }

            if (r.getCommercials() != null) {

                getFlaggedLabel().setIcon(getVideoFlaggedImageIcon());

            } else {

                getFlaggedLabel().setIcon(getRecordingBlankImageIcon());
            }

            switch (r.getVideoFormat()) {
            default:
                getVideoLabel().setIcon(getRecordingBlankImageIcon());
                break;

            case NMSConstants.VIDEO_1080I:
                getVideoLabel().setIcon(getVideo1080ImageIcon());
                break;

            case NMSConstants.VIDEO_720P:
                getVideoLabel().setIcon(getVideo720ImageIcon());
                break;
            }

            switch (r.getAudioFormat()) {
            default:
                getAudioLabel().setIcon(getRecordingBlankImageIcon());
                break;

            case NMSConstants.AUDIO_DOLBY_DIGITAL_5_1:
                getAudioLabel().setIcon(getDolby51ImageIcon());
                break;

            case NMSConstants.AUDIO_DOLBY_DIGITAL_2_0:
                getAudioLabel().setIcon(getDolby20ImageIcon());
                break;
            }

            JXLabel l = getTitleLabel();
            if (l != null) {

                l.setText(r.getTitle());
            }

            l = getChannelDateLabel();
            Date d = r.getDate();
            if ((l != null) && (d != null)) {

                l.setText(d.toString());
            }

            l = getDescriptionLabel();
            if (l != null) {

                String sub = r.getSubtitle();
                String desc = r.getDescription();
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
                l.repaint();
            }

        } else {

            getRecordingNowLabel().setIcon(getRecordingBlankImageIcon());
            getFlaggedLabel().setIcon(getRecordingBlankImageIcon());
            getVideoLabel().setIcon(getRecordingBlankImageIcon());
            getAudioLabel().setIcon(getRecordingBlankImageIcon());

            JXLabel l = getTitleLabel();
            if (l != null) {

                l.setText("");
            }

            l = getChannelDateLabel();
            if (l != null) {

                l.setText("");
            }

            l = getDescriptionLabel();
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

    private JXLabel getChannelDateLabel() {
        return (channelDateLabel);
    }

    private void setChannelDateLabel(JXLabel l) {
        channelDateLabel = l;
    }

    private JXLabel getDescriptionLabel() {
        return (descriptionLabel);
    }

    private void setDescriptionLabel(JXLabel l) {
        descriptionLabel = l;
    }

    private JXLabel getRecordingNowLabel() {
        return (recordingNowLabel);
    }

    private void setRecordingNowLabel(JXLabel l) {
        recordingNowLabel = l;
    }

    private JXLabel getFlaggedLabel() {
        return (flaggedLabel);
    }

    private void setFlaggedLabel(JXLabel l) {
        flaggedLabel = l;
    }

    private JXLabel getAudioLabel() {
        return (audioLabel);
    }

    private void setAudioLabel(JXLabel l) {
        audioLabel = l;
    }

    private JXLabel getVideoLabel() {
        return (videoLabel);
    }

    private void setVideoLabel(JXLabel l) {
        videoLabel = l;
    }

    private ImageIcon getRecordingBlankImageIcon() {
        return (recordingBlankImageIcon);
    }

    private void setRecordingBlankImageIcon(ImageIcon ii) {
        recordingBlankImageIcon = ii;
    }

    private ImageIcon getDolby20ImageIcon() {
        return (dolby20ImageIcon);
    }

    private void setDolby20ImageIcon(ImageIcon ii) {
        dolby20ImageIcon = ii;
    }

    private ImageIcon getDolby51ImageIcon() {
        return (dolby51ImageIcon);
    }

    private void setDolby51ImageIcon(ImageIcon ii) {
        dolby51ImageIcon = ii;
    }

    private ImageIcon getVideo1080ImageIcon() {
        return (video1080ImageIcon);
    }

    private void setVideo1080ImageIcon(ImageIcon ii) {
        video1080ImageIcon = ii;
    }

    private ImageIcon getVideo720ImageIcon() {
        return (video720ImageIcon);
    }

    private void setVideo720ImageIcon(ImageIcon ii) {
        video720ImageIcon = ii;
    }

    private ImageIcon getVideoFlaggedImageIcon() {
        return (videoFlaggedImageIcon);
    }

    private void setVideoFlaggedImageIcon(ImageIcon ii) {
        videoFlaggedImageIcon = ii;
    }

    private ImageIcon getVideoRecordingImageIcon() {
        return (videoRecordingImageIcon);
    }

    private void setVideoRecordingImageIcon(ImageIcon ii) {
        videoRecordingImageIcon = ii;
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
        JXLabel chanDate = getChannelDateLabel();
        JXLabel description = getDescriptionLabel();
        JXLabel recnow = getRecordingNowLabel();
        JXLabel flagged = getFlaggedLabel();
        JXLabel audio = getAudioLabel();
        JXLabel video = getVideoLabel();
        if ((d != null) && (pane != null) && (title != null)
            && (chanDate != null) && (description != null)
            && (recnow != null) && (flagged != null)
            && (audio != null) && (video != null)) {

            double width = d.getWidth();
            double height = d.getHeight();
            double gap = height * 0.1;

            double halfWidth = width / 2.0;
            double titleHeight = height * 0.2;
            double chanDateHeight = height * 0.2;
            double descriptionHeight = height * 0.6;

            title.setBounds(0, 0, (int) halfWidth, (int) titleHeight);
            chanDate.setBounds(0, (int) titleHeight, (int) halfWidth,
                (int) chanDateHeight);
            description.setBounds(0, (int) (titleHeight + chanDateHeight),
                (int) width, (int) descriptionHeight);

            pane.add(title, Integer.valueOf(100));
            pane.add(chanDate, Integer.valueOf(100));
            pane.add(description, Integer.valueOf(100));

            // The ImageIcon labels...we assume all the same size...
            Dimension labdim = recnow.getPreferredSize();
            if (labdim != null) {

                double span = (gap + labdim.getWidth());
                double x = width - span;
                double y = gap;
                video.setBounds((int) x, (int) y, (int) labdim.getWidth(),
                    (int) labdim.getHeight());
                pane.add(video, Integer.valueOf(100));

                x -= span;
                audio.setBounds((int) x, (int) y, (int) labdim.getWidth(),
                    (int) labdim.getHeight());
                pane.add(audio, Integer.valueOf(100));

                x -= span;
                flagged.setBounds((int) x, (int) y, (int) labdim.getWidth(),
                    (int) labdim.getHeight());
                pane.add(flagged, Integer.valueOf(100));

                x -= span;
                recnow.setBounds((int) x, (int) y, (int) labdim.getWidth(),
                    (int) labdim.getHeight());
                pane.add(recnow, Integer.valueOf(100));
            }
        }
    }

}


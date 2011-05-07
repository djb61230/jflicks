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

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.Video;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports Labels in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class VideoDetailPanel extends BaseCustomizePanel {

    private Video video;
    private JXLabel titleLabel;
    private JXLabel releasedLabel;
    private JXLabel descriptionLabel;
    private JXLabel timeLabel;
    private JXLabel aspectRatioLabel;

    /**
     * Simple empty constructor.
     */
    public VideoDetailPanel() {

        JXLabel title = new JXLabel();
        title.setFont(getLargeFont());
        title.setTextAlignment(JXLabel.TextAlignment.LEFT);
        title.setForeground(getSelectedColor());
        setTitleLabel(title);

        JXLabel released = new JXLabel();
        released.setFont(getSmallFont());
        released.setTextAlignment(JXLabel.TextAlignment.LEFT);
        released.setForeground(getSelectedColor());
        setReleasedLabel(released);

        JXLabel description = new JXLabel();
        description.setFont(getSmallFont());
        description.setTextAlignment(JXLabel.TextAlignment.LEFT);
        description.setVerticalAlignment(SwingConstants.TOP);
        description.setForeground(getSelectedColor());
        description.setLineWrap(true);
        setDescriptionLabel(description);

        JXLabel time = new JXLabel();
        time.setFont(getSmallFont());
        time.setTextAlignment(JXLabel.TextAlignment.LEFT);
        time.setForeground(getSelectedColor());
        setTimeLabel(time);

        JXLabel aspect = new JXLabel();
        aspect.setFont(getSmallFont());
        aspect.setTextAlignment(JXLabel.TextAlignment.LEFT);
        aspect.setForeground(getSelectedColor());
        setAspectRatioLabel(aspect);
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

        JXLabel title = getTitleLabel();
        JXLabel released = getReleasedLabel();
        JXLabel description = getDescriptionLabel();
        JXLabel time = getTimeLabel();
        JXLabel aspect = getAspectRatioLabel();
        JLayeredPane pane = getLayeredPane();
        if ((d != null) && (title != null) && (released != null)
            && (description != null) && (time != null) && (aspect != null)
            && (pane != null)) {

            double width = d.getWidth();
            double height = d.getHeight();

            double halfWidth = width / 2.0;
            double threeQuarterWidth = width * 0.75;
            double oneQuarterWidth = width - threeQuarterWidth;

            double titleHeight = height * 0.2;
            double releasedHeight = height * 0.2;
            double descriptionHeight = height * 0.6;

            title.setBounds(0, 0, (int) threeQuarterWidth, (int) titleHeight);
            released.setBounds(0, (int) titleHeight, (int) halfWidth,
                (int) releasedHeight);
            description.setBounds(0, (int) (titleHeight + releasedHeight),
                (int) width, (int) descriptionHeight);
            time.setBounds((int) threeQuarterWidth, 0, (int) oneQuarterWidth,
                (int) titleHeight);
            aspect.setBounds((int) threeQuarterWidth, (int) titleHeight,
                (int) oneQuarterWidth, (int) titleHeight);

            pane.add(title, Integer.valueOf(100));
            pane.add(released, Integer.valueOf(100));
            pane.add(description, Integer.valueOf(100));
            pane.add(time, Integer.valueOf(100));
            pane.add(aspect, Integer.valueOf(100));

            Color back = getPanelColor();
            back = new Color(back.getRed(), back.getGreen(),
                back.getBlue(), (int) (getPanelAlpha() * 255));
            setPanelColor(back);
            MattePainter mpainter = new MattePainter(getPanelColor());
            setBackgroundPainter(mpainter);
        }
    }

    /**
     * We display information about a Video.
     *
     * @return A Video instance.
     */
    public Video getVideo() {
        return (video);
    }

    /**
     * We display information about a Video.
     *
     * @param v A Video instance.
     */
    public void setVideo(Video v) {

        video = v;
        if (video != null) {

            JXLabel l = getTitleLabel();
            if (l != null) {

                if (v.isTV()) {

                    l.setText(v.getTitle() + " Season " + v.getSeason()
                        + " Episode " + v.getEpisode());

                } else {

                    l.setText(v.getTitle());
                }
            }

            l = getReleasedLabel();
            if (l != null) {

                l.setText(v.getReleased());
            }

            l = getDescriptionLabel();
            if (l != null) {

                l.setText(v.getDescription());
            }

            l = getTimeLabel();
            if (l != null) {

                long duration = v.getDuration();
                if (duration != 0) {

                    long hours = duration / 3600;
                    long mins = (duration - (hours * 3600)) / 60;
                    if (mins < 10) {

                        if (hours > 0) {

                            l.setText("Time " + hours + ":0" + mins);

                        } else {

                            long secs = (duration - (mins * 60));
                            if (secs > 0) {

                                l.setText("Time " + mins + " min " + secs
                                    + " sec");

                            } else {

                                l.setText("Time " + mins + " min");
                            }
                        }

                    } else {

                        if (hours > 0) {

                            l.setText("Time " + hours + ":" + mins);

                        } else {

                            long secs = (duration - (mins * 60));
                            if (secs > 0) {

                                l.setText("Time " + mins + " minutes " + secs
                                    + " seconds");

                            } else {

                                l.setText("Time " + mins + " minutes");
                            }
                        }
                    }

                } else {

                    l.setText("");
                }
            }

            l = getAspectRatioLabel();
            if (l != null) {

                String ar = v.getAspectRatio();
                if (ar == null) {

                    ar = NMSConstants.ASPECT_RATIO_16X9;
                }

                l.setText("Aspect Ratio " + ar);
            }

        } else {

            JXLabel l = getTitleLabel();
            if (l != null) {

                l.setText("");
            }

            l = getReleasedLabel();
            if (l != null) {

                l.setText("");
            }

            l = getDescriptionLabel();
            if (l != null) {

                l.setText("");
            }

            l = getTimeLabel();
            if (l != null) {

                l.setText("");
            }

            l = getAspectRatioLabel();
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

    private JXLabel getReleasedLabel() {
        return (releasedLabel);
    }

    private void setReleasedLabel(JXLabel l) {
        releasedLabel = l;
    }

    private JXLabel getDescriptionLabel() {
        return (descriptionLabel);
    }

    private void setDescriptionLabel(JXLabel l) {
        descriptionLabel = l;
    }

    private JXLabel getTimeLabel() {
        return (timeLabel);
    }

    private void setTimeLabel(JXLabel l) {
        timeLabel = l;
    }

    private JXLabel getAspectRatioLabel() {
        return (aspectRatioLabel);
    }

    private void setAspectRatioLabel(JXLabel l) {
        aspectRatioLabel = l;
    }

}


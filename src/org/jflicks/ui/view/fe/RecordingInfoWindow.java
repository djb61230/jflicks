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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLayeredPane;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.jflicks.imagecache.ImageCache;
import org.jflicks.player.Player;
import org.jflicks.player.PlayState;
import org.jflicks.tv.Recording;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This is our "banner" window showing the state of the currently running
 * video.  It uses the DetailPanel to display information about the
 * recording and draws the "poster" for the recording on the back side.
 *
 * The banner is not transparent to allow video bits to come through as
 * a Java window functionality for this is not available until Java 7, or
 * at least not in a public API.  The second issue with it is that
 * "compositing" needs to be enabled to use it under Linux which may
 * clash with VDPAU.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordingInfoWindow extends JWindow implements ActionListener {

    private static final double HGAP = 0.02;
    private static final double VGAP = 0.02;

    private JXPanel panel;
    private JXLabel titleLabel;
    private JXLabel channelDateLabel;
    private JXLabel descriptionLabel;
    private TimelinePanel timelinePanel;
    private Recording recording;
    private Player player;
    private int seconds;
    private ImageCache imageCache;
    private Timer timer;
    private int currentSeconds;
    private SimpleDateFormat dateFormat;

    /**
     * Simple constructor with our required arguments.
     *
     * @param r The Rectangle defining the location of the main window.
     * use the "poster" image without scaling it ugly.
     * @param seconds the number of seconds to leave the banner visible.
     * @param normal The text color to match the theme.
     * @param backlight The background color to match the theme.
     * @param alpha The translucent level for the window so the back image
     * shows through.
     * @param small A small font to use.
     * @param large A large font to use.
     */
    public RecordingInfoWindow(Rectangle r, int seconds, Color normal,
        Color backlight, float alpha, Font small, Font large) {

        setCursor(Util.getNoCursor());
        setSeconds(seconds);

        int loffset = (int) (r.width * 0.05);
        int toffset = (int) (r.height * 0.05);
        int width = r.width - (2 * loffset);
        int height = (int) (width / 5.4);

        setBounds(loffset + r.x, toffset + r.y, width, height);

        double hgap = width * HGAP;
        double vgap = height * VGAP;

        JXPanel p = new JXPanel(new BorderLayout());
        setPanel(p);

        JXPanel top = new JXPanel(new BorderLayout());
        top.setOpaque(false);
        top.setAlpha(alpha);
        Color copy = new Color(backlight.getRed(), backlight.getGreen(),
            backlight.getBlue(), (int) (alpha * 255));
        MattePainter mpainter = new MattePainter(copy);
        top.setBackgroundPainter(mpainter);
        p.add(top, BorderLayout.CENTER);

        JLayeredPane pane = new JLayeredPane();
        top.add(pane, BorderLayout.CENTER);

        JXLabel title = new JXLabel();
        title.setFont(large);
        title.setTextAlignment(JXLabel.TextAlignment.LEFT);
        title.setForeground(normal);
        setTitleLabel(title);

        JXLabel channelDate = new JXLabel();
        channelDate.setFont(small);
        channelDate.setTextAlignment(JXLabel.TextAlignment.LEFT);
        channelDate.setForeground(normal);
        setChannelDateLabel(channelDate);

        JXLabel description = new JXLabel();
        description.setFont(small);
        description.setTextAlignment(JXLabel.TextAlignment.LEFT);
        description.setVerticalAlignment(SwingConstants.TOP);
        description.setForeground(normal);
        description.setLineWrap(true);
        setDescriptionLabel(description);

        TimelinePanel tp = new TimelinePanel();
        setTimelinePanel(tp);

        ClockPanel cpanel = new ClockPanel(large, normal, backlight, alpha);
        cpanel.setOpaque(false);

        double halfWidth = ((double) width) / 2.0;
        double titleHeight = ((double) height) * 0.2;
        double chanDateHeight = ((double) height) * 0.2;
        double descriptionHeight = ((double) height) * 0.4;
        double timelineHeight = ((double) height) * 0.2 - vgap;

        title.setBounds((int) hgap, (int) vgap, (int) halfWidth,
            (int) titleHeight);
        channelDate.setBounds((int) hgap,
            (int) (titleHeight + vgap), (int) halfWidth,
            (int) chanDateHeight);
        description.setBounds((int) hgap,
            (int) (vgap + titleHeight + chanDateHeight),
            (int) (width - hgap * 2.0), (int) descriptionHeight);
        tp.setBounds((int) hgap,
            (int) (vgap + titleHeight + chanDateHeight + descriptionHeight),
            (int) (width - hgap * 2.0), (int) timelineHeight);

        pane.add(title, Integer.valueOf(100));
        pane.add(channelDate, Integer.valueOf(100));
        pane.add(description, Integer.valueOf(100));
        pane.add(tp, Integer.valueOf(100));

        Dimension cpdim = cpanel.getPreferredSize();
        if (cpdim != null) {

            double x = width - cpdim.getWidth() - hgap - ClockPanel.FUDGE;
            cpanel.setBounds((int) x, (int) vgap,
                (int) (cpdim.getWidth() + ClockPanel.FUDGE),
                (int) cpdim.getHeight());
            pane.add(cpanel, Integer.valueOf(120));
        }

        add(p);
        Timer t = new Timer(1000, this);
        setTimer(t);

        setDateFormat(new SimpleDateFormat("EEE MMM d h:mm aaa"));
    }

    private JXPanel getPanel() {
        return (panel);
    }

    private void setPanel(JXPanel p) {
        panel = p;
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

    private TimelinePanel getTimelinePanel() {
        return (timelinePanel);
    }

    private void setTimelinePanel(TimelinePanel p) {
        timelinePanel = p;
    }

    private int getSeconds() {
        return (seconds);
    }

    private void setSeconds(int i) {
        seconds = i;
    }

    private SimpleDateFormat getDateFormat() {
        return (dateFormat);
    }

    private void setDateFormat(SimpleDateFormat df) {
        dateFormat = df;
    }

    private String formatDate(SimpleDateFormat df, Date d) {

        String result = null;

        if (df != null) {

            if (d != null) {

                StringBuffer sb = new StringBuffer();
                df.format(d, sb, new FieldPosition(0));
                result = sb.toString();
            }

        } else {

            if (d != null) {

                result = d.toString();
            }
        }

        return (result);
    }

    /**
     * We use the ImageCache service to get poster images to display.
     *
     * @return An ImageCache instance.
     */
    public ImageCache getImageCache() {
        return (imageCache);
    }

    /**
     * We use the ImageCache service to get poster images to display.
     *
     * @param ic An ImageCache instance.
     */
    public void setImageCache(ImageCache ic) {
        imageCache = ic;
    }

    private Timer getTimer() {
        return (timer);
    }

    private void setTimer(Timer t) {
        timer = t;
    }

    private int getCurrentSeconds() {
        return (currentSeconds);
    }

    private void setCurrentSeconds(int i) {
        currentSeconds = i;
    }

    /**
     * We need a Recording instance to be able to draw information useful
     * to the user.
     *
     * @return A given Recording instance.
     */
    public Recording getRecording() {
        return (recording);
    }

    /**
     * We need a Recording instance to be able to draw information useful
     * to the user.
     *
     * @param r A given Recording instance.
     */
    public void setRecording(Recording r) {

        recording = r;

        JXPanel p = getPanel();
        ImageCache ic = getImageCache();
        if ((r != null) && (p != null) && (ic != null)) {

            BufferedImage bi = ic.getImage(r.getBannerURL());
            if (bi != null) {

                int w = getWidth();
                if (w > bi.getWidth()) {

                    bi = Util.scaleLarger(w, bi);
                }
                ImagePainter painter = new ImagePainter(bi);
                painter.setScaleToFit(true);
                p.setBackgroundPainter(painter);

            } else {

                p.setBackgroundPainter(null);
            }
            JXLabel l = getTitleLabel();
            if (l != null) {

                l.setText(r.getTitle());
            }

            l = getChannelDateLabel();
            Date d = r.getDate();
            if ((l != null) && (d != null)) {

                l.setText(formatDate(getDateFormat(), d));
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
            }
        }
    }

    /**
     * We need to communicate with the player to get status of the playing
     * video.
     *
     * @return The Player instance.
     */
    public Player getPlayer() {
        return (player);
    }

    /**
     * We need to communicate with the player to get status of the playing
     * video.
     *
     * @param p The Player instance.
     */
    public void setPlayer(Player p) {
        player = p;
    }

    /**
     * Override so we can start a Timer to auto shut off the banner.
     *
     * @param b True if the banner is asked to be visible.
     */
    public void setVisible(boolean b) {

        setCurrentSeconds(0);
        updateWindow();

        super.setVisible(b);

        if (b) {

            Timer t = getTimer();
            if (t != null) {

                if (!t.isRunning()) {

                    t.restart();
                }
            }
        }
    }

    private void updateWindow() {

        TimelinePanel tp = getTimelinePanel();
        Player p = getPlayer();
        Recording r = getRecording();
        if ((p != null) && (tp != null) && (r != null)) {

            PlayState ps = p.getPlayState();
            if (ps != null) {

                double current = ps.getTime();
                double length = (double) r.getDuration();
                if (r.isCurrentlyRecording()) {

                    long now = System.currentTimeMillis();
                    long realstart = r.getRealStart();
                    if (realstart == 0L) {

                        length = current;

                    } else {

                        long llength = now - realstart;
                        llength /= 1000L;
                        length = (double) llength;
                    }
                }

                // Sometimes the player can't give us an accurate time.  So
                // either we display 0 (or at the beginning) or at the end.
                // It's probably more correct to be at the end...
                if (current == 0.0) {
                    current = length;
                }

                double percentage = current / length * 100.0;
                tp.setValue((int) percentage);
                tp.setCurrent((int) current);
                tp.setLength((int) length);
            }
        }
    }

    /**
     * When our timer goes off we set the visiblity to false.
     *
     * @param event The given Timer event.
     */
    public void actionPerformed(ActionEvent event) {

        int current = getCurrentSeconds() + 1;
        if (current >= getSeconds()) {

            setCurrentSeconds(0);
            setVisible(false);
            Timer t = getTimer();
            if (t != null) {
                t.stop();
            }

        } else {

            setCurrentSeconds(current);
            updateWindow();
        }
    }

}


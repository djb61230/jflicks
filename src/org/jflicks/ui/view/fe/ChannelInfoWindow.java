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
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JLayeredPane;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.jflicks.tv.Channel;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This is our "banner" window showing the state of the currently running
 * live channel.
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
public class ChannelInfoWindow extends JWindow implements ActionListener {

    private static final double HGAP = 0.02;
    private static final double VGAP = 0.02;

    private JXPanel panel;
    private JXLabel channelLabel;
    private JXLabel titleLabel;
    private JXLabel descriptionLabel;
    private int seconds;
    private Timer timer;
    private int currentSeconds;
    private Channel channel;
    private ShowAiring showAiring;

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
    public ChannelInfoWindow(Rectangle r, int seconds, Color normal,
        Color backlight, float alpha, Font small, Font large) {

        setCursor(Util.getNoCursor());
        setSeconds(seconds);

        int loffset = (int) (r.width * 0.10);
        int toffset = (int) (r.height - (r.height * 0.25));
        int width = r.width - (2 * loffset);
        int height = (int) (r.height / 5);

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

        JXLabel channelLab = new JXLabel();
        channelLab.setFont(large);
        channelLab.setTextAlignment(JXLabel.TextAlignment.LEFT);
        channelLab.setForeground(normal);
        setChannelLabel(channelLab);

        JXLabel description = new JXLabel();
        description.setFont(small);
        description.setTextAlignment(JXLabel.TextAlignment.LEFT);
        description.setVerticalAlignment(SwingConstants.TOP);
        description.setForeground(normal);
        description.setLineWrap(true);
        setDescriptionLabel(description);

        double quarterWidth = ((double) width) / 4.0;
        double titleHeight = ((double) height) * 0.2;
        double chanHeight = ((double) height) * 0.2;
        double descHeight = ((double) height) * 0.8;
        double chanTop = (((double) height) - chanHeight) / 2.0;

        channelLab.setBounds((int) hgap, (int) chanTop,
            (int) quarterWidth, (int) chanHeight);
        title.setBounds((int) (hgap + quarterWidth), (int) vgap,
            (int) (quarterWidth * 4), (int) titleHeight);
        description.setBounds((int) (hgap + quarterWidth),
            (int) (vgap + titleHeight + vgap),
            (int) (quarterWidth * 4), (int) descHeight);

        pane.add(channelLab, Integer.valueOf(100));
        pane.add(title, Integer.valueOf(100));
        pane.add(description, Integer.valueOf(100));

        add(p);
        Timer t = new Timer(1000, this);
        setTimer(t);
    }

    private JXPanel getPanel() {
        return (panel);
    }

    private void setPanel(JXPanel p) {
        panel = p;
    }

    private JXLabel getChannelLabel() {
        return (channelLabel);
    }

    private void setChannelLabel(JXLabel l) {
        channelLabel = l;
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

    private int getSeconds() {
        return (seconds);
    }

    private void setSeconds(int i) {
        seconds = i;
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
     * We need a Channel instance to be able to draw information useful
     * to the user.
     *
     * @return A given Channel instance.
     */
    public Channel getChannel() {
        return (channel);
    }

    /**
     * We need a Channel instance to be able to draw information useful
     * to the user.
     *
     * @param c A given Channel instance.
     */
    public void setChannel(Channel c) {

        channel = c;

        JXPanel p = getPanel();
        if ((c != null) && (p != null)) {

            JXLabel l = getChannelLabel();
            if (l != null) {

                l.setText(c.toString());
            }
        }
    }

    /**
     * We need a ShowAiring instance to be able to draw information useful
     * to the user.
     *
     * @return A given ShowAiring instance.
     */
    public ShowAiring getShowAiring() {
        return (showAiring);
    }

    /**
     * We need a ShowAiring instance to be able to draw information useful
     * to the user.
     *
     * @param sa A given ShowAiring instance.
     */
    public void setShowAiring(ShowAiring sa) {

        showAiring = sa;

        JXPanel p = getPanel();
        if ((sa != null) && (p != null)) {

            Show s = sa.getShow();
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
            }
        }
    }

    /**
     * Override so we can start a Timer to auto shut off the banner.
     *
     * @param b True if the banner is asked to be visible.
     */
    public void setVisible(boolean b) {

        setCurrentSeconds(0);

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
        }
    }

}


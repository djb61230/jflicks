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
import java.util.Date;
import javax.swing.JLayeredPane;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.jflicks.imagecache.ImageCache;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This is our "banner" window showing the state of the currently running
 * screen that is playing either Web or set top box media.  It simply
 * displays basic text as there is not a way to really identify the playing
 * media since the control has been passed to the browser or set top box.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SimpleInfoWindow extends JWindow implements ActionListener {

    private static final double HGAP = 0.02;
    private static final double VGAP = 0.02;

    private JXPanel panel;
    private JXLabel titleLabel;
    private JXLabel descriptionLabel;
    private String title;
    private String description;
    private BufferedImage bannerBufferedImage;
    private int seconds;
    private Timer timer;
    private int currentSeconds;

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
    public SimpleInfoWindow(Rectangle r, int seconds, Color normal,
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

        JXLabel description = new JXLabel();
        description.setFont(small);
        description.setTextAlignment(JXLabel.TextAlignment.LEFT);
        description.setVerticalAlignment(SwingConstants.TOP);
        description.setForeground(normal);
        description.setLineWrap(true);
        setDescriptionLabel(description);

        ClockPanel cpanel = new ClockPanel(large, normal, backlight, alpha);
        cpanel.setOpaque(false);

        double halfWidth = ((double) width) / 2.0;
        double titleHeight = ((double) height) * 0.2;
        double chanDateHeight = ((double) height) * 0.2;
        double descriptionHeight = ((double) height) * 0.4;
        double timelineHeight = ((double) height) * 0.2 - vgap;

        title.setBounds((int) hgap, (int) vgap, (int) halfWidth,
            (int) titleHeight);
        description.setBounds((int) hgap,
            (int) (vgap + titleHeight + chanDateHeight),
            (int) (width - hgap * 2.0), (int) descriptionHeight);

        pane.add(title, Integer.valueOf(100));
        pane.add(description, Integer.valueOf(100));

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
     * We need a title property to display to the user.
     *
     * @return A String instance.
     */
    public String getTitle() {
        return (title);
    }

    /**
     * We need a title property to display to the user.
     *
     * @param s A String instance.
     */
    public void setTitle(String s) {

        title = s;

        JXPanel p = getPanel();
        if ((s != null) && (p != null)) {

            JXLabel l = getTitleLabel();
            if (l != null) {

                l.setText(s);
            }
        }
    }

    /**
     * We can display a banner image if the property is set.
     *
     * @return A BufferedImage instance.
     */
    public BufferedImage getBannerBufferedImage() {
        return (bannerBufferedImage);
    }

    /**
     * We can display a banner image if the property is set.
     *
     * @param bi A BufferedImage instance.
     */
    public void setBannerBufferedImage(BufferedImage bi) {

        bannerBufferedImage = bi;

        JXPanel p = getPanel();
        if (p != null) {

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
        }
    }

    /**
     * We need a description property to display to the user.
     *
     * @return A String instance.
     */
    public String getDescription() {
        return (description);
    }

    /**
     * We need a description property to display to the user.
     *
     * @param s A String instance.
     */
    public void setDescription(String s) {

        description = s;

        JXPanel p = getPanel();
        if ((s != null) && (p != null)) {

            JXLabel l = getDescriptionLabel();
            if (l != null) {

                l.setText(s);
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


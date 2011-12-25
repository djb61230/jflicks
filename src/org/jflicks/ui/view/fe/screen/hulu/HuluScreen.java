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
package org.jflicks.ui.view.fe.screen.hulu;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.rc.RC;
import org.jflicks.ui.view.fe.FrontEndView;
import org.jflicks.ui.view.fe.SimpleInfoWindow;
import org.jflicks.ui.view.fe.screen.PlayerScreen;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports Hulu in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class HuluScreen extends PlayerScreen implements PropertyChangeListener {

    private SimpleInfoWindow simpleInfoWindow;

    /**
     * Simple empty constructor.
     */
    public HuluScreen() {

        setTitle("Hulu");
        BufferedImage bi = getImageByName("Hulu");
        setDefaultBackgroundImage(bi);
    }

    private SimpleInfoWindow getSimpleInfoWindow() {
        return (simpleInfoWindow);
    }

    private void setSimpleInfoWindow(SimpleInfoWindow w) {
        simpleInfoWindow = w;
    }

    /**
     * Override so we can start up the player.
     *
     * @param b When true we are ready to start the player.
     */
    public void setVisible(boolean b) {

        super.setVisible(b);
        if (b) {

            SimpleInfoWindow w = getSimpleInfoWindow();
            if (w != null) {

                w.setTitle("Huludesktop");
                w.setDescription("You are watching Hulu via the Huludesktop");
            }

            // Just start up the player!
            Player p = getPlayer();
            RC rc = getRC();
            if ((rc != null) && (p != null) && (!p.isPlaying())) {

                rc.setGuideKeyEvent(KeyEvent.VK_ESCAPE);
                rc.setPauseKeyEvent(KeyEvent.VK_SPACE);
                p.addPropertyChangeListener("Completed", this);
                p.play((java.lang.String[]) null);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {

        JLayeredPane pane = getLayeredPane();
        if ((d != null) && (pane != null)) {

            FrontEndView fev = (FrontEndView) getView();
            SimpleInfoWindow siv = new SimpleInfoWindow(fev.getPosition(), 8,
                getInfoColor(), getPanelColor(), (float) getPanelAlpha(),
                getSmallFont(), getMediumFont());

            BufferedImage bi = null;

            try {

                bi = ImageIO.read(getClass().getResource("banner.png"));

            } catch (IOException ex) {

                log(INFO, "Failed to load banner image");
            }

            siv.setBannerBufferedImage(bi);
            setSimpleInfoWindow(siv);

            JXPanel panel = new JXPanel(new BorderLayout());
            JXLabel l = new JXLabel("Launching Huludesktop, please wait...");
            l.setHorizontalTextPosition(SwingConstants.CENTER);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setFont(getLargeFont());
            panel.add(l, BorderLayout.CENTER);
            MattePainter p = new MattePainter(Color.BLACK);
            panel.setBackgroundPainter(p);
            panel.setBounds(0, 0, (int) d.getWidth(), (int) d.getHeight());

            pane.add(panel, Integer.valueOf(100));
        }
    }

    /**
     * {@inheritDoc}
     */
    public Bookmark createBookmark() {

        Bookmark result = null;

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public String getBookmarkId() {

        String result = null;

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void info() {

        SimpleInfoWindow w = getSimpleInfoWindow();
        if (w != null) {

            w.setVisible(!w.isVisible());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void guide() {
    }

    /**
     * {@inheritDoc}
     */
    public void pageup() {
    }

    /**
     * {@inheritDoc}
     */
    public void pagedown() {
    }

    /**
     * {@inheritDoc}
     */
    public void close() {

        SimpleInfoWindow w = getSimpleInfoWindow();
        if (w != null) {

            w.setVisible(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void rewind() {
    }

    /**
     * {@inheritDoc}
     */
    public void forward() {
    }

    /**
     * {@inheritDoc}
     */
    public void skipforward() {
    }

    /**
     * {@inheritDoc}
     */
    public void skipbackward() {
    }

    /**
     * {@inheritDoc}
     */
    public void up() {
    }

    /**
     * {@inheritDoc}
     */
    public void down() {
    }

    /**
     * {@inheritDoc}
     */
    public void left() {
    }

    /**
     * {@inheritDoc}
     */
    public void right() {
    }

    /**
     * {@inheritDoc}
     */
    public void enter() {
    }

    /**
     * Screens that use Plaers usually need to listen for action events from
     * a user selected button but we just launch chrome.
     *
     * @param event A given ActionEvent instance.
     */
    public void actionPerformed(ActionEvent event) {
    }

    /**
     * Listen for the Player being "done".  This signifies the video finished
     * by coming to the end.
     *
     * @param event A given PropertyChangeEvent.
     */
    public void propertyChange(PropertyChangeEvent event) {

        if ((event.getSource() == getPlayer()) && (!isDone())) {

            log(DEBUG, "Player set update");

            // If we get this property update, then it means the video
            // finished playing on it's own.
            Boolean bobj = (Boolean) event.getNewValue();
            if (bobj.booleanValue()) {

                getPlayer().removePropertyChangeListener(this);
                setDone(true);

                RC rc = getRC();
                if (rc != null) {

                    rc.setGuideKeyEvent(rc.getDefaultGuideKeyEvent());
                    rc.setPauseKeyEvent(rc.getDefaultPauseKeyEvent());
                    rc.setKeyboardControl(true);
                }
            }
        }
    }

}

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
package org.jflicks.ui.view.fe.screen.dvd;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JLayeredPane;

import org.jflicks.mvc.View;
import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.rc.RC;
import org.jflicks.ui.view.JFlicksView;
import org.jflicks.ui.view.fe.screen.PlayerScreen;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports Videos in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DVDScreen extends PlayerScreen implements PropertyChangeListener {

    /**
     * Simple empty constructor.
     */
    public DVDScreen() {

        setTitle("DVD");
        BufferedImage bi = getImageByName("DVD");
        setDefaultBackgroundImage(bi);
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {

        JLayeredPane pane = getLayeredPane();
        if ((d != null) && (pane != null)) {

            JXPanel panel = new JXPanel();
            MattePainter p = new MattePainter(Color.BLACK);
            panel.setBackgroundPainter(p);
            panel.setBounds(0, 0, (int) d.getWidth(), (int) d.getHeight());

            pane.add(panel, Integer.valueOf(100));
        }
    }

    /**
     * Override so we can start up the player.
     *
     * @param b When true we are ready to start the player.
     */
    public void setVisible(boolean b) {

        super.setVisible(b);
        if (b) {

            // Just need to start the player.
            RC rc = getRC();
            Player p = getPlayer();
            View v = getView();
            if ((rc != null) && (p != null) && (v != null)) {

                rc.setEventControl(true);
                rc.setMouseControl(false);
                rc.setKeyboardControl(false);
                p.addPropertyChangeListener("Playing", this);
                p.play(v.getProperty(JFlicksView.DVD_DEVICE_PROPERTY));
            }
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
    }

    /**
     * {@inheritDoc}
     */
    public void guide() {

        LogUtil.log(LogUtil.DEBUG, "DVDScreen guide");

        Player p = getPlayer();
        if (p != null) {

            p.guide();
        }
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

        LogUtil.log(LogUtil.DEBUG, "DVDScreen close");
        RC rc = getRC();
        if (rc != null) {

            rc.setEventControl(true);
            rc.setMouseControl(false);
            rc.setKeyboardControl(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void rewind() {

        LogUtil.log(LogUtil.DEBUG, "DVDScreen rewind");
        Player p = getPlayer();
        if (p != null) {

            p.seek(-8);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void forward() {

        LogUtil.log(LogUtil.DEBUG, "DVDScreen forward");
        Player p = getPlayer();
        if (p != null) {

            p.seek(30);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void skipforward() {

        LogUtil.log(LogUtil.DEBUG, "DVDScreen skipforward");
        Player p = getPlayer();
        if (p != null) {

            p.next();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void skipbackward() {

        LogUtil.log(LogUtil.DEBUG, "DVDScreen skipbackward");
        Player p = getPlayer();
        if (p != null) {

            p.previous();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void up() {

        LogUtil.log(LogUtil.DEBUG, "DVDScreen up");

        Player p = getPlayer();
        if (p != null) {

            p.up();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void down() {

        LogUtil.log(LogUtil.DEBUG, "DVDScreen down");

        Player p = getPlayer();
        if (p != null) {

            p.down();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void left() {

        LogUtil.log(LogUtil.DEBUG, "DVDScreen left");

        Player p = getPlayer();
        if (p != null) {

            p.left();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void right() {

        LogUtil.log(LogUtil.DEBUG, "DVDScreen right");

        Player p = getPlayer();
        if (p != null) {

            p.right();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void enter() {

        LogUtil.log(LogUtil.DEBUG, "DVDScreen enter");

        Player p = getPlayer();
        if (p != null) {

            p.enter();
        }
    }

    /**
     * Listen for the Player being "done".  This signifies the video finished
     * by coming to the end.
     *
     * @param event A given PropertyChangeEvent.
     */
    public void propertyChange(PropertyChangeEvent event) {

        if ((event.getSource() == getPlayer()) && (!isDone())) {

            // If we get this property update, then it means the video
            // finished playing on it's own.
            Boolean bobj = (Boolean) event.getNewValue();
            if (!bobj.booleanValue()) {

                getPlayer().removePropertyChangeListener(this);
                setDone(true);
            }
        }
    }

    /**
     * We don't actually listen for anything but need to implement because
     * we extend PlayerScreen.  It's designed to listen for buttons like
     * "Play", "Play from Bookmark" etc but since we only start mplayer we
     * don't need to listen.
     *
     * @param event An ActionEvent instance.
     */
    public void actionPerformed(ActionEvent event) {
    }

}


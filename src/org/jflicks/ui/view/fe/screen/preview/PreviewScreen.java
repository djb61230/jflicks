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
package org.jflicks.ui.view.fe.screen.preview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.JLayeredPane;

import org.jflicks.mvc.View;
import org.jflicks.nms.NMS;
import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.ui.view.fe.FrontEndView;
import org.jflicks.ui.view.fe.NMSProperty;
import org.jflicks.ui.view.fe.screen.PlayerScreen;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports Videos in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class PreviewScreen extends PlayerScreen implements NMSProperty,
    PropertyChangeListener {

    private NMS[] nms;

    /**
     * Simple empty constructor.
     */
    public PreviewScreen() {

        setTitle("Previews");
        BufferedImage bi = getImageByName("Previews");
        setDefaultBackgroundImage(bi);
    }

    /**
     * {@inheritDoc}
     */
    public NMS[] getNMS() {

        NMS[] result = null;

        if (nms != null) {

            result = Arrays.copyOf(nms, nms.length);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void setNMS(NMS[] array) {

        if (array != null) {
            nms = Arrays.copyOf(array, array.length);
        } else {
            nms = null;
        }
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

    private String[] computeURLs() {

        String[] result = null;

        NMS[] array = getNMS();
        if ((array != null) && (array.length > 0)) {

            ArrayList<String> intros = new ArrayList<String>();
            ArrayList<String> urls = new ArrayList<String>();
            for (int i = 0; i < array.length; i++) {

                String[] uarray = array[i].getTrailerURLs();
                if ((uarray != null) && (uarray.length > 1)) {

                    for (int j = 0; j < uarray.length; j++) {

                        if (j == 0) {

                            intros.add(uarray[j]);

                        } else {

                            urls.add(uarray[j]);
                        }
                    }
                }
            }

            if (urls.size() > 0) {

                // At this point we should have two lists, one with intros one
                // with actual movie trailers.  We have to turn it into one
                // list.
                ArrayList<String> l = new ArrayList<String>();
                if (intros.size() > 0) {

                    l.add(intros.get(0));
                }

                for (int i = 0; i < urls.size(); i++) {

                    l.add(urls.get(i));
                }

                result = l.toArray(new String[l.size()]);
            }
        }

        return (result);
    }

    /**
     * Override so we can start up the player.
     *
     * @param b When true we are ready to start the player.
     */
    public void setVisible(boolean b) {

        super.setVisible(b);
        if (b) {

            String[] urls = computeURLs();
            if ((urls != null) && (urls.length > 0)) {

                Player p = getPlayer();
                if (p != null) {

                    View v = getView();
                    if (v instanceof FrontEndView) {

                        FrontEndView fev = (FrontEndView) v;
                        p.setRectangle(fev.getPosition());
                    }

                    p.addPropertyChangeListener("Playing", this);
                    controlKeyboard(false);
                    p.setFrame(Util.findFrame(this));
                    p.play(urls);
                }
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

        controlKeyboard(true);
    }

    /**
     * {@inheritDoc}
     */
    public void rewind() {

        Player p = getPlayer();
        if (p != null) {

            p.seek(-8);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void forward() {

        Player p = getPlayer();
        if (p != null) {

            p.seek(30);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void skipforward() {

        Player p = getPlayer();
        if (p != null) {

            p.next();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void skipbackward() {

        Player p = getPlayer();
        if (p != null) {

            p.previous();
        }
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

        Player p = getPlayer();
        if (p != null) {

            p.seek(-8);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void right() {

        Player p = getPlayer();
        if (p != null) {

            p.seek(30);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void enter() {
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


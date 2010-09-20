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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.JLayeredPane;

import org.jflicks.nms.NMS;
import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.ui.view.fe.NMSProperty;
import org.jflicks.ui.view.fe.screen.PlayerScreen;

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

    private String trailerPath;
    private String previewPath;
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

        if ((nms != null) && (nms.length > 0)) {

            for (int i = 0; i < nms.length; i++) {

                String thome = nms[i].getTrailerHome();
                String ppath = nms[i].getTrailerIntro();
                if ((thome != null) && (ppath != null)) {

                    File fthome = new File(thome);
                    File fppath = new File(ppath);
                    if ((fthome.exists()) && (fppath.exists())) {

                        // Just take the first config we find...
                        setTrailerPath(thome);
                        setPreviewPath(ppath);
                        break;
                    }
                }
            }
        }
    }

    private String getTrailerPath() {
        return (trailerPath);
    }

    private void setTrailerPath(String s) {
        trailerPath = s;
    }

    private String getPreviewPath() {
        return (previewPath);
    }

    private void setPreviewPath(String s) {
        previewPath = s;
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

            // Make a play list file.
            String tpath = getTrailerPath();
            String ppath = getPreviewPath();
            if ((tpath != null) && (ppath != null)) {

                File dir = new File(tpath);
                File[] all = dir.listFiles();
                if (all != null) {

                    boolean success = true;
                    Arrays.sort(all, new FileSort());

                    try {

                        FileWriter fw = new FileWriter("preview.txt");
                        String line = ppath + "\n";
                        fw.write(line, 0, line.length());
                        for (int i = 0; i < all.length; i++) {

                            line = all[i].getPath() + "\n";
                            fw.write(line, 0, line.length());
                        }

                        fw.close();

                    } catch (IOException ex) {

                        success = false;
                    }

                    // Just need to start the player.
                    if (success) {

                        Player p = getPlayer();
                        if (p != null) {

                            p.addPropertyChangeListener("Playing", this);
                            p.play("-playlist preview.txt");
                        }
                    }
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
    public void close() {
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

    static class FileSort implements Comparator<File>, Serializable {

        public int compare(File f0, File f1) {

            Long l0 = Long.valueOf(f0.lastModified());
            Long l1 = Long.valueOf(f1.lastModified());

            return (l1.compareTo(l0));
        }
    }

}


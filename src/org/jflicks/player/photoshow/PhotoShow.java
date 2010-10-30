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
package org.jflicks.player.photoshow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

import org.jflicks.player.BasePlayer;
import org.jflicks.player.Bookmark;
import org.jflicks.player.PlayState;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This Player will display photos in a JFrame.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class PhotoShow extends BasePlayer implements ActionListener {

    private JFrame frame;
    private JXPanel panel;
    private Timer timer;
    private File[] photoFiles;
    private int currentIndex;

    /**
     * Simple constructor.
     */
    public PhotoShow() {

        setType(PLAYER_SLIDESHOW);
        setTitle("PhotoShow");
    }

    private JFrame getFrame() {
        return (frame);
    }

    private void setFrame(JFrame f) {
        frame = f;
    }

    private JXPanel getPanel() {
        return (panel);
    }

    private void setPanel(JXPanel p) {
        panel = p;
    }

    private Timer getTimer() {
        return (timer);
    }

    private void setTimer(Timer t) {
        timer = t;
    }

    private File[] getPhotoFiles() {
        return (photoFiles);
    }

    private void setPhotoFiles(File[] array) {
        photoFiles = array;
    }

    private int getCurrentIndex() {
        return (currentIndex);
    }

    private void setCurrentIndex(int i) {
        currentIndex = i;
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsPause() {
        return (true);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsAutoSkip() {
        return (false);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsSeek() {
        return (false);
    }

    /**
     * {@inheritDoc}
     */
    public void play(String url) {

        play(url, null);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void play(String url, Bookmark b) {

        if (!isPlaying()) {

            setAudioOffset(0);
            setPaused(false);
            setPlaying(true);
            setCompleted(false);

            String[] lines = Util.readTextFile(new File(url));
            if (lines != null) {

                pathsToFiles(lines);
                setCurrentIndex(0);

                Rectangle r = null;
                if (isFullscreen()) {

                    r = getFullscreenRectangle();

                } else {

                    r = getRectangle();
                }

                int x = (int) r.getX();
                int y = (int) r.getY();
                int width = (int) r.getWidth();
                int height = (int) r.getHeight();

                JXPanel p = new JXPanel();
                p.setOpaque(false);
                p.setBounds(x, y, width, height);
                setPanel(p);

                JXPanel backp = new JXPanel(new BorderLayout());
                backp.setOpaque(false);
                backp.setBackgroundPainter(new MattePainter(Color.BLACK));
                backp.add(p, BorderLayout.CENTER);

                Cursor cursor = Util.getNoCursor();
                JFrame f = new JFrame();

                f.setAlwaysOnTop(true);
                f.setUndecorated(true);
                f.setBounds(x, y, width, height);
                f.add(backp);
                f.requestFocus();
                if (cursor != null) {
                    f.getContentPane().setCursor(cursor);
                }
                setFrame(f);

                p.setFocusable(true);
                p.requestFocus();
                InputMap map = p.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

                InfoAction infoAction = new InfoAction();
                map.put(KeyStroke.getKeyStroke("I"), "i");
                p.getActionMap().put("i", infoAction);

                QuitAction quitAction = new QuitAction();
                map.put(KeyStroke.getKeyStroke("Q"), "q");
                p.getActionMap().put("q", quitAction);

                setMessage(null);

                Timer t = new Timer(5000, this);
                t.setInitialDelay(500);
                setTimer(t);
                t.start();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        System.out.println("stop called!");
        setPaused(false);
        setPlaying(false);
        setCompleted(true);

        JFrame w = getFrame();
        if (w != null) {

            w.setVisible(false);
            w.dispose();
            setFrame(null);
        }

        Timer t = getTimer();
        if (t != null) {

            t.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void pause(boolean b) {

        setPaused(b);
    }

    /**
     * {@inheritDoc}
     */
    public void seek(int seconds) {
    }

    /**
     * {@inheritDoc}
     */
    public void next() {
    }

    /**
     * {@inheritDoc}
     */
    public void previous() {
    }

    /**
     * {@inheritDoc}
     */
    public void audiosync(double offset) {
    }

    /**
     * {@inheritDoc}
     */
    public PlayState getPlayState() {

        PlayState result = null;

        return (result);
    }

    /**
     * Time to switch to the next photo.
     *
     * @param event A given ActionEvent instance.
     */
    public void actionPerformed(ActionEvent event) {

        if ((isPlaying()) && (!isPaused())) {

            JFrame w = getFrame();
            JXPanel p = getPanel();
            File file = getNextFile();
            if ((w != null) && (p != null) && (file != null)) {

                try {

                    BufferedImage bi = ImageIO.read(file);
                    ImagePainter painter =
                        (ImagePainter) p.getBackgroundPainter();
                    if (painter != null) {

                        painter.setImage(bi);

                    } else {

                        painter = new ImagePainter(bi);
                        painter.setScaleToFit(true);
                        p.setBackgroundPainter(painter);
                    }

                    p.repaint();

                    if (!w.isVisible()) {

                        w.setVisible(true);
                    }

                } catch (IOException ex) {
                }
            }
        }
    }

    private void pathsToFiles(String[] array) {

        if ((array != null) && (array.length > 0)) {

            File[] farray = new File[array.length];
            for (int i = 0; i < array.length; i++) {

                farray[i] = new File(array[i]);
            }

            setPhotoFiles(farray);
        }
    }

    private File getNextFile() {

        File result = null;

        File[] files = getPhotoFiles();
        if ((files != null) && (files.length > 0)) {

            int index = getCurrentIndex();
            if ((index >= 0) && (index < files.length)) {

                result = files[index];

            } else {

                setCurrentIndex(0);
                result = files[0];
            }

            setCurrentIndex(getCurrentIndex() + 1);
        }

        return (result);
    }

}


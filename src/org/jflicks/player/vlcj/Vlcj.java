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
package org.jflicks.player.vlcj;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.Timer;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import org.jflicks.player.BasePlayer;
import org.jflicks.player.Bookmark;
import org.jflicks.player.PlayState;
import org.jflicks.util.Util;

/**
 * This player uses the vlcj library to embed vlc.  It current supports
 * streaming UDP packets.  It can play files but does not do well with
 * transport streams.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Vlcj extends BasePlayer {

    private JDialog dialog;
    private MediaPlayerFactory mediaPlayerFactory;
    private EmbeddedMediaPlayer embeddedMediaPlayer;
    private JPanel keyPanel;
    private Canvas canvas;

    /**
     * Simple constructor.
     */
    public Vlcj() {

        setType(PLAYER_VIDEO_STREAM_UDP);
        setTitle("Vlcj");

        JPanel pan = new JPanel(new BorderLayout());
        pan.setFocusable(true);
        InputMap map = pan.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        QuitAction quitAction = new QuitAction();
        map.put(KeyStroke.getKeyStroke("Q"), "q");
        pan.getActionMap().put("q", quitAction);

        InfoAction infoAction = new InfoAction();
        map.put(KeyStroke.getKeyStroke("I"), "i");
        pan.getActionMap().put("i", infoAction);

        UpAction upAction = new UpAction();
        map.put(KeyStroke.getKeyStroke("UP"), "up");
        pan.getActionMap().put("up", upAction);

        DownAction downAction = new DownAction();
        map.put(KeyStroke.getKeyStroke("DOWN"), "down");
        pan.getActionMap().put("down", downAction);

        LeftAction leftAction = new LeftAction();
        map.put(KeyStroke.getKeyStroke("LEFT"), "left");
        pan.getActionMap().put("left", leftAction);

        RightAction rightAction = new RightAction();
        map.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        pan.getActionMap().put("right", rightAction);

        EnterAction enterAction = new EnterAction();
        map.put(KeyStroke.getKeyStroke("ENTER"), "enter");
        pan.getActionMap().put("enter", enterAction);

        GuideAction guideAction = new GuideAction();
        map.put(KeyStroke.getKeyStroke("G"), "g");
        pan.getActionMap().put("g", guideAction);

        PauseAction pauseAction = new PauseAction();
        map.put(KeyStroke.getKeyStroke("P"), "p");
        pan.getActionMap().put("p", pauseAction);

        PageUpAction pageupAction = new PageUpAction();
        map.put(KeyStroke.getKeyStroke("PAGE_UP"), "pageup");
        pan.getActionMap().put("pageup", pageupAction);

        PageDownAction pagedownAction = new PageDownAction();
        map.put(KeyStroke.getKeyStroke("PAGE_DOWN"), "pagedown");
        pan.getActionMap().put("pagedown", pagedownAction);

        RewindAction rewindAction = new RewindAction();
        map.put(KeyStroke.getKeyStroke("R"), "r");
        pan.getActionMap().put("r", rewindAction);

        ForwardAction forwardAction = new ForwardAction();
        map.put(KeyStroke.getKeyStroke("F"), "f");
        pan.getActionMap().put("f", forwardAction);

        SkipBackwardAction skipBackwardAction = new SkipBackwardAction();
        map.put(KeyStroke.getKeyStroke("Z"), "z");
        pan.getActionMap().put("z", skipBackwardAction);

        SkipForwardAction skipForwardAction = new SkipForwardAction();
        map.put(KeyStroke.getKeyStroke("X"), "x");
        pan.getActionMap().put("x", skipForwardAction);

        AudioSyncPlusAction audioSyncPlusAction = new AudioSyncPlusAction();
        map.put(KeyStroke.getKeyStroke("N"), "n");
        pan.getActionMap().put("n", audioSyncPlusAction);

        AudioSyncMinusAction audioSyncMinusAction = new AudioSyncMinusAction();
        map.put(KeyStroke.getKeyStroke("M"), "m");
        pan.getActionMap().put("m", audioSyncMinusAction);

        Canvas can = new Canvas();
        can.setBackground(Color.BLACK);
        pan.add(can, BorderLayout.CENTER);

        setKeyPanel(pan);
        setCanvas(can);
    }

    private JDialog getDialog() {
        return (dialog);
    }

    private void setDialog(JDialog d) {
        dialog = d;
    }

    private JPanel getKeyPanel() {
        return (keyPanel);
    }

    private void setKeyPanel(JPanel p) {
        keyPanel = p;
    }

    private Canvas getCanvas() {
        return (canvas);
    }

    private void setCanvas(Canvas c) {
        canvas = c;
    }

    private MediaPlayerFactory getMediaPlayerFactory() {
        return (mediaPlayerFactory);
    }

    private void setMediaPlayerFactory(MediaPlayerFactory f) {
        mediaPlayerFactory = f;
    }

    private EmbeddedMediaPlayer getEmbeddedMediaPlayer() {
        return (embeddedMediaPlayer);
    }

    private void setEmbeddedMediaPlayer(EmbeddedMediaPlayer p) {
        embeddedMediaPlayer = p;
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
        return (true);
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

            Cursor cursor = Util.getNoCursor();
            JDialog win = new JDialog(getFrame());
            win.setUndecorated(true);
            win.setBounds(x, y, width, height);

            JPanel pan = getKeyPanel();

            win.add(pan, BorderLayout.CENTER);
            win.setVisible(true);

            setDialog(win);

            String[] vlcArgs = {
                "--no-video-title-show",
                "--deinterlace-mode=bob"
            };
            MediaPlayerFactory mpf = new MediaPlayerFactory(vlcArgs);
            setMediaPlayerFactory(mpf);

            EmbeddedMediaPlayer mediaPlayer = mpf.newMediaPlayer(null);
            mediaPlayer.setEnableKeyInputHandling(false);
            mediaPlayer.setEnableMouseInputHandling(false);
            mediaPlayer.setVideoSurface(getCanvas());
            setEmbeddedMediaPlayer(mediaPlayer);

            mediaPlayer.playMedia(url);
            if (b != null) {

                float ftmp = (float) b.getPosition();
                mediaPlayer.setPosition(ftmp / 1000.0f);
            }

            final JPanel fpan = pan;
            ActionListener focusPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {

                    fpan.requestFocus();
                }
            };
            Timer focusTimer = new Timer(5000, focusPerformer);
            focusTimer.setRepeats(false);
            focusTimer.start();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        System.out.println("stop called!");
        setPaused(false);
        setPlaying(false);

        EmbeddedMediaPlayer p = getEmbeddedMediaPlayer();
        if (p != null) {

            p.stop();
            p.release();
            setEmbeddedMediaPlayer(null);
        }

        MediaPlayerFactory f = getMediaPlayerFactory();
        if (f != null) {

            f.release();
            setMediaPlayerFactory(null);
        }

        JDialog w = getDialog();
        if (w != null) {

            w.setVisible(false);
            w.dispose();
            setDialog(null);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void pause(boolean b) {

        setPaused(b);
        if (getType() != PLAYER_VIDEO_STREAM_UDP) {

            EmbeddedMediaPlayer p = getEmbeddedMediaPlayer();
            if (p != null) {

                p.setPause(b);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void seek(int seconds) {

        EmbeddedMediaPlayer p = getEmbeddedMediaPlayer();
        if (p != null) {

            System.out.println("seek length: " + p.getLength());
            if (p.getLength() > 0) {

                p.skip((long) (seconds * 1000));

            } else {

                float denom = (float) getLengthHint();
                if (denom > 0.0f) {

                    float worth = (float) seconds / denom;
                    System.out.println("figuring : " + worth);
                    p.setPosition(p.getPosition() + worth);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void seekPosition(int seconds) {
    }

    /**
     * {@inheritDoc}
     */
    public void seekPosition(double percentage) {
    }

    /**
     * {@inheritDoc}
     */
    public void guide() {

        EmbeddedMediaPlayer p = getEmbeddedMediaPlayer();
        if (p != null) {

            p.menuActivate();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void up() {

        EmbeddedMediaPlayer p = getEmbeddedMediaPlayer();
        if (p != null) {

            p.menuUp();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void down() {

        EmbeddedMediaPlayer p = getEmbeddedMediaPlayer();
        if (p != null) {

            p.menuDown();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void left() {

        EmbeddedMediaPlayer p = getEmbeddedMediaPlayer();
        if (p != null) {

            p.menuLeft();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void right() {

        EmbeddedMediaPlayer p = getEmbeddedMediaPlayer();
        if (p != null) {

            p.menuRight();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void enter() {

        if (getType() == PLAYER_VIDEO_DVD) {

            EmbeddedMediaPlayer p = getEmbeddedMediaPlayer();
            if (p != null) {

                p.menuActivate();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void next() {

        EmbeddedMediaPlayer p = getEmbeddedMediaPlayer();
        if (p != null) {

            p.nextChapter();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void previous() {

        EmbeddedMediaPlayer p = getEmbeddedMediaPlayer();
        if (p != null) {

            p.previousChapter();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void audiosync(double offset) {

        EmbeddedMediaPlayer p = getEmbeddedMediaPlayer();
        if (p != null) {

            p.setAudioDelay((long) offset);
        }
    }

    /**
     * {@inheritDoc}
     */
    public PlayState getPlayState() {

        PlayState result = new PlayState();

        EmbeddedMediaPlayer p = getEmbeddedMediaPlayer();
        if (p != null) {

            if (p.getLength() > 0) {

                result.setPosition((long) (p.getPosition() * 1000.0f));
                result.setTime((double) (p.getTime() / 1000));
                result.setPaused(isPaused());
                result.setPlaying(isPlaying());

            } else {

                // Here we only have position to help us...
                long hint = getLengthHint();
                if (hint > 0L) {

                    float percent = p.getPosition();
                    result.setPosition((long) (percent * 1000.0f));
                    result.setTime((double) (percent * hint));
                    result.setPaused(isPaused());
                    result.setPlaying(isPlaying());
                }
            }
        }

        return (result);
    }

}



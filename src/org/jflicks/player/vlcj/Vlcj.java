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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.Timer;

import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;

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
    private DirectMediaPlayer directMediaPlayer;
    private ImagePanel keyPanel;
    private String[] args;
    private String[] urls;
    private int urlIndex;
    private BufferedImage bufferedImage;
    private FrameRenderCallback frameRenderCallback;


    /**
     * Simple constructor.
     */
    public Vlcj() {

        setType(PLAYER_VIDEO_STREAM_UDP);
        setTitle("Vlcj");

        ImagePanel pan = new ImagePanel();
        pan.setLayout(new BorderLayout());
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

        setKeyPanel(pan);

        String[] vlcArgs = {
            "--no-video-title-show",
            "--deinterlace-mode=bob",
        };

        setArgs(vlcArgs);
    }

    /**
     * We allow custom arguments to the VLC object.
     *
     * @return The arguments as a String array.
     */
    public String[] getArgs() {

        String[] result = null;

        if (args != null) {

            result = Arrays.copyOf(args, args.length);
        }

        return (result);
    }

    /**
     * We allow custom arguments to the VLC object.
     *
     * @param array The arguments as a String array.
     */
    public void setArgs(String[] array) {

        if (array != null) {
            args = Arrays.copyOf(array, array.length);
        } else {
            args = null;
        }
    }

    private int getUrlIndex() {
        return (urlIndex);
    }

    private void setUrlIndex(int i) {
        urlIndex = i;
    }

    private String[] getUrls() {
        return (urls);
    }

    private void setUrls(String[] array) {
        urls = array;
    }

    private JDialog getDialog() {
        return (dialog);
    }

    private void setDialog(JDialog d) {
        dialog = d;
    }

    private ImagePanel getKeyPanel() {
        return (keyPanel);
    }

    private void setKeyPanel(ImagePanel p) {
        keyPanel = p;
    }

    private MediaPlayerFactory getMediaPlayerFactory() {
        return (mediaPlayerFactory);
    }

    private void setMediaPlayerFactory(MediaPlayerFactory f) {
        mediaPlayerFactory = f;
    }

    private DirectMediaPlayer getDirectMediaPlayer() {
        return (directMediaPlayer);
    }

    private void setDirectMediaPlayer(DirectMediaPlayer p) {
        directMediaPlayer = p;
    }

    @Override
    public void setSize(Rectangle r) {

        if (r != null) {

            JDialog d = getDialog();
            if (d != null) {

                d.setBounds(r);
            }

            ImagePanel p = getKeyPanel();
            if (p != null) {

                p.setBounds(0, 0, r.width, r.height);
            }
        }
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
    public boolean supportsMaximize() {
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
    public void play(String ... urls) {

        if ((urls != null) && (urls.length > 0)) {

            if (urls.length == 1) {

                setUrlIndex(-1);
                setUrls(null);

            } else {

                setUrlIndex(0);
                setUrls(urls);
            }

            play(urls[0], null);
        }
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

            JDialog win = new JDialog(getFrame());
            win.setFocusable(true);
            win.setUndecorated(true);
            win.setBounds(x, y, width, height);
            setDialog(win);

            ImagePanel pan = getKeyPanel();
            JLayeredPane lpane = new JLayeredPane();
            setLayeredPane(lpane);
            pan.setBounds(0, 0, width, height);

            bufferedImage = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration()
                .createCompatibleImage(width, height);
            frameRenderCallback = new FrameRenderCallback(bufferedImage);

            pan.setBufferedImage(bufferedImage);

            lpane.add(pan, Integer.valueOf(100));
            win.add(lpane, BorderLayout.CENTER);

            Cursor cursor = Util.getNoCursor();
            if (cursor != null) {

                pan.setCursor(cursor);
            }

            win.setVisible(true);

            log(DEBUG, "vlc url <" + url + ">");
            String[] vlcArgs = getArgs();
            log(DEBUG, "vlcargs " + (vlcArgs != null));
            if (vlcArgs != null) {

                for (int i = 0; i < vlcArgs.length; i++) {

                    log(DEBUG, "vlcargs " + i + " " + vlcArgs[i]);
                }
            }

            MediaPlayerFactory mpf = new MediaPlayerFactory(vlcArgs);
            setMediaPlayerFactory(mpf);

            DirectMediaPlayer mediaPlayer =
                mpf.newDirectMediaPlayer(width, height, frameRenderCallback);
            mediaPlayer.addMediaPlayerEventListener(
                new MyMediaPlayerEventAdapter());
            //mediaPlayer.setPlaySubItems(true);
            setDirectMediaPlayer(mediaPlayer);

            mediaPlayer.playMedia(url);
            if (b != null) {

                float ftmp = (float) b.getPosition();
                mediaPlayer.setPosition(ftmp / 1000.0f);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        log(DEBUG, "stop called!");
        setPaused(false);
        setPlaying(false);

        DirectMediaPlayer p = getDirectMediaPlayer();
        if (p != null) {

            p.stop();
            p.release();
            setDirectMediaPlayer(null);
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
    public void maximize(boolean b) {

        setMaximized(b);
    }

    /**
     * {@inheritDoc}
     */
    public void pause(boolean b) {

        setPaused(b);
        if (getType() != PLAYER_VIDEO_STREAM_UDP) {

            DirectMediaPlayer p = getDirectMediaPlayer();
            if (p != null) {

                p.setPause(b);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void seek(int seconds) {

        DirectMediaPlayer p = getDirectMediaPlayer();
        if (p != null) {

            log(DEBUG, "seek length: " + p.getLength());
            if (p.getLength() > 0) {

                p.skip((long) (seconds * 1000));

            } else {

                float denom = (float) getLengthHint();
                if (denom > 0.0f) {

                    float worth = (float) seconds / denom;
                    log(DEBUG, "figuring : " + worth);
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

        DirectMediaPlayer p = getDirectMediaPlayer();
        if (p != null) {

            p.menuActivate();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void up() {

        DirectMediaPlayer p = getDirectMediaPlayer();
        if (p != null) {

            p.menuUp();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void down() {

        DirectMediaPlayer p = getDirectMediaPlayer();
        if (p != null) {

            p.menuDown();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void left() {

        DirectMediaPlayer p = getDirectMediaPlayer();
        if (p != null) {

            p.menuLeft();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void right() {

        DirectMediaPlayer p = getDirectMediaPlayer();
        if (p != null) {

            p.menuRight();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void enter() {

        if (getType() == PLAYER_VIDEO_DVD) {

            DirectMediaPlayer p = getDirectMediaPlayer();
            if (p != null) {

                p.menuActivate();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void next() {

        DirectMediaPlayer p = getDirectMediaPlayer();
        if (p != null) {

            p.nextChapter();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void previous() {

        DirectMediaPlayer p = getDirectMediaPlayer();
        if (p != null) {

            p.previousChapter();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void audiosync(double offset) {

        DirectMediaPlayer p = getDirectMediaPlayer();
        if (p != null) {

            // Convert to microseconds...
            offset *= 1000000;
            long loffset = (long) offset;
            long current = p.getAudioDelay();
            p.setAudioDelay(loffset + current);
            log(DEBUG, "set audiodelay to <" + (loffset + current) + ">");
        }
    }

    /**
     * {@inheritDoc}
     */
    public PlayState getPlayState() {

        PlayState result = new PlayState();

        DirectMediaPlayer p = getDirectMediaPlayer();
        if (p != null) {

            if (p.getLength() > 0) {

                result.setPosition((long) (p.getPosition() * 1000.0f));
                result.setTime((double) (p.getTime() / 1000.0));
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

    class FrameRenderCallback extends RenderCallbackAdapter {

        private BufferedImage bufferedImage;

        public FrameRenderCallback(BufferedImage image) {

            super(((DataBufferInt) image.getRaster().getDataBuffer()).getData());
            bufferedImage = image;
        }

        @Override
        public void onDisplay(DirectMediaPlayer mediaPlayer, int[] data) {

            ImagePanel p = getKeyPanel();
            if (p != null) {

                p.repaint();
            }
        }
    }

    class ImagePanel extends JPanel {

        private BufferedImage bufferedImage;

        public ImagePanel() {
        }

        public BufferedImage getBufferedImage() {
            return (bufferedImage);
        }

        public void setBufferedImage(BufferedImage bi) {
            bufferedImage = bi;
        }

        @Override
        public void paint(Graphics g) {

            Graphics2D g2 = (Graphics2D) g;
            if (bufferedImage != null) {
                g2.drawImage(bufferedImage, null, 0, 0);
            }
        }

    }

    class MyMediaPlayerEventAdapter extends MediaPlayerEventAdapter {

        public MyMediaPlayerEventAdapter() {
        }

        public void stopped(MediaPlayer mediaPlayer) {

            System.out.println("stopped");
        }

        public void finished(MediaPlayer mediaPlayer) {

            System.out.println("finished");
            String[] all = getUrls();
            if (urls != null) {

                int index = getUrlIndex() + 1;
                if (index == urls.length) {

                    setUrlIndex(-1);
                    setUrls(null);
                    setPlaying(false);
                    setCompleted(true);
                    stop();

                } else {

                    setUrlIndex(index);
                    mediaPlayer.playMedia(urls[index]);
                }

            } else {

                setPlaying(false);
                setCompleted(true);
                stop();
            }
            /*
            if (!mediaPlayer.playNextSubItem()) {

                System.out.println("finished bee");
                setPlaying(false);
                setCompleted(true);
                stop();

            } else {

                mediaPlayer.play();
            }
            */
        }

    }

}



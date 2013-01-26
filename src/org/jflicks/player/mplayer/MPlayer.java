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
package org.jflicks.player.mplayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.sun.jna.Native;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.player.BasePlayer;
import org.jflicks.player.Bookmark;
import org.jflicks.player.PlayState;
import org.jflicks.util.Util;

/**
 * This Player (with other classes in this package) is capable of
 * executing the program mplayer to play media files.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class MPlayer extends BasePlayer {

    private JDialog window;
    private JPanel keyPanel;
    private MPlayerCanvas canvas;
    private MPlayerJob mplayerJob;
    private PlayStateJob statusJob;
    private JobContainer jobContainer;
    private JobContainer statusJobContainer;
    private boolean userStop;
    private boolean forceFullscreen;
    private String programName;
    private int disposeTimeout;
    private String[] args;

    /**
     * Simple constructor.
     */
    public MPlayer() {

        setType(PLAYER_VIDEO);
        setTitle("M");
        setProgramName("mplayer");
        setDisposeTimeout(0);

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

        SapAction sapAction = new SapAction();
        map.put(KeyStroke.getKeyStroke("S"), "s");
        pan.getActionMap().put("s", sapAction);

        AudioSyncPlusAction audioSyncPlusAction = new AudioSyncPlusAction();
        map.put(KeyStroke.getKeyStroke("N"), "n");
        pan.getActionMap().put("n", audioSyncPlusAction);

        AudioSyncMinusAction audioSyncMinusAction = new AudioSyncMinusAction();
        map.put(KeyStroke.getKeyStroke("M"), "m");
        pan.getActionMap().put("m", audioSyncMinusAction);

        MPlayerCanvas can = new MPlayerCanvas();
        can.setBackground(Color.BLACK);
        can.setFocusable(true);
        pan.add(can, BorderLayout.CENTER);

        setKeyPanel(pan);
        setCanvas(can);
    }

    /**
     * We allow custom arguments to the mplayer process.
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
     * We allow custom arguments to the mplayer process.
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

    private JDialog getDialog() {
        return (window);
    }

    private void setDialog(JDialog w) {
        window = w;
    }

    private JPanel getKeyPanel() {
        return (keyPanel);
    }

    private void setKeyPanel(JPanel p) {
        keyPanel = p;
    }

    private MPlayerCanvas getCanvas() {
        return (canvas);
    }

    private void setCanvas(MPlayerCanvas c) {
        canvas = c;
    }

    private MPlayerJob getMPlayerJob() {
        return (mplayerJob);
    }

    private void setMPlayerJob(MPlayerJob j) {
        mplayerJob = j;
    }

    private PlayStateJob getPlayStateJob() {
        return (statusJob);
    }

    private void setPlayStateJob(PlayStateJob j) {
        statusJob = j;
    }

    private JobContainer getJobContainer() {
        return (jobContainer);
    }

    private void setJobContainer(JobContainer jc) {
        jobContainer = jc;
    }

    private JobContainer getPlayStateJobContainer() {
        return (statusJobContainer);
    }

    private void setPlayStateJobContainer(JobContainer jc) {
        statusJobContainer = jc;
    }

    /**
     * Flag to signify that the user stopped the video, that it didn't come
     * to it's natural end.
     *
     * @return True if the user quit.
     */
    public boolean isUserStop() {
        return (userStop);
    }

    private void setUserStop(boolean b) {
        userStop = b;
    }

    /**
     * Force mplayer to go fullscreen.
     *
     * @return True when you want mplayer to go fullscreen.
     */
    public boolean isForceFullscreen() {
        return (forceFullscreen);
    }

    /**
     * Force mplayer to go fullscreen.
     *
     * @param b True when you want mplayer to go fullscreen.
     */
    public void setForceFullscreen(boolean b) {
        forceFullscreen = b;
    }

    /**
     * The program name to run.  This allows use to use mplayer or
     * mplayer2.
     *
     * @return The program name property.
     */
    public String getProgramName() {
        return (programName);
    }

    /**
     * The program name to run.  This allows use to use mplayer or
     * mplayer2.
     *
     * @param s The program name property.
     */
    public void setProgramName(String s) {
        programName = s;
    }

    /**
     * A hack-ish value to timeout before doing a dispose of the window.
     * The idea is that closing the window too soon stops a system job that
     * runs mplayer to not exit properly.  This seems to happen in a jflicks
     * client-server instance often so we wait a bit before disposing and
     * it seems to fix it.  Again this is a hack.
     *
     * @return The timeout value.
     */
    public int getDisposeTimeout() {
        return (disposeTimeout);
    }

    /**
     * A hack-ish value to timeout before doing a dispose of the window.
     * The idea is that closing the window too soon stops a system job that
     * runs mplayer to not exit properly.  This seems to happen in a jflicks
     * client-server instance often so we wait a bit before disposing and
     * it seems to fix it.  Again this is a hack.
     *
     * @param i The timeout value.
     */
    public void setDisposeTimeout(int i) {
        disposeTimeout = i;
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
        return (true);
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

        if (urls != null) {

            if (urls.length == 1) {

                play(urls[0], null);

            } else {

                // We need to make a playlist file.
                try {

                    FileWriter fw = new FileWriter("playlist.txt");
                    for (int i = 0; i < urls.length; i++) {

                        String tmp = urls[i] + "\n";
                        fw.write(tmp, 0, tmp.length());
                    }

                    fw.close();
                    play("-playlist playlist.txt", null);

                } catch (IOException ex) {

                    log(DEBUG, ex.getMessage());
                }
            }
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
            setUserStop(false);

            String mytype = getType();
            if (PLAYER_VIDEO_STREAM_UDP.equals(mytype)) {

                int port = 1234;
                MPlayerUdpJob job = null;
                if (isForceFullscreen()) {

                    job = new MPlayerUdpJob(this, null, getArgs(), port);
                    setMPlayerJob(job);

                } else {

                    MPlayerCanvas can = getCanvas();
                    JDialog win = createWindow();
                    setDialog(win);
                    win.setVisible(true);

                    long canid = Native.getComponentID(can);
                    log(DEBUG, "canvas id: " + canid);
                    String wid = "" + canid;

                    job = new MPlayerUdpJob(this, wid, getArgs(), port);
                    setMPlayerJob(job);
                }

                JobContainer jc = JobManager.getJobContainer(job);
                setJobContainer(jc);
                jc.start();

            } else {

                long position = 0L;
                int time = 0;
                int playStateTime = 0;
                boolean bookmarkSeconds = false;

                if (b != null) {

                    playStateTime = b.getTime();
                    if (b.isPreferTime()) {

                        bookmarkSeconds = true;
                        time = playStateTime;

                    } else {

                        position = b.getPosition();
                    }
                }

                MPlayerJob job = null;
                if (isForceFullscreen()) {

                    job = new MPlayerJob(this, null, getArgs(), position,
                        time, url, isAutoSkip());
                    setMPlayerJob(job);

                } else {

                    MPlayerCanvas can = getCanvas();
                    JDialog win = createWindow();
                    setDialog(win);
                    win.setVisible(true);

                    final MPlayerCanvas fcan = can;
                    Runnable doRun = new Runnable() {

                        public void run() {

                            System.out.println("Requesting focus 4 canvas....");
                            fcan.requestFocus();
                        }
                    };
                    SwingUtilities.invokeLater(doRun);

                    long canid = Native.getComponentID(can);
                    log(DEBUG, "canvas id: " + canid);
                    String wid = "" + canid;

                    job = new MPlayerJob(this, wid, getArgs(), position,
                        time, url, isAutoSkip());
                    setMPlayerJob(job);
                }

                boolean preferTime = true;
                if (mytype == PLAYER_VIDEO_TRANSPORT_STREAM) {
                    preferTime = false;
                }
                PlayStateJob psj = new PlayStateJob(this, job, playStateTime,
                    bookmarkSeconds, preferTime);
                setPlayStateJob(psj);

                JobContainer jc = JobManager.getJobContainer(psj);
                setPlayStateJobContainer(jc);
                jc.start();

                jc = JobManager.getJobContainer(job);
                setJobContainer(jc);
                jc.start();
            }
        }
    }

    private JDialog createWindow() {

        JDialog result = null;

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

        result = new JDialog(getFrame());
        result.setUndecorated(true);
        result.setFocusable(true);
        result.setBounds(x, y, width, height);
        result.setBackground(Color.BLACK);

        MPlayerCanvas can = getCanvas();
        JPanel pan = getKeyPanel();
        JLayeredPane lpane = new JLayeredPane();
        setLayeredPane(lpane);
        pan.setBounds(0, 0, width, height);

        lpane.add(pan, Integer.valueOf(100));
        result.add(lpane, BorderLayout.CENTER);

        Cursor cursor = Util.getNoCursor();
        if (cursor != null) {

            pan.setCursor(cursor);
            can.setCursor(cursor);
            result.setCursor(cursor);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        System.out.println("Stop being called.");
        setPaused(false);
        setPlaying(false);
        setUserStop(true);
        if (PLAYER_VIDEO_STREAM_UDP.equals(getType())) {

            kill();

        } else {

            command("stop\n");
        }

        dispose();
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
        command("pause\n");
    }

    /**
     * {@inheritDoc}
     */
    public void seek(int seconds) {

        if (seconds > 0) {

            command("seek +" + seconds + "\n");

        } else {

            command("seek " + seconds + "\n");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void seekPosition(int seconds) {

        PlayStateJob job = getPlayStateJob();
        if (job != null) {

            double min = job.getMinimumTime();
            if (min != Double.MAX_VALUE) {

                int place = seconds + (int) min;
                command("seek " + place + " 2\n");
                command("set_property fullscreen 1\n");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void seekPosition(double percentage) {

        int per = (int) (percentage * 100.0);
        command("seek " + per + " 1\n");
        command("set_property fullscreen 1\n");
    }

    /**
     * {@inheritDoc}
     */
    public void next() {

        command("pt_step +1\n");
    }

    /**
     * {@inheritDoc}
     */
    public void previous() {

        command("pt_step -1\n");
    }

    /**
     * {@inheritDoc}
     */
    public void sap() {

        System.out.println("Yup at SAP mplayer");
        command("switch_audio\n");
    }

    /**
     * {@inheritDoc}
     */
    public void audiosync(double offset) {

        double current = getAudioOffset();
        current += offset;
        if (current < -100.0) {
            current = -100.0;
        }
        if (current > 100.0) {
            current = 100.0;
        }
        setAudioOffset(current);

        command("set_property audio_delay " + current + "\n");
    }

    @Override
    public void setSize(Rectangle r) {

        if (r != null) {

            JDialog d = getDialog();
            if (d != null) {

                d.setBounds(r);
            }

            JPanel p = getKeyPanel();
            if (p != null) {

                p.setBounds(0, 0, r.width, r.height);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public PlayState getPlayState() {

        PlayState result = null;

        PlayStateJob job = getPlayStateJob();
        if (job != null) {
            result = job.getPlayState();
        }

        return (result);
    }

    /**
     * Clean up window resources.
     */
    public synchronized void dispose() {

        JDialog w = getDialog();
        if (w != null) {

            waitForFinish(getDisposeTimeout(), 10);
            w.setVisible(false);
            w.dispose();
            setDialog(null);
            if (Util.isLinux()) {

                System.out.println("On Linux: doing a killall just for fun.");
                log(MPlayer.INFO, "On Linux: doing a killall just for fun.");
                SystemJob job =
                    SystemJob.getInstance("killall " + getProgramName());
                JobContainer jc = JobManager.getJobContainer(job);
                jc.start();
            }
        }
    }

    private void waitForFinish(int timeout, int step) {

        JobContainer jc = getJobContainer();
        if ((jc != null) && (timeout > 0)) {

            int sofar = 0;
            boolean done = false;
            while (!done) {

                if (jc.isAlive()) {

                    JobManager.sleep((long) step);
                    sofar += step;
                    if (sofar >= timeout) {

                        log(DEBUG, "Not Cool we timed out.");
                        done = true;
                    }

                } else {

                    log(DEBUG, "Cool Job thread was done.");
                    done = true;
                }
            }
        }
    }

    private void command(String s) {

        MPlayerJob job = getMPlayerJob();
        if ((s != null) && (job != null)) {

            log(DEBUG, "send command to mplayer <" + s + ">");
            job.command(s);
        }
    }

    private void kill() {

        MPlayerJob job = getMPlayerJob();
        if (job != null) {

            log(DEBUG, "send stop to mplayer");
            job.stop();
        }
    }

}


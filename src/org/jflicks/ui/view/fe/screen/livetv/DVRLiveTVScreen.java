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
package org.jflicks.ui.view.fe.screen.livetv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.mvc.View;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSUtil;
import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.transfer.Transfer;
import org.jflicks.tv.Channel;
import org.jflicks.tv.LiveTV;
import org.jflicks.tv.Recording;
import org.jflicks.tv.ShowAiring;
import org.jflicks.ui.view.fe.ChannelInfoPanel;
import org.jflicks.ui.view.fe.FrontEndView;
import org.jflicks.ui.view.fe.GuideJob;
import org.jflicks.ui.view.fe.NMSProperty;
import org.jflicks.ui.view.fe.RecordingInfoPanel;
import org.jflicks.ui.view.fe.screen.PlayerScreen;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports Videos in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DVRLiveTVScreen extends PlayerScreen implements NMSProperty,
    PropertyChangeListener, JobListener {

    private NMS[] nms;
    private LiveTV liveTV;
    private Timer startTimer;
    private Channel nextChannel;
    private JobContainer guideJobContainer;
    private HashMap<Channel, ShowAiring[]> guideMap;
    private RecordingInfoPanel recordingInfoPanel;
    private ChannelInfoPanel channelInfoPanel;
    private long watchingStartTime;
    private Transfer transfer;

    /**
     * Simple empty constructor.
     */
    public DVRLiveTVScreen() {

        setTitle("Live TV");
        BufferedImage bi = getImageByName("Live_TV");
        setDefaultBackgroundImage(bi);

        setFocusable(true);
        requestFocus();
    }

    public Transfer getTransfer() {
        return (transfer);
    }

    public void setTransfer(Transfer t) {
        transfer = t;
    }

    private LiveTV getLiveTV() {
        return (liveTV);
    }

    private void setLiveTV(LiveTV l) {
        liveTV = l;
    }

    private Channel getNextChannel() {
        return (nextChannel);
    }

    private void setNextChannel(Channel c) {
        nextChannel = c;
    }

    private JobContainer getGuideJobContainer() {
        return (guideJobContainer);
    }

    private void setGuideJobContainer(JobContainer c) {
        guideJobContainer = c;
    }

    private HashMap<Channel, ShowAiring[]> getGuideMap() {
        return (guideMap);
    }

    private void setGuideMap(HashMap<Channel, ShowAiring[]> m) {
        guideMap = m;

        log(INFO, "Guide is done...");
    }

    private RecordingInfoPanel getRecordingInfoPanel() {
        return (recordingInfoPanel);
    }

    private void setRecordingInfoPanel(RecordingInfoPanel w) {
        recordingInfoPanel = w;
    }

    private ChannelInfoPanel getChannelInfoPanel() {
        return (channelInfoPanel);
    }

    private void setChannelInfoPanel(ChannelInfoPanel w) {
        channelInfoPanel = w;
    }

    private long getWatchingStartTime() {
        return (watchingStartTime);
    }

    private void setWatchingStartTime(long l) {
        watchingStartTime = l;
    }

    private void startPlayer(LiveTV l) {

        Player p = getPlayer();
        Transfer t = getTransfer();
        if ((p != null) && (t != null) && (l != null)) {

            if (p.isPlaying()) {

                p.stop();
            }

            View v = getView();
            if (v instanceof FrontEndView) {

                FrontEndView fev = (FrontEndView) v;
                p.setRectangle(fev.getPosition());
            }

            p.setFrame(Util.findFrame(this));

            ChannelInfoPanel cip = getChannelInfoPanel();
            if (cip != null) {
                cip.setPlayer(p);
            }

            RecordingInfoPanel rip = getRecordingInfoPanel();
            if (rip != null) {
                rip.setPlayer(p);
            }

            Recording r = new Recording();
            r.setPath(l.getPath());
            r.setStreamURL(l.getStreamURL());
            r.setHostPort(l.getHostPort());

            log(DEBUG, l.getPath());
            log(DEBUG, l.getStreamURL());

            p.addPropertyChangeListener("Completed", this);

            String path = t.transfer(r, 20, 4);
            log(DEBUG, "local: " + path);
            setMarkTime(System.currentTimeMillis());
            p.play(path);
        }
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

            int width = (int) d.getWidth();
            int height = (int) d.getHeight();

            FrontEndView fev = (FrontEndView) getView();
            RecordingInfoPanel w = new RecordingInfoPanel(fev.getPosition(),
                8, getInfoColor(), getPanelColor(), (float) getPanelAlpha(),
                getSmallFont(), getMediumFont());
            w.setImageCache(getImageCache());
            w.setPlayer(getPlayer());
            w.setVisible(false);
            setRecordingInfoPanel(w);

            ChannelInfoPanel cw = new ChannelInfoPanel(fev.getPosition(), 8,
                getInfoColor(), getPanelColor(), (float) getPanelAlpha(),
                getSmallFont(), getMediumFont());
            cw.setVisible(false);
            setChannelInfoPanel(cw);

            JXPanel panel = new JXPanel(new BorderLayout());
            JXLabel l = new JXLabel("Tuning channel, please wait...");
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
     * Override so we can start up the player.
     *
     * @param b When true we are ready to start the player.
     */
    public void setVisible(boolean b) {

        super.setVisible(b);
        if (b) {

            NMS[] array = getNMS();
            if ((array != null) && (array.length > 0)) {

                NMS n = null;

                // Just use the first one we find....
                for (int i = 0; i < array.length; i++) {

                    if (array[i].supportsLiveTV()) {

                        n = array[i];
                        break;
                    }
                }

                if (n != null) {

                    setWatchingStartTime(System.currentTimeMillis());
                    LiveTV l = n.openSession();
                    log(DEBUG, "Called start livetv: " + l);
                    if (l != null) {

                        log(DEBUG, "livetv: " + l.getMessage());
                        if (l.getMessageType() == LiveTV.MESSAGE_TYPE_NONE) {

                            GuideJob gjob = new GuideJob(n, l.getChannels());
                            gjob.addJobListener(this);
                            JobContainer jc = JobManager.getJobContainer(gjob);
                            setGuideJobContainer(jc);
                            jc.start();

                            setLiveTV(l);
                            updateInfoWindow();
                            controlKeyboard(false);

                            final LiveTV fl = l;
                            Runnable doRun = new Runnable() {

                                public void run() {

                                    log(DEBUG, "Starting player...");
                                    startPlayer(fl);
                                }
                            };
                            SwingUtilities.invokeLater(doRun);

                        } else {

                            n.closeSession(l);
                            setLiveTV(null);
                            setDone(true);
                        }

                    } else {

                        setDone(true);
                    }

                } else {

                    setDone(true);
                }

            } else {

                setDone(true);
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

        RecordingInfoPanel w = getRecordingInfoPanel();
        if (w != null) {

            w.setVisible(!w.isVisible());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() {

        Player p = getPlayer();
        if (p != null) {

            p.stop();
        }

        close();
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

        log(DEBUG, "Yep at close!!!!");
        controlKeyboard(true);
        RecordingInfoPanel w = getRecordingInfoPanel();
        if (w != null) {

            w.setVisible(false);
        }

        ChannelInfoPanel cw = getChannelInfoPanel();
        if (cw != null) {

            cw.setVisible(false);
        }

        LiveTV l = getLiveTV();
        if (l != null) {

            NMS n = NMSUtil.select(getNMS(), l.getHostPort());
            if (n != null) {

                log(DEBUG, "calling stop...");
                n.closeSession(l);
                setLiveTV(null);
                setNextChannel(null);

                JobContainer jc = getGuideJobContainer();
                if (jc != null) {

                    jc.stop();
                    setGuideJobContainer(null);
                }
            }
        }

        Transfer t = getTransfer();
        if (t != null) {
            t.transfer(null, 0, 0);
        }

        setDone(true);
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

            int left = leftToGo(p, getMarkTime());
            log(DEBUG, "left to go: " + left);
            if (left > 30) {
                p.seek(30);
            }
        }
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

        computeNextChannelUp();
        log(DEBUG, "Up: " + getNextChannel());

        ChannelInfoPanel cw = getChannelInfoPanel();
        Channel c = getNextChannel();
        HashMap<Channel, ShowAiring[]> m = getGuideMap();
        if ((c != null) && (m != null) && (cw != null)) {

            cw.setChannel(c);
            cw.setShowAiring(currentShowAiring(m.get(c)));
            cw.setVisible(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void down() {

        computeNextChannelDown();
        log(DEBUG, "Down: " + getNextChannel());

        ChannelInfoPanel cw = getChannelInfoPanel();
        Channel c = getNextChannel();
        HashMap<Channel, ShowAiring[]> m = getGuideMap();
        if ((c != null) && (m != null) && (cw != null)) {

            cw.setChannel(c);
            cw.setShowAiring(currentShowAiring(m.get(c)));
            cw.setVisible(true);
        }
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

            int left = leftToGo(p, getMarkTime());
            log(DEBUG, "left to go: " + left);
            if (left > 30) {
                p.seek(30);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void enter() {

        LiveTV l = getLiveTV();
        Channel c = getNextChannel();
        if ((l != null) && (c != null)) {

            if (!c.equals(l.getCurrentChannel())) {

                Transfer t = getTransfer();
                Player p = getPlayer();
                if ((p != null) && (t != null)) {

                    ChannelInfoPanel cw = getChannelInfoPanel();
                    if (cw != null) {

                        cw.setVisible(false);
                    }

                    if (p.isPlaying()) {

                        p.stop();
                    }
                    repaint();

                    NMS n = NMSUtil.select(getNMS(), l.getHostPort());
                    if (n != null) {

                        setWatchingStartTime(System.currentTimeMillis());
                        l = n.changeChannel(l, c);
                        if (l.getMessageType() == LiveTV.MESSAGE_TYPE_NONE) {

                            setLiveTV(l);
                            updateInfoWindow();
                            controlKeyboard(false);
                            final LiveTV fl = l;
                            Runnable doRun = new Runnable() {

                                public void run() {

                                    log(DEBUG, "Starting player...");
                                    startPlayer(fl);
                                }
                            };
                            SwingUtilities.invokeLater(doRun);

                        } else {

                            n.closeSession(l);
                            setLiveTV(null);
                            setDone(true);
                        }

                    } else {

                        setLiveTV(null);
                        setDone(true);
                    }
                }
            }
        }
    }

    /**
     * Listen for the Player being "done".  This signifies the video finished
     * by coming to the end.
     *
     * @param event A given PropertyChangeEvent.
     */
    public void propertyChange(PropertyChangeEvent event) {

        log(DEBUG, "propertyChange: " + event.getPropertyName());
        if ((event.getSource() == getPlayer()) && (!isDone())) {

            // If we get this property update, then it means the video
            // finished playing on it's own.
            Boolean bobj = (Boolean) event.getNewValue();
            if (bobj.booleanValue()) {

                getPlayer().removePropertyChangeListener(this);
                log(DEBUG, "we are stopping because mplayer says so");

                close();

                log(DEBUG, "about to request focus");
                requestFocus();
            }
        }
    }

    public void actionPerformed(ActionEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            Serializable s = event.getState();
            if (s instanceof HashMap<?, ?>) {

                setGuideMap((HashMap<Channel, ShowAiring[]>) s);
                updateInfoWindow();
                setGuideJobContainer(null);
            }
        }
    }

    private void computeNextChannelUp() {

        LiveTV l = getLiveTV();
        if (l != null) {

            Channel[] array = l.getChannels();
            if (array != null) {

                Channel current = getNextChannel();
                if (current == null) {

                    setNextChannel(l.getCurrentChannel());

                } else {

                    int index = 0;
                    for (int i = 0; i < array.length; i++) {

                        if (array[i].equals(current)) {

                            index = i;
                            break;
                        }
                    }

                    index--;
                    if (index < 0) {

                        index = array.length - 1;
                    }

                    setNextChannel(array[index]);
                }
            }
        }
    }

    private void computeNextChannelDown() {

        LiveTV l = getLiveTV();
        if (l != null) {

            Channel[] array = l.getChannels();
            if (array != null) {

                Channel current = getNextChannel();
                if (current == null) {

                    setNextChannel(l.getCurrentChannel());

                } else {

                    int index = 0;
                    for (int i = 0; i < array.length; i++) {

                        if (array[i].equals(current)) {

                            index = i;
                            break;
                        }
                    }

                    index++;
                    if (index == array.length) {

                        index = 0;
                    }

                    setNextChannel(array[index]);
                }
            }
        }
    }

    private void updateInfoWindow() {

        LiveTV l = getLiveTV();
        if (l != null) {

            Channel c = l.getCurrentChannel();
            HashMap<Channel, ShowAiring[]> m = getGuideMap();
            RecordingInfoPanel w = getRecordingInfoPanel();
            if ((c != null) && (m != null) && (w != null)) {

                ShowAiring[] array = m.get(c);

                Recording r = new Recording(currentShowAiring(array));
                r.setCurrentlyRecording(true);
                r.setRealStart(getWatchingStartTime());
                w.setRecording(r);
            }
        }
    }

    private ShowAiring currentShowAiring(ShowAiring[] array) {

        ShowAiring result = null;

        if (array != null) {

            if (array.length > 0) {

                for (int i = 0; i < array.length; i++) {

                    if (!array[i].isOver()) {

                        result = array[i];
                        break;
                    }
                }
            }
        }

        return (result);
    }

}


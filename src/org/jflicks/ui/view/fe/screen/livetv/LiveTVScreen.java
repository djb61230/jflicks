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
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.mvc.View;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSUtil;
import org.jflicks.tv.Channel;
import org.jflicks.tv.LiveTV;
import org.jflicks.tv.Recording;
import org.jflicks.tv.ShowAiring;
import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.ui.view.fe.ChannelInfoWindow;
import org.jflicks.ui.view.fe.FrontEndView;
import org.jflicks.ui.view.fe.GuideJob;
import org.jflicks.ui.view.fe.NMSProperty;
import org.jflicks.ui.view.fe.RecordingInfoWindow;
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
public class LiveTVScreen extends PlayerScreen implements NMSProperty,
    PropertyChangeListener, JobListener {

    private static final long FILE_MIN_SIZE = 3072000L;

    private NMS[] nms;
    private LiveTV liveTV;
    private Timer startTimer;
    private Channel nextChannel;
    private JobContainer guideJobContainer;
    private HashMap<Channel, ShowAiring[]> guideMap;
    private RecordingInfoWindow recordingInfoWindow;
    private ChannelInfoWindow channelInfoWindow;
    private long watchingStartTime;

    /**
     * Simple empty constructor.
     */
    public LiveTVScreen() {

        setTitle("Live TV");
        BufferedImage bi = getImageByName("Live_TV");
        setDefaultBackgroundImage(bi);
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

    private Timer getStartTimer() {
        return (startTimer);
    }

    private void setStartTimer(Timer t) {
        startTimer = t;
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

    private RecordingInfoWindow getRecordingInfoWindow() {
        return (recordingInfoWindow);
    }

    private void setRecordingInfoWindow(RecordingInfoWindow w) {
        recordingInfoWindow = w;
    }

    private ChannelInfoWindow getChannelInfoWindow() {
        return (channelInfoWindow);
    }

    private void setChannelInfoWindow(ChannelInfoWindow w) {
        channelInfoWindow = w;
    }

    private long getWatchingStartTime() {
        return (watchingStartTime);
    }

    private void setWatchingStartTime(long l) {
        watchingStartTime = l;
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

            RecordingInfoWindow w = new RecordingInfoWindow(width, height, 8,
                getInfoColor(), getPanelColor(), (float) getPanelAlpha(),
                getSmallFont(), getMediumFont());
            w.setImageCache(getImageCache());
            w.setPlayer(getPlayer());
            setRecordingInfoWindow(w);

            ChannelInfoWindow cw = new ChannelInfoWindow(width, height, 8,
                getInfoColor(), getPanelColor(), (float) getPanelAlpha(),
                getSmallFont(), getMediumFont());
            setChannelInfoWindow(cw);

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

                Player p = getPlayer();
                if ((n != null) && (p != null)) {

                    setWatchingStartTime(System.currentTimeMillis());
                    LiveTV l = null;
                    String hostaddr = null;
                    try {

                        InetAddress local = InetAddress.getLocalHost();
                        hostaddr = local.getHostAddress();
                        l = n.openSession(hostaddr, 1234);

                    } catch (UnknownHostException ex) {

                        log(DEBUG, ex.getMessage());
                    }

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

                            View v = getView();
                            if (v instanceof FrontEndView) {

                                FrontEndView fev = (FrontEndView) v;
                                p.setRectangle(fev.getPosition());
                            }

                            p.addPropertyChangeListener("Paused", this);
                            p.addPropertyChangeListener("Completed", this);
                            //controlKeyboard(false);
                            p.setFrame(Util.findFrame(this));
                            p.play("udp://@" + hostaddr + ":1234");

                            //Timer wait = new Timer(1000, this);
                            //setStartTimer(wait);
                            //wait.start();

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

        RecordingInfoWindow w = getRecordingInfoWindow();
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
    public void close() {

        //controlKeyboard(true);
        RecordingInfoWindow w = getRecordingInfoWindow();
        if (w != null) {

            w.setVisible(false);
        }

        ChannelInfoWindow cw = getChannelInfoWindow();
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

                Player p = getPlayer();
                if (p != null) {

                    p.stop();
                }

                JobContainer jc = getGuideJobContainer();
                if (jc != null) {

                    jc.stop();
                    setGuideJobContainer(null);
                }
            }
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

            p.seek(30);
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

        ChannelInfoWindow cw = getChannelInfoWindow();
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

        ChannelInfoWindow cw = getChannelInfoWindow();
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

            p.seek(30);
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

                ChannelInfoWindow cw = getChannelInfoWindow();
                if (cw != null) {

                    cw.setVisible(false);
                }

                NMS n = NMSUtil.select(getNMS(), l.getHostPort());
                if (n != null) {

                    setWatchingStartTime(System.currentTimeMillis());
                    l = n.changeChannel(l, c);
                    if (l.getMessageType() == LiveTV.MESSAGE_TYPE_NONE) {

                        setLiveTV(l);
                        updateInfoWindow();

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

    /**
     * Listen for the Player being "done".  This signifies the video finished
     * by coming to the end.
     *
     * @param event A given PropertyChangeEvent.
     */
    public void propertyChange(PropertyChangeEvent event) {

        if ((event.getSource() == getPlayer()) && (!isDone())) {

            if (event.getPropertyName().equals("Completed")) {

                // If we get this property update, then it means the video
                // finished playing on it's own.
                Boolean bobj = (Boolean) event.getNewValue();
                if (!bobj.booleanValue()) {

                    getPlayer().removePropertyChangeListener(this);
                    log(DEBUG, "we are stopping because player says so");

                    ActionListener closePerformer = new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            close();
                        }
                    };
                    Timer closeTimer = new Timer(1000, closePerformer);
                    closeTimer.setRepeats(false);
                    closeTimer.start();

                    setDone(true);
                }
            }
        }
    }

    /**
     * We need to listen for actions.
     *
     * @param event An ActionEvent instance.
     */
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
            RecordingInfoWindow w = getRecordingInfoWindow();
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


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

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;
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
import org.jflicks.tv.Airing;
import org.jflicks.tv.Channel;
import org.jflicks.tv.LiveTV;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;
import org.jflicks.ui.view.fe.AddRuleJob;
import org.jflicks.ui.view.fe.ButtonPanel;
import org.jflicks.ui.view.fe.ChannelInfoPanel;
import org.jflicks.ui.view.fe.Dialog;
import org.jflicks.ui.view.fe.FrontEndView;
import org.jflicks.ui.view.fe.GridGuidePanel;
import org.jflicks.ui.view.fe.GuideJob;
import org.jflicks.ui.view.fe.NMSProperty;
import org.jflicks.ui.view.fe.RecordingInfoPanel;
import org.jflicks.ui.view.fe.RecordingRulePanel;
import org.jflicks.ui.view.fe.RecordingRuleProperty;
import org.jflicks.ui.view.fe.ShowDetailPanel;
import org.jflicks.ui.view.fe.screen.PlayerScreen;
import org.jflicks.util.Busy;
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
    RecordingRuleProperty, PropertyChangeListener, JobListener {

    private static final int ALL_CHANNELS = 1;
    private static final int FAVORITE_CHANNELS = 2;
    private static final String ALL_CHANNELS_TEXT = "All Channels";
    private static final String FAVORITE_CHANNELS_TEXT = "Favorite Channels";
    private static final String ADD_FAVORITE = "Add to Favorites";
    private static final String SWITCH_TO_FAVORITE = "Switch to Favorites";
    private static final String REMOVE_FAVORITE = "Remove from Favorites";
    private static final String SWITCH_FROM_FAVORITE = "Switch from Favorites";
    private static final String CHANGE_CHANNEL = "Change Channel";
    private static final String SCHEDULE = "Schedule";

    private NMS[] nms;
    private LiveTV liveTV;
    private Timer startTimer;
    private Channel nextChannel;
    private JobContainer guideJobContainer;
    private HashMap<Channel, ShowAiring[]> guideMap;
    private RecordingRule[] recordingRules;
    private RecordingInfoPanel recordingInfoPanel;
    private ChannelInfoPanel channelInfoPanel;
    private long watchingStartTime;
    private Transfer transfer;
    private String streamType;
    private Rectangle guideRectangle;
    private boolean guideMode;
    private JXPanel waitPanel;
    private GridGuidePanel gridGuidePanel;
    private ShowDetailPanel showDetailPanel;
    private ShowAiring selectedShowAiring;
    private RecordingRulePanel recordingRulePanel;
    private int channelState;
    private JXLabel channelLabel;
    private Channel lastChannel;
    private ArrayList<String> favoriteChannelList;
    private Channel[] allChannels;
    private int initialTime;
    private int restTime;

    /**
     * Simple empty constructor.
     */
    public DVRLiveTVScreen() {

        setTitle("Live TV");
        setStreamType(Player.PLAYER_VIDEO_TRANSPORT_STREAM);
        setFavoriteChannelList(new ArrayList<String>());

        BufferedImage bi = getImageByName("Live_TV");
        setDefaultBackgroundImage(bi);

        setFocusable(true);
        requestFocus();

        setChannelState(ALL_CHANNELS);
        setChannelLabel(new JXLabel(ALL_CHANNELS_TEXT));

        RecordingRulePanel rrp = new RecordingRulePanel();
        HashSet<AWTKeyStroke> set =
            new HashSet<AWTKeyStroke>(rrp.getFocusTraversalKeys(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        set.clear();
        set.add(KeyStroke.getKeyStroke("DOWN"));
        rrp.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
            set);

        set = new HashSet<AWTKeyStroke>(rrp.getFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        set.clear();
        set.add(KeyStroke.getKeyStroke("UP"));
        rrp.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
            set);
        setRecordingRulePanel(rrp);
    }

    public Transfer getTransfer() {
        return (transfer);
    }

    public void setTransfer(Transfer t) {
        transfer = t;
    }

    /**
     * {@inheritDoc}
     */
    public RecordingRule[] getRecordingRules() {

        RecordingRule[] result = null;

        if (recordingRules != null) {

            result = Arrays.copyOf(recordingRules, recordingRules.length);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void setRecordingRules(RecordingRule[] array) {

        if (array != null) {
            recordingRules = Arrays.copyOf(array, array.length);
        } else {
            recordingRules = null;
        }
    }

    private Frame getFrame() {
        return (Util.findFrame(this));
    }

    private ArrayList<String> getFavoriteChannelList() {
        return (favoriteChannelList);
    }

    private void setFavoriteChannelList(ArrayList<String> l) {
        favoriteChannelList = l;
    }

    private Channel[] getAllChannels() {
        return (allChannels);
    }

    private void setAllChannels(Channel[] array) {
        allChannels = array;
    }

    private Channel getLastChannel() {
        return (lastChannel);
    }

    private void setLastChannel(Channel c) {
        lastChannel = c;
    }

    private int getChannelState() {
        return (channelState);
    }

    private void setChannelState(int i) {
        channelState = i;
    }

    private boolean isGuideMode() {
        return (guideMode);
    }

    private void setGuideMode(boolean b) {
        guideMode = b;
    }

    private Rectangle getGuideRectangle() {
        return (guideRectangle);
    }

    private void setGuideRectangle(Rectangle r) {
        guideRectangle = r;
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

        GridGuidePanel ggp = getGridGuidePanel();
        if (ggp != null) {

            ggp.setGuideMap(guideMap);
        }
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

    private GridGuidePanel getGridGuidePanel() {
        return (gridGuidePanel);
    }

    private void setGridGuidePanel(GridGuidePanel p) {
        gridGuidePanel = p;
    }

    private ShowDetailPanel getShowDetailPanel() {
        return (showDetailPanel);
    }

    private void setShowDetailPanel(ShowDetailPanel p) {
        showDetailPanel = p;
    }

    private JXLabel getChannelLabel() {
        return (channelLabel);
    }

    private void setChannelLabel(JXLabel l) {
        channelLabel = l;
    }

    private long getWatchingStartTime() {
        return (watchingStartTime);
    }

    private void setWatchingStartTime(long l) {
        watchingStartTime = l;
    }

    private JXPanel getWaitPanel() {
        return (waitPanel);
    }

    private void setWaitPanel(JXPanel p) {
        waitPanel = p;
    }

    private RecordingRulePanel getRecordingRulePanel() {
        return (recordingRulePanel);
    }

    private void setRecordingRulePanel(RecordingRulePanel p) {
        recordingRulePanel = p;
    }

    private boolean isPlayingVideo() {
        return (Player.PLAYER_VIDEO.equals(streamType));
    }

    private String getStreamType() {
        return (streamType);
    }

    private void setStreamType(String s) {
        streamType = s;
    }

    private ShowAiring getSelectedShowAiring() {
        return (selectedShowAiring);
    }

    private void setSelectedShowAiring(ShowAiring sa) {
        selectedShowAiring = sa;
    }

    @Override
    public Player getPlayer() {
        return (getPlayer(getStreamType()));
    }

    private void startPlayer(LiveTV l) {

        Player p = getPlayer();
        Transfer t = getTransfer();
        if ((p != null) && (t != null) && (l != null)) {

            if (p.isPlaying()) {

                p.removePropertyChangeListener("Completed", this);
                p.stop();
            }

            // Now we have to get the right player in case we switched
            // stream types.
            if (l.getPath().endsWith("mpg")) {
                setStreamType(Player.PLAYER_VIDEO_PROGRAM_STREAM);
            } else if (l.getPath().endsWith("ts")) {
                setStreamType(Player.PLAYER_VIDEO_TRANSPORT_STREAM);
            }

            p = getPlayer();

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
            r.setTitle(l.getPath());
            r.setPath(l.getPath());
            r.setStreamURL(l.getStreamURL());
            r.setHostPort(l.getHostPort());

            log(DEBUG, l.getPath());
            log(DEBUG, l.getStreamURL());

            p.addPropertyChangeListener("Completed", this);

            String path = t.transfer(r, getInitialTime(), getRestTime());
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

    public int getInitialTime() {
        return (initialTime);
    }

    public void setInitialTime(int i) {
        initialTime = i;
    }

    public int getRestTime() {
        return (restTime);
    }

    public void setRestTime(int i) {
        restTime = i;
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {

        JLayeredPane pane = getLayeredPane();
        if ((d != null) && (pane != null)) {

            float alpha = (float) getPanelAlpha();

            int width = (int) d.getWidth();
            int height = (int) d.getHeight();

            int wspan = (int) (width * 0.03);
            int listwidth = (width - (2 * wspan));
            int halflistwidth = (width - (3 * wspan)) / 2;
            int onethirdlistwidth = (width - (3 * wspan)) / 3;
            int twothirdlistwidth = onethirdlistwidth * 2;

            int hspan = (int) (height * 0.03);
            int listheight = (int) ((height - (2 * hspan)) / 1.5);

            int detailwidth = (int) (width * 0.63);
            int detailheight = (height - (3 * hspan)) - listheight;

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
            setWaitPanel(panel);

            GridGuidePanel ggp = new GridGuidePanel();
            ggp.addPropertyChangeListener("SelectedShowAiring", this);
            setGridGuidePanel(ggp);

            ShowDetailPanel sdp = new ShowDetailPanel();
            sdp.setBounds(wspan, hspan + listheight + hspan, detailwidth,
                detailheight);
            setShowDetailPanel(sdp);

            JXLabel label = getChannelLabel();
            label.setFont(ggp.getLargeFont());
            label.setForeground(ggp.getInfoColor());
            label.setHorizontalTextPosition(SwingConstants.CENTER);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            Dimension ldim = label.getPreferredSize();
            int labelHeight = (int) ldim.getHeight();
            label.setBounds(wspan, hspan, listwidth, labelHeight);

            ggp.setBounds(wspan, hspan + labelHeight, listwidth,
                listheight - labelHeight);

            Rectangle fevrec = fev.getPosition();

            int subx = wspan + detailwidth + wspan;
            subx += (int) fevrec.getX();
            int suby = hspan + hspan + listheight;
            suby += (int) fevrec.getY();
            int subw = detailheight * 16 / 9;
            int subh = detailheight;

            setGuideRectangle(new Rectangle(subx, suby, subw, subh));

            setDefaultBackgroundImage(
                Util.resize(getDefaultBackgroundImage(), width, height));
        }
    }

    private void updateLayout(boolean wait) {

        JLayeredPane pane = getLayeredPane();
        if (pane != null) {

            pane.removeAll();
            if (wait) {

                pane.add(getWaitPanel(), Integer.valueOf(100));

            } else {

                if (isGuideMode()) {

                    pane.add(getChannelLabel(), Integer.valueOf(110));
                    pane.add(getGridGuidePanel(), Integer.valueOf(100));
                    pane.add(getShowDetailPanel(), Integer.valueOf(100));

                } else {

                    pane.add(getWaitPanel(), Integer.valueOf(100));
                }
            }

            repaint();
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

            // We are being shown so let's read the local channel favorite
            // text file in case it's been changed since last time we were
            // here.  Each entry is a line that came from a Channel.toString()
            // call.  Since we can dynamically change them here we will read
            // them into an ArrayList<String>.  When this screen ends we
            // will need to write out the current state in case other screens
            // need the same info.
            File here = new File (".");
            ArrayList<String> favlist = getFavoriteChannelList();
            if (favlist != null) {

                favlist.clear();
                String[] cnames =
                    Util.readTextFile(new File(here, "fav-chan.txt"));
                if ((cnames != null) && (cnames.length > 0)) {

                    for (int i = 0; i < cnames.length; i++) {

                        favlist.add(cnames[i]);
                    }

                    Collections.sort(favlist);
                }
            }

            // Try to find the last channel.
            String lastChannelNumber = null;
            String[] lastText =
                Util.readTextFile(new File(here, "last-chan.txt"));
            if ((lastText != null) && (lastText.length > 0)) {
                lastChannelNumber = lastText[0];
            }

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
                    LiveTV l = n.openSession(lastChannelNumber);
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
                            updateLayout(true);

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

        System.out.println("info called: " + isGuideMode());
        if (isGuideMode()) {

            if (!isPopupEnabled()) {

                System.out.println("Should be only popup");
                GridGuidePanel ggp = getGridGuidePanel();
                if (ggp != null) {

                    ArrayList<String> blist = new ArrayList<String>();

                    if (ggp.isOn(getSelectedShowAiring())) {
                        blist.add(CHANGE_CHANNEL);
                    } else {
                        blist.add(SCHEDULE);
                    }
                    if (getChannelState() == ALL_CHANNELS) {

                        blist.add(SWITCH_TO_FAVORITE);
                        blist.add(ADD_FAVORITE);

                    } else {

                        blist.add(SWITCH_FROM_FAVORITE);
                        blist.add(REMOVE_FAVORITE);
                    }

                    blist.add(CANCEL);
                    popup(blist.toArray(new String[blist.size()]));
                }
            }

        } else {

            RecordingInfoPanel w = getRecordingInfoPanel();
            if (w != null) {

                w.setVisible(!w.isVisible());
            }
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

        System.out.println("guide function");
        Player p = getPlayer();
        Rectangle r = getGuideRectangle();
        if ((p != null) && (p.isPlaying()) && (r != null)) {

            if (!isGuideMode()) {

                RecordingInfoPanel w = getRecordingInfoPanel();
                if (w != null) {

                    w.setVisible(false);
                    w.removeFromPlayer();
                }
                ChannelInfoPanel cw = getChannelInfoPanel();
                if (cw != null) {

                    cw.setVisible(false);
                }

                setGuideMode(true);

                LiveTV l = getLiveTV();
                GridGuidePanel ggp = getGridGuidePanel();
                if ((l != null) && (ggp != null)) {

                    Channel c = l.getCurrentChannel();
                    if (c != null) {

                        ggp.setCurrentChannel(c);
                    }
                }

                log(DEBUG, "About to resize to little");
                p.setSize(r);
                //requestFocus();
                updateLayout(false);

            } else {

                setGuideMode(false);
                log(DEBUG, "About to resize to big");
                p.setSize(p.getRectangle());
                updateLayout(true);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void pageup() {

        GridGuidePanel ggp = getGridGuidePanel();
        if (ggp != null) {
            ggp.pageUp();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void pagedown() {

        GridGuidePanel ggp = getGridGuidePanel();
        if (ggp != null) {

            ggp.pageDown();
        }
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

        File here = new File (".");
        ArrayList<String> favlist = getFavoriteChannelList();
        if (favlist != null) {

            StringBuilder sb = new StringBuilder("");
            for (int i = 0; i < favlist.size(); i++) {

                sb.append(favlist.get(i));
                sb.append("\n");
            }

            try {

                Util.writeTextFile(new File(here, "fav-chan.txt"),
                    sb.toString());

            } catch (IOException ex) {
            }
        }

        Channel lc = getLastChannel();
        if (lc != null) {

            try {

                Util.writeTextFile(new File(here, "last-chan.txt"),
                    lc.getNumber() + "\n");

            } catch (IOException ex) {
            }
        }

        setGuideMode(false);
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

        if (!isGuideMode()) {

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

        } else {

            if (isPopupEnabled()) {

                ButtonPanel bp = getPlayButtonPanel();
                if (bp != null) {

                    bp.moveUp();
                }

            } else {

                GridGuidePanel ggp = getGridGuidePanel();
                if (ggp != null) {

                    ggp.up();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void down() {

        if (!isGuideMode()) {

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

        } else {

            if (isPopupEnabled()) {

                ButtonPanel bp = getPlayButtonPanel();
                if (bp != null) {

                    bp.moveDown();
                }

            } else {

                GridGuidePanel ggp = getGridGuidePanel();
                if (ggp != null) {

                    ggp.down();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void left() {

        if (!isGuideMode()) {

            Player p = getPlayer();
            if (p != null) {

                p.seek(-8);
            }

        } else {

            if (isPopupEnabled()) {
            } else {

                GridGuidePanel ggp = getGridGuidePanel();
                if (ggp != null) {

                    ggp.left();

                    /*
                    if (!ggp.left()) {

                        if (getChannelState() == ALL_CHANNELS) {
                            setChannelState(FAVORITE_CHANNELS);
                        } else if (getChannelState() == FAVORITE_CHANNELS) {
                            setChannelState(ALL_CHANNELS);
                        }

                        applyChannels();
                    }
                    */
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void right() {

        if (!isGuideMode()) {

            Player p = getPlayer();
            if (p != null) {

                int left = leftToGo(p, getMarkTime());
                log(DEBUG, "left to go: " + left);
                if (left > 30) {
                    p.seek(30);
                }
            }

        } else {

            if (isPopupEnabled()) {
            } else {

                GridGuidePanel ggp = getGridGuidePanel();
                if (ggp != null) {

                    ggp.right();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void enter() {

        if (!isGuideMode()) {

            changeChannel(getLiveTV(), getNextChannel());

        } else {

            if (!isPopupEnabled()) {

                handleGuideEnter();

            } else {

                // We got this event because the player has focus.  So
                // as a hack we call actionPerformed so we do the right
                // thing.  Cheesy.
                String val = getPlayButtonPanel().getSelectedButton();
                actionPerformed(new ActionEvent(this, 1, val));
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

        } else if (event.getPropertyName().equals("SelectedShowAiring")) {

            ShowDetailPanel sdp = getShowDetailPanel();
            if (sdp != null) {

                ShowAiring sa = (ShowAiring) event.getNewValue();
                sdp.setShowAiring(sa);
                setSelectedShowAiring(sa);
            }
        }
    }

    private void handleFavorite() {

        GridGuidePanel ggp = getGridGuidePanel();
        ArrayList<String> favlist = getFavoriteChannelList();
        if ((ggp != null) && (favlist != null)) {

            Channel chan = ggp.getSelectedChannel();
            if (chan != null) {

                String text = chan.toString();
                if (getChannelState() == ALL_CHANNELS) {

                    if (!favlist.contains(text)) {

                        favlist.add(text);
                        Collections.sort(favlist);
                    }

                } else if (getChannelState() == FAVORITE_CHANNELS) {

                    if (favlist.contains(text)) {

                        favlist.remove(text);
                        Collections.sort(favlist);
                        applyChannels();
                    }
                }
            }
        }
    }

    public void editRule(NMS n, RecordingRulePanel p, RecordingRule rr) {

        if ((n != null) && (p != null) && (rr != null)) {

            p.setNMS(n);
            p.setRecordingRule(rr);
            p.setFrame(getFrame());

            Dialog.showPanel(getFrame(), p, p.getOkButton(),
                p.getCancelButton());
            if (p.isAccept()) {

                rr = p.getRecordingRule();
                AddRuleJob arj = new AddRuleJob(n, rr);
                Busy busy = new Busy(getLayeredPane(), arj);
                busy.addJobListener(this);
                busy.execute();

            } else {

                requestFocus();
            }
        }
    }

    /**
     * We need to listen for events from the "play popup dialog".
     *
     * @param event A given event.
     */
    public void actionPerformed(ActionEvent event) {

        System.out.println("action performed: " + event.getSource());
        if (isGuideMode()) {

            ButtonPanel pbp = getPlayButtonPanel();
            if (pbp != null) {

                if (CHANGE_CHANNEL.equals(pbp.getSelectedButton())) {

                    GridGuidePanel ggp = getGridGuidePanel();
                    if (ggp != null) {

                        setGuideMode(false);
                        updateLayout(true);
                        changeChannel(getLiveTV(), ggp.getSelectedChannel());
                    }

                } else if (ADD_FAVORITE.equals(pbp.getSelectedButton())) {

                    handleFavorite();

                } else if (REMOVE_FAVORITE.equals(pbp.getSelectedButton())) {

                    handleFavorite();

                } else if (SWITCH_TO_FAVORITE.equals(pbp.getSelectedButton())) {

                    setChannelState(FAVORITE_CHANNELS);
                    applyChannels();

                } else if (SWITCH_FROM_FAVORITE.equals(
                    pbp.getSelectedButton())) {

                    setChannelState(ALL_CHANNELS);
                    applyChannels();

                } else if (SCHEDULE.equals(pbp.getSelectedButton())) {

                    ShowAiring sa = getSelectedShowAiring();
                    RecordingRulePanel rrp = getRecordingRulePanel();
                    if ((sa != null) && (rrp != null)) {

                        NMS n = NMSUtil.select(getNMS(), sa.getHostPort());
                        if (n != null) {

                            Show show = sa.getShow();
                            Airing airing = sa.getAiring();
                            if ((show != null) && (airing != null)) {

                                RecordingRule rr = getRecordingRule(sa);

                                if (rr == null) {

                                    rr = new RecordingRule();
                                    rr.setShowAiring(sa);
                                    rr.setType(RecordingRule.SERIES_TYPE);
                                    rr.setName(show.getTitle());
                                    rr.setShowId(show.getId());
                                    rr.setSeriesId(show.getSeriesId());
                                    rr.setChannelId(airing.getChannelId());
                                    rr.setListingId(airing.getListingId());
                                    rr.setDuration(airing.getDuration());
                                    rr.setPriority(
                                        RecordingRule.NORMAL_PRIORITY);
                                    rr.setTasks(n.getTasks());
                                }

                                editRule(n, rrp, rr);
                            }
                        }
                    }
                }

                unpopup();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            Serializable s = event.getState();
            if (s instanceof HashMap<?, ?>) {

                HashMap<Channel, ShowAiring[]> map =
                    (HashMap<Channel, ShowAiring[]>) s;
                setGuideMap(map);
                updateInfoWindow();
                setGuideJobContainer(null);

                Set<Channel> set = map.keySet();
                if (set.size() > 0) {

                    Channel[] chans = set.toArray(new Channel[set.size()]);
                    Arrays.sort(chans);
                    setAllChannels(chans);
                    applyChannels();
                }
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

    private ShowAiring[] computeShowAirings(ShowAiring[] array) {

        ShowAiring[] result = array;

        if (array != null) {

            if (array.length > 0) {

                int skip = 0;
                for (int i = 0; i < array.length; i++) {

                    if (!array[i].isOver()) {

                        skip = i;
                        break;
                    }
                }

                if (skip > 0) {

                    result = Arrays.copyOfRange(array, skip, array.length);
                }
            }
        }

        return (result);
    }

    private Channel[] filterByFavorite(Channel[] array) {

        Channel[] result = null;

        ArrayList<String> favlist = getFavoriteChannelList();
        if ((array != null) && (favlist != null)) {

            ArrayList<Channel> filter = new ArrayList<Channel>();
            for (int i = 0; i < favlist.size(); i++) {

                String tmp = favlist.get(i);
                if (tmp != null) {

                    for (int j = 0; j < array.length; j++) {

                        if (tmp.equals(array[j].toString())) {

                            filter.add(array[j]);
                            break;
                        }
                    }
                }
            }

            if (filter.size() > 0) {

                Collections.sort(filter);
                result = filter.toArray(new Channel[filter.size()]);
            }
        }

        return (result);
    }

    private void applyChannels() {

        GridGuidePanel ggp = getGridGuidePanel();
        Channel[] carray = getAllChannels();
        JXLabel l = getChannelLabel();
        if ((ggp != null) && (carray != null) && (l != null)) {

            if (getChannelState() == ALL_CHANNELS) {

                ggp.setChannels(carray);
                l.setText(ALL_CHANNELS_TEXT);

            } else if (getChannelState() == FAVORITE_CHANNELS) {

                Channel[] only = filterByFavorite(carray);
                if (only != null) {

                    ggp.setChannels(only);
                    l.setText(FAVORITE_CHANNELS_TEXT);

                } else {

                    setChannelState(ALL_CHANNELS);
                }
            }
        }

    }

    private void handleGuideEnter() {

        GridGuidePanel ggp = getGridGuidePanel();
        if (ggp != null) {

            Channel chan = ggp.getSelectedChannel();
            if (chan != null) {

                ArrayList<String> blist = new ArrayList<String>();

                ShowAiring sa = getSelectedShowAiring();
                if (sa != null) {

                    if (ggp.isOn(sa)) {
                        blist.add(CHANGE_CHANNEL);
                    } else {
                        blist.add(SCHEDULE);
                    }
                }

                blist.add(CANCEL);
                popup(blist.toArray(new String[blist.size()]));
            }
        }
    }

    private void changeChannel(LiveTV l, Channel c) {

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
                    p.setSize(p.getRectangle());
                    repaint();

                    NMS n = NMSUtil.select(getNMS(), l.getHostPort());
                    if (n != null) {

                        setWatchingStartTime(System.currentTimeMillis());
                        l = n.changeChannel(l, c);
                        if (l.getMessageType() == LiveTV.MESSAGE_TYPE_NONE) {

                            setLastChannel(c);
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

    private RecordingRule getRecordingRule(ShowAiring sa) {

        RecordingRule result = null;

        if (sa != null) {

            NMS n = NMSUtil.select(getNMS(), sa.getHostPort());
            Show show = sa.getShow();
            Airing airing = sa.getAiring();

            if ((n != null) && (show != null) && (airing != null)) {

                int cid = airing.getChannelId();
                String seriesId = show.getSeriesId();
                RecordingRule[] rules = n.getRecordingRules();
                if ((seriesId != null) && (rules != null)) {

                    for (int i = 0; i < rules.length; i++) {

                        ShowAiring rrsa = rules[i].getShowAiring();
                        if (rrsa != null) {

                            // We have a rule that is a ONCE recording.  It
                            // is only our rule to edit if the two ShowAiring
                            // instances are the same.
                            if (rrsa.equals(sa)) {

                                result = rules[i];
                                break;
                            }

                        } else {

                            if ((cid == rules[i].getChannelId())
                                && (seriesId.equals(rules[i].getSeriesId()))) {

                                result = rules[i];
                                break;
                            }
                        }
                    }
                }
            }
        }

        return (result);
    }

}


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
package org.jflicks.ui.view.fe.screen.recording;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jflicks.imagecache.ImageCache;
import org.jflicks.mvc.View;
import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.player.PlayState;
import org.jflicks.transfer.Transfer;
import org.jflicks.tv.Commercial;
import org.jflicks.tv.Recording;
import org.jflicks.ui.view.fe.ButtonPanel;
import org.jflicks.ui.view.fe.FrontEndView;
import org.jflicks.ui.view.fe.RecordingListPanel;
import org.jflicks.ui.view.fe.RecordingDetailPanel;
import org.jflicks.ui.view.fe.RecordingInfoWindow;
import org.jflicks.ui.view.fe.RecordingProperty;
import org.jflicks.ui.view.fe.screen.PlayerScreen;
import org.jflicks.ui.view.fe.screen.ScreenEvent;
import org.jflicks.util.Util;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports Recordings in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordingScreen extends PlayerScreen implements RecordingProperty,
    PropertyChangeListener {

    private static final String AUTO_SKIP_PREFIX = "Auto Skip";
    private static final String AUTO_SKIP_IS_ON = "Auto Skip is On";
    private static final String AUTO_SKIP_IS_OFF = "Auto Skip is Off";
    private static final String AUDIO_CONTROL_PREFIX = "Audio Control";
    private static final String AUDIO_CONTROL_IS_ON = "Audio Control is On";
    private static final String AUDIO_CONTROL_IS_OFF = "Audio Control is Off";
    private static final String CERTAIN = "Are you certain?";

    private Recording[] recordings;
    private RecordingListPanel groupRecordingListPanel;
    private RecordingListPanel recordingListPanel;
    private RecordingDetailPanel recordingDetailPanel;
    private JXPanel screenShotPanel;
    private RecordingInfoWindow recordingInfoWindow;
    private Integer[] timeline;
    private int currentGroupIndex;
    private int currentGroupStartIndex;
    private int currentRecordingIndex;
    private int currentRecordingStartIndex;
    private Recording currentRecording;
    private boolean restoreState;
    private Animator screenShotAnimator;
    private boolean playingVideo;
    private Transfer transfer;
    private String streamType;
    private boolean autoSkip;
    private boolean audioControl;
    private Timer autoSkipTimer;
    private AutoSkipActionListener autoSkipActionListener;
    private int lastScreenEvent;
    private Recording certainRecording;

    /**
     * Simple empty constructor.
     */
    public RecordingScreen() {

        setTitle("Watch Recordings");
        setStreamType(Player.PLAYER_VIDEO_TRANSPORT_STREAM);

        BufferedImage bi = getImageByName("Watch_Recordings");
        setDefaultBackgroundImage(bi);

        File home = new File(".");
        File dbhome = new File(home, "db");
        setBookmarkFile(new File(dbhome, "recbookmarks.dat"));
        log(DEBUG, getBookmarkFile().getPath());
        load();

        setFocusable(true);
        requestFocus();

        InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        LeftAction leftAction = new LeftAction();
        map.put(KeyStroke.getKeyStroke("LEFT"), "left");
        getActionMap().put("left", leftAction);

        RightAction rightAction = new RightAction();
        map.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        getActionMap().put("right", rightAction);

        UpAction upAction = new UpAction();
        map.put(KeyStroke.getKeyStroke("UP"), "up");
        getActionMap().put("up", upAction);

        DownAction downAction = new DownAction();
        map.put(KeyStroke.getKeyStroke("DOWN"), "down");
        getActionMap().put("down", downAction);

        PageUpAction pageUpAction = new PageUpAction();
        map.put(KeyStroke.getKeyStroke("PAGE_UP"), "pageup");
        getActionMap().put("pageup", pageUpAction);

        PageDownAction pageDownAction = new PageDownAction();
        map.put(KeyStroke.getKeyStroke("PAGE_DOWN"), "pagedown");
        getActionMap().put("pagedown", pageDownAction);

        EnterAction enterAction = new EnterAction();
        map.put(KeyStroke.getKeyStroke("ENTER"), "enter");
        getActionMap().put("enter", enterAction);

        AutoSkipActionListener asal = new AutoSkipActionListener();
        setAutoSkipActionListener(asal);
        Timer t = new Timer(1000, asal);
        t.setInitialDelay(5000);
        setAutoSkipTimer(t);
    }

    public Transfer getTransfer() {
        return (transfer);
    }

    public void setTransfer(Transfer t) {
        transfer = t;
    }

    private Integer[] getTimeline() {
        return (timeline);
    }

    private void setTimeline(Integer[] array) {

        timeline = array;

        if (array != null) {

            // The time line set here includes starts and stops of
            // commercials.  We are going to only keep the commercial
            // stops - in other words just when the show returns.
            timeline = new Integer[array.length / 2];
            int index = 0;
            for (int i = 1; i < array.length; i++) {

                if ((i % 2) == 1) {
                    log(DEBUG, "timeline: " + array[i].intValue());
                    timeline[index++] = array[i];
                }
            }

        } else {

            timeline = null;
        }
    }

    @Override
    public Player getPlayer() {
        return (getPlayer(getStreamType()));
    }

    /**
     * {@inheritDoc}
     */
    public Recording[] getRecordings() {

        Recording[] result = null;

        if (recordings != null) {

            result = Arrays.copyOf(recordings, recordings.length);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void setRecordings(Recording[] array) {

        if (array != null) {
            recordings = Arrays.copyOf(array, array.length);
        } else {
            recordings = null;
        }

        RecordingListPanel group = getGroupRecordingListPanel();
        RecordingListPanel rlp = getRecordingListPanel();
        if ((group != null) && (rlp != null)) {

            // If we are currently "working" then the user probably just
            // did a delete.  We want to save the current state so we
            // can restore it.
            if (!isDone()) {
                preserveState();
            }
            ArrayList<Recording> list = new ArrayList<Recording>();
            if (recordings != null) {

                // Here we only add one of each "title" of recording.  What
                // I mean is if we have 7 "Leave it to Beaver" then we will
                // only put one (the first one found) in this list.
                for (int i = 0; i < recordings.length; i++) {

                    Recording tmp = recordings[i];
                    if (!contains(list, tmp)) {

                        list.add(tmp);
                    }
                }

                Collections.sort(list, new RecordingSortByTitle());

                Recording week = new Recording();
                week.setTitle("Last Week Only");
                list.add(0, week);

                Recording yesterday = new Recording();
                yesterday.setTitle("Yesterday Only");
                list.add(0, yesterday);

                Recording today = new Recording();
                today.setTitle("Today Only");
                list.add(0, today);

                Recording all = new Recording();
                all.setTitle("All");
                list.add(0, all);
            }

            // If we had any Recordings in the array this will hold at least
            // the "All" fake Recording.
            if (list.size() > 0) {

                Recording[] newarray = list.toArray(new Recording[list.size()]);
                group.setRecordings(newarray);

            } else {

                group.setRecordings(null);
            }

            if (isRestoreState()) {

                //setRestoreState(false);
                // Even though the chances that the group selection changed,
                // we need to force a property update because the best way
                // to update is via the property change.
                group.setSelectedRecording(null);
                group.setSelectedIndex(getCurrentGroupIndex());
                group.setStartIndex(getCurrentGroupStartIndex());
                //rlp.setSelectedIndex(getCurrentRecordingIndex());
                //rlp.setStartIndex(getCurrentRecordingStartIndex());

            } else {

                // This is easy, the user probably just went to this screen.
                // So we just set it to the "beginning".
                group.setSelectedIndex(0);
                group.setStartIndex(0);
                rlp.setSelectedIndex(0);
                rlp.setStartIndex(0);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateRecording(Recording r) {

        log(DEBUG, "updateRecording: " + r);
        if (r != null) {

            // First thing is to update the Recording in our array.
            int index = getRecordingById(r.getId());
            log(DEBUG, "updateRecording: index " + index);
            if (index != -1) {

                recordings[index] = r;

                // Next we update the UI components that have Recording
                // instances.
                RecordingDetailPanel rdp = getRecordingDetailPanel();
                if (rdp != null) {

                    if (r.equals(rdp.getRecording())) {

                        log(DEBUG, "updateRecording: selected ");
                        rdp.setRecording(r);

                        ImageCache ic = getImageCache();
                        JXPanel ssp = getScreenShotPanel();
                        if ((ic != null) && (ssp != null)) {

                            BufferedImage bi = findBufferedImage(ic, r);
                            if (bi != null) {

                                ImagePainter painter =
                                    (ImagePainter) ssp.getBackgroundPainter();
                                if (painter != null) {

                                    painter.setImage(bi);

                                } else {

                                    painter = new ImagePainter(bi);
                                    painter.setScaleToFit(true);
                                    ssp.setBackgroundPainter(painter);
                                }

                                repaint();
                            }
                        }
                    }
                }

                RecordingListPanel gllp = getGroupRecordingListPanel();
                if (gllp != null) {

                    gllp.updateRecording(r);
                }

                RecordingListPanel rllp = getRecordingListPanel();
                if (rllp != null) {

                    rllp.updateRecording(r);
                }

                RecordingInfoWindow w = getRecordingInfoWindow();
                if (w != null) {

                    if (r.equals(w.getRecording())) {

                        w.setRecording(r);
                    }
                }
            }
        }
    }

    private Recording[] getLastWeekRecordings() {

        long now = System.currentTimeMillis();
        return (getTimeRecordings(now, now - (7 * 24 * 60 * 60 * 1000)));
    }

    private Recording[] getTodayRecordings() {

        long now = System.currentTimeMillis();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        return (getTimeRecordings(now, c.getTimeInMillis()));
    }

    private Recording[] getYesterdayRecordings() {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        long midnight = c.getTimeInMillis();

        return (getTimeRecordings(midnight, midnight - (24 * 60 * 60 * 1000)));
    }

    private Recording[] getTimeRecordings(long newest, long oldest) {

        Recording[] result = null;

        Recording[] all = getRecordings();
        if ((all != null) && (all.length > 0)) {

            ArrayList<Recording> rlist = new ArrayList<Recording>();
            for (int i = 0; i < all.length; i++) {

                Date date = all[i].getDate();
                if (date != null) {

                    long time = date.getTime();
                    if ((oldest < time) && (time <= newest)) {

                        rlist.add(all[i]);
                    }
                }
            }

            if (rlist.size() > 0) {

                result = rlist.toArray(new Recording[rlist.size()]);
            }
        }

        return (result);
    }

    private int getRecordingById(String s) {

        int result = -1;

        if ((s != null) && (recordings != null)) {

            for (int i = 0; i < recordings.length; i++) {

                if (s.equals(recordings[i].getId())) {

                    result = i;
                    break;
                }
            }
        }

        return (result);
    }

    private boolean contains(ArrayList<Recording> l, Recording r) {

        boolean result = false;

        if ((l != null) && (r != null)) {

            String title = r.getTitle();
            if (title != null) {

                for (int i = 0; i < l.size(); i++) {

                    if (title.equals(l.get(i).getTitle())) {

                        result = true;
                        break;
                    }
                }
            }
        }

        return (result);
    }

    private boolean equals(Recording first, Recording second) {

        boolean result = false;

        if ((first != null) && (second != null)) {

            String title = first.getTitle();
            if (title != null) {

                result = title.equals(second.getTitle());
            }
        }

        return (result);
    }

    private RecordingListPanel getGroupRecordingListPanel() {
        return (groupRecordingListPanel);
    }

    private void setGroupRecordingListPanel(RecordingListPanel p) {
        groupRecordingListPanel = p;
    }

    private RecordingListPanel getRecordingListPanel() {
        return (recordingListPanel);
    }

    private void setRecordingListPanel(RecordingListPanel p) {
        recordingListPanel = p;
    }

    private RecordingDetailPanel getRecordingDetailPanel() {
        return (recordingDetailPanel);
    }

    private void setRecordingDetailPanel(RecordingDetailPanel p) {
        recordingDetailPanel = p;
    }

    private JXPanel getScreenShotPanel() {
        return (screenShotPanel);
    }

    private void setScreenShotPanel(JXPanel p) {
        screenShotPanel = p;
    }

    private Animator getScreenShotAnimator() {
        return (screenShotAnimator);
    }

    private void setScreenShotAnimator(Animator a) {
        screenShotAnimator = a;
    }

    private RecordingInfoWindow getRecordingInfoWindow() {
        return (recordingInfoWindow);
    }

    private void setRecordingInfoWindow(RecordingInfoWindow w) {
        recordingInfoWindow = w;
    }

    private int getCurrentGroupIndex() {
        return (currentGroupIndex);
    }

    private void setCurrentGroupIndex(int i) {
        currentGroupIndex = i;
    }

    private int getCurrentRecordingIndex() {
        return (currentRecordingIndex);
    }

    private void setCurrentRecordingIndex(int i) {
        currentRecordingIndex = i;
    }

    private int getCurrentGroupStartIndex() {
        return (currentGroupStartIndex);
    }

    private void setCurrentGroupStartIndex(int i) {
        currentGroupStartIndex = i;
    }

    private int getCurrentRecordingStartIndex() {
        return (currentRecordingStartIndex);
    }

    private void setCurrentRecordingStartIndex(int i) {
        currentRecordingStartIndex = i;
    }

    private Recording getCurrentRecording() {
        return (currentRecording);
    }

    private void setCurrentRecording(Recording r) {
        currentRecording = r;
    }

    private boolean isRestoreState() {
        return (restoreState);
    }

    private void setRestoreState(boolean b) {
        restoreState = b;
    }

    private boolean isAudioControl() {
        return (audioControl);
    }

    private void setAudioControl(boolean b) {
        audioControl = b;
    }

    private boolean isAutoSkip() {
        return (autoSkip);
    }

    private void setAutoSkip(boolean b) {
        autoSkip = b;
    }

    private AutoSkipActionListener getAutoSkipActionListener() {
        return (autoSkipActionListener);
    }

    private void setAutoSkipActionListener(AutoSkipActionListener l) {
        autoSkipActionListener = l;
    }

    private Timer getAutoSkipTimer() {
        return (autoSkipTimer);
    }

    private void setAutoSkipTimer(Timer t) {
        autoSkipTimer = t;
    }

    private int getLastScreenEvent() {
        return (lastScreenEvent);
    }

    private void setLastScreenEvent(int i) {
        lastScreenEvent = i;
    }

    private Recording getCertainRecording() {
        return (certainRecording);
    }

    private void setCertainRecording(Recording r) {
        certainRecording = r;
    }

    private void preserveState() {

        log(DEBUG, "preserveState");
        RecordingListPanel group = getGroupRecordingListPanel();
        RecordingListPanel rlp = getRecordingListPanel();
        if ((group != null) && (rlp != null)) {

            setCurrentGroupIndex(group.getSelectedIndex());
            setCurrentGroupStartIndex(group.getStartIndex());
            setCurrentRecordingIndex(rlp.getSelectedIndex());
            setCurrentRecordingStartIndex(rlp.getStartIndex());
            setRestoreState(true);
            log(DEBUG, "preserveState group: " + getCurrentGroupIndex());
            log(DEBUG, "preserveState rec: " + getCurrentRecordingIndex());
        }
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
            int listwidth = (width - (3 * wspan)) / 2;

            int hspan = (int) (height * 0.03);
            int listheight = (int) ((height - (3 * hspan)) / 1.5);

            int detailwidth = (int) (width * 0.63);
            int detailheight = (height - (4 * hspan)) - listheight;

            RecordingListPanel gllp = new RecordingListPanel();
            gllp.setAlpha(alpha);

            gllp.addPropertyChangeListener("SelectedRecording", this);
            gllp.setUseTitle(true);
            gllp.setCompleteDescription(false);
            gllp.setControl(true);

            setGroupRecordingListPanel(gllp);

            RecordingListPanel rllp = new RecordingListPanel();
            rllp.setAlpha(alpha);

            rllp.addPropertyChangeListener("SelectedRecording", this);
            setRecordingListPanel(rllp);

            RecordingDetailPanel dp = new RecordingDetailPanel();
            dp.setAlpha(alpha);
            setRecordingDetailPanel(dp);

            JXPanel ssp = new JXPanel();
            setScreenShotPanel(ssp);

            TimingTarget tt = PropertySetter.getTarget(ssp, "alpha",
                Float.valueOf(0.0f), Float.valueOf(1.0f));
            Animator sani =
                new Animator.Builder().setDuration(250,
                    TimeUnit.MILLISECONDS).addTarget(tt).build();
            setScreenShotAnimator(sani);

            gllp.setBounds(wspan, hspan, listwidth, listheight);
            rllp.setBounds(wspan + wspan + listwidth, hspan, listwidth,
                listheight);
            dp.setBounds(wspan, hspan + hspan + listheight, detailwidth,
                detailheight);
            ssp.setBounds(wspan + detailwidth + wspan, hspan + hspan
                + listheight, detailheight * 16 / 9, detailheight);

            pane.add(gllp, Integer.valueOf(100));
            pane.add(rllp, Integer.valueOf(100));
            pane.add(dp, Integer.valueOf(100));
            pane.add(ssp, Integer.valueOf(100));

            FrontEndView fev = (FrontEndView) getView();
            setRecordingInfoWindow(new RecordingInfoWindow(
                fev.getPosition(), 8, getInfoColor(),
                getPanelColor(), (float) getPanelAlpha(),
                getSmallFont(), getMediumFont()));
            getRecordingInfoWindow().setVisible(false);

            setDefaultBackgroundImage(
                Util.resize(getDefaultBackgroundImage(), width, height));

            // Create our blank panel.
            JXPanel blank = new JXPanel(new BorderLayout());
            JXLabel l = new JXLabel("Preparing to play, please wait...");
            l.setHorizontalTextPosition(SwingConstants.CENTER);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setFont(getLargeFont());
            blank.add(l, BorderLayout.CENTER);
            MattePainter blankp = new MattePainter(Color.BLACK);
            blank.setBackgroundPainter(blankp);
            blank.setBounds(0, 0, width, height);
            blank.setCursor(Util.getNoCursor());
            setBlankPanel(blank);
        }

    }

    /**
     * {@inheritDoc}
     */
    public Bookmark createBookmark() {

        Bookmark result = null;

        RecordingListPanel rllp = getRecordingListPanel();
        Player p = getPlayer();
        if ((p != null) && (rllp != null) && (rllp.isControl())) {

            PlayState ps = p.getPlayState();
            Recording r = rllp.getSelectedRecording();
            if ((r != null) && (ps != null)) {

                updateLengthHint(r, p);
                result = new Bookmark();
                result.setTime((int) ps.getTime());
                result.setPosition(ps.getPosition());
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public String getBookmarkId() {

        String result = null;

        Recording r = getCurrentRecording();
        if (r != null) {
            result = r.getId();
        } else {
            result = getRecordingListBookmarkId();
        }

        return (result);
    }

    private String getRecordingListBookmarkId() {

        String result = null;

        RecordingListPanel rllp = getRecordingListPanel();
        if (rllp != null) {

            Recording r = rllp.getSelectedRecording();
            if (r != null) {
                result = r.getId();
            }
        }

        return (result);
    }

    private boolean isSelectedRecordingNow() {

        boolean result = false;

        RecordingListPanel rllp = getRecordingListPanel();
        if (rllp != null) {

            Recording r = rllp.getSelectedRecording();
            if (r != null) {

                result = r.isCurrentlyRecording();
            }
        }

        return (result);
    }

    private void updateLengthHint(Recording r, Player p) {

        if ((r != null) && (p != null)) {

            if (r.isCurrentlyRecording()) {

                p.setLengthHint(System.currentTimeMillis() - r.getRealStart());

            } else {

                p.setLengthHint(r.getDuration());
            }
        }
    }

    private BufferedImage findBufferedImage(ImageCache ic, Recording r) {

        BufferedImage result = null;

        if ((ic != null) && (r != null)) {

            String path = r.getPath();
            System.out.println("path <" + path + ">");
            File tmp = new File(path);
            if ((tmp.exists()) && (tmp.isFile())) {

                if (Util.isWindows()) {

                    path = path.replace(":", "|");
                    path = "/" + path;
                }

                result = ic.getImage("file://" + path + ".png", false);
                if (result == null) {

                    path = path.substring(0, path.length() - 4);
                    result = ic.getImage("file://" + path + ".png", false);
                }

            } else {

                // We have to build a URL from the StreamURL.  However the
                // screen shot is based upon the raw video file name not
                // from the "indexed" video version.  So we have to do some
                // trickery here.
                String surl = r.getStreamURL();
                System.out.println("streamurl <" + surl + ">");
                if (surl != null) {

                    String iext = r.getIndexedExtension();
                    if ((iext != null) && (surl.endsWith(iext))) {

                        int ilength = iext.length() + 1;
                        surl = surl.substring(0, surl.length() - ilength);
                    }

                    System.out.println("streamurl GERN BLANK <" + surl + ">");
                    result = ic.getImage(surl + ".png", false);
                }
            }
        }

        return (result);
    }

    private String computeRecordingPath(Recording r) {

        String result = null;

        if (r != null) {

            // Default to the path property.
            result = r.getPath();

            // Audio control is just on for transport streams so we dont
            // append the extension unless audio control is off.
            String iext = r.getIndexedExtension();
            if ((iext != null) && (!isAudioControl())) {

                File tmp = new File(result + "." + iext);
                if ((tmp.exists()) && (tmp.isFile())) {

                    result = tmp.getPath();
                }
            }

            String streamURL = r.getStreamURL();
            if (streamURL != null) {

                File tmp = new File(result);
                if ((tmp.exists()) && (tmp.isFile())) {
                } else {

                    // We use the streamURL.  If we got here then we have to
                    // recheck the ext type of the URL.
                    result = streamURL;

                    System.out.println("iext <" + iext + ">");
                    System.out.println("streamURL <" + streamURL + ">");
                    if (iext != null) {

                        if (streamURL.endsWith(iext)) {

                            if (isAudioControl()) {

                                result = result.substring(0,
                                    result.lastIndexOf("."));
                                System.out.println("ts  - audio control on");
                                System.out.println(result);

                            } else {
                                System.out.println("saying video");
                            }
                        }
                    }

                    // If the Recording is done, then we are done, we just
                    // use the streamURL.  But we don't want to use the
                    // streamURL if the Recording is not done as it's
                    // problematic.  Try to transfer the file and then play
                    // our local copy.
                    Transfer t = getTransfer();
                    if ((t != null) && (r.isCurrentlyRecording())) {

                        result = t.transfer(r, -30, 20);
                        setMarkTime(r.getRealStart());
                    }
                }
            }
        }

        if (result != null) {

            if (result.endsWith("ts")) {
                setStreamType(Player.PLAYER_VIDEO_TRANSPORT_STREAM);
            } else if (result.endsWith("mpg")) {
                setStreamType(Player.PLAYER_VIDEO_PROGRAM_STREAM);
            } else {
                setStreamType(Player.PLAYER_VIDEO);
            }
        }

        return (result);
    }

    /**
     * We listen for property change events from the panels that deal
     * with selecting a recording.
     *
     * @param event A given PropertyChangeEvent instance.
     */
    public void propertyChange(PropertyChangeEvent event) {

        if (event.getSource() == getGroupRecordingListPanel()) {

            Recording r = (Recording) event.getNewValue();
            if (r != null) {

                int rcount = 0;
                String title = r.getTitle();
                RecordingListPanel rlp = getRecordingListPanel();
                if ((title != null) && (rlp != null)) {

                    if (title.equals("All")) {

                        rlp.setCompleteDescription(true);
                        Recording[] myrec = getRecordings();
                        if (myrec != null) {
                            rcount = myrec.length;
                        }
                        rlp.setRecordings(myrec);

                    } else if (title.equals("Last Week Only")) {

                        rlp.setCompleteDescription(true);
                        Recording[] weekrec = getLastWeekRecordings();
                        if (weekrec != null) {
                            rcount = weekrec.length;
                        }
                        rlp.setRecordings(weekrec);

                    } else if (title.equals("Today Only")) {

                        rlp.setCompleteDescription(true);
                        Recording[] todayrec = getTodayRecordings();
                        if (todayrec != null) {
                            rcount = todayrec.length;
                        }
                        rlp.setRecordings(todayrec);

                    } else if (title.equals("Yesterday Only")) {

                        rlp.setCompleteDescription(true);
                        Recording[] yesterdayrec = getYesterdayRecordings();
                        if (yesterdayrec != null) {
                            rcount = yesterdayrec.length;
                        }
                        rlp.setRecordings(yesterdayrec);

                    } else {

                        Recording[] recs = getRecordings();
                        if (recs != null) {

                            ArrayList<Recording> rlist =
                                new ArrayList<Recording>();
                            for (int i = 0; i < recs.length; i++) {

                                if (equals(r, recs[i])) {

                                    rlist.add(recs[i]);
                                }
                            }

                            rlp.setCompleteDescription(false);
                            rcount = rlist.size();
                            if (rlist.size() > 0) {

                                rlp.setRecordings(rlist.toArray(
                                    new Recording[rlist.size()]));

                            } else {

                                rlp.setRecordings(null);
                            }
                        }
                    }

                    if (isRestoreState()) {

                        // The user deleted a recording so we need to return
                        // to a state that is most reasonable.  First we are
                        // done after this few lines of code so reset our
                        // restore boolean.
                        setRestoreState(false);

                        // We "remembered" the start index before we deleted.
                        // Now we want to restore to that index but only if
                        // we have a full list.  The delete may make us one
                        // short and we want to scroll down if we can.
                        int old = getCurrentRecordingStartIndex();
                        if (old > 0) {

                            log(DEBUG, "rcount: " + rcount);
                            if (old + rlp.getVisibleCount() > rcount) {

                                old--;
                            }
                        }
                        rlp.setStartIndex(old);

                        // We saved the old index.  If we were at the bottom,
                        // the RecordingListPanel will fix an out of range
                        // value so we don't need to be careful here.
                        rlp.setSelectedIndex(getCurrentRecordingIndex());

                    } else {

                        rlp.setSelectedIndex(0);
                        rlp.setStartIndex(0);
                    }
                }
            }

        } else if (event.getSource() == getRecordingListPanel()) {

            RecordingDetailPanel dp = getRecordingDetailPanel();
            if (dp != null) {

                Recording r = (Recording) event.getNewValue();
                ImageCache ic = getImageCache();
                if ((ic != null) && (r != null)) {

                    BufferedImage bi = ic.getImage(r.getFanartURL(), false);
                    if (bi != null) {

                        Dimension d = getSize();
                        if (d != null) {

                            if (bi.getWidth() < d.getWidth()) {

                                bi = Util.scaleLarger((int) d.getWidth(), bi);
                            }
                            setCurrentBackgroundImage(bi);

                        } else {

                            setCurrentBackgroundImage(
                                getDefaultBackgroundImage());
                        }

                    } else {

                        setCurrentBackgroundImage(getDefaultBackgroundImage());
                    }

                    JXPanel ssp = getScreenShotPanel();
                    if (ssp != null) {

                        bi = findBufferedImage(ic, r);
                        if (bi != null) {

                            ImagePainter painter =
                                (ImagePainter) ssp.getBackgroundPainter();
                            if (painter != null) {

                                painter.setImage(bi);

                            } else {

                                painter = new ImagePainter(bi);
                                painter.setScaleToFit(true);
                                ssp.setBackgroundPainter(painter);
                            }

                            if (isEffects()) {

                                Animator sani = getScreenShotAnimator();
                                if (sani != null) {

                                    if (sani.isRunning()) {
                                        sani.stop();
                                    }

                                    sani.start();
                                }
                            }

                        } else {

                            ImagePainter painter =
                                (ImagePainter) ssp.getBackgroundPainter();
                            if (painter != null) {

                                painter.setImage(null);
                            }
                        }
                    }

                }

                dp.setRecording(r);
            }

        } else if ((event.getSource() == getPlayer()) && (!isDone())) {

            Boolean bobj = (Boolean) event.getNewValue();
            if (bobj.booleanValue()) {

                // If we get this property update, then it means the video
                // finished playing on it's own.
                setBlocking(false);
                getPlayer().removePropertyChangeListener(this);
                RecordingListPanel rllp = getRecordingListPanel();
                if (rllp != null) {

                    Recording r = rllp.getSelectedRecording();
                    if (r != null) {

                        close();
                        deleteBookmark(r.getId());
                    }
                }

                log(DEBUG, "about to request focus");
                requestFocus();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void info() {

        RecordingInfoWindow w = getRecordingInfoWindow();
        System.out.println("RecordingInfoWindow " + w);
        if (w != null) {

            System.out.println("RecordingInfoWindow " + w.isVisible());
            w.setVisible(!w.isVisible());
            System.out.println("RecordingInfoWindow " + w.isVisible());
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

        removeBlankPanel();
        controlKeyboard(true);
        setCurrentRecording(null);
        RecordingInfoWindow w = getRecordingInfoWindow();
        if (w != null) {

            w.setVisible(false);
        }

        Transfer t = getTransfer();
        if (t != null) {
            t.transfer(null, 0, 0);
        }

        Timer timer = getAutoSkipTimer();
        if (timer != null) {

            timer.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void rewind() {

        Player p = getPlayer();
        if (p != null) {

            updateLengthHint(getCurrentRecording(), p);
            p.seek(-8);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void forward() {

        Player p = getPlayer();
        Recording cr = getCurrentRecording();
        if ((p != null) && (cr != null)) {

            updateLengthHint(cr, p);
            int left = leftToGo(p, cr);
            if (left > 30) {
                p.seek(30);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void skipforward() {

        Player p = getPlayer();
        if (p != null) {

            PlayState ps = p.getPlayState();
            Recording cr = getCurrentRecording();
            if ((ps != null) && (cr != null)) {

                int current = (int) ps.getTime();
                int next = Commercial.whereNext(getTimeline(), current);
                if (next != current) {

                    updateLengthHint(cr, p);
                    int diff = next - current;
                    int left = leftToGo(p, cr);
                    if (left > diff) {
                        p.seek(diff);
                    }

                } else {

                    log(INFO, "commercials not set or end, skipping 10 min");

                    updateLengthHint(cr, p);
                    int left = leftToGo(p, cr);
                    if (left > 600) {
                        p.seek(600);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void skipbackward() {

        Player p = getPlayer();
        if (p != null) {

            PlayState ps = p.getPlayState();
            if (ps != null) {

                int current = (int) ps.getTime();
                int back = Commercial.wherePrevious(getTimeline(), current);
                if (back != current) {

                    updateLengthHint(getCurrentRecording(), p);
                    //back = Commercial.wherePrevious(getTimeline(), back);
                    //p.seekPosition(back);
                    p.seek((back - current) - 4);

                } else {

                    log(INFO, "commercials not set or end, going back 10 min");
                    updateLengthHint(getCurrentRecording(), p);
                    p.seek(-600);
                }
            }
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

            updateLengthHint(getCurrentRecording(), p);
            p.seek(-8);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void right() {

        Player p = getPlayer();
        Recording r = getCurrentRecording();
        if ((p != null) && (r != null)) {

            updateLengthHint(r, p);
            int left = -1;
            if (r.isCurrentlyRecording()) {
                left = leftToGo(p, getMarkTime());
            } else {
                left = leftToGo(p, r);
            }

            if (left > 30) {
                p.seek(30);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void enter() {
    }

    /**
     * We need to listen for events from the "play popup dialog".
     *
     * @param event A given event.
     */
    public void actionPerformed(ActionEvent event) {

        RecordingListPanel rllp = getRecordingListPanel();
        if ((rllp != null) && (event.getSource() == getPlayButtonPanel())) {

            Recording r = rllp.getSelectedRecording();
            Player p = getPlayer();
            if ((p != null) && (!p.isPlaying()) && (r != null)) {

                boolean doUnpopup = true;
                boolean certainPopup = false;

                ButtonPanel pbp = getPlayButtonPanel();
                if (PLAY.equals(pbp.getSelectedButton())) {

                    final Recording myr = r;
                    final RecordingScreen myscreen = this;
                    addBlankPanel();
                    Runnable doRun = new Runnable() {

                        public void run() {

                            String recpath = computeRecordingPath(myr);
                            Player p = getPlayer();
                            View v = getView();
                            if (v instanceof FrontEndView) {

                                FrontEndView fev = (FrontEndView) v;
                                p.setRectangle(fev.getPosition());
                            }

                            p.addPropertyChangeListener("Completed", myscreen);
                            RecordingInfoWindow w = getRecordingInfoWindow();
                            if (w != null) {

                                w.setVisible(false);
                                w.setImageCache(getImageCache());
                                w.setRecording(myr);
                                w.setPlayer(p);
                            }

                            setTimeline(Commercial.timeline(myr.getCommercials()));
                            setCurrentRecording(myr);
                            updateLengthHint(myr, p);
                            controlKeyboard(false);

                            if (isAutoSkip()) {

                                AutoSkipActionListener asal = getAutoSkipActionListener();
                                Timer timer = getAutoSkipTimer();
                                if ((asal != null) && (timer != null)) {

                                    asal.setRecording(myr);
                                    timer.start();
                                }
                            }

                            p.setFrame(Util.findFrame(myscreen));
                            setBlocking(true);
                            p.play(recpath);
                        }
                    };
                    SwingUtilities.invokeLater(doRun);

                } else if (PLAY_FROM_BOOKMARK.equals(pbp.getSelectedButton())) {

                    final Recording myr = r;
                    final RecordingScreen myscreen = this;
                    addBlankPanel();
                    Runnable doRun = new Runnable() {

                        public void run() {

                            String recpath = computeRecordingPath(myr);
                            Player p = getPlayer();
                            View v = getView();
                            if (v instanceof FrontEndView) {

                                FrontEndView fev = (FrontEndView) v;
                                p.setRectangle(fev.getPosition());
                            }

                            p.addPropertyChangeListener("Completed", myscreen);
                            RecordingInfoWindow w = getRecordingInfoWindow();
                            if (w != null) {

                                w.setVisible(false);
                                w.setImageCache(getImageCache());
                                w.setRecording(myr);
                                w.setPlayer(p);
                            }

                            setTimeline(Commercial.timeline(myr.getCommercials()));
                            setCurrentRecording(myr);
                            updateLengthHint(myr, p);

                            Bookmark bm = getBookmark(myr.getId());
                            if (bm != null) {

                                if (isPlayingVideo()) {

                                    bm.setPreferTime(true);

                                } else {

                                    bm.setPreferTime(false);
                                }

                                if (isAutoSkip()) {


                                    AutoSkipActionListener asal =
                                        getAutoSkipActionListener();
                                    Timer timer = getAutoSkipTimer();
                                    if ((asal != null) && (timer != null)) {

                                        asal.setRecording(myr);
                                        timer.start();
                                    }
                                }

                                controlKeyboard(false);
                                p.setFrame(Util.findFrame(myscreen));
                                setBlocking(true);
                                p.play(recpath, bm);
                            }
                        }
                    };
                    SwingUtilities.invokeLater(doRun);

                } else if (DELETE.equals(pbp.getSelectedButton())) {

                    log(DEBUG, "firing delete recording after certain");
                    certainPopup = true;
                    setLastScreenEvent(ScreenEvent.DELETE_RECORDING);
                    setCertainRecording(r);

                } else if (DELETE_ALLOW_RERECORDING.equals(
                    pbp.getSelectedButton())) {

                    log(DEBUG, "firing delete recording - allow after certain");
                    certainPopup = true;
                    setLastScreenEvent(
                        ScreenEvent.DELETE_RECORDING_ALLOW_RERECORDING);
                    setCertainRecording(r);

                } else if (STOP_RECORDING.equals(pbp.getSelectedButton())) {

                    log(DEBUG, "firing stop recording after certain");
                    certainPopup = true;
                    setLastScreenEvent(ScreenEvent.STOP_RECORDING);
                    setCertainRecording(r);

                } else if (CERTAIN.equals(pbp.getSelectedButton())) {

                    fireScreenEvent(getLastScreenEvent(), getCertainRecording());

                } else if (CANCEL.equals(pbp.getSelectedButton())) {

                    log(DEBUG, "cancel hit");

                } else {

                    String btext = pbp.getSelectedButton();
                    if (btext.startsWith(AUTO_SKIP_PREFIX)) {

                        boolean old = isAutoSkip();
                        setAutoSkip(!isAutoSkip());
                        doUnpopup = false;
                        String[] barray = pbp.getButtons();
                        if ((barray != null) && (barray.length > 2)) {
                            if (old) {
                                barray[barray.length - 2] = AUTO_SKIP_IS_OFF;
                            } else {
                                barray[barray.length - 2] = AUTO_SKIP_IS_ON;
                            }
                            pbp.setButtons(barray);
                        }

                    } else if (btext.startsWith(AUDIO_CONTROL_PREFIX)) {

                        boolean old = isAudioControl();
                        setAudioControl(!isAudioControl());
                        doUnpopup = false;
                        String[] barray = pbp.getButtons();
                        if ((barray != null) && (barray.length > 3)) {

                            if (old) {
                                barray[barray.length - 3] = AUDIO_CONTROL_IS_OFF;
                            } else {
                                barray[barray.length - 3] = AUDIO_CONTROL_IS_ON;
                            }
                            pbp.setButtons(barray);
                        }
                    }
                }

                if (doUnpopup) {

                    unpopup();
                }

                if (certainPopup) {

                    // We have an action we want to confirm.
                    ArrayList<String> blist = new ArrayList<String>();
                    blist.add(CERTAIN);
                    blist.add(CANCEL);
                    String[] barray = blist.toArray(new String[blist.size()]);
                    popup(barray);
                }
            }
        }
    }

    class LeftAction extends AbstractAction {

        public LeftAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (!isPopupEnabled()) {

                RecordingListPanel gllp = getGroupRecordingListPanel();
                if (gllp != null) {

                    gllp.setControl(true);
                }

                RecordingListPanel rllp = getRecordingListPanel();
                if (rllp != null) {

                    rllp.setControl(false);
                }
            }

            fireScreenEvent(ScreenEvent.USER_INPUT);
        }
    }

    class RightAction extends AbstractAction {

        public RightAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (!isPopupEnabled()) {

                RecordingListPanel gllp = getGroupRecordingListPanel();
                if (gllp != null) {

                    gllp.setControl(false);
                }

                RecordingListPanel rllp = getRecordingListPanel();
                if (rllp != null) {

                    rllp.setControl(true);
                }
            }

            fireScreenEvent(ScreenEvent.USER_INPUT);
        }
    }

    class UpAction extends AbstractAction {

        public UpAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (isPopupEnabled()) {

                ButtonPanel bp = getPlayButtonPanel();
                if (bp != null) {

                    bp.moveUp();
                }

            } else {

                RecordingListPanel gllp = getGroupRecordingListPanel();
                if (gllp != null) {

                    if (gllp.isControl()) {
                        gllp.moveUp();
                    }
                }

                RecordingListPanel rllp = getRecordingListPanel();
                if (rllp != null) {

                    if (rllp.isControl()) {
                        rllp.moveUp();
                    }
                }
            }

            fireScreenEvent(ScreenEvent.USER_INPUT);
        }
    }

    class DownAction extends AbstractAction {

        public DownAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (isPopupEnabled()) {

                ButtonPanel bp = getPlayButtonPanel();
                if (bp != null) {

                    bp.moveDown();
                }

            } else {

                RecordingListPanel gllp = getGroupRecordingListPanel();
                if (gllp != null) {

                    if (gllp.isControl()) {
                        gllp.moveDown();
                    }
                }

                RecordingListPanel rllp = getRecordingListPanel();
                if (rllp != null) {

                    if (rllp.isControl()) {
                        rllp.moveDown();
                    }
                }
            }

            fireScreenEvent(ScreenEvent.USER_INPUT);
        }
    }

    class PageUpAction extends AbstractAction {

        public PageUpAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (!isPopupEnabled()) {

                RecordingListPanel gllp = getGroupRecordingListPanel();
                if (gllp != null) {

                    if (gllp.isControl()) {
                        gllp.movePageUp();
                    }
                }

                RecordingListPanel rllp = getRecordingListPanel();
                if (rllp != null) {

                    if (rllp.isControl()) {
                        rllp.movePageUp();
                    }
                }
            }

            fireScreenEvent(ScreenEvent.USER_INPUT);
        }
    }

    class PageDownAction extends AbstractAction {

        public PageDownAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (!isPopupEnabled()) {

                RecordingListPanel gllp = getGroupRecordingListPanel();
                if (gllp != null) {

                    if (gllp.isControl()) {
                        gllp.movePageDown();
                    }
                }

                RecordingListPanel rllp = getRecordingListPanel();
                if (rllp != null) {

                    if (rllp.isControl()) {
                        rllp.movePageDown();
                    }
                }
            }

            fireScreenEvent(ScreenEvent.USER_INPUT);
        }
    }

    class EnterAction extends AbstractAction {

        public EnterAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (!isPopupEnabled()) {

                RecordingListPanel rllp = getRecordingListPanel();
                if (rllp != null) {

                    if (rllp.isControl()) {

                        ArrayList<String> blist = new ArrayList<String>();
                        blist.add(PLAY);
                        if (hasBookmark(getBookmarkId())) {
                            blist.add(PLAY_FROM_BOOKMARK);
                        }
                        if (isSelectedRecordingNow()) {
                            blist.add(STOP_RECORDING);
                            blist.add(DELETE);
                        } else {
                            blist.add(DELETE);
                            blist.add(DELETE_ALLOW_RERECORDING);
                        }
                        if (isAudioControl()) {
                            blist.add(AUDIO_CONTROL_IS_ON);
                        } else {
                            blist.add(AUDIO_CONTROL_IS_OFF);
                        }
                        if (isAutoSkip()) {
                            blist.add(AUTO_SKIP_IS_ON);
                        } else {
                            blist.add(AUTO_SKIP_IS_OFF);
                        }
                        blist.add(CANCEL);

                        String[] barray = blist.toArray(new String[blist.size()]);
                        popup(barray);
                    }
                }
            }

            fireScreenEvent(ScreenEvent.USER_INPUT);
        }
    }

    static class RecordingSortByTitle implements Comparator<Recording>,
        Serializable {

        public int compare(Recording r0, Recording r1) {

            String title0 = Util.toSortableTitle(r0.getTitle());
            String title1 = Util.toSortableTitle(r1.getTitle());

            return (title0.compareTo(title1));
        }
    }

    class AutoSkipActionListener implements ActionListener {

        private Recording recording;

        public AutoSkipActionListener() {
        }

        public Recording getRecording() {
            return (recording);
        }

        public void setRecording(Recording r) {
            recording = r;
        }

        private boolean isTimeToSkip(double current, double start, double end) {

            return ((current >= start) && (current < end));
        }

        private boolean isTimeToSkip(double current, Integer[] array) {

            boolean result = false;

            if ((array != null) && (array.length > 0)) {

                for (int i = 0; i < array.length; i += 2) {

                    // Only skip at the start of a commercial.  If it
                    // skips too far then the user can't "fix it" by
                    // skipping back a bit.
                    double start = array[i].doubleValue();
                    double end = start + 7;
                    if (start > 0.0) {

                        if (isTimeToSkip(current, start, end)) {

                            result = true;
                            break;
                        }
                    }
                }
            }

            return (result);
        }

        public void actionPerformed(ActionEvent event) {

            Player p = getPlayer();
            Recording r = getRecording();
            if ((r != null) && (p != null) && (p.isPlaying())) {

                Commercial[] carray = r.getCommercials();
                if ((carray != null) && (carray.length > 0)) {

                    Integer[] timeline = Commercial.timeline(carray);
                    PlayState ps = p.getPlayState();
                    if ((ps != null) && (timeline != null)) {

                        double current = ps.getTime();
                        if (isTimeToSkip(current, timeline)) {

                            skipforward();
                        }
                    }
                }
            }
        }
    }

}


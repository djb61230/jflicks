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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;

import org.jflicks.imagecache.ImageCache;
import org.jflicks.mvc.View;
import org.jflicks.player.Bookmark;
import org.jflicks.player.Player;
import org.jflicks.player.PlayState;
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
import org.jflicks.util.AWTUtil;
import org.jflicks.util.Util;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
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

    /**
     * Simple empty constructor.
     */
    public RecordingScreen() {

        setTitle("Watch Recordings");

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
    }

    private Integer[] getTimeline() {
        return (timeline);
    }

    private void setTimeline(Integer[] array) {
        timeline = array;

        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                log(DEBUG, "timeline: " + array[i].intValue());
            }
        }
    }

    @Override
    public Player getPlayer() {

        System.out.println("RecordingScreen.getPlayer()");
        Player result = null;

        if (isPlayingVideo()) {

            System.out.println("RecordingScreen.getPlayer() - VIDEO");
            result = getPlayer(Player.PLAYER_VIDEO);

        } else {

            System.out.println("RecordingScreen.getPlayer() - TS");
            result = getPlayer(Player.PLAYER_VIDEO_TRANSPORT_STREAM);
        }

        return (result);
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
        return (playingVideo);
    }

    private void setPlayingVideo(boolean b) {
        playingVideo = b;
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
            Animator sani = PropertySetter.createAnimator(250,
                ssp, "alpha", 0.0f, 1.0f);
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
            if (AWTUtil.isTranslucentSupported()) {

                setRecordingInfoWindow(new RecordingInfoWindow(
                    fev.getFrame(),
                    fev.getPosition(), 8, getInfoColor(),
                    getPanelColor(), (float) getPanelAlpha(),
                    getSmallFont(), getMediumFont()));

            } else {

                setRecordingInfoWindow(new RecordingInfoWindow(
                    fev.getPosition(), 8, getInfoColor(),
                    getPanelColor(), (float) getPanelAlpha(),
                    getSmallFont(), getMediumFont()));
            }

            setDefaultBackgroundImage(
                Util.resize(getDefaultBackgroundImage(), width, height));

            // Create our blank panel.
            JXPanel blank = new JXPanel();
            MattePainter blankp = new MattePainter(Color.BLACK);
            blank.setBackgroundPainter(blankp);
            blank.setBounds(0, 0, width, height);
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
            if (Util.isWindows()) {

                path = path.replace(":", "|");
                path = "/" + path;
            }

            result = ic.getImage("file://" + path + ".png", false);
            if (result == null) {

                path = path.substring(0, path.length() - 4);
                result = ic.getImage("file://" + path + ".png", false);
            }
        }

        return (result);
    }

    private String computeRecordingPath(Recording r) {

        String result = null;

        setPlayingVideo(false);
        if (r != null) {

            // Default to the path property.
            result = r.getPath();

            String iext = r.getIndexedExtension();
            if (iext != null) {

                File tmp = new File(result + "." + r.getIndexedExtension());
                if ((tmp.exists()) && (tmp.isFile())) {

                    result = tmp.getPath();
                    setPlayingVideo(true);
                }
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

                            Animator sani = getScreenShotAnimator();
                            if (sani != null) {

                                if (sani.isRunning()) {
                                    sani.stop();
                                }

                                sani.start();
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

            // If we get this property update, then it means the video
            // finished playing on it's own.
            Boolean bobj = (Boolean) event.getNewValue();
            if (bobj.booleanValue()) {

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

        removeBlankPanel();
        controlKeyboard(true);
        setCurrentRecording(null);
        RecordingInfoWindow w = getRecordingInfoWindow();
        if (w != null) {

            w.setVisible(false);
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
        if (p != null) {

            updateLengthHint(getCurrentRecording(), p);
            p.seek(30);
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
                    p.seek(diff);

                } else {

                    log(INFO, "commercials not set or end");
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

                    log(INFO, "commercials not set or end");
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
        if (p != null) {

            updateLengthHint(getCurrentRecording(), p);
            p.seek(30);
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
            String recpath = computeRecordingPath(r);
            Player p = getPlayer();
            if ((p != null) && (!p.isPlaying()) && (r != null)) {

                ButtonPanel pbp = getPlayButtonPanel();
                if (PLAY.equals(pbp.getSelectedButton())) {

                    View v = getView();
                    if (v instanceof FrontEndView) {

                        FrontEndView fev = (FrontEndView) v;
                        p.setRectangle(fev.getPosition());
                    }

                    p.addPropertyChangeListener("Completed", this);
                    RecordingInfoWindow w = getRecordingInfoWindow();
                    if (w != null) {

                        w.setImageCache(getImageCache());
                        w.setRecording(r);
                        w.setPlayer(p);
                    }

                    setTimeline(Commercial.timeline(r.getCommercials()));
                    setCurrentRecording(r);
                    updateLengthHint(r, p);
                    controlKeyboard(false);

                    p.setFrame(Util.findFrame(this));
                    addBlankPanel();
                    p.play(recpath);

                } else if (PLAY_FROM_BOOKMARK.equals(pbp.getSelectedButton())) {

                    View v = getView();
                    if (v instanceof FrontEndView) {

                        FrontEndView fev = (FrontEndView) v;
                        p.setRectangle(fev.getPosition());
                    }

                    p.addPropertyChangeListener("Completed", this);
                    RecordingInfoWindow w = getRecordingInfoWindow();
                    if (w != null) {

                        w.setImageCache(getImageCache());
                        w.setRecording(r);
                        w.setPlayer(p);
                    }

                    setTimeline(Commercial.timeline(r.getCommercials()));
                    setCurrentRecording(r);
                    updateLengthHint(r, p);

                    Bookmark bm = getBookmark(r.getId());
                    if (bm != null) {

                        if (isPlayingVideo()) {

                            bm.setPreferTime(true);

                        } else {

                            bm.setPreferTime(false);
                        }

                        controlKeyboard(false);
                        p.setFrame(Util.findFrame(this));
                        addBlankPanel();
                        p.play(recpath, bm);
                    }

                } else if (DELETE.equals(pbp.getSelectedButton())) {

                    log(DEBUG, "firing delete recording");
                    fireScreenEvent(ScreenEvent.DELETE_RECORDING, r);

                } else if (DELETE_ALLOW_RERECORDING.equals(
                    pbp.getSelectedButton())) {

                    log(DEBUG, "firing delete recording - allow");
                    fireScreenEvent(
                        ScreenEvent.DELETE_RECORDING_ALLOW_RERECORDING, r);

                } else if (STOP_RECORDING.equals(pbp.getSelectedButton())) {

                    log(DEBUG, "firing stop recording");
                    fireScreenEvent(ScreenEvent.STOP_RECORDING, r);

                } else if (CANCEL.equals(pbp.getSelectedButton())) {

                    log(DEBUG, "cancel hit");
                }

                unpopup();
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
                        blist.add(CANCEL);

                        popup(blist.toArray(new String[blist.size()]));
                    }
                }
            }
        }
    }

    static class RecordingSortByTitle implements Comparator<Recording>,
        Serializable {

        public int compare(Recording r0, Recording r1) {
            return (r0.getTitle().compareTo(r1.getTitle()));
        }
    }

}


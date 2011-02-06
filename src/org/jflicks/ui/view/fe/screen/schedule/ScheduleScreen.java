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
package org.jflicks.ui.view.fe.screen.schedule;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.NMSUtil;
import org.jflicks.photomanager.Tag;
import org.jflicks.tv.Airing;
import org.jflicks.tv.Channel;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;
import org.jflicks.tv.Upcoming;
import org.jflicks.ui.view.fe.AllGuideJob;
import org.jflicks.ui.view.fe.ButtonPanel;
import org.jflicks.ui.view.fe.ChannelListPanel;
import org.jflicks.ui.view.fe.Dialog;
import org.jflicks.ui.view.fe.NMSProperty;
import org.jflicks.ui.view.fe.ParameterProperty;
import org.jflicks.ui.view.fe.RecordingRuleListPanel;
import org.jflicks.ui.view.fe.RecordingRuleProperty;
import org.jflicks.ui.view.fe.ShowAiringListPanel;
import org.jflicks.ui.view.fe.ShowDetailPanel;
import org.jflicks.ui.view.fe.TagListPanel;
import org.jflicks.ui.view.fe.UpcomingListPanel;
import org.jflicks.ui.view.fe.UpcomingDetailPanel;
import org.jflicks.ui.view.fe.UpcomingProperty;
import org.jflicks.ui.view.fe.screen.Screen;
import org.jflicks.util.Busy;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * A screen to schedule recordings by title, using a guide or just to edit
 * rules.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ScheduleScreen extends Screen implements ParameterProperty,
    NMSProperty, UpcomingProperty, PropertyChangeListener, JobListener,
    ActionListener, RecordingRuleProperty {

    private static final String CANCEL = "Cancel";

    private static final String BY_TITLE = "By Title";
    private static final String BY_GUIDE = "Using Guide";
    private static final String BY_RULES = "Recording Rules";
    private static final String UPCOMING_RECORDINGS = "Upcoming Recordings";
    private static final int ALL_UPCOMING = 1;
    private static final int RECORDING_UPCOMING = 2;
    private static final int NOT_RECORDING_UPCOMING = 3;
    private static final String ALL_UPCOMING_TEXT = "Showing All";
    private static final String RECORDING_UPCOMING_TEXT = "Showing Recording";
    private static final String NOT_RECORDING_UPCOMING_TEXT =
        "Showing Not Recording";

    private NMS[] nms;
    private String[] parameters;
    private String selectedParameter;
    private boolean updatedParameter;
    private Upcoming[] upcomings;
    private RecordingRule[] recordingRules;
    private HashMap<Channel, ShowAiring[]> guideMap;
    private JobContainer allGuideJobContainer;
    private long lastGuide;
    private HashMap<Tag, TagValue> tagMap;

    private JXPanel waitPanel;
    private TagListPanel titleTagListPanel;
    private ShowDetailPanel showDetailPanel;
    private RecordingRulePanel recordingRulePanel;
    private ChannelListPanel channelListPanel;
    private ShowAiringListPanel showAiringListPanel;
    private RecordingRuleListPanel recordingRuleListPanel;
    private ShowAiring selectedShowAiring;
    private RecordingRule selectedRecordingRule;
    private UpcomingListPanel upcomingListPanel;
    private UpcomingDetailPanel upcomingDetailPanel;
    private JXLabel upcomingLabel;
    private int upcomingState;
    private ButtonPanel upcomingButtonPanel;
    private boolean popupEnabled;

    /**
     * Simple empty constructor.
     */
    public ScheduleScreen() {

        setTitle("Schedule");
        BufferedImage bi = getImageByName("Schedule");
        setDefaultBackgroundImage(bi);

        setFocusable(true);
        requestFocus();
        setUpcomingState(ALL_UPCOMING);
        setUpcomingLabel(new JXLabel(ALL_UPCOMING_TEXT));

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

        String[] array = {

            BY_TITLE,
            BY_GUIDE,
            BY_RULES,
            UPCOMING_RECORDINGS
        };

        setParameters(array);

        setTagMap(new HashMap<Tag, TagValue>());

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
    public String[] getParameters() {

        String[] result = null;

        if (parameters != null) {

            result = Arrays.copyOf(parameters, parameters.length);
        }

        return (result);
    }

   private void setParameters(String[] array) {

        if (array != null) {
            parameters = Arrays.copyOf(array, array.length);
        } else {
            parameters = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getSelectedParameter() {
        return (selectedParameter);
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectedParameter(String s) {

        if ((s != null) && (selectedParameter != null)) {
            setUpdatedParameter(!s.equals(selectedParameter));
        } else {
            setUpdatedParameter(true);
        }

        selectedParameter = s;
    }

    private boolean isUpdatedParameter() {
        return (updatedParameter);
    }

    private void setUpdatedParameter(boolean b) {
        updatedParameter = b;
    }

    /**
     * {@inheritDoc}
     */
    public Upcoming[] getUpcomings() {

        Upcoming[] result = null;

        if (upcomings != null) {

            result = Arrays.copyOf(upcomings, upcomings.length);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void setUpcomings(Upcoming[] array) {

        if (array != null) {
            upcomings = Arrays.copyOf(array, array.length);
        } else {
            upcomings = null;
        }

        applyUpcoming();
    }

    private int getUpcomingState() {
        return (upcomingState);
    }

    private void setUpcomingState(int i) {
        upcomingState = i;
    }

    private void applyUpcoming() {

        Upcoming[] array = getUpcomings();
        UpcomingListPanel ulp = getUpcomingListPanel();
        if ((array != null) && (ulp != null)) {

            ArrayList<Upcoming> list = new ArrayList<Upcoming>();
            int state = getUpcomingState();
            switch (state) {

            case ALL_UPCOMING:
            default:

                ulp.setUpcomings(array);
                break;

            case RECORDING_UPCOMING:

                for (int i = 0; i < array.length; i++) {

                    if (NMSConstants.READY.equals(array[i].getStatus())) {

                        list.add(array[i]);
                    }
                }
                if (list.size() > 0) {

                    array = list.toArray(new Upcoming[list.size()]);
                    ulp.setUpcomings(array);

                } else {

                    ulp.setUpcomings(null);
                }
                break;

            case NOT_RECORDING_UPCOMING:

                for (int i = 0; i < array.length; i++) {

                    if (!NMSConstants.READY.equals(array[i].getStatus())) {

                        list.add(array[i]);
                    }
                }
                if (list.size() > 0) {

                    array = list.toArray(new Upcoming[list.size()]);
                    ulp.setUpcomings(array);

                } else {

                    ulp.setUpcomings(null);
                }
                break;
            }

            ulp.setSelectedIndex(0);
        }
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

        applyRecordingRule();
    }

    private void applyRecordingRule() {

        RecordingRule[] array = getRecordingRules();
        RecordingRuleListPanel lp = getRecordingRuleListPanel();
        if ((array != null) && (lp != null)) {

            int start = lp.getStartIndex();
            int vis = lp.getVisibleCount();
            Arrays.sort(array, new RecordingRuleSortByName());
            lp.setRecordingRules(array);
            log(DEBUG, "start: " + start);
            log(DEBUG, "vis: " + vis);
            log(DEBUG, "array.length: " + array.length);
            if ((start + vis) <= array.length) {

                // Safe to reset the start index.
                lp.setStartIndex(start);

            } else {

                start--;
                if ((start > 0) && ((start + vis) <= array.length)) {

                    // Safe again to reset to start index minus one.
                    lp.setStartIndex(start);
                }
            }
        }
    }

    private boolean isParameterByTitle() {
        return (BY_TITLE.equals(getSelectedParameter()));
    }

    private boolean isParameterByGuide() {
        return (BY_GUIDE.equals(getSelectedParameter()));
    }

    private boolean isParameterByRules() {
        return (BY_RULES.equals(getSelectedParameter()));
    }

    private boolean isParameterUpcomingRecordings() {
        return (UPCOMING_RECORDINGS.equals(getSelectedParameter()));
    }

    private TagListPanel getTitleTagListPanel() {
        return (titleTagListPanel);
    }

    private void setTitleTagListPanel(TagListPanel p) {
        titleTagListPanel = p;
    }

    private ChannelListPanel getChannelListPanel() {
        return (channelListPanel);
    }

    private void setChannelListPanel(ChannelListPanel p) {
        channelListPanel = p;
    }

    private ShowAiringListPanel getShowAiringListPanel() {
        return (showAiringListPanel);
    }

    private void setShowAiringListPanel(ShowAiringListPanel p) {
        showAiringListPanel = p;
    }

    private RecordingRuleListPanel getRecordingRuleListPanel() {
        return (recordingRuleListPanel);
    }

    private void setRecordingRuleListPanel(RecordingRuleListPanel p) {
        recordingRuleListPanel = p;
    }

    private ShowDetailPanel getShowDetailPanel() {
        return (showDetailPanel);
    }

    private void setShowDetailPanel(ShowDetailPanel p) {
        showDetailPanel = p;
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

    private UpcomingListPanel getUpcomingListPanel() {
        return (upcomingListPanel);
    }

    private void setUpcomingListPanel(UpcomingListPanel p) {
        upcomingListPanel = p;
    }

    private JXLabel getUpcomingLabel() {
        return (upcomingLabel);
    }

    private void setUpcomingLabel(JXLabel l) {
        upcomingLabel = l;
    }

    private UpcomingDetailPanel getUpcomingDetailPanel() {
        return (upcomingDetailPanel);
    }

    private void setUpcomingDetailPanel(UpcomingDetailPanel p) {
        upcomingDetailPanel = p;
    }

    private ButtonPanel getUpcomingButtonPanel() {
        return (upcomingButtonPanel);
    }

    private void setUpcomingButtonPanel(ButtonPanel p) {
        upcomingButtonPanel = p;
    }

    private JobContainer getAllGuideJobContainer() {
        return (allGuideJobContainer);
    }

    private void setAllGuideJobContainer(JobContainer c) {
        allGuideJobContainer = c;
    }

    private long getLastGuide() {
        return (lastGuide);
    }

    private void setLastGuide(long l) {
        lastGuide = l;
    }

    private ShowAiring getSelectedShowAiring() {
        return (selectedShowAiring);
    }

    private void setSelectedShowAiring(ShowAiring sa) {
        selectedShowAiring = sa;
    }

    private RecordingRule getSelectedRecordingRule() {
        return (selectedRecordingRule);
    }

    private void setSelectedRecordingRule(RecordingRule rr) {
        selectedRecordingRule = rr;
    }

    private boolean isWithinAnHour() {

        boolean result = false;

        long l = getLastGuide() + 60 * 60 * 1000;
        if (System.currentTimeMillis() < l) {

            result = true;
        }

        return (result);
    }

    private HashMap<Channel, ShowAiring[]> getGuideMap() {
        return (guideMap);
    }

    private void setGuideMap(HashMap<Channel, ShowAiring[]> m) {
        guideMap = m;

        setLastGuide(System.currentTimeMillis());
    }

    private HashMap<Tag, TagValue> getTagMap() {
        return (tagMap);
    }

    private void setTagMap(HashMap<Tag, TagValue> m) {
        tagMap = m;
    }

    private void clearTagMap() {

        HashMap<Tag, TagValue> m = getTagMap();
        if (m != null) {
            m.clear();
        }
    }

    private void add(Tag t, Channel c, ShowAiring sa) {

        add(t, new TagValue(c, sa));
    }

    private void add(Tag t, TagValue tv) {

        HashMap<Tag, TagValue> m = getTagMap();
        if ((m != null) && (t != null) && (tv != null)) {

            m.put(t, tv);
        }
    }

    private TagValue getTagValueByTag(Tag t) {

        TagValue result = null;

        HashMap<Tag, TagValue> m = getTagMap();
        if ((m != null) && (t != null)) {

            result = m.get(t);
        }

        return (result);
    }

    private boolean isPopupEnabled() {
        return (popupEnabled);
    }

    protected void setPopupEnabled(boolean b) {
        popupEnabled = b;
    }

    protected void popup(String[] choices) {

        JLayeredPane pane = getLayeredPane();
        if ((pane != null) && (choices != null)) {

            Dimension d = pane.getSize();
            int width = (int) d.getWidth();
            int height = (int) d.getHeight();

            ButtonPanel bp = new ButtonPanel();
            bp.setMediumFont(bp.getLargeFont());
            bp.setSmallFont(bp.getLargeFont());
            bp.addActionListener(this);
            bp.setButtons(choices);
            setUpcomingButtonPanel(bp);

            d = bp.getPreferredSize();
            int bpwidth = (int) d.getWidth();
            int bpheight = (int) d.getHeight();
            int bpx = (int) ((width - bpwidth) / 2);
            int bpy = (int) ((height - bpheight) / 2);
            bp.setBounds(bpx, bpy, bpwidth, bpheight);

            setPopupEnabled(true);
            pane.add(bp, Integer.valueOf(300));
            bp.requestFocus();
            bp.setControl(true);
            bp.setButtons(choices);
        }
    }

    protected void unpopup() {

        setPopupEnabled(false);
        JLayeredPane pane = getLayeredPane();
        ButtonPanel bp = getUpcomingButtonPanel();
        if ((pane != null) && (bp != null)) {

            bp.removeActionListener(this);
            setUpcomingButtonPanel(null);
            pane.remove(bp);
            pane.repaint();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void save() {
    }

    /**
     * {@inheritDoc}
     */
    public void commandReceived(String command) {
    }

    /**
     * Override so we can start up the player.
     *
     * @param b When true we are ready to start the player.
     */
    public void setVisible(boolean b) {

        super.setVisible(b);
        if (b) {

            if ((isParameterByTitle()) || (isParameterByGuide())) {

                if (!isWithinAnHour()) {

                    AllGuideJob gjob = new AllGuideJob(getNMS());
                    gjob.addJobListener(this);
                    JobContainer jc = JobManager.getJobContainer(gjob);
                    setAllGuideJobContainer(jc);
                    jc.start();
                    updateLayout(true);

                } else {

                    updateLayout(false);
                }

            } else if (isParameterByRules()) {

                NMS[] array = getNMS();
                RecordingRuleListPanel rrlp = getRecordingRuleListPanel();
                if ((array != null) && (array.length > 0) && (rrlp != null)) {

                    RecordingRule[] rules = null;
                    ArrayList<RecordingRule> rlist =
                        new ArrayList<RecordingRule>();
                    for (int i = 0; i < array.length; i++) {

                        rules = array[i].getRecordingRules();
                        if (rules != null) {

                            for (int j = 0; j < rules.length; j++) {

                                rlist.add(rules[j]);
                            }
                        }
                    }

                    if (rlist.size() > 0) {

                        rules = rlist.toArray(new RecordingRule[rlist.size()]);
                        Arrays.sort(rules, new RecordingRuleSortByName());
                        rrlp.setRecordingRules(rules);
                    }

                    updateLayout(false);
                }

            } else if (isParameterUpcomingRecordings()) {

                updateLayout(false);
            }
        }
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
            int onethirdlistwidth = (width - (3 * wspan)) / 3;
            int twothirdlistwidth = onethirdlistwidth * 2;

            int hspan = (int) (height * 0.03);
            int listheight = (int) ((height - (2 * hspan)) / 1.5);

            int detailwidth = listwidth;
            int detailheight = (height - (3 * hspan)) - listheight;

            JXPanel panel = new JXPanel(new BorderLayout());
            JXLabel l = new JXLabel("Getting TV data, please wait...");
            l.setHorizontalTextPosition(SwingConstants.CENTER);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setFont(getLargeFont());
            panel.add(l, BorderLayout.CENTER);
            MattePainter p = new MattePainter(Color.BLACK);
            panel.setBackgroundPainter(p);
            panel.setBounds(0, 0, (int) d.getWidth(), (int) d.getHeight());
            setWaitPanel(panel);

            TagListPanel tlp = new TagListPanel();
            tlp.setControl(true);
            tlp.addPropertyChangeListener("SelectedTag", this);
            tlp.setBounds(wspan, hspan, listwidth, listheight);
            setTitleTagListPanel(tlp);

            ChannelListPanel clp = new ChannelListPanel();
            clp.setControl(true);
            clp.addPropertyChangeListener("SelectedChannel", this);
            clp.setBounds(wspan, hspan, onethirdlistwidth, listheight);
            setChannelListPanel(clp);

            ShowAiringListPanel salp = new ShowAiringListPanel();
            salp.setControl(false);
            salp.addPropertyChangeListener("SelectedShowAiring", this);
            salp.setBounds(wspan + wspan + onethirdlistwidth, hspan,
                twothirdlistwidth, listheight);
            setShowAiringListPanel(salp);

            RecordingRuleListPanel rrlp = new RecordingRuleListPanel();
            rrlp.setControl(true);
            rrlp.addPropertyChangeListener("SelectedRecordingRule", this);
            rrlp.setBounds(wspan, hspan, listwidth, listheight + detailheight);
            setRecordingRuleListPanel(rrlp);

            ShowDetailPanel sdp = new ShowDetailPanel();
            sdp.setBounds(wspan, hspan + listheight + hspan, detailwidth,
                detailheight);
            setShowDetailPanel(sdp);

            UpcomingListPanel ulp = new UpcomingListPanel();
            ulp.setAlpha(alpha);

            ulp.addPropertyChangeListener("SelectedUpcoming", this);
            ulp.setControl(true);

            setUpcomingListPanel(ulp);

            UpcomingDetailPanel dp = new UpcomingDetailPanel();
            dp.setAlpha(alpha);
            setUpcomingDetailPanel(dp);

            JXLabel label = getUpcomingLabel();
            label.setFont(ulp.getLargeFont());
            label.setForeground(ulp.getSelectedColor());
            label.setHorizontalTextPosition(SwingConstants.CENTER);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            Dimension ldim = label.getPreferredSize();
            label.setBounds(wspan, hspan, listwidth, (int) ldim.getHeight());

            ulp.setBounds(wspan, hspan, listwidth, listheight);
            dp.setBounds(wspan, hspan + hspan + listheight, detailwidth,
                detailheight);

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

                if (isParameterByTitle()) {

                    pane.add(getTitleTagListPanel(), Integer.valueOf(100));
                    pane.add(getShowDetailPanel(), Integer.valueOf(100));

                } else if (isParameterByGuide()) {

                    pane.add(getChannelListPanel(), Integer.valueOf(100));
                    pane.add(getShowAiringListPanel(), Integer.valueOf(100));
                    pane.add(getShowDetailPanel(), Integer.valueOf(100));

                } else if (isParameterByRules()) {

                    pane.add(getRecordingRuleListPanel(), Integer.valueOf(100));

                } else if (isParameterUpcomingRecordings()) {

                    pane.add(getUpcomingListPanel(), Integer.valueOf(100));
                    pane.add(getUpcomingLabel(), Integer.valueOf(110));
                    pane.add(getUpcomingDetailPanel(), Integer.valueOf(100));
                }
            }

            repaint();
        }
    }

    private boolean isPaidProgramming(Show s) {

        boolean result = false;

        if (s != null) {

            String type = s.getType();
            if ((type != null) && (type.equalsIgnoreCase("Paid Programming"))) {

                result = true;
            }
        }

        return (result);
    }

    private void add(Tag root, Channel c, ShowAiring[] array) {

        if ((root != null) && (c != null) && (array != null)) {

            String chantext = c.toString();

            for (int i = 0; i < array.length; i++) {

                Show show = array[i].getShow();
                if ((show != null) && (!isPaidProgramming(show))) {

                    String title = show.getTitle();
                    if (title != null) {

                        String letter = title.substring(0, 1);
                        letter = letter.toUpperCase();
                        Tag letterTag = null;
                        if (root.hasChildByName(letter)) {

                            letterTag = root.getChildByName(letter);

                        } else {

                            letterTag = new Tag();
                            letterTag.setName(letter);
                            root.addChild(letterTag);
                        }

                        if (letterTag != null) {

                            Tag titleTag = null;
                            if (letterTag.hasChildByName(title)) {

                                titleTag = letterTag.getChildByName(title);

                            } else {

                                titleTag = new Tag();
                                titleTag.setName(title);
                                letterTag.addChild(titleTag);
                            }

                            if (titleTag != null) {

                                Tag channelTag = null;
                                if (titleTag.hasChildByName(chantext)) {

                                    channelTag =
                                        titleTag.getChildByName(chantext);

                                } else {

                                    channelTag = new Tag();
                                    channelTag.setName(chantext);
                                    titleTag.addChild(channelTag);
                                }

                                if (channelTag != null) {

                                    Tag when = new Tag();
                                    when.setName(array[i].toString());
                                    channelTag.addChild(when);
                                    add(when, c, array[i]);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Upcoming[] getUpcomings(RecordingRule rr) {

        Upcoming[] result = null;

        if (rr != null) {

            String seriesId = rr.getSeriesId();
            int channelId = rr.getChannelId();
            String lid = rr.getListingId();
            NMS n = NMSUtil.select(getNMS(), rr.getHostPort());
            if ((seriesId != null) && (n != null) && (lid != null)) {

                Channel c = n.getChannelById(channelId, lid);
                Upcoming[] all = n.getUpcomings();
                if ((all != null) && (c != null)) {

                    String cn = c.getName();
                    if (cn != null) {

                        ArrayList<Upcoming> ul = new ArrayList<Upcoming>();
                        for (int i = 0; i < all.length; i++) {

                            if ((seriesId.equals(all[i].getSeriesId()))
                                && (cn.equals(all[i].getChannelName()))) {

                                ul.add(all[i]);
                            }
                        }

                        if (ul.size() > 0) {

                            result = ul.toArray(new Upcoming[ul.size()]);
                        }
                    }
                }
            }
        }

        return (result);
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
                setAllGuideJobContainer(null);
                TagListPanel tlp = getTitleTagListPanel();
                if (tlp != null) {

                    clearTagMap();
                    Tag root = new Tag();
                    root.setName("Root");

                    Set<Map.Entry<Channel, ShowAiring[]>> set = map.entrySet();
                    Iterator<Map.Entry<Channel, ShowAiring[]>> iter =
                        set.iterator();

                    while (iter.hasNext()) {

                        Map.Entry<Channel, ShowAiring[]> entry = iter.next();
                        Channel key = entry.getKey();
                        ShowAiring[] array = entry.getValue();
                        add(root, key, array);
                    }

                    tlp.setRootTag(root);
                }

                ChannelListPanel clp = getChannelListPanel();
                if (clp != null) {

                    Set<Channel> set = map.keySet();
                    if (set.size() > 0) {

                        Channel[] chans = set.toArray(new Channel[set.size()]);
                        Arrays.sort(chans);
                        clp.setChannels(chans);
                    }
                }

                updateLayout(false);
                requestFocus();
            }
        }
    }

    private void updateRecordingStatus() {

        ShowDetailPanel sdp = getShowDetailPanel();
        if (sdp != null) {

            ShowAiring sa = getSelectedShowAiring();
            if (sa != null) {

                String sadatestr = "";
                Airing airing = sa.getAiring();
                if (airing != null) {

                    Date d = airing.getAirDate();
                    if (d != null) {

                        sadatestr = d.toString();
                    }
                }
                RecordingRule rr = getRecordingRule(sa);
                if (rr != null) {

                    Upcoming[] ups = getUpcomings(rr);
                    if (ups != null) {

                        boolean found = false;
                        int doing = 0;
                        for (int i = 0; i < ups.length; i++) {

                            if (NMSConstants.READY.equals(ups[i].getStatus())) {

                                doing++;

                                if (!found) {

                                    found = sadatestr.equals(ups[i].getStart());
                                }
                            }
                        }

                        String extra = "";
                        if (doing > 0) {

                            if (found) {
                                extra = " including this one";
                            } else {
                                extra = " but not this one";
                            }
                        }

                        sdp.setRecordingStatus("Recording " + doing + " of "
                            + ups.length + extra);

                    } else {

                        sdp.setRecordingStatus("Recording 0 of 0");
                    }

                } else {

                    sdp.setRecordingStatus("Not Recording");
                }

            } else {

                sdp.setRecordingStatus(null);
            }
        }
    }

    private Frame getFrame() {
        return (Util.findFrame(this));
    }

    /**
     * We need to listen for events from the "upcoming override dialog".
     *
     * @param event A given event.
     */
    public void actionPerformed(ActionEvent event) {

        UpcomingListPanel ulp = getUpcomingListPanel();
        if ((ulp != null) && (event.getSource() == getUpcomingButtonPanel())) {

            ButtonPanel bp = getUpcomingButtonPanel();
            if (!CANCEL.equals(bp.getSelectedButton())) {

                Upcoming up = ulp.getSelectedUpcoming();
                if (up != null) {

                    NMS n = NMSUtil.select(getNMS(), up.getHostPort());
                    if (n != null) {

                        OverrideUpcomingJob ouj =
                            new OverrideUpcomingJob(n, up);
                        Busy busy = new Busy(getLayeredPane(), ouj);
                        busy.addJobListener(this);
                        busy.execute();
                    }
                }
            }

            unpopup();
        }
    }

    /**
     * We listen for property change events from the panels that deal
     * with selecting a show to schedule.
     *
     * @param event A given PropertyChangeEvent instance.
     */
    public void propertyChange(PropertyChangeEvent event) {

        if (event.getPropertyName().equals("SelectedTag")) {

            ShowDetailPanel sdp = getShowDetailPanel();
            if (sdp != null) {

                TagValue tv = getTagValueByTag((Tag) event.getNewValue());
                if (tv != null) {

                    Channel c = tv.getChannel();
                    ShowAiring sa = tv.getShowAiring();
                    sdp.setChannel(c);
                    sdp.setShowAiring(sa);
                    setSelectedShowAiring(sa);

                } else {

                    sdp.setChannel(null);
                    sdp.setShowAiring(null);
                    setSelectedShowAiring(null);
                }
            }

            updateRecordingStatus();

        } else if (event.getPropertyName().equals("SelectedChannel")) {

            HashMap<Channel, ShowAiring[]> map = getGuideMap();
            Channel c = (Channel) event.getNewValue();
            ShowAiringListPanel salp = getShowAiringListPanel();
            if ((map != null) && (c != null) && (salp != null)) {

                salp.setShowAirings(map.get(c));
            }

        } else if (event.getPropertyName().equals("SelectedShowAiring")) {

            ShowDetailPanel sdp = getShowDetailPanel();
            if (sdp != null) {

                ShowAiring sa = (ShowAiring) event.getNewValue();
                sdp.setShowAiring(sa);
                setSelectedShowAiring(sa);
            }

            updateRecordingStatus();

        } else if (event.getPropertyName().equals("SelectedRecordingRule")) {

            RecordingRule rr = (RecordingRule) event.getNewValue();
            setSelectedRecordingRule(rr);

        } else if (event.getPropertyName().equals("SelectedUpcoming")) {

            UpcomingDetailPanel dp = getUpcomingDetailPanel();
            if (dp != null) {

                Upcoming u = (Upcoming) event.getNewValue();
                dp.setUpcoming(u);
            }
        }
    }

    class LeftAction extends AbstractAction {

        public LeftAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (!isPopupEnabled()) {

                if (isParameterByTitle()) {

                    TagListPanel tlp = getTitleTagListPanel();
                    if (tlp != null) {

                        if (tlp.isExpanded()) {

                            tlp.toggle();

                        } else {

                            Tag selected = tlp.getSelectedTag();
                            if (selected != null) {

                                Tag parent = selected.getParent();
                                if ((parent != null) && (!parent.isRoot())) {

                                    if (tlp.isExpanded(parent)) {

                                        tlp.setSelectedTag(parent);
                                        tlp.toggle();
                                    }
                                }
                            }
                        }
                    }

                } else if (isParameterByGuide()) {

                    ChannelListPanel clp = getChannelListPanel();
                    if (clp != null) {

                        clp.setControl(true);
                    }

                    ShowAiringListPanel salp = getShowAiringListPanel();
                    if (salp != null) {

                        salp.setControl(false);
                    }

                } else if (isParameterUpcomingRecordings()) {

                    String text = null;
                    int state = getUpcomingState();
                    switch (state) {

                    default:
                    case ALL_UPCOMING:
                        state = NOT_RECORDING_UPCOMING;
                        text = NOT_RECORDING_UPCOMING_TEXT;
                        break;

                    case RECORDING_UPCOMING:
                        state = ALL_UPCOMING;
                        text = ALL_UPCOMING_TEXT;
                        break;

                    case NOT_RECORDING_UPCOMING:
                        state = RECORDING_UPCOMING;
                        text = RECORDING_UPCOMING_TEXT;
                        break;
                    }

                    JXLabel l = getUpcomingLabel();
                    if (l != null) {

                        l.setText(text);
                    }
                    setUpcomingState(state);
                    applyUpcoming();
                }
            }
        }
    }

    class RightAction extends AbstractAction {

        public RightAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (!isPopupEnabled()) {

                if (isParameterByTitle()) {

                    TagListPanel tlp = getTitleTagListPanel();
                    if (tlp != null) {

                        tlp.toggle();
                    }

                } else if (isParameterByGuide()) {

                    ChannelListPanel clp = getChannelListPanel();
                    if (clp != null) {

                        clp.setControl(false);
                    }

                    ShowAiringListPanel salp = getShowAiringListPanel();
                    if (salp != null) {

                        salp.setControl(true);
                    }

                } else if (isParameterUpcomingRecordings()) {

                    String text = null;
                    int state = getUpcomingState();
                    switch (state) {

                    default:
                    case ALL_UPCOMING:
                        state = RECORDING_UPCOMING;
                        text = RECORDING_UPCOMING_TEXT;
                        break;

                    case RECORDING_UPCOMING:
                        state = NOT_RECORDING_UPCOMING;
                        text = NOT_RECORDING_UPCOMING_TEXT;
                        break;

                    case NOT_RECORDING_UPCOMING:
                        state = ALL_UPCOMING;
                        text = ALL_UPCOMING_TEXT;
                        break;
                    }

                    JXLabel l = getUpcomingLabel();
                    if (l != null) {

                        l.setText(text);
                    }
                    setUpcomingState(state);
                    applyUpcoming();
                }
            }
        }
    }

    class UpAction extends AbstractAction {

        public UpAction() {
        }

        public void actionPerformed(ActionEvent e) {

            if (isPopupEnabled()) {

                ButtonPanel bp = getUpcomingButtonPanel();
                if (bp != null) {

                    bp.moveUp();
                }

            } else {

                if (isParameterByTitle()) {

                    TagListPanel tlp = getTitleTagListPanel();
                    if (tlp != null) {

                        tlp.moveUp();
                    }

                } else if (isParameterByGuide()) {

                    ChannelListPanel clp = getChannelListPanel();
                    if (clp != null) {

                        if (clp.isControl()) {
                            clp.moveUp();
                        }
                    }

                    ShowAiringListPanel salp = getShowAiringListPanel();
                    if (salp != null) {

                        if (salp.isControl()) {
                            salp.moveUp();
                        }
                    }

                } else if (isParameterByRules()) {

                    RecordingRuleListPanel rrlp = getRecordingRuleListPanel();
                    if (rrlp != null) {

                        rrlp.moveUp();
                    }

                } else if (isParameterUpcomingRecordings()) {

                    UpcomingListPanel ulp = getUpcomingListPanel();
                    if (ulp != null) {

                        ulp.moveUp();
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

                ButtonPanel bp = getUpcomingButtonPanel();
                if (bp != null) {

                    bp.moveDown();
                }

            } else {

                if (isParameterByTitle()) {

                    TagListPanel tlp = getTitleTagListPanel();
                    if (tlp != null) {

                        tlp.moveDown();
                    }

                } else if (isParameterByGuide()) {

                    ChannelListPanel clp = getChannelListPanel();
                    if (clp != null) {

                        if (clp.isControl()) {
                            clp.moveDown();
                        }
                    }

                    ShowAiringListPanel salp = getShowAiringListPanel();
                    if (salp != null) {

                        if (salp.isControl()) {
                            salp.moveDown();
                        }
                    }

                } else if (isParameterByRules()) {

                    RecordingRuleListPanel rrlp = getRecordingRuleListPanel();
                    if (rrlp != null) {

                        rrlp.moveDown();
                    }

                } else if (isParameterUpcomingRecordings()) {

                    UpcomingListPanel ulp = getUpcomingListPanel();
                    if (ulp != null) {

                        ulp.moveDown();
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

                if (isParameterByTitle()) {

                    TagListPanel tlp = getTitleTagListPanel();
                    if (tlp != null) {

                        tlp.movePageUp();
                    }

                } else if (isParameterByGuide()) {

                    ChannelListPanel clp = getChannelListPanel();
                    if (clp != null) {

                        if (clp.isControl()) {
                            clp.movePageUp();
                        }
                    }

                    ShowAiringListPanel salp = getShowAiringListPanel();
                    if (salp != null) {

                        if (salp.isControl()) {
                            salp.movePageUp();
                        }
                    }

                } else if (isParameterByRules()) {

                    RecordingRuleListPanel rrlp = getRecordingRuleListPanel();
                    if (rrlp != null) {

                        rrlp.movePageUp();
                    }

                } else if (isParameterUpcomingRecordings()) {

                    UpcomingListPanel ulp = getUpcomingListPanel();
                    if (ulp != null) {

                        ulp.movePageUp();
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

                if (isParameterByTitle()) {

                    TagListPanel tlp = getTitleTagListPanel();
                    if (tlp != null) {

                        tlp.movePageDown();
                    }

                } else if (isParameterByGuide()) {

                    ChannelListPanel clp = getChannelListPanel();
                    if (clp != null) {

                        if (clp.isControl()) {
                            clp.movePageDown();
                        }
                    }

                    ShowAiringListPanel salp = getShowAiringListPanel();
                    if (salp != null) {

                        if (salp.isControl()) {
                            salp.movePageDown();
                        }
                    }

                } else if (isParameterByRules()) {

                    RecordingRuleListPanel rrlp = getRecordingRuleListPanel();
                    if (rrlp != null) {

                        rrlp.movePageDown();
                    }

                } else if (isParameterUpcomingRecordings()) {

                    UpcomingListPanel ulp = getUpcomingListPanel();
                    if (ulp != null) {

                        ulp.movePageDown();
                    }
                }
            }
        }
    }

    class EnterAction extends AbstractAction implements JobListener {

        public EnterAction() {
        }

        public void jobUpdate(JobEvent event) {

            if (event.getType() == JobEvent.COMPLETE) {

                if (isParameterByRules()) {

                    RecordingRuleListPanel rrlp = getRecordingRuleListPanel();
                    NMS[] array = getNMS();
                    if ((rrlp != null) && (array != null)
                        && (array.length > 0)) {

                        RecordingRule[] rules = array[0].getRecordingRules();
                        if (rules != null) {

                            Arrays.sort(rules, new RecordingRuleSortByName());
                        }

                        rrlp.setRecordingRules(rules);
                    }
                }
            }
        }

        public void editRule(NMS n, RecordingRulePanel p, RecordingRule rr) {

            if ((n != null) && (p != null) && (rr != null)) {

                p.setNMS(n);
                p.setRecordingRule(rr);

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

        public void handleUpcoming() {

            UpcomingListPanel ulp = getUpcomingListPanel();
            if (ulp != null) {

                Upcoming up = ulp.getSelectedUpcoming();
                if (up != null) {

                    ArrayList<String> blist = new ArrayList<String>();
                    if (NMSConstants.PREVIOUSLY_RECORDED.equals(
                        up.getStatus())) {

                        blist.add("Forget Old Recording");

                    } else {

                        blist.add("Don't Record");
                    }

                    blist.add(CANCEL);
                    popup(blist.toArray(new String[blist.size()]));
                }
            }
        }

        public void actionPerformed(ActionEvent e) {

            System.out.println("enter:actionPerformed");
            RecordingRulePanel p = getRecordingRulePanel();
            if (p != null) {

                if (isParameterByRules()) {

                    RecordingRule rr = getSelectedRecordingRule();
                    if (rr != null) {

                        NMS n = NMSUtil.select(getNMS(), rr.getHostPort());
                        if (n != null) {

                            editRule(n, p, rr);
                        }
                    }

                } else if ((isParameterByGuide()) || (isParameterByTitle())) {

                    ShowAiring sa = getSelectedShowAiring();
                    if (sa != null) {

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

                                editRule(n, p, rr);
                            }
                        }
                    }

                } else if (isParameterUpcomingRecordings()) {

                    handleUpcoming();
                }
            }
        }
    }

    static class TagValue {

        private Channel channel;
        private ShowAiring showAiring;

        public TagValue(Channel c, ShowAiring sa) {

            setChannel(c);
            setShowAiring(sa);
        }

        public Channel getChannel() {
            return (channel);
        }

        private void setChannel(Channel c) {
            channel = c;
        }

        public ShowAiring getShowAiring() {
            return (showAiring);
        }

        private void setShowAiring(ShowAiring c) {
            showAiring = c;
        }

    }

    static class RecordingRuleSortByName implements Comparator<RecordingRule>,
        Serializable {

        public RecordingRuleSortByName() {
        }

        public int compare(RecordingRule rr0, RecordingRule rr1) {

            String s0 = rr0.getName();
            String s1 = rr1.getName();

            return (s0.compareTo(s1));
        }
    }

}


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
package org.jflicks.ui.view.j4cc.scheduler;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.nms.NMS;
import org.jflicks.tv.Airing;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;
import org.jflicks.tv.Upcoming;
import org.jflicks.ui.view.j4cc.AbstractPanel;
import org.jflicks.util.ProgressBar;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXFrame;

/**
 * A base class that defines a panel for this app.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SchedulerPanel extends AbstractPanel {

    private static final String RECORDING_FRAME = "recordings";
    private static final String UPCOMING_FRAME = "upcoming";
    private static final String RULE_FRAME = "rule";
    private static final String MAIN_FRAME = "main";

    private JButton upcomingButton;
    private JButton ruleButton;
    private JButton recordingButton;
    private JButton recordButton;
    private JTabbedPane tabbedPane;
    private ChannelGuidePanel channelGuidePanel;
    private SearchPanel searchPanel;
    private JXFrame recordingFrame;
    private DisplayRecordingPanel displayRecordingPanel;
    private RecordingAction recordingAction;
    private JXFrame upcomingFrame;
    private DisplayUpcomingPanel displayUpcomingPanel;
    private UpcomingAction upcomingAction;
    private JXFrame ruleFrame;
    private EditRecordingRulePanel editRecordingRulePanel;
    private RuleAction ruleAction;

    /**
     * Default constructor.
     */
    public SchedulerPanel() {

        setLayout(new GridBagLayout());

        RuleAction rulea = new RuleAction();
        JButton rbutton = new JButton(rulea);
        setRuleButton(rbutton);

        UpcomingAction ua = new UpcomingAction();
        JButton ubutton = new JButton(ua);
        setUpcomingButton(ubutton);

        RecordingAction reca = new RecordingAction(this);
        JButton recbutton = new JButton(reca);
        setRecordingButton(recbutton);

        RecordAction ra = new RecordAction();
        ra.setEnabled(false);
        JButton button = new JButton(ra);
        setRecordButton(button);

        JTabbedPane pane = new JTabbedPane();
        setTabbedPane(pane);

        ChannelGuidePanel cgp = new ChannelGuidePanel();
        setChannelGuidePanel(cgp);

        SearchPanel sp = new SearchPanel();
        setSearchPanel(sp);

        pane.add("Channel Guide", cgp);
        pane.add("Search", sp);

        ShowAiringPanel sap = new ShowAiringPanel(this);
        cgp.addPropertyChangeListener("ShowAiring", sap);
        cgp.addPropertyChangeListener("ShowAiring", ra);
        sp.addPropertyChangeListener("ShowAiring", sap);
        sp.addPropertyChangeListener("ShowAiring", ra);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.25;
        gbc.weighty = 0.0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getRecordingButton(), gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.25;
        gbc.weighty = 0.0;
        gbc.gridwidth = 1;
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getUpcomingButton(), gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.25;
        gbc.weighty = 0.0;
        gbc.gridwidth = 1;
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getRuleButton(), gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.25;
        gbc.weighty = 0.0;
        gbc.gridwidth = 1;
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(getRecordButton(), gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 4;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(pane, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridwidth = 4;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(sap, gbc);
    }

    public void populate() {

        ChannelGuidePanel cgp = getChannelGuidePanel();
        if (cgp != null) {

            cgp.setNMS(getNMS());
        }

        SearchPanel sp = getSearchPanel();
        if (sp != null) {

            sp.setNMS(getNMS());
        }
    }

    private SchedulerPanel getThisPanel() {
        return (this);
    }

    private JButton getRuleButton() {
        return (ruleButton);
    }

    private void setRuleButton(JButton b) {
        ruleButton = b;
    }

    private JButton getRecordingButton() {
        return (recordingButton);
    }

    private void setRecordingButton(JButton b) {
        recordingButton = b;
    }

    private JButton getUpcomingButton() {
        return (upcomingButton);
    }

    private void setUpcomingButton(JButton b) {
        upcomingButton = b;
    }

    private JButton getRecordButton() {
        return (recordButton);
    }

    private void setRecordButton(JButton b) {
        recordButton = b;
    }

    private JTabbedPane getTabbedPane() {
        return (tabbedPane);
    }

    private void setTabbedPane(JTabbedPane tp) {
        tabbedPane = tp;
    }

    private ChannelGuidePanel getChannelGuidePanel() {
        return (channelGuidePanel);
    }

    private void setChannelGuidePanel(ChannelGuidePanel p) {
        channelGuidePanel = p;
    }

    private SearchPanel getSearchPanel() {
        return (searchPanel);
    }

    private void setSearchPanel(SearchPanel p) {
        searchPanel = p;
    }

    private JXFrame getRecordingFrame() {
        return (recordingFrame);
    }

    private void setRecordingFrame(JXFrame f) {
        recordingFrame = f;
    }

    private DisplayRecordingPanel getDisplayRecordingPanel() {
        return (displayRecordingPanel);
    }

    private void setDisplayRecordingPanel(DisplayRecordingPanel p) {
        displayRecordingPanel = p;
    }

    private RecordingAction getRecordingAction() {
        return (recordingAction);
    }

    private void setRecordingAction(RecordingAction a) {
        recordingAction = a;
    }

    private JXFrame getUpcomingFrame() {
        return (upcomingFrame);
    }

    private void setUpcomingFrame(JXFrame f) {
        upcomingFrame = f;
    }

    private DisplayUpcomingPanel getDisplayUpcomingPanel() {
        return (displayUpcomingPanel);
    }

    private void setDisplayUpcomingPanel(DisplayUpcomingPanel p) {
        displayUpcomingPanel = p;
    }

    private UpcomingAction getUpcomingAction() {
        return (upcomingAction);
    }

    private void setUpcomingAction(UpcomingAction a) {
        upcomingAction = a;
    }

    private JXFrame getRuleFrame() {
        return (ruleFrame);
    }

    private void setRuleFrame(JXFrame f) {
        ruleFrame = f;
    }

    private EditRecordingRulePanel getEditRecordingRulePanel() {
        return (editRecordingRulePanel);
    }

    private void setEditRecordingRulePanel(EditRecordingRulePanel p) {
        editRecordingRulePanel = p;
    }

    private RuleAction getRuleAction() {
        return (ruleAction);
    }

    private void setRuleAction(RuleAction a) {
        ruleAction = a;
    }

    /**
     * Convenience method to get a channel by it's Id.
     *
     * @param id A given Id.
     * @return A Channel instance if it exists.
     */
    public Channel getChannelById(int id) {

        Channel result = null;

        ChannelGuidePanel cgp = getChannelGuidePanel();
        if (cgp != null) {

            result = cgp.getChannelById(id);
        }

        return (result);
    }

    class RecordingAction extends AbstractAction implements JobListener {

        private boolean showIt;
        private SchedulerPanel schedulerPanel;

        public RecordingAction(SchedulerPanel p) {

            schedulerPanel = p;

            ImageIcon sm = new ImageIcon(getClass().getResource("info16.png"));
            ImageIcon lge = new ImageIcon(getClass().getResource("info32.png"));
            putValue(NAME, "Recordings");
            putValue(SHORT_DESCRIPTION, "Recordings");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_C));
            setShowIt(true);
        }

        private boolean isShowIt() {
            return (showIt);
        }

        private void setShowIt(boolean b) {
            showIt = b;
        }

        public void update() {

            setShowIt(false);
            actionPerformed(null);
        }

        public void jobUpdate(JobEvent event) {

            if (event.getType() == JobEvent.COMPLETE) {

                Serializable s = event.getState();
                if (s instanceof Recording[]) {

                    Recording[] array = (Recording[]) s;
                    if (array != null) {

                        JXFrame f = getRecordingFrame();
                        if (f == null) {

                            DisplayRecordingPanel drp =
                                new DisplayRecordingPanel(schedulerPanel);
                            drp.setRecordings(array);
                            setDisplayRecordingPanel(drp);
                            f = new JXFrame();
                            f.setTitle("Recordings");
                            f.add(drp);
                            f.pack();
                            setRecordingFrame(f);
                            Rectangle r = getBounds(RECORDING_FRAME);
                            if (r != null) {
                                f.setBounds(r);
                            }

                            if (isShowIt()) {
                                f.setVisible(true);
                            }

                        } else {

                            DisplayRecordingPanel drp =
                                getDisplayRecordingPanel();
                            if (drp != null) {
                                drp.setRecordings(array);
                            }
                            if (isShowIt()) {
                                f.setVisible(true);
                            }
                        }
                    }
                }
            }

            setShowIt(true);
        }

        public void actionPerformed(ActionEvent e) {

            NMS n = getNMS();
            if (n != null) {

                RecordingJob rj = new RecordingJob(n);
                ProgressBar pbar =
                    new ProgressBar(getTabbedPane(), "Recordings...", rj);
                pbar.addJobListener(this);
                pbar.execute();
            }
        }
    }

    class UpcomingAction extends AbstractAction implements JobListener {

        private boolean showIt;

        public UpcomingAction() {

            ImageIcon sm = new ImageIcon(getClass().getResource("info16.png"));
            ImageIcon lge = new ImageIcon(getClass().getResource("info32.png"));
            putValue(NAME, "Upcoming Recordings");
            putValue(SHORT_DESCRIPTION, "Upcoming Recordings");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_U));
            setShowIt(true);
        }

        private boolean isShowIt() {
            return (showIt);
        }

        private void setShowIt(boolean b) {
            showIt = b;
        }

        public void update() {

            setShowIt(false);
            actionPerformed(null);
        }

        public void jobUpdate(JobEvent event) {

            if (event.getType() == JobEvent.COMPLETE) {

                Serializable s = event.getState();
                if (s instanceof Upcoming[]) {

                    Upcoming[] array = (Upcoming[]) s;
                    if (array != null) {

                        JXFrame f = getUpcomingFrame();
                        if (f == null) {

                            DisplayUpcomingPanel dup = new DisplayUpcomingPanel();
                            dup.setUpcomings(array);
                            setDisplayUpcomingPanel(dup);
                            f = new JXFrame();
                            f.setTitle("Upcoming");
                            f.add(dup);
                            f.pack();
                            setUpcomingFrame(f);
                            Rectangle r = getBounds(UPCOMING_FRAME);
                            if (r != null) {
                                f.setBounds(r);
                            }
                            if (isShowIt()) {
                                f.setVisible(true);
                            }

                        } else {

                            DisplayUpcomingPanel dup = getDisplayUpcomingPanel();
                            if (dup != null) {
                                dup.setUpcomings(array);
                            }
                            if (isShowIt()) {
                                f.setVisible(true);
                            }
                        }
                    }

                } else {

                    JOptionPane.showMessageDialog(Util.findFrame(SchedulerPanel.this),
                        "No upcoming recordings scheduled.", "alert", JOptionPane.ERROR_MESSAGE);
                }
            }

            setShowIt(true);
        }

        public void actionPerformed(ActionEvent e) {

            NMS n = getNMS();
            if (n != null) {

                UpcomingJob uj = new UpcomingJob(n);
                ProgressBar pbar =
                    new ProgressBar(getTabbedPane(), "Upcoming...", uj);
                pbar.addJobListener(this);
                pbar.execute();
            }
        }
    }

    class RuleAction extends AbstractAction implements JobListener {

        private boolean showIt;

        public RuleAction() {

            ImageIcon sm = new ImageIcon(getClass().getResource("movie16.png"));
            ImageIcon lge =
                new ImageIcon(getClass().getResource("movie32.png"));
            putValue(NAME, "Rules");
            putValue(SHORT_DESCRIPTION, "Rules");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_L));
            setShowIt(true);
        }

        private boolean isShowIt() {
            return (showIt);
        }

        private void setShowIt(boolean b) {
            showIt = b;
        }

        public void update() {

            setShowIt(false);
            actionPerformed(null);
        }

        public void jobUpdate(JobEvent event) {

            if (event.getType() == JobEvent.COMPLETE) {

                Serializable s = event.getState();
                if (s instanceof RecordingRule[]) {

                    RecordingRule[] array = (RecordingRule[]) s;
                    if (array != null) {

                        for (int i = 0; i < array.length; i++) {
                            array[i].setSortBy(RecordingRule.SORT_BY_NAME);
                        }
                        Arrays.sort(array);

                        JXFrame f = getRuleFrame();
                        if (f == null) {

                            EditRecordingRulePanel errp =
                                new EditRecordingRulePanel(getNMS());
                            errp.setRecordingRules(array);
                            setEditRecordingRulePanel(errp);
                            f = new JXFrame();
                            f.setTitle("Rules");
                            f.add(errp);
                            f.pack();
                            setRuleFrame(f);
                            Rectangle r = getBounds(RULE_FRAME);
                            if (r != null) {
                                f.setBounds(r);
                            }
                            if (isShowIt()) {
                                f.setVisible(true);
                            }

                        } else {

                            EditRecordingRulePanel errp =
                                getEditRecordingRulePanel();
                            if (errp != null) {
                                errp.setRecordingRules(array);
                            }
                            if (isShowIt()) {
                                f.setVisible(true);
                            }
                        }
                    }
                }
            }

            setShowIt(true);
        }

        public void actionPerformed(ActionEvent e) {

            NMS n = getNMS();
            if (n != null) {

                RuleJob rj = new RuleJob(n);
                ProgressBar pbar =
                    new ProgressBar(getTabbedPane(), "Rules...", rj);
                pbar.addJobListener(this);
                pbar.execute();
            }
        }
    }

    class RecordAction extends AbstractAction
        implements PropertyChangeListener, JobListener {

        private ShowAiring showAiring;

        public RecordAction() {

            ImageIcon sm =
                new ImageIcon(getClass().getResource("record16.png"));
            ImageIcon lge =
                new ImageIcon(getClass().getResource("record32.png"));
            putValue(NAME, "Record");
            putValue(SHORT_DESCRIPTION, "Record");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_R));
        }

        private ShowAiring getShowAiring() {
            return (showAiring);
        }

        private void setShowAiring(ShowAiring sa) {
            showAiring = sa;
        }

        public void propertyChange(PropertyChangeEvent event) {

            if (event.getPropertyName().equals("ShowAiring")) {

                ShowAiring sa = (ShowAiring) event.getNewValue();
                setShowAiring(sa);
                setEnabled(sa != null);
            }
        }

        public void jobUpdate(JobEvent event) {

            if (event.getType() == JobEvent.COMPLETE) {
                //log(INFO, "saved!");
            }
        }

        public void actionPerformed(ActionEvent e) {

            ShowAiring sa = getShowAiring();
            if (sa != null) {

                Show show = sa.getShow();
                Airing airing = sa.getAiring();
                ChannelGuidePanel cgp = getChannelGuidePanel();
                NMS n = getNMS();
                if ((n != null) && (show != null) && (airing != null)
                    && (cgp != null)) {

                    RecordingRule rr = new RecordingRule();
                    rr.setShowAiring(sa);
                    rr.setType(RecordingRule.SERIES_TYPE);
                    rr.setName(show.getTitle());
                    rr.setShowId(show.getId());
                    rr.setSeriesId(show.getSeriesId());
                    rr.setChannelId(airing.getChannelId());
                    rr.setListingId(airing.getListingId());
                    rr.setDuration(airing.getDuration());
                    rr.setPriority(RecordingRule.NORMAL_PRIORITY);
                    rr.setTasks(n.getTasks());

                    RecordingRulePanel rrp = new RecordingRulePanel();
                    rrp.setNMS(n);
                    rrp.setRecordingRule(rr);
                    if (Util.showDialog(Util.findFrame(getThisPanel()),
                        "Add Rule", rrp)) {

                        AddRuleJob arj = new AddRuleJob(n, rr);
                        ProgressBar pbar = new ProgressBar(getTabbedPane(),
                            "Add Rule...", arj);
                        pbar.addJobListener(this);
                        pbar.execute();
                    }
                }
            }
        }
    }

}

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
package org.jflicks.ui.view.scheduler;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.NMSUtil;
import org.jflicks.tv.Airing;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;
import org.jflicks.tv.Upcoming;
import org.jflicks.ui.view.JFlicksView;
import org.jflicks.util.ProgressBar;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXFrame;

/**
 * A implements a View so a user can control the scheduling of
 * recordings.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SchedulerView extends JFlicksView implements ActionListener {

    private static final String HOWTO =
        "http://www.jflicks.org/wiki/index.php?title=Scheduler";

    private static final String RECORDING_FRAME = "recordings";
    private static final String UPCOMING_FRAME = "upcoming";
    private static final String RULE_FRAME = "rule";
    private static final String MAIN_FRAME = "main";

    private NMS[] nms;
    private JXFrame frame;
    private AboutPanel aboutPanel;
    private JComboBox nmsComboBox;
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
    public SchedulerView() {
    }

    private NMS[] getNMS() {
        return (nms);
    }

    private void setNMS(NMS[] array) {
        nms = array;
    }

    public NMS getNMSByHostPort(String s) {

        NMS result = null;

        if (s != null) {

            result = NMSUtil.select(getNMS(), s);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void modelPropertyChange(PropertyChangeEvent event) {

        String name = event.getPropertyName();
        if (name != null) {

            if (name.equals("NMS")) {

                JComboBox cb = getNMSComboBox();
                if (cb != null) {

                    cb.removeAllItems();
                    NMS[] array = (NMS[]) event.getNewValue();
                    setNMS(array);
                    if (array != null) {

                        for (int i = 0; i < array.length; i++) {

                            cb.addItem(array[i].getTitle());
                        }
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public JFrame getFrame() {

        if (frame == null) {

            frame = new JXFrame("jflicks media system - Scheduler");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    exitAction();
                }
            });

            frame.setLayout(new GridBagLayout());

            JComboBox cb = new JComboBox();
            cb.addActionListener(this);
            setNMSComboBox(cb);

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
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(4, 4, 4, 4);

            frame.add(cb, gbc);

            gbc = new GridBagConstraints();
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
            gbc.gridwidth = 1;
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(4, 4, 4, 4);

            frame.add(getRecordButton(), gbc);

            gbc = new GridBagConstraints();
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.gridwidth = 2;
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(4, 4, 4, 4);

            frame.add(pane, gbc);

            gbc = new GridBagConstraints();
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
            gbc.gridwidth = 2;
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(4, 4, 4, 4);

            frame.add(sap, gbc);

            // Build our menubar.
            JMenuBar mb = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            fileMenu.setMnemonic(Integer.valueOf(KeyEvent.VK_F));
            JMenu viewMenu = new JMenu("View");
            viewMenu.setMnemonic(Integer.valueOf(KeyEvent.VK_V));
            JMenu helpMenu = new JMenu("Help");
            helpMenu.setMnemonic(Integer.valueOf(KeyEvent.VK_H));

            ExitAction exitAction = new ExitAction();
            fileMenu.addSeparator();
            fileMenu.add(exitAction);

            RuleAction rrAction = new RuleAction();
            viewMenu.add(rrAction);
            setRuleAction(rrAction);

            UpcomingAction upAction = new UpcomingAction();
            viewMenu.add(upAction);
            setUpcomingAction(upAction);

            RecordingAction recAction = new RecordingAction(this);
            viewMenu.add(recAction);
            setRecordingAction(recAction);

            HelpAction helpAction = new HelpAction();
            helpMenu.add(helpAction);
            AboutAction aboutAction = new AboutAction();
            helpMenu.add(aboutAction);

            mb.add(fileMenu);
            mb.add(viewMenu);
            mb.add(helpMenu);
            frame.setJMenuBar(mb);

            try {

                BufferedImage image =
                    ImageIO.read(getClass().getResource("icon.png"));
                frame.setIconImage(image);

            } catch (IOException ex) {

                log(WARNING, "Did not find icon for aplication.");
            }

            frame.pack();
            Rectangle r = getBounds(MAIN_FRAME);
            if (r != null) {
                frame.setBounds(r);
            }
        }

        return (frame);
    }

    private JComboBox getNMSComboBox() {
        return (nmsComboBox);
    }

    private void setNMSComboBox(JComboBox cb) {
        nmsComboBox = cb;
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

    private NMS getSelectedNMS() {

        NMS result = null;

        NMS[] array = getNMS();
        JComboBox cb = getNMSComboBox();
        if ((array != null) && (cb != null)) {

            int index = cb.getSelectedIndex();
            if ((index >= 0) && (index < array.length)) {
                result = array[index];
            }
        }

        return (result);
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

    /**
     * Time to exit.
     */
    public void exitAction() {

        JXFrame f = getRecordingFrame();
        if (f != null) {
            setBounds(RECORDING_FRAME, f.getBounds());
        }

        f = getUpcomingFrame();
        if (f != null) {
            setBounds(UPCOMING_FRAME, f.getBounds());
        }

        f = getRuleFrame();
        if (f != null) {
            setBounds(RULE_FRAME, f.getBounds());
        }

        JFrame mf = getFrame();
        if (mf != null) {
            setBounds(MAIN_FRAME, mf.getBounds());
        }

        log(INFO, "saving properties....");
        saveProperties();

        super.exitAction();
    }

    /**
     * {@inheritDoc}
     */
    public void messageReceived(String s) {

        if (s != null) {

            if ((s.startsWith(NMSConstants.MESSAGE_RECORDING_UPDATE))
                || (s.startsWith(NMSConstants.MESSAGE_RECORDING_ADDED))
                || (s.startsWith(NMSConstants.MESSAGE_RECORDING_REMOVED))) {

                // Kind of a cheesy way to do it but what the heck...
                DisplayRecordingPanel drp = getDisplayRecordingPanel();
                RecordingAction a = getRecordingAction();
                if ((drp != null) && (a != null)) {

                    a.update();
                }

            } else if ((s.startsWith(NMSConstants.MESSAGE_RULE_UPDATE))
                || (s.startsWith(NMSConstants.MESSAGE_RULE_ADDED))) {

                // Kind of a cheesy way to do it but what the heck...
                EditRecordingRulePanel errp = getEditRecordingRulePanel();
                RuleAction a = getRuleAction();
                if ((errp != null) && (a != null)) {

                    a.update();
                }

            } else if (s.startsWith(NMSConstants.MESSAGE_SCHEDULE_UPDATE)) {

                // Kind of a cheesy way to do it but what the heck...
                DisplayUpcomingPanel dup = getDisplayUpcomingPanel();
                UpcomingAction a = getUpcomingAction();
                if ((dup != null) && (a != null)) {

                    a.update();
                }
            }
        }
    }

    /**
     * We listen for action events to respond to user action.
     *
     * @param event A given action event.
     */
    public void actionPerformed(ActionEvent event) {

        if (event.getSource() == getNMSComboBox()) {

            NMS n = getSelectedNMS();
            if (n != null) {

                ChannelGuidePanel cgp = getChannelGuidePanel();
                if (cgp != null) {

                    cgp.setNMS(n);
                }
                SearchPanel sp = getSearchPanel();
                if (sp != null) {

                    sp.setNMS(n);
                }
            }
        }
    }

    class ExitAction extends AbstractAction {

        public ExitAction() {

            ImageIcon sm = new ImageIcon(getClass().getResource("exit16.png"));
            ImageIcon lge = new ImageIcon(getClass().getResource("exit32.png"));
            putValue(NAME, "Exit");
            putValue(SHORT_DESCRIPTION, "Exit");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_X));
        }

        public void actionPerformed(ActionEvent e) {

            exitAction();
        }
    }

    class AboutAction extends AbstractAction {

        public AboutAction() {

            ImageIcon sm = new ImageIcon(getClass().getResource("about16.png"));
            ImageIcon lge =
                new ImageIcon(getClass().getResource("about32.png"));
            putValue(NAME, "About");
            putValue(SHORT_DESCRIPTION, "About jflicks Scheduler");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_A));
        }

        public void actionPerformed(ActionEvent e) {

            if (aboutPanel == null) {

                aboutPanel = new AboutPanel();
            }

            if (aboutPanel != null) {

                Util.showDialog(getFrame(), "About...", aboutPanel, false);
            }
        }
    }

    class HelpAction extends AbstractAction {

        public HelpAction() {

            ImageIcon sm = new ImageIcon(getClass().getResource("help16.png"));
            ImageIcon lge = new ImageIcon(getClass().getResource("help32.png"));
            putValue(NAME, "Online Help");
            putValue(SHORT_DESCRIPTION, "Online Documentaion");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_O));
        }

        public void actionPerformed(ActionEvent e) {

            Desktop desktop = Desktop.getDesktop();
            if (desktop != null) {

                if (desktop.isDesktopSupported()) {

                    try {

                        desktop.browse(new URI(HOWTO));

                    } catch (URISyntaxException ex) {

                        JOptionPane.showMessageDialog(getFrame(),
                            "Could not load browser", "alert",
                            JOptionPane.ERROR_MESSAGE);

                    } catch (IOException ex) {

                        JOptionPane.showMessageDialog(getFrame(),
                            "Could not load browser", "alert",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
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
                log(INFO, "saved!");
            }
        }

        public void actionPerformed(ActionEvent e) {

            ShowAiring sa = getShowAiring();
            if (sa != null) {

                Show show = sa.getShow();
                Airing airing = sa.getAiring();
                ChannelGuidePanel cgp = getChannelGuidePanel();
                NMS n = getSelectedNMS();
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

                    System.out.println("chanid: " + rr.getChannelId());
                    System.out.println("listid: " + rr.getListingId());

                    RecordingRulePanel rrp = new RecordingRulePanel();
                    rrp.setNMS(n);
                    rrp.setRecordingRule(rr);
                    if (Util.showDialog(getFrame(), "Add Rule", rrp)) {

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
                                new EditRecordingRulePanel(getSelectedNMS());
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

            NMS n = getSelectedNMS();
            if (n != null) {

                RuleJob rj = new RuleJob(n);
                ProgressBar pbar =
                    new ProgressBar(getTabbedPane(), "Rules...", rj);
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

                            DisplayUpcomingPanel dup =
                                new DisplayUpcomingPanel();
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

                            DisplayUpcomingPanel dup =
                                getDisplayUpcomingPanel();
                            if (dup != null) {
                                dup.setUpcomings(array);
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

            NMS n = getSelectedNMS();
            if (n != null) {

                UpcomingJob uj = new UpcomingJob(n);
                ProgressBar pbar =
                    new ProgressBar(getTabbedPane(), "Upcoming...", uj);
                pbar.addJobListener(this);
                pbar.execute();
            }
        }
    }

    class RecordingAction extends AbstractAction implements JobListener {

        private boolean showIt;
        private SchedulerView schedulerView;

        public RecordingAction(SchedulerView v) {

            schedulerView = v;

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
                                new DisplayRecordingPanel(schedulerView);
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

            NMS n = getSelectedNMS();
            if (n != null) {

                RecordingJob rj = new RecordingJob(n);
                ProgressBar pbar =
                    new ProgressBar(getTabbedPane(), "Recordings...", rj);
                pbar.addJobListener(this);
                pbar.execute();
            }
        }
    }

}

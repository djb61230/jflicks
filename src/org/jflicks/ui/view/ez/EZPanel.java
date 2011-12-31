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
package org.jflicks.ui.view.ez;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import org.jflicks.configure.Configuration;
import org.jflicks.configure.FromChoiceTypePanel;
import org.jflicks.configure.ListTypePanel;
import org.jflicks.configure.NameValue;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Task;
import org.jflicks.util.MessagePanel;
import org.jflicks.util.PromptPanel;
import org.jflicks.util.Util;

/**
 * This panel is designed to contain the most likely things in an NMS
 * installation that the user might want to change.  So in a sense it
 * has "hard-wired" code to search Configuration instances for specific
 * properties that it deems "basic" or "simple" property values.  The
 * other desktop App called Client is more generic and fully supports
 * editing ALL Configuration settings.  But it also requires more
 * understanding of the system as a whole.  The average user should be
 * able to only need this one.  The other program is there if they want
 * total control.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class EZPanel extends JXPanel implements ListSelectionListener,
    ChangeListener {

    private JFrame frame;
    private NMS nms;
    private Configuration[] configurations;
    private EZObject originalObject;
    private EZObject object;
    private EZRecorder selectedRecorder;
    private JSpinner artSpinner;
    private JSpinner queueSpinner;
    private JList recorderList;
    private JList schedulesDirectList;
    private JTextField indexerTextField;
    private JTextField listingTextField;
    private MessagePanel messagePanel;
    private JButton saveButton;
    private JButton editIndexerButton;
    private JButton editListingButton;
    private JButton scanButton;
    private JButton channelListButton;

    private EZPanel() {
    }

    public EZPanel(JFrame f, NMS n) {

        setFrame(f);
        setNMS(n);

        performLayout();

        if (n != null) {
            setConfigurations(n.getConfigurations());
        }
    }

    private JFrame getFrame() {
        return (frame);
    }

    private void setFrame(JFrame f) {
        frame = f;
    }

    private NMS getNMS() {
        return (nms);
    }

    private void setNMS(NMS n) {
        nms = n;
    }

    private Configuration[] getConfigurations() {
        return (configurations);
    }

    private void setConfigurations(Configuration[] array) {
        configurations = array;

        Task[] tarray = null;
        NMS n = getNMS();
        if (n != null) {

            tarray = n.getTasks();
        }

        EZObject eobj = new EZObject(array, tarray);
        setOriginalObject(new EZObject(eobj));
        setObject(eobj);
        updateState();
        JSpinner s = getQueueSpinner();
        if (s != null) {

            s.setValue(Integer.valueOf(eobj.getMaxJobs()));
        }

        s = getArtSpinner();
        if (s != null) {

            s.setValue(Integer.valueOf(eobj.getUpdateTimeInMinutes()));
        }

        JList l = getRecorderList();
        if (l != null) {

            EZRecorder[] recs = eobj.getRecorders();
            if (recs != null) {

                l.setListData(recs);

            } else {

                l.setListData(new String[0]);
            }
        }

        l = getSchedulesDirectList();
        if (l != null) {

            String[] lnames = eobj.getListingNames();
            if (lnames != null) {
                l.setListData(lnames);
            } else {
                l.setListData(new String[0]);
            }
        }
    }

    private EZObject getOriginalObject() {
        return (originalObject);
    }

    private void setOriginalObject(EZObject o) {
        originalObject = o;
    }

    private EZObject getObject() {
        return (object);
    }

    private void setObject(EZObject o) {
        object = o;
    }

    private EZRecorder getSelectedRecorder() {
        return (selectedRecorder);
    }

    private void setSelectedRecorder(EZRecorder r) {
        selectedRecorder = r;

        JTextField itf = getIndexerTextField();
        JTextField ltf = getListingTextField();
        if ((itf != null) && (ltf != null)) {

            if (r != null) {

                EZIndexer ind = r.getIndexer();
                if (ind != null) {
                    itf.setText(ind.getDescription());
                } else {
                    itf.setText("");
                }
                ltf.setText(r.getListingName());

            } else {

                itf.setText("");
                ltf.setText("");
            }
        }
    }

    private JSpinner getArtSpinner() {
        return (artSpinner);
    }

    private void setArtSpinner(JSpinner s) {
        artSpinner = s;
    }

    private JSpinner getQueueSpinner() {
        return (queueSpinner);
    }

    private void setQueueSpinner(JSpinner s) {
        queueSpinner = s;
    }

    private JList getRecorderList() {
        return (recorderList);
    }

    private void setRecorderList(JList l) {
        recorderList = l;
    }

    private JList getSchedulesDirectList() {
        return (schedulesDirectList);
    }

    private void setSchedulesDirectList(JList l) {
        schedulesDirectList = l;
    }

    private JTextField getIndexerTextField() {
        return (indexerTextField);
    }

    private void setIndexerTextField(JTextField tf) {
        indexerTextField = tf;
    }

    private JTextField getListingTextField() {
        return (listingTextField);
    }

    private void setListingTextField(JTextField tf) {
        listingTextField = tf;
    }

    private MessagePanel getMessagePanel() {
        return (messagePanel);
    }

    private void setMessagePanel(MessagePanel mp) {
        messagePanel = mp;
    }

    private JButton getSaveButton() {
        return (saveButton);
    }

    private void setSaveButton(JButton b) {
        saveButton = b;
    }

    private JButton getEditIndexerButton() {
        return (editIndexerButton);
    }

    private void setEditIndexerButton(JButton b) {
        editIndexerButton = b;
    }

    private JButton getEditListingButton() {
        return (editListingButton);
    }

    private void setEditListingButton(JButton b) {
        editListingButton = b;
    }

    private JButton getScanButton() {
        return (scanButton);
    }

    private void setScanButton(JButton b) {
        scanButton = b;
    }

    private JButton getChannelListButton() {
        return (channelListButton);
    }

    private void setChannelListButton(JButton b) {
        channelListButton = b;
    }

    public void messageReceived(String s) {

        if ((s != null)
            && (s.startsWith(NMSConstants.MESSAGE_RECORDER_SCAN_UPDATE))) {

            MessagePanel mp = getMessagePanel();
            if (mp != null) {

                String tmp = s.substring(
                    NMSConstants.MESSAGE_RECORDER_SCAN_UPDATE.length());
                tmp = tmp.trim();
                mp.addMessage(tmp);
            }
        }
    }

    private Configuration findConfigurationBySource(Configuration[] array,
        String source) {

        Configuration result = null;

        if ((array != null) && (source != null)) {

            for (int i = 0; i < array.length; i++) {

                if (source.equals(array[i].getSource())) {

                    result = array[i];
                    break;
                }
            }
        }

        return (result);
    }

    private Configuration[] findConfigurationByName(Configuration[] array,
        String name) {

        Configuration[] result = null;

        if ((array != null) && (name != null)) {

            ArrayList<Configuration> l = new ArrayList<Configuration>();
            for (int i = 0; i < array.length; i++) {

                if (name.equals(array[i].getName())) {

                    l.add(array[i]);
                }
            }

            if (l.size() > 0) {

                result = l.toArray(new Configuration[l.size()]);
            }
        }

        return (result);
    }

    private void performLayout() {

        setLayout(new GridBagLayout());

        MessagePanel mp = new MessagePanel("Messages", 20, 60);
        setMessagePanel(mp);

        SpinnerNumberModel model = new SpinnerNumberModel(25, 5, 25, 5);
        JSpinner artspinner = new JSpinner(model);
        artspinner.addChangeListener(this);
        setArtSpinner(artspinner);

        JXPanel artPanel = new JXPanel();
        artPanel.add(new JXLabel("Check every "));
        artPanel.add(artspinner);
        artPanel.add(new JXLabel("minutes"));
        artPanel.setBorder(BorderFactory.createTitledBorder(
            "Automatic Art Search & Download"));
        artPanel.setToolTipText("<html>"
            + "Control how often the system searches the Internet<br/>"
            + "for artwork for your recordings and video files."
            + "</html>");

        model = new SpinnerNumberModel(1, 1, 4, 1);
        JSpinner queuespinner = new JSpinner(model);
        queuespinner.addChangeListener(this);
        setQueueSpinner(queuespinner);
        JXPanel queuePanel = new JXPanel();
        queuePanel.add(new JXLabel("Max concurrent tasks to run"));
        queuePanel.add(queuespinner);
        queuePanel.setBorder(BorderFactory.createTitledBorder(
            "Processing Recordings"));
        queuePanel.setToolTipText("<html>"
            + "After a Recording is finished there are a few<br/>"
            + "things to be done.  These include commercial<br/>"
            + "flagging and making a file that is suitable for<br/>"
            + "easy playing and seeking on devices like the Roku."
            + "</html>");

        JList sdlist = new JList();
        setSchedulesDirectList(sdlist);
        sdlist.setToolTipText("<html>"
            + "The listings you have set up on Schedule's Direct.<br/>"
            + "If this list is empty ensure you have set your<br/>"
            + "user name and password using the button below.<br/>"
            + "And of course you have a Schedule's Direct account.<br/>"
            + "</html>");
        JScrollPane sdScroller = new JScrollPane(sdlist,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JXPanel sdScrollerPanel = new JXPanel(new BorderLayout(4, 4));
        sdScrollerPanel.add(sdScroller, BorderLayout.CENTER);
        sdScrollerPanel.setBorder(BorderFactory.createTitledBorder(
            "Schedule's Direct Listing(s)"));

        SetUserPasswordAction userPassAction = new SetUserPasswordAction();
        JButton userPassButton = new JButton(userPassAction);

        JList reclist = new JList();
        reclist.addListSelectionListener(this);
        reclist.setToolTipText("<html>"
            + "The Recorders that are available to use to<br/>"
            + "make recordings.  You need to associate them<br/>"
            + "with a Schedule's Direct listing before they can<br/>"
            + "do any work.  Of course the listing should match<br/>"
            + "the capability of the Recorder."
            + "</html>");
        setRecorderList(reclist);
        reclist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane recScroller = new JScrollPane(reclist,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JXPanel recScrollerPanel = new JXPanel(new BorderLayout(4, 4));
        recScrollerPanel.add(recScroller, BorderLayout.CENTER);
        recScrollerPanel.setBorder(BorderFactory.createTitledBorder(
            "Recorder(s)"));

        JTextField indexerTextField = new JTextField();
        indexerTextField.setToolTipText("<html>"
            + "There are many ways to process a recording so<br/>"
            + "it can be watched nicely on devices like the<br/>"
            + "Roku.  Usually have to trade time for quality.<br/>"
            + "</html>");
        indexerTextField.setEditable(false);
        indexerTextField.setBorder(null);
        setIndexerTextField(indexerTextField);
        JXPanel indexerPanel = new JXPanel(new BorderLayout(4, 4));
        indexerPanel.add(indexerTextField, BorderLayout.CENTER);
        indexerPanel.setToolTipText("<html>"
            + "There are many ways to process a recording so<br/>"
            + "it can be watched nicely on devices like the<br/>"
            + "Roku.  Usually have to trade time for quality.<br/>"
            + "</html>");

        EditIndexerAction editIndexerAction = new EditIndexerAction();
        JButton ibutton = new JButton(editIndexerAction);
        setEditIndexerButton(ibutton);
        indexerPanel.add(ibutton, BorderLayout.EAST);
        indexerPanel.setBorder(BorderFactory.createTitledBorder(
            "Recording Processor"));

        JTextField sdlistingTextField = new JTextField();
        setListingTextField(sdlistingTextField);
        sdlistingTextField.setEditable(false);
        sdlistingTextField.setBorder(null);
        JXPanel sdlistingPanel = new JXPanel(new BorderLayout(4, 4));
        sdlistingPanel.add(sdlistingTextField, BorderLayout.CENTER);

        EditListingAction editListingAction = new EditListingAction();
        JButton lbutton = new JButton(editListingAction);
        setEditListingButton(lbutton);
        sdlistingPanel.add(lbutton, BorderLayout.EAST);
        sdlistingPanel.setBorder(BorderFactory.createTitledBorder(
            "Connected Schedule's Direct Listing"));

        RecorderScanAction scanAction = new RecorderScanAction();
        JButton sbutton = new JButton(scanAction);
        setScanButton(sbutton);

        ChannelListAction channelListAction = new ChannelListAction();
        JButton clbutton = new JButton(channelListAction);
        setChannelListButton(clbutton);

        RefreshAction refreshAction = new RefreshAction();
        JButton refreshButton = new JButton(refreshAction);

        SaveAction saveAction = new SaveAction();
        JButton saveb = new JButton(saveAction);
        setSaveButton(saveb);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(sdScrollerPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(artPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(queuePanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(userPassButton, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(recScrollerPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(indexerPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(sdlistingPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.25;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(sbutton, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.25;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(clbutton, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.75;
        gbc.weighty = 0.50;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(mp, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weightx = 0.25;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(refreshButton, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weightx = 0.25;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(saveb, gbc);
    }

    /**
     * We listen for selection on the recorder list box.
     *
     * @param event The given list selection event.
     */
    public void valueChanged(ListSelectionEvent event) {

        if (!event.getValueIsAdjusting()) {

            if (event.getSource() == getRecorderList()) {

                JList l = getRecorderList();
                int index = l.getSelectedIndex();
                if (index != -1) {

                    setSelectedRecorder((EZRecorder) l.getSelectedValue());

                } else {

                    setSelectedRecorder(null);
                }

                updateState();
            }
        }
    }

    /**
     * We listen for selection on the JSpinners.
     *
     * @param event The given spinner selection event.
     */
    public void stateChanged(ChangeEvent event) {

        EZObject eobj = getObject();
        if (eobj != null) {

            if (event.getSource() instanceof JSpinner) {

                JSpinner s = (JSpinner) event.getSource();
                if (s == getArtSpinner()) {

                    Integer iobj = (Integer) s.getValue();
                    eobj.setUpdateTimeInMinutes(iobj.intValue());

                } else if (s == getQueueSpinner()) {

                    Integer iobj = (Integer) s.getValue();
                    eobj.setMaxJobs(iobj.intValue());
                }
            }

            updateState();
        }
    }

    private void updateState() {

        EZObject orig = getOriginalObject();
        EZObject work = getObject();
        JButton save = getSaveButton();
        if ((orig != null) && (work != null) && (save != null)) {

            save.setEnabled(!orig.equals(work));
        }

        JButton ibutton = getEditIndexerButton();
        if (ibutton != null) {
            ibutton.setEnabled(getSelectedRecorder() != null);
        }
        JButton lbutton = getEditListingButton();
        if (lbutton != null) {
            lbutton.setEnabled(getSelectedRecorder() != null);
        }
        JButton sbutton = getScanButton();
        if (sbutton != null) {
            sbutton.setEnabled(getSelectedRecorder() != null);
        }
        JButton clbutton = getChannelListButton();
        if (clbutton != null) {
            clbutton.setEnabled(getSelectedRecorder() != null);
        }
    }

    private String computeFromArray(String[] array) {

        String result = null;

        if (array != null) {

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; i++) {

                sb.append(array[i]);
                if ((i + 1) < array.length) {
                    sb.append("|");
                }
            }

            result = sb.toString();
        }

        return (result);
    }

    private Configuration analyzeArt() {

        Configuration result = null;

        EZObject orig = getOriginalObject();
        EZObject work = getObject();
        if ((orig != null) && (work != null)) {

            if ((orig.getUpdateTimeInMinutes()
                != work.getUpdateTimeInMinutes())) {

                result = findConfigurationBySource(getConfigurations(),
                    "System Auto Art");
                if (result != null) {

                    NameValue nv = result.findNameValueByName(
                        NMSConstants.UPDATE_TIME_IN_MINUTES);
                    nv.setValue("" + work.getUpdateTimeInMinutes());
                }
            }
        }

        return (result);
    }

    private Configuration analyzePostProc() {

        Configuration result = null;

        EZObject orig = getOriginalObject();
        EZObject work = getObject();
        if ((orig != null) && (work != null)) {

            if ((orig.getMaxJobs() != work.getMaxJobs())) {

                result = findConfigurationBySource(getConfigurations(),
                    "System Post Proc");
                if (result != null) {

                    NameValue nv = result.findNameValueByName(
                        NMSConstants.POST_PROC_MAXIMUM_JOBS);
                    nv.setValue("" + work.getMaxJobs());
                }
            }
        }

        return (result);
    }

    private Configuration analyzeProgramData() {

        Configuration result = null;

        EZObject orig = getOriginalObject();
        EZObject work = getObject();
        if ((orig != null) && (work != null)) {

            String origuser = orig.getUserName();
            String origpass = orig.getPassword();
            String workuser = work.getUserName();
            String workpass = work.getPassword();
            boolean userSame = Util.equalOrNull(origuser, workuser);
            boolean passSame = Util.equalOrNull(origpass, workpass);

            if ((!userSame) || (!passSame)) {

                result = findConfigurationBySource(getConfigurations(),
                    "Schedules Direct");
                if (result != null) {

                    if (!userSame) {

                        NameValue nv = result.findNameValueByName(
                            NMSConstants.USER_NAME);
                        nv.setValue(work.getUserName());
                    }

                    if (!passSame) {

                        NameValue nv = result.findNameValueByName(
                            NMSConstants.PASSWORD);
                        nv.setValue(work.getPassword());
                    }
                }
            }
        }

        return (result);
    }

    private Configuration[] analyzeRecorders() {

        Configuration[] result = null;

        EZObject orig = getOriginalObject();
        EZObject work = getObject();
        if ((orig != null) && (work != null)) {

            ArrayList<Configuration> clist = new ArrayList<Configuration>();

            // We have to worry about Recorder configuration and also
            // the Scheduler Configuration.  First find the Recorders
            // that did change.
            EZRecorder[] origRecorders = orig.getRecorders();
            EZRecorder[] workRecorders = work.getRecorders();

            if ((origRecorders != null) && (workRecorders != null)
                && (origRecorders.length == workRecorders.length)) {

                // This should be true even if there were no editing
                // because the user cannot delete anything.
                ArrayList<EZRecorder> l = new ArrayList<EZRecorder>();
                for (int i = 0; i < origRecorders.length; i++) {

                    if (!origRecorders[i].equals(workRecorders[i])) {

                        l.add(workRecorders[i]);
                    }
                }

                Configuration sched = findConfigurationBySource(
                    getConfigurations(), "Scheduler System");
                if ((sched != null) && (l.size() > 0)) {

                    boolean saveSched = false;
                    for (int i = 0; i < l.size(); i++) {

                        EZRecorder r = l.get(i);
                        Configuration c = findConfigurationBySource(
                            getConfigurations(), r.getName());
                        if (c != null) {

                            // Three properties to set...
                            NameValue nv = c.findNameValueByName(
                                NMSConstants.CUSTOM_CHANNEL_LIST_TYPE);
                            if (nv != null) {
                                nv.setValue(r.getListType());
                            }
                            nv = c.findNameValueByName(
                                NMSConstants.CUSTOM_CHANNEL_LIST);
                            if (nv != null) {
                                nv.setValue(computeFromArray(
                                    r.getChannelList()));
                            }
                            nv = c.findNameValueByName(
                                NMSConstants.RECORDING_INDEXER_NAME);
                            EZIndexer ind = r.getIndexer();
                            if ((nv != null) && (ind != null)) {
                                nv.setValue(ind.getTitle());
                            }

                            clist.add(c);

                            // Now we check the listing name and see
                            // if it changed in the scheduler.  If so
                            // we have to set it and flag the scheduler
                            // to be saved.
                            String lname = r.getListingName();
                            nv = sched.findNameValueByName(r.getName());
                            if ((lname != null) && (nv != null)) {

                                if (!lname.equals(nv.getValue())) {

                                    nv.setValue(lname);
                                    saveSched = true;
                                }
                            }
                        }
                    }

                    if (saveSched) {

                        clist.add(sched);
                    }
                }
            }

            if (clist.size() > 0) {

                result = clist.toArray(new Configuration[clist.size()]);
            }
        }

        return (result);
    }

    private Channel[] getChannels(boolean inclusive, String listing,
        String[] cnumbers) {

        Channel[] result = null;

        // First get the Channels from the NMS for this listing.
        NMS n = getNMS();
        if ((listing != null) && (n != null)) {

            Channel[] chans = n.getChannelsByListingName(listing);
            if (chans != null) {

                // Next see if the cnumbers is valid.
                if ((cnumbers != null) && (cnumbers.length > 0)) {

                    ArrayList<Integer> indexlist = new ArrayList<Integer>();
                    ArrayList<Channel> clist = new ArrayList<Channel>();
                    for (int i = 0; i < cnumbers.length; i++) {

                        String snumber = cnumbers[i];
                        if (snumber != null) {

                            for (int j = 0; j < chans.length; j++) {

                                if (snumber.equals(chans[j].getNumber())) {

                                    clist.add(chans[j]);
                                    indexlist.add(Integer.valueOf(j));
                                    break;
                                }
                            }
                        }
                    }

                    // OK at this point the clist have the Channel instances
                    // matching our argument channel number String array.
                    // So we either want to return these channels or the
                    // channels that are NOT these.  We kept the locations
                    // so we don't have to research for them.
                    if (inclusive) {

                        result = clist.toArray(new Channel[clist.size()]);

                    } else {

                        ArrayList<Channel> notlist = new ArrayList<Channel>();
                        for (int i = 0; i < chans.length; i++) {

                            if (!indexlist.contains(Integer.valueOf(i))) {
                                notlist.add(chans[i]);
                            }
                        }

                        result = notlist.toArray(new Channel[notlist.size()]);
                    }

                    Arrays.sort(result);

                } else {

                    // Ahh bach.  We either send back nothing or all of them.
                    if (inclusive) {
                        result = chans;
                    }
                }
            }
        }

        return (result);
    }

    class SetUserPasswordAction extends AbstractAction {

        public SetUserPasswordAction() {

            putValue(NAME, "Set Schedule's Direct User/Password");
        }

        public void actionPerformed(ActionEvent e) {

            EZObject eobj = getObject();
            if (eobj != null) {

                String[] prompts = {
                    "User Name",
                    "Password"
                };

                JTextField tf = new JTextField(eobj.getUserName(), 25);
                JPasswordField pf = new JPasswordField(eobj.getPassword(), 25);
                JComponent[] comps = {
                    tf,
                    pf
                };

                PromptPanel pp = new PromptPanel(prompts, comps);
                if (Util.showDialog(getFrame(), "Schedule's Direct", pp)) {

                    eobj.setUserName(tf.getText());
                    eobj.setPassword(new String(pf.getPassword()));
                    updateState();
                }
            }
        }

    }

    class EditIndexerAction extends AbstractAction {

        public EditIndexerAction() {

            putValue(NAME, "Select");
        }

        public void actionPerformed(ActionEvent e) {

            EZObject eobj = getObject();
            EZRecorder r = getSelectedRecorder();
            if ((eobj != null) && (r != null)) {

                String[] prompts = {
                    "Indexer(s)"
                };

                JComboBox cb = new JComboBox();
                EZIndexer[] inds = eobj.getIndexers();
                if (inds != null) {

                    for (int i = 0; i < inds.length; i++) {

                        cb.addItem(inds[i]);
                    }
                }

                cb.setSelectedItem(r.getIndexer());
                JComponent[] comps = {
                    cb
                };

                PromptPanel pp = new PromptPanel(prompts, comps);
                if (Util.showDialog(getFrame(), "Recorder Indexer", pp)) {

                    r.setIndexer((EZIndexer) cb.getSelectedItem());
                    setSelectedRecorder(r);
                    updateState();
                }
            }
        }
    }

    class EditListingAction extends AbstractAction {

        public EditListingAction() {

            putValue(NAME, "Select");
        }

        public void actionPerformed(ActionEvent e) {

            EZObject eobj = getObject();
            EZRecorder r = getSelectedRecorder();
            if ((eobj != null) && (r != null)) {

                String[] prompts = {
                    "Listing(s)"
                };

                JComboBox cb = new JComboBox();
                cb.addItem(NMSConstants.NOT_CONNECTED);
                String[] lnames = eobj.getListingNames();
                if (lnames != null) {

                    for (int i = 0; i < lnames.length; i++) {

                        cb.addItem(lnames[i]);
                    }
                }

                cb.setSelectedItem(r.getListingName());
                JComponent[] comps = {
                    cb
                };

                PromptPanel pp = new PromptPanel(prompts, comps);
                if (Util.showDialog(getFrame(), "Recorder Listing", pp)) {

                    r.setListingName((String) cb.getSelectedItem());
                    setSelectedRecorder(r);
                    updateState();
                }
            }
        }

    }

    class ChannelListAction extends AbstractAction {

        public ChannelListAction() {

            putValue(NAME, "Edit Channel List");
        }

        public void actionPerformed(ActionEvent e) {

            EZRecorder r = getSelectedRecorder();
            if (r != null) {

                String[] tchoices = {
                    NMSConstants.LIST_IS_IGNORED,
                    NMSConstants.LIST_IS_A_WHITELIST,
                    NMSConstants.LIST_IS_A_BLACKLIST
                };

                NameValue tnv = new NameValue();
                tnv.setType(NameValue.STRING_FROM_CHOICE_TYPE);
                tnv.setChoices(tchoices);
                tnv.setName("Custom Channel List Type");
                tnv.setDescription("Custom Channel List Type");
                tnv.setValue(r.getListType());

                NameValue lnv = new NameValue();
                lnv.setType(NameValue.STRINGLIST_TYPE);
                lnv.setName("Custom Channel List");
                lnv.setDescription("Custom Channel List");
                lnv.setValue(computeFromArray(r.getChannelList()));

                String[] prompts = {
                    "",
                    ""
                };

                FromChoiceTypePanel fctp = new FromChoiceTypePanel(tnv);

                Channel[] wanted =
                   getChannels(true, r.getListingName(), r.getChannelList());
                Channel[] rest =
                   getChannels(false, r.getListingName(), r.getChannelList());
                EZChannelSelect cs = new EZChannelSelect(wanted, rest);

                JComponent[] comps = {
                    fctp,
                    cs
                };

                PromptPanel pp = new PromptPanel(prompts, comps);
                if (Util.showDialog(getFrame(), "Recorder Channels", pp)) {

                    r.setListType(fctp.getEditedValue());
                    Channel[] chans = cs.getLeftChannels();
                    if (chans != null) {

                        String[] carray = new String[chans.length];
                        for (int i = 0; i < carray.length; i++) {
                            carray[i] = chans[i].getNumber();
                        }
                        r.setChannelList(carray);

                    } else {

                        r.setChannelList(null);
                    }
                    setSelectedRecorder(r);
                    updateState();
                }
            }
        }

    }

    class SaveAction extends AbstractAction {

        public SaveAction() {

            putValue(NAME, "Save Changes");
        }

        public void actionPerformed(ActionEvent e) {

            NMS n = getNMS();
            if (n != null) {

                // We have to determine which Configuration actually
                // changed.  We will have one or more saves to do.
                ArrayList<Configuration> l = new ArrayList<Configuration>();
                Configuration c = analyzeArt();
                if (c != null) {

                    l.add(c);
                }

                c = analyzePostProc();
                if (c != null) {

                    l.add(c);
                }

                c = analyzeProgramData();
                if (c != null) {

                    l.add(c);
                }

                Configuration[] array = analyzeRecorders();
                if ((array != null) && (array.length > 0)) {

                    for (int i = 0; i < array.length; i++) {

                        l.add(array[i]);
                    }
                }

                if (l.size() > 0) {

                    for (int i = 0; i < l.size(); i++) {

                        n.save(l.get(i), true);
                    }

                    setConfigurations(n.getConfigurations());
                    updateState();

                } else {

                    String mess = "Could NOT find any changes"
                        + " to save.  This is a bad error.";
                    JOptionPane.showMessageDialog(
                        getFrame(), mess, "Alert",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }

    class RefreshAction extends AbstractAction {

        public RefreshAction() {

            putValue(NAME, "Refresh Settings");
        }

        public void actionPerformed(ActionEvent e) {

            boolean proceed = true;

            JButton save = getSaveButton();
            if ((save != null) && (save.isEnabled())) {

                String mess = "Your changes will be lost,"
                    + " proceed anyway?";

                int result = JOptionPane.showConfirmDialog(getFrame(),
                    mess, "Alert",
                    JOptionPane.YES_NO_OPTION);
                proceed = (result != JOptionPane.NO_OPTION);
            }

            if (proceed) {

                NMS n = getNMS();
                if (n != null) {

                    setConfigurations(n.getConfigurations());
                }
            }
        }

    }

    class RecorderScanAction extends AbstractAction {

        public RecorderScanAction() {

            putValue(NAME, "Channel Scan");
        }

        public void actionPerformed(ActionEvent e) {

            NMS nms = getNMS();
            if (nms != null) {

                EZRecorder r = getSelectedRecorder();
                if (r != null) {

                    String src = r.getName();
                    System.out.println("src: <" + src + ">");

                    Object[] options = {
                        NMSConstants.OTA,
                        NMSConstants.CABLE
                    };
                    Object svalue = JOptionPane.showInputDialog(getFrame(),
                        "Choose one", "Select Frequency Type",
                        JOptionPane.INFORMATION_MESSAGE,
                        null, options, options[0]);
                    if (svalue instanceof String) {

                        String answer = (String) svalue;

                        if (nms.performChannelScan(src, answer)) {

                            MessagePanel mp = getMessagePanel();
                            if (mp != null) {

                                mp.clearMessage();
                            }

                        } else {

                            String mess = "Scan NOT started."
                                + " This could be because:\n"
                                + " 1) This Recorder doesn't"
                                + " have a tuner.\n"
                                + " 2) It's not connected to a"
                                + " Channel Listing.\n"
                                + " Please See the Scheduler"
                                + " configuration to assign one.";
                            JOptionPane.showMessageDialog(
                                getFrame(), mess, "Alert",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(getFrame(),
                        "Please select a "
                        + NMSConstants.RECORDER_NAME, "Alert",
                        JOptionPane.ERROR_MESSAGE);
                }

            } else {

                JOptionPane.showMessageDialog(getFrame(),
                    "Please load an NMS configuration", "Alert",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}

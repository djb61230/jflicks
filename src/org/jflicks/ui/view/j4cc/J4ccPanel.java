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
package org.jflicks.ui.view.j4cc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXPanel;

import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.ui.view.JFlicksView;
import org.jflicks.ui.view.j4cc.configuration.ConfigurationPanel;
import org.jflicks.ui.view.j4cc.scheduler.SchedulerPanel;
import org.jflicks.ui.view.j4cc.status.StatusPanel;
import org.jflicks.util.MessagePanel;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class J4ccPanel extends JXPanel {

    private JFlicksView jflicksView;
    private JFrame frame;
    private NMS nms;
    private JTabbedPane tabbedPane;
    private StatusPanel statusPanel;
    private ConfigurationPanel configurationPanel;
    private SchedulerPanel schedulerPanel;
    private MessagePanel messagePanel;

    private J4ccPanel() {
    }

    public J4ccPanel(JFlicksView v, JFrame f, NMS n) {

        setFrame(f);
        setNMS(n);
        setJFlicksView(v);

        performLayout();
    }

    private JFlicksView getJFlicksView() {
        return (jflicksView);
    }

    private void setJFlicksView(JFlicksView v) {
        jflicksView = v;
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

    private JTabbedPane getTabbedPane() {
        return (tabbedPane);
    }

    private void setTabbedPane(JTabbedPane tp) {
        tabbedPane = tp;
    }

    private StatusPanel getStatusPanel() {
        return (statusPanel);
    }

    private void setStatusPanel(StatusPanel p) {
        statusPanel = p;
    }

    private ConfigurationPanel getConfigurationPanel() {
        return (configurationPanel);
    }

    private void setConfigurationPanel(ConfigurationPanel p) {
        configurationPanel = p;
    }

    private SchedulerPanel getSchedulerPanel() {
        return (schedulerPanel);
    }

    private void setSchedulerPanel(SchedulerPanel p) {
        schedulerPanel = p;
    }

    /*
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
    */

    private void performLayout() {

        setLayout(new BorderLayout());
        JTabbedPane pane = new JTabbedPane();
        setTabbedPane(pane);
        pane.setPreferredSize(new Dimension(900, 750));

        // Add our tabbed panes to the main one.
        StatusPanel statPanel = new StatusPanel();
        statPanel.setJFlicksView(getJFlicksView());
        statPanel.setNMS(getNMS());
        setStatusPanel(statPanel);
        ConfigurationPanel configPanel = new ConfigurationPanel();
        configPanel.setJFlicksView(getJFlicksView());
        configPanel.setNMS(getNMS());
        setConfigurationPanel(configPanel);
        SchedulerPanel schedPanel = new SchedulerPanel();
        schedPanel.setJFlicksView(getJFlicksView());
        schedPanel.setNMS(getNMS());
        setSchedulerPanel(schedPanel);

        pane.add("Status", statPanel);
        pane.add("Configuration", configPanel);
        pane.add("Scheduler", schedPanel);

        add(pane, BorderLayout.CENTER);
    }

}

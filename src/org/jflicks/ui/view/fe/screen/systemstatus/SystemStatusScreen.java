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
package org.jflicks.ui.view.fe.screen.systemstatus;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.nms.NMS;
import org.jflicks.ui.view.fe.FrontEndView;
import org.jflicks.ui.view.fe.MessagePanel;
import org.jflicks.ui.view.fe.NMSProperty;
import org.jflicks.ui.view.fe.ParameterProperty;
import org.jflicks.ui.view.fe.screen.Screen;
import org.jflicks.util.Busy;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports SystemStatus in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SystemStatusScreen extends Screen implements ParameterProperty,
    NMSProperty, JobListener {

    private static final String SOFTWARE_UPDATE = "Software Update";
    private static final String RESTART = "Restart";
    private static final String RSYNC_MESSAGE = "sending incremental file list";

    private NMS[] nms;
    private String[] parameters;
    private String selectedParameter;
    private JXPanel waitPanel;
    private MessagePanel messagePanel;
    private SystemJob systemJob;

    /**
     * Simple empty constructor.
     */
    public SystemStatusScreen() {

        setTitle("System Status");
        BufferedImage bi = getImageByName("SystemStatus");
        setDefaultBackgroundImage(bi);

        String[] array = {

            SOFTWARE_UPDATE,
            RESTART
        };

        setParameters(array);
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
        selectedParameter = s;
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

            if (isParameterSoftwareUpdate()) {

                updateLayout(true);
                SystemJob job =
                    SystemJob.getInstance("./update." + getScriptExtension());
                setSystemJob(job);
                Busy busy = new Busy(getLayeredPane(), job);
                busy.addJobListener(this);
                busy.execute();

            } else if (isParameterRestart()) {

                FrontEndView fev = (FrontEndView) getView();
                if (fev != null) {

                    SystemJob job = SystemJob.getInstance("./restart."
                        + getScriptExtension());
                    JobContainer jc = JobManager.getJobContainer(job);
                    jc.start();
                    fev.exitAction(false);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {

        JLayeredPane pane = getLayeredPane();
        if ((d != null) && (pane != null)) {

            FrontEndView fev = (FrontEndView) getView();

            JXPanel panel = new JXPanel(new BorderLayout());
            JXLabel l = new JXLabel("Working, please wait...");
            l.setHorizontalTextPosition(SwingConstants.CENTER);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setFont(getLargeFont());
            panel.add(l, BorderLayout.CENTER);
            MattePainter p = new MattePainter(Color.BLACK);
            panel.setBackgroundPainter(p);
            panel.setBounds(0, 0, (int) d.getWidth(), (int) d.getHeight());
            setWaitPanel(panel);

            MessagePanel mp = new MessagePanel();
            mp.setBounds(0, 0, (int) d.getWidth(), (int) d.getHeight());
            setMessagePanel(mp);
        }
    }

    private void updateLayout(boolean wait) {

        JLayeredPane pane = getLayeredPane();
        if (pane != null) {

            pane.removeAll();
            if (wait) {

                pane.add(getWaitPanel(), Integer.valueOf(100));

            } else {

                if (isParameterSoftwareUpdate()) {
                    pane.add(getMessagePanel(), Integer.valueOf(100));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            System.out.println("The update finished!!!!!");

            int bundleCount = 0;
            SystemJob job = getSystemJob();
            if (job != null) {

                String text = job.getOutputText();
                if (text != null) {

                    int index = text.indexOf(RSYNC_MESSAGE);
                    if (index != -1) {

                        text = text.substring(index);
                        String[] lines = text.split("\n");
                        if ((lines != null) && (lines.length > 0)) {

                            for (int i = 0; i < lines.length; i++) {

                                if (lines[i].startsWith("jflicks")) {
                                    bundleCount++;
                                }
                            }
                        }
                    }
                }
                setSystemJob(null);
            }

            System.out.println("bundleCount: " + bundleCount);
            MessagePanel mp = getMessagePanel();
            System.out.println("mp: " + mp);
            if (mp != null) {

                String message = "No need to restart as there were no updates.";
                if (bundleCount > 0) {

                    String more = " was";
                    if (bundleCount > 1) {
                        more = "s were";
                    }
                    message = "Please restart as " + bundleCount + " update"
                        + more + " found.";
                }

                mp.setMessage(message);
            }

            updateLayout(false);
        }
    }

    private JXPanel getWaitPanel() {
        return (waitPanel);
    }

    private void setWaitPanel(JXPanel p) {
        waitPanel = p;
    }

    private MessagePanel getMessagePanel() {
        return (messagePanel);
    }

    private void setMessagePanel(MessagePanel p) {
        messagePanel = p;
    }

    private SystemJob getSystemJob() {
        return (systemJob);
    }

    private void setSystemJob(SystemJob j) {
        systemJob = j;
    }

    private boolean isParameterSoftwareUpdate() {
        return (SOFTWARE_UPDATE.equals(getSelectedParameter()));
    }

    private boolean isParameterRestart() {
        return (RESTART.equals(getSelectedParameter()));
    }

    private String getScriptExtension() {

        String result = "sh";

        if (Util.isWindows()) {
            result = "cmd";
        }

        return (result);
    }

}

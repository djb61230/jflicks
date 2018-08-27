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
package org.jflicks.ui.view.server;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
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
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.nms.NMSUtil;
import org.jflicks.ui.view.JFlicksView;
import org.jflicks.ui.view.status.StatusPanel;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXFrame;

import com.install4j.api.launcher.ApplicationLauncher;

/**
 * A implements a View so a user can control the scheduling of
 * recordings.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ServerView extends JFlicksView {

    private static final String MAIN_FRAME = "main";

    private NMS[] nms;
    private JXFrame frame;
    private StatusPanel statusPanel;

    /**
     * Default constructor.
     */
    public ServerView() {

        Timer timer = new Timer("autoRefresh");
        RefreshTimerTask rtt = new RefreshTimerTask();
        timer.scheduleAtFixedRate(rtt, 90000, 180000);
    }

    private NMS[] getNMS() {
        return (nms);
    }

    private void setNMS(NMS[] array) {

        nms = array;
        StatusPanel sp = getStatusPanel();
        if ((array != null) && (sp != null)) {

            NMS n = getLocalNMS(array);
            if (n != null) {

                sp.setNMS(n);
                sp.populate();
            }
        }
    }

    public NMS getLocalNMS(NMS[] array) {

        NMS result = null;

        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                String cn = array[i].getClass().getName();
                if ((cn != null) && (cn.startsWith("org.jflicks.nms"))) {

                    result = array[i];
                    break;
                }
            }
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

                NMS[] array = (NMS[]) event.getNewValue();
                setNMS(array);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public JFrame getFrame() {

        if (frame == null) {

            frame = new JXFrame("jflicks for cord cutters - Server");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    exitAction(true);
                }
            });

            frame.setLayout(new GridBagLayout());

            StatusPanel sp = new StatusPanel();
            setStatusPanel(sp);
            NMS n = getLocalNMS(getNMS());
            if (n != null) {

                sp.setNMS(n);
                sp.populate();
            }

            RefreshAction raction = new RefreshAction();
            JButton ref = new JButton(raction);

            CheckAction caction = new CheckAction();
            JButton check = new JButton(caction);

            ExitAction action = new ExitAction();
            JButton shut = new JButton(action);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.gridwidth = 3;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(4, 4, 4, 4);

            frame.add(sp, gbc);

            gbc = new GridBagConstraints();
            gbc.weightx = 0.33;
            gbc.weighty = 0.0;
            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.NONE;
            gbc.insets = new Insets(4, 4, 4, 4);

            frame.add(ref, gbc);

            gbc = new GridBagConstraints();
            gbc.weightx = 0.33;
            gbc.weighty = 0.0;
            gbc.gridwidth = 1;
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;
            gbc.insets = new Insets(4, 4, 4, 4);

            frame.add(check, gbc);

            gbc = new GridBagConstraints();
            gbc.weightx = 0.33;
            gbc.weighty = 0.0;
            gbc.gridwidth = 1;
            gbc.gridx = 2;
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.fill = GridBagConstraints.NONE;
            gbc.insets = new Insets(4, 4, 4, 4);

            frame.add(shut, gbc);

            try {

                BufferedImage image = ImageIO.read(getClass().getResource("icon.png"));
                frame.setIconImage(image);

            } catch (IOException ex) {

                LogUtil.log(LogUtil.WARNING, "Did not find icon for aplication.");
            }

            frame.pack();
            Rectangle r = getBounds(MAIN_FRAME);
            if (r != null) {
                frame.setBounds(r);
            } else {

                r = new Rectangle(0, 0, 760, 400);
                frame.setBounds(r);
            }
        }

        return (frame);
    }

    private StatusPanel getStatusPanel() {
        return (statusPanel);
    }

    private void setStatusPanel(StatusPanel p) {
        statusPanel = p;
    }

    /**
     * Time to exit.
     */
    public void exitAction(boolean ask) {

        JFrame mf = getFrame();
        if (mf != null) {
            setBounds(MAIN_FRAME, mf.getBounds());
        }

        LogUtil.log(LogUtil.INFO, "saving properties....");
        saveProperties();

        super.exitAction(ask);
    }

    /**
     * {@inheritDoc}
     */
    public void messageReceived(String s) {

        // Update the status panel on all messages.
        StatusPanel sp = getStatusPanel();
        if (sp != null) {

            sp.populate();
        }
    }

    class CheckAction extends AbstractAction {

        private boolean prepare = false;

        public CheckAction() {

            ImageIcon sm = new ImageIcon(getClass().getResource("check16.png"));
            ImageIcon lge = new ImageIcon(getClass().getResource("check32.png"));
            putValue(NAME, "Check for update");
            putValue(SHORT_DESCRIPTION, "Check for update");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_C));
        }

        public void actionPerformed(ActionEvent event) {

            // This will return immediately if you call it from the EDT,
            // otherwise it will block until the installer application exits
            ApplicationLauncher.launchApplicationInProcess("939", null, new ApplicationLauncher.Callback() {
                public void exited(int exitValue) {

                    if (!prepare) {

                        if (exitValue == 1) {

                            // I think this means no update.
                            JOptionPane.showMessageDialog(getFrame(), "You have the latest version.", "Result",
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
        
                public void prepareShutdown() {
                    prepare = true;
                }
            }, ApplicationLauncher.WindowMode.FRAME, null);
        }

    }

    class RefreshAction extends AbstractAction {

        public RefreshAction() {

            ImageIcon sm = new ImageIcon(getClass().getResource("refresh16.png"));
            ImageIcon lge = new ImageIcon(getClass().getResource("refresh32.png"));
            putValue(NAME, "Refresh");
            putValue(SHORT_DESCRIPTION, "Refresh Status");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_R));
        }

        public void actionPerformed(ActionEvent event) {

            StatusPanel sp = getStatusPanel();
            if (sp != null) {

                sp.populate();
            }
        }

    }

    class ExitAction extends AbstractAction {

        public ExitAction() {

            ImageIcon sm = new ImageIcon(getClass().getResource("exit16.png"));
            ImageIcon lge = new ImageIcon(getClass().getResource("exit32.png"));
            putValue(NAME, "Shutdown");
            putValue(SHORT_DESCRIPTION, "Shutdown");
            putValue(SMALL_ICON, sm);
            putValue(LARGE_ICON_KEY, lge);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_X));
        }

        public void actionPerformed(ActionEvent e) {

            exitAction(true);
        }
    }

    class RefreshTimerTask extends TimerTask {

        public RefreshTimerTask() {
        }

        public void run() {

            StatusPanel sp = getStatusPanel();
            if (sp != null) {

                sp.populate();
            }
        }
    }

}

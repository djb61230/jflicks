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
package org.jflicks.ui.view.client;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import org.jflicks.configure.Configuration;
import org.jflicks.configure.ConfigurationPanel;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.ui.view.JFlicksView;
import org.jflicks.util.MessagePanel;
import org.jflicks.util.TabClose;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXFrame;

/**
 * A base class that full Views can extend.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ClientView extends JFlicksView {

    private static final String HOWTO =
        "http://www.jflicks.org/wiki/index.php?title=Server_Configuration";

    private JXFrame frame;
    private JTabbedPane tabbedPane;
    private JMenu nmsMenu;
    private AboutPanel aboutPanel;
    private HashMap<JTabbedPane, NMS> hashMap;
    private MessagePanel messagePanel;

    /**
     * Default constructor.
     */
    public ClientView() {

        setHashMap(new HashMap<JTabbedPane, NMS>());
    }

    private HashMap<JTabbedPane, NMS> getHashMap() {
        return (hashMap);
    }

    private void setHashMap(HashMap<JTabbedPane, NMS> m) {
        hashMap = m;
    }

    private void add(JTabbedPane p, NMS n) {

        HashMap<JTabbedPane, NMS> m = getHashMap();
        if ((m != null) && (p != null) && (n != null)) {

            m.put(p, n);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void modelPropertyChange(PropertyChangeEvent event) {

        String name = event.getPropertyName();
        if (name != null) {

            if (name.equals("NMS")) {

                JMenu menu = getNMSMenu();
                if (menu != null) {

                    menu.removeAll();
                    NMS[] array = (NMS[]) event.getNewValue();
                    if (array != null) {

                        for (int i = 0; i < array.length; i++) {

                            menu.add(new NMSAction(array[i]));
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

            frame = new JXFrame("jflicks media system - Server Configuration");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    exitAction();
                }
            });

            frame.setLayout(new BorderLayout());

            JTabbedPane pane = new JTabbedPane();
            setTabbedPane(pane);
            pane.setPreferredSize(new Dimension(800, 600));

            frame.add(pane, BorderLayout.CENTER);

            // Build our menubar.
            JMenuBar mb = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            fileMenu.setMnemonic(Integer.valueOf(KeyEvent.VK_F));
            JMenu helpMenu = new JMenu("Help");
            helpMenu.setMnemonic(Integer.valueOf(KeyEvent.VK_H));

            JMenu nmenu = new JMenu("NMS");
            setNMSMenu(nmenu);
            fileMenu.add(nmenu);

            DeleteConfigurationAction daction = new DeleteConfigurationAction();
            fileMenu.add(daction);

            RecorderScanAction rsaction = new RecorderScanAction();
            fileMenu.add(rsaction);

            ExitAction exitAction = new ExitAction();
            fileMenu.addSeparator();
            fileMenu.add(exitAction);

            HelpAction helpAction = new HelpAction();
            helpMenu.add(helpAction);
            AboutAction aboutAction = new AboutAction();
            helpMenu.add(aboutAction);

            mb.add(fileMenu);
            mb.add(helpMenu);
            frame.setJMenuBar(mb);

            MessagePanel mp = new MessagePanel("Log", 20, 60);
            setMessagePanel(mp);

            try {

                BufferedImage image =
                    ImageIO.read(getClass().getResource("icon.png"));
                frame.setIconImage(image);

            } catch (IOException ex) {

                log(WARNING, "Did not find icon for aplication.");
            }

            frame.pack();
        }

        return (frame);
    }

    private JTabbedPane getTabbedPane() {
        return (tabbedPane);
    }

    private void setTabbedPane(JTabbedPane tp) {
        tabbedPane = tp;
    }

    private JMenu getNMSMenu() {
        return (nmsMenu);
    }

    private void setNMSMenu(JMenu m) {
        nmsMenu = m;
    }

    private MessagePanel getMessagePanel() {
        return (messagePanel);
    }

    private void setMessagePanel(MessagePanel mp) {
        messagePanel = mp;
    }

    /**
     * {@inheritDoc}
     */
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
            putValue(SHORT_DESCRIPTION, "About jflicks Configuration");
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

    class NMSAction extends AbstractAction implements PropertyChangeListener {

        private NMS nms;

        public NMSAction(NMS n) {

            nms = n;
            if (n != null) {

                putValue(NAME, n.getTitle());
            }
        }

        public void propertyChange(PropertyChangeEvent event) {

            String name = event.getPropertyName();
            if ((nms != null) && (name != null)) {

                Configuration c = (Configuration) event.getNewValue();
                if (c != null) {

                    nms.save(c, true);
                }
            }
        }

        public void actionPerformed(ActionEvent e) {

            if (nms != null) {

                // Load up a new tab with a ConfigurationPanel.
                JTabbedPane tp = getTabbedPane();
                if (tp != null) {

                    int index = tp.getTabCount();
                    JTabbedPane sub = new JTabbedPane(JTabbedPane.LEFT);
                    Configuration[] confs = nms.getConfigurations();
                    if (confs != null) {

                        for (int i = 0; i < confs.length; i++) {

                            ConfigurationPanel cp =
                                new ConfigurationPanel(confs[i]);
                            cp.addPropertyChangeListener("Configuration", this);
                            sub.add(confs[i].getName(), cp);
                        }
                    }

                    add(sub, nms);
                    tp.add(nms.getTitle(), sub);
                    tp.setTabComponentAt(index,
                        new TabClose(tp, nms.getTitle()));
                    tp.setSelectedIndex(index);
                }
            }
        }
    }

    class DeleteConfigurationAction extends AbstractAction {

        public DeleteConfigurationAction() {

            putValue(NAME, "Delete Configuration");
        }

        public void actionPerformed(ActionEvent e) {

            HashMap<JTabbedPane, NMS> m = getHashMap();
            JTabbedPane tp = getTabbedPane();
            if ((m != null) && (tp != null)) {

                JTabbedPane sub = (JTabbedPane) tp.getSelectedComponent();
                if (sub != null) {

                    NMS nms = m.get(sub);
                    if (nms != null) {

                        ConfigurationPanel cp =
                            (ConfigurationPanel) sub.getSelectedComponent();
                        if (cp != null) {

                            Configuration c = cp.getConfiguration();
                            if (c != null) {

                                nms.removeConfiguration(c);
                                sub.remove(cp);
                            }
                        }
                    }
                }
            }
        }
    }

    class RecorderScanAction extends AbstractAction {

        public RecorderScanAction() {

            putValue(NAME, "Recorder Channel Scan");
        }

        public void actionPerformed(ActionEvent e) {

            HashMap<JTabbedPane, NMS> m = getHashMap();
            JTabbedPane tp = getTabbedPane();
            if ((m != null) && (tp != null)) {

                JTabbedPane sub = (JTabbedPane) tp.getSelectedComponent();
                if (sub != null) {

                    NMS nms = m.get(sub);
                    if (nms != null) {

                        ConfigurationPanel cp =
                            (ConfigurationPanel) sub.getSelectedComponent();
                        if (cp != null) {

                            Configuration c = cp.getConfiguration();
                            if (c != null) {

                                String name = c.getName();
                                if (NMSConstants.RECORDER_NAME.equals(name)) {

                                    String src = c.getSource();
                                    System.out.println("src: <" + src + ">");
                                    if (nms.performChannelScan(src)) {

                                        MessagePanel mp = getMessagePanel();
                                        if (mp != null) {

                                            mp.clearMessage();
                                            Util.showDoneDialog(getFrame(),
                                                "Scanning...", mp);
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
                                            getFrame(), mess, "alert",
                                            JOptionPane.ERROR_MESSAGE);
                                    }

                                } else {

                                    JOptionPane.showMessageDialog(getFrame(),
                                        "Please select a "
                                        + NMSConstants.RECORDER_NAME, "alert",
                                        JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    }

                } else {

                    JOptionPane.showMessageDialog(getFrame(),
                        "Please load an NMS configuration", "alert",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}

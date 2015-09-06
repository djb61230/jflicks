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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.ToolTipManager;

import org.jflicks.configure.Configuration;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.ui.view.JFlicksView;
import org.jflicks.util.LogUtil;
import org.jflicks.util.PropertyUtil;
import org.jflicks.util.TabClose;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXFrame;

/**
 * A base class that full Views can extend.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class J4ccView extends JFlicksView {

    private static final String HOWTO =
        "http://www.jflicks.org/wiki/index.php?title=Server_Configuration";

    private JXFrame frame;
    private JTabbedPane tabbedPane;
    private JMenu nmsMenu;
    private AboutPanel aboutPanel;
    private HashMap<J4ccPanel, NMS> hashMap;
    private Properties stateProperties;

    /**
     * Default constructor.
     */
    public J4ccView() {

        loadStateProperties();
        setHashMap(new HashMap<J4ccPanel, NMS>());
    }

    private Properties getStateProperties() {
        return (stateProperties);
    }

    private void setStateProperties(Properties p) {
        stateProperties = p;
    }

    private String getStateProperty(String s) {

        String result = null;

        Properties p = getStateProperties();
        if (p != null) {

            result = p.getProperty(s);
        }

        return (result);
    }

    private Rectangle getStateBounds(String prefix) {

        Rectangle result = null;

        Properties p = getStateProperties();
        if ((p != null) && (prefix != null)) {

            int x = Util.str2int(p.getProperty(prefix + "_x"), -1);
            int y = Util.str2int(p.getProperty(prefix + "_y"), -1);
            int w = Util.str2int(p.getProperty(prefix + "_w"), -1);
            int h = Util.str2int(p.getProperty(prefix + "_h"), -1);
            if ((x != -1) && (y != -1) && (w != -1) && (h != -1)) {

                result = new Rectangle(x, y, w, h);
            }
        }

        return (result);
    }

    private void setStateBounds(String prefix, Rectangle r) {

        Properties p = getStateProperties();
        if ((p != null) && (prefix != null) && (r != null)) {

            p.setProperty(prefix + "_x", "" + r.x);
            p.setProperty(prefix + "_y", "" + r.y);
            p.setProperty(prefix + "_w", "" + r.width);
            p.setProperty(prefix + "_h", "" + r.height);
        }
    }

    private void loadStateProperties() {

        File home = new File(".");
        File prop = new File(home, "j4cc.properties");
        if (prop.exists()) {

            setStateProperties(PropertyUtil.read(prop));

        } else {

            setStateProperties(new Properties());
        }
    }

    private void saveStateProperties() {

        Properties p = getStateProperties();
        if (p != null) {

            PropertyUtil.write(new File("j4cc.properties"), p);
        }
    }

    private HashMap<J4ccPanel, NMS> getHashMap() {
        return (hashMap);
    }

    private void setHashMap(HashMap<J4ccPanel, NMS> m) {
        hashMap = m;
    }

    private void add(J4ccPanel p, NMS n) {

        HashMap<J4ccPanel, NMS> m = getHashMap();
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

            ToolTipManager.sharedInstance().setDismissDelay(10000);

            frame = new JXFrame("jflicks media system for Cord Cutters");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    exitAction(true);
                }
            });

            frame.setLayout(new BorderLayout());

            JTabbedPane pane = new JTabbedPane();
            setTabbedPane(pane);
            pane.setPreferredSize(new Dimension(925, 730));

            frame.add(pane, BorderLayout.CENTER);

            // Build our menubar.
            JMenuBar mb = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            fileMenu.setMnemonic(Integer.valueOf(KeyEvent.VK_F));
            JMenu helpMenu = new JMenu("Help");
            helpMenu.setMnemonic(Integer.valueOf(KeyEvent.VK_H));

            JMenu nmenu = new JMenu("jflicks Server");
            setNMSMenu(nmenu);
            fileMenu.add(nmenu);

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

            try {

                BufferedImage image =
                    ImageIO.read(getClass().getResource("icon.png"));
                frame.setIconImage(image);

            } catch (IOException ex) {

                LogUtil.log(LogUtil.WARNING, "Did not find icon for aplication.");
            }

            frame.pack();

            Rectangle r = getStateBounds("main");
            if (r != null) {

                frame.setBounds(r);
            }
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

    /**
     * {@inheritDoc}
     */
    public void messageReceived(String s) {

        // We need to determine the selected tab and forward this
        // message.
        /*
        JTabbedPane tp = getTabbedPane();
        if (tp != null) {

            if (tp.getTabCount() > 0) {

                J4ccPanel j4ccp = (J4ccPanel) tp.getSelectedComponent();
                j4ccp.messageReceived(s);
            }
        }
        */
    }

    /**
     * Exit out of the OSGi framework nicely.
     *
     * @param ask True when the user should be prompted for "sureness".
     */
    @Override
    public void exitAction(boolean ask) {

        setStateBounds("main", getFrame().getBounds());
        saveStateProperties();
        super.exitAction(ask);
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
            exitAction(true);
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

                // Load up a new tab with a J4ccPanel.
                JTabbedPane tp = getTabbedPane();
                if (tp != null) {

                    int index = tp.getTabCount();
                    J4ccPanel j4ccp = new J4ccPanel(J4ccView.this, getFrame(), nms);

                    add(j4ccp, nms);
                    tp.add(nms.getTitle(), j4ccp);
                    tp.setTabComponentAt(index,
                        new TabClose(tp, nms.getTitle()));
                    tp.setSelectedIndex(index);
                }
            }
        }
    }

}

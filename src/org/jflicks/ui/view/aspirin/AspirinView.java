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
package org.jflicks.ui.view.aspirin;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Rectangle;
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
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import org.jflicks.nms.NMS;
import org.jflicks.ui.view.JFlicksView;
import org.jflicks.ui.view.aspirin.analyze.Analyze;
import org.jflicks.util.Util;

import org.jdesktop.swingx.JXFrame;

/**
 * A base class that full Views can extend.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class AspirinView extends JFlicksView {

    private static final String HOWTO =
        "http://www.jflicks.org/wiki/index.php?title=Aspirin";

    private static final String EXECUTE_FRAME = "execute";
    private static final String MAIN_FRAME = "main";

    private NMS[] nms;
    private JXFrame frame;
    private ControlPanel controlPanel;
    private JMenu nmsMenu;
    private AboutPanel aboutPanel;
    private ArrayList<Analyze> analyzeList;

    /**
     * Default constructor.
     */
    public AspirinView() {

        setAnalyzeList(new ArrayList<Analyze>());
    }

    private NMS[] getNMS() {
        return (nms);
    }

    private void setNMS(NMS[] array) {
        nms = array;
    }

    private ArrayList<Analyze> getAnalyzeList() {
        return (analyzeList);
    }

    private void setAnalyzeList(ArrayList<Analyze> l) {
        analyzeList = l;
    }

    /**
     * We keep track of Analyze instances that are available to us.
     *
     * @param a A given Analyze.
     */
    public void addAnalyze(Analyze a) {

        ArrayList<Analyze> l = getAnalyzeList();
        if ((l != null) && (a != null)) {

            l.add(a);
            updateAnalyze();
        }
    }

    /**
     * We keep track of Analyze instances that are available to us.
     *
     * @param a A given Analyze.
     */
    public void removeAnalyze(Analyze a) {

        ArrayList<Analyze> l = getAnalyzeList();
        if ((l != null) && (a != null)) {

            l.remove(a);
            updateAnalyze();
        }
    }

    private void updateAnalyze() {

        ArrayList<Analyze> l = getAnalyzeList();
        ControlPanel cp = getControlPanel();
        if ((cp != null) && (l != null)) {

            if (l.size() > 0) {

                Analyze[] array = l.toArray(new Analyze[l.size()]);
                cp.setAnalyzes(array);
            }
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

            frame = new JXFrame("jflicks media system - Aspirin");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    exitAction();
                }
            });

            frame.setLayout(new BorderLayout());

            ControlPanel control = new ControlPanel();
            JFrame ef = new JFrame("Aspirin - Execute Analysis");
            ef.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            control.setExecuteFrame(ef);
            setControlPanel(control);
            updateAnalyze();

            frame.add(control, BorderLayout.CENTER);

            // Build our menubar.
            JMenuBar mb = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            fileMenu.setMnemonic(Integer.valueOf(KeyEvent.VK_F));
            JMenu helpMenu = new JMenu("Help");
            helpMenu.setMnemonic(Integer.valueOf(KeyEvent.VK_H));

            JMenu nmenu = new JMenu("NMS");
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

                log(WARNING, "Did not find icon for aplication.");
            }

            frame.pack();
            Rectangle r = getBounds(MAIN_FRAME);
            if (r != null) {
                frame.setBounds(r);
            }

            r = getBounds(EXECUTE_FRAME);
            if (r != null) {
                ef.setBounds(r);
            }
        }

        return (frame);
    }

    private ControlPanel getControlPanel() {
        return (controlPanel);
    }

    private void setControlPanel(ControlPanel p) {
        controlPanel = p;
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
    }

    /**
     * Time to exit.
     */
    public void exitAction() {

        JFrame mf = getFrame();
        if (mf != null) {
            setBounds(MAIN_FRAME, mf.getBounds());
        }

        ControlPanel cp = getControlPanel();
        if (cp != null) {

            JFrame ef = cp.getExecuteFrame();
            if (ef != null) {

                setBounds(EXECUTE_FRAME, ef.getBounds());
            }
        }

        log(INFO, "saving properties....");
        saveProperties();

        super.exitAction();
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
            putValue(SHORT_DESCRIPTION, "About jflicks Aspirin");
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

    static class NMSAction extends AbstractAction implements
        PropertyChangeListener {

        private NMS nms;

        public NMSAction(NMS n) {

            nms = n;
            if (n != null) {

                putValue(NAME, n.getTitle());
            }
        }

        public void propertyChange(PropertyChangeEvent event) {
        }

        public void actionPerformed(ActionEvent e) {
        }
    }

}

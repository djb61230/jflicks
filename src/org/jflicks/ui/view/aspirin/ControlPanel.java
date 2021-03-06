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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jflicks.ui.view.aspirin.analyze.Analyze;
import org.jflicks.util.BundleFilter;
import org.jflicks.util.Util;

/**
 * Main panel that allows the user to run Analyze instances against a
 * jflicks installation.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ControlPanel extends JPanel implements ActionListener,
    ListSelectionListener {

    private Analyze[] analyzes;
    private Analyze[] currentAnalyzes;
    private JTextField pathTextField;
    private JButton browseButton;
    private JList analyzeList;
    private AnalyzePanel analyzePanel;
    private JButton executeButton;
    private JFrame executeFrame;
    private ExecutePanel executePanel;

    /**
     * Simple constructor.
     */
    public ControlPanel() {

        JTextField pathtf = new JTextField(20);
        pathtf.setEditable(false);
        pathtf.setBorder(null);
        setPathTextField(pathtf);
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.setBorder(BorderFactory.createTitledBorder(
            "jflicks Installation to Examine"));
        pathPanel.add(pathtf, BorderLayout.CENTER);

        JButton browse = new JButton("Browse");
        browse.addActionListener(this);
        setBrowseButton(browse);

        JList alist = new JList();
        alist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        alist.addListSelectionListener(this);
        setAnalyzeList(alist);

        JScrollPane alistScroller = new JScrollPane(alist,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder(
            "Analyzers To Execute"));
        listPanel.add(alistScroller, BorderLayout.CENTER);

        AnalyzePanel ap = new AnalyzePanel();
        setAnalyzePanel(ap);

        ExecutePanel ep = new ExecutePanel();
        setExecutePanel(ep);

        JButton execute = new JButton("Execute");
        execute.addActionListener(this);
        setExecuteButton(execute);

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(pathPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(browse, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(listPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(ap, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(execute, gbc);
    }

    /**
     * We keep track of the Analyze instances we are to execute.
     *
     * @return An array of Analyze instances.
     */
    public Analyze[] getAnalyzes() {

        Analyze[] result = null;

        if (analyzes != null) {

            result = Arrays.copyOf(analyzes, analyzes.length);
        }

        return (result);
    }

    /**
     * We keep track of the Analyze instances we are to execute.
     *
     * @param array An array of Analyze instances.
     */
    public void setAnalyzes(Analyze[] array) {

        if (array != null) {
            analyzes = Arrays.copyOf(array, array.length);
        } else {
            analyzes = null;
        }

        setCurrentAnalyzes(analyzes);

        JList l = getAnalyzeList();
        if (l != null) {

            l.setListData(analyzes);
        }
    }

    private Analyze[] getCurrentAnalyzes() {
        return (currentAnalyzes);
    }

    private void setCurrentAnalyzes(Analyze[] array) {
        currentAnalyzes = array;
    }

    private JTextField getPathTextField() {
        return (pathTextField);
    }

    private void setPathTextField(JTextField tf) {
        pathTextField = tf;
    }

    private JButton getBrowseButton() {
        return (browseButton);
    }

    private void setBrowseButton(JButton b) {
        browseButton = b;
    }

    private JList getAnalyzeList() {
        return (analyzeList);
    }

    private void setAnalyzeList(JList l) {
        analyzeList = l;
    }

    private AnalyzePanel getAnalyzePanel() {
        return (analyzePanel);
    }

    private void setAnalyzePanel(AnalyzePanel p) {
        analyzePanel = p;
    }

    private ExecutePanel getExecutePanel() {
        return (executePanel);
    }

    private void setExecutePanel(ExecutePanel p) {
        executePanel = p;
    }

    private JButton getExecuteButton() {
        return (executeButton);
    }

    private void setExecuteButton(JButton b) {
        executeButton = b;
    }

    /**
     * We are given a Frame to use so the View can save it's state.
     *
     * @return A JFrame instance.
     */
    public JFrame getExecuteFrame() {
        return (executeFrame);
    }

    /**
     * We are given a Frame to use so the View can save it's state.
     *
     * @param f A JFrame instance.
     */
    public void setExecuteFrame(JFrame f) {
        executeFrame = f;

        if (f != null) {

            ExecutePanel ep = getExecutePanel();
            f.setLayout(new BorderLayout());
            f.add(ep, BorderLayout.CENTER);
            f.pack();
        }
    }

    private void browseAction() {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            File dir = chooser.getSelectedFile();
            File bundle = new File(dir, "bundle");
            if ((bundle.exists()) && (bundle.isDirectory())) {

                BundleFilter bf = new BundleFilter();
                String[] bundleNames = bundle.list(bf);

                Analyze[] all = getAnalyzes();
                if ((all != null) && (bundleNames != null)) {

                    ArrayList<Analyze> l = new ArrayList<Analyze>();
                    for (int i = 0; i < all.length; i++) {

                        if (all[i].isNeeded(bundleNames)) {

                            all[i].setInstallationPath(dir.getPath());
                            l.add(all[i]);
                        }
                    }

                    if (l.size() > 0) {

                        Analyze[] array = l.toArray(new Analyze[l.size()]);
                        setCurrentAnalyzes(array);
                        JList list = getAnalyzeList();
                        if (list != null) {

                            list.setListData(array);
                        }
                    }
                }

                JTextField tf = getPathTextField();
                if (tf != null) {

                    tf.setText(dir.getPath());
                }

            } else {

                JOptionPane.showMessageDialog(Util.findFrame(this),
                    "Not a jflicks install!!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void executeAction() {

        Analyze[] array = getCurrentAnalyzes();
        JFrame f = getExecuteFrame();
        if ((array != null) && (f != null) && (!f.isVisible())) {

            ExecutePanel ep = getExecutePanel();
            ep.setAnalyzes(array);
            f.setVisible(true);

            ep.execute();
        }
    }

    /**
     * We listen for events from the Browse and Execute buttons.
     *
     * @param event A given ActionEvent instance.
     */
    public void actionPerformed(ActionEvent event) {

        if (event.getSource() == getBrowseButton()) {
            browseAction();
        } else if (event.getSource() == getExecuteButton()) {
            executeAction();
        }
    }

    /**
     * We listen for clicks on our JList so we can update our UI if
     * necessary.
     *
     * @param event A given ListSelectionEvent instance.
     */
    public void valueChanged(ListSelectionEvent event) {

        if (!event.getValueIsAdjusting()) {

            if (event.getSource() == getAnalyzeList()) {

                AnalyzePanel ap = getAnalyzePanel();
                if (ap != null) {

                    JList l = getAnalyzeList();
                    int index = l.getSelectedIndex();
                    if (index != -1) {

                        Analyze a = (Analyze) l.getSelectedValue();
                        ap.setAnalyze(a);

                    } else {

                        ap.setAnalyze(null);
                    }
                }
            }
        }
    }

}

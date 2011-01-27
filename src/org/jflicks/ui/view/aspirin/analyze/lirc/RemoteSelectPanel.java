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
package org.jflicks.ui.view.aspirin.analyze.lirc;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jflicks.util.Util;

/**
 * Simple panel to allow the user to select a lircd.conf file
 * and pick a remote defined in it.  After doing so allow the
 * user to create a configuration file using an AssignmentPanel.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RemoteSelectPanel extends JPanel implements ActionListener {

    private static final String NO_REMOTE = "No Remote Selected";

    private JTextField pathTextField;
    private JButton browseButton;
    private JComboBox remoteComboBox;
    private JButton createButton;
    private HashMap<Remote, AssignmentPanel> remoteHashMap;

    /**
     * Simple constructor.
     */
    public RemoteSelectPanel() {

        setRemoteHashMap(new HashMap<Remote, AssignmentPanel>());

        JTextField pathtf = new JTextField(20);
        pathtf.setEditable(false);
        pathtf.setBorder(null);
        setPathTextField(pathtf);
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.setBorder(BorderFactory.createTitledBorder(
            "The lircd.conf file"));
        pathPanel.add(pathtf, BorderLayout.CENTER);

        JButton browse = new JButton("Browse");
        browse.addActionListener(this);
        setBrowseButton(browse);

        JComboBox remotes = new JComboBox();
        remotes.addActionListener(this);
        Remote dummy = new Remote();
        dummy.setName(NO_REMOTE);
        remotes.addItem(dummy);
        setRemoteComboBox(remotes);

        JButton create = new JButton("Create Configuration");
        create.addActionListener(this);
        create.setEnabled(false);
        setCreateButton(create);

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
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(remotes, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(create, gbc);
    }

    private HashMap<Remote, AssignmentPanel> getRemoteHashMap() {
        return (remoteHashMap);
    }

    private void setRemoteHashMap(HashMap<Remote, AssignmentPanel> hm) {
        remoteHashMap = hm;
    }

    private AssignmentPanel getAssignmentPanel(Remote r) {

        AssignmentPanel result = null;

        HashMap<Remote, AssignmentPanel> hm = getRemoteHashMap();
        if ((hm != null) && (r != null)) {

            result = hm.get(r);
        }

        return (result);
    }

    private void add(Remote r, AssignmentPanel ap) {

        HashMap<Remote, AssignmentPanel> hm = getRemoteHashMap();
        if ((hm != null) && (r != null) && (ap != null)) {

            hm.put(r, ap);
        }
    }

    private void clear() {

        HashMap<Remote, AssignmentPanel> hm = getRemoteHashMap();
        if (hm != null) {

            hm.clear();
        }
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

    private JComboBox getRemoteComboBox() {
        return (remoteComboBox);
    }

    private void setRemoteComboBox(JComboBox cb) {
        remoteComboBox = cb;
    }

    private JButton getCreateButton() {
        return (createButton);
    }

    private void setCreateButton(JButton b) {
        createButton = b;
    }

    private Remote getSelectedRemote() {

        Remote result = null;

        JComboBox cb = getRemoteComboBox();
        if (cb != null) {

            result = (Remote) cb.getSelectedItem();
        }

        return (result);
    }

    private void browseAction() {

        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            File lircd = chooser.getSelectedFile();
            if (lircd != null) {

                JTextField tf = getPathTextField();
                if (tf != null) {

                    tf.setText(lircd.getPath());

                    JComboBox cb = getRemoteComboBox();
                    if (cb != null) {

                        clear();
                        Remote none = (Remote) cb.getItemAt(0);
                        cb.removeAllItems();
                        cb.addItem(none);
                        ParseLirc pl = new ParseLirc(tf.getText());
                        Remote[] array = pl.getRemotes();
                        if (array != null) {

                            for (int i = 0; i < array.length; i++) {

                                cb.addItem(array[i]);
                                add(array[i], new AssignmentPanel(array[i]));
                            }
                        }

                        cb.setSelectedIndex(0);
                    }
                }
            }
        }
    }

    private void remoteAction() {

        JButton b = getCreateButton();
        if (b != null) {

            Remote r = getSelectedRemote();
            if (r != null) {

                b.setEnabled(!NO_REMOTE.equals(r.getName()));
            }
        }
    }

    private void createAction() {

        AssignmentPanel ap = getAssignmentPanel(getSelectedRemote());
        Util.showDialog(null, "Assign Buttons", ap, false);
    }

    /**
     * We listen for events from the Browse and Create buttons.
     *
     * @param event A given ActionEvent instance.
     */
    public void actionPerformed(ActionEvent event) {

        if (event.getSource() == getBrowseButton()) {
            browseAction();
        } else if (event.getSource() == getRemoteComboBox()) {
            remoteAction();
        } else if (event.getSource() == getCreateButton()) {
            createAction();
        }
    }

    /**
     * Convenience method to write out a LircJob.lircrc file.
     *
     * @param f A File pointing to where it goes in an installation.
     * @return True on success.
     */
    public boolean write(File f) {

        boolean result = false;

        AssignmentPanel ap = getAssignmentPanel(getSelectedRemote());
        if (ap != null) {

            Function[] array = ap.getFunctions();
            if ((array != null) && (array.length > 0)) {

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < array.length; i++) {

                    sb.append("begin\n");
                    sb.append("\tbutton = " + array[i].getSelected() + "\n");
                    sb.append("\tconfig = " + array[i].getName() + "\n");
                    sb.append("end\n\n");
                }

                try {

                    Util.writeTextFile(f, sb.toString());
                    result = true;

                } catch (IOException ex) {

                    result = false;
                }
            }
        }

        return (result);
    }

}

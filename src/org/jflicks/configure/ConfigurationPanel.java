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
package org.jflicks.configure;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jflicks.util.Util;
import org.jflicks.wizard.WizardDisplayer;

/**
 * This class displays the summary details of the current configuration
 * of some component.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ConfigurationPanel extends JPanel implements ActionListener {

    private Configuration configuration;
    private JTextField nameTextField;
    private JTextField sourceTextField;
    private JTextArea summaryTextArea;
    private JButton configureButton;

    /**
     * Simple constructor with required Configuration argument.
     *
     * @param c A given Configuration instance.
     */
    public ConfigurationPanel(Configuration c) {

        JTextField ntf = new JTextField(20);
        ntf.setEditable(false);
        setNameTextField(ntf);

        JTextField stf = new JTextField(20);
        stf.setEditable(false);
        setSourceTextField(stf);

        JTextArea sta = new JTextArea(8, 40);
        sta.setEditable(false);
        setSummaryTextArea(sta);

        JScrollPane scroller = new JScrollPane(sta,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JButton b = new JButton("Configure");
        b.addActionListener(this);
        setConfigureButton(b);

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(new JLabel("Name"), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(ntf, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(new JLabel("Source"), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(stf, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(new JLabel("Summary"), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(scroller, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(new JSeparator(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(b, gbc);

        setConfiguration(c);
    }

    /**
     * This panel needs a Configuration instance to edit.
     *
     * @return The Configuration instance.
     */
    public Configuration getConfiguration() {
        return (configuration);
    }

    /**
     * This panel needs a Configuration instance to edit.
     *
     * @param c The Configuration instance.
     */
    public void setConfiguration(Configuration c) {

        Configuration old = configuration;
        configuration = c;

        if (configuration != null) {

            JTextField tf = getNameTextField();
            tf.setText(c.getName());
            tf = getSourceTextField();
            tf.setText(c.getSource());
            JTextArea ta = getSummaryTextArea();
            ta.setText(c.getSummary());

        } else {

            JTextField tf = getNameTextField();
            tf.setText("Unknown");
            tf = getSourceTextField();
            tf.setText("Unknown");
            JTextArea ta = getSummaryTextArea();
            ta.setText("Unknown");
        }

        firePropertyChange("Configuration", old, configuration);
    }

    private JTextField getNameTextField() {
        return (nameTextField);
    }

    private void setNameTextField(JTextField tf) {
        nameTextField = tf;
    }

    private JTextField getSourceTextField() {
        return (sourceTextField);
    }

    private void setSourceTextField(JTextField tf) {
        sourceTextField = tf;
    }

    private JTextArea getSummaryTextArea() {
        return (summaryTextArea);
    }

    private void setSummaryTextArea(JTextArea ta) {
        summaryTextArea = ta;
    }

    private JButton getConfigureButton() {
        return (configureButton);
    }

    private void setConfigureButton(JButton b) {
        configureButton = b;
    }

    /**
     * Launch the wizard to allow editing of the Configuration.
     *
     * @param event The given ActionEvent instance.
     */
    public void actionPerformed(ActionEvent event) {

        Configuration c = getConfiguration();
        if (c != null) {

            ConfigurationPanelProvider cpp = new ConfigurationPanelProvider(c);
            Map map = WizardDisplayer.showWizard(Util.findFrame(this),
                cpp.createWizard());
            if (map != null) {

                Configuration newc = (Configuration) map.get("Configuration");
                if (newc != null) {

                    setConfiguration(newc);
                }
            }
        }
    }

}


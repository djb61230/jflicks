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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Main panel that allows the user to run Analyze instances against a
 * jflicks installation.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class AssignmentPanel extends JPanel implements ActionListener,
    ItemListener, ListSelectionListener {

    private static final String NOT_SET = "Not Set";

    private Function[] functions;
    private JList functionList;
    private JTextField nameTextField;
    private JTextArea descTextArea;
    private JComboBox choiceComboBox;

    /**
     * Simple constructor with one argument.
     *
     * @param r A given Remote instance.
     */
    public AssignmentPanel(Remote r) {

        Function[] array = Function.getFunctions();

        String[] buts = null;
        if ((r != null) && (array != null)) {

            buts = r.getButtons();
            if (buts != null) {

                for (int i = 0; i < array.length; i++) {

                    array[i].setChoices(buts);
                }
            }
        }

        setFunctions(array);
        JList flist = new JList(array);
        flist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flist.addListSelectionListener(this);
        setFunctionList(flist);

        JScrollPane flistScroller = new JScrollPane(flist,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JTextField nametf = new JTextField(20);
        nametf.setEditable(false);
        setNameTextField(nametf);

        JTextArea descta = new JTextArea(8, 20);
        descta.setEditable(false);
        descta.setLineWrap(true);
        descta.setWrapStyleWord(true);
        setDescTextArea(descta);

        JScrollPane descScroller = new JScrollPane(descta,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JComboBox cb = new JComboBox();
        cb.addItem(NOT_SET);
        if (buts != null) {

            for (int i = 0; i < buts.length; i++) {

                cb.addItem(buts[i]);
            }
        }
        cb.addItemListener(this);
        setChoiceComboBox(cb);

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(flistScroller, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(nametf, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(descScroller, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(cb, gbc);
    }

    private JList getFunctionList() {
        return (functionList);
    }

    private void setFunctionList(JList l) {
        functionList = l;
    }

    private JTextField getNameTextField() {
        return (nameTextField);
    }

    private void setNameTextField(JTextField tf) {
        nameTextField = tf;
    }

    private JTextArea getDescTextArea() {
        return (descTextArea);
    }

    private void setDescTextArea(JTextArea ta) {
        descTextArea = ta;
    }

    private JComboBox getChoiceComboBox() {
        return (choiceComboBox);
    }

    private void setChoiceComboBox(JComboBox cb) {
        choiceComboBox = cb;
    }

    /**
     * This will return all the functions.  Using these a config
     * file could be generated.
     *
     * @return An array of Function instances.
     */
    public Function[] getFunctions() {

        Function[] result = null;

        if (functions != null) {

            result = Arrays.copyOf(functions, functions.length);
        }

        return (result);
    }

    private void setFunctions(Function[] array) {

        if (array != null) {
            functions = Arrays.copyOf(array, array.length);
        } else {
            functions = null;
        }
    }

    private Function getFunction() {

        Function result = null;

        JList l = getFunctionList();
        int index = l.getSelectedIndex();
        if (index != -1) {

            result = (Function) l.getSelectedValue();
        }

        return (result);
    }

    private void choiceAction() {

        Function f = getFunction();
        JComboBox cb = getChoiceComboBox();
        if ((f != null) && (cb != null)) {

            f.setIndex(cb.getSelectedIndex() - 1);
        }
    }

    /**
     * We listen for events from the combo box.
     *
     * @param event A given ActionEvent instance.
     */
    public void actionPerformed(ActionEvent event) {

        if (event.getSource() == getChoiceComboBox()) {
            choiceAction();
        }
    }

    /**
     * We listen for events from the combo box.
     *
     * @param event A given ActionEvent instance.
     */
    public void itemStateChanged(ItemEvent event) {

        if (event.getSource() == getChoiceComboBox()) {

            if (event.getStateChange() == ItemEvent.SELECTED) {

                choiceAction();
            }
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

            if (event.getSource() == getFunctionList()) {

                Function f = getFunction();
                JTextField tf = getNameTextField();
                JTextArea ta = getDescTextArea();
                JComboBox cb = getChoiceComboBox();
                if ((f != null) && (tf != null) && (ta != null)
                    && (cb != null)) {

                    tf.setText(f.getName());
                    ta.setText(f.getDescription());
                    cb.setSelectedIndex(f.getIndex() + 1);
                }
            }
        }
    }

}

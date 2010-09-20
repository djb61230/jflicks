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
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * User interface to edit or create a list of items.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ListTypePanel extends BaseTypePanel implements ActionListener,
    ListSelectionListener {

    private JTextField entryTextField;
    private JButton addButton;
    private JButton deleteButton;
    private JButton modifyButton;
    private JList list;

    /**
     * Constructor with required NameValue argument.
     *
     * @param nv The NameValue instance.
     */
    public ListTypePanel(NameValue nv) {

        super(nv);
        if (nv != null) {

            setEntryTextField(new JTextField(24));

            setAddButton(new JButton("Add"));
            getAddButton().addActionListener(this);
            setDeleteButton(new JButton("Delete"));
            getDeleteButton().addActionListener(this);
            setModifyButton(new JButton("Modify"));
            getModifyButton().addActionListener(this);

            JList lst = null;
            String[] values = nv.valueToArray();
            if (values != null) {

                lst = new JList(values);

            } else {

                lst = new JList();
            }

            lst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            lst.setVisibleRowCount(6);
            lst.addListSelectionListener(this);
            setList(lst);
            JScrollPane listScroller = new JScrollPane(lst,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.weightx = 0.0;
            gbc.weighty = 0.25;
            gbc.insets = new Insets(4, 4, 4, 4);

            add(new JLabel(nv.getName()), gbc);

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

            add(getEntryTextField(), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.weightx = 0.0;
            gbc.weighty = 0.25;
            gbc.insets = new Insets(4, 4, 4, 4);

            add(getAddButton(), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.weightx = 0.0;
            gbc.weighty = 0.25;
            gbc.insets = new Insets(4, 4, 4, 4);

            add(getDeleteButton(), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.weightx = 0.0;
            gbc.weighty = 0.25;
            gbc.insets = new Insets(4, 4, 4, 4);

            add(getModifyButton(), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            gbc.gridheight = 3;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.insets = new Insets(4, 4, 4, 4);

            add(listScroller, gbc);
        }
    }

    private JTextField getEntryTextField() {
        return (entryTextField);
    }

    private void setEntryTextField(JTextField tf) {
        entryTextField = tf;
    }

    private JButton getAddButton() {
        return (addButton);
    }

    private void setAddButton(JButton b) {
        addButton = b;
    }

    private JButton getDeleteButton() {
        return (deleteButton);
    }

    private void setDeleteButton(JButton b) {
        deleteButton = b;
    }

    private JButton getModifyButton() {
        return (modifyButton);
    }

    private void setModifyButton(JButton b) {
        modifyButton = b;
    }

    private JList getList() {
        return (list);
    }

    private void setList(JList l) {
        list = l;
    }

    /**
     * Listen for clicks on the buttons.
     *
     * @param e The given ActionEvent instance.
     */
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == getAddButton()) {
            addAction();
        } else if (e.getSource() == getDeleteButton()) {
            deleteAction();
        } else if (e.getSource() == getModifyButton()) {
            modifyAction();
        }
    }

    /**
     * Listen for clicks on the list of items.
     *
     * @param e The given ListSelectionEvent instance.
     */
    public void valueChanged(ListSelectionEvent e) {

        if (!e.getValueIsAdjusting()) {

            int index = list.getSelectedIndex();
            if (index != -1) {

                String[] array = (String[]) getValue();
                if (index < array.length) {
                    apply(array[index]);
                }
            }
        }
    }

    private void addAction() {

        String text = from();
        if ((text != null) && (!contains(text))) {

            String[] array = (String[]) getValue();
            if (array != null) {

                String[] newarray = new String[array.length + 1];
                for (int i = 0; i < array.length; i++) {

                    newarray[i] = array[i];
                }
                newarray[array.length] = text;
                array = newarray;

            } else {

                array = new String[1];
                array[0] = text;
            }

            setValue(array);
            doListSelection(array.length - 1);
        }
    }

    private void deleteAction() {

        String[] array = (String[]) getValue();
        JList l = getList();
        if ((array != null) && (l != null)) {

            int index = l.getSelectedIndex();
            if ((index >= 0) && (index < array.length)) {

                String[] newarray = new String[array.length - 1];
                int oldindex = 0;
                for (int i = 0; i < newarray.length; i++) {

                    if (oldindex == index) {
                        oldindex++;
                    }
                    newarray[i] = array[oldindex++];
                }
                setValue(newarray);
            }

            doListSelection(index);
        }
    }

    private void modifyAction() {

        String[] array = (String[]) getValue();
        JList l = getList();
        if ((array != null) && (l != null)) {

            int index = l.getSelectedIndex();
            if ((index >= 0) && (index < array.length)) {

                array[index] = from();
            }

            setValue(array);
            doListSelection(index);
        }
    }

    private String[] getValue() {

        String[] result = null;

        JList l = getList();
        if (l != null) {

            ListModel model = l.getModel();
            if (model.getSize() > 0) {

                result = new String[model.getSize()];
                for (int i = 0; i < model.getSize(); i++) {
                    result[i] = (String) model.getElementAt(i);
                }
            }
        }

        return (result);
    }

    private void setValue(String[] array) {

        JList l = getList();
        if (l != null) {

            if (array != null) {

                l.setListData(array);

            } else {

                l.setListData(new Vector());
                apply("");
            }
        }
    }

    private void doListSelection(int index) {

        JList l = getList();
        if (l != null) {

            ListModel lm = l.getModel();
            while (index >= 0) {

                if (lm.getSize() > index) {

                    l.setSelectedIndex(index);
                    index = 0;
                }
                index--;
            }
        }
    }

    private void apply(String s) {

        JTextField tf = getEntryTextField();
        if ((s != null) && (tf != null)) {

            tf.setText(s);
        }
    }

    private String from() {

        String result = null;

        JTextField tf = getEntryTextField();
        if (tf != null) {

            result = tf.getText();
            if (result != null) {

                result = result.trim();
                if (result.length() == 0) {
                    result = null;
                }
            }
        }

        return (result);
    }

    private boolean contains(String s) {

        boolean result = false;
        String[] array = (String[]) getValue();
        if ((array != null) && (s != null)) {

            for (int i = 0; i < array.length; i++) {

                if (s.equals(array[i])) {

                    result = true;
                    i = array.length;
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public String getEditedValue() {

        String result = null;

        String[] array = getValue();
        if (array != null) {

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; i++) {

                sb.append(array[i]);
                if ((i + 1) < array.length) {

                    sb.append("|");
                }
            }

            result = sb.toString();
        }

        return (result);
    }

}


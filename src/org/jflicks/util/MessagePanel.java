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
package org.jflicks.util;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * This class is used to create a Message type Panel.  This is great to use
 * as a log message area to convey status to the user.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class MessagePanel extends JPanel {

    private JTextArea textArea;

    /**
     * Default constructor that takes one required argument.
     *
     * @param title The title of the panel, this will be a text border.  Null
     * will result in an etched border.
     */
    public MessagePanel(String title) {
        this(title, 10, 40);
    }

    /**
     * Constructor with title, row size and column size.
     *
     * @param title The title of the panel, this will be a text border.  Null
     * will result in an etched border.
     * @param rows Row size;
     * @param cols column size.
     */
    public MessagePanel(String title, int rows, int cols) {

        if (title != null) {
            setBorder(BorderFactory.createTitledBorder(title));
        } else {
            setBorder(BorderFactory.createEtchedBorder());
        }
        setTextArea(new JTextArea(rows, cols));
        getTextArea().setEditable(false);
        JScrollPane scrollPane = new JScrollPane(getTextArea(),
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(scrollPane, gbc);
    }

    private JTextArea getTextArea() {
        return (textArea);
    }

    private void setTextArea(JTextArea ta) {
        textArea = ta;
    }

    /**
     * Add the string to the text area.
     *
     * @param s String to add.
     */
    public void addMessage(String s) {

        JTextArea ta = getTextArea();
        if (ta != null) {

            synchronized (ta) {

                ta.append(s);
                ta.append("\n");
            }
            processScroll();
        }
    }

    /**
     * Clear the text area of any old messages.
     *
     */

    public void clearMessage() {

        JTextArea ta = getTextArea();
        if (ta != null) {

            ta.setText("");
            processScroll();
        }
    }

    private void processScroll() {

        JTextArea ta = getTextArea();
        if (ta != null) {
            ta.setCaretPosition(ta.getDocument().getLength());
        }
    }

}

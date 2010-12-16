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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.jflicks.ui.view.aspirin.analyze.Finding;
import org.jflicks.util.PromptPanel;

/**
 * Panel that deals with displaying an Finding instance.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class FindingPanel extends JPanel {

    private Finding finding;
    private JTextField titleTextField;
    private JTextArea descriptionTextArea;
    private JTextField statusTextField;
    private JButton fixButton;

    /**
     * Simple constructor.
     */
    public FindingPanel() {

        JTextField titletf = new JTextField(20);
        titletf.setEditable(false);
        setTitleTextField(titletf);

        JTextArea descta = new JTextArea(6, 20);
        descta.setEditable(false);
        descta.setLineWrap(true);
        descta.setWrapStyleWord(true);
        setDescriptionTextArea(descta);

        JScrollPane descScroller = new JScrollPane(descta,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JTextField statustf = new JTextField(20);
        statustf.setEditable(false);
        setStatusTextField(statustf);

        JButton button = new JButton("Fix");
        setFixButton(button);

        String[] prompts = {
            "Title", "Description", "Status", ""
        };

        JComponent[] comps = {
            titletf, descScroller, statustf, button
        };

        PromptPanel pp = new PromptPanel(prompts, comps);

        setLayout(new BorderLayout());
        add(pp, BorderLayout.CENTER);
    }

    /**
     * All UI components show data from a Finding instance.
     *
     * @return A Finding object.
     */
    public Finding getFinding() {
        return (finding);
    }

    /**
     * All UI components show data from a Finding instance.
     *
     * @param f A Finding object.
     */
    public void setFinding(Finding f) {
        finding = f;

        if (f != null) {

            apply(getTitleTextField(), f.getTitle());
            apply(getDescriptionTextArea(), f.getDescription());
            if (f.isPassed()) {
                apply(getStatusTextField(), "Passed");
            } else {
                apply(getStatusTextField(), "Failed");
            }
            getFixButton().setEnabled(f.getFix() != null);

        } else {

            apply(getTitleTextField(), null);
            apply(getDescriptionTextArea(), null);
            apply(getStatusTextField(), null);
            getFixButton().setEnabled(false);
        }
    }

    private JTextField getTitleTextField() {
        return (titleTextField);
    }

    private void setTitleTextField(JTextField tf) {
        titleTextField = tf;
    }

    private JTextArea getDescriptionTextArea() {
        return (descriptionTextArea);
    }

    private void setDescriptionTextArea(JTextArea ta) {
        descriptionTextArea = ta;
    }

    private JTextField getStatusTextField() {
        return (statusTextField);
    }

    private void setStatusTextField(JTextField tf) {
        statusTextField = tf;
    }

    private JButton getFixButton() {
        return (fixButton);
    }

    private void setFixButton(JButton b) {
        fixButton = b;
    }

    private void apply(JTextComponent c, String s) {

        if (c != null) {

            if (s != null) {

                c.setText(s.trim());

            } else {

                c.setText("");
            }
        }
    }

}

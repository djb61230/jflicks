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
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.jflicks.ui.view.aspirin.analyze.Analyze;
import org.jflicks.util.PromptPanel;

/**
 * Panel that deals with displaying an Analyze instance.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class AnalyzePanel extends JPanel {

    private Analyze analyze;
    private JTextField shortTextField;
    private JTextArea longTextArea;
    private JList bundleList;

    /**
     * Simple constructor.
     */
    public AnalyzePanel() {

        JTextField shorttf = new JTextField(20);
        shorttf.setEditable(false);
        setShortTextField(shorttf);

        JTextArea longta = new JTextArea(8, 20);
        longta.setEditable(false);
        longta.setLineWrap(true);
        longta.setWrapStyleWord(true);
        setLongTextArea(longta);

        JScrollPane longScroller = new JScrollPane(longta,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JList list = new JList();
        setBundleList(list);

        JScrollPane bundlelistScroller = new JScrollPane(list,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        String[] prompts = {
            "Name", "Description", "Bundles"
        };

        JComponent[] comps = {
            shorttf, longScroller, bundlelistScroller
        };

        double[] yweights = {
            0.0, 0.5, 0.5
        };

        PromptPanel pp = new PromptPanel(prompts, comps, yweights);

        setLayout(new BorderLayout());
        add(pp, BorderLayout.CENTER);
    }

    /**
     * All UI components show data from a Analyze instance.
     *
     * @return A Analyze object.
     */
    public Analyze getAnalyze() {
        return (analyze);
    }

    /**
     * All UI components show data from a Analyze instance.
     *
     * @param a A Analyze object.
     */
    public void setAnalyze(Analyze a) {
        analyze = a;

        if (a != null) {

            apply(getShortTextField(), a.getShortDescription());
            apply(getLongTextArea(), a.getLongDescription());
            apply(getBundleList(), a.getBundles());

        } else {

            apply(getShortTextField(), null);
            apply(getLongTextArea(), null);
            apply(getBundleList(), null);
        }
    }

    private JTextField getShortTextField() {
        return (shortTextField);
    }

    private void setShortTextField(JTextField tf) {
        shortTextField = tf;
    }

    private JTextArea getLongTextArea() {
        return (longTextArea);
    }

    private void setLongTextArea(JTextArea ta) {
        longTextArea = ta;
    }

    private JList getBundleList() {
        return (bundleList);
    }

    private void setBundleList(JList l) {
        bundleList = l;
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

    private void apply(JList l, Object[] array) {

        if (l != null) {

            if (array != null) {

                l.setListData(array);

            } else {

                String[] tmp = new String[0];
                l.setListData(tmp);
            }
        }
    }

}

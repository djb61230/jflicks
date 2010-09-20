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
package org.jflicks.ui.view.fe;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jflicks.util.Util;

/**
 * This class is a undecorated dialog window which gives a better look
 * for a frontend UI at the TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class Dialog extends JDialog {

    private Dialog(Frame parent, JComponent c) {

        super(parent, "", true);
        setUndecorated(true);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(c, BorderLayout.CENTER);
        pack();

        Cursor cursor = Util.getNoCursor();
        if (cursor != null) {

            getContentPane().setCursor(cursor);
        }
    }

    /**
     * Display a ButtonPanel that gives the user a set of buttons to
     * do as an action.
     *
     * @param parent The parent frame to center in.
     * @param bp The given ButtonPanel instance.
     */
    public static void showButtonPanel(Frame parent, ButtonPanel bp) {

        final Dialog dialog = new Dialog(parent, bp);
        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        bp.addActionListener(
            new ActionListener() {

                public void actionPerformed(ActionEvent event) {

                    dialog.setVisible(false);
                    dialog.dispose();
                }
            }
        );

        dialog.setVisible(true);
    }

    /**
     * Display a JPanel and a ButtonPanel that gives the user a set of
     * buttons to do as an action.
     *
     * @param parent The parent frame to center in.
     * @param p The given JPanel instance.
     * @param bp The given ButtonPanel instance.
     */
    public static void showButtonPanel(Frame parent, JPanel p, ButtonPanel bp) {

        JPanel all = new JPanel(new BorderLayout());
        all.add(p, BorderLayout.CENTER);
        if (bp.isVertical()) {
            all.add(bp, BorderLayout.EAST);
        } else {
            all.add(bp, BorderLayout.SOUTH);
        }
        final Dialog dialog = new Dialog(parent, all);
        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        bp.addActionListener(
            new ActionListener() {

                public void actionPerformed(ActionEvent event) {

                    dialog.setVisible(false);
                    dialog.dispose();
                }
            }
        );

        dialog.setVisible(true);
    }

    /**
     * Display a JPanel in a dialog.
     *
     * @param parent The parent frame to center in.
     * @param p The given JPanel instance.
     * @param buts A set of buttons that we listen and dismiss after getting
     * any sort of action performed on them.
     */
    public static void showPanel(Frame parent, JPanel p, JButton ... buts) {

        final Dialog dialog = new Dialog(parent, p);
        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        if (buts != null) {

            for (int i = 0; i < buts.length; i++) {

                buts[i].addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent event) {

                            dialog.setVisible(false);
                            dialog.dispose();
                        }
                    }
                );
            }
        }

        dialog.setVisible(true);
    }

}

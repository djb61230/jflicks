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
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

/**
 * This will display the current time updating itself.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ClockPanel extends JXPanel implements ActionListener {

    public static final double FUDGE = 60.0;

    private SimpleDateFormat simpleDateFormat;
    private JXLabel label;

    /**
     * A constructor with are required arguments.
     *
     * @param font The font to use.
     * @param color The text color.
     * @param back The background color.
     * @param alpha The desired alpha value.
     */
    public ClockPanel(Font font, Color color, Color back, float alpha) {

        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        setAlpha(alpha);
        SimpleDateFormat sdf = new SimpleDateFormat("E M-d h:mm a");
        setSimpleDateFormat(sdf);

        JXLabel l = new JXLabel();
        l.setFont(font);
        l.setForeground(color);
        l.setBackground(back);
        l.setHorizontalTextPosition(SwingConstants.CENTER);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        setLabel(l);

        setBackground(back);
        setLayout(new BorderLayout());
        add(l, BorderLayout.CENTER);

        updateTime();

        Timer t = new Timer(10 * 1000, this);
        t.setRepeats(true);
        t.start();
    }

    private SimpleDateFormat getSimpleDateFormat() {
        return (simpleDateFormat);
    }

    private void setSimpleDateFormat(SimpleDateFormat sdf) {
        simpleDateFormat = sdf;
    }

    private JXLabel getLabel() {
        return (label);
    }

    private void setLabel(JXLabel l) {
        label = l;
    }

    private void updateTime() {

        JXLabel l = getLabel();
        SimpleDateFormat sdf = getSimpleDateFormat();
        if ((l != null) && (sdf != null)) {

            l.setText(sdf.format(System.currentTimeMillis()));
        }
    }

    /**
     * We listen for timer events so we can update our display.
     *
     * @param event A given ActionEvent instance.
     */
    public void actionPerformed(ActionEvent event) {

        updateTime();
    }

}

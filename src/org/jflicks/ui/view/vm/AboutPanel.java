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
package org.jflicks.ui.view.vm;

import java.io.IOException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JLayeredPane;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.TextPainter;

/**
 * This class simply displays information about this program.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class AboutPanel extends JLayeredPane {

    /**
     * Simple Empty constructor.
     */
    public AboutPanel() {

        setPreferredSize(new Dimension(400, 267));
        JXPanel p = new JXPanel();
        p.setBounds(0, 0, 400, 267);
        try {

            BufferedImage bi =
                ImageIO.read(getClass().getResource("aboutbg.png"));
            ImagePainter painter = new ImagePainter(bi);
            p.setBackgroundPainter(painter);
            p.setAlpha(0.4f);

        } catch (IOException ex) {

            throw new RuntimeException(ex);
        }

        JXLabel title1 = new JXLabel();
        Font font = title1.getFont();
        String text1 = "jflicks media system";
        title1.setText(text1);
        title1.setForeground(Color.BLACK);
        TextPainter tpainter =
            new TextPainter(text1, font.deriveFont(font.getSize2D() + 10.0f));
        title1.setForegroundPainter(tpainter);
        title1.setBounds(0, 50, 400, 30);

        JXLabel title2 = new JXLabel();
        String text2 = "Video Manager";
        title2.setText(text2);
        title2.setForeground(Color.BLACK);
        tpainter =
            new TextPainter(text2, font.deriveFont(font.getSize2D() + 10.0f));
        title2.setForegroundPainter(tpainter);
        title2.setBounds(0, 80, 400, 30);

        JXLabel version = new JXLabel();
        String textversion = "Version 1.0";
        version.setText(textversion);
        version.setForeground(Color.BLACK);
        tpainter =
            new TextPainter(textversion,
            font.deriveFont(font.getSize2D() + 8.0f));
        version.setForegroundPainter(tpainter);
        version.setBounds(0, 130, 400, 30);

        JXLabel copy = new JXLabel();
        String textcopy = "Copyright 2011 Doug Barnum";
        copy.setText(textcopy);
        copy.setForeground(Color.BLACK);
        tpainter =
            new TextPainter(textcopy,
            font.deriveFont(font.getSize2D() + 2.0f));
        copy.setForegroundPainter(tpainter);
        copy.setBounds(0, 235, 400, 30);

        add(p, Integer.valueOf(100));
        add(title1, Integer.valueOf(110));
        add(title2, Integer.valueOf(110));
        add(version, Integer.valueOf(110));
        add(copy, Integer.valueOf(110));
    }

}

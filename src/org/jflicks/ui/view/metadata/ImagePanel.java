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
package org.jflicks.ui.view.metadata;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.jflicks.nms.Media;
import org.jflicks.util.ColumnPanel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.ImagePainter;

/**
 * Be able to select of of our Metadata suppliers.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ImagePanel extends JPanel {

    /**
     * Constructor with one required argument.
     *
     * @param m A Media instance.
     */
    public ImagePanel(Media m) {

        BufferedImage banner = null;
        BufferedImage fanart = null;
        BufferedImage poster = null;
        if (m != null) {

            try {
                banner = ImageIO.read(new URL(m.getBannerURL()));
            } catch (IOException ex) {
            }

            try {
                fanart = ImageIO.read(new URL(m.getFanartURL()));
            } catch (IOException ex) {
            }

            try {
                poster = ImageIO.read(new URL(m.getPosterURL()));
            } catch (IOException ex) {
            }
        }

        if (banner == null) {

            try {
                banner =
                    ImageIO.read(getClass().getResource("missing_banner.png"));
            } catch (IOException ex) {
            }
        }

        if (fanart == null) {

            try {
                fanart =
                    ImageIO.read(getClass().getResource("missing_fanart.png"));
            } catch (IOException ex) {
            }
        }

        if (poster == null) {

            try {
                poster =
                    ImageIO.read(getClass().getResource("missing_poster.png"));
            } catch (IOException ex) {
            }
        }

        JXPanel bannerPanel = new JXPanel();
        ImagePainter painter = new ImagePainter(banner);
        painter.setScaleToFit(true);
        bannerPanel.setBackgroundPainter(painter);
        if (banner != null) {

            bannerPanel.setPreferredSize(new Dimension(banner.getWidth(),
                banner.getHeight()));
        }

        JXPanel fanartPanel = new JXPanel();
        painter = new ImagePainter(fanart);
        painter.setScaleToFit(true);
        fanartPanel.setBackgroundPainter(painter);
        if (fanart != null) {

            fanartPanel.setPreferredSize(new Dimension(fanart.getWidth(),
                fanart.getHeight()));
        }

        JXPanel posterPanel = new JXPanel();
        painter = new ImagePainter(poster);
        painter.setScaleToFit(true);
        posterPanel.setBackgroundPainter(painter);
        if (poster != null) {

            posterPanel.setPreferredSize(new Dimension(poster.getWidth(),
                poster.getHeight()));
        }

        ColumnPanel cp = new ColumnPanel(bannerPanel, fanartPanel, posterPanel);

        setLayout(new BorderLayout());
        add(cp, BorderLayout.CENTER);
    }

}

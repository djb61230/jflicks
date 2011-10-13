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

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXLabel;
import org.jflicks.nms.Video;
import org.jflicks.util.Util;

/**
 * Implements a View so a user can control the metadata of videos.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ThumbnailPanel extends JPanel {

    private static final int POSTER_THUMB_WIDTH = 68;
    private static final int POSTER_THUMB_HEIGHT = 100;
    private static final int FANART_THUMB_WIDTH = 178;
    private static final int FANART_THUMB_HEIGHT = 100;

    private Video video;
    private JXLabel posterLabel;
    private JXLabel fanartLabel;
    private ImageIcon missingPosterImageIcon;
    private ImageIcon missingFanartImageIcon;

    /**
     * Default constructor.
     */
    public ThumbnailPanel() {

        BufferedImage posterbi = null;
        BufferedImage fanartbi = null;
        try {
            posterbi =
               ImageIO.read(getClass().getResource("missing_poster_thumb.png"));
        } catch (IOException ex) {
        }

        try {
            fanartbi =
               ImageIO.read(getClass().getResource("missing_fanart_thumb.png"));
        } catch (IOException ex) {
        }

        ImageIcon pii = null;
        ImageIcon fii = null;
        if (posterbi != null) {
            pii = new ImageIcon(posterbi);
        }
        if (fanartbi != null) {
            fii = new ImageIcon(fanartbi);
        }

        setMissingPosterImageIcon(pii);
        setMissingFanartImageIcon(fii);

        JXLabel poster = new JXLabel(pii);
        setPosterLabel(poster);

        JXLabel fanart = new JXLabel(fii);
        setFanartLabel(fanart);

        JPanel labelPanel = new JPanel();
        labelPanel.add(poster);
        labelPanel.add(fanart);

        setLayout(new BorderLayout());

        add(labelPanel);
    }

    /**
     * The thumbnails from this Video instance are displayed.
     *
     * @return A Video instance.
     */
    public Video getVideo() {
        return (video);
    }

    /**
     * The thumbnails from this Video instance are displayed.
     *
     * @param v A Video instance.
     */
    public void setVideo(Video v) {
        video = v;
        updateVideo(v);
    }

    private JXLabel getPosterLabel() {
        return (posterLabel);
    }

    private void setPosterLabel(JXLabel l) {
        posterLabel = l;
    }

    private JXLabel getFanartLabel() {
        return (fanartLabel);
    }

    private void setFanartLabel(JXLabel l) {
        fanartLabel = l;
    }

    private ImageIcon getMissingPosterImageIcon() {
        return (missingPosterImageIcon);
    }

    private void setMissingPosterImageIcon(ImageIcon ii) {
        missingPosterImageIcon = ii;
    }

    private ImageIcon getMissingFanartImageIcon() {
        return (missingFanartImageIcon);
    }

    private void setMissingFanartImageIcon(ImageIcon ii) {
        missingFanartImageIcon = ii;
    }

    private ImageIcon getImageIcon(String url, int w, int h, ImageIcon def) {

        ImageIcon result = def;

        if (url != null) {

            try {

                BufferedImage bi = ImageIO.read(new URL(url));
                if (bi != null) {

                    bi = Util.resize(bi, w, h);
                    result = new ImageIcon(bi);
                }

            } catch (IOException ex) {
            }
        }

        return (result);
    }

    private void updateVideo(Video v) {

        if (v != null) {

            getPosterLabel().setIcon(getImageIcon(v.getPosterURL(),
                POSTER_THUMB_WIDTH, POSTER_THUMB_HEIGHT,
                getMissingPosterImageIcon()));
            getFanartLabel().setIcon(getImageIcon(v.getFanartURL(),
                FANART_THUMB_WIDTH, FANART_THUMB_HEIGHT,
                getMissingFanartImageIcon()));

        } else {

            getPosterLabel().setIcon(getMissingPosterImageIcon());
            getFanartLabel().setIcon(getMissingFanartImageIcon());
        }
    }

}

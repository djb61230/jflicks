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

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.jdesktop.swingx.JXPanel;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class CrossFadePanel extends JXPanel {

    private BufferedImage currentBufferedImage;
    private BufferedImage fromBufferedImage;

    public CrossFadePanel() {
    }

    public BufferedImage getCurrentBufferedImage() {
        return (currentBufferedImage);
    }

    public void setCurrentBufferedImage(BufferedImage bi) {
        currentBufferedImage = bi;
    }

    public BufferedImage getFromBufferedImage() {
        return (fromBufferedImage);
    }

    public void setFromBufferedImage(BufferedImage bi) {
        fromBufferedImage = bi;
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        if (fromBufferedImage != null) {

            float a = getAlpha();
            if (a != 1.0f) {

                g2.setComposite(AlphaComposite.SrcOver.derive(1.0f - a));
                g2.drawImage(fromBufferedImage, 0, 0, null);
                g2.setComposite(AlphaComposite.SrcOver.derive(a));
                g2.drawImage(currentBufferedImage, 0, 0, null);

            } else {
                g2.drawImage(currentBufferedImage, 0, 0, null);
            }

        } else {

            g2.drawImage(currentBufferedImage, 0, 0, null);
        }
    }

}

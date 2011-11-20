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

import java.awt.Rectangle;

import org.jdesktop.core.animation.timing.Evaluator;

/**
 * An evaluator that can interpolate for the Font property.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RectangleEvaluator implements Evaluator<Rectangle> {

    private Rectangle rectangle;

    /**
     * Constructor with all required arguments.
     *
     * @param r The size when full size.
     */
    public RectangleEvaluator(Rectangle r) {

        rectangle = new Rectangle(r);
    }

    /**
     * We need to return the proper Rectangle for this moment in time for the
     * animation.
     *
     * @param one The smallest font.
     * @param two The largest font.
     * @param fraction The place in the animation.
     * @return The determined Rectangle.
     */
    public Rectangle evaluate(Rectangle r0, Rectangle r1, double fraction) {

        Rectangle result = new Rectangle(rectangle);

        if ((r0 != null) && (r1 != null)) {

            double v0 = r0.getY();
            double v1 = r1.getY();
            double v = v0 + (v1 - v0) * fraction;

            result.setRect(result.getX(), v, result.getWidth(), result.getHeight());
        }

        return (result);
    }

    public Class<Rectangle> getEvaluatorClass() {
        return (Rectangle.class);
    }

    public Rectangle[] getRectangles() {

        Rectangle[] result = null;

        if (rectangle != null) {

            Rectangle r = new Rectangle(rectangle);
            int count = (int) (r.getY() + r.getHeight());
            if (count > 0) {

                result = new Rectangle[count];
                int y = (int) (r.getHeight() * -1);
                for (int i = 0; i < result.length; i++, y++) {

                    result[i] = new Rectangle(r);
                    double d = (double) y;
                    result[i].setRect(r.getX(), d, r.getWidth(), r.getHeight());
                }
            }
        }

        return (result);
    }

}

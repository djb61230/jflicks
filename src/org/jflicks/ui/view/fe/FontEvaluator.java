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

import java.awt.Font;

import org.jdesktop.animation.timing.interpolation.Evaluator;

/**
 * An evaluator that can interpolate for the Font property.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class FontEvaluator extends Evaluator {

    private Font[] fonts;

    /**
     * Constructor with all required arguments.
     *
     * @param small The small font.
     * @param large The large font.
     * @param size The size of the small font.
     * @param max The max size.
     */
    public FontEvaluator(Font small, Font large, double size, double max) {

        int isize = (int) size;
        int imax = (int) max;
        int count = imax - isize + 1;
        fonts = new Font[count];
        fonts[0] = small;
        for (int i = 1; i < fonts.length - 1; i++) {

            fonts[i] = small.deriveFont((float) (i + isize));
        }

        fonts[fonts.length - 1] = large;
    }

    /**
     * We need to return the proper Font for this moment in time for the
     * animation.
     *
     * @param one The smallest font.
     * @param two The largest font.
     * @param fraction The place in the animation.
     * @return The determined Font.
     */
    public Font evaluate(Object one, Object two, float fraction) {

        Font result = null;

        if ((one instanceof Font) && (two instanceof Font)) {

            Font f0 = (Font) one;
            Font f1 = (Font) two;

            int factor = f1.getSize() - f0.getSize();
            float ffraction = fraction * factor;
            int ifraction = (int) ffraction;
            if (ifraction > fonts.length) {
                ifraction = fonts.length;
            }
            result = fonts[ifraction];
        }

        return (result);
    }

}

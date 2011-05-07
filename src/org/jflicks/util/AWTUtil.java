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
package org.jflicks.util;

import java.awt.Shape;
import java.awt.Window;

import com.sun.awt.AWTUtilities;

/**
 * A set of methods that help doing AWT transparency stuff.  At this point
 * this just echoes to the com.sun.awt.AWTUtilities class.  Figure it
 * can be one place that uses the private class and we can fix it for
 * Java 7.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class AWTUtil {

    private AWTUtil() {
    }

    /**
     * See if even transparent top-level windows are supported.
     *
     * @return True if the graphics supports translucency.
     */
    public static boolean isTranslucentSupported() {

        return (AWTUtilities.isTranslucencySupported(
            AWTUtilities.Translucency.TRANSLUCENT));
    }

    /**
     * Set the window opaque property.
     *
     * @param w A given Window instance.
     * @param b True or false.
     */
    public static void setWindowOpaque(Window w, boolean b) {

        AWTUtilities.setWindowOpaque(w, b);
    }

    /**
     * Set the window opacity by supplying a float value.
     *
     * @param w A given Window instance.
     * @param f The value of opacity (0.0 - 1.0).
     */
    public static void setWindowOpacity(Window w, float f) {

        AWTUtilities.setWindowOpacity(w, f);
    }

    /**
     * Set the window shape by supplying a Shape object.
     *
     * @param w A given Window instance.
     * @param s The Shape to use.
     */
    public static void setWindowShape(Window w, Shape s) {

        AWTUtilities.setWindowShape(w, s);
    }

}

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
import java.awt.Component;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class HeightTimingTarget extends TimingTargetAdapter {

    private Component component;
    private int max;
    private boolean reversed;

    public HeightTimingTarget(Component c, int height) {

        setComponent(c);
        setMax(height);
        setReversed(false);
    }

    private Component getComponent() {
        return (component);
    }

    private void setComponent(Component c) {
        component = c;
    }

    private int getMax() {
        return (max);
    }

    private void setMax(int i) {
        max = i;
    }

    private boolean isReversed() {
        return (reversed);
    }

    private void setReversed(boolean b) {
        reversed = b;
    }

    public void reverse(Animator source) {

        setReversed(true);
    }

    public void end(Animator source) {

        setReversed(false);
    }

    public void timingEvent(Animator source, double fraction) {

        Component c = getComponent();
        if (c != null) {

            Rectangle r = c.getBounds();
            if (r != null) {

                double dmax = (double) getMax();
                r.height = (int) (dmax * fraction);
                c.setBounds(r);
            }
        }
    }

}


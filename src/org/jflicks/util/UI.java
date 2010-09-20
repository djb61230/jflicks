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

import javax.swing.JFrame;

import org.jflicks.mvc.Controller;
import org.jflicks.mvc.View;

/**
 * Top level user interface program that uses a View for it's main client
 * area.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class UI implements Runnable {

    private Controller controller;
    private View view;
    private JFrame viewframe;

    /**
     * Create a user interface with the given view.
     *
     * @param v The view for this user interface.
     * @param c The controller for this user interface.
     */
    public UI(View v, Controller c) {

        setView(v);
        setController(c);
    }

    private View getView() {
        return (view);
    }

    private void setView(View v) {
        view = v;
    }

    private Controller getController() {
        return (controller);
    }

    private void setController(Controller c) {
        controller = c;
    }

    /**
     * We have been told to "go away".  Clean up nicely.
     */
    public void dispose() {

        if (viewframe != null) {

            viewframe.setVisible(false);
            viewframe.dispose();
        }
    }

    /**
     * We are a Thread so here is our run method.
     */
    public void run() {

        View v = getView();
        Controller c = getController();
        if ((v != null) && (c != null)) {

            viewframe = v.getFrame();
            c.addView(v);
            viewframe.setVisible(true);
        }
    }

}

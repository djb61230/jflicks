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
package org.jflicks.ui.view.fe.screen;

import java.awt.Dimension;
import java.util.Arrays;

/**
 * This abstract class supports playing a video in a front end UI on a TV,
 * while keeping track of bookmarks.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class ExecuteScreen extends Screen {

    private String[] commands;

    /**
     * Extensions need to display some info banner over the video.
     *
     * @param command Execute one of the defined commands.
     */
    public abstract void execute(String command);

    /**
     * Simple empty constructor.
     */
    public ExecuteScreen() {
    }

    /**
     * A simple Screen that only has a set of commands.
     *
     * @return An array of String instances.
     */
    public String[] getCommands() {

        String[] result = null;

        if (commands != null) {

            result = Arrays.copyOf(commands, commands.length);
        }

        return (result);
    }

    /**
     * A simple Screen that only has a set of commands.
     *
     * @param array An array of String instances.
     */
    public void setCommands(String[] array) {

        if (array != null) {
            commands = Arrays.copyOf(array, array.length);
        } else {
            commands = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void commandReceived(String command) {
    }

    /**
     * {@inheritDoc}
     */
    public void save() {
    }

    /**
     * {@inheritDoc}
     */
    public void performControl() {
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {
    }

}


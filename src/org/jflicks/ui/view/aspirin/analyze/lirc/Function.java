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
package org.jflicks.ui.view.aspirin.analyze.lirc;

import java.util.Arrays;

import org.jflicks.rc.RC;

/**
 * A container object to have a remote function name, a
 * description, an array of possible selections and a selected
 * index into this array.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class Function {

    private String name;
    private String description;
    private String[] choices;
    private int index;

    private Function() {
    }

    private Function(String name) {

        setName(name);
        setIndex(-1);
    }

    /**
     * Convenience method to return all the Function instances defined
     * for the jflicks media system remote control commands.
     *
     * @return An array of Function instances.
     */
    public static Function[] getFunctions() {

        Function[] result = new Function[17];

        result[0] = new Function(RC.UP_COMMAND);
        result[0].setDescription("Move up in the UI.");
        result[1] = new Function(RC.DOWN_COMMAND);
        result[1].setDescription("Move down in the UI.");
        result[2] = new Function(RC.LEFT_COMMAND);
        result[2].setDescription("Move left in the UI.");
        result[3] = new Function(RC.RIGHT_COMMAND);
        result[3].setDescription("Move right in the UI.");
        result[4] = new Function(RC.ENTER_COMMAND);
        result[4].setDescription("Select the highlighted item.");
        result[5] = new Function(RC.ESCAPE_COMMAND);
        result[5].setDescription("Go back a screen or stop playing.");
        result[6] = new Function(RC.PAUSE_COMMAND);
        result[6].setDescription("Pause playing.");
        result[7] = new Function(RC.INFO_COMMAND);
        result[7].setDescription("Pop up some sort information window.");
        result[8] = new Function(RC.PAGE_UP_COMMAND);
        result[8].setDescription("Move the selection by a page going up.");
        result[9] = new Function(RC.PAGE_DOWN_COMMAND);
        result[9].setDescription("Move the selection by a page going down.");
        result[10] = new Function(RC.REWIND_COMMAND);
        result[10].setDescription("Rewind some defined seconds (default 8).");
        result[11] = new Function(RC.FORWARD_COMMAND);
        result[11].setDescription("Forward some defined seconds (default 30).");
        result[12] = new Function(RC.SKIPBACKWARD_COMMAND);
        result[12].setDescription("Go back a commercial or in a playlist. "
            + " Also move items from one list to another.");
        result[13] = new Function(RC.SKIPFORWARD_COMMAND);
        result[13].setDescription("Go forward a commercial or in a playlist"
            + " list.  Also move items from one list to another.");
        result[14] = new Function(RC.AUDIOSYNC_PLUS_COMMAND);
        result[14].setDescription("Adjust audio sync.");
        result[15] = new Function(RC.AUDIOSYNC_MINUS_COMMAND);
        result[15].setDescription("Adjust audio sync.");
        result[16] = new Function(RC.GUIDE_COMMAND);
        result[16].setDescription("Popup some sort guide UI.");

        return (result);
    }

    /**
     * The name of the remote function on jflicks.
     *
     * @return A String instance.
     */
    public String getName() {
        return (name);
    }

    /**
     * The name of the remote function on jflicks.
     *
     * @param s A String instance.
     */
    public void setName(String s) {
        name = s;
    }

    /**
     * A description of what the remote function generally does.
     *
     * @return A description of the remote function.
     */
    public String getDescription() {
        return (description);
    }

    /**
     * A description of what the remote function generally does.
     *
     * @param s A description of the remote function.
     */
    public void setDescription(String s) {
        description = s;
    }

    /**
     * The current index into the choices array that defines the selection.
     *
     * @return An int value.
     */
    public int getIndex() {
        return (index);
    }

    /**
     * The current index into the choices array that defines the selection.
     *
     * @param i An int value.
     */
    public void setIndex(int i) {
        index = i;
    }

    /**
     * Convenience method to return the currently selected choice.
     *
     * @return A String instance.
     */
    public String getSelected() {

        String result = null;

        int i = getIndex();
        if ((choices != null) && (i < choices.length)) {

            result = choices[i];
        }

        return (result);
    }

    /**
     * An array of String objects that define a set of choices.
     *
     * @return An array of String instances.
     */
    public String[] getChoices() {

        String[] result = null;

        if (choices != null) {

            result = Arrays.copyOf(choices, choices.length);
        }

        return (result);
    }

    /**
     * An array of String objects that define a set of choices.
     *
     * @param array An array of String instances.
     */
    public void setChoices(String[] array) {

        if (array != null) {
            choices = Arrays.copyOf(array, array.length);
        } else {
            choices = null;
        }
    }

    /**
     * Use the name property as the toString value.
     *
     * @return The name property.
     */
    public String toString() {
        return (getName());
    }

}


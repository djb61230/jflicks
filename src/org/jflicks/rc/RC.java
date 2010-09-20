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
package org.jflicks.rc;

import java.beans.PropertyChangeListener;

/**
 * This interface defines methods that enable the use of a remote control
 * in JFLICKS.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface RC {

    /**
     * The RC interface needs a title property.
     */
    String TITLE_PROPERTY = "RC-Title";

    /**
     * We define our standard commands so clients will know what they
     * are, here is "up".
     */
    String UP_COMMAND = "up";

    /**
     * We define our standard commands so clients will know what they
     * are, here is "down".
     */
    String DOWN_COMMAND = "down";

    /**
     * We define our standard commands so clients will know what they
     * are, here is "left".
     */
    String LEFT_COMMAND = "left";

    /**
     * We define our standard commands so clients will know what they
     * are, here is "right".
     */
    String RIGHT_COMMAND = "right";

    /**
     * We define our standard commands so clients will know what they
     * are, here is "enter".
     */
    String ENTER_COMMAND = "enter";

    /**
     * We define our standard commands so clients will know what they
     * are, here is "escape".
     */
    String ESCAPE_COMMAND = "escape";

    /**
     * The pause command is a "player" command.
     */
    String PAUSE_COMMAND = "pause";

    /**
     * The info command is a "player" command.
     */
    String INFO_COMMAND = "info";

    /**
     * The pageup command is a UI command and means to scroll up a page.
     */
    String PAGE_UP_COMMAND = "pageup";

    /**
     * The pagedown command is a UI command and means to scroll down a page.
     */
    String PAGE_DOWN_COMMAND = "pagedown";

    /**
     * The rewind command is a "player" command and means a rewind a certain
     * number of seconds.
     */
    String REWIND_COMMAND = "rewind";

    /**
     * The forward command is a "player" command and means go ahead a
     * certain number of seconds.
     */
    String FORWARD_COMMAND = "forward";

    /**
     * The skipback command is a "player" command and means go back to a
     * previous break or marker.
     */
    String SKIPBACKWARD_COMMAND = "skipbackward";

    /**
     * The skipforward command is a "player" command and means go ahead to a
     * future break or marker.
     */
    String SKIPFORWARD_COMMAND = "skipforward";

    /**
     * The audiosync command is a "player" command and will adjust sound
     * and video syncing.
     */
    String AUDIOSYNC_PLUS_COMMAND = "audiosyncplus";

    /**
     * The audiosync command is a "player" command and will adjust sound
     * and video syncing.
     */
    String AUDIOSYNC_MINUS_COMMAND = "audiosyncminus";

    /**
     * The guide command is designed to trigger some popup or action.
     */
    String GUIDE_COMMAND = "guide";

    /**
     * The title of this RC service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * The RC implementation can control the mouse if it is desired.
     *
     * @return True if the RC is currently manipulating the mouse.
     */
    boolean isMouseControl();

    /**
     * The RC implementation can control the mouse if it is desired.
     *
     * @param b True if the RC is currently manipulating the mouse.
     */
    void setMouseControl(boolean b);

    /**
     * The RC implementation can send key strokes if it is desired.
     *
     * @return True if the RC is currently sending key strokes.
     */
    boolean isKeyboardControl();

    /**
     * The RC implementation can send key strokes if it is desired.
     *
     * @param b True if the RC is currently sending key strokes.
     */
    void setKeyboardControl(boolean b);

    /**
     * The RC implementation can send OSGi events in response to the
     * remote control.  This is handy when you want a listener to receive
     * RC commands when they don't have control of a mouse or keyboard.
     *
     * @return True if event notification is in effect.
     */
    boolean isEventControl();

    /**
     * The RC implementation can send OSGi events in response to the
     * remote control.  This is handy when you want a listener to receive
     * RC commands when they don't have control of a mouse or keyboard.
     *
     * @param b True if event notification is in effect.
     */
    void setEventControl(boolean b);

    /**
     * An RC needs to keep track of property change listeners.  Here we
     * add a listener.
     *
     * @param l The listener to add to our list.
     */
    void addPropertyChangeListener(PropertyChangeListener l);

    /**
     * Add a listener.
     *
     * @param name A property name.
     * @param l A given listener.
     */
    void addPropertyChangeListener(String name, PropertyChangeListener l);

    /**
     * An RC needs to keep track of property change listeners.  Here we
     * remove a listener.
     *
     * @param l The listener to remove from our list.
     */
    void removePropertyChangeListener(PropertyChangeListener l);
}


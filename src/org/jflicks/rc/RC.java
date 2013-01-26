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
     * The pause command is a "player" command.
     */
    String MAXIMIZE_COMMAND = "maximize";

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
     * The SAP command is a "player" command and will cycle through audio
     * streams.
     */
    String SAP_COMMAND = "sap";

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
     * The default key event for up.
     *
     * @return The up key event.
     */
    int getDefaultUpKeyEvent();

    /**
     * The default key event for down.
     *
     * @return The down key event.
     */
    int getDefaultDownKeyEvent();

    /**
     * The default key event for left.
     *
     * @return The left key event.
     */
    int getDefaultLeftKeyEvent();

    /**
     * The default key event for right.
     *
     * @return The right key event.
     */
    int getDefaultRightKeyEvent();

    /**
     * The default key event for enter.
     *
     * @return The enter key event.
     */
    int getDefaultEnterKeyEvent();

    /**
     * The default key event for escape.
     *
     * @return The escape key event.
     */
    int getDefaultEscapeKeyEvent();

    /**
     * The default key event for info.
     *
     * @return The info key event.
     */
    int getDefaultInfoKeyEvent();

    /**
     * The default key event for pause.
     *
     * @return The pause key event.
     */
    int getDefaultPauseKeyEvent();

    /**
     * The default key event for maximize.
     *
     * @return The maximize key event.
     */
    int getDefaultMaximizeKeyEvent();

    /**
     * The default key event for page up.
     *
     * @return The page up key event.
     */
    int getDefaultPageUpKeyEvent();

    /**
     * The default key event for page down.
     *
     * @return The page down key event.
     */
    int getDefaultPageDownKeyEvent();

    /**
     * The default key event for rewind.
     *
     * @return The rewind key event.
     */
    int getDefaultRewindKeyEvent();

    /**
     * The default key event for forward.
     *
     * @return The forward key event.
     */
    int getDefaultForwardKeyEvent();

    /**
     * The default key event for skip backward.
     *
     * @return The skip backward key event.
     */
    int getDefaultSkipBackwardKeyEvent();

    /**
     * The default key event for skip forward.
     *
     * @return The skip forward key event.
     */
    int getDefaultSkipForwardKeyEvent();

    /**
     * The default key event for sap.
     *
     * @return The sap key event.
     */
    int getDefaultSapKeyEvent();

    /**
     * The default key event for audiosync plus.
     *
     * @return The audiosync plus key event.
     */
    int getDefaultAudiosyncPlusKeyEvent();

    /**
     * The default key event for audiosync minus.
     *
     * @return The audiosync minus key event.
     */
    int getDefaultAudiosyncMinusKeyEvent();

    /**
     * The default key event for guide.
     *
     * @return The guide key event.
     */
    int getDefaultGuideKeyEvent();

    /**
     * The current key event for up.
     *
     * @return The up key event.
     */
    int getUpKeyEvent();

    /**
     * The current key event for down.
     *
     * @return The down key event.
     */
    int getDownKeyEvent();

    /**
     * The current key event for left.
     *
     * @return The left key event.
     */
    int getLeftKeyEvent();

    /**
     * The current key event for right.
     *
     * @return The right key event.
     */
    int getRightKeyEvent();

    /**
     * The current key event for enter.
     *
     * @return The enter key event.
     */
    int getEnterKeyEvent();

    /**
     * The current key event for escape.
     *
     * @return The escape key event.
     */
    int getEscapeKeyEvent();

    /**
     * The current key event for info.
     *
     * @return The info key event.
     */
    int getInfoKeyEvent();

    /**
     * The current key event for pause.
     *
     * @return The pause key event.
     */
    int getPauseKeyEvent();

    /**
     * The current key event for maximize.
     *
     * @return The maximize key event.
     */
    int getMaximizeKeyEvent();

    /**
     * The current key event for page up.
     *
     * @return The page up key event.
     */
    int getPageUpKeyEvent();

    /**
     * The current key event for page down.
     *
     * @return The page down key event.
     */
    int getPageDownKeyEvent();

    /**
     * The current key event for rewind.
     *
     * @return The rewind key event.
     */
    int getRewindKeyEvent();

    /**
     * The current key event for forward.
     *
     * @return The forward key event.
     */
    int getForwardKeyEvent();

    /**
     * The current key event for skip backward.
     *
     * @return The skip backward key event.
     */
    int getSkipBackwardKeyEvent();

    /**
     * The current key event for skip forward.
     *
     * @return The skip forward key event.
     */
    int getSkipForwardKeyEvent();

    /**
     * The current key event for sap.
     *
     * @return The sap key event.
     */
    int getSapKeyEvent();

    /**
     * The current key event for audiosync plus.
     *
     * @return The audiosync plus key event.
     */
    int getAudiosyncPlusKeyEvent();

    /**
     * The current key event for audiosync minus.
     *
     * @return The audiosync minus key event.
     */
    int getAudiosyncMinusKeyEvent();

    /**
     * The current key event for guide.
     *
     * @return The guide key event.
     */
    int getGuideKeyEvent();

    /**
     * The current key event for up.
     *
     * @param i The up key event.
     */
    void setUpKeyEvent(int i);

    /**
     * The current key event for down.
     *
     * @param i The down key event.
     */
    void setDownKeyEvent(int i);

    /**
     * The current key event for left.
     *
     * @param i The left key event.
     */
    void setLeftKeyEvent(int i);

    /**
     * The current key event for right.
     *
     * @param i The right key event.
     */
    void setRightKeyEvent(int i);

    /**
     * The current key event for enter.
     *
     * @param i The enter key event.
     */
    void setEnterKeyEvent(int i);

    /**
     * The current key event for escape.
     *
     * @param i The escape key event.
     */
    void setEscapeKeyEvent(int i);

    /**
     * The current key event for info.
     *
     * @param i The info key event.
     */
    void setInfoKeyEvent(int i);

    /**
     * The current key event for pause.
     *
     * @param i The pause key event.
     */
    void setPauseKeyEvent(int i);

    /**
     * The current key event for maximize.
     *
     * @param i The maximize key event.
     */
    void setMaximizeKeyEvent(int i);

    /**
     * The current key event for page up.
     *
     * @param i The page up key event.
     */
    void setPageUpKeyEvent(int i);

    /**
     * The current key event for page down.
     *
     * @param i The page down key event.
     */
    void setPageDownKeyEvent(int i);

    /**
     * The current key event for rewind.
     *
     * @param i The rewind key event.
     */
    void setRewindKeyEvent(int i);

    /**
     * The current key event for forward.
     *
     * @param i The forward key event.
     */
    void setForwardKeyEvent(int i);

    /**
     * The current key event for skip backward.
     *
     * @param i The skip backward key event.
     */
    void setSkipBackwardKeyEvent(int i);

    /**
     * The current key event for skip forward.
     *
     * @param i The skip forward key event.
     */
    void setSkipForwardKeyEvent(int i);

    /**
     * The current key event for sap.
     *
     * @param i The sap key event.
     */
    void setSapKeyEvent(int i);

    /**
     * The current key event for audiosync plus.
     *
     * @param i The audiosync plus key event.
     */
    void setAudiosyncPlusKeyEvent(int i);

    /**
     * The current key event for audiosync minus.
     *
     * @param i The audiosync minus key event.
     */
    void setAudiosyncMinusKeyEvent(int i);

    /**
     * The current key event for guide.
     *
     * @param i The guide key event.
     */
    void setGuideKeyEvent(int i);

    /**
     * An RC needs to keep track of property change listeners.  Here we
     * add a listener.

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


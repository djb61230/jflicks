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

import java.awt.AWTEvent;

import org.jflicks.tv.Recording;

/**
 * We try to capture all the properties one needs to manage a Screen.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ScreenEvent extends AWTEvent {

    /**
     * Status that a Screen requests a recording deletion.
     */
    public static final int DELETE_RECORDING = 1;

    /**
     * Status that a Screen requests a recording deletion.
     */
    public static final int DELETE_RECORDING_ALLOW_RERECORDING = 2;

    /**
     * Status that a Screen requests a Recording to stop.
     */
    public static final int STOP_RECORDING = 3;

    /**
     * Status that a new Screen has come online to be added.
     */
    public static final int ADD_SCREEN = 4;

    /**
     * Status that an old Screen has gone and needs to be removed.
     */
    public static final int REMOVE_SCREEN = 5;

    private int type;
    private Recording recording;
    private Screen screen;

    /**
     * Constructor to make just a status event.
     *
     * @param source The source of the event.
     * @param type The status type.
     */
    public ScreenEvent(Screenable source, int type) {

        super(source, -1);
        setType(type);
    }

    /**
     * Constructor to make a status event with some Recording object.
     *
     * @param source The source of the event.
     * @param type The status type.
     * @param r A Recording instance.
     */
    public ScreenEvent(Screenable source, int type, Recording r) {

        this(source, type);
        setRecording(r);
    }

    /**
     * Constructor to make a status event with some Screen object.
     *
     * @param source The source of the event.
     * @param type The status type.
     * @param s A Screen instance.
     */
    public ScreenEvent(Screenable source, int type, Screen s) {

        this(source, type);
        setScreen(s);
    }

    /**
     * The type of event.
     *
     * @return The type as an int.
     */
    public int getType() {
        return (type);
    }

    /**
     * The type of event.
     *
     * @param i The type as an int.
     */
    public void setType(int i) {
        type = i;
    }

    /**
     * A Screen event handing a Recording.
     *
     * @return A Recording instance.
     */
    public Recording getRecording() {
        return (recording);
    }

    /**
     * A Screen event handing a Recording.
     *
     * @param r A Recording instance.
     */
    public void setRecording(Recording r) {
        recording = r;
    }

    /**
     * A Screen has changed in some way.
     *
     * @return A Screen instance.
     */
    public Screen getScreen() {
        return (screen);
    }

    /**
     * A Screen has changed in some way.
     *
     * @param s A Screen instance.
     */
    public void setScreen(Screen s) {
        screen = s;
    }

}

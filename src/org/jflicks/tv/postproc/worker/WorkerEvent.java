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
package org.jflicks.tv.postproc.worker;

import java.awt.AWTEvent;

import org.jflicks.tv.Recording;

/**
 * We try to capture all the properties one needs to manage a Worker.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class WorkerEvent extends AWTEvent {

    /**
     * Status that a Worker has completed.
     */
    public static final int COMPLETE = 1;

    /**
     * Status that a Worker has some update.
     */
    public static final int UPDATE = 2;

    private int type;
    private String message;
    private Recording recording;
    private boolean updateRecording;

    /**
     * Constructor to make just a status event.
     *
     * @param source The source of the event.
     * @param type The status type.
     * @param r This event is about this Recording.
     * @param update Flag whether Recording needs to be updated.
     */
    public WorkerEvent(Workerable source, int type, Recording r,
        boolean update) {

        super(source, -1);
        setType(type);
        setRecording(r);
        setUpdateRecording(update);
    }

    /**
     * Constructor to make a status event with a message.
     *
     * @param source The source of the event.
     * @param type The status type.
     * @param r This event is about this Recording.
     * @param message The status message.
     */
    public WorkerEvent(Workerable source, int type, Recording r,
        String message) {

        this(source, type, r, false);
        setMessage(message);
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
     * A message associated with this event.
     *
     * @return A message as a string.
     */
    public String getMessage() {
        return (message);
    }

    /**
     * A message associated with this event.
     *
     * @param s A message as a string.
     */
    public void setMessage(String s) {
        message = s;
    }

    /**
     * A Recording associated with this event.
     *
     * @return A Recording instance.
     */
    public Recording getRecording() {
        return (recording);
    }

    /**
     * A Recording associated with this event.
     *
     * @param r A Recording instance.
     */
    public void setRecording(Recording r) {
        recording = r;
    }

    /**
     * Flag that the Recording needs to be updated by the Scheduler.
     *
     * @return True if the Recording instance has been updated.
     */
    public boolean isUpdateRecording() {
        return (updateRecording);
    }

    /**
     * Flag that the Recording needs to be updated by the Scheduler.
     *
     * @param b True if the Recording instance has been updated.
     */
    public void setUpdateRecording(boolean b) {
        updateRecording = b;
    }

}

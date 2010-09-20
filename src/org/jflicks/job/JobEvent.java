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
package org.jflicks.job;

import java.awt.AWTEvent;
import java.io.Serializable;

/**
 * We try to capture all the properties one needs to manage a Job.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class JobEvent extends AWTEvent {

    /**
     * Status that a Job has completed.
     */
    public static final int COMPLETE = 1;

    /**
     * Status that a Job has some update.
     */
    public static final int UPDATE = 2;

    private int type;
    private String message;
    private Serializable state;

    /**
     * Constructor to make just a status event.
     *
     * @param source The source of the event.
     * @param type The status type.
     */
    public JobEvent(Jobable source, int type) {

        super(source, -1);
        setType(type);
    }

    /**
     * Constructor to make a status event with a message.
     *
     * @param source The source of the event.
     * @param type The status type.
     * @param message The status message.
     */
    public JobEvent(Jobable source, int type, String message) {

        this(source, type);
        setMessage(message);
    }

    /**
     * Constructor to make a status event with some Serializable object.
     *
     * @param source The source of the event.
     * @param type The status type.
     * @param state An object that represents some state.
     */
    public JobEvent(Jobable source, int type, Serializable state) {

        this(source, type);
        setState(state);
    }

    /**
     * Constructor to make a status event with some Serializable object
     * and message.
     *
     * @param source The source of the event.
     * @param type The status type.
     * @param message The status message.
     * @param state An object that represents some state.
     */
    public JobEvent(Jobable source, int type, String message,
        Serializable state) {

        this(source, type);
        setMessage(message);
        setState(state);
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
     * An object that represents the state of the event.  Only requirement
     * is that the object is Serializable.
     *
     * @return A state object as a Serializable.
     */
    public Serializable getState() {
        return (state);
    }

    /**
     * An object that represents the state of the event.  Only requirement
     * is that the object is Serializable.
     *
     * @param s A state object as a Serializable.
     */
    public void setState(Serializable s) {
        state = s;
    }

}

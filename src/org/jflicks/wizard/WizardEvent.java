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
package org.jflicks.wizard;

import java.awt.AWTEvent;

/**
 * This class encapsulates all the data needed for a wizard event.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class WizardEvent extends AWTEvent {

    /**
     * The user has decided to cancel.
     */
    public static final int CANCEL = 0;

    /**
     * The user has gone all the way to finish.
     */
    public static final int FINISH = 1;

    /**
     * The user has hit next.
     */
    public static final int NEXT = 2;

    private int state;

    /**
     * Constructor used to create an environment change event.
     *
     * @param source The firing object.
     * @param state The updated wizard state.
     */
    public WizardEvent(Wizardable source, int state) {

        super(source, -1);
        setState(state);
    }

    /**
     * The current state.
     *
     * @return The current state.
     */
    public int getState() {
        return (state);
    }

    /**
     * The current state.
     *
     * @param i The current state.
     */
    public void setState(int i) {
        state = i;
    }

}

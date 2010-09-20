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
package org.jflicks.ui;

import org.jflicks.mvc.BaseController;

/**
 * The controller for our MVC user interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class JFlicksController extends BaseController {

    /**
     * The Ticket array property.
     */
    public static final String TICKETS_PROPERTY = "Tickets";

    /**
     * Constructor with a given model.
     *
     * @param m An JFLICKSModel instance.
     */
    public JFlicksController(JFlicksModel m) {

        addModel(m);
    }

    /**
     * Called by a view to change the ticket array in the model.
     *
     * @param array The updated ticket array.
     */
    /*
    public void changeTickets(Ticket[] array) {
        setModelProperty(TICKETS_PROPERTY, array);
    }
    */

}

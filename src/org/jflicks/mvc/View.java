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
package org.jflicks.mvc;

import java.beans.PropertyChangeEvent;
import javax.swing.JFrame;

/**
 * View interface that represents the view in a MVC scheme.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface View {

    /**
     * A view has a handle to a controller.
     *
     * @return The controller this view is associated.
     */
    Controller getController();

    /**
     * A view has a handle to a controller.
     *
     * @param c The controller this view is associated.
     */
    void setController(Controller c);

    /**
     * A view has a "main" Frame.  Because some components like JMenu are
     * tied to a Frame, it makes sense to have the creation of the "view"
     * also create the Frame.
     *
     * @return A JFrame containing the main UI window.
     */
    JFrame getFrame();

    /**
     * Called by a controller when it needs to set a property change.
     *
     * @param event The property change event from the model.
     */
    void modelPropertyChange(PropertyChangeEvent event);

    /**
     * A view can maintain a set of properties if it wishes to do so.
     *
     * @param s A property name.
     * @return A property value.
     */
    String getProperty(String s);
}

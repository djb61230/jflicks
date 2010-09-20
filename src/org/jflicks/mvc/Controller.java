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

import java.beans.PropertyChangeListener;

/**
 * This is the controller in our MVC scheme.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Controller extends PropertyChangeListener {

    /**
     * A controller is associated with N number of views.  One is added
     * using this method.
     *
     * @param v A given view to add.
     */
    void addView(View v);

    /**
     * A controller is associated with N number of views.  One is removed
     * using this method.
     *
     * @param v A given view to remove.
     */
    void removeView(View v);

    /**
     * A controller is associated with N number of models.  One is added
     * using this method.
     *
     * @param m A given model to add.
     */
    void addModel(Model m);

    /**
     * A controller is associated with N number of models.  One is removed
     * using this method.
     *
     * @param m A given model to remove.
     */
    void removeModel(Model m);

    /**
     * Views can update models by calling this method on the controller.  There
     * is not direct link between a model and a view - all communication
     * goes through the controller.
     *
     * @param name A given proprty name.
     * @param newValue The updated value from the view (and user).
     */
    void setModelProperty(String name, Object newValue);
}

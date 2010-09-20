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
 * This is the interface for the Model in our MVC scheme.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Model {

    /**
     * A model needs to keep track of property change listeners.  Here we
     * add a listener.
     *
     * @param l The listener to add to our list.
     */
    void addPropertyChangeListener(PropertyChangeListener l);

    /**
     * A model needs to keep track of property change listeners.  Here we
     * remove a listener.
     *
     * @param l The listener to remove from our list.
     */
    void removePropertyChangeListener(PropertyChangeListener l);

    /**
     * Fires an event to all registered listeners informing them that a
     * property in this model has changed.
     *
     * @param s The name of the property
     * @param oldValue The previous value of the property before the change
     * @param newValue The new property value after the change
     */
    void firePropertyChange(String s, Object oldValue, Object newValue);

    /**
     * Fires an event to all registered listeners informing them that an
     * indexed property in this model has changed.
     *
     * @param s The name of the property
     * @param index The property index.
     * @param oldValue The previous value of the property before the change
     * @param newValue The new property value after the change
     */
    void fireIndexedPropertyChange(String s, int index, Object oldValue,
        Object newValue);

    /**
     * Fires an event to all registered listeners informing them that an
     * indexed property in this model has changed.
     *
     * @param s The name of the property
     * @param index The property index.
     * @param oldValue The previous value of the property before the change
     * @param newValue The new property value after the change
     */
    void fireIndexedPropertyChange(String s, int index, boolean oldValue,
        boolean newValue);

    /**
     * Fires an event to all registered listeners informing them that an
     * indexed property in this model has changed.
     *
     * @param s The name of the property
     * @param index The property index.
     * @param oldValue The previous value of the property before the change
     * @param newValue The new property value after the change
     */
    void fireIndexedPropertyChange(String s, int index, int oldValue,
        int newValue);

    /**
     * Fire all property values.  This is handy when a view "comes" and "goes".
     */
    void fireAllProperties();
}

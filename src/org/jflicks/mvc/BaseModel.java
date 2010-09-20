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
import java.beans.PropertyChangeSupport;

/**
 * This simple abstract base class for Model implements the PropertyChange
 * event code so extensions to this class do not have to do so.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseModel implements Model {

    private PropertyChangeSupport propertyChangeSupport;

    /**
     * Simple empty constructor.
     */
    public BaseModel() {

        setPropertyChangeSupport(new PropertyChangeSupport(this));
    }

    private PropertyChangeSupport getPropertyChangeSupport() {
        return (propertyChangeSupport);
    }

    private void setPropertyChangeSupport(PropertyChangeSupport pcs) {
        propertyChangeSupport = pcs;
    }

    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {

        PropertyChangeSupport pcs = getPropertyChangeSupport();
        if (pcs != null) {

            pcs.addPropertyChangeListener(l);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {

        PropertyChangeSupport pcs = getPropertyChangeSupport();
        if (pcs != null) {

            pcs.removePropertyChangeListener(l);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void firePropertyChange(String s, Object oldValue,
        Object newValue) {

        PropertyChangeSupport pcs = getPropertyChangeSupport();
        if ((pcs != null) && (s != null)) {

            pcs.firePropertyChange(s, oldValue, newValue);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void fireIndexedPropertyChange(String s, int index,
        Object oldValue, Object newValue) {

        PropertyChangeSupport pcs = getPropertyChangeSupport();
        if ((pcs != null) && (s != null)) {

            pcs.fireIndexedPropertyChange(s, index, oldValue, newValue);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void fireIndexedPropertyChange(String s, int index,
        boolean oldValue, boolean newValue) {

        PropertyChangeSupport pcs = getPropertyChangeSupport();
        if ((pcs != null) && (s != null)) {

            pcs.fireIndexedPropertyChange(s, index, oldValue, newValue);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void fireIndexedPropertyChange(String s, int index,
        int oldValue, int newValue) {

        PropertyChangeSupport pcs = getPropertyChangeSupport();
        if ((pcs != null) && (s != null)) {

            pcs.fireIndexedPropertyChange(s, index, oldValue, newValue);
        }
    }

}

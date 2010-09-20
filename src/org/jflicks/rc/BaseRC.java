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
import java.beans.PropertyChangeSupport;

/**
 * This base class implements some RC functionality that extensions can
 * take advantage.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseRC implements RC {

    private String title;
    private boolean mouseControl;
    private boolean keyboardControl;
    private boolean eventControl;
    private PropertyChangeSupport propertyChangeSupport;

    /**
     * Simple constructor.
     */
    public BaseRC() {

        setPropertyChangeSupport(new PropertyChangeSupport(this));
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return (title);
    }

    /**
     * Convenience method to set the title property.
     *
     * @param s A given String.
     */
    public void setTitle(String s) {

        String old = title;
        title = s;
        firePropertyChange("Title", old, title);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMouseControl() {
        return (mouseControl);
    }

    /**
     * {@inheritDoc}
     */
    public void setMouseControl(boolean b) {

        boolean old = mouseControl;
        mouseControl = b;
        firePropertyChange("MouseControl", old, mouseControl);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isKeyboardControl() {
        return (keyboardControl);
    }

    /**
     * {@inheritDoc}
     */
    public void setKeyboardControl(boolean b) {

        boolean old = keyboardControl;
        keyboardControl = b;
        firePropertyChange("KeyboardControl", old, keyboardControl);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEventControl() {
        return (eventControl);
    }

    /**
     * {@inheritDoc}
     */
    public void setEventControl(boolean b) {

        boolean old = eventControl;
        eventControl = b;
        firePropertyChange("EventControl", old, eventControl);
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
    public void addPropertyChangeListener(String name,
        PropertyChangeListener l) {

        PropertyChangeSupport pcs = getPropertyChangeSupport();
        if (pcs != null) {

            pcs.addPropertyChangeListener(name, l);
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

    protected void firePropertyChange(String s, Object oldValue,
        Object newValue) {

        PropertyChangeSupport pcs = getPropertyChangeSupport();
        if ((pcs != null) && (s != null)) {

            pcs.firePropertyChange(s, oldValue, newValue);
        }
    }

    protected void firePropertyChange(String s, boolean oldValue,
        boolean newValue) {

        PropertyChangeSupport pcs = getPropertyChangeSupport();
        if ((pcs != null) && (s != null)) {

            pcs.firePropertyChange(s, oldValue, newValue);
        }
    }

}


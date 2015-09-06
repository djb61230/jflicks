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

import java.awt.event.KeyEvent;
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
    private int upKeyEvent;
    private int downKeyEvent;
    private int leftKeyEvent;
    private int rightKeyEvent;
    private int enterKeyEvent;
    private int escapeKeyEvent;
    private int infoKeyEvent;
    private int maximizeKeyEvent;
    private int pauseKeyEvent;
    private int pageUpKeyEvent;
    private int pageDownKeyEvent;
    private int rewindKeyEvent;
    private int forwardKeyEvent;
    private int skipBackwardKeyEvent;
    private int skipForwardKeyEvent;
    private int sapKeyEvent;
    private int audiosyncPlusKeyEvent;
    private int audiosyncMinusKeyEvent;
    private int guideKeyEvent;

    /**
     * Simple constructor.
     */
    public BaseRC() {

        setPropertyChangeSupport(new PropertyChangeSupport(this));
        setUpKeyEvent(getDefaultUpKeyEvent());
        setDownKeyEvent(getDefaultDownKeyEvent());
        setLeftKeyEvent(getDefaultLeftKeyEvent());
        setRightKeyEvent(getDefaultRightKeyEvent());
        setEnterKeyEvent(getDefaultEnterKeyEvent());
        setEscapeKeyEvent(getDefaultEscapeKeyEvent());
        setInfoKeyEvent(getDefaultInfoKeyEvent());
        setMaximizeKeyEvent(getDefaultMaximizeKeyEvent());
        setPauseKeyEvent(getDefaultPauseKeyEvent());
        setPageUpKeyEvent(getDefaultPageUpKeyEvent());
        setPageDownKeyEvent(getDefaultPageDownKeyEvent());
        setRewindKeyEvent(getDefaultRewindKeyEvent());
        setForwardKeyEvent(getDefaultForwardKeyEvent());
        setSkipBackwardKeyEvent(getDefaultSkipBackwardKeyEvent());
        setSkipForwardKeyEvent(getDefaultSkipForwardKeyEvent());
        setSapKeyEvent(getDefaultSapKeyEvent());
        setAudiosyncPlusKeyEvent(getDefaultAudiosyncPlusKeyEvent());
        setAudiosyncMinusKeyEvent(getDefaultAudiosyncMinusKeyEvent());
        setGuideKeyEvent(getDefaultGuideKeyEvent());
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

    /**
     * {@inheritDoc}
     */
    public int getDefaultUpKeyEvent() {
        return (KeyEvent.VK_UP);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultDownKeyEvent() {
        return (KeyEvent.VK_DOWN);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultLeftKeyEvent() {
        return (KeyEvent.VK_LEFT);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultRightKeyEvent() {
        return (KeyEvent.VK_RIGHT);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultEnterKeyEvent() {
        return (KeyEvent.VK_ENTER);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultEscapeKeyEvent() {
        return (KeyEvent.VK_ESCAPE);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultInfoKeyEvent() {
        return (KeyEvent.VK_I);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultMaximizeKeyEvent() {
        return (KeyEvent.VK_F);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultPauseKeyEvent() {
        return (KeyEvent.VK_P);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultPageUpKeyEvent() {
        return (KeyEvent.VK_PAGE_UP);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultPageDownKeyEvent() {
        return (KeyEvent.VK_PAGE_DOWN);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultRewindKeyEvent() {
        return (KeyEvent.VK_R);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultForwardKeyEvent() {
        return (KeyEvent.VK_X);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultSkipBackwardKeyEvent() {
        return (KeyEvent.VK_Z);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultSkipForwardKeyEvent() {
        return (KeyEvent.VK_X);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultSapKeyEvent() {
        return (KeyEvent.VK_S);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultAudiosyncPlusKeyEvent() {
        return (KeyEvent.VK_PLUS);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultAudiosyncMinusKeyEvent() {
        return (KeyEvent.VK_MINUS);
    }

    /**
     * {@inheritDoc}
     */
    public int getDefaultGuideKeyEvent() {
        return (KeyEvent.VK_G);
    }

    /**
     * {@inheritDoc}
     */
    public int getUpKeyEvent() {
        return (upKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getDownKeyEvent() {
        return (downKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getLeftKeyEvent() {
        return (leftKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getRightKeyEvent() {
        return (rightKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getEnterKeyEvent() {
        return (enterKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getEscapeKeyEvent() {
        return (escapeKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getInfoKeyEvent() {
        return (infoKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getMaximizeKeyEvent() {
        return (maximizeKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getPauseKeyEvent() {
        return (pauseKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getPageUpKeyEvent() {
        return (pageUpKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getPageDownKeyEvent() {
        return (pageDownKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getRewindKeyEvent() {
        return (rewindKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getForwardKeyEvent() {
        return (forwardKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getSkipBackwardKeyEvent() {
        return (skipBackwardKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getSkipForwardKeyEvent() {
        return (skipForwardKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getSapKeyEvent() {
        return (sapKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getAudiosyncPlusKeyEvent() {
        return (audiosyncPlusKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getAudiosyncMinusKeyEvent() {
        return (audiosyncMinusKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public int getGuideKeyEvent() {
        return (guideKeyEvent);
    }

    /**
     * {@inheritDoc}
     */
    public void setUpKeyEvent(int i) {
        upKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setDownKeyEvent(int i) {
        downKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setLeftKeyEvent(int i) {
        leftKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setRightKeyEvent(int i) {
        rightKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setEnterKeyEvent(int i) {
        enterKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setEscapeKeyEvent(int i) {
        escapeKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setInfoKeyEvent(int i) {
        infoKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setMaximizeKeyEvent(int i) {
        maximizeKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setPauseKeyEvent(int i) {
        pauseKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setPageUpKeyEvent(int i) {
        pageUpKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setPageDownKeyEvent(int i) {
        pageDownKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setRewindKeyEvent(int i) {
        rewindKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setForwardKeyEvent(int i) {
        forwardKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setSkipBackwardKeyEvent(int i) {
        skipBackwardKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setSkipForwardKeyEvent(int i) {
        skipForwardKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setSapKeyEvent(int i) {
        sapKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setAudiosyncPlusKeyEvent(int i) {
        audiosyncPlusKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setAudiosyncMinusKeyEvent(int i) {
        audiosyncMinusKeyEvent = i;
    }

    /**
     * {@inheritDoc}
     */
    public void setGuideKeyEvent(int i) {
        guideKeyEvent = i;
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


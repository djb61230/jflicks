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
package org.jflicks.player;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.AbstractAction;

/**
 * This class is a base implementation of the Player interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BasePlayer implements Player {

    private PropertyChangeSupport propertyChangeSupport;

    private String type;
    private String title;
    private boolean playing;
    private boolean paused;
    private boolean autoSkip;
    private boolean completed;
    private double audioOffset;
    private Rectangle rectangle;
    private String message;

   /**
     * Simple empty constructor.
     */
    public BasePlayer() {

        setPropertyChangeSupport(new PropertyChangeSupport(this));
        setAudioOffset(0.0);
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

            // Remove it first in case users are sloppy about adding
            // themselves.
            pcs.removePropertyChangeListener(l);
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

            // Remove it first in case users are sloppy about adding
            // themselves.
            pcs.removePropertyChangeListener(name, l);
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

    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(String name,
        PropertyChangeListener l) {

        PropertyChangeSupport pcs = getPropertyChangeSupport();
        if (pcs != null) {

            pcs.removePropertyChangeListener(name, l);
        }
    }

    protected void firePropertyChange(String s, boolean oldValue,
        boolean newValue) {

        PropertyChangeSupport pcs = getPropertyChangeSupport();
        if ((pcs != null) && (s != null)) {

            pcs.firePropertyChange(s, oldValue, newValue);
        }
    }

    protected void firePropertyChange(String s, Object oldValue,
        Object newValue) {

        PropertyChangeSupport pcs = getPropertyChangeSupport();
        if ((pcs != null) && (s != null)) {

            pcs.firePropertyChange(s, oldValue, newValue);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return (type);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given type value.
     */
    public void setType(String s) {

        String old = type;
        type = s;
        firePropertyChange("Type", old, type);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVideoType() {
        return (Player.PLAYER_VIDEO.equals(getType()));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVideoWebType() {
        return (Player.PLAYER_VIDEO_WEB.equals(getType()));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAudioType() {
        return (Player.PLAYER_AUDIO.equals(getType()));
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return (title);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given title value.
     */
    public void setTitle(String s) {

        String old = title;
        title = s;
        firePropertyChange("Title", old, title);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPlaying() {
        return (playing);
    }

    /**
     * Convenience method to set this property.
     *
     * @param b The given playing state.
     */
    public void setPlaying(boolean b) {

        boolean old = playing;
        playing = b;
        firePropertyChange("Playing", old, playing);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPaused() {
        return (paused);
    }

    /**
     * Convenience method to set this property.
     *
     * @param b The given pause state.
     */
    public void setPaused(boolean b) {

        boolean old = paused;
        paused = b;
        firePropertyChange("Paused", old, paused);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAutoSkip() {
        return (autoSkip);
    }

    /**
     * {@inheritDoc}
     */
    public void setAutoSkip(boolean b) {

        boolean old = autoSkip;
        autoSkip = b;
        firePropertyChange("AutoSkip", old, autoSkip);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCompleted() {
        return (completed);
    }

    /**
     * {@inheritDoc}
     */
    public void setCompleted(boolean b) {

        boolean old = completed;
        completed = b;
        firePropertyChange("Completed", old, completed);
    }

    /**
     * A Player needs to maintain the current audio offset so changes by
     * the user can be correctly done.
     *
     * @return The current audio offset.
     */
    public double getAudioOffset() {
        return (audioOffset);
    }

    /**
     * A Player needs to maintain the current audio offset so changes by
     * the user can be correctly done.
     *
     * @param d The current audio offset.
     */
    public void setAudioOffset(double d) {
        audioOffset = d;
    }

    /**
     * {@inheritDoc}
     */
    public String getMessage() {
        return (message);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given type value.
     */
    public void setMessage(String s) {

        String old = message;
        message = s;
        firePropertyChange("Message", old, message);
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getRectangle() {
        return (rectangle);
    }

    /**
     * {@inheritDoc}
     */
    public void setRectangle(Rectangle r) {
        rectangle = r;
    }

    /**
     * Convenience method to determine if the user's Rectangle is in
     * fact the same size as fullscreen.
     *
     * @return True if the user has setRectangle to the actual screen size.
     */
    public boolean isFullscreen() {

        boolean result = true;

        Rectangle r = getRectangle();
        if (r != null) {

            Toolkit tk = Toolkit.getDefaultToolkit();
            Rectangle desktop = new Rectangle(tk.getScreenSize());
            if (!desktop.equals(r)) {

                result = false;
            }
        }

        return (result);
    }

    /**
     * Convenience method to get the display full screen Rectangle.
     *
     * @return A Rectangle the size of the display.
     */
    public Rectangle getFullscreenRectangle() {

        Toolkit tk = Toolkit.getDefaultToolkit();
        return (new Rectangle(tk.getScreenSize()));
    }

    /**
     * {@inheritDoc}
     */
    public void guide() {
    }

    /**
     * {@inheritDoc}
     */
    public void up() {
    }

    /**
     * {@inheritDoc}
     */
    public void down() {
    }

    /**
     * {@inheritDoc}
     */
    public void left() {
    }

    /**
     * {@inheritDoc}
     */
    public void right() {
    }

    /**
     * {@inheritDoc}
     */
    public void enter() {
    }

    protected class QuitAction extends AbstractAction {

        public QuitAction() {
        }

        public void actionPerformed(ActionEvent e) {

            setMessage(MESSAGE_QUIT);
        }
    }

    protected class InfoAction extends AbstractAction {

        public InfoAction() {
        }

        public void actionPerformed(ActionEvent e) {

            setMessage(MESSAGE_INFO);
        }
    }

}


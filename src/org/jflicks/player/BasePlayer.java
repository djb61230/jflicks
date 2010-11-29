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

import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Hashtable;
import javax.swing.AbstractAction;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

import org.jflicks.log.BaseLog;
import org.jflicks.rc.RC;

/**
 * This class is a base implementation of the Player interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BasePlayer extends BaseLog implements Player {

    private PropertyChangeSupport propertyChangeSupport;

    private Frame frame;
    private String type;
    private String title;
    private boolean playing;
    private boolean paused;
    private boolean autoSkip;
    private boolean completed;
    private double audioOffset;
    private Rectangle rectangle;
    private long lengthHint;
    private ServiceTracker eventServiceTracker;

   /**
     * Simple empty constructor.
     */
    public BasePlayer() {

        setPropertyChangeSupport(new PropertyChangeSupport(this));
        setAudioOffset(0.0);
    }

    /**
     * We keep a service tracker to keep track of the OSGi Event Admin.
     *
     * @return A ServiceTracker instance.
     */
    public ServiceTracker getEventServiceTracker() {
        return (eventServiceTracker);
    }

    /**
     * We keep a service tracker to keep track of the OSGi Event Admin.
     *
     * @param est A ServiceTracker instance.
     */
    public void setEventServiceTracker(ServiceTracker est) {
        eventServiceTracker = est;
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
    public Frame getFrame() {
        return (frame);
    }

    /**
     * {@inheritDoc}
     */
    public void setFrame(Frame f) {
        frame = f;
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
     * {@inheritDoc}
     */
    public long getLengthHint() {
        return (lengthHint);
    }

    /**
     * {@inheritDoc}
     */
    public void setLengthHint(long l) {
        lengthHint = l;
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

    /**
     * This can be used by extensions to turn any action into an RC
     * event message.
     *
     * @param s The command string.
     */
    public void commandEvent(String s) {

        log(DEBUG, "commandEvent <" + s + ">");
        ServiceTracker st = getEventServiceTracker();
        if ((st != null) && (s != null)) {

            EventAdmin ea = (EventAdmin) st.getService();
            if (ea != null) {

                Hashtable<String, String> props =
                    new Hashtable<String, String>();
                props.put("command", s);
                Event evt = new Event("org/jflicks/rc/COMMAND", props);
                ea.postEvent(evt);
            }
        }
    }

    protected class QuitAction extends AbstractAction {

        public QuitAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.ESCAPE_COMMAND);
        }
    }

    protected class InfoAction extends AbstractAction {

        public InfoAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.INFO_COMMAND);
        }
    }

    protected class UpAction extends AbstractAction {

        public UpAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.UP_COMMAND);
        }
    }

    protected class DownAction extends AbstractAction {

        public DownAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.DOWN_COMMAND);
        }
    }

    protected class LeftAction extends AbstractAction {

        public LeftAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.LEFT_COMMAND);
        }
    }

    protected class RightAction extends AbstractAction {

        public RightAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.RIGHT_COMMAND);
        }
    }

    protected class EnterAction extends AbstractAction {

        public EnterAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.ENTER_COMMAND);
        }
    }

    protected class GuideAction extends AbstractAction {

        public GuideAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.GUIDE_COMMAND);
        }
    }

    protected class PauseAction extends AbstractAction {

        public PauseAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.PAUSE_COMMAND);
        }
    }

    protected class PageUpAction extends AbstractAction {

        public PageUpAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.PAGE_UP_COMMAND);
        }
    }

    protected class PageDownAction extends AbstractAction {

        public PageDownAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.PAGE_DOWN_COMMAND);
        }
    }

    protected class RewindAction extends AbstractAction {

        public RewindAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.REWIND_COMMAND);
        }
    }

    protected class ForwardAction extends AbstractAction {

        public ForwardAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.FORWARD_COMMAND);
        }
    }

    protected class SkipBackwardAction extends AbstractAction {

        public SkipBackwardAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.SKIPBACKWARD_COMMAND);
        }
    }

    protected class SkipForwardAction extends AbstractAction {

        public SkipForwardAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.SKIPFORWARD_COMMAND);
        }
    }

    protected class AudioSyncPlusAction extends AbstractAction {

        public AudioSyncPlusAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.AUDIOSYNC_PLUS_COMMAND);
        }
    }

    protected class AudioSyncMinusAction extends AbstractAction {

        public AudioSyncMinusAction() {
        }

        public void actionPerformed(ActionEvent e) {

            commandEvent(RC.AUDIOSYNC_MINUS_COMMAND);
        }
    }

}


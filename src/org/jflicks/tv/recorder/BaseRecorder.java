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
package org.jflicks.tv.recorder;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

import org.jflicks.configure.BaseConfig;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Recording;

/**
 * This class is a base implementation of the Recorder interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseRecorder extends BaseConfig implements Recorder {

    private PropertyChangeSupport propertyChangeSupport;

    private String title;
    private String device;
    private boolean recording;
    private boolean recordingLiveTV;
    private Channel channel;
    private long startedAt;
    private long duration;
    private File destination;
    private String extension;
    private String host;
    private int port;

    /**
     * Simple empty constructor.
     */
    public BaseRecorder() {

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
    public String getTitle() {
        return (title);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given title value.
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * {@inheritDoc}
     */
    public String getDevice() {
        return (device);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given device value.
     */
    public void setDevice(String s) {
        device = s;
    }

    /**
     * {@inheritDoc}
     */
    public String getExtension() {
        return (extension);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given file extension value.
     */
    public void setExtension(String s) {
        extension = s;
    }

    /**
     * {@inheritDoc}
     */
    public String getHost() {
        return (host);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given Host value.
     */
    public void setHost(String s) {
        host = s;
    }

    /**
     * {@inheritDoc}
     */
    public int getPort() {
        return (port);
    }

    /**
     * Convenience method to set this property.
     *
     * @param i The given port value.
     */
    public void setPort(int i) {
        port = i;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRecording() {
        return (recording);
    }

    /**
     * Convenience method to set the recording live TV property.
     *
     * @param b The given boolean value.
     */
    public void setRecording(boolean b) {

        boolean old = recording;
        recording = b;
        firePropertyChange("Recording", old, recording);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRecordingLiveTV() {
        return (recordingLiveTV);
    }

    /**
     * Convenience method to set the recording live TV property.
     *
     * @param b The given boolean value.
     */
    public void setRecordingLiveTV(boolean b) {

        boolean old = recordingLiveTV;
        recordingLiveTV = b;
        firePropertyChange("RecordingLiveTV", old, recordingLiveTV);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRecording(Recording r) {

        boolean result = isRecording();

        if (result) {

            result = false;
            File dest = getDestination();
            if (dest != null) {

                String path = dest.getPath();
                if (path != null) {

                    result = path.equals(r.getPath());
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Channel getChannel() {
        return (channel);
    }

    /**
     * Convenience method to save the Channel property.
     *
     * @param c The given Channel instance.
     */
    protected void setChannel(Channel c) {
        channel = c;
    }

    /**
     * {@inheritDoc}
     */
    public long getDuration() {
        return (duration);
    }

    /**
     * Convenience method to save the duration property.
     *
     * @param l The given duration value.
     */
    protected void setDuration(long l) {
        duration = l;
    }

    /**
     * {@inheritDoc}
     */
    public long getStartedAt() {
        return (startedAt);
    }

    /**
     * Convenience method to save the startedAt property.
     *
     * @param l The given startedAt value.
     */
    protected void setStartedAt(long l) {
        startedAt = l;
    }

    /**
     * {@inheritDoc}
     */
    public File getDestination() {
        return (destination);
    }

    /**
     * Convenience method to save the File property.
     *
     * @param f The given File instance.
     */
    protected void setDestination(File f) {
        destination = f;
    }

}


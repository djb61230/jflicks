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
import java.util.ArrayList;

import org.jflicks.configure.BaseConfig;
import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
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
    private boolean quickTunable;
    private boolean recordingLiveTV;
    private Channel channel;
    private long startedAt;
    private long duration;
    private File destination;
    private String extension;
    private String host;
    private int port;
    private String[] channelNameList;
    private NMS nms;

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
     * Handy to have a reference to the NMS associated
     * with this Recorder.
     *
     * @return An NMS instance.
     */
    public NMS getNMS() {
        return (nms);
    }

    /**
     * Handy to have a reference to the NMS associated
     * with this Recorder.
     *
     * @param n An NMS instance.
     */
    public void setNMS(NMS n) {
        nms = n;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isQuickTunable() {
        return (quickTunable);
    }

    /**
     * Convenience method to set the quick tunable property.
     *
     * @param b The given boolean value.
     */
    public void setQuickTunable(boolean b) {

        boolean old = quickTunable;
        quickTunable = b;
        firePropertyChange("QuickTunable", old, quickTunable);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isWhiteList() {

        boolean result = false;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(
                NMSConstants.CUSTOM_CHANNEL_LIST_TYPE);
            if (nv != null) {

                result = NMSConstants.LIST_IS_A_WHITELIST.equals(nv.getValue());
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBlackList() {

        boolean result = false;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(
                NMSConstants.CUSTOM_CHANNEL_LIST_TYPE);
            if (nv != null) {

                result = NMSConstants.LIST_IS_A_BLACKLIST.equals(nv.getValue());
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getChannelNameList() {

        String[] result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv = c.findNameValueByName(
                NMSConstants.CUSTOM_CHANNEL_LIST);
            if (nv != null) {

                result = nv.valueToArray();
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Channel[] getCustomChannels(Channel[] array) {

        Channel[] result = array;

        String[] names = getChannelNameList();

        if ((result != null) && (names != null)) {

            log(DEBUG, "Looks like we DO have a custom channel list");

            // We do have a non-null list of channel names.  We proceed in
            // one of two ways.
            if (isWhiteList()) {

                log(DEBUG, "It's a WHITELIST");

                // The list tells us the only channels we can really record.
                // We need to filter the Channel instances to only our list
                // of names.
                ArrayList<Channel> list = new ArrayList<Channel>();
                for (int i = 0; i < array.length; i++) {

                    for (int j = 0; j < names.length; j++) {

                        if (isChannelNameOrNumber(array[i], names[j])) {

                            list.add(array[i]);
                            break;
                        }
                    }
                }

                if (list.size() > 0) {

                    result = list.toArray(new Channel[list.size()]);
                }

            } else if (isBlackList()) {

                log(DEBUG, "It's a BLACKLIST");

                // The list tells us the channels we cannot record.
                // We need to filter the Channel instances to ignore our list
                // of names.
                ArrayList<Channel> list = new ArrayList<Channel>();
                for (int i = 0; i < array.length; i++) {

                    boolean found = false;
                    for (int j = 0; j < names.length; j++) {

                        if (isChannelNameOrNumber(array[i], names[j])) {

                            found = true;
                        }
                    }

                    if (!found) {

                        // Not on our restricted list.
                        list.add(array[i]);
                    }
                }

                if (list.size() > 0) {

                    result = list.toArray(new Channel[list.size()]);
                }

            } else {

                log(DEBUG, "We are set to IGNORE the list");
            }

        } else {

            log(DEBUG, "Looks like we do NOT have a custom channel list");
        }

        return (result);
    }

    private boolean isChannelNameOrNumber(Channel c, String s) {

        boolean result = false;

        if ((c != null) && (s != null)) {

            String name = c.getName();
            String number = c.getNumber();

            result = ((s.equals(name)) || (s.equals(number)));
        }

        return (result);
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


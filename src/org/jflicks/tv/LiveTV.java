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
package org.jflicks.tv;

import java.io.Serializable;
import java.util.Arrays;

import org.jflicks.util.RandomGUID;

/**
 * This class contains all the properties representing a live tv session.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class LiveTV implements Serializable {

    /**
     * No message information to convey based upon the last action.
     */
    public static final int MESSAGE_TYPE_NONE = 1;

    /**
     * An error message to convey based upon the last action.
     */
    public static final int MESSAGE_TYPE_ERROR = 2;

    /**
     * Just an informational message to convey based upon the last action.
     */
    public static final int MESSAGE_TYPE_INFO = 3;

    private String id;
    private String path;
    private Channel currentChannel;
    private Channel[] channels;
    private String message;
    private int messageType;
    private String hostPort;

    /**
     * Simple empty constructor.
     */
    public LiveTV() {

        setId(RandomGUID.createGUID());
    }

    /**
     * A unique ID is associated with this object.
     *
     * @return An ID value as a String.
     */
    public String getId() {
        return (id);
    }

    private void setId(String s) {
        id = s;
    }

    /**
     * There is a Path property.
     *
     * @return The path to the live video stream.
     */
    public String getPath() {
        return (path);
    }

    /**
     * There is a Path property.
     *
     * @param s The path to the live video stream.
     */
    public void setPath(String s) {
        path = s;
    }

    /**
     * The Channel currently being viewed.
     *
     * @return The Channel that is being watched.
     */
    public Channel getCurrentChannel() {
        return (currentChannel);
    }

    /**
     * The Channel currently being viewed.
     *
     * @param c The Channel that is being watched.
     */
    public void setCurrentChannel(Channel c) {
        currentChannel = c;
    }

    /**
     * All the Channel instances supported.
     *
     * @return An array of Channel instances.
     */
    public Channel[] getChannels() {

        Channel[] result = null;

        if (channels != null) {

            result = Arrays.copyOf(channels, channels.length);
        }

        return (result);
    }

    /**
     * All the Channel instances supported.
     *
     * @param array An array of Channel instances.
     */
    public void setChannels(Channel[] array) {

        if (array != null) {
            channels = Arrays.copyOf(array, array.length);
        } else {
            channels = null;
        }
    }

    /**
     * Some sort of status message.
     *
     * @return A message from the last action.
     */
    public String getMessage() {
        return (message);
    }

    /**
     * Some sort of status message.
     *
     * @param s A message from the last action.
     */
    public void setMessage(String s) {
        message = s;
    }

    /**
     * The type of message generated from the last action.
     *
     * @return The int value denoting the message type.
     */
    public int getMessageType() {
        return (messageType);
    }

    /**
     * The type of message generated from the last action.
     *
     * @param i The int value denoting the message type.
     */
    public void setMessageType(int i) {
        messageType = i;
    }

    /**
     * Clients can tell the source of an instance of LiveTV by this
     * host and port of where it's NMS is running.  It is in the format
     * of host:port.
     *
     * @return The host as a String.
     */
    public String getHostPort() {
        return (hostPort);
    }

    /**
     * Clients can tell the source of an instance of LiveTV by this
     * host and port of where it's NMS is running.  It is in the format
     * of host:port.
     *
     * @param s The host as a String.
     */
    public void setHostPort(String s) {
        hostPort = s;
    }

}


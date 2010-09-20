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
package org.jflicks.stream;

/**
 * This class is a base implementation of the Stream interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseStream implements Stream {

    private String type;
    private String title;
    private String host;
    private int port;
    private boolean streaming;

    /**
     * Simple empty constructor.
     */
    public BaseStream() {
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
        type = s;
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
    public String getHost() {
        return (host);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given host value.
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
    public boolean isStreaming() {
        return (streaming);
    }

    /**
     * Convenience method to set the streaming property.
     *
     * @param b The given boolean value.
     */
    public void setStreaming(boolean b) {
        streaming = b;
    }

}


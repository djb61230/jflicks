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
package org.jflicks.restlet;

import org.jflicks.tv.Channel;
import org.jflicks.tv.ShowAiring;
import org.jflicks.util.Util;

/**
 * This class contains all the properties to be able to start live tv
 * on a channel.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class LiveTVItem {

    private String hostPort;
    private Channel channel;
    private ShowAiring showAiring;

    /**
     * Simple empty constructor.
     */
    public LiveTVItem() {
    }

    /**
     * A String to be able to find the associated NMS.
     *
     * @return A String.
     */
    public String getHostPort() {
        return (hostPort);
    }

    /**
     * A String to be able to find the associated NMS.
     *
     * @param s A String.
     */
    public void setHostPort(String s) {
        hostPort = s;
    }

    /**
     * A Channel that is available to watch live.
     *
     * @return A Channel instance.
     */
    public Channel getChannel() {
        return (channel);
    }

    /**
     * A Channel that is available to watch live.
     *
     * @param c A Channel instance.
     */
    public void setChannel(Channel c) {
        channel = c;
    }

    /**
     * What is showing right now.
     *
     * @return A ShowAiring instance.
     */
    public ShowAiring getShowAiring() {
        return (showAiring);
    }

    /**
     * What is showing right now.
     *
     * @param sa A ShowAiring instance.
     */
    public void setShowAiring(ShowAiring sa) {
        showAiring = sa;
    }

}


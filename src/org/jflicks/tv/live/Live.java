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
package org.jflicks.tv.live;

import org.jflicks.configure.Config;
import org.jflicks.nms.NMS;
import org.jflicks.tv.Channel;
import org.jflicks.tv.LiveTV;

/**
 * A Live service allows users to watch live TV and do things like get
 * show data and change channels.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Live extends Config {

    /**
     * The Live interface needs a title property.
     */
    String TITLE_PROPERTY = "Live-Title";

    /**
     * The title of this Live service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * The live needs access to the NMS since it has some convenience
     * methods to get/set recording information.
     *
     * @return A NMS instance.
     */
    NMS getNMS();

    /**
     * The live needs access to the NMS since it has some convenience
     * methods to get/set recording information.  On discovery of a Live, a
     * NMS should set this property.
     *
     * @param n A NMS instance.
     */
    void setNMS(NMS n);

    /**
     * Request to open a live TV session.  Any problems will be reported
     * in the returned LiveTV instance.
     *
     * @return A LiveTV instance.
     */
    LiveTV openSession();

    /**
     * Request a change to the given Channel.
     *
     * @param l A LiveTV instance.
     * @param c The requested Channel.
     * @return A LiveTV instance (possible the same object passed in).
     */
    LiveTV changeChannel(LiveTV l, Channel c);

    /**
     * Close a live TV session.
     *
     * @param l A LiveTV instance.
     */
    void closeSession(LiveTV l);
}


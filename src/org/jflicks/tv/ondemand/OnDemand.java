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
package org.jflicks.tv.ondemand;

import org.jflicks.configure.Config;
import org.jflicks.nms.NMS;
import org.jflicks.tv.recorder.Recorder;

/**
 * A Live service allows users to watch live TV and do things like get
 * show data and change channels.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface OnDemand extends Config {

    /**
     * The up command.
     */
    int COMMAND_UP = 1;

    /**
     * The down command.
     */
    int COMMAND_DOWN = 2;

    /**
     * The left command.
     */
    int COMMAND_LEFT = 3;

    /**
     * The right command.
     */
    int COMMAND_RIGHT = 4;

    /**
     * The select command.
     */
    int COMMAND_SELECT = 5;

    /**
     * The home command.
     */
    int COMMAND_HOME = 6;

    /**
     * The forward command.
     */
    int COMMAND_FWD = 7;

    /**
     * The back command.
     */
    int COMMAND_BACK = 8;

    /**
     * The pause command.
     */
    int COMMAND_PAUSE = 9;

    /**
     * The info command.
     */
    int COMMAND_INFO = 10;

    /**
     * The info command.
     */
    int COMMAND_REPLAY = 11;

    /**
     * The info command.
     */
    int COMMAND_GOBACK = 12;

    /**
     * The OnDemand interface needs a title property.
     */
    String TITLE_PROPERTY = "OnDemand-Title";

    /**
     * The title of this OnDemand service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * The OnDemand needs access to the NMS since it has some convenience
     * methods to get/set recording information.
     *
     * @return A NMS instance.
     */
    NMS getNMS();

    /**
     * The OnDemand needs access to the NMS since it has some convenience
     * methods to get/set recording information.  On discovery of an OnDemand,
     * a NMS should set this property.
     *
     * @param n A NMS instance.
     */
    void setNMS(NMS n);

    /**
     * Each OnDemand has a dedicated Recorder.  Perhaps this is a bit
     * restrictive but this "rule" will leave a clean division between
     * the Scheduler and OnDemand services and never the twain shall
     * meet as they say.
     *
     * @return A Recorder instance.
     */
    Recorder getRecorder();

    /**
     * Open a session to start a viewing from an OnDemand source.
     *
     * @param host A given host instance.
     * @param port The to send the stream data.
     * @return A StreamSession instance.
     */
    StreamSession openSession(String host, int port);

    /**
     * Close a previously opened stream session.
     *
     * @param ss This instance is needed so resources are properly cleaned up.
     */
    void closeSession(StreamSession ss);

    /**
     * Send a command for the current session.
     *
     * @param ss The current session.
     * @param type The type of command.
     */
    void command(StreamSession ss, int type);
}


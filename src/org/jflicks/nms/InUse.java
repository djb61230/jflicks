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
package org.jflicks.nms;

import java.io.File;
import java.util.ArrayList;

import org.jflicks.tv.Recording;
import org.jflicks.tv.recorder.Recorder;
import org.jflicks.tv.scheduler.Scheduler;

/**
 * This class details activity of clients.  We use it to keep track of
 * what recordings clients are watching.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class InUse {

    private String clientIpAddress;
    private String recordingId;
    private String hostPort;

    public InUse() {
    }

    public String getClientIpAddress() {
        return (clientIpAddress);
    }

    public void setClientIpAddress(String s) {
        clientIpAddress = s;
    }

    public String getRecordingId() {
        return (recordingId);
    }

    public void setRecordingId(String s) {
        recordingId = s;
    }

    public String getHostPort() {
        return (hostPort);
    }

    public void setHostPort(String s) {
        hostPort = s;
    }

}

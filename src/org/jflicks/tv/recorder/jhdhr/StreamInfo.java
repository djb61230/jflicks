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
package org.jflicks.tv.recorder.jhdhr;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.util.Util;

/**
 * This job will set a HDHR device to a particular "program".  A program
 * is a particular stream on a frequency.  For example, an OTA broadcaster
 * has it's main channel and often one or more sub-channels.  After the
 * HDHR is set to a frequency, this job will tune it to a particular
 * stream - either the main channel one of it's sub-channels.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class StreamInfo {

    private String program;

    /**
     * Simple no argument constructor.
     */
    public StreamInfo() {
    }

    public String getProgram() {
        return (program);
    }

    public void setProgram(String s) {
        program = s;
    }

    public String getProgramId(HDHRRecorder r) {

        String result = null;

        if (r != null) {

            String device = r.getDevice();
            int index = device.indexOf("-");
            String id = device.substring(0, index);
            String tunerStr = device.substring(index + 1);
            int tuner = Util.str2int(tunerStr, 0);

            result = getProgramId(id, tuner);
        }

        return (result);
    }

    public String getProgramId(String id, int tuner) {

        String result = null;

        if (id != null) {

            HDHRConfig config = new HDHRConfig();
            String text = config.streaminfo(id, tuner);
            if (text != null) {

                String[] lines = text.split("\n");
                if (lines != null) {

                    for (int i = 0; i < lines.length; i++) {

                        if (isProgramLine(lines[i])) {

                            int cindex = lines[i].indexOf(":");
                            if (cindex != -1) {

                                result = lines[i].substring(0, cindex);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return (result);
    }

    private boolean isProgramLine(String line) {

        boolean result = false;

        if (line != null) {

            int index = line.indexOf(":");
            if (index != -1) {

                // Get the "rest" of the line.
                line = line.substring(index + 1);
                line = line.trim();
                result = line.startsWith(getProgram());
            }
        }

        return (result);
    }

}

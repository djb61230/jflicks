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

import java.util.Arrays;

import org.jflicks.tv.LiveTV;
import org.jflicks.tv.recorder.Recorder;
import org.jflicks.tv.scheduler.RecorderInformation;

/**
 * This class contains all the properties representing a live tv session for
 * the server side.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Session {

    private LiveTV liveTV;
    private Recorder currentRecorder;
    private RecorderInformation[] recorderInformations;

    /**
     * Constructor with two required arguments.
     *
     * @param liveTV A LiveTV instance.
     * @param array An array of RecorderInformation instances.
     */
    public Session(LiveTV liveTV, RecorderInformation[] array) {

        setLiveTV(liveTV);
        setRecorderInformations(array);
    }

    /**
     * A unique LiveTV is associated with this object.
     *
     * @return An LiveTV instance.
     */
    public LiveTV getLiveTV() {
        return (liveTV);
    }

    private void setLiveTV(LiveTV l) {
        liveTV = l;
    }

    /**
     * A RecorderInformation array defines the recorders available to us
     * for live TV.
     *
     * @return An LiveTV instance.
     */
    public RecorderInformation[] getRecorderInformations() {

        RecorderInformation[] result = null;

        if (recorderInformations != null) {

            result = Arrays.copyOf(recorderInformations,
                recorderInformations.length);
        }

        return (result);
    }

    private void setRecorderInformations(RecorderInformation[] array) {

        if (array != null) {
            recorderInformations = Arrays.copyOf(array, array.length);
        } else {
            recorderInformations = null;
        }
    }

    /**
     * We have a property that defines the current recorder in use.
     *
     * @return A Recorder instance.
     */
    public Recorder getCurrentRecorder() {
        return (currentRecorder);
    }

    /**
     * We have a property that defines the current recorder in use.
     *
     * @param r A Recorder instance.
     */
    public void setCurrentRecorder(Recorder r) {
        currentRecorder = r;
    }

}


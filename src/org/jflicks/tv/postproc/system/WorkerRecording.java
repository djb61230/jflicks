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
package org.jflicks.tv.postproc.system;

import org.jflicks.tv.Recording;
import org.jflicks.tv.postproc.worker.Worker;

/**
 * Container class that we keep in our queue.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class WorkerRecording {

    private Worker worker;
    private Recording recording;

    /**
     * Simple default constructor.
     */
    public WorkerRecording() {
    }

    /**
     * The Worker we will ask to process the Recording.
     *
     * @return A Worker instance.
     */
    public Worker getWorker() {
        return (worker);
    }

    /**
     * The Worker we will ask to process the Recording.
     *
     * @param w A Worker instance.
     */
    public void setWorker(Worker w) {
        worker = w;
    }

    /**
     * A Worker needs a Recording to process.
     *
     * @return A Recording instance.
     */
    public Recording getRecording() {
        return (recording);
    }

    /**
     * A Worker needs a Recording to process.
     *
     * @param r A Recording instance.
     */
    public void setRecording(Recording r) {
        recording = r;
    }

}


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
package org.jflicks.tv.postproc;

import org.jflicks.configure.Config;
import org.jflicks.nms.NMS;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.postproc.worker.Worker;

/**
 * This interface defines a service that allows post processing of recordings.
 * These post workers can really be designed to do anything from commercial
 * detection to transcoding.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface PostProc extends Config {

    /**
     * The PostProc interface needs a title property.
     */
    String TITLE_PROPERTY = "PostProc-Title";

    /**
     * The title of this PostProc service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * The postproc needs access to the NMS since it has some convenience
     * methods to get/set recording data.
     *
     * @return A NMS instance.
     */
    NMS getNMS();

    /**
     * The postproc needs access to the NMS since it has some convenience
     * methods to get/set recording data.  On discovery of a PostProc, a
     * NMS should set this property.
     *
     * @param n A NMS instance.
     */
    void setNMS(NMS n);

    /**
     * Acquire all the known Worker instances.
     *
     * @return An array of Worker objects.
     */
    Worker[] getWorkers();

    /**
     * Add the post processing work to be done by submitting a RecordingRule
     * and a Recording.
     *
     * @param rr A given RecordingRule.
     * @param r A given Recording.
     */
    void addProcessing(RecordingRule rr, Recording r);

    /**
     * Add the post processing work to be done by submitting a Worker
     * name and a Recording.
     *
     * @param s A given Worker name.
     * @param r A given Recording.
     */
    void addProcessing(String s, Recording r);

    /**
     * There is a maximum number of jobs that can be run at one time.  This
     * can help limit the CPU usage of the computer.
     *
     * @return The maximum jobs that can be run at oonce.
     */
    int getConfiguredMaximumJobs();
}


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
package org.jflicks.tv.postproc.worker;

import org.jflicks.tv.Recording;

/**
 * This interface defines a service that performs some sort of workering
 * on a Recording.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Worker extends Workerable {

    /**
     * The PostProc interface needs a title property.
     */
    String TITLE_PROPERTY = "Worker-Title";

    /**
     * The title of this Worker service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * The description of this Worker service.
     *
     * @return The description as a String.
     */
    String getDescription();

    /**
     * Perform the work on a Recording.
     *
     * @param r A given Recording to act upon.
     */
    void work(Recording r);

    /**
     * Cancel a worker that was initiated earlier.
     *
     * @param r A given Recording.
     */
    void cancel(Recording r);

    /**
     * A Worker classifies itself as a heavy or light job.  A heavy job needs
     * to process an entire Video file to complete while a light job just
     * needs to "sample" the Video file.  This means a light job does not
     * take long to do.
     *
     * @return True is the Worker is "heavy".
     */
    boolean isHeavy();

    /**
     * A Worker can be flagged to be run as a default, of course the user
     * can always override this property.
     *
     * @return True if the Worker is run as a default as a suggestion.
     */
    boolean isDefaultRun();

    /**
     * A Worker can be flagged to always be run.  The idea is that we
     * want to run things as a worker but to allow the user to not run
     * it doesn't make a lot of sense.  We want to always run it if in
     * fact the service is deployed.
     *
     * @return True if the Worker is run always.
     */
    boolean isUserSelectable();
}


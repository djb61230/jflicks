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
package org.jflicks.tv;

import java.io.Serializable;

/**
 * This class contains all the properties representing a Task.  A Task can
 * act upon a Recording in some way.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Task implements Serializable {

    private String title;
    private String description;
    private boolean defaultRun;
    private boolean run;
    private boolean selectable;
    private boolean indexer;
    private boolean commercialDetector;

    /**
     * Simple empty constructor.
     */
    public Task() {
    }

    /**
     * Constructor to copy a given Task.
     *
     * @param t A given Task to "clone".
     */
    public Task(Task t) {

        if (t != null) {

            setTitle(t.getTitle());
            setDescription(t.getDescription());
            setDefaultRun(t.isDefaultRun());
            setRun(t.isRun());
            setSelectable(t.isSelectable());
            setIndexer(t.isIndexer());
            setCommercialDetector(t.isCommercialDetector());
        }
    }

    /**
     * A Task has an associated title.
     *
     * @return The task title.
     */
    public String getTitle() {
        return (title);
    }

    /**
     * A Task has an associated title.
     *
     * @param s The task title.
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * A Task has an associated description.
     *
     * @return The task description.
     */
    public String getDescription() {
        return (description);
    }

    /**
     * A Task has an associated description.
     *
     * @param s The task description.
     */
    public void setDescription(String s) {
        description = s;
    }

    /**
     * A task has a property that flags it as a default task to run.
     *
     * @return True if it is a default run task.
     */
    public boolean isDefaultRun() {
        return (defaultRun);
    }

    /**
     * A task has a property that flags it as a default task to run.
     *
     * @param b True if it is a default run task.
     */
    public void setDefaultRun(boolean b) {
        defaultRun = b;
    }

    /**
     * A task has a property that flags it should be run.
     *
     * @return True if it is to be run.
     */
    public boolean isRun() {
        return (run);
    }

    /**
     * A task has a property that flags it should be run.
     *
     * @param b True if it is to be run.
     */
    public void setRun(boolean b) {
        run = b;
    }

    /**
     * Can the user select the task to optionally run?
     *
     * @return True if the user can select the task.
     */
    public boolean isSelectable() {
        return (selectable);
    }

    /**
     * Can the user select the task to optionally run?
     *
     * @param b True if the user can select the task.
     */
    public void setSelectable(boolean b) {
        selectable = b;
    }

    /**
     * Can this Task index recordings?
     *
     * @return True if this Task does indexing.
     */
    public boolean isIndexer() {
        return (indexer);
    }

    /**
     * Can this Task index recordings?
     *
     * @param b True if this Task does indexing.
     */
    public void setIndexer(boolean b) {
        indexer = b;
    }

    /**
     * Can this Task detect commercials?
     *
     * @return True if this Task detects commercials.
     */
    public boolean isCommercialDetector() {
        return (commercialDetector);
    }

    /**
     * Can this Task detect commercials?
     *
     * @paran b True if this Task detects commercials.
     */
    public void setCommercialDetector(boolean b) {
        commercialDetector = b;
    }

    /**
     * Override to return Title.
     *
     * @return A String.
     */
    public String toString() {
        return (getTitle());
    }

}


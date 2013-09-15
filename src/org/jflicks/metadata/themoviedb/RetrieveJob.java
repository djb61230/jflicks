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
package org.jflicks.metadata.themoviedb;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;

/**
 * A job that retrieves a Movie instance by ID.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RetrieveJob extends AbstractJob {

    private int id;

    /**
     * Constructor with our required argument.
     *
     * @param id The supplied search terms to use.
     */
    public RetrieveJob(int id) {

        setId(id);
    }

    private int getId() {
        return (id);
    }

    private void setId(int i) {
        id = i;
    }

    /**
     * @inheritDoc
     */
    public void start() {
        setTerminate(false);
    }

    /**
     * @inheritDoc
     */
    public void run() {

        Movie movie = null;

        int theid = getId();
        TheMovieDB tmdb = TheMovieDB.getInstance();
        movie = tmdb.retrieve(theid);

        fireJobEvent(JobEvent.COMPLETE, movie);
    }

    /**
     * @inheritDoc
     */
    public void stop() {
        setTerminate(true);
    }

}

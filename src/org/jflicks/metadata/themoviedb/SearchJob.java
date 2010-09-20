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
 * A job that searches themoviedb.org given search terms.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SearchJob extends AbstractJob {

    private String terms;

    /**
     * Constructor with our required argument.
     *
     * @param terms The supplied search terms to use.
     */
    public SearchJob(String terms) {

        setTerms(terms);
    }

    private String getTerms() {
        return (terms);
    }

    private void setTerms(String s) {
        terms = s;
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

        Search search = null;

        String s = getTerms();
        if (s != null) {

            TheMovieDB tmdb = TheMovieDB.getInstance();
            search = tmdb.search(terms);
        }

        fireJobEvent(JobEvent.COMPLETE, search);
    }

    /**
     * @inheritDoc
     */
    public void stop() {
        setTerminate(true);
    }

}

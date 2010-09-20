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
package org.jflicks.metadata.thetvdb;

import java.util.List;

import com.moviejukebox.thetvdb.TheTVDB;
import com.moviejukebox.thetvdb.model.Series;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;

/**
 * A job that searches thetvdb.com given search terms.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SearchJob extends AbstractJob {

    private TheTVDB theTVDB;
    private String terms;

    /**
     * Constructor with our required arguments.
     *
     * @param tvdb A TheTVDB instanced used to communicate.
     * @param terms The supplied search terms to use.
     */
    public SearchJob(TheTVDB tvdb, String terms) {

        setTheTVDB(tvdb);
        setTerms(terms);
    }

    private TheTVDB getTheTVDB() {
        return (theTVDB);
    }

    private void setTheTVDB(TheTVDB tvdb) {
        theTVDB = tvdb;
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

        Series[] series = null;

        TheTVDB tvdb = getTheTVDB();
        String s = getTerms();
        if ((tvdb != null) && (s != null)) {

            List<Series> list = tvdb.searchSeries(terms, "en");
            if ((list != null) && (list.size() > 0)) {

                series = list.toArray(new Series[list.size()]);
            }
        }

        fireJobEvent(JobEvent.COMPLETE, series);
    }

    /**
     * @inheritDoc
     */
    public void stop() {
        setTerminate(true);
    }

}

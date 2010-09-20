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

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;

import com.moviejukebox.thetvdb.TheTVDB;
import com.moviejukebox.thetvdb.model.Episode;

/**
 * A job that retrieves a Movie instance by ID.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class EpisodeJob extends AbstractJob {

    private TheTVDB theTVDB;
    private String id;
    private int season;
    private int episode;

    /**
     * Constructor with our required arguments.
     *
     * @param tvdb A TheTVDB instanced used to communicate.
     * @param id The supplied search terms to use.
     * @param season The season number.
     * @param episode The episode number for the given season.
     */
    public EpisodeJob(TheTVDB tvdb, String id, int season, int episode) {

        setTheTVDB(tvdb);
        setId(id);
        setSeason(season);
        setEpisode(episode);
    }

    private TheTVDB getTheTVDB() {
        return (theTVDB);
    }

    private void setTheTVDB(TheTVDB tvdb) {
        theTVDB = tvdb;
    }

    private String getId() {
        return (id);
    }

    private void setId(String s) {
        id = s;
    }

    private int getSeason() {
        return (season);
    }

    private void setSeason(int i) {
        season = i;
    }

    private int getEpisode() {
        return (episode);
    }

    private void setEpisode(int i) {
        episode = i;
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

        Episode[] result = null;

        TheTVDB tvdb = getTheTVDB();
        String s = getId();
        if ((s != null) && (tvdb != null)) {

            Episode epp = tvdb.getEpisode(id, getSeason(), getEpisode(), "en");
            if (epp != null) {

                result = new Episode[1];
                result[0] = epp;
            }
        }

        fireJobEvent(JobEvent.COMPLETE, result);
    }

    /**
     * @inheritDoc
     */
    public void stop() {
        setTerminate(true);
    }

}

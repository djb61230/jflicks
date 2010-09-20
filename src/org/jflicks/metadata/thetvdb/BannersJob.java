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

import java.util.ArrayList;
import java.util.List;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;

import com.moviejukebox.thetvdb.TheTVDB;
import com.moviejukebox.thetvdb.model.Banner;
import com.moviejukebox.thetvdb.model.Banners;

/**
 * A job that retrieves a Movie instance by ID.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class BannersJob extends AbstractJob {

    private TheTVDB theTVDB;
    private String id;

    /**
     * Constructor with our required arguments.
     *
     * @param tvdb A TheTVDB instanced used to communicate.
     * @param id The supplied search terms to use.
     */
    public BannersJob(TheTVDB tvdb, String id) {

        setTheTVDB(tvdb);
        setId(id);
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

        Banner[] result = null;

        TheTVDB tvdb = getTheTVDB();
        String s = getId();
        if ((s != null) && (tvdb != null)) {

            Banners banners = tvdb.getBanners(id);
            if (banners != null) {

                ArrayList<Banner> l = new ArrayList<Banner>();
                List<Banner> bl = banners.getFanartList();
                if (bl != null) {

                    for (int i = 0; i < bl.size(); i++) {

                        l.add(bl.get(i));
                    }
                }

                bl = banners.getPosterList();
                if (bl != null) {

                    for (int i = 0; i < bl.size(); i++) {

                        l.add(bl.get(i));
                    }
                }

                bl = banners.getSeasonList();
                if (bl != null) {

                    for (int i = 0; i < bl.size(); i++) {

                        l.add(bl.get(i));
                    }
                }

                bl = banners.getSeriesList();
                if (bl != null) {

                    for (int i = 0; i < bl.size(); i++) {

                        l.add(bl.get(i));
                    }
                }

                if (l.size() > 0) {

                    result = l.toArray(new Banner[l.size()]);
                }
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

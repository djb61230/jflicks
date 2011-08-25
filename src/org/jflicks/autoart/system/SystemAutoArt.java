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
package org.jflicks.autoart.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jflicks.autoart.BaseAutoArt;
import org.jflicks.autoart.SearchItem;
import org.jflicks.db.DbWorker;
import org.jflicks.metadata.themoviedb.Image;
import org.jflicks.metadata.themoviedb.Movie;
import org.jflicks.metadata.themoviedb.Search;
import org.jflicks.metadata.themoviedb.TheMovieDB;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.osgi.Db4oService;
import com.db4o.query.Predicate;
import com.moviejukebox.thetvdb.TheTVDB;
import com.moviejukebox.thetvdb.model.Banner;
import com.moviejukebox.thetvdb.model.Banners;
import com.moviejukebox.thetvdb.model.Series;

/**
 * This is our implementation of an AutoArt service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SystemAutoArt extends BaseAutoArt implements DbWorker {

    private static final String KEY = "DD342FB4D55DF7BB";

    private ObjectContainer objectContainer;
    private Db4oService db4oService;
    private TheTVDB theTVDB;

    /**
     * Default empty constructor.
     */
    public SystemAutoArt() {

        setTitle("SystemAutoArt");
        setTheTVDB(new TheTVDB(KEY));
    }

    /**
     * We use the Db4oService to persist the configuration data.
     *
     * @return A Db4oService instance.
     */
    public Db4oService getDb4oService() {
        return (db4oService);
    }

    /**
     * We use the Db4oService to persist the configuration data.
     *
     * @param s A Db4oService instance.
     */
    public void setDb4oService(Db4oService s) {
        db4oService = s;
    }

    private TheTVDB getTheTVDB() {
        return (theTVDB);
    }

    private void setTheTVDB(TheTVDB tvdb) {
        theTVDB = tvdb;
    }

    private synchronized ObjectContainer getObjectContainer() {

        if (objectContainer == null) {

            Db4oService s = getDb4oService();
            if (s != null) {

                com.db4o.config.Configuration config = s.newConfiguration();
                objectContainer = s.openFile(config, "db/autoart.dat");

            } else {

                log(WARNING, "SystemAutoArt: Db4oService null!");
            }
        }

        return (objectContainer);
    }

    private void purge(ObjectContainer db, Class c) {

        if ((db != null) && (c != null)) {

            ObjectSet result = db.queryByExample(c);
            while (result.hasNext()) {
                db.delete(result.next());
            }
        }
    }

    private void addSearchItem(SearchItem si) {

        ObjectContainer oc = getObjectContainer();
        if ((si != null) && (oc != null)) {

            // First remove this SearchItem if it already exists.
            removeSearchItem(si);
            oc.store(new SearchItem(si));
            oc.commit();
        }
    }

    private void removeSearchItem(SearchItem si) {

        ObjectContainer oc = getObjectContainer();
        if ((si != null) && (oc != null)) {

            final String id = si.getId();
            List<SearchItem> sis =
                oc.query(new Predicate<SearchItem>() {

                public boolean match(SearchItem si) {
                    return (id.equals(si.getId()));
                }
            });

            if (sis != null) {

                for (int i = 0; i < sis.size(); i++) {

                    SearchItem tmp = sis.get(i);
                    oc.delete(tmp);
                }
            }
        }
    }

    private SearchItem getSearchItemById(String id) {

        SearchItem result = null;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            final String sid = id;
            List<SearchItem> items =
                oc.query(new Predicate<SearchItem>() {

                public boolean match(SearchItem si) {
                    return (sid.equals(si.getId()));
                }
            });

            if ((items != null) && (items.size() > 0)) {
                result = items.get(0);
            }
        }

        return (result);
    }

    private SearchItem[] getSearchItemHistory() {

        SearchItem[] result = null;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            ObjectSet<SearchItem> os = oc.queryByExample(SearchItem.class);
            if (os != null) {

                result = os.toArray(new SearchItem[os.size()]);
            }
        }

        return (result);
    }

    private boolean isReadyToSearch(SearchItem si) {

        boolean result = false;

        if (si != null) {

            // Not more than once every three days...
            long when = si.getLastCheck() + 3 * 24 * 60 * 60 * 1000;
            long now = System.currentTimeMillis();
            result = now > when;
        }

        return (result);
    }

    /**
     * Close up all resources.
     */
    public void close() {

        if (objectContainer != null) {

            boolean result = objectContainer.close();
            log(DEBUG, "SystemAutoArt: closed " + result);
            objectContainer = null;

        } else {

            log(DEBUG, "SystemAutoArt: Tried to close "
                + "but objectContainer null.");
        }
    }

    private void performSearch(SearchItem si) {

        // First let's try the TV database.
        TheTVDB tvdb = getTheTVDB();
        boolean checkMovie = true;
        if ((si != null) && (tvdb != null)) {

            List<Series> list = tvdb.searchSeries(si.getTitle(), "en");
            log(DEBUG, "Series list: " + list);
            if ((list != null) && (list.size() > 0)) {

                log(DEBUG, "Series list.size(): " + list.size());
                Banners banners = tvdb.getBanners(list.get(0).getId());
                if (banners != null) {

                    if (si.isNeedBanner()) {

                        List<Banner> blist = banners.getSeriesList();
                        if ((blist != null) && (blist.size() > 0)) {

                            si.setBannerURL(blist.get(0).getUrl());
                            checkMovie = false;
                        }
                    }

                    if (si.isNeedFanart()) {

                        List<Banner> blist = banners.getFanartList();
                        if ((blist != null) && (blist.size() > 0)) {

                            si.setFanartURL(blist.get(0).getUrl());
                            checkMovie = false;
                        }
                    }

                    if (si.isNeedPoster()) {

                        List<Banner> blist = banners.getPosterList();
                        if ((blist != null) && (blist.size() > 0)) {

                            si.setPosterURL(blist.get(0).getUrl());
                            checkMovie = false;
                        }
                    }
                }
            }

            if (checkMovie) {

                // OK we didn't find anything with the TV database.  We
                // will now try using the movie database.
                TheMovieDB tmdb = TheMovieDB.getInstance();
                if (tmdb != null) {

                    Search search = tmdb.search(si.getTitle());
                    if (search != null) {

                        Movie[] array = search.getMovies();
                        if ((array != null) && (array.length > 0)) {

                            Image[] iarray = array[0].getImages();
                            if ((iarray != null) && (iarray.length > 0)) {

                                String url = null;

                                // First find the first poster.
                                for (int i = 0; i < iarray.length; i++) {

                                    if (iarray[i].isPosterType()) {

                                        url = findMovieImageURL(array[0],
                                            iarray[i]);
                                        break;
                                    }
                                }

                                if (url != null) {
                                    si.setPosterURL(url);
                                }

                                url = null;

                                // Next find the first fanart.
                                for (int i = 0; i < iarray.length; i++) {

                                    if (iarray[i].isBackdropType()) {

                                        url = findMovieImageURL(array[0],
                                            iarray[i]);
                                        break;
                                    }
                                }

                                if (url != null) {
                                    si.setFanartURL(url);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private String findMovieImageURL(Movie movie, Image image) {

        String result = null;

        if ((movie != null) && (image != null)) {

            String id = image.getId();
            Image real = movie.getOriginalSizeImageById(id);
            if (real != null) {
                result = real.getUrl();
            } else {

                real = movie.getMidSizeImageById(id);
                if (real != null) {
                    result = real.getUrl();
                } else {
                    result = image.getUrl();
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void performUpdate() {

        SearchItem[] array = getSearchItems();
        if ((array != null) && (array.length > 0)) {

            log(INFO, "We have " + array.length + " items missing art.");

            // We have missing artwork, so we should check for new stuff.
            // However we should filter it because if it was missing on
            // our last run then perhaps we should wait a while before
            // checking again.  So we need to build a real list of
            // searches to do NOW.
            ArrayList<SearchItem> l = new ArrayList<SearchItem>();
            for (int i = 0; i < array.length; i++) {

                SearchItem si = getSearchItemById(array[i].getId());
                if (si == null) {

                    l.add(array[i]);

                } else if (isReadyToSearch(si)) {

                    l.add(array[i]);
                }
            }

            log(INFO, "We are going to search for " + l.size()
                + " items at this time.");

            for (int i = 0; i < l.size(); i++) {

                SearchItem si = l.get(i);
                log(INFO, "Searching using <" + si.getTitle() + ">");
                si.setLastCheck(System.currentTimeMillis());
                performSearch(si);
                save(si);
                addSearchItem(si);
            }
        }
    }

}


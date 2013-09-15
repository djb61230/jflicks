/*
    This file is part of ATM.

    ATM is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ATM is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ATM.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.jflicks.metadata.themoviedb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.google.gson.Gson;

/**
 * This class is used to communicate with themoviedb.org.  It can search and
 * get information on a movie given an ID.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class TheMovieDB {

    private static final String PROTOCOL = "http://";
    private static final String DOMAIN = "api.themoviedb.org";
    private static final String VERSION = "3";
    private static final String SEARCH = "search";
    private static final String GET_INFO = "movie";
    private static final String KEY = "ddfbd52df8841f7efe4d11d0117fa0c5";

    private static TheMovieDB instance = new TheMovieDB();
    private static String baseUrl = null;
    private static Gson gson = new Gson();

    /**
     * Default empty constructor.
     */
    private TheMovieDB() {
    }

    /**
     * We are a singleton, so users need access to it.
     *
     * @return A TheMovieDB instance.
     */
    public static TheMovieDB getInstance() {
        return (instance);
    }

    private String create(String url) {

        String result = null;

        if (url != null) {

            try {

                URL website = new URL(url);
                URLConnection connection = website.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));

                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                result = response.toString();

            } catch (Exception ex) {
                result = null;
            }
        }

        return (result);
    }

    /**
     * Search themoviedb.org site with the given search terms.
     *
     * @param terms Search terms from the user.
     * @return A Search instance with all the movies found.
     */
    public Search search(String terms) {

        Search result = null;

        if (baseUrl == null) {

            String curl = PROTOCOL + DOMAIN + "/" + VERSION + "/configuration"
                + "?api_key=" + KEY;
            String cjson = create(curl);
            System.out.println("cjson: " + cjson);

            // Futz with the json....
            baseUrl = cjson.substring(23);
            baseUrl = baseUrl.substring(0, baseUrl.indexOf("\""));
        }

        if (terms != null) {

            terms = encodeArgs(terms);
            String url = PROTOCOL + DOMAIN + "/" + VERSION + "/" + SEARCH
                + "/movie?api_key=" + KEY + "&query=" + terms;
            String json = create(url);
            result = gson.fromJson(json, Search.class);

            // We want to retrieve the movies again one-by-one so we
            // have all the info.
            if (result != null) {

                Movie[] marray = result.getMovies();
                if ((marray != null) && (marray.length > 0)) {

                    ArrayList<Movie> list = new ArrayList<Movie>();
                    for (int i = 0; i < marray.length; i++) {

                        Movie m = retrieve(marray[i].getId());
                        if (m != null) {

                            list.add(m);
                        }
                    }

                    if (list.size() > 0) {

                        result.setMovies(list.toArray(new Movie[list.size()]));
                    }
                }

            }
        }

        return (result);
    }

    /**
     * Given a movie ID get the complete information on it from
     * themoviedb.org.
     *
     * @param id The given ID.
     * @return A Movie instance for the ID.
     */
    public Movie retrieve(int id) {

        Movie result = null;

        String url = PROTOCOL + DOMAIN + "/" + VERSION + "/" + GET_INFO
            + "/" + id + "?api_key=" + KEY;
        String json = create(url);

        try {

            result = gson.fromJson(json, Movie.class);

            if (result != null) {

                url = PROTOCOL + DOMAIN + "/" + VERSION + "/" + GET_INFO
                    + "/" + id + "/images?api_key=" + KEY;
                json = create(url);
                Artwork art = gson.fromJson(json, Artwork.class);
                result.setArtwork(art);

                if (art != null) {

                    // Fill in urls.
                    fillUrls("Backdrop", "w300", art.getBackdrops());
                    fillUrls("Poster", "w92", art.getPosters());
                }
            }

        } catch (Exception ex) {

            result = null;
        }

        return (result);
    }

    private void fillUrls(String type, String sizeThumb, Image[] array) {

        if ((array != null) && (array.length > 0) && (baseUrl != null)) {

            String size = "original";
            for (int i = 0; i < array.length; i++) {

                String url = baseUrl + size + array[i].getFilePath();
                array[i].setType(type);
                array[i].setUrl(url);
                url = baseUrl + sizeThumb + array[i].getFilePath();
                array[i].setUrlThumb(url);
            }
        }
    }

    private String encodeArgs(String args) {

        String result = null;

        if (args != null) {

            try {

                result = URLEncoder.encode(args, "UTF-8");

            } catch (UnsupportedEncodingException ex) {

                result = null;
            }
        }

        return (result);
    }

    public static void main(String[] args) {

        TheMovieDB tmdb = new TheMovieDB();
        Search search = tmdb.search("Casablanca");
        System.out.println(search.getPage());
        System.out.println(search.getTotalResults());
        Movie[] array = search.getMovies();
        System.out.println("movies array: " + array);
        if (array != null) {

            System.out.println("array.length " + array.length);
            for (int i = 0; i < array.length; i++) {

                if (i == 0) {

                    Movie m = tmdb.retrieve(array[i].getId());
                    if (m != null) {

                        System.out.println("genres: " + m.getGenres());
                        Genre[] garray = m.getGenres();
                        if ((garray != null) && (garray.length > 0)) {

                            for (int j = 0; j < garray.length; j++) {

                                System.out.println("n: " + garray[j].getName());
                            }
                        }
                        System.out.println("a: " + m.getArtwork());
                    }
                }
            }
        }
    }

}


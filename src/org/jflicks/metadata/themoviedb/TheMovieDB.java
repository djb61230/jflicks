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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

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
    private static final String VERSION = "2.1";
    private static final String SEARCH = "Movie.search";
    private static final String GET_INFO = "Movie.getInfo";
    private static final String LANGUAGE = "en";
    private static final String TYPE = "xml";
    private static final String KEY = "FRED";

    private static TheMovieDB instance = new TheMovieDB();

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

    private Document create(String url) {

        Document result = null;

        if (url != null) {

            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            builder.setFeature(
              "http://apache.org/xml/features/nonvalidating/load-external-dtd",
              false);
            try {

                result = builder.build(url);

            } catch (JDOMException ex) {
                result = null;
            } catch (IOException ex) {
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

        if (terms != null) {

            terms = encodeArgs(terms);
            String url = PROTOCOL + DOMAIN + "/" + VERSION + "/" + SEARCH + "/"
                + LANGUAGE + "/" + TYPE + "/" + KEY + "/" + terms;
            Document doc = create(url);
            if (doc != null) {

                result = new Search(doc.getRootElement());
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
    public Movie retrieve(String id) {

        Movie result = null;

        if (id != null) {

            String url = PROTOCOL + DOMAIN + "/" + VERSION + "/" + GET_INFO
                + "/" + LANGUAGE + "/" + TYPE + "/" + KEY + "/" + id;
            Document doc = create(url);
            if (doc != null) {
                Search search = new Search(doc.getRootElement());
                if (search != null) {

                    result = search.getMovieAt(0);
                }
            }
        }

        return (result);
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

}


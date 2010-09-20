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

import java.util.ArrayList;

import org.jdom.Element;

/**
 * A Search object contains the results from a search to themoviedb.org.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Search extends BaseObject {

    private ArrayList<Movie> movieList;

    /**
     * Constructor supplying an Element.
     *
     * @param e The given element that contains data for this object.
     */
    public Search(Element e) {

        super(e);
    }

    private ArrayList<Movie> getMovieList() {
        return (movieList);
    }

    private void setMovieList(ArrayList<Movie> l) {
        movieList = l;
    }

    private void addMovie(Movie m) {

        ArrayList<Movie> l = getMovieList();
        if ((l != null) && (m != null)) {

            l.add(m);
        }
    }

    private void removeMovie(Movie m) {

        ArrayList<Movie> l = getMovieList();
        if ((l != null) && (m != null)) {

            l.remove(m);
        }
    }

    private void clear() {

        ArrayList<Movie> l = getMovieList();
        if (l != null) {

            l.clear();
        }
    }

    /**
     * Get the Movies as an array.
     *
     * @return An array of movies.
     */
    public Movie[] getMovies() {

        Movie[] result = null;

        ArrayList<Movie> l = getMovieList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Movie[l.size()]);
        }

        return (result);
    }

    private void setMovies(Movie[] array) {

        clear();
        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                addMovie(array[i]);
            }
        }
    }

    /**
     * How many Movies in this Searcch.
     *
     * @return The movie count in this Search.
     */
    public int getMovieCount() {

        int result = 0;

        ArrayList<Movie> l = getMovieList();
        if ((l != null) && (l.size() > 0)) {

            result = l.size();
        }

        return (result);
    }

    /**
     * Convenience method to get a Movie via an index.
     *
     * @param index A given index.
     * @return A Movie if it exists with the given index.
     */
    public Movie getMovieAt(int index) {

        Movie result = null;

        ArrayList<Movie> l = getMovieList();
        if ((l != null) && (l.size() > index)) {

            result = l.get(index);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void handle() {

        setMovieList(new ArrayList<Movie>());

        Element[] array = expectElements(getElement(), "movies", "movie");
        if ((array != null) && (array.length > 0)) {

            for (int i = 0; i < array.length; i++) {

                addMovie(new Movie(array[i]));
            }
        }
    }

}


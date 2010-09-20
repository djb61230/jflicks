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
package org.jflicks.metadata;

import java.awt.AWTEvent;

/**
 * We try to capture all the properties one needs to manage a Search.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SearchEvent extends AWTEvent {

    /**
     * Status that a Search has completed.
     */
    public static final int UPDATE = 1;

    /**
     * Suggest search terms to a listener.
     */
    public static final int SEARCH_TERMS = 2;

    /**
     * The suggested search terms work best with a movie source.
     */
    public static final int SEARCH_MOVIE = 3;

    /**
     * The suggested search terms work best with a tv source.
     */
    public static final int SEARCH_TV = 4;

    /**
     * The suggested search terms work best with a music source.
     */
    public static final int SEARCH_MUSIC = 5;

    private int type;
    private Hit hit;
    private String terms;
    private int searchType;

    /**
     * Constructor to make just a search event.
     *
     * @param source The source of the event.
     * @param type The search type.
     */
    public SearchEvent(Searchable source, int type) {

        super(source, -1);
        setType(type);
    }

    /**
     * Constructor to make a search event with some Hit object.
     *
     * @param source The source of the event.
     * @param type The search type.
     * @param h A Hit instance.
     */
    public SearchEvent(Searchable source, int type, Hit h) {

        this(source, type);
        setHit(h);
    }

    /**
     * Constructor to make a search event with search terms.
     *
     * @param source The source of the event.
     * @param searchType The search type (movie, tv or music).
     * @param terms Suggested search terms.
     */
    public SearchEvent(Searchable source, int searchType, String terms) {

        this(source, SEARCH_TERMS);
        setSearchType(searchType);
        setTerms(terms);
    }

    /**
     * The type of event.
     *
     * @return The type as an int.
     */
    public int getType() {
        return (type);
    }

    /**
     * The type of event.
     *
     * @param i The type as an int.
     */
    public void setType(int i) {
        type = i;
    }

    /**
     * A Search event handling a Hit.
     *
     * @return A Hit instance.
     */
    public Hit getHit() {
        return (hit);
    }

    /**
     * A Search event handling a Hit.
     *
     * @param r A Hit instance.
     */
    public void setHit(Hit r) {
        hit = r;
    }

    /**
     * A suggestion of search terms to use.
     *
     * @return A String instance.
     */
    public String getTerms() {
        return (terms);
    }

    /**
     * A suggestion of search terms to use.
     *
     * @param s A String instance.
     */
    public void setTerms(String s) {
        terms = s;
    }

    /**
     * The suggested search terms work best with a certain source, movie, tv
     * or music.
     *
     * @return An int value.
     */
    public int getSearchType() {
        return (searchType);
    }

    /**
     * The suggested search terms work best with a certain source, movie, tv
     * or music.
     *
     * @param i An int value.
     */
    public void setSearchType(int i) {
        searchType = i;
    }

}

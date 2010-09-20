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

/**
 * This class is a base implementation of the Metadata interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseMetadata implements Metadata {

    private String title;
    private boolean movieData;
    private boolean tvData;
    private boolean musicData;

    /**
     * Simple empty constructor.
     */
    public BaseMetadata() {
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return (title);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given title value.
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * Convenience property to determine movie support.
     *
     * @return True is movie data is supplied.
     */
    public boolean isMovieData() {
        return (movieData);
    }

    /**
     * Convenience property to determine movie support.
     *
     * @param b True is movie data is supplied.
     */
    public void setMovieData(boolean b) {
        movieData = b;
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsMovies() {
        return (isMovieData());
    }

    /**
     * Convenience property to determine TV support.
     *
     * @return True is TV data is supplied.
     */
    public boolean isTVData() {
        return (tvData);
    }

    /**
     * Convenience property to determine TV support.
     *
     * @param b True is TV data is supplied.
     */
    public void setTVData(boolean b) {
        tvData = b;
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsTV() {
        return (isTVData());
    }

    /**
     * Convenience property to determine music support.
     *
     * @return True is music data is supplied.
     */
    public boolean isMusicData() {
        return (musicData);
    }

    /**
     * Convenience property to determine music support.
     *
     * @param b True is music data is supplied.
     */
    public void setMusicData(boolean b) {
        musicData = b;
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsMusic() {
        return (isMusicData());
    }

    /**
     * Override with title property.
     *
     * @return a String.
     */
    public String toString() {
        return (getTitle());
    }

}


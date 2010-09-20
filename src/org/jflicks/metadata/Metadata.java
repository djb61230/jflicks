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
 * This interface defines the methods that allow acquiring data from some
 * source of metadata information on media - video, TV, music etc.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Metadata {

    /**
     * The Metadata interface needs a title property.
     */
    String TITLE_PROPERTY = "Metadata-Title";

    /**
     * The title of this source service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * A source might support movie metadata.
     *
     * @return True if movies are supported.
     */
    boolean supportsMovies();

    /**
     * A source might support TV metadata.
     *
     * @return True if TV shows are supported.
     */
    boolean supportsTV();

    /**
     * A source might support music metadata.
     *
     * @return True if music is supported.
     */
    boolean supportsMusic();

    /**
     * A Metadata implementation supplies a UI component.
     *
     * @return A UI component that interacts with the Metadata source.
     */
    SearchPanel getSearchPanel();
}


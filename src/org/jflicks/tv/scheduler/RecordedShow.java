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
package org.jflicks.tv.scheduler;

import org.jflicks.tv.Show;

/**
 * This class has all the properties needed to maintain that a show has
 * previously been recorded.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordedShow {

    private String showId;

    /**
     * Constructor with a Show argument.
     *
     * @param s A given Show instance.
     */
    public RecordedShow(Show s) {

        if (s != null) {
            setShowId(s.getId());
        }
    }

    /**
     * Constructor with a RecordedShow argument.
     *
     * @param rs A given RecordedShow instance.
     */
    public RecordedShow(RecordedShow rs) {

        if (rs != null) {
            setShowId(rs.getShowId());
        }
    }

    /**
     * Constructor with a String argument.
     *
     * @param showId A given show Id value.
     */
    public RecordedShow(String showId) {

        setShowId(showId);
    }

    /**
     * In case a we want access to the Show object we keep a showId property
     * in this class.
     *
     * @return A show id value.
     */
    public String getShowId() {
        return (showId);
    }

    /**
     * In case a we want access to the Show object we keep a showId property
     * in this class.
     *
     * @param s A show id value.
     */
    public void setShowId(String s) {
        showId = s;
    }

}


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
package org.jflicks.tv;

import java.io.Serializable;
import java.util.Date;

/**
 * This class contains all the properties representing a Show.  A Show can
 * be an episode of a series or a one time airing.  An instance of a Show
 * is what is recorded.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Show implements Serializable {

    private String id;
    private String title;
    private String subtitle;
    private String description;
    private String type;
    private String episodeNumber;
    private Date originalAirDate;
    private String seriesId;

    /**
     * Simple empty constructor.
     */
    public Show() {
    }

    /**
     * A unique ID is associated with this object.
     *
     * @return An ID value as a String.
     */
    public String getId() {
        return (id);
    }

    /**
     * A unique ID is associated with this object.
     *
     * @param s An ID value as a String.
     */
    public void setId(String s) {
        id = s;
    }

    /**
     * A Show has an associated title.
     *
     * @return The show title.
     */
    public String getTitle() {
        return (title);
    }

    /**
     * A Show has an associated title.
     *
     * @param s The show title.
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * A Show has an associated subtitle.
     *
     * @return The show subtitle.
     */
    public String getSubtitle() {
        return (subtitle);
    }

    /**
     * A Show has an associated subtitle.
     *
     * @param s The show subtitle.
     */
    public void setSubtitle(String s) {
        subtitle = s;
    }

    /**
     * A Show has an associated description.
     *
     * @return The show description.
     */
    public String getDescription() {
        return (description);
    }

    /**
     * A Show has an associated description.
     *
     * @param s The show description.
     */
    public void setDescription(String s) {
        description = s;
    }

    /**
     * A Show has an associated type.  Could be "Series", "Special" etc.
     *
     * @return The show type.
     */
    public String getType() {
        return (type);
    }

    /**
     * A Show has an associated type.  Could be "Series", "Special" etc.
     *
     * @param s The show type.
     */
    public void setType(String s) {
        type = s;
    }

    /**
     * A Show may have an episode number.
     *
     * @return The show episode number.
     */
    public String getEpisodeNumber() {
        return (episodeNumber);
    }

    /**
     * A Show may have an episode number.
     *
     * @param s The show episode number.
     */
    public void setEpisodeNumber(String s) {
        episodeNumber = s;
    }

    /**
     * The first time this Show was aired.
     *
     * @return The show original air date.
     */
    public Date getOriginalAirDate() {

        Date result = null;

        if (originalAirDate != null) {

            result = new Date(originalAirDate.getTime());
        }

        return (result);
    }

    /**
     * The first time this Show was aired.
     *
     * @param d The show original air date.
     */
    public void setOriginalAirDate(Date d) {

        if (d != null) {
            originalAirDate = new Date(d.getTime());
        } else {
            originalAirDate = null;
        }
    }

    /**
     * This Show is associated with a "series" by the SeriesId property.
     *
     * @return The series ID as a String instance.
     */
    public String getSeriesId() {
        return (seriesId);
    }

    /**
     * This Show is associated with a "series" by the SeriesId property.
     *
     * @param s The series ID as a String instance.
     */
    public void setSeriesId(String s) {
        seriesId = s;
    }

}


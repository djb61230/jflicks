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
 * This class contains all the properties representing a Airing.  A Airing can
 * be an episode of a channel or a one time airing.  An instance of a Airing
 * is what is recorded.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Airing implements Serializable {

    private long duration;
    private Date airDate;
    private String showId;
    private int channelId;
    private String listingId;

    /**
     * Simple empty constructor.
     */
    public Airing() {
    }

    /**
     * The time of the airing in the number of seconds.
     *
     * @return The number of seconds as a long.
     */
    public long getDuration() {
        return (duration);
    }

    /**
     * The time of the airing in the number of seconds.
     *
     * @param l The number of seconds as a long.
     */
    public void setDuration(long l) {
        duration = l;
    }

    /**
     * The time this Airing will be aired.
     *
     * @return The show air date.
     */
    public Date getAirDate() {

        Date result = null;

        if (airDate != null) {

            result = new Date(airDate.getTime());
        }

        return (result);
    }

    /**
     * The time this Airing will be aired.
     *
     * @param d The show air date.
     */
    public void setAirDate(Date d) {

        if (d != null) {
            airDate = new Date(d.getTime());
        } else {
            airDate = null;
        }
    }

    /**
     * This Airing is associated with a Show by the ShowId property.
     *
     * @return The show ID as a String instance.
     */
    public String getShowId() {
        return (showId);
    }

    /**
     * This Airing is associated with a Show by the ShowId property.
     *
     * @param s The show ID as a String instance.
     */
    public void setShowId(String s) {
        showId = s;
    }

    /**
     * This Airing is associated with a "channel" by the ChannelId property.
     *
     * @return The channel ID as a int.
     */
    public int getChannelId() {
        return (channelId);
    }

    /**
     * This Airing is associated with a "channel" by the ChannelId property.
     *
     * @param i The channel ID as a int.
     */
    public void setChannelId(int i) {
        channelId = i;
    }

    /**
     * This Airing is associated with a "listing" by the ListingId property.
     *
     * @return The listing ID as a String instance.
     */
    public String getListingId() {
        return (listingId);
    }

    /**
     * This Airing is associated with a "listing" by the ListingId property.
     *
     * @param s The listing ID as a String instance.
     */
    public void setListingId(String s) {
        listingId = s;
    }

    /**
     * Override since we override equals.
     *
     * @return An int value.
     */
    public int hashCode() {

        int result = 17;
        result = 37 * result + channelId;
        result = 37 * result + (int) (duration ^ (duration >>> 32));
        if (airDate != null) {
            result = 37 * result + airDate.hashCode();
        } else {
            result = 37 * result + 1;
        }
        if (showId != null) {
            result = 37 * result + showId.hashCode();
        } else {
            result = 37 * result + 1;
        }

        return (result);
    }

    /**
     * The equals override method.
     *
     * @param o A gven object to check.
     * @return True if the objects are equal.
     */
    public boolean equals(Object o) {

        boolean result = false;

        if (o == this) {

            result = true;

        } else if (!(o instanceof Airing)) {

            result = false;

        } else {

            Airing a = (Airing) o;

            result = getDuration() == a.getDuration();
            if (result) {

                result = getChannelId() == a.getChannelId();
                if (result) {

                    String s0 = getShowId();
                    String s1 = a.getShowId();
                    if ((s0 != null) && (s1 != null)) {

                        result = s0.equals(s1);

                    } else {

                        result = ((s0 == null) && (s1 == null));
                    }

                    if (result) {

                        Date d0 = getAirDate();
                        Date d1 = a.getAirDate();
                        if ((d0 != null) && (d1 != null)) {

                            result = d0.equals(d1);

                        } else {

                            result = ((d0 == null) && (d1 == null));
                        }
                    }
                }
            }
        }

        return (result);
    }

}


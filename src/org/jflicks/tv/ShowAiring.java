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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jflicks.util.RandomGUID;

/**
 * This class contains all the properties representing a TV channel.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ShowAiring implements Serializable, Comparable<ShowAiring> {

    private String id;
    private Show show;
    private Airing airing;
    private String hostPort;

    /**
     * Simple empty constructor.
     */
    public ShowAiring() {

        setId(RandomGUID.createGUID());
    }

    /**
     * Simple constructor with the two required arguments.
     *
     * @param s A given Show instance.
     * @param a A given Airing instance.
     */
    public ShowAiring(Show s, Airing a) {

        this();
        setShow(s);
        setAiring(a);
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
     * The Show property.
     *
     * @return A Show instance.
     */
    public Show getShow() {
        return (show);
    }

    /**
     * The Show property.
     *
     * @param s A Show instance.
     */
    public void setShow(Show s) {
        show = s;
    }

    /**
     * The Airing property.
     *
     * @return An Airing instance.
     */
    public Airing getAiring() {
        return (airing);
    }

    /**
     * The Airing property.
     *
     * @param a An Airing instance.
     */
    public void setAiring(Airing a) {
        airing = a;
    }

    /**
     * Determine if this show has fully aired at this moment in time.
     *
     * @return True if the show is over.
     */
    public boolean isOver() {

        boolean result = false;

        Airing a = getAiring();
        if (a != null) {

            Date d = a.getAirDate();
            if (d != null) {

                long end = d.getTime() + (a.getDuration() * 1000);
                result = (end < System.currentTimeMillis());
            }
        }

        return (result);
    }

    /**
     * Clients can tell the source of an instance of ShowAiring by this
     * host and port of where it's NMS is running.  It is in the format
     * of host:port.
     *
     * @return The host as a String.
     */
    public String getHostPort() {
        return (hostPort);
    }

    /**
     * Clients can tell the source of an instance of ShowAiring by this
     * host and port of where it's NMS is running.  It is in the format
     * of host:port.
     *
     * @param s The host as a String.
     */
    public void setHostPort(String s) {
        hostPort = s;
    }

    /*
    private SimpleDateFormat getSimpleDateFormat() {
        return (simpleDateFormat);
    }

    private void setSimpleDateFormat(SimpleDateFormat sdf) {
        simpleDateFormat = sdf;
    }
    */

    /**
     * Make a nice String suitable for sorting.
     *
     * @return A String.
     */
    public String toString() {

        StringBuilder sb = new StringBuilder();

        Airing a = getAiring();
        if (a != null) {

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd hh:mm a");
            sb.append(sdf.format(a.getAirDate()));
        }

        Show s = getShow();
        if (s != null) {

            sb.append(" ");
            sb.append(s.getTitle());
        }

        return (sb.toString());
    }

    /**
     * Override the hashcode.
     *
     * @return An int value.
     */
    public int hashCode() {

        int result = 0;

        Airing air = getAiring();
        if (air != null) {

            Date d = air.getAirDate();
            if (d != null) {

                result = d.hashCode();
            }
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

        } else if (!(o instanceof ShowAiring)) {

            result = false;

        } else {

            ShowAiring sa = (ShowAiring) o;

            String sid = getId();
            if (sid != null) {

                result = sid.equals(sa.getId());
            }
        }

        return (result);
    }

    /**
     * The comparable interface.
     *
     * @param sa The given ShowAiring instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(ShowAiring sa) throws ClassCastException {

        int result = 0;

        if (sa == null) {

            throw new NullPointerException();
        }

        if (sa == this) {

            result = 0;

        } else {

            Airing air0 = getAiring();
            if (air0 != null) {

                Airing air1 = sa.getAiring();
                if (air1 != null) {

                    Date date0 = air0.getAirDate();
                    Date date1 = air1.getAirDate();
                    if ((date0 != null) && (date1 != null)) {

                        result = date0.compareTo(date1);
                    }
                }
            }
        }

        return (result);
    }

}


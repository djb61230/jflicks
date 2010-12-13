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

import org.jflicks.util.RandomGUID;

/**
 * This class contains all the properties representing a recording rule.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Upcoming implements Serializable, Comparable<Upcoming> {

    private String id;
    private String title;
    private String subtitle;
    private String description;
    private String priority;
    private String channelNumber;
    private String channelName;
    private String start;
    private String duration;
    private String recorderName;
    private String status;
    private String seriesId;
    private String showId;
    private String bannerURL;
    private String posterURL;
    private String fanartURL;
    private String hostPort;
    private Date date;

    /**
     * Simple empty constructor.
     */
    public Upcoming() {

        setId(RandomGUID.createGUID());
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
     * There is a Title property.
     *
     * @return The title.
     */
    public String getTitle() {
        return (title);
    }

    /**
     * There is a Title property.
     *
     * @param s The title.
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * There is a Subtitle property.
     *
     * @return The subtitle.
     */
    public String getSubtitle() {
        return (subtitle);
    }

    /**
     * There is a Subtitle property.
     *
     * @param s The subtitle.
     */
    public void setSubtitle(String s) {
        subtitle = s;
    }

    /**
     * There is a Description property.
     *
     * @return The description.
     */
    public String getDescription() {
        return (description);
    }

    /**
     * There is a Description property.
     *
     * @param s The description.
     */
    public void setDescription(String s) {
        description = s;
    }

    /**
     * The defined priority for the rule that generated this upcoming
     * recording.
     *
     * @return A priority String.
     */
    public String getPriority() {
        return (priority);
    }

    /**
     * The defined priority for the rule that generated this upcoming
     * recording.
     *
     * @param s A priority String.
     */
    public void setPriority(String s) {
        priority = s;
    }

    /**
     * The Channel number as a String.  Example 6.1.
     *
     * @return The Channel number that is being recorded.
     */
    public String getChannelNumber() {
        return (channelNumber);
    }

    /**
     * The Channel number as a String.  Example 6.1.
     *
     * @param s The Channel number that is being recorded.
     */
    public void setChannelNumber(String s) {
        channelNumber = s;
    }

    /**
     * The Channel name as a String.  Example ESPN.
     *
     * @return The Channel name that is being recorded.
     */
    public String getChannelName() {
        return (channelName);
    }

    /**
     * The Channel name as a String.  Example ESPN.
     *
     * @param s The Channel name that is being recorded.
     */
    public void setChannelName(String s) {
        channelName = s;
    }

    /**
     * The starting time for the recording.
     *
     * @return The start time as a String.
     */
    public String getStart() {
        return (start);
    }

    /**
     * The starting time for the recording.
     *
     * @param s The start time as a String.
     */
    public void setStart(String s) {
        start = s;
    }

    /**
     * The length of time for the recording.
     *
     * @return The duration as a String.
     */
    public String getDuration() {
        return (duration);
    }

    /**
     * The length of time for the recording.
     *
     * @param s The duration as a String.
     */
    public void setDuration(String s) {
        duration = s;
    }

    /**
     * There is a RecorderName property.
     *
     * @return The recorder name.
     */
    public String getRecorderName() {
        return (recorderName);
    }

    /**
     * There is a RecorderName property.
     *
     * @param s The recorder name.
     */
    public void setRecorderName(String s) {
        recorderName = s;
    }

    /**
     * The actual status of the upcoming recording.  This status signifies
     * if the recording is actually going to happen and if not why it won't.
     *
     * @return the status as a String.
     */
    public String getStatus() {
        return (status);
    }

    /**
     * The actual status of the upcoming recording.  This status signifies
     * if the recording is actually going to happen and if not why it won't.
     *
     * @param s the status as a String.
     */
    public void setStatus(String s) {
        status = s;
    }

    /**
     * We keep a series ID in an upcoming.
     *
     * @return A series Id as a String.
     */
    public String getSeriesId() {
        return (seriesId);
    }

    /**
     * We keep a series ID in an upcoming.
     *
     * @param s A series Id as a String.
     */
    public void setSeriesId(String s) {
        seriesId = s;
    }

    /**
     * We keep a show ID in an upcoming.
     *
     * @return A show Id as a String.
     */
    public String getShowId() {
        return (showId);
    }

    /**
     * We keep a show ID in an upcoming.
     *
     * @param s A show Id as a String.
     */
    public void setShowId(String s) {
        showId = s;
    }

    /**
     * URL as a String where a banner image can be found if it exists.
     *
     * @return A String instance.
     */
    public String getBannerURL() {
        return (bannerURL);
    }

    /**
     * URL as a String where a banner image can be found if it exists.
     *
     * @param s A String instance.
     */
    public void setBannerURL(String s) {
        bannerURL = s;
    }

    /**
     * URL as a String where a poster image can be found if it exists.
     *
     * @return A String instance.
     */
    public String getPosterURL() {
        return (posterURL);
    }

    /**
     * URL as a String where a poster image can be found if it exists.
     *
     * @param s A String instance.
     */
    public void setPosterURL(String s) {
        posterURL = s;
    }

    /**
     * URL as a String where a fanart image can be found if it exists.
     *
     * @return A String instance.
     */
    public String getFanartURL() {
        return (fanartURL);
    }

    /**
     * URL as a String where a fanart image can be found if it exists.
     *
     * @param s A String instance.
     */
    public void setFanartURL(String s) {
        fanartURL = s;
    }

    /**
     * Clients can tell the source of an instance of Upcoming by this
     * host and port of where it's NMS is running.  It is in the format
     * of host:port.
     *
     * @return The host as a String.
     */
    public String getHostPort() {
        return (hostPort);
    }

    /**
     * Clients can tell the source of an instance of Upcoming by this
     * host and port of where it's NMS is running.  It is in the format
     * of host:port.
     *
     * @param s The host as a String.
     */
    public void setHostPort(String s) {
        hostPort = s;
    }

    /**
     * When the Upcoming will occur.
     *
     * @return A Date instance.
     */
    public Date getDate() {

        Date result = null;

        if (date != null) {

            result = new Date(date.getTime());
        }

        return (result);
    }

    /**
     * When the Upcoming will occur.
     *
     * @param d A Date instance.
     */
    public void setDate(Date d) {

        if (d != null) {
            date = new Date(d.getTime());
        } else {
            date = null;
        }
    }

    /**
     * The standard hashcode override.
     *
     * @return An int value.
     */
    public int hashCode() {
        return (getId().hashCode());
    }

    /**
     * The equals override method.
     *
     * @param o A given object to check.
     * @return True if the objects are equal.
     */
    public boolean equals(Object o) {

        boolean result = false;

        if (o == this) {

            result = true;

        } else if (!(o instanceof Upcoming)) {

            result = false;

        } else {

            Upcoming r = (Upcoming) o;
            String s = getId();
            if (s != null) {

                result = s.equals(r.getId());
            }
        }

        return (result);
    }

    /**
     * The comparable interface.
     *
     * @param u The given Upcoming instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(Upcoming u) throws ClassCastException {

        int result = 0;

        if (u == null) {

            throw new NullPointerException();
        }

        if (u == this) {

            result = 0;

        } else {

            Date date0 = getDate();
            Date date1 = u.getDate();
            if ((date0 != null) && (date1 != null)) {

                result = date0.compareTo(date1);
            }
        }

        return (result);
    }

    /**
     * Override by returning the Title property.
     *
     * @return The Title property.
     */
    public String toString() {
        return (getTitle());
    }

}


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
import java.util.Arrays;
import java.util.Date;

import org.jflicks.nms.Media;
import org.jflicks.util.RandomGUID;

/**
 * This class contains all the properties representing a recording.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Recording implements Media, Serializable, Comparable<Recording> {

    private String id;
    private String recordingRuleId;
    private String showId;
    private String title;
    private String subtitle;
    private String description;
    private String path;
    private Date date;
    private long duration;
    private long realStart;
    private Commercial[] commercials;
    private String seriesId;
    private String bannerURL;
    private String posterURL;
    private String fanartURL;
    private boolean currentlyRecording;
    private int audioFormat;
    private int videoFormat;
    private String hostPort;

    /**
     * Simple empty constructor.
     */
    public Recording() {

        setId(RandomGUID.createGUID());
    }

    /**
     * Convenience constructor to build a recording from a ShowAiring
     * instance.
     *
     * @param sa A given ShowAiring instance.
     */
    public Recording(ShowAiring sa) {

        this();

        if (sa != null) {

            Show show = sa.getShow();
            if (show != null) {

                setTitle(show.getTitle());
                setSubtitle(show.getSubtitle());
                setDescription(show.getDescription());
                setSeriesId(show.getSeriesId());
                setShowId(show.getId());
            }

            Airing airing = sa.getAiring();
            if (airing != null) {

                setDate(airing.getAirDate());
                setDuration(airing.getDuration());
            }
        }
    }

    /**
     * Constructor to "clone" a Recording instance.
     *
     * @param r A given Recording.
     */
    public Recording(Recording r) {

        setId(r.getId());
        setRecordingRuleId(r.getRecordingRuleId());
        setShowId(r.getShowId());
        setTitle(r.getTitle());
        setSubtitle(r.getSubtitle());
        setDescription(r.getDescription());
        setPath(r.getPath());
        setDate(r.getDate());
        setDuration(r.getDuration());
        setRealStart(r.getRealStart());
        setCommercials(r.getCommercials());
        setBannerURL(r.getBannerURL());
        setPosterURL(r.getPosterURL());
        setFanartURL(r.getFanartURL());
        setSeriesId(r.getSeriesId());
        setCurrentlyRecording(r.isCurrentlyRecording());
        setAudioFormat(r.getAudioFormat());
        setVideoFormat(r.getVideoFormat());
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
     * The Id of the RecordingRule that generated this recording.
     *
     * @return A unique Id for the RecordingRule.
     */
    public String getRecordingRuleId() {
        return (recordingRuleId);
    }

    /**
     * The Id of the RecordingRule that generated this recording.
     *
     * @param s A unique Id for the RecordingRule.
     */
    public void setRecordingRuleId(String s) {
        recordingRuleId = s;
    }

    /**
     * We need to keep the show ID that this recording is from in case
     * in the future the user wants to delete the recording but also allow
     * for re-recording.  Since everyday the program data is cleared we
     * do not necessarily have this data around when the user goes to do
     * a delete.  By still having this property we can ensure that the
     * data store which maintains the recording history can be updated.
     *
     * @return The Show Id.
     */
    public String getShowId() {
        return (showId);
    }

    /**
     * We need to keep the show ID that this recording is from in case
     * in the future the user wants to delete the recording but also allow
     * for re-recording.  Since everyday the program data is cleared we
     * do not necessarily have this data around when the user goes to do
     * a delete.  By still having this property we can ensure that the
     * data store which maintains the recording history can be updated.
     *
     * @param s The Show Id.
     */
    public void setShowId(String s) {
        showId = s;
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
     * A description of the recording.
     *
     * @return The description as a String instance.
     */
    public String getDescription() {
        return (description);
    }

    /**
     * A description of the recording.
     *
     * @param s The description as a String instance.
     */
    public void setDescription(String s) {
        description = s;
    }

    /**
     * The path of the recording on the server.
     *
     * @return The path as a String instance.
     */
    public String getPath() {
        return (path);
    }

    /**
     * The path of the recording on the server.
     *
     * @param s The path as a String instance.
     */
    public void setPath(String s) {
        path = s;
    }

    /**
     * When the recording occurred.
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
     * When the recording occurred.
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
     * The length of the recording in seconds.
     *
     * @return A long value representing the number of seconds of the
     * recording.
     */
    public long getDuration() {
        return (duration);
    }

    /**
     * The length of the recording in seconds.
     *
     * @param l A long value representing the number of seconds of the
     * recording.
     */
    public void setDuration(long l) {
        duration = l;
    }

    /**
     * The real time the recording was started.  Important to compute at
     * run time the current length of the recording while it's recording
     * and the true duration when it concludes.
     *
     * @return The real time the recording started in millis.
     */
    public long getRealStart() {
        return (realStart);
    }

    /**
     * The real time the recording was started.  Important to compute at
     * run time the current length of the recording while it's recording
     * and the true duration when it concludes.
     *
     * @param l The real time the recording started in millis.
     */
    public void setRealStart(long l) {
        realStart = l;
    }

    /**
     * Need to know if the Recording is currently in progress.
     *
     * @return True if recording now.
     */
    public boolean isCurrentlyRecording() {
        return (currentlyRecording);
    }

    /**
     * Need to know if the Recording is currently in progress.
     *
     * @param b True if recording now.
     */
    public void setCurrentlyRecording(boolean b) {
        currentlyRecording = b;
    }

    /**
     * The audio format defined in NMSConstants.
     *
     * @return The audio format type value.
     */
    public int getAudioFormat() {
        return (audioFormat);
    }

    /**
     * The audio format defined in NMSConstants.
     *
     * @param i The audio format type value.
     */
    public void setAudioFormat(int i) {
        audioFormat = i;
    }

    /**
     * The video format defined in NMSConstants.
     *
     * @return The video format type value.
     */
    public int getVideoFormat() {
        return (videoFormat);
    }

    /**
     * The video format defined in NMSConstants.
     *
     * @param i The video format type value.
     */
    public void setVideoFormat(int i) {
        videoFormat = i;
    }

    /**
     * An array of Commercial objects that flag the location of commercials
     * in the recording.
     *
     * @return An array of Commercial instances.
     */
    public Commercial[] getCommercials() {

        Commercial[] result = null;

        if (commercials != null) {

            result = Arrays.copyOf(commercials, commercials.length);
        }

        return (result);
    }

    /**
     * An array of Commercial objects that flag the location of commercials
     * in the recording.
     *
     * @param array An array of Commercial instances.
     */
    public void setCommercials(Commercial[] array) {

        if (array != null) {
            commercials = Arrays.copyOf(array, array.length);
        } else {
            commercials = null;
        }
    }

    /**
     * We keep a series ID in a recording for future lookup if need be.  One
     * could do things like "find future episodes" or it gives one a way to
     * attach other series information (like fanart) to this episode.
     *
     * @return A series Id as a String.
     */
    public String getSeriesId() {
        return (seriesId);
    }

    /**
     * We keep a series ID in a recording for future lookup if need be.  One
     * could do things like "find future episodes" or it gives one a way to
     * attach other series information (like fanart) to this episode.
     *
     * @param s A series Id as a String.
     */
    public void setSeriesId(String s) {
        seriesId = s;
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
     * Clients can tell the source of an instance of Recording by this
     * host and port of where it's NMS is running.  It is in the format
     * of host:port.
     *
     * @return The host as a String.
     */
    public String getHostPort() {
        return (hostPort);
    }

    /**
     * Clients can tell the source of an instance of Recording by this
     * host and port of where it's NMS is running.  It is in the format
     * of host:port.
     *
     * @param s The host as a String.
     */
    public void setHostPort(String s) {
        hostPort = s;
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
     * @param o A gven object to check.
     * @return True if the objects are equal.
     */
    public boolean equals(Object o) {

        boolean result = false;

        if (o == this) {

            result = true;

        } else if (!(o instanceof Recording)) {

            result = false;

        } else {

            Recording r = (Recording) o;
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
     * @param r The given Recording instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(Recording r) throws ClassCastException {

        int result = 0;

        if (r == null) {

            throw new NullPointerException();
        }

        if (r == this) {

            result = 0;

        } else {

            Date date0 = getDate();
            Date date1 = r.getDate();
            if ((date0 != null) && (date1 != null)) {

                result = date1.compareTo(date0);
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


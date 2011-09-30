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
package org.jflicks.nms;

import java.io.Serializable;

import org.jflicks.util.RandomGUID;

/**
 * This class contains all the properties to describe a Video type of media.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Video implements Media, Serializable, Comparable<Video> {

    private String id;
    private String path;
    private String filename;
    private String category;
    private String subcategory;
    private String title;
    private String description;
    private String bannerURL;
    private String posterURL;
    private String fanartURL;
    private String streamURL;
    private String released;
    private String aspectRatio;
    private long duration;
    private int season;
    private int episode;
    private boolean playIntro;
    private boolean hidden;
    private String hostPort;
    private StringBuilder stringBuilder;

    /**
     * Simple empty constructor.
     */
    public Video() {

        setId(RandomGUID.createGUID());
        stringBuilder = new StringBuilder();
    }

    /**
     * Constructor to "clone" a Video instance.
     *
     * @param v A given Video.
     */
    public Video(Video v) {

        this();
        setId(v.getId());
        setPath(v.getPath());
        setFilename(v.getFilename());
        setCategory(v.getCategory());
        setSubcategory(v.getSubcategory());
        setTitle(v.getTitle());
        setDescription(v.getDescription());
        setBannerURL(v.getBannerURL());
        setPosterURL(v.getPosterURL());
        setFanartURL(v.getFanartURL());
        setStreamURL(v.getStreamURL());
        setReleased(v.getReleased());
        setDuration(v.getDuration());
        setSeason(v.getSeason());
        setEpisode(v.getEpisode());
        setHidden(v.isHidden());
        setAspectRatio(v.getAspectRatio());
        setPlayIntro(v.isPlayIntro());
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
     * A Video has a file path where it is located.
     *
     * @return The path to the video.
     */
    public String getPath() {
        return (path);
    }

    /**
     * A Video has a file path where it is located.
     *
     * @param s The path to the video.
     */
    public void setPath(String s) {
        path = s;
    }

    /**
     * The Video also has a filename property which is used for a "key"
     * field.  By that we mean a Video filename MUST be unique for the
     * set of video files.  It must be unique regardless of directory.
     * We do this so the directories can be changed or moved without
     * having to redo the metadata.  The metadata is keys to just the
     * filename, not the path.
     *
     * @return The filename of the video.
     */
    public String getFilename() {
        return (filename);
    }

    /**
     * The Video also has a filename property which is used for a "key"
     * field.  By that we mean a Video filename MUST be unique for the
     * set of video files.  It must be unique regardless of directory.
     * We do this so the directories can be changed or moved without
     * having to redo the metadata.  The metadata is keys to just the
     * filename, not the path.
     *
     * @param s The filename of the video.
     */
    public void setFilename(String s) {
        filename = s;
    }

    /**
     * A Video has a main category.
     *
     * @return A String instance.
     */
    public String getCategory() {
        return (category);
    }

    /**
     * A Video has a main category.
     *
     * @param s A String instance.
     */
    public void setCategory(String s) {
        category = s;
    }

    /**
     * In addition to a main category, a Video has a subcategory.
     *
     * @return A String instance.
     */
    public String getSubcategory() {
        return (subcategory);
    }

    /**
     * In addition to a main category, a Video has a subcategory.
     *
     * @param s A String instance.
     */
    public void setSubcategory(String s) {
        subcategory = s;
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
     * {@inheritDoc}
     */
    public String getStreamURL() {
        return (streamURL);
    }

    /**
     * {@inheritDoc}
     */
    public void setStreamURL(String s) {
        streamURL = s;
    }

    /**
     * When the Video was released.
     *
     * @return A String instance.
     */
    public String getReleased() {
        return (released);
    }

    /**
     * When the Video was released.
     *
     * @param s A String instance.
     */
    public void setReleased(String s) {
        released = s;
    }

    /**
     * The length of the video in seconds.
     *
     * @return A long value representing the number of seconds of the
     * video.
     */
    public long getDuration() {
        return (duration);
    }

    /**
     * The length of the video in seconds.
     *
     * @param l A long value representing the number of seconds of the
     * video.
     */
    public void setDuration(long l) {
        duration = l;
    }

    /**
     * The aspect ratio of the video.
     *
     * @return A String instance.
     */
    public String getAspectRatio() {
        return (aspectRatio);
    }

    /**
     * The aspect ratio of the video.
     *
     * @param s A String instance.
     */
    public void setAspectRatio(String s) {
        aspectRatio = s;
    }

    /**
     * Convenience method to determine if the Video is 16x9.
     *
     * @return True if 16x9.
     */
    public boolean isSixteenByNine() {
        return (NMSConstants.ASPECT_RATIO_16X9.equals(getAspectRatio()));
    }

    /**
     * Convenience method to determine if the Video is 4x3.
     *
     * @return True if 4x3.
     */
    public boolean isFourByThree() {
        return (NMSConstants.ASPECT_RATIO_4X3.equals(getAspectRatio()));
    }

    /**
     * Convenience method to determine if the Video is 2.35x1.
     *
     * @return True if 2.35x1.
     */
    public boolean isTwoThirtyFiveByOne() {
        return (NMSConstants.ASPECT_RATIO_235X1.equals(getAspectRatio()));
    }

    /**
     * A TV Video might have a Season property.
     *
     * @return The season number.
     */
    public int getSeason() {
        return (season);
    }

    /**
     * A TV Video might have a Season property.
     *
     * @param i The season number.
     */
    public void setSeason(int i) {
        season = i;
    }

    /**
     * A TV Video might have an Episode property.
     *
     * @return The episode number.
     */
    public int getEpisode() {
        return (episode);
    }

    /**
     * A TV Video might have an Episode property.
     *
     * @param i The episode number.
     */
    public void setEpisode(int i) {
        episode = i;
    }

    /**
     * It's sometimes desireable to make a Video hidden.
     *
     * @return True if the Video is meant to be hidden.
     */
    public boolean isHidden() {
        return (hidden);
    }

    /**
     * It's sometimes desireable to make a Video hidden.
     *
     * @param b True if the Video is meant to be hidden.
     */
    public void setHidden(boolean b) {
        hidden = b;
    }

    /**
     * A Video can be flagged that an "intro" should play before
     * this video.  This is just a suggestion and clients can do
     * what they want to do.
     *
     * @return True if the Video is meant to be a "feature".
     */
    public boolean isPlayIntro() {
        return (playIntro);
    }

    /**
     * A Video can be flagged that an "intro" should play before
     * this video.  This is just a suggestion and clients can do
     * what they want to do.
     *
     * @param b True if the Video is meant to be a "feature".
     */
    public void setPlayIntro(boolean b) {
        playIntro = b;
    }

    /**
     * Convenience method to determine if the category is TV.
     *
     * @return True if the category is TV.
     */
    public boolean isTV() {
        return (NMSConstants.VIDEO_TV.equals(getCategory()));
    }

    /**
     * Convenience method to determine if the category is a Movie.
     *
     * @return True if the category is a Movie.
     */
    public boolean isMovie() {
        return (NMSConstants.VIDEO_MOVIE.equals(getCategory()));
    }

    /**
     * Convenience method to determine if the category is Home Video.
     *
     * @return True if the category is Home Video.
     */
    public boolean isHome() {
        return (NMSConstants.VIDEO_HOME.equals(getCategory()));
    }

    /**
     * Convenience method to determine if the category is the given one.
     *
     * @param s A given category to check.
     * @return True if the category is found.
     */
    public boolean isCategory(String s) {

        boolean result = false;

        if (s != null) {

            result = s.equals(getCategory());
        }

        return (result);
    }

    /**
     * Convenience method to determine if the subcategory is the given one.
     *
     * @param s A given subcategory to check.
     * @return True if the subcategory is found.
     */
    public boolean isSubcategory(String s) {

        boolean result = false;

        if (s != null) {

            result = s.equals(getSubcategory());
        }

        return (result);
    }

    /**
     * Clients can tell the source of an instance of Video by this
     * host and port of where it's NMS is running.  It is in the format
     * of host:port.
     *
     * @return The host as a String.
     */
    public String getHostPort() {
        return (hostPort);
    }

    /**
     * Clients can tell the source of an instance of Video by this
     * host and port of where it's NMS is running.  It is in the format
     * of host:port.
     *
     * @param s The host as a String.
     */
    public void setHostPort(String s) {
        hostPort = s;
    }

    private String toCompareString(Video v) {

        stringBuilder.setLength(0);
        stringBuilder.append(v.getTitle());
        if (v.isTV()) {

            int sea = v.getSeason();
            if (sea < 10) {
                stringBuilder.append("0");
            }
            stringBuilder.append("" + sea);

            int ep = v.getEpisode();
            if (ep < 10) {
                stringBuilder.append("0");
            }
            stringBuilder.append("" + ep);
        }

        return (stringBuilder.toString());
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

        } else if (!(o instanceof Video)) {

            result = false;

        } else {

            Video v = (Video) o;
            String s = getId();
            if (s != null) {

                result = s.equals(v.getId());
            }
        }

        return (result);
    }

    /**
     * The comparable interface.
     *
     * @param v The given Video instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(Video v) throws ClassCastException {

        int result = 0;

        if (v == null) {

            throw new NullPointerException();
        }

        if (v == this) {

            result = 0;

        } else {

            String s0 = toCompareString(this);
            String s1 = toCompareString(v);
            if ((s0 != null) && (s1 != null)) {

                result = s0.compareTo(s1);
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


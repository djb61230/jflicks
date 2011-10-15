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
package org.jflicks.autoart;

import java.io.Serializable;

/**
 * This class contains all the properties representing an art search that
 * needs to be done.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SearchItem implements Serializable, Comparable<SearchItem> {

    private String id;
    private String fileId;
    private String title;
    private String bannerURL;
    private String posterURL;
    private String fanartURL;
    private String videoId;
    private int season;
    private int episode;
    private String overview;
    private String released;
    private String genre;
    private int runtime;
    private boolean needBanner;
    private boolean needPoster;
    private boolean needFanart;
    private boolean needMetadata;
    private long lastCheck;

    /**
     * Simple empty constructor.
     */
    public SearchItem() {
    }

    public SearchItem(SearchItem si) {

        if (si != null) {

            setId(si.getId());
            setFileId(si.getFileId());
            setTitle(si.getTitle());
            setBannerURL(si.getBannerURL());
            setPosterURL(si.getPosterURL());
            setFanartURL(si.getFanartURL());
            setNeedBanner(si.isNeedBanner());
            setNeedPoster(si.isNeedPoster());
            setNeedFanart(si.isNeedFanart());
            setNeedMetadata(si.isNeedMetadata());
            setVideoId(si.getVideoId());
            setSeason(si.getSeason());
            setEpisode(si.getEpisode());
            setOverview(si.getOverview());
            setReleased(si.getReleased());
            setGenre(si.getGenre());
            setRuntime(si.getRuntime());
            setLastCheck(si.getLastCheck());
        }
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

    public String getFileId() {
        return (fileId);
    }

    public void setFileId(String s) {
        fileId = s;
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

    public boolean isNeedBanner() {
        return (needBanner);
    }

    public void setNeedBanner(boolean b) {
        needBanner = b;
    }

    public boolean isNeedPoster() {
        return (needPoster);
    }

    public void setNeedPoster(boolean b) {
        needPoster = b;
    }

    public boolean isNeedFanart() {
        return (needFanart);
    }

    public void setNeedFanart(boolean b) {
        needFanart = b;
    }

    public boolean isNeedMetadata() {
        return (needMetadata);
    }

    public void setNeedMetadata(boolean b) {
        needMetadata = b;
    }

    /**
     * A video ID is used to backfill some metadata.  We only do this for
     * Video items.
     *
     * @return An ID value as a String.
     */
    public String getVideoId() {
        return (videoId);
    }

    /**
     * A video ID is used to backfill some metadata.  We only do this for
     * Video items.
     *
     * @param s An ID value as a String.
     */
    public void setVideoId(String s) {
        videoId = s;
    }

    public int getSeason() {
        return (season);
    }

    public void setSeason(int i) {
        season = i;
    }

    public int getEpisode() {
        return (episode);
    }

    public void setEpisode(int i) {
        episode = i;
    }

    public int getRuntime() {
        return (runtime);
    }

    public void setRuntime(int i) {
        runtime = i;
    }

    /**
     * There is a Overview property.
     *
     * @return The Overview text.
     */
    public String getOverview() {
        return (overview);
    }

    /**
     * There is a Overview property.
     *
     * @param s The Overview text.
     */
    public void setOverview(String s) {
        overview = s;
    }

    /**
     * There is a Released property.
     *
     * @return The Released value.
     */
    public String getReleased() {
        return (released);
    }

    /**
     * There is a Released property.
     *
     * @param s The Released value.
     */
    public void setReleased(String s) {
        released = s;
    }

    /**
     * There is a Genre property if we have a Video that is a Movie.
     *
     * @return The Genre value.
     */
    public String getGenre() {
        return (genre);
    }

    /**
     * There is a Genre property if we have a Video that is a Movie.
     *
     * @param s The Genre value.
     */
    public void setGenre(String s) {
        genre = s;
    }

    /**
     * We don't want to swamp the Internet fan art sites so here we can
     * mitigate how often we actually check a particular item.
     *
     * @return A time as a long value.
     */
    public long getLastCheck() {
        return (lastCheck);
    }

    /**
     * We don't want to swamp the Internet fan art sites so here we can
     * mitigate how often we actually check a particular item.
     *
     * @param l A time as a long value.
     */
    public void setLastCheck(long l) {
        lastCheck = l;
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

        } else if (!(o instanceof SearchItem)) {

            result = false;

        } else {

            SearchItem si = (SearchItem) o;
            String s = si.getId();
            if (s != null) {

                result = s.equals(getId());
            }
        }

        return (result);
    }

    /**
     * The comparable interface.
     *
     * @param si The given SearchItem instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(SearchItem si) throws ClassCastException {

        int result = 0;

        if (si == null) {

            throw new NullPointerException();
        }

        if (si == this) {

            result = 0;

        } else {

            String id0 = getId();
            String id1 = si.getId();
            result = id0.compareTo(id1);
        }

        return (result);
    }

}


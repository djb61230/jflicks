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
package org.jflicks.tv.programdata.sd.json;

import java.io.Serializable;

/**
 * A class to capture the JSON defining a rating.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Program implements Serializable {

    private String programID;
    private String airDateTime;
    private String originalDateTime;
    private int duration;
    private String md5;
    private String[] audioProperties;
    private String[] videoProperties;
    private Rating[] ratings;
    private String[] genres;
    private String showType;
    private String episodeTitle150;;
    private Title[] titles;
    private EventDetails eventDetails;
    private Descriptions descriptions;
    private Crew[] crew;
    //private Metadata metadata;
    private boolean marked;

    /**
     * Simple empty constructor.
     */
    public Program() {
    }

    public String getProgramID() {
        return (programID);
    }

    public void setProgramID(String s) {
        programID = s;
    }

    public String getAirDateTime() {
        return (airDateTime);
    }

    public void setAirDateTime(String s) {
        airDateTime = s;
    }

    public String getOriginalDateTime() {
        return (originalDateTime);
    }

    public void setOriginalDateTime(String s) {
        originalDateTime = s;
    }

    public int getDuration() {
        return (duration);
    }

    public void setDuration(int i) {
        duration = i;
    }

    public String getMd5() {
        return (md5);
    }

    public void setMd5(String s) {
        md5 = s;
    }

    public String[] getAudioProperties() {
        return (audioProperties);
    }

    public void setAudioProperties(String[] array) {
        audioProperties = array;
    }

    public String[] getVideoProperties() {
        return (videoProperties);
    }

    public void setVideoProperties(String[] array) {
        videoProperties = array;
    }

    public Rating[] getRatings() {
        return (ratings);
    }

    public void setRatings(Rating[] array) {
        ratings = array;
    }

    public String[] getGenres() {
        return (genres);
    }

    public void setGenres(String[] array) {
        genres = array;
    }

    public String getShowType() {
        return (showType);
    }

    public void setShowType(String s) {
        showType = s;
    }

    public String getEpisodeTitle150() {
        return (episodeTitle150);
    }

    public void setEpisodeTitle150(String s) {
        episodeTitle150 = s;
    }

    public Title[] getTitles() {
        return (titles);
    }

    public void setTitles(Title[] array) {
        titles = array;
    }

    public EventDetails getEventDetails() {
        return (eventDetails);
    }

    public void setEventDetails(EventDetails ed) {
        eventDetails = ed;
    }

    public Descriptions getDescriptions() {
        return (descriptions);
    }

    public void setDescriptions(Descriptions d) {
        descriptions = d;
    }

    public Crew[] getCrew() {
        return (crew);
    }

    public void setCrew(Crew[] array) {
        crew = array;
    }

    /*
    public Metadata getMetadata() {
        return (metadata);
    }

    public void setMetadata(Metadata m) {
        metadata = m;
    }
    */

    public boolean isMarked() {
        return (marked);
    }

    public void setMarked(boolean b) {
        marked = b;
    }

    public String getBestTitle() {

        String result = "Unknown Title";

        Title[] all = getTitles();
        if ((all != null) && (all.length > 0)) {

            Title t = all[0];
            result = t.getTitle120();
        }

        return (result);
    }

    public String getBestDescription() {

        String result = "Unknown Description";

        Descriptions d = getDescriptions();
        if (d != null) {

            Description1000[] d1000 = d.getDescription1000();
            if ((d1000 != null) && (d1000.length > 0)) {

                result = d1000[0].getDescription();

            } else {

                Description100[] d100 = d.getDescription100();
                if ((d100 != null) && (d100.length > 0)) {

                    result = d100[0].getDescription();
                }
            }
        }

        return (result);
    }

    public String getSeriesId() {

        String result = getProgramID();

        if ((result != null) && (result.length() > 10)) {

            result = result.substring(0, 10);
        }

        return (result);
    }

    /*
    public String getEpisode() {

        String result = null;

        Metadata m = getMetadata();
        if (m != null) {

            Tribune t = m.getTribune();
            if (t != null) {

                int season = t.getSeason();
                int episode = t.getEpisode();
                if ((season > 0) && (episode > 0)) {

                    StringBuilder sb = new StringBuilder("S");
                    if (season < 10) {
                        sb.append(0);
                    }
                    sb.append(season);
                    sb.append("E");
                    if (episode < 10) {
                        sb.append(0);
                    }
                    sb.append(episode);

                    result = sb.toString();
                }
            }
        }

        return (result);
    }
    */

}


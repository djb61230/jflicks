/*
    This file is part of ATM.

    ATM is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ATM is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ATM.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.jflicks.metadata.themoviedb;

import java.io.Serializable;
import java.net.URI;

import com.google.gson.annotations.SerializedName;

/**
 * This is an object that encapsulates the information about a movie
 * available from themoviedb.org.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Movie implements Serializable {

    private boolean adult;
    @SerializedName("backdrop_path")
    private URI backdropPath;
    private int id;
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("poster_path")
    private URI posterPath;
    private double popularity;
    private String title;
    @SerializedName("vote_average")
    private double voteAverage;
    @SerializedName("vote_count")
    private int voteCount;
    @SerializedName("belongs_to_collection")
    private String belongsToCollection;
    private int budget;
    private String homepage;
    @SerializedName("imdb_id")
    private String imdbId;
    private String overview;
    private int revenue;
    private int runtime;
    private String status;
    private String tagline;
    private Genre[] genres;
    private Artwork artwork;

    /**
     * Empty constructor.
     */
    public Movie() {
    }

    public boolean isAdult() {
        return (adult);
    }

    public void setAdult(boolean b) {
        adult = b;
    }

    public URI getBackdropPath() {
        return (backdropPath);
    }

    public void setBackdropPath(URI s) {
        backdropPath = s;
    }

    public int getId() {
        return (id);
    }

    public void setId(int i) {
        id = i;
    }

    public String getOriginalTitle() {
        return (originalTitle);
    }

    public void setOriginalTitle(String s) {
        originalTitle = s;
    }

    public String getReleaseDate() {
        return (releaseDate);
    }

    public void setReleaseDate(String s) {
        releaseDate = s;
    }

    public URI getPosterPath() {
        return (posterPath);
    }

    public void setPosterPath(URI s) {
        posterPath = s;
    }

    public double getPopularity() {
        return (popularity);
    }

    public void setPopularity(double d) {
        popularity = d;
    }

    public String getTitle() {
        return (title);
    }

    public void setTitle(String s) {
        title = s;
    }

    public double getVoteAverage() {
        return (voteAverage);
    }

    public void setVoteAverage(double d) {
        voteAverage = d;
    }

    public int getVoteCount() {
        return (voteCount);
    }

    public void setVoteCount(int i) {
        voteCount = i;
    }

    public String getBelongsToCollection() {
        return (belongsToCollection);
    }

    public void setBelongsToCollection(String s) {
        belongsToCollection = s;
    }

    public int getBudget() {
        return (budget);
    }

    public void setBudget(int i) {
        budget = i;
    }

    public String getHomepage() {
        return (homepage);
    }

    public void setHomepage(String s) {
        homepage = s;
    }

    public String getImdbId() {
        return (imdbId);
    }

    public void setImdbId(String s) {
        imdbId = s;
    }

    public String getOverview() {
        return (overview);
    }

    public void setOverview(String s) {
        overview = s;
    }

    public int getRevenue() {
        return (revenue);
    }

    public void setRevenue(int i) {
        revenue = i;
    }

    public int getRuntime() {
        return (runtime);
    }

    public void setRuntime(int i) {
        runtime = i;
    }

    public String getStatus() {
        return (status);
    }

    public void setStatus(String s) {
        status = s;
    }

    public String getTagline() {
        return (tagline);
    }

    public void setTagline(String s) {
        tagline = s;
    }

    public Genre[] getGenres() {
        return (genres);
    }

    public void setGenres(Genre[] array) {
        genres = array;
    }

    public Artwork getArtwork() {
        return (artwork);
    }

    public void setArtwork(Artwork a) {
        artwork = a;
    }

    public boolean hasThumbnails() {

        boolean result = false;

        Artwork art = getArtwork();
        if (art != null) {

            result = (art.getPosters() != null) || (art.getBackdrops() != null);
        }

        return (result);
    }

    public String toString() {
        return (getTitle());
    }

}


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

import com.google.gson.annotations.SerializedName;

/**
 * This class captures the Image information available from themoviedb.org.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Image {

    private String type;
    private String urlThumb;
    private String url;
    @SerializedName("file_path")
    private String filePath;
    private int width;
    private int height;
    @SerializedName("aspect_ratio")
    private double aspectRatio;
    @SerializedName("vote_average")
    private double voteAverage;
    @SerializedName("vote_count")
    private int voteCount;

    /**
     * Empty constructor.
     */
    public Image() {
    }

    public String getType() {
        return (type);
    }

    public void setType(String s) {
        type = s;
    }

    public String getUrl() {
        return (url);
    }

    public void setUrl(String s) {
        url = s;
    }

    public String getUrlThumb() {
        return (urlThumb);
    }

    public void setUrlThumb(String s) {
        urlThumb = s;
    }

    public String getFilePath() {
        return (filePath);
    }

    public void setFilePath(String s) {
        filePath = s;
    }

    public int getWidth() {
        return (width);
    }

    public void setWidth(int i) {
        width = i;
    }

    public int getHeight() {
        return (height);
    }

    public void setHeight(int i) {
        height = i;
    }

    public double getAspectRatio() {
        return (aspectRatio);
    }

    public void setAspectRatio(double d) {
        aspectRatio = d;
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

    /**
     * Override this method to return the file path property.
     *
     * @return The file path as a String.
     */
    public String toString() {

        String result = "Unknown";

        if (filePath != null) {

            result = getType() + " - "
                + filePath.substring(1, filePath.indexOf("."));
        }

        return (result);
    }

}


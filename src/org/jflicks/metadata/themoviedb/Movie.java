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

import java.util.ArrayList;

import org.jflicks.util.Util;

import org.jdom.Element;

/**
 * This is an object that encapsulates the information about a movie
 * available from themoviedb.org.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Movie extends BaseObject {

    private double score;
    private int popularity;
    private String name;
    private String alternativeName;
    private String type;
    private String id;
    private String imdbId;
    private String url;
    private double rating;
    private String overview;
    private String released;
    private String genre;
    private int runtime;
    private ArrayList<Image> imageList;

    /**
     * Constructor with the required argument.
     *
     * @param e The element that will be examined for data.
     */
    public Movie(Element e) {

        super(e);
    }

    /**
     * A movie has a score property.
     *
     * @return The score as a double value.
     */
    public double getScore() {
        return (score);
    }

    private void setScore(double d) {
        score = d;
    }

    /**
     * A movie has a popularity property.
     *
     * @return The popularity as an int value.
     */
    public int getPopularity() {
        return (popularity);
    }

    private void setPopularity(int i) {
        popularity = i;
    }

    /**
     * A movie has a name property.
     *
     * @return The name as a String value.
     */
    public String getName() {
        return (name);
    }

    private void setName(String s) {
        name = s;
    }

    /**
     * A movie has a alternative name property.
     *
     * @return The alternative name as a String value.
     */
    public String getAlternativeName() {
        return (alternativeName);
    }

    private void setAlternativeName(String s) {
        alternativeName = s;
    }

    /**
     * A movie has a type property.
     *
     * @return The type as a String value.
     */
    public String getType() {
        return (type);
    }

    private void setType(String s) {
        type = s;
    }

    /**
     * A movie has an ID property.
     *
     * @return The ID as a String value.
     */
    public String getId() {
        return (id);
    }

    private void setId(String s) {
        id = s;
    }

    /**
     * A movie has an ImdbID property.
     *
     * @return The ImdbID as a String value.
     */
    public String getImdbId() {
        return (imdbId);
    }

    private void setImdbId(String s) {
        imdbId = s;
    }

    /**
     * A movie has an URL property.
     *
     * @return The URL as a String value.
     */
    public String getUrl() {
        return (url);
    }

    private void setUrl(String s) {
        url = s;
    }

    /**
     * A movie has a rating property.
     *
     * @return The rating as a double value.
     */
    public double getRating() {
        return (rating);
    }

    private void setRating(double d) {
        rating = d;
    }

    /**
     * A movie has an overview property.
     *
     * @return The overview as a String value.
     */
    public String getOverview() {
        return (overview);
    }

    private void setOverview(String s) {
        overview = s;
    }

    /**
     * A movie has a released property.
     *
     * @return The released as a String value.
     */
    public String getReleased() {
        return (released);
    }

    private void setReleased(String s) {
        released = s;
    }

    /**
     * A movie has a genre property.  The web site has a notion of
     * categories and we will stick the first one we find that is a genre
     * in this property.  The problem is movies can be in multiple genres.
     *
     * @return The genre as a String value.
     */
    public String getGenre() {
        return (genre);
    }

    private void setGenre(String s) {
        genre = s;
    }

    /**
     * A movie has a runtime property.  This is the number of minutes the
     * movie runs.
     *
     * @return The runtime as an int value.
     */
    public int getRuntime() {
        return (runtime);
    }

    private void setRuntime(int i) {
        runtime = i;
    }

    private ArrayList<Image> getImageList() {
        return (imageList);
    }

    private void setImageList(ArrayList<Image> l) {
        imageList = l;
    }

    private void addImage(Image m) {

        ArrayList<Image> l = getImageList();
        if ((l != null) && (m != null)) {

            l.add(m);
        }
    }

    private void removeImage(Image p) {

        ArrayList<Image> l = getImageList();
        if ((l != null) && (p != null)) {

            l.remove(p);
        }
    }

    private void clear() {

        ArrayList<Image> l = getImageList();
        if (l != null) {

            l.clear();
        }
    }

    /**
     * A movie has a set of images associated with it.
     *
     * @return An array of Image instances.
     */
    public Image[] getImages() {

        Image[] result = null;

        ArrayList<Image> l = getImageList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Image[l.size()]);
        }

        return (result);
    }

    private void setImages(Image[] array) {

        clear();
        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                addImage(array[i]);
            }
        }
    }

    /**
     * Convenience method to find the Image count.
     *
     * @return The number of Images for this movie.
     */
    public int getImageCount() {

        int result = 0;

        ArrayList<Image> l = getImageList();
        if ((l != null) && (l.size() > 0)) {

            result = l.size();
        }

        return (result);
    }

    /**
     * Convenience method to get an Image at a given index.
     *
     * @param index A given index.
     * @return The Image if it exists at the given index.
     */
    public Image getImageAt(int index) {

        Image result = null;

        ArrayList<Image> l = getImageList();
        if ((l != null) && (l.size() > index)) {

            result = l.get(index);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void handle() {

        setScore(Util.str2double(expectElement(getElement(), "score"), 0.0));
        setPopularity(Util.str2int(expectElement(getElement(), "popularity"),
            0));
        setName(expectElement(getElement(), "name"));
        setAlternativeName(expectElement(getElement(), "alternative_name"));
        setType(expectElement(getElement(), "type"));
        setId(expectElement(getElement(), "id"));
        setImdbId(expectElement(getElement(), "imdb_id"));
        setUrl(expectElement(getElement(), "url"));
        setScore(Util.str2double(expectElement(getElement(), "rating"), 0.0));
        setOverview(expectElement(getElement(), "overview"));
        setReleased(expectElement(getElement(), "released"));

        Element[] cats = expectElements(getElement(), "categories", "category");
        if ((cats != null) && (cats.length > 0)) {

            for (int i = 0; i < cats.length; i++) {

                String ctype = cats[i].getAttributeValue("type");
                if ((ctype != null) && (ctype.equalsIgnoreCase("genre"))) {

                    setGenre(cats[i].getAttributeValue("name"));
                    break;
                }
            }
        }

        setRuntime(Util.str2int(expectElement(getElement(), "runtime"), 0));

        setImageList(new ArrayList<Image>());

        Element[] array = expectElements(getElement(), "images", "image");
        if ((array != null) && (array.length > 0)) {

            for (int i = 0; i < array.length; i++) {

                addImage(new Image(array[i]));
            }
        }
    }

    /**
     * Only the Images that are thumbnails are returned.
     *
     * @return An array of thumbnail images.
     */
    public Image[] getThumbnailImages() {

        Image[] result = null;

        Image[] all = getImages();
        if (all != null) {

            ArrayList<Image> l = new ArrayList<Image>();
            for (int i = 0; i < all.length; i++) {

                Image tmp = all[i];
                if ((tmp != null) && (tmp.isThumbSize())) {
                    l.add(tmp);
                }
            }

            if (l.size() > 0) {

                result = l.toArray(new Image[l.size()]);
            }
        }

        return (result);
    }

    /**
     * Given an ID find the Image that is "poster" sized.
     *
     * @param id The given Image ID.
     * @return An Image instance.
     */
    public Image getPosterSizeImageById(String id) {

        Image result = null;

        Image[] all = getImages();
        if ((all != null) && (id != null)) {

            for (int i = 0; i < all.length; i++) {

                Image tmp = all[i];
                if ((tmp != null) && (tmp.isPosterSize())
                    && (id.equalsIgnoreCase(tmp.getId()))) {

                    result = tmp;
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * Given an ID find the Image that is "original" sized.
     *
     * @param id The given Image ID.
     * @return An Image instance.
     */
    public Image getOriginalSizeImageById(String id) {

        Image result = null;

        Image[] all = getImages();
        if ((all != null) && (id != null)) {

            for (int i = 0; i < all.length; i++) {

                Image tmp = all[i];
                if ((tmp != null) && (tmp.isOriginalSize())
                    && (id.equalsIgnoreCase(tmp.getId()))) {

                    result = tmp;
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * Given an ID find the Image that is "mid" sized.
     *
     * @param id The given Image ID.
     * @return An Image instance.
     */
    public Image getMidSizeImageById(String id) {

        Image result = null;

        Image[] all = getImages();
        if ((all != null) && (id != null)) {

            for (int i = 0; i < all.length; i++) {

                Image tmp = all[i];
                if ((tmp != null) && (tmp.isMidSize())
                    && (id.equalsIgnoreCase(tmp.getId()))) {

                    result = tmp;
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * Given an ID find the Image that is "cover" sized.
     *
     * @param id The given Image ID.
     * @return An Image instance.
     */
    public Image getCoverSizeImageById(String id) {

        Image result = null;

        Image[] all = getImages();
        if ((all != null) && (id != null)) {

            for (int i = 0; i < all.length; i++) {

                Image tmp = all[i];
                if ((tmp != null) && (tmp.isCoverSize())
                    && (id.equalsIgnoreCase(tmp.getId()))) {

                    result = tmp;
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * Does this Movie have thumbnail images?.
     *
     * @return True if at least one thumbnail image exists.
     */
    public boolean hasThumbnails() {
        return (getThumbnailImages() != null);
    }

    /**
     * Override this method to return the name property.
     *
     * @return The name property as a String.
     */
    public String toString() {
        return (getName());
    }

}


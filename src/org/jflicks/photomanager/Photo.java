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
package org.jflicks.photomanager;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import org.jflicks.util.RandomGUID;

/**
 * This class contains all the properties representing a photo.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Photo implements Serializable, Comparable<Photo> {

    private String id;
    private String path;
    private Date date;
    private String[] tagPaths;

    /**
     * Simple empty constructor.
     */
    public Photo() {

        setId(RandomGUID.createGUID());
    }

    /**
     * Constructor to "clone" a Photo instance.
     *
     * @param p A given Photo.
     */
    public Photo(Photo p) {

        setId(p.getId());
        setPath(p.getPath());
        setDate(p.getDate());
        setTagPaths(p.getTagPaths());
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
     * The path of the photo.
     *
     * @return The path as a String instance.
     */
    public String getPath() {
        return (path);
    }

    /**
     * The path of the photo.
     *
     * @param s The path as a String instance.
     */
    public void setPath(String s) {
        path = s;
    }

    /**
     * When the photo occurred.
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
     * When the photo occurred.
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
     * An array of Tag objects that describe the Photo.
     *
     * @return An array of Tag instances.
     */
    public String[] getTagPaths() {

        String[] result = null;

        if (tagPaths != null) {

            result = Arrays.copyOf(tagPaths, tagPaths.length);
        }

        return (result);
    }

    /**
     * An array of Tag path String objects that describe the Photo.
     *
     * @param array An array of Tag path instances.
     */
    public void setTagPaths(String[] array) {

        if (array != null) {
            tagPaths = Arrays.copyOf(array, array.length);
        } else {
            tagPaths = null;
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
     * @param o A gven object to check.
     * @return True if the objects are equal.
     */
    public boolean equals(Object o) {

        boolean result = false;

        if (o == this) {

            result = true;

        } else if (!(o instanceof Photo)) {

            result = false;

        } else {

            Photo p = (Photo) o;
            String s = getId();
            if (s != null) {

                result = s.equals(p.getId());
            }
        }

        return (result);
    }

    /**
     * The comparable interface.
     *
     * @param p The given Photo instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(Photo p) throws ClassCastException {

        int result = 0;

        if (p == null) {

            throw new NullPointerException();
        }

        if (p == this) {

            result = 0;

        } else {

            Date date0 = getDate();
            Date date1 = p.getDate();
            if ((date0 != null) && (date1 != null)) {

                result = date1.compareTo(date0);
            }
        }

        return (result);
    }

    /**
     * Override by returning the Path property.
     *
     * @return The Path property.
     */
    public String toString() {
        return (getPath());
    }

}


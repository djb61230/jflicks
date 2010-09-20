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

import org.jdom.Element;

/**
 * This class captures the Image information available from themoviedb.org.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Image extends BaseObject {

    private String type;
    private String url;
    private String size;
    private String id;

    /**
     * Constructor with the required argument.
     *
     * @param e The element that will be examined for data.
     */
    public Image(Element e) {

        super(e);
    }

    /**
     * An image has a type property.
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
     * An image has an URL property.
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
     * An image has a size property.
     *
     * @return The size as a String value.
     */
    public String getSize() {
        return (size);
    }

    private void setSize(String s) {
        size = s;
    }

    /**
     * An image has an ID property.
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
     * {@inheritDoc}
     */
    public void handle() {

        setType(expectAttribute(getElement(), "type"));
        setUrl(expectAttribute(getElement(), "url"));
        setSize(expectAttribute(getElement(), "size"));
        setId(expectAttribute(getElement(), "id"));
    }

    /**
     * Is this Image a "thumbnail" size?
     *
     * @return True if it is a thumbnail.
     */
    public boolean isThumbSize() {

        boolean result = false;

        String s = getSize();
        if ((s != null) && (s.equalsIgnoreCase("thumb"))) {
            result = true;
        }

        return (result);
    }

    /**
     * Is this Image a "cover" size?
     *
     * @return True if it is a cover.
     */
    public boolean isCoverSize() {

        boolean result = false;

        String s = getSize();
        if ((s != null) && (s.equalsIgnoreCase("cover"))) {
            result = true;
        }

        return (result);
    }

    /**
     * Is this Image a "mid" size?
     *
     * @return True if it is a mid.
     */
    public boolean isMidSize() {

        boolean result = false;

        String s = getSize();
        if ((s != null) && (s.equalsIgnoreCase("mid"))) {
            result = true;
        }

        return (result);
    }

    /**
     * Is this Image an "original" size?
     *
     * @return True if it is a original.
     */
    public boolean isOriginalSize() {

        boolean result = false;

        String s = getSize();
        if ((s != null) && (s.equalsIgnoreCase("original"))) {
            result = true;
        }

        return (result);
    }

    /**
     * Is this Image a "poster" size?
     *
     * @return True if it is a poster.
     */
    public boolean isPosterSize() {

        boolean result = false;

        String s = getSize();
        if ((s != null) && (s.equalsIgnoreCase("poster"))) {
            result = true;
        }

        return (result);
    }

    /**
     * Is this Image a "backdrop" type?
     *
     * @return True if it is a backdrop.
     */
    public boolean isBackdropType() {

        boolean result = false;

        String s = getType();
        if ((s != null) && (s.equalsIgnoreCase("backdrop"))) {
            result = true;
        }

        return (result);
    }

    /**
     * Is this Image a "poster" type?
     *
     * @return True if it is a poster.
     */
    public boolean isPosterType() {

        boolean result = false;

        String s = getType();
        if ((s != null) && (s.equalsIgnoreCase("poster"))) {
            result = true;
        }

        return (result);
    }

    /**
     * Override this method to return the type and ID properties.
     *
     * @return The type and ID properties as a String.
     */
    public String toString() {
        return (getType() + " - " + getId());
    }

}


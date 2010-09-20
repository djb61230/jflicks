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
package org.jflicks.ui.view.fe;

import java.awt.image.BufferedImage;

import org.jflicks.util.RandomGUID;

/**
 * Simple bean that has two properties, a string property called Text and
 * a Swing Icon.  It's used by the TextImagePanel to simulate a typical
 * mythTV type menu screen.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class TextImage {

    private String id;
    private String text;
    private BufferedImage image;
    private TextImage parentTextImage;

    /**
     * Simple constructor with two properties maintained by this bean.
     *
     * @param text The text value for this bean.
     * @param image A background image when selected.
     */
    public TextImage(String text, BufferedImage image) {

        setId(RandomGUID.createGUID());
        setText(text);
        setImage(image);
    }

    /**
     * Simple constructor with two properties maintained by this bean.
     *
     * @param text The text value for this bean.
     * @param parent Instead of a "full" item, it can be a "sub" item.
     */
    public TextImage(String text, TextImage parent) {

        setId(RandomGUID.createGUID());
        setText(text);
        setParentTextImage(parent);
    }

    /**
     * A TextImage has a unique Id associated with it.  This can be assigned
     * or auto generated.
     *
     * @return The Id as a String.
     */
    public String getId() {
        return (id);
    }

    /**
     * A TextImage has a unique Id associated with it.  This can be assigned
     * or auto generated.
     *
     * @param s The Id as a String.
     */
    public void setId(String s) {
        id = s;
    }

    /**
     * The text value of this bean.
     *
     * @return The value of the text property as a String object.
     */
    public String getText() {
        return (text);
    }

    /**
     * The text value of this bean.
     *
     * @param s The value of the text property as a String object.
     */
    public void setText(String s) {
        text = s;
    }

    /**
     * An image that can be drawn as a background.
     *
     * @return An image used as a background.
     */
    public BufferedImage getImage() {
        return (image);
    }

    /**
     * An image that can be drawn as a background.
     *
     * @param bi An image used as a background.
     */
    public void setImage(BufferedImage bi) {
        image = bi;
    }

    /**
     * This item is the child of another TextImage.
     *
     * @return The parent TextImage instance.
     */
    public TextImage getParentTextImage() {
        return (parentTextImage);
    }

    /**
     * This item is the child of another TextImage.
     *
     * @param ti The parent TextImage instance.
     */
    public void setParentTextImage(TextImage ti) {
        parentTextImage = ti;
    }

    /**
     * The standard hashcode override.
     *
     * @return An int value.
     */
    public int hashCode() {
        return (getText().hashCode());
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

        } else if (!(o instanceof TextImage)) {

            result = false;

        } else {

            TextImage ti = (TextImage) o;
            String s = getText();
            if (s != null) {

                result = s.equals(ti.getText());
            }
        }

        return (result);
    }

}

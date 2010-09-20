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
import javax.swing.Icon;

import org.jflicks.util.RandomGUID;

/**
 * Simple bean that has two properties, a string property called Text and
 * a Swing Icon.  It's used by the TextIconPanel to simulate a typical
 * mythTV type menu screen.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class TextIcon {

    private String id;
    private String text;
    private Icon icon;
    private Icon selectedIcon;
    private BufferedImage backgroundImage;
    private BufferedImage selectedBackgroundImage;

    /**
     * Simple constructor with two properties maintained by this bean.
     *
     * @param text The text value for this bean.
     * @param icon The icon associated with the text property.
     */
    public TextIcon(String text, Icon icon) {

        setId(RandomGUID.createGUID());
        setText(text);
        setIcon(icon);
    }

    /**
     * Constructor with three properties maintained by this bean.
     *
     * @param text The text value for this bean.
     * @param icon The icon associated with the text property.
     * @param selectedIcon The selected icon associated with the text property.
     */
    public TextIcon(String text, Icon icon, Icon selectedIcon) {

        this(text, icon);
        setSelectedIcon(selectedIcon);
    }

    /**
     * Constructor with three properties maintained by this bean.
     *
     * @param text The text value for this bean.
     * @param backgroundImage A background image.
     * @param selectedBackgroundImage A background image when selected.
     */
    public TextIcon(String text, BufferedImage backgroundImage,
        BufferedImage selectedBackgroundImage) {

        this(text, null);
        setBackgroundImage(backgroundImage);
        setSelectedBackgroundImage(selectedBackgroundImage);
    }

    /**
     * Constructor with the five properties maintained by this bean.
     *
     * @param text The text value for this bean.
     * @param icon The icon associated with the text property.
     * @param selectedIcon The selected icon associated with the text property.
     * @param backgroundImage A background image.
     * @param selectedBackgroundImage A background image when selected.
     */
    public TextIcon(String text, Icon icon, Icon selectedIcon,
        BufferedImage backgroundImage, BufferedImage selectedBackgroundImage) {

        this(text, icon, selectedIcon);
        setBackgroundImage(backgroundImage);
        setSelectedBackgroundImage(selectedBackgroundImage);
    }

    /**
     * A TextIcon has a unique Id associated with it.  This can be assigned
     * or auto generated.
     *
     * @return The Id as a String.
     */
    public String getId() {
        return (id);
    }

    /**
     * A TextIcon has a unique Id associated with it.  This can be assigned
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
     * The icon object of this bean.
     *
     * @return The Icon instance associated with this object.
     */
    public Icon getIcon() {
        return (icon);
    }

    /**
     * The icon object of this bean.
     *
     * @param i The Icon instance associated with this object.
     */
    public void setIcon(Icon i) {
        icon = i;
    }

    /**
     * The "selected" icon object of this bean.
     *
     * @return The "selected" Icon instance associated with this object.
     */
    public Icon getSelectedIcon() {
        return (selectedIcon);
    }

    /**
     * The "selected" icon object of this bean.
     *
     * @param i The "selected" Icon instance associated with this object.
     */
    public void setSelectedIcon(Icon i) {
        selectedIcon = i;
    }

    /**
     * An image that can be drawn as a background.
     *
     * @return An image used as a background.
     */
    public BufferedImage getBackgroundImage() {
        return (backgroundImage);
    }

    /**
     * An image that can be drawn as a background.
     *
     * @param bi An image used as a background.
     */
    public void setBackgroundImage(BufferedImage bi) {
        backgroundImage = bi;
    }

    /**
     * An image that can be drawn as a background if selected.
     *
     * @return An image used as a background if selected.
     */
    public BufferedImage getSelectedBackgroundImage() {
        return (selectedBackgroundImage);
    }

    /**
     * An image that can be drawn as a background if selected.
     *
     * @param bi An image used as a background if selected.
     */
    public void setSelectedBackgroundImage(BufferedImage bi) {
        selectedBackgroundImage = bi;
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

        } else if (!(o instanceof TextIcon)) {

            result = false;

        } else {

            TextIcon ti = (TextIcon) o;
            String s = getText();
            if (s != null) {

                result = s.equals(ti.getText());
            }
        }

        return (result);
    }

}

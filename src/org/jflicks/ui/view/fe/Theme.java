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

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;

import org.jflicks.util.RandomGUID;

/**
 * Simple container class for our UI themes.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Theme implements Comparable<Theme>, Customize {

    private String id;
    private String title;
    private String description;
    private TextIcon[] textIcons;

    private double smallFontSize;
    private double mediumFontSize;
    private double largeFontSize;
    private Font smallFont;
    private Font mediumFont;
    private Font largeFont;
    private String smallFontFamily;
    private String mediumFontFamily;
    private String largeFontFamily;
    private int smallFontStyle;
    private int mediumFontStyle;
    private int largeFontStyle;
    private Color unselectedColor;
    private Color selectedColor;
    private Color highlightColor;
    private Color infoColor;
    private Color panelColor;
    private double panelAlpha;

    /**
     * Simple empty constructor.
     */
    public Theme() {

        setId(RandomGUID.createGUID());
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
     * A Theme has an associated title.
     *
     * @return The theme title.
     */
    public String getTitle() {
        return (title);
    }

    /**
     * A Theme has an associated title.
     *
     * @param s The theme title.
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * A Theme has an associated description.
     *
     * @return The theme description.
     */
    public String getDescription() {
        return (description);
    }

    /**
     * A Theme has an associated description.
     *
     * @param s The theme description.
     */
    public void setDescription(String s) {
        description = s;
    }

    /**
     * The screen images are represented by TextIcon instances.
     *
     * @return An array of TextIcon instances.
     */
    public TextIcon[] getTextIcons() {

        TextIcon[] result = null;

        if (textIcons != null) {

            result = Arrays.copyOf(textIcons, textIcons.length);
        }

        return (result);
    }

    /**
     * The screen images are represented by TextIcon instances.
     *
     * @param array An array of TextIcon instances.
     */
    public void setTextIcons(TextIcon[] array) {

        if (array != null) {
            textIcons = Arrays.copyOf(array, array.length);
        } else {
            textIcons = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Font getSmallFont() {
        return (smallFont);
    }

    /**
     * {@inheritDoc}
     */
    public void setSmallFont(Font f) {
        smallFont = f;
    }

    /**
     * {@inheritDoc}
     */
    public Font getMediumFont() {
        return (mediumFont);
    }

    /**
     * {@inheritDoc}
     */
    public void setMediumFont(Font f) {
        mediumFont = f;
    }

    /**
     * {@inheritDoc}
     */
    public Font getLargeFont() {
        return (largeFont);
    }

    /**
     * {@inheritDoc}
     */
    public void setLargeFont(Font f) {
        largeFont = f;
    }

    /**
     * {@inheritDoc}
     */
    public double getSmallFontSize() {
        return (smallFontSize);
    }

    /**
     * {@inheritDoc}
     */
    public void setSmallFontSize(double d) {
        smallFontSize = d;
    }

    /**
     * {@inheritDoc}
     */
    public double getMediumFontSize() {
        return (mediumFontSize);
    }

    /**
     * {@inheritDoc}
     */
    public void setMediumFontSize(double d) {
        mediumFontSize = d;
    }

    /**
     * {@inheritDoc}
     */
    public double getLargeFontSize() {
        return (largeFontSize);
    }

    /**
     * {@inheritDoc}
     */
    public void setLargeFontSize(double d) {
        largeFontSize = d;
    }

    /**
     * {@inheritDoc}
     */
    public String getSmallFontFamily() {
        return (smallFontFamily);
    }

    /**
     * {@inheritDoc}
     */
    public void setSmallFontFamily(String s) {
        smallFontFamily = s;
    }

    /**
     * {@inheritDoc}
     */
    public String getMediumFontFamily() {
        return (mediumFontFamily);
    }

    /**
     * {@inheritDoc}
     */
    public void setMediumFontFamily(String s) {
        mediumFontFamily = s;
    }

    /**
     * {@inheritDoc}
     */
    public String getLargeFontFamily() {
        return (largeFontFamily);
    }

    /**
     * {@inheritDoc}
     */
    public void setLargeFontFamily(String s) {
        largeFontFamily = s;
    }

    /**
     * {@inheritDoc}
     */
    public int getSmallFontStyle() {
        return (smallFontStyle);
    }

    /**
     * {@inheritDoc}
     */
    public void setSmallFontStyle(int i) {
        smallFontStyle = i;
    }

    /**
     * {@inheritDoc}
     */
    public int getMediumFontStyle() {
        return (mediumFontStyle);
    }

    /**
     * {@inheritDoc}
     */
    public void setMediumFontStyle(int i) {
        mediumFontStyle = i;
    }

    /**
     * {@inheritDoc}
     */
    public int getLargeFontStyle() {
        return (largeFontStyle);
    }

    /**
     * {@inheritDoc}
     */
    public void setLargeFontStyle(int i) {
        largeFontStyle = i;
    }

    /**
     * {@inheritDoc}
     */
    public Color getUnselectedColor() {
        return (unselectedColor);
    }

    /**
     * {@inheritDoc}
     */
    public void setUnselectedColor(Color c) {
        unselectedColor = c;
    }

    /**
     * {@inheritDoc}
     */
    public Color getSelectedColor() {
        return (selectedColor);
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectedColor(Color c) {
        selectedColor = c;
    }

    /**
     * {@inheritDoc}
     */
    public Color getHighlightColor() {
        return (highlightColor);
    }

    /**
     * {@inheritDoc}
     */
    public void setHighlightColor(Color c) {
        highlightColor = c;
    }

    /**
     * {@inheritDoc}
     */
    public Color getInfoColor() {
        return (infoColor);
    }

    /**
     * {@inheritDoc}
     */
    public void setInfoColor(Color c) {
        infoColor = c;
    }

    /**
     * {@inheritDoc}
     */
    public Color getPanelColor() {
        return (panelColor);
    }

    /**
     * {@inheritDoc}
     */
    public void setPanelColor(Color c) {
        panelColor = c;
    }

    /**
     * {@inheritDoc}
     */
    public double getPanelAlpha() {
        return (panelAlpha);
    }

    /**
     * {@inheritDoc}
     */
    public void setPanelAlpha(double d) {
        panelAlpha = d;
    }

    /**
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

        } else if (!(o instanceof Theme)) {

            result = false;

        } else {

            Theme t = (Theme) o;
            String s = getId();
            if (s != null) {
                result = s.equals(t.getId());
            }
        }

        return (result);
    }

    /**
     * The comparable interface.
     *
     * @param t The given Theme instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(Theme t) throws ClassCastException {

        int result = 0;

        if (t == null) {

            throw new NullPointerException();
        }

        if (t == this) {

            result = 0;

        } else {

            String title0 = getTitle();
            String title1 = t.getTitle();
            if ((title0 != null) && (title1 != null)) {

                result = title0.compareTo(title1);
            }
        }

        return (result);
    }

    /**
     * Override to return Title.
     *
     * @return A String.
     */
    public String toString() {
        return (getTitle());
    }

}


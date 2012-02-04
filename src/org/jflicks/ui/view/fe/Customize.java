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

/**
 * This interface details all the supported customizable properties that
 * a class may want to allow.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Customize {

    /**
     * A default for a small font size (28.0).
     */
    double SMALL_FONT_SIZE = 22.0;

    /**
     * A default for a medium font size (34.0).
     */
    double MEDIUM_FONT_SIZE = 30.0;

    /**
     * A default for a large font size (38.0).
     */
    double LARGE_FONT_SIZE = 38.0;

    /**
     * A default for a small font family.  We use the default system "Dialog".
     */
    String SMALL_FONT_FAMILY = "Dialog";

    /**
     * A default for a medium font family.  We use the default system "Dialog".
     */
    String MEDIUM_FONT_FAMILY = "Dialog";

    /**
     * A default for a large font family.  We use the default system "Dialog".
     */
    String LARGE_FONT_FAMILY = "Dialog";

    /**
     * A default style for a small font.  We use the default "PLAIN".
     */
    int SMALL_FONT_STYLE = Font.PLAIN;

    /**
     * A default style for a medium font.  We use the default "PLAIN".
     */
    int MEDIUM_FONT_STYLE = Font.PLAIN;

    /**
     * A default style for a large font.  We use the default "PLAIN".
     */
    int LARGE_FONT_STYLE = Font.PLAIN;

    /**
     * A default for unselected color (Color.LIGHT_GRAY).
     */
    Color UNSELECTED_COLOR = Color.LIGHT_GRAY;

    /**
     * A default for selected color (Color.WHITE).
     */
    Color SELECTED_COLOR = Color.WHITE;

    /**
     * A default for the highlight color (Color.RED).
     */
    Color HIGHLIGHT_COLOR = Color.RED;

    /**
     * A default for the info popup text color (Color.BLACK).
     */
    Color INFO_COLOR = Color.BLACK;

    /**
     * A default for the panel color (Color.DARK_GRAY).
     */
    Color PANEL_COLOR = Color.DARK_GRAY;

    /**
     * A default value for the panel alpha (default 0.65).
     */
    double PANEL_ALPHA = 0.65;

    /**
     * A UI might have a notion of a selected color, to allow the user a way
     * to see an item is selected.
     *
     * @return A Color instance.
     */
    Color getSelectedColor();

    /**
     * A UI might have a notion of a selected color, to allow the user a way
     * to see an item is selected.
     *
     * @param c A Color instance.
     */
    void setSelectedColor(Color c);

    /**
     * A UI might have a notion of an unselected color, to allow the user a way
     * to see an item is selected.
     *
     * @return A Color instance.
     */
    Color getUnselectedColor();

    /**
     * A UI might have a notion of an unselected color, to allow the user a way
     * to see an item is selected.
     *
     * @param c A Color instance.
     */
    void setUnselectedColor(Color c);

    /**
     * Sometimes a UI component needs to be highlighted with a color.
     *
     * @return A Color instance.
     */
    Color getHighlightColor();

    /**
     * Sometimes a UI component needs to be highlighted with a color.
     *
     * @param c A Color instance.
     */
    void setHighlightColor(Color c);

    /**
     * Popup windows can have a different text color.
     *
     * @return A Color instance.
     */
    Color getInfoColor();

    /**
     * Popup windows can have a different text color.
     *
     * @param c A Color instance.
     */
    void setInfoColor(Color c);

    /**
     * A UI component that draws some sort of background can use the Panel
     * color property.
     *
     * @return A Color instance.
     */
    Color getPanelColor();

    /**
     * A UI component that draws some sort of background can use the Panel
     * color property.
     *
     * @param c A Color instance.
     */
    void setPanelColor(Color c);

    /**
     * The panel of the component has an alpha value.
     *
     * @return A double value in the range 0.0 - 1.0.
     */
    double getPanelAlpha();

    /**
     * The panel of the component has an alpha value.
     *
     * @param d A double value in the range 0.0 - 1.0.
     */
    void setPanelAlpha(double d);

    /**
     * Any UI component might have the need for a small font.
     *
     * @return A Font instance.
     */
    Font getSmallFont();

    /**
     * Any UI component might have the need for a small font.
     *
     * @param f A Font instance.
     */
    void setSmallFont(Font f);

    /**
     * Any UI component might have the need for a medium font.
     *
     * @return A Font instance.
     */
    Font getMediumFont();

    /**
     * Any UI component might have the need for a medium font.
     *
     * @param f A Font instance.
     */
    void setMediumFont(Font f);

    /**
     * Any UI component might have the need for a large font.
     *
     * @return A Font instance.
     */
    Font getLargeFont();

    /**
     * Any UI component might have the need for a large font.
     *
     * @param f A Font instance.
     */
    void setLargeFont(Font f);

    /**
     * The font has a small size.
     *
     * @return A double primitive for the small point size of the font.
     */
    double getSmallFontSize();

    /**
     * The font has a small size.
     *
     * @param d A double primitive for the small point size of the font.
     */
    void setSmallFontSize(double d);

    /**
     * The font has a medium size.
     *
     * @return A double primitive for the medium point size of the font.
     */
    double getMediumFontSize();

    /**
     * The font has a medium size.
     *
     * @param d A double primitive for the medium point size of the font.
     */
    void setMediumFontSize(double d);

    /**
     * The font has a large size.
     *
     * @return A double primitive for the large point size of the font.
     */
    double getLargeFontSize();

    /**
     * The font has a large size.
     *
     * @param d A double primitive for the large point size of the font.
     */
    void setLargeFontSize(double d);

    /**
     * The small font has a family name.
     *
     * @return A String instance.
     */
    String getSmallFontFamily();

    /**
     * The small font has a family name.
     *
     * @param s A String instance.
     */
    void setSmallFontFamily(String s);

    /**
     * The medium font has a family name.
     *
     * @return A String instance.
     */
    String getMediumFontFamily();

    /**
     * The medium font has a family name.
     *
     * @param s A String instance.
     */
    void setMediumFontFamily(String s);

    /**
     * The large font has a family name.
     *
     * @return A String instance.
     */
    String getLargeFontFamily();

    /**
     * The large font has a family name.
     *
     * @param s A String instance.
     */
    void setLargeFontFamily(String s);

    /**
     * The small font has a family name.
     *
     * @return An int value.
     */
    int getSmallFontStyle();

    /**
     * The small font has a style.
     *
     * @param i An int value.
     */
    void setSmallFontStyle(int i);

    /**
     * The medium font has a style.
     *
     * @return An int value.
     */
    int getMediumFontStyle();

    /**
     * The medium font has a style.
     *
     * @param i An int value.
     */
    void setMediumFontStyle(int i);

    /**
     * The large font has a family name.
     *
     * @return An int value.
     */
    int getLargeFontStyle();

    /**
     * The large font has a family name.
     *
     * @param i An int value.
     */
    void setLargeFontStyle(int i);

    /**
     * Preference of user whether or not to use any fancy effects.
     *
     * @return True if effects are OK.
     */
    boolean isEffects();

    /**
     * Preference of user whether or not to use any fancy effects.
     *
     * @param b True if effects are OK.
     */
    void setEffects(boolean b);
}


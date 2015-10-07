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
package org.jflicks.util;

/**
 * A container class to define the type of grayscale pixels we are interested
 * in finding along with a cutoff value to decide which fit our needs.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DetectRatingPlan {

    private int type;
    private int value;
    private int red;
    private int green;
    private int blue;
    private int range;

    /**
     * Default empty constructor.
     */
    public DetectRatingPlan() {

        setType(0);
        setValue(5);
    }

    /**
     * The type of logo image we expect, whether it's a BLACK or WHITE
     * type.  This is also the DetectRating.TYPE property.
     *
     * @return An int value.
     */
    public int getType() {
        return (type);
    }

    /**
     * The type of logo image we expect, whether it's a BLACK or WHITE
     * type.  This is also the DetectRating.TYPE property.
     *
     * @param i An int value.
     */
    public void setType(int i) {
        type = i;
    }

    /**
     * The minimum or maximum value we are interested in that the RGB
     * parts of a pixel meet our requirements.
     *
     * @return The value to determine if a grayscale color fits our needs.
     */
    public int getValue() {
        return (value);
    }

    /**
     * The minimum or maximum value we are interested in that the RGB
     * parts of a pixel meet our requirements.
     *
     * @param i The value to determine if a grayscale color fits our needs.
     */
    public void setValue(int i) {
        value = i;
    }

    public int getRange() {
        return (range);
    }

    public void setRange(int i) {
        range = i;
    }

    public int getRed() {
        return (red);
    }

    public void setRed(int i) {
        red = i;
    }

    public int getGreen() {
        return (green);
    }

    public void setGreen(int i) {
        green = i;
    }

    public int getBlue() {
        return (blue);
    }

    public void setBlue(int i) {
        blue = i;
    }

}


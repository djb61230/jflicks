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
 * Simple container class for a loaded rating image.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RatingImage {

    private String path;
    private int[] data;
    private int[] alphaData;
    private int width;
    private int height;
    private int[] buffer;

    /**
     * Constructor with four required arguments.
     *
     * @param path The path to the file.
     * @param array The array to use.
     * @param w The width of the image.
     * @param h the height of the image.
     */
    public RatingImage(String path, int[] array, int w, int h) {

        setPath(path);
        setAlphaData(array);
        if (array != null) {

            int[] rgbdata = new int[array.length];
            for (int i = 0; i < rgbdata.length; i++) {

                rgbdata[i] = array[i];
                rgbdata[i] &= 0x00ffffff;
            }

            setData(rgbdata);
        }
        setWidth(w);
        setHeight(h);
        setBuffer(new int[array.length]);
    }

    /**
     * The path to the file.
     *
     * @return The path as a String.
     */
    public String getPath() {
        return (path);
    }

    private void setPath(String s) {
        path = s;
    }

    /**
     * The raw data.
     *
     * @return An array of int values.
     */
    public int[] getData() {
        return (data);
    }

    private void setData(int[] array) {
        data = array;
    }

    /**
     * The alpha data.
     *
     * @return An array of int values.
     */
    public int[] getAlphaData() {
        return (alphaData);
    }

    private void setAlphaData(int[] array) {
        alphaData = array;
    }

    /**
     * The width of the image.
     *
     * @return The width in pixels.
     */
    public int getWidth() {
        return (width);
    }

    private void setWidth(int i) {
        width = i;
    }

    /**
     * The height of the image.
     *
     * @return The height in pixels.
     */
    public int getHeight() {
        return (height);
    }

    private void setHeight(int i) {
        height = i;
    }

    /**
     * We keep a working buffer in memory so we don't have to reload
     * all the time.
     *
     * @return An array of int values.
     */
    public int[] getBuffer() {
        return (buffer);
    }

    private void setBuffer(int[] array) {
        buffer = array;
    }

}

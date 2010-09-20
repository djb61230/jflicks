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
package org.jflicks.imagecache;

import java.awt.image.BufferedImage;

/**
 * The ImageCache interface defines a service to cache images both locally
 * on disk and in memory.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface ImageCache {

    /**
     * The ImageCache interface needs a title property.
     */
    String TITLE_PROPERTY = "ImageCache-Title";

    /**
     * The title of this stream service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * The idea is that images live at some URL and this service will fetch
     * them for you and create a BufferedImage instance.  The service will
     * also store it locally on disk and keep it in memory for quick fetches
     *
     * @param url A given URL that points to an image.
     * @return The loaded image as a BufferedImage instance.
     */
    BufferedImage getImage(String url);

    /**
     * The idea is that images live at some URL and this service will fetch
     * them for you and create a BufferedImage instance.  The service will
     * also store it locally on disk but not keep it in memory unless
     * specified.
     *
     * @param url A given URL that points to an image.
     * @param keepInMemory If True.
     * @return The loaded image as a BufferedImage instance.
     */
    BufferedImage getImage(String url, boolean keepInMemory);

    /**
     * Place the given image into the cache and return a URL String of where
     * the image is on disk.
     *
     * @param url A URL that will "match" the newly cached image.
     * @param bi A given image to cache.
     */
    void putImage(String url, BufferedImage bi);
}


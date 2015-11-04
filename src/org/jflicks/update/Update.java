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
package org.jflicks.update;

/**
 * The Update interface defines a service to update bundles from the net.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Update {

    /**
     * The Update interface needs a title property.
     */
    String TITLE_PROPERTY = "Update-Title";

    /**
     * The title of this stream service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * The local bundles we may update are located in a bundle
     * directory.
     *
     * @return A bundle directory as a String instance.
     */
    String getBundleDirectory();

    /**
     * The bundles that can be used to update are at a source URL.
     *
     * @return sourceURL A URL that contains the latest bundles.
     */
    String getSourceURL();

    /**
     * Give a directory of bundles, and a URL as a String, examine the
     * state of the local bundles to the net collection of bundles.  An
     * UpdateState instance is populated to allow the user some information
     * and the chance to either continue with the update or not.
     *
     * @return An UpdateState instance.
     */
    UpdateState open();

    /**
     * Given an UpdateState instance, perform an update.
     *
     * @param us A given UpdateState instance.
     */
    boolean update(UpdateState us);

    /**
     * Given an UpdateState instance, perform a close to clean things up.
     *
     * @param us A given UpdateState instance.
     */
    void close(UpdateState us);
}


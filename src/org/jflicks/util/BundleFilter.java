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

import java.io.File;
import java.io.FilenameFilter;

/**
 * Given a set of extensions, create a FilenameFilter.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class BundleFilter implements FilenameFilter {

    /**
     * Simple contructor.
     */
    public BundleFilter() {
    }

    /**
     * Check a File.
     *
     * @param dir The directory where is located (we don't care).
     * @param name The name of the file.
     * @return True if it starts with "jflicks-" and ends with ".jar".
     */
    public boolean accept(File dir, String name) {

        boolean result = false;

        if (name != null) {

            if ((name.startsWith("jflicks-")) && (name.endsWith(".jar"))) {
                result = true;
            }
        }

        return (result);
    }

}

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
import java.util.Arrays;

/**
 * Given a set of extensions, create a FilenameFilter.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ExtensionsFilter implements FilenameFilter {

    private String[] extensions;

    /**
     * Contructor accepting one argument.
     *
     * @param extensions The extensions to check a file.
     */
    public ExtensionsFilter(String[] extensions) {

        setExtensions(extensions);
    }

    /**
     * We have an array of acceptable extensions.
     *
     * @return An array of String instances.
     */
    public String[] getExtensions() {

        String[] result = null;

        if (extensions != null) {

            result = Arrays.copyOf(extensions, extensions.length);
        }

        return (result);
    }

    /**
     * We have an array of acceptable extensions.
     *
     * @param array An array of String instances.
     */
    public void setExtensions(String[] array) {

        if (array != null) {
            extensions = Arrays.copyOf(array, array.length);
        } else {
            extensions = null;
        }
    }

    /**
     * Check a File.
     *
     * @param dir The directory where is located (we don't care).
     * @param name The name of the file.
     * @return True if any of the extensions match.
     */
    public boolean accept(File dir, String name) {

        boolean result = false;

        String[] array = getExtensions();
        if ((name != null) && (array != null) && (!name.startsWith("."))) {

            for (int i = 0; i < array.length; i++) {

                if (name.endsWith(array[i])) {

                    result = true;
                    break;
                }
            }
        }

        return (result);
    }

}

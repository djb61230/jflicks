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

/**
 * Simple class that contains a time as an integer and a file.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DetectResult {

    private int time;
    private File file;

    /**
     * Default empty constructor.
     */
    public DetectResult() {
    }

    public int getTime() {
        return (time);
    }

    public void setTime(int i) {
        time = i;
    }

    public File getFile() {
        return (file);
    }

    public void setFile(File f) {
        file = f;
    }

}


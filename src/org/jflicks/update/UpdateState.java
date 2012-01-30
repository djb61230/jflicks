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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class UpdateState implements Serializable {

    private File[] files;
    private String bundleDirectory;
    private String sourceURL;
    private File workingDirectory;

    /**
     * Constructor.
     */
    public UpdateState(File[] files, String dir, String sourceURL) {

        setFiles(files);
        setBundleDirectory(dir);
        setSourceURL(sourceURL);
        try {

            File f = File.createTempFile("update", ".tmp");
            System.out.println("gern: " + f);
            if ((f != null) && (f.isFile()) && (f.exists())) {

                System.out.println("gern2: " + f);
                if (!f.delete()) {

                    System.out.println("Can't delete temp file meant as dir");
                }

                if (f.mkdir()) {

                    setWorkingDirectory(f);
                }
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public File[] getFiles() {
        return (files);
    }

    private void setFiles(File[] array) {
        files = array;
    }

    public String getBundleDirectory() {
        return (bundleDirectory);
    }

    private void setBundleDirectory(String s) {
        bundleDirectory = s;
    }

    public String getSourceURL() {
        return (sourceURL);
    }

    private void setSourceURL(String s) {
        sourceURL = s;
    }

    public int getUpdateCount() {

        int result = 0;

        if ((files != null) && (files.length > 0)) {

            result = files.length;
        }

        return (result);
    }

    public File getWorkingDirectory() {
        return (workingDirectory);
    }

    private void setWorkingDirectory(File f) {
        workingDirectory = f;
    }

}

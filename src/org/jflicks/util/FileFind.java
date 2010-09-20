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
import java.util.ArrayList;

/**
 * Given a directory and set of extensions, find all the matching files.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class FileFind {

    private ArrayList<File> fileList;
    private static FileFind instance = new FileFind();

    private FileFind() {

        setFileList(new ArrayList<File>());
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return A FileFind instance.
     */
    public static FileFind getInstance() {
        return (instance);
    }

    private ArrayList<File> getFileList() {
        return (fileList);
    }

    private void setFileList(ArrayList<File> l) {
        fileList = l;
    }

    private void addFile(File f) {

        ArrayList<File> l = getFileList();
        if ((f != null) && (l != null)) {

            l.add(f);
        }
    }

    /**
     * Find all the matching files in the given directory that match any of
     * the file extensions.
     *
     * @param dir A given directory File.
     * @param extensions An array of extensions as a String.
     * @return An array of File instances if any are found.
     */
    public synchronized File[] find(File dir, String[] extensions) {

        File[] result = null;

        ArrayList<File> l = getFileList();
        if (l != null) {

            l.clear();
            traverse(dir);

            if (l.size() > 0) {

                if (extensions != null) {

                    ArrayList<File> filter = new ArrayList<File>();
                    ExtensionsFilter ef = new ExtensionsFilter(extensions);
                    for (int i = 0; i < l.size(); i++) {

                        File tmp = l.get(i);
                        if (tmp != null) {

                            File parent = tmp.getParentFile();
                            String name = tmp.getName();
                            if (ef.accept(parent, name)) {

                                filter.add(tmp);
                            }
                        }
                    }

                    if (filter.size() > 0) {

                        result = filter.toArray(new File[filter.size()]);
                    }

                } else {

                    result = l.toArray(new File[l.size()]);
                }
            }
        }

        return (result);
    }

    private void traverse(File f) {

        if (f.isDirectory()) {

            File[] kids = f.listFiles();
            if (kids != null) {

                for (File kid : kids) {

                    traverse(kid);
                }
            }

        } else {

            addFile(f);
        }
    }

}

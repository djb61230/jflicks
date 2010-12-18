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
package org.jflicks.ui.view.aspirin.analyze.path;

import java.util.ArrayList;

import org.jflicks.ui.view.aspirin.analyze.BaseAnalyze;
import org.jflicks.ui.view.aspirin.analyze.Finding;
import org.jflicks.util.Util;

/**
 * This class is an Analyze implementation that can check if a program
 * is in the users path.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class PathAnalyze extends BaseAnalyze {

    private String program;
    private String download;
    private String[] paths;

    /**
     * Simple empty constructor.
     */
    public PathAnalyze() {

        setPaths(Util.getEnvPaths());
    }

    /**
     * The program to check whether it is currently in the users PATH.
     *
     * @return A program name.
     */
    public String getProgram() {
        return (program);
    }

    /**
     * The program to check whether it is currently in the users PATH.
     *
     * @param s A program name.
     */
    public void setProgram(String s) {
        program = s;
    }

    /**
     * A String that describes where to download the program off the Internet.
     * This is most likely a String URL but it doesn't have to be so.
     *
     * @return A download String.
     */
    public String getDownload() {
        return (download);
    }

    /**
     * A String that describes where to download the program off the Internet.
     * This is most likely a String URL but it doesn't have to be so.
     *
     * @param s A download String.
     */
    public void setDownload(String s) {
        download = s;
    }

    /**
     * The paths we use to check for programs.  We default by setting to
     * the users PATH environment variable but this can be overridden to
     * only check specific directories.
     *
     * @return An array of String instances.
     */
    public String[] getPaths() {
        return (paths);
    }

    /**
     * The paths we use to check for programs.  We default by setting to
     * the users PATH environment variable but this can be overridden to
     * only check specific directories.
     *
     * @param array An array of String instances.
     */
    public void setPaths(String[] array) {
        paths = array;
    }

    private String[] substitute(String[] array) {

        String[] result = array;

        String path = getInstallationPath();
        if ((path != null) && (array != null) && (array.length > 0)) {

            ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < array.length; i++) {

                String tmp = array[i];
                int index = tmp.indexOf("$INSTALL");
                if (index != -1) {

                    tmp = path + tmp.substring(index + 8);
                }

                list.add(tmp);
            }

            result = list.toArray(new String[list.size()]);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Finding analyze() {

        Finding result = null;

        String s = getProgram();
        String[] array = getPaths();
        array = substitute(array);
        if ((s != null) && (array != null)) {

            result = new Finding();
            result.setTitle(getShortDescription());
            array = Util.getProgramPaths(array, s);
            if (array != null) {

                result.setPassed(true);
                StringBuilder sb = new StringBuilder();
                sb.append(s);
                sb.append(" has been found in " + array.length);
                if (array.length > 1) {
                    sb.append(" locations. They are ");
                } else {
                    sb.append(" location.  Is it ");
                }

                for (int i = 0; i < array.length; i++) {

                    sb.append(array[i]);
                    if ((i + 1) < array.length) {

                        sb.append(", ");
                    }
                }

                result.setDescription(sb.toString());

            } else {

                result.setPassed(false);
                String dl = getDownload();
                if (dl != null) {

                    result.setDescription(s + " was not found!  More Info: "
                        + dl);

                } else {

                    result.setDescription(s + " was not found!");
                }
            }

        } else {

            throw new RuntimeException("Program property cannot be NULL!");
        }

        return (result);
    }

    /**
     * Override so we look good in UI components.
     *
     * @return A String that is the short description property.
     */
    public String toString() {
        return (getShortDescription());
    }

}


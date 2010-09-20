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
package org.jflicks.ui.view.fe.screen.script;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.ui.view.fe.screen.ExecuteScreen;

/**
 * A script screen is configured with a title, and array of command
 * names and an array of script paths.  The command names and script paths
 * arrays are associative and of course must be the size length.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ScriptScreen extends ExecuteScreen {

    private String[] paths;

    /**
     * Constructor with 3 required arguments.
     *
     * @param title The title of this screen.
     * @param cmds The individual command names.
     * @param paths Paths to scripts to be executed on demand.
     */
    public ScriptScreen(String title, String[] cmds, String[] paths) {

        setTitle(title);
        setCommands(cmds);
        setPaths(paths);

        BufferedImage bi = getImageByName(title);
        setDefaultBackgroundImage(bi);
    }

    /**
     * The paths are an associative array of String paths that point to a
     * script or program.  The script or program will be called when the
     * user selects the associated command.
     *
     * @return A script path.
     */
    public String[] getPaths() {

        String[] result = null;

        if (paths != null) {

            result = Arrays.copyOf(paths, paths.length);
        }

        return (result);
    }

    private void setPaths(String[] array) {

        if (array != null) {
            paths = Arrays.copyOf(array, array.length);
        } else {
            paths = null;
        }
    }

    private String getPathFromCommand(String command) {

        String result = null;

        String[] cmds = getCommands();
        String[] scripts = getPaths();
        if ((command != null) && (cmds != null) && (scripts != null)
            && (cmds.length == scripts.length)) {

            for (int i = 0; i < cmds.length; i++) {

                if (command.equals(cmds[i])) {

                    result = scripts[i];
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void execute(String command) {

        if (command != null) {

            String script = getPathFromCommand(command);
            if (script != null) {

                SystemJob job = SystemJob.getInstance(script);
                JobContainer jc = JobManager.getJobContainer(job);
                jc.start();
            }
        }
    }

}


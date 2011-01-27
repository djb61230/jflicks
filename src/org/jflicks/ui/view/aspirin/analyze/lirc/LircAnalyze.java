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
package org.jflicks.ui.view.aspirin.analyze.lirc;

import java.io.File;

import org.jflicks.ui.view.aspirin.analyze.BaseAnalyze;
import org.jflicks.ui.view.aspirin.analyze.Finding;

/**
 * This class is an Analyze implementation that can check on lirc.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class LircAnalyze extends BaseAnalyze {

    /**
     * Simple empty constructor.
     */
    public LircAnalyze() {

        setTitle("LircAnalyze");
        setShortDescription("Check if lirc is configured for the TV UI.");

        StringBuilder sb = new StringBuilder();

        sb.append("If you want to use a remote control with the TV ");
        sb.append("user interface you have to have a configuration ");
        sb.append("that has the correct information.  This ");
        sb.append("information must match data that the Lirc ");
        sb.append("process will be sending to responses to ");
        sb.append("button presses on the remote.  Our ");
        sb.append("software will respond if this configuration ");
        sb.append("file is correct.  We check for the files ");
        sb.append("existance and assume it is correct.  If it ");
        sb.append("is missing than you can create one by using the fix.");

        setLongDescription(sb.toString());

        String[] array = {
            "jflicks-rc-lirc.jar",
            "jflicks-rc-winlirc.jar"
        };

        setBundles(array);
    }

    /**
     * {@inheritDoc}
     */
    public Finding analyze() {

        Finding result = new Finding();

        result.setTitle(getShortDescription());
        boolean passed = false;
        StringBuilder sb = new StringBuilder();
        String path = getInstallationPath();
        if (path != null) {

            File fpath = new File(path);
            if ((fpath.exists()) && (fpath.isDirectory())) {

                File conf = new File(fpath, "conf");
                if ((conf.exists()) && (conf.isDirectory())) {

                    File lircrc = new File(conf, "LircJob.lircrc");
                    if ((lircrc.exists()) && (lircrc.isFile())) {

                        passed = true;

                    } else {

                        result.setFix(new LircFix(lircrc));
                    }
                }
            }
        }

        if (passed) {

            result.setPassed(true);
            sb.append("Found the LircJob.lircrc file");

        } else {

            result.setPassed(false);
            sb.append("Did NOT find the LircJob.lircrc file");
        }

        result.setDescription(sb.toString());

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


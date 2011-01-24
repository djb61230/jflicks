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
package org.jflicks.ui.view.aspirin.analyze.schedulesdirect;

import java.io.File;

import org.jflicks.ui.view.aspirin.analyze.BaseAnalyze;
import org.jflicks.ui.view.aspirin.analyze.Finding;
import org.jflicks.util.Hostname;

/**
 * This class is an Analyze implementation that can check if a program
 * is in the users path.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SchedulesDirectAnalyze extends BaseAnalyze {

    /**
     * Simple empty constructor.
     */
    public SchedulesDirectAnalyze() {

        setTitle("SchedulesDirectAnalyze");
        setShortDescription("See if a config file for Schedule's Direct exists.");

        StringBuilder sb = new StringBuilder();

        sb.append("To be able to get guide data from Schedules Direct ");
        sb.append("a specialized XML formatted configuration file ");
        sb.append("needs to exist.  We check for it.");

        setLongDescription(sb.toString());

        String[] array = {
            "jflicks-tv-programdata-sd.jar"
        };

        setBundles(array);
    }

    /**
     * {@inheritDoc}
     */
    public Finding analyze() {

        Finding result = new Finding();

        boolean passed = false;
        StringBuilder sb = new StringBuilder();
        result.setTitle(getShortDescription());
        String path = getInstallationPath();
        if (path != null) {

            File fpath = new File(path);
            if ((fpath.exists()) && (fpath.isDirectory())) {

                File conf = new File(fpath, "conf");
                if ((conf.exists()) && (conf.isDirectory())) {

                    File xtvd = new File(conf, "XTVD.xml");
                    if ((xtvd.exists()) && (xtvd.isFile())) {

                        passed = true;

                    } else {

                        result.setFix(new SchedulesDirectFix(xtvd));
                    }
                }
            }
        }

        if (passed) {

            result.setPassed(true);
            sb.append("Found the XTVD.xml");

        } else {

            result.setPassed(false);
            sb.append("Did NOT find the XTVD.xml");
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


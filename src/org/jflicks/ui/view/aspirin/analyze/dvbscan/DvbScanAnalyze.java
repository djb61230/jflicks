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
package org.jflicks.ui.view.aspirin.analyze.dvbscan;

import java.io.File;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
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
public class DvbScanAnalyze extends BaseAnalyze {

    /**
     * Simple empty constructor.
     */
    public DvbScanAnalyze() {

        setTitle("DvbScanAnalyze");
        setShortDescription("See if channel files exist for DVB devices.");

        StringBuilder sb = new StringBuilder();

        sb.append("To be able to tune a channel, a DVB device needs ");
        sb.append("to have a conf/adapterN_dvrN.conf file that has ");
        sb.append("entries to tune to channels that it can receive. ");
        sb.append("We assume that if it exists, then all is a-ok.");

        setLongDescription(sb.toString());

        String[] array = {
            "jflicks-tv-recorder-dvb.jar"
        };

        setBundles(array);
    }

    /**
     * {@inheritDoc}
     */
    public Finding analyze() {

        Finding result = new Finding();

        result.setTitle(getShortDescription());

        StringBuilder sb = new StringBuilder();
        String installPath = getInstallationPath();
        if (installPath != null) {

            DiscoverJob job = new DiscoverJob();
            JobContainer jc = JobManager.getJobContainer(job);
            jc.start();
            while (jc.isAlive()) {

                JobManager.sleep(100);
            }

            String[] filenames = null;
            DvbPath[] array = job.getDvbPaths();
            if ((array != null) && (array.length > 0)) {

                filenames = new String[array.length];
                for (int i = 0; i < array.length; i++) {

                    filenames[i] = array[i].getChannelScanFileName();
                }
            }

            if (filenames != null) {

                int fcount = 0;
                boolean[] found = new boolean[filenames.length];
                File installFile = new File(installPath);
                File conf = new File(installFile, "conf");
                for (int i = 0; i < filenames.length; i++) {

                    File chan = new File(conf, filenames[i]);
                    if ((chan.exists()) && (chan.isFile())) {

                        // This is good...
                        found[i] = true;
                        fcount++;

                    } else {

                        found[i] = true;
                    }
                }

                if (fcount == filenames.length) {

                    result.setPassed(true);
                    result.setDescription("Looks like you have a scan  ");
                    result.setDescription("file for all your DVB devices.");

                } else {

                    result.setPassed(false);
                    result.setDescription("You have " + fcount + " missing ");
                    result.setDescription("scan file(s).  They are:");

                    for (int i = 0; i < filenames.length; i++) {

                        if (!found[i]) {

                            File chan = new File(conf, filenames[i]);
                            result.setDescription(chan.getPath());
                        }
                    }
                }

            } else {

                result.setPassed(true);
                result.setDescription("Actually didn't find any DVB ");
                result.setDescription("devices, so perhaps you do not need ");
                result.setDescription("to install support for them.");
            }

        } else {

            result.setPassed(true);
            result.setDescription("This check is only effective if you have ");
            result.setDescription("selected an installation directory.");
        }

        result.setDescription(sb.toString());

        /*
        String ip = Hostname.getHostAddress();
        if (ip != null) {

            StringBuilder sb = new StringBuilder();
            if (Hostname.isLoopback(ip)) {

                // Not good, we need to set failure.
                result.setPassed(false);
                sb.append("Your localhost resolves to " + ip);
                sb.append(" which will cause problems.  Please add an");
                sb.append(" entry to your hosts file or resolve this in");
                sb.append(" some way.");

            } else {

                // Looks like we are OK.
                result.setPassed(true);
                sb.append("Your localhost resolves to " + ip);
                sb.append(" which should be fine.");
            }

            result.setDescription(sb.toString());

        } else {

            result.setPassed(false);
            result.setDescription("Your hostname is NOT set!!");
        }
        */

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


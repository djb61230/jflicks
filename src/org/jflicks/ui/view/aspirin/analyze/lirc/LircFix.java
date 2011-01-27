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
import javax.swing.JOptionPane;

import org.jflicks.job.JobEvent;
import org.jflicks.ui.view.aspirin.analyze.BaseFix;
import org.jflicks.util.Util;

/**
 * With the aid of the user create a LircJob.lircrc file.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class LircFix extends BaseFix {

    private File file;

    /**
     * Simple one argument constructor.
     *
     * @param file The file path to the proper LircJob.lircrc.
     */
    public LircFix(File file) {

        setFile(file);
    }

    private File getFile() {
        return (file);
    }

    private void setFile(File f) {
        file = f;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setTerminate(false);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        RemoteSelectPanel rsp = new RemoteSelectPanel();
        if (Util.showDialog(null, "Parse lircd.conf", rsp)) {

            if (!rsp.write(getFile())) {

                JOptionPane.showMessageDialog(null,
                    "Failed to write file", "alert",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
    }

}

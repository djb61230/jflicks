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
package org.jflicks.tv.recorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.Timer;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobManager;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

/**
 * A job that saves images to an NMS.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class CopyJob extends RecoverJob {

    private String dest;
    private FileOutputStream fileOutputStream;

    /**
     * Constructor with our two required arguments.
     *
     * @param source The source path.
     * @param dest The destination path.
     */
    public CopyJob(String source, String dest) {

        setDevice(source);
        setDest(dest);
    }

    private String getDest() {
        return (dest);
    }

    private void setDest(String s) {
        dest = s;
    }

    private FileOutputStream getFileOutputStream() {

        if (fileOutputStream == null) {

            try {

                fileOutputStream = new FileOutputStream(getDest());

            } catch (FileNotFoundException ex) {

                fileOutputStream = null;
            }
        }

        return (fileOutputStream);
    }

    /**
     * {@inheritDoc}
     */
    public void process(byte[] buffer, int length) {

        FileOutputStream fos = getFileOutputStream();
        if (fos != null) {

            try {

                fos.write(buffer, 0, length);

            } catch (IOException ex) {

                fireJobEvent(JobEvent.UPDATE, ex.getMessage());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {

        if (fileOutputStream != null) {

            try {

                fileOutputStream.close();
                fileOutputStream = null;

            } catch (IOException ex) {

                fileOutputStream = null;
            }
        }
    }

}

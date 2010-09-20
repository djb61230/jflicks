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
package org.jflicks.tv.recorder.v4l2;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;

/**
 * A job that saves images to an NMS.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class CopyJob extends AbstractJob {

    private String source;
    private String dest;

    /**
     * Simple no argument constructor.
     */
    public CopyJob(String source, String dest) {

        setSource(source);
        setDest(dest);
    }

    private String getSource() {
        return (source);
    }

    private void setSource(String s) {
        source = s;
    }

    private String getDest() {
        return (dest);
    }

    private void setDest(String s) {
        dest = s;
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

        byte[] buffer = new byte[1024];

        String s = getSource();
        String d = getDest();
        if ((s != null) && (d != null)) {

            try {

                FileInputStream fis = new FileInputStream(s);
                FileOutputStream fos = new FileOutputStream(d);

                while (!isTerminate()) {

                    int count = fis.read(buffer);
                    fos.write(buffer, 0, count);
                }

                fis.close();
                fos.close();

            } catch (Exception ex) {

                System.out.println(ex.getMessage());
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

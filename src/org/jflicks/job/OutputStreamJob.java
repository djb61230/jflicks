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
package org.jflicks.job;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This job will write on an output stream.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class OutputStreamJob extends AbstractJob {

    private OutputStream outputStream;

    private OutputStreamJob() {
    }

    /**
     * The output stream to send data.
     *
     * @param os The given output stream.
     */
    public OutputStreamJob(OutputStream os) {

        setOutputStream(os);
    }

    private OutputStream getOutputStream() {
        return (outputStream);
    }

    private void setOutputStream(OutputStream os) {
        outputStream = os;
    }

    /**
     * Write the given data to the stream.
     *
     * @param array A byte array of data.
     * @param offset The offset into the array.
     * @param length The number of bytes to write.
     */
    public void write(byte[] array, int offset, int length) {

        OutputStream os = getOutputStream();
        if ((os != null) && (array != null)) {

            try {

                os.write(array, offset, length);
                os.flush();

            } catch (IOException ex) {
            }
        }
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

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());
        }

        setTerminate(true);
        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
    }

}


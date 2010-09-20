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

import java.io.InputStream;

/**
 * This job will listen on an input stream and fire events when data
 * is read.  It also will maintain a buffer where users can get all data
 * read.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class InputStreamJob extends AbstractJob {

    private static final int BUFFER_SIZE = 20480;

    private InputStream inputStream;
    private StringBuffer stringBuffer;

    private InputStreamJob() {
    }

    /**
     * Read from the given input stream.
     *
     * @param is The given InputStream to read from.
     */
    public InputStreamJob(InputStream is) {

        setInputStream(is);
        stringBuffer = new StringBuffer();
    }

    private InputStream getInputStream() {
        return (inputStream);
    }

    private void setInputStream(InputStream is) {
        inputStream = is;
    }

    /**
     * Retrieve all read data.
     *
     * @return The read data as a String instance.
     */
    public String getOutputText() {
        return (stringBuffer.toString());
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

        byte[] data = new byte[BUFFER_SIZE];
        InputStream is = getInputStream();
        if (is != null) {

            stringBuffer.setLength(0);
            try {

                while (!isTerminate()) {

                    boolean done = false;
                    while (!done) {

                        int count = is.read(data);
                        if (count > 0) {

                            String tmp = new String(data, 0, count);
                            tmp = tmp.trim();
                            fireJobEvent(JobEvent.UPDATE, tmp);
                            stringBuffer.append(tmp);
                            stringBuffer.append("\n");

                        } else {

                            done = true;
                        }
                    }
                }

            } catch (Exception ex) {

                // We were probably shutdown or interrupted.
            }
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


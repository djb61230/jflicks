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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.FileChannel;
import javax.swing.Timer;

import org.jflicks.job.JobEvent;

/**
 * This class has code to read from a file and have the notion of recovering
 * when we appear to be blocking on a read.  What seems to happen when
 * reading from a video for linux device (the HD-PVR) it seems we can open
 * a stream and then read - but something happens and the read ends up just
 * blocking and not getting any data.  It might be because the stream to the
 * box has been disrupted in some way.  One would think that the low level
 * driver would not get confused - so we do this to try to work around this
 * problem.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class RecoverJob extends BaseDeviceJob implements
    ActionListener {

    private FileInputStream fileInputStream;
    private FileChannel fileChannel;
    private long currentRead;
    private long lastRead;

    /**
     * Extensions need to do something with the data we have read.
     *
     * @param buffer An array of bytes.
     * @param length The number of valid bytes.
     */
    public abstract void process(byte[] buffer, int length);

    /**
     * Close any resources that we opened so data could have been processed.
     */
    public abstract void close();

    private int blockCount;

    /**
     * Simple no argument constructor.
     */
    public RecoverJob() {
    }

    private int getBlockCount() {
        return (blockCount);
    }

    private void setBlockCount(int i) {
        blockCount = i;
    }

    /**
     * We listen for our timer event to check to see if our read is blocking.
     *
     * @param event Time to check.
     */
    public void actionPerformed(ActionEvent event) {

        if (currentRead != 0L) {

            if (lastRead == 0L) {

                // First time through...
                lastRead = currentRead;

            } else if (currentRead == lastRead) {

                // We have arrived here with the same read time as last.
                // We are probably blocked...
                System.out.println("We are probably blocking...");
                int bcount = getBlockCount();
                bcount++;
                System.out.println("Times we failed on a block: " + bcount);
                if (bcount < 20) {

                    setBlockCount(bcount);
                    if (fileChannel != null) {

                        try {

                            fileChannel.close();
                            fileChannel = null;

                        } catch (IOException ex) {

                            System.out.println("exception on interupt close");
                        }
                    }

                } else {

                    System.out.println("Time to give up!!");
                    stop();
                }

            } else {

                // All good reset our last read...
                lastRead = currentRead;
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

        ByteBuffer bb = ByteBuffer.allocate(1024);
        byte[] buffer = bb.array();

        try {

            int count = 0;
            fileInputStream = new FileInputStream(getDevice());
            fileChannel = fileInputStream.getChannel();
            currentRead = 0L;
            lastRead = 0L;
            Timer timer = new Timer(2000, this);
            timer.start();

            while (!isTerminate()) {

                try {

                    currentRead = System.currentTimeMillis();
                    bb.rewind();
                    count = fileChannel.read(bb);

                } catch (AsynchronousCloseException ex) {

                    timer.stop();
                    count = 0;
                    currentRead = 0L;
                    lastRead = 0L;
                    fileInputStream.close();
                    fileInputStream = new FileInputStream(getDevice());
                    fileChannel = fileInputStream.getChannel();
                    timer = new Timer(2000, this);
                    timer.start();
                }

                if (count > 0) {

                    process(buffer, count);
                }
            }

            fileInputStream.close();
            timer.stop();
            close();

        } catch (Exception ex) {

            System.out.println("RecoverJob: " + ex.getMessage());
        }

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            stop();
        }
    }

}

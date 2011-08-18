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
import java.nio.channels.ClosedChannelException;
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

    private static final int MAX_BLOCK_COUNT = 40;
    private static final int TIMER_MILLIS = 5000;

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
                setBlockCount(bcount);
                System.out.println("Times we failed on a block: " + bcount);

                // We always want to close...
                if (fileChannel != null) {

                    synchronized (fileChannel) {

                        try {

                            System.out.println("Attempting to close!");
                            fileChannel.close();
                            fileChannel = null;

                        } catch (Exception ex) {

                            System.out.println("exception on interupt close");
                            fileChannel = null;
                        }
                    }

                } else {

                    System.out.println("Can't close, fileChannel is null!");
                }

                if (bcount > MAX_BLOCK_COUNT) {

                    System.out.println("Time to give up!!");
                    stop();
                }

            } else {

                // All good reset our last read...
                lastRead = currentRead;
            }
        }
    }

    private void reset() {

        System.out.println("We are trying to reset!");

        try {

            currentRead = 0L;
            lastRead = 0L;

            // Only start a new read and timer if we haven't
            // reached our max retry count.
            if (getBlockCount() < MAX_BLOCK_COUNT) {

                System.out.println("Getting new fileChannel...");
                fileInputStream = new FileInputStream(getDevice());
                fileChannel = fileInputStream.getChannel();

                if (fileChannel != null) {

                    // Let's up the delay by a second - perhaps
                    // recovery will happen.
                    System.out.println("Got new fileChannel.");

                } else {

                    // We tried to get a channel, it failed giveup.
                    System.out.println("Failed new fileChannel.");
                    setTerminate(true);
                }

            } else {

                System.out.println("Looks like time to quit.");
                setTerminate(true);
            }

        } catch (IOException ex) {

            System.out.println("Failed to re-open channel, perhaps next time.");
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

        Timer timer = null;
        ByteBuffer bb = ByteBuffer.allocate(1024);
        byte[] buffer = bb.array();

        try {

            int count = 0;
            fileInputStream = new FileInputStream(getDevice());
            fileChannel = fileInputStream.getChannel();
            currentRead = 0L;
            lastRead = 0L;
            timer = new Timer(TIMER_MILLIS, this);
            timer.start();

            while (!isTerminate()) {

                try {

                    bb.rewind();
                    currentRead = System.currentTimeMillis();
                    count = fileChannel.read(bb);

                } catch (AsynchronousCloseException ex) {

                    System.out.println("We have been interupted!");
                    if (fileInputStream != null) {

                        fileInputStream.close();
                    }

                    timer.stop();
                    count = 0;
                    reset();
                    timer.restart();

                } catch (ClosedChannelException ex) {

                    timer.stop();
                    count = 0;
                    reset();
                    timer.restart();

                } catch (Exception ex) {

                    // Here is a catch-all.  Hopefully we can still reset.
                    timer.stop();
                    count = 0;
                    reset();
                    timer.restart();
                }

                if (count > 0) {

                    process(buffer, count);
                }
            }

            fileInputStream.close();
            timer.stop();
            close();

        } catch (Exception ex) {

            System.out.println("Exception RecoverJob: "
                + ex.getClass().getName() + " " + ex.getMessage());
            ex.printStackTrace();
            timer.stop();
            close();
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

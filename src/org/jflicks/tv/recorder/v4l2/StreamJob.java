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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.FileChannel;
import java.nio.channels.InterruptibleChannel;
import javax.swing.Timer;

import org.jflicks.job.JobEvent;
import org.jflicks.job.JobManager;

/**
 * A job that saves images to an NMS.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class StreamJob extends BaseV4l2Job implements ActionListener {

    private String host;
    private int port;
    private FileInputStream fileInputStream;
    private FileChannel fileChannel;
    private long currentRead;
    private long lastRead;

    /**
     * Simple no argument constructor.
     */
    public StreamJob() {
    }

    /**
     * We need a host to send packets.
     *
     * @return A host as a String.
     */
    public String getHost() {
        return (host);
    }

    /**
     * We need a host to send packets.
     *
     * @param s A host as a String.
     */
    public void setHost(String s) {
        host = s;
    }

    /**
     * We need a port to send packets.
     *
     * @return A port as an int.
     */
    public int getPort() {
        return (port);
    }

    /**
     * We need a port to send packets.
     *
     * @param i A port as an int.
     */
    public void setPort(int i) {
        port = i;
    }

    public void actionPerformed(ActionEvent event) {

        if (currentRead != 0L) {

            if (lastRead == 0L) {

                // First time through...
                lastRead = currentRead;

            } else if (currentRead == lastRead) {

                // We have arrived here with the same read time as last.
                // We are probably blocked...
                System.out.println("We are probably blocking...");
                System.out.println("fileChannel: " + fileChannel);
                if (fileChannel != null) {

                    try {

                        fileChannel.close();
                        fileChannel = null;
                        System.out.println("after close");

                    } catch (IOException ex) {

                        System.out.println("exception on interupt close");
                    }
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

        //byte[] buffer = new byte[1024];
        ByteBuffer bb = ByteBuffer.allocate(1024);
        byte[] buffer = bb.array();
        System.out.println("ByteBuffer: " + bb + " " + buffer);

        String h = getHost();
        String d = getDevice();
        System.out.println("host: " + h);
        System.out.println("device: " + d);
        if ((h != null) && (d != null)) {

            try {

                int count = 0;
                int p = getPort();
                DatagramSocket socket = new DatagramSocket();
                InetAddress addr = InetAddress.getByName(h);
                fileInputStream = new FileInputStream(d);
                fileChannel = fileInputStream.getChannel();
                System.out.println("fileChannel: " + fileChannel);
                System.out.println(fileChannel instanceof InterruptibleChannel);
                currentRead = 0L;
                lastRead = 0L;
                Timer timer = new Timer(2000, this);
                timer.start();

                while (!isTerminate()) {

                    try {

                        currentRead = System.currentTimeMillis();
                        //count = fileInputStream.read(buffer);
                        bb.rewind();
                        count = fileChannel.read(bb);
                        //System.out.println("count: " + count);

                    } catch (AsynchronousCloseException ex) {

                        timer.stop();
                        count = 0;
                        currentRead = 0L;
                        lastRead = 0L;
                        fileInputStream.close();
                        fileInputStream = new FileInputStream(d);
                        fileChannel = fileInputStream.getChannel();
                        timer = new Timer(2000, this);
                        timer.start();
                    }

                    if (count > 0) {

                        DatagramPacket packet =
                            new DatagramPacket(buffer, count, addr, p);
                        socket.send(packet);
                    }
                }

                fileInputStream.close();
                timer.stop();

            } catch (Exception ex) {

                System.out.println("Exception dude: " + ex.getMessage());
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

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            stop();
        }
    }

}

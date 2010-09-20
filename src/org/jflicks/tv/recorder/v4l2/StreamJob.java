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

import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;

/**
 * A job that saves images to an NMS.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class StreamJob extends BaseV4l2Job {

    private String host;
    private int port;

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

        String h = getHost();
        String d = getDevice();
        System.out.println("host: " + h);
        System.out.println("device: " + d);
        if ((h != null) && (d != null)) {

            try {

                int p = getPort();
                DatagramSocket socket = new DatagramSocket();
                InetAddress addr = InetAddress.getByName(h);
                FileInputStream fis = new FileInputStream(d);

                while (!isTerminate()) {

                    int count = fis.read(buffer);
                    DatagramPacket packet =
                        new DatagramPacket(buffer, count, addr, p);
                    socket.send(packet);
                }

                fis.close();

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

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            stop();
        }
    }

}

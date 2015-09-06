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
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.Timer;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

/**
 * Read from a device and send the data via UDP packet.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class StreamJob extends RecoverJob {

    private String host;
    private int port;
    private DatagramSocket datagramSocket;
    private InetAddress inetAddress;

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

    private DatagramSocket getDatagramSocket() {

        if (datagramSocket == null) {

            try {

                datagramSocket = new DatagramSocket();

            } catch (IOException ex) {

                datagramSocket = null;
            }
        }

        return (datagramSocket);
    }

    private InetAddress getInetAddress() {

        if (inetAddress == null) {

            try {

                inetAddress = InetAddress.getByName(getHost());

            } catch (IOException ex) {

                inetAddress = null;
            }
        }

        return (inetAddress);
    }

    /**
     * {@inheritDoc}
     */
    public void process(byte[] buffer, int length) {

        DatagramSocket socket = getDatagramSocket();
        InetAddress addr = getInetAddress();
        if ((socket != null) && (addr != null)) {

            int p = getPort();
            try {

                DatagramPacket packet =
                    new DatagramPacket(buffer, length, addr, p);
                socket.send(packet);

            } catch (IOException ex) {

                LogUtil.log(LogUtil.WARNING, ex.getMessage());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {

        datagramSocket = null;
        inetAddress = null;
    }

}

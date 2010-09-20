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
package org.jflicks.discovery;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URLEncoder;

import org.jflicks.job.AbstractJob;

/**
 * Base class that has common methods and properties to handle "service
 * discovery".
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseDiscoveryJob extends AbstractJob {

    private InetAddress multicastInetAddress;
    private int multicastPort;
    private String serviceName;
    private MulticastSocket multicastSocket;
    private DatagramPacket queuedDatagramPacket;
    private DatagramPacket receivedDatagramPacket;

    /**
     * Constructor with our one required argument.
     *
     * @param name The given service name.
     */
    public BaseDiscoveryJob(String name) {
        setServiceName(name);
    }

    /**
     * Whether server or client we need a multicast address to use.
     *
     * @return An InetAddress instance.
     */
    public InetAddress getMulticastInetAddress() {
        return (multicastInetAddress);
    }

    /**
     * Whether server or client we need a multicast address to use.
     *
     * @param ia An InetAddress instance.
     */
    public void setMulticastInetAddress(InetAddress ia) {
        multicastInetAddress = ia;
    }

    /**
     * Whether server or client we need a multicast port to use.
     *
     * @return A port as an int.
     */
    public int getMulticastPort() {
        return (multicastPort);
    }

    /**
     * Whether server or client we need a multicast port to use.
     *
     * @param i A port as an int.
     */
    public void setMulticastPort(int i) {
        multicastPort = i;
    }

    /**
     * Whether we are looking for or advertising for a service, it needs to
     * have a name.
     *
     * @return The name of the service.
     */
    public String getServiceName() {
        return (serviceName);
    }

    /**
     * Whether we are looking for or advertising for a service, it needs to
     * have a name.
     *
     * @param s The name of the service.
     */
    public void setServiceName(String s) {
        serviceName = s;
    }

    /**
     * We need a MulticastSocket to communicate on.
     *
     * @return A MulticastSocket instance.
     */
    public MulticastSocket getMulticastSocket() {
        return (multicastSocket);
    }

    /**
     * We need a MulticastSocket to communicate on.
     *
     * @param ms A MulticastSocket instance.
     */
    public void setMulticastSocket(MulticastSocket ms) {
        multicastSocket = ms;
    }

    /**
     * We remember the currently queued DatagramPacket.
     *
     * @return A DatagramPacket instance.
     */
    public DatagramPacket getQueuedDatagramPacket() {
        return (queuedDatagramPacket);
    }

    /**
     * We remember the currently queued DatagramPacket.
     *
     * @param dp A DatagramPacket instance.
     */
    public void setQueuedDatagramPacket(DatagramPacket dp) {
        queuedDatagramPacket = dp;
    }

    /**
     * We remember the last received DatagramPacket.
     *
     * @return A DatagramPacket instance.
     */
    public DatagramPacket getReceivedDatagramPacket() {
        return (receivedDatagramPacket);
    }

    /**
     * We remember the last received DatagramPacket.
     *
     * @param dp A DatagramPacket instance.
     */
    public void setReceivedDatagramPacket(DatagramPacket dp) {
        receivedDatagramPacket = dp;
    }

    /**
     * Get the service name encoded to network traffic.
     *
     * @return An encoded string.
     */
    public String getEncodedServiceName() {

        String result = getServiceName();

        try {

            result = URLEncoder.encode(result, "UTF-8");

        } catch (UnsupportedEncodingException uee) {

            result = null;
        }

        return (result);
    }

    /**
     * Convenience method to send the current queued packet.
     */
    public void sendQueuedPacket() {

        MulticastSocket socket = getMulticastSocket();
        DatagramPacket packet = getQueuedDatagramPacket();
        if ((packet != null) && (socket != null)) {

            try {

                socket.send(packet);
                setQueuedDatagramPacket(null);

            } catch (IOException ioe) {

                System.err.println("Unexpected exception: " + ioe);
                ioe.printStackTrace();
            }
        }
    }

}

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
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A job that runs and listens foe multipackets to supply a simple
 * "service discovery" for this project.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ServiceResponderJob extends BaseDiscoveryJob {

    private ServiceDescription serviceDescription;
    private ArrayList<String> clientList;

    /**
     * Constructor with our required argument.
     *
     * @param name The name of the service.
     */
    public ServiceResponderJob(String name) {

        super(name);
        setClientList(new ArrayList<String>());
    }

    /**
     * The ServiceDescription object defines the information that will be
     * returned when answering a query.
     *
     * @return The ServiceDescription instance.
     */
    public ServiceDescription getServiceDescription() {
        return (serviceDescription);
    }

    /**
     * The ServiceDescription object defines the information that will be
     * returned when answering a query.
     *
     * @param sd The ServiceDescription instance.
     */
    public void setServiceDescription(ServiceDescription sd) {
        serviceDescription = sd;
    }

    private ArrayList<String> getClientList() {
        return (clientList);
    }

    private void setClientList(ArrayList<String> l) {
        clientList = l;
    }

    private void add(String s) {

        ArrayList<String> l = getClientList();
        if (l != null) {
            l.add(s);
        }
    }

    private boolean isInList(String s) {

        boolean result = false;

        ArrayList<String> l = getClientList();
        if ((l != null) && (l.contains(s))) {
            result = true;
        }

        return (result);
    }

    private boolean isPrevious() {

        boolean result = false;

        DatagramPacket packet = getReceivedDatagramPacket();
        if (packet != null) {

            String s = new String(packet.getData());
            int pos = s.indexOf((char) 0);
            if (pos > -1) {
                s = s.substring(0, pos);
            }
            String tmp = "SERVICE QUERY " + getEncodedServiceName();
            String id = s.substring(tmp.length()).trim();
            result = isInList(id);
            if (!result) {
                add(id);
            }
        }

        return (result);
    }

    private boolean isQueryPacket() {

        boolean result = false;

        DatagramPacket packet = getReceivedDatagramPacket();
        if (packet != null) {

            String s = new String(packet.getData());
            int pos = s.indexOf((char) 0);
            if (pos > -1) {
                s = s.substring(0, pos);
            }

            result = s.startsWith("SERVICE QUERY " + getEncodedServiceName());
        }

        return (result);
    }

    private DatagramPacket getReplyPacket() {

        DatagramPacket result = null;

        ServiceDescription descriptor = getServiceDescription();
        if (descriptor != null) {

            StringBuilder buf = new StringBuilder();

            try {

                buf.append("SERVICE REPLY " + getEncodedServiceName() + " ");
                buf.append(descriptor.toString());

                byte[] bytes = buf.toString().getBytes();
                result = new DatagramPacket(bytes, bytes.length);
                result.setAddress(getMulticastInetAddress());
                result.setPort(getMulticastPort());

            } catch (NullPointerException npe) {

                System.err.println("Unexpected exception: " + npe);
                npe.printStackTrace();
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setTerminate(false);
        try {

            setMulticastInetAddress(InetAddress.getByName(
                ServiceConstants.MULTICAST_INET_ADDRESS));
            setMulticastPort(ServiceConstants.MULTICAST_PORT);

            MulticastSocket socket = new MulticastSocket(getMulticastPort());
            socket.joinGroup(getMulticastInetAddress());
            socket.setSoTimeout(ServiceConstants.RESPONDER_SOCKET_TIMEOUT);
            setMulticastSocket(socket);

        } catch (UnknownHostException uhe) {

            System.err.println("Unexpected exception: " + uhe);
            uhe.printStackTrace();
            setTerminate(true);

        } catch (IOException ex) {

            System.err.println("Unexpected exception: " + ex);
            ex.printStackTrace();
            setTerminate(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        byte[] buf = new byte[ServiceConstants.DATAGRAM_LENGTH];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        setReceivedDatagramPacket(packet);
        while (!isTerminate()) {

            try {

                Arrays.fill(buf, (byte) 0);
                MulticastSocket socket = getMulticastSocket();
                if (socket != null) {

                    socket.receive(packet);

                    if (isQueryPacket()) {

                        if (!isPrevious()) {

                            DatagramPacket replyPacket = getReplyPacket();
                            setQueuedDatagramPacket(replyPacket);
                            sendQueuedPacket();
                        }
                    }
                }

            } catch (SocketTimeoutException ex) {

                // Nothing to do.

            } catch (IOException ex) {

                System.err.println("Unexpected exception: " + ex);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
    }

}

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
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import org.jflicks.util.RandomGUID;

/**
 * A client side job to discover server side services.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ServiceBrowserJob extends BaseDiscoveryJob
    implements Discoverable {

    private ArrayList<DiscoverListener> discoverList =
        new ArrayList<DiscoverListener>();

    private Timer myTimer;
    private String id;

    /**
     * Constructor with our one required argument.
     *
     * @param name A given service name.
     */
    public ServiceBrowserJob(String name) {

        super(name);
        setId(RandomGUID.createGUID());
    }

    private String getId() {
        return (id);
    }

    private void setId(String s) {
        id = s;
    }

    /**
     * {@inheritDoc}
     */
    public void addDiscoverListener(DiscoverListener l) {
        discoverList.add(l);
    }

    /**
     * {@inheritDoc}
     */
    public void removeDiscoverListener(DiscoverListener l) {
        discoverList.remove(l);
    }

    /**
     * Start the timer that repeats so new services can be found.
     */
    public void startLookup() {

        if (myTimer == null) {

            myTimer = new Timer("QueryTimer");
            myTimer.scheduleAtFixedRate(new QueryTimerTask(), 0L,
                ServiceConstants.BROWSER_QUERY_INTERVAL);
        }
    }

    /**
     * Start a single lookup.
     */
    public void startSingleLookup() {

        if (myTimer == null) {

            myTimer = new Timer("QueryTimer");
            myTimer.schedule(new QueryTimerTask(), 0L);
            myTimer = null;
        }
    }

    /**
     * Stop the timer that repeats so new services can be found.
     */
    public void stopLookup() {

        if (myTimer != null) {

            myTimer.cancel();
            myTimer = null;
        }
    }

    private boolean isReplyPacket() {

        boolean result = false;

        DatagramPacket packet = getReceivedDatagramPacket();
        if (packet != null) {

            String s = new String(packet.getData());
            int pos = s.indexOf((char) 0);
            if (pos > -1) {
                s = s.substring(0, pos);
            }

            /* REQUIRED TOKEN TO START */
            result = s.startsWith("SERVICE REPLY " + getEncodedServiceName());
        }

        return (result);
    }

    private ServiceDescription getServiceDescription() {

        ServiceDescription result = null;

        DatagramPacket packet = getReceivedDatagramPacket();
        if (packet != null) {

            String s = new String(packet.getData());
            int pos = s.indexOf((char) 0);
            if (pos > -1) {
                s = s.substring(0, pos);
            }

            StringTokenizer tokens = new StringTokenizer(s.substring(15
                + getEncodedServiceName().length()));
            if (tokens.countTokens() == 3) {

                result = ServiceDescription.parse(tokens.nextToken(),
                    tokens.nextToken(), tokens.nextToken());
            }
        }

        return (result);
    }

    private DatagramPacket getQueryPacket() {

        DatagramPacket result = null;

        StringBuilder buf = new StringBuilder();
        buf.append("SERVICE QUERY " + getEncodedServiceName());
        buf.append(" " + getId());

        byte[] bytes = buf.toString().getBytes();
        result = new DatagramPacket(bytes, bytes.length);
        result.setAddress(getMulticastInetAddress());
        result.setPort(getMulticastPort());

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
            socket.setSoTimeout(ServiceConstants.BROWSER_SOCKET_TIMEOUT);
            setMulticastSocket(socket);
            startLookup();

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

        while (!isTerminate()) {

            byte[] buf = new byte[ServiceConstants.DATAGRAM_LENGTH];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            setReceivedDatagramPacket(packet);

            try {

                MulticastSocket socket = getMulticastSocket();
                if (socket != null) {

                    socket.receive(packet);
                    if (isReplyPacket()) {

                        ServiceDescription sd = getServiceDescription();
                        if (sd != null) {

                            fireDiscoverEvent(sd);
                        }
                    }
                }

            } catch (SocketTimeoutException ex) {
            } catch (IOException ex) {

                System.err.println("Unexpected exception: " + ex);
            }

            sendQueuedPacket();
        }

    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
        stopLookup();
    }

    private void fireDiscoverEvent(ServiceDescription sd) {
        processDiscoverEvent(new DiscoverEvent(this, sd));
    }

    private synchronized void processDiscoverEvent(DiscoverEvent event) {

        for (int i = 0; i < discoverList.size(); i++) {

            DiscoverListener l = discoverList.get(i);
            l.serviceReply(event);
        }
    }

    class QueryTimerTask extends TimerTask {

        public void run() {

            DatagramPacket packet = getQueryPacket();
            if (packet != null) {

                setQueuedDatagramPacket(packet);
            }
        }
    }

}

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
package org.jflicks.util;

import java.io.File;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

import org.jflicks.job.SystemJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;

/**
 * A class that will find out the hostname of the local machine.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class Hostname {

    private Hostname() {
    }

    /**
     * Try to determine the IP of the local machine.  A machine may actually
     * have several addresses associated with it.  What we want to do is find
     * one that is not a loopback address.  We want a "real" one that we can
     * use for communicating with other machines on the local network.
     *
     * @return A String representing an IP address.
     */
    public static String getHostname() {

        String result = null;

        // This should work on Linux and Windows - not sure about OS/X.
        SystemJob job = SystemJob.getInstance("hostname");
        JobContainer jc = JobManager.getJobContainer(job);
        jc.start();

        boolean done = false;
        int count = 0;
        while (jc.isAlive()) {

            if (!jc.isAlive()) {

                done = true;

            } else {

                count += 100;
                if (count > 2900) {

                    done = true;

                } else {

                    JobManager.sleep(100);
                }
            }
        }

        result = job.getOutputText();
        if (result != null) {
            result = result.trim();
        }

        return (result);
    }

    public static InetAddress getInetAddressViaNetworkInterface() {

        InetAddress result = null;

        try {

            Enumeration<NetworkInterface> e =
                NetworkInterface.getNetworkInterfaces();
            if (e != null) {

                boolean done = false;
                while ((e.hasMoreElements()) && (!done)) {

                    NetworkInterface ni = e.nextElement();

                    if ((!ni.isLoopback()) && (ni.isUp())) {

                        List<InterfaceAddress> elist =
                            ni.getInterfaceAddresses();
                        if (elist != null) {

                            for (int i = 0; i < elist.size(); i++) {

                                InterfaceAddress addr = elist.get(i);
                                if (addr.getBroadcast() != null) {

                                    InetAddress inet = addr.getAddress();
                                    if (!isLoopback(inet.getHostAddress())) {

                                        result = inet;
                                        done = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (SocketException ex) {
        }

        return (result);
    }

    public static String getHostAddressViaNetworkInterface() {

        String result = null;

        try {

            Enumeration<NetworkInterface> e =
                NetworkInterface.getNetworkInterfaces();
            if (e != null) {

                boolean done = false;
                while ((e.hasMoreElements()) && (!done)) {

                    NetworkInterface ni = e.nextElement();

                    if ((!ni.isLoopback()) && (ni.isUp())) {

                        Enumeration<InetAddress> einet = ni.getInetAddresses();
                        if (einet != null) {

                            while ((einet.hasMoreElements()) && (!done)) {

                                InetAddress inet = einet.nextElement();
                                String tmp = inet.getHostAddress();
                                if (!isLoopback(tmp)) {

                                    result = tmp;
                                    done = true;
                                }
                            }
                        }
                    }
                }
            }

        } catch (SocketException ex) {
        }

        return (result);
    }

    /**
     * Find the local machine IP address.  First we lookup using the java
     * InetAddress object for localhost.  Sometimes this will return a
     * loopback address.  If so lets look further to get all the addresses
     * for the hostname of this machine.  Perhaps we will find a non-loopback
     * address we can use to communicate on the local network.
     *
     * @return A String representing an IP address.
     */
    public static String getHostAddress() {

        String result = null;

        try {

            // First see if localhost returns the right thing...
            result = InetAddress.getLocalHost().getHostAddress();
            if (result != null) {

                if (isLoopback(result)) {

                    // Next lets look at the NetworkInterfaces route...
                    result = getHostAddressViaNetworkInterface();

                    if (isLoopback(result)) {

                        // We got the loopback - lets try the hostname route...
                        String hname = getHostname();
                        if (hname != null) {

                            // Get all that we have.
                            InetAddress[] addrs =
                                InetAddress.getAllByName(hname);
                            if ((addrs != null) && (addrs.length > 0)) {

                                for (int i = 0; i < addrs.length; i++) {

                                    result = addrs[i].getHostAddress();
                                    if (!isLoopback(result)) {

                                        // We take the first non-loopback....
                                        break;
                                    }
                                }

                            } else {

                                // Give up and return loopback...
                                result = "127.0.0.1";
                            }

                        } else {

                            // Give up and return loopback...
                            result = "127.0.0.1";

                        }

                    } else {

                        // Give up and return loopback...
                        result = "127.0.0.1";
                    }
                }

            } else {

                // Give up and return loopback...
                result = "127.0.0.1";
            }

        } catch (UnknownHostException ex) {

            // Give up and return loopback...
            result = "127.0.0.1";
        }

        return (result);
    }

    /**
     * Find the local machine InetAddress.  First we lookup using the java
     * InetAddress object for localhost.  Sometimes this will return a
     * loopback address.  If so lets look further to get all the addresses
     * for the hostname of this machine.  Perhaps we will find a non-loopback
     * address we can use to communicate on the local network.
     *
     * @return An InetAddress instance.
     */
    public static InetAddress getLocalhostAddress() {

        InetAddress result = null;

        try {

            // First see if localhost returns the right thing...
            String ip = InetAddress.getLocalHost().getHostAddress();
            if (ip != null) {

                if (isLoopback(ip)) {

                    // Next lets look at the NetworkInterfaces route...
                    result = getInetAddressViaNetworkInterface();

                    if (isLoopback(result)) {

                        // We got the loopback - lets try the hostname route...
                        String hname = getHostname();
                        if (hname != null) {

                            // Get all that we have.
                            InetAddress[] addrs =
                                InetAddress.getAllByName(hname);
                            if ((addrs != null) && (addrs.length > 0)) {

                                for (int i = 0; i < addrs.length; i++) {

                                    ip = addrs[i].getHostAddress();
                                    if (!isLoopback(ip)) {

                                        // We take the first non-loopback....
                                        result = addrs[i];
                                        break;
                                    }
                                }
                            }
                        }
                    }

                } else {

                    result = InetAddress.getLocalHost();
                }
            }

        } catch (UnknownHostException ex) {
        }

        if ((result == null) && (Util.isLinux())) {

            result = checkLinux();
        }

        return (result);
    }

    public static InetAddress checkLinux() {

        InetAddress result = null;

        // If we have gotten here then the /etc/hosts is not set right.  So
        // let's look for /etc/network/interfaces and try to get a static
        // IP there.
        String[] lines = Util.readTextFile(new File("/etc/network/interfaces"));
        if (lines != null) {

            boolean found = false;
            for (int i = 0; i < lines.length; i++) {

                String tmp = lines[i].trim();
                if (!found) {

                    if (tmp.startsWith("iface eth0 inet static")) {

                        found = true;
                    }

                } else {

                    if (tmp.startsWith("address")) {

                        tmp = tmp.substring(tmp.indexOf(" "));
                        tmp = tmp.trim();

                        try {

                            result = InetAddress.getByName(tmp);

                        } catch (UnknownHostException ex) {
                        }
                        break;
                    }
                }
            }
        }

        return (result);
    }

    /**
     * Convenience method to examine a given String to see if it looks like
     * a loopback address.
     *
     * @param s A given String representing an address.
     * @return True if it looks like a loopback address.
     */
    public static boolean isLoopback(String s) {

        boolean result = false;

        if (s != null) {

            if ((s.startsWith("127")) || (s.startsWith("0:0:0:0"))) {

                result = true;
            }
        }

        return (result);
    }

    public static boolean isLoopback(InetAddress inet) {

        boolean result = true;

        if (inet != null) {

            try {

                result = isLoopback(inet.getLocalHost().getHostAddress());

            } catch (UnknownHostException ex) {
            }
        }

        return (result);
    }

}

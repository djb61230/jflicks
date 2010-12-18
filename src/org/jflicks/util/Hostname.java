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

import java.util.ArrayList;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jflicks.job.SystemJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;

/**
 * A class that will find out the hostname of the local machine.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Hostname {

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

                    // We got the loopback - lets try the hostname route...
                    String hname = getHostname();
                    if (hname != null) {

                        // Get all that we have.
                        InetAddress[] addrs = InetAddress.getAllByName(hname);
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

                    // We got the loopback - lets try the hostname route...
                    String hname = getHostname();
                    if (hname != null) {

                        // Get all that we have.
                        InetAddress[] addrs = InetAddress.getAllByName(hname);
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

                } else {

                    result = InetAddress.getLocalHost();
                }
            }

        } catch (UnknownHostException ex) {
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

}

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
package org.jflicks.stb.directvweb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.jflicks.stb.BaseSTB;
import org.jflicks.util.Util;

/**
 * The STB implementation that can change channels on a modern DirecTV
 * set top box that has an ethernet port.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DirectvWeb extends BaseSTB {

    private String host;
    private int port;

    /**
     * Simple constructor.
     */
    public DirectvWeb() {

        setTitle("DirectvWeb");
        setPort(8080);
    }

    /**
     * The DirecTV box has a host or IP address.
     *
     * @return The IP or host as a String.
     */
    public String getHost() {
        return (host);
    }

    /**
     * The DirecTV box has a host or IP address.
     *
     * @param s The IP or host as a String.
     */
    public void setHost(String s) {
        host = s;
    }

    /**
     * The DirecTV box is listening on a particular port.
     *
     * @return The port value.
     */
    public int getPort() {
        return (port);
    }

    /**
     * The DirecTV box is listening on a particular port.
     *
     * @param i The port value.
     */
    public void setPort(int i) {
        port = i;
    }

    /**
     * {@inheritDoc}
     */
    public void changeChannel(String s) {

        String h = getHost();
        if ((h != null) && (s != null)) {

            String major = s;
            String minor = null;

            // Check for a minus in the channel
            int index = s.indexOf("-");
            if (index == -1) {

                // No minus, check for dot
                index = s.indexOf(".");
            }

            // If we have an index then either a minus or dot exists
            if (index != -1) {

                major = s.substring(index);
                minor = s.substring(index + 1);
            }

            String urlstr = null;

            if (minor != null) {

                urlstr = "http://" + host + ":" + getPort()
                    + "/tv/tune?major=" + major + "&minor=" + minor;

            } else {

                urlstr = "http://" + host + ":" + getPort()
                    + "/tv/tune?major=" + major;
            }

            System.out.println("urlstr: <" + urlstr + ">");
            if (urlstr != null) {

                try {

                    URL url = new URL(urlstr);
                    URLConnection urlc = url.openConnection();
                    BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlc.getInputStream()));

                    String inputLine = null;
                    while ((inputLine = in.readLine()) != null) {

                        System.out.println(inputLine);
                    }

                    in.close();

                } catch (IOException ex) {

                    System.out.println(ex.getMessage());
                }
            }
        }

    }

    /**
     * The main method to run as a command line.
     *
     * @param args The arguments as an array of String instances.
     */
    public static void main(String[] args) {

        String h = null;
        int p = 8080;
        String channel = null;

        for (int i = 0; i < args.length; i += 2) {

            if (args[i].equalsIgnoreCase("-host")) {

                h = args[i + 1];

            } else if (args[i].equalsIgnoreCase("-port")) {

                p = Util.str2int(args[i + 1], p);

            } else if (args[i].equalsIgnoreCase("-channel")) {

                channel = args[i + 1];
            }
        }

        if ((h != null) && (channel != null)) {

            DirectvWeb stb = new DirectvWeb();
            stb.setHost(h);
            stb.setPort(p);
            stb.changeChannel(channel);
        }
    }

}


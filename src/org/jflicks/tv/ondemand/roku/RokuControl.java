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
package org.jflicks.tv.ondemand.roku;

import java.io.InputStream;
import java.io.PrintStream;
import org.apache.commons.net.telnet.TelnetClient;

/**
 * Simple class that can communicate with a Roku box and send it
 * commands.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RokuControl {

    private String host;
    private int port;
    private InputStream inputStream;
    private PrintStream printStream;
    private TelnetClient telnetClient;

    /**
     * Constructor with two required arguments.
     *
     * @param host The host or IP as a String.
     * @param port The port that the Roku is listening upon.
     */
    public RokuControl(String host, int port) {

        setHost(host);
        setPort(port);
    }

    private String getHost() {
        return (host);
    }

    private void setHost(String s) {
        host = s;
    }

    private int getPort() {
        return (port);
    }

    private void setPort(int i) {
        port = i;
    }

    private String readPrompt(InputStream in) {

        String result = null;

        try {

            char lastChar = '>';
            StringBuffer sb = new StringBuffer();
            char ch = (char) in.read();
            while (true) {

                System.out.print(ch);
                sb.append(ch);
                if (ch == lastChar) {

                    if (sb.toString().endsWith(">")) {
                        result = sb.toString();
                    }
                }
                ch = (char) in.read();
            }

        } catch (Exception ex) {

            System.out.println(ex.getMessage());
        }

        return (result);
    }

    private void write(PrintStream out, String value) {

        try {

            out.println(value);
            out.flush();
            System.out.println(value);

        } catch (Exception ex) {

            System.out.println(ex.getMessage());
        }
    }

    private void sendCommand(PrintStream out, String command) {

        try {

            write(out, command);

        } catch (Exception ex) {

            System.out.println(ex.getMessage());

            telnetClient = null;
            getTelnetClient();
        }
    }

    private TelnetClient getTelnetClient() {

        if (telnetClient == null) {

            try {

                telnetClient = new TelnetClient();
                telnetClient.connect(getHost(), getPort());

                printStream = new PrintStream(telnetClient.getOutputStream());

            } catch (Exception ex) {

                telnetClient = null;
                printStream = null;
            }
        }

        return (telnetClient);
    }

    /**
     * Send the String based command.
     *
     * @param cmd The command String.
     */
    public void command(String cmd) {

        TelnetClient c = getTelnetClient();
        if (c != null) {

            sendCommand(printStream, cmd);
        }
    }

    /**
     * Clean up any resources with have used.
     */
    public void close() {

        if (telnetClient != null) {

            try {

                printStream.close();
                telnetClient.disconnect();
                printStream = null;
                telnetClient = null;

            } catch (Exception ex) {

                printStream = null;
                telnetClient = null;
            }
        }
    }

}


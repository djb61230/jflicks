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
package org.jflicks.stb.directvserial;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

import org.jflicks.stb.BaseSTB;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * The STB implementation that can change channels on a modern DirecTV
 * set top box that has an ethernet port.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class DirectvSerial extends BaseSTB implements SerialPortEventListener {

    private String port;
    private InputStream inputStream;

    /**
     * Simple constructor.
     */
    public DirectvSerial() {

        setTitle("DirectvSerial");
        setPort("/dev/ttyS0");
    }

    public String getPort() {
        return (port);
    }

    public void setPort(String s) {
        port = s;
    }

    private byte[] toBytes(char ch) {

        byte[] result = null;

        result = new byte[5];
        result[0] = (byte) 0xfa;
        result[1] = (byte) 0xa5;
        result[2] = (byte) 0x00;
        result[3] = (byte) 0x01;
        switch (ch) {

        case '0':
            result[4] = (byte) 0xe0;
            break;
        case '1':
            result[4] = (byte) 0xe1;
            break;
        case '2':
            result[4] = (byte) 0xe2;
            break;
        case '3':
            result[4] = (byte) 0xe3;
            break;
        case '4':
            result[4] = (byte) 0xe4;
            break;
        case '5':
            result[4] = (byte) 0xe5;
            break;
        case '6':
            result[4] = (byte) 0xe6;
            break;
        case '7':
            result[4] = (byte) 0xe7;
            break;
        case '8':
            result[4] = (byte) 0xe8;
            break;
        case '9':
            result[4] = (byte) 0xe9;
            break;
        case '\n':
            result[4] = (byte) 0xa0;
            break;
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void changeChannel(String s) {

        String p = getPort();
        if ((p != null) && (s != null)) {

            try {

                s = s + "\n";
                boolean found = false;
                CommPortIdentifier pid = null;
                Enumeration portList = CommPortIdentifier.getPortIdentifiers();
                while (portList.hasMoreElements()) {

                    pid = (CommPortIdentifier) portList.nextElement();
                    if (pid.getPortType() == CommPortIdentifier.PORT_SERIAL) {

                        if (p.equals(pid.getName())) {

                            found = true;
                            break;
                        }
                    }
                }

                if (found) {

                    CommPort cp = pid.open("DirectvSerial", 2000);
                    SerialPort sp = (SerialPort) cp;
                    sp.addEventListener(this);
                    sp.notifyOnParityError(true);
                    sp.notifyOnDataAvailable(true);
                    sp.setSerialPortParams(9600, SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    sp.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
                    inputStream = sp.getInputStream();
                    OutputStream out = sp.getOutputStream();
                    System.out.println("before");
                    for (int i = 0; i < s.length(); i++) {

                        byte[] data = toBytes(s.charAt(i));
                        for (int j = 0; j < data.length; j++) {
                            System.out.println("write: "
                                + Integer.toHexString((int) (data[j] & 0x000000ff)));
                        }
                        out.write(data);
                        Thread.sleep(1000);
                    }
                    System.out.println("after");
                    out.close();
                    cp.close();
                }

            } catch (Exception ex) {

                System.out.println(ex.getMessage());
            }
        }
    }

    public void serialEvent(SerialPortEvent event) {

        switch (event.getEventType()) {

        case SerialPortEvent.BI:
        case SerialPortEvent.OE:
        case SerialPortEvent.FE:
        case SerialPortEvent.PE:
        case SerialPortEvent.CD:
        case SerialPortEvent.CTS:
        case SerialPortEvent.DSR:
        case SerialPortEvent.RI:
        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
            System.out.println("herku");
            break;

        case SerialPortEvent.DATA_AVAILABLE:
            try {

                while (inputStream.available() > 0) {

                    int val = inputStream.read();
                    System.out.println("read: " + Integer.toHexString(val));
                }

            } catch (IOException ex) {
            }
            break;
        }
    }

    public static void main(String[] args) {

        String p = "/dev/ttyS0";
        String channel = null;

        for (int i = 0; i < args.length; i += 2) {

            if (args[i].equalsIgnoreCase("-port")) {

                p = args[i + 1];

            } else if (args[i].equalsIgnoreCase("-channel")) {

                channel = args[i + 1];
            }
        }

        if ((p != null) && (channel != null)) {

            DirectvSerial stb = new DirectvSerial();
            stb.setPort(p);
            stb.changeChannel(channel);
            System.exit(0);
        }
    }

}


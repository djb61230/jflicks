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

import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class Spew {

    /**
     * Default empty constructor.
     */
    private Spew() {
    }

    /**
     * Simple main method that dumps the system properties to stdout.
     *
     * @param args Arguments that happen to be ignored.
     */
    public static void main(String[] args) {

        byte[] buffer = new byte[2048];

        try {

            int number = 0;
            DatagramSocket socket = new DatagramSocket();
            InetAddress addr = InetAddress.getByName("192.168.2.2");
            FileInputStream fis = new FileInputStream("test.mpg");
            while (fis.available() > 0) {

                int count = fis.read(buffer);
                DatagramPacket packet =
                    new DatagramPacket(buffer, count, addr, 1234);
                socket.send(packet);
                number++;
                Thread.sleep(1);
            }

            fis.close();

        } catch (Exception ex) {

            System.out.println(ex.getMessage());
        }
    }

}


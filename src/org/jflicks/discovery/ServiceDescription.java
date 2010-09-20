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

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;

/**
 * A simple class that contains all the properties to simply define a service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ServiceDescription implements Comparable<ServiceDescription> {

    private String instanceName;
    private int port;
    private InetAddress address;

    /**
     * Simple empty constructor.
     */
    public ServiceDescription() {
    }

    /**
     * The address of this service.
     *
     * @return An InetAddress instance.
     */
    public InetAddress getAddress() {
        return (address);
    }

    /**
     * The address of this service.
     *
     * @param serviceAddress An InetAddress instance.
     */
    public void setAddress(InetAddress serviceAddress) {
        address = serviceAddress;
    }

    /**
     * Convenience method to get the address as a string.
     *
     * @return The address of this computer as a string.
     */
    public String getAddressAsString() {
        return (getAddress().getHostAddress());
    }

    /**
     * The instance name.
     *
     * @return The instance name.
     */
    public String getInstanceName() {
        return (instanceName);
    }

    /**
     * The instance name.
     *
     * @param s The instance name.
     */
    public void setInstanceName(String s) {
        instanceName = s;
    }

    /**
     * Encode the name that it is "network friendly".
     *
     * @return An encoded string.
     */
    public String getEncodedInstanceName() {

        String result = null;

        try {

            result = URLEncoder.encode(getInstanceName(), "UTF-8");

        } catch (UnsupportedEncodingException uee) {

            result = null;
        }

        return (result);
    }

    /**
     * The port the service is running on.
     *
     * @return An int value.
     */
    public int getPort() {
        return (port);
    }

    /**
     * The port the service is running on.
     *
     * @param i An int value.
     */
    public void setPort(int i) {
        port = i;
    }

    private String getPortAsString() {
        return ("" + getPort());
    }

    /**
     * Make a nice to string method.
     *
     * @return A string detailing the properties of this instance.
     */
    public String toString() {

        StringBuilder buf = new StringBuilder();

        buf.append(getEncodedInstanceName());
        buf.append(" ");
        buf.append(getAddressAsString());
        buf.append(" ");
        buf.append(getPortAsString());

        return (buf.toString());
    }

    /**
     * The equals override method.
     *
     * @param o A gven object to check.
     * @return True if the objects are equal.
     */
    public boolean equals(Object o) {

        boolean result = false;

        if (o == this) {

            result = true;

        } else if (!(o instanceof ServiceDescription)) {

            result = false;

        } else {

            ServiceDescription descriptor = (ServiceDescription) o;
            result = descriptor.getInstanceName().equals(getInstanceName());
        }

        return (result);
    }

    /**
     * The standard hashcode override.
     *
     * @return An int value.
     */
    public int hashCode() {
        return (getInstanceName().hashCode());
    }

    /**
     * The comparable interface.
     *
     * @param sd The given ServiceDescription instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(ServiceDescription sd) throws ClassCastException {

        int result = 0;

        if (sd == null) {

            throw new NullPointerException();
        }

        if (sd == this) {

            result = 0;

        } else {

            result = getInstanceName().compareTo(sd.getInstanceName());
        }

        return (result);
    }

    /**
     * Take a string and parse it to set property values.
     *
     * @param encodedInstanceName Name that is encoded for network transport.
     * @param addressAsString IP address.
     * @param portAsString Port as a string.
     * @return A ServiceDescription instance.
     */
    public static ServiceDescription parse(String encodedInstanceName,
        String addressAsString, String portAsString) {

        ServiceDescription result = new ServiceDescription();

        try {

            String name = URLDecoder.decode(encodedInstanceName, "UTF-8");
            if ((name == null) || (name.length() == 0)) {

                result = null;

            } else {

                result.setInstanceName(name);
            }

        } catch (UnsupportedEncodingException uee) {

            System.err.println("Unexpected exception: " + uee);
            uee.printStackTrace();
            result = null;
        }

        if (result != null) {

            try {

                InetAddress addr = InetAddress.getByName(addressAsString);
                result.setAddress(addr);

            } catch (UnknownHostException uhe) {

                System.err.println("Unexpected exception: " + uhe);
                uhe.printStackTrace();
                result = null;
            }
        }

        if (result != null) {

            try {

                int p = Integer.parseInt(portAsString);
                result.setPort(p);

            } catch (NumberFormatException nfe) {

                System.err.println("Unexpected exception: " + nfe);
                nfe.printStackTrace();
                result = null;
            }
        }

        return (result);
    }

}

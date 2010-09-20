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
package org.jflicks.tv.recorder.v4l2;

import java.util.ArrayList;

/**
 * This bean encapsulates the basic properties for a found V4l2 device.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class V4l2Device {

    private String path;
    private String driverName;
    private String cardType;
    private String busInfo;
    private String driverVersion;
    private String capabilitiesMask;
    private ArrayList<String> capabilityList;

    /**
     * Simple no argument constructor.
     */
    public V4l2Device() {

        setCapabilityList(new ArrayList<String>());
    }

    /**
     * V4l2 devices are in /dev and (for our purposes) are named videoN
     * where N is the number device.  The path will be something like
     * /dev/video0.
     *
     * @return the path of the device.
     */
    public String getPath() {
        return (path);
    }

    /**
     * V4l2 devices are in /dev and (for our purposes) are named videoN
     * where N is the number device.  The path will be something like
     * /dev/video0.
     *
     * @param s the path of the device.
     */
    public void setPath(String s) {
        path = s;
    }

    /**
     * The driver name (ivtv, etc).
     *
     * @return The driver name as a String.
     */
    public String getDriverName() {
        return (driverName);
    }

    /**
     * The driver name (ivtv, etc).
     *
     * @param s The driver name as a String.
     */
    public void setDriverName(String s) {
        driverName = s;
    }

    /**
     * This is a descriptive String showiing the brand of device.
     *
     * @return A String instance.
     */
    public String getCardType() {
        return (cardType);
    }

    /**
     * This is a descriptive String showiing the brand of device.
     *
     * @param s A String instance.
     */
    public void setCardType(String s) {
        cardType = s;
    }

    /**
     * Where the device is located in the computer (PCI, USB).
     *
     * @return The bus info as a String.
     */
    public String getBusInfo() {
        return (busInfo);
    }

    /**
     * Where the device is located in the computer (PCI, USB).
     *
     * @param s The bus info as a String.
     */
    public void setBusInfo(String s) {
        busInfo = s;
    }

    /**
     * The version of the Linux driver.
     *
     * @return The version as a String.
     */
    public String getDriverVersion() {
        return (driverVersion);
    }

    /**
     * The version of the Linux driver.
     *
     * @param s The version as a String.
     */
    public void setDriverVersion(String s) {
        driverVersion = s;
    }

    /**
     * V4l2 defines a capabilities mask.
     *
     * @return A mask as a String.
     */
    public String getCapabilitiesMask() {
        return (capabilitiesMask);
    }

    /**
     * V4l2 defines a capabilities mask.
     *
     * @param s A mask as a String.
     */
    public void setCapabilitiesMask(String s) {
        capabilitiesMask = s;
    }

    private ArrayList<String> getCapabilityList() {
        return (capabilityList);
    }

    private void setCapabilityList(ArrayList<String> l) {
        capabilityList = l;
    }

    /**
     * Convenience method to add a capability.
     *
     * @param s A given capability String.
     */
    public void addCapability(String s) {

        ArrayList<String> l = getCapabilityList();
        if ((l != null) && (s != null)) {

            l.add(s);
        }
    }

    /**
     * Convenience method to clear all capability String instances.
     */
    public void clear() {

        ArrayList<String> l = getCapabilityList();
        if (l != null) {
            l.clear();
        }

        setDriverName(null);
        setCardType(null);
        setBusInfo(null);
        setDriverVersion(null);
        setCapabilitiesMask(null);
    }

    /**
     * The capabilities are detailed by one or more String instances.
     *
     * @return An array of String objects.
     */
    public String[] getCapabilities() {

        String[] result = null;

        ArrayList<String> l = getCapabilityList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new String[l.size()]);
        }

        return (result);
    }

}

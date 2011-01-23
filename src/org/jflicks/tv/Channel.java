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
package org.jflicks.tv;

import java.io.Serializable;

import org.jflicks.util.Util;

/**
 * This class contains all the properties representing a TV channel.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Channel implements Serializable, Comparable<Channel> {

    private int id;
    private String name;
    private int frequency;
    private String number;
    private String affiliate;
    private String callSign;
    private String listingId;
    private String referenceNumber;

    /**
     * Simple empty constructor.
     */
    public Channel() {
    }

    /**
     * Be able to copy a Channel instance from an instance.
     *
     * @param c A given Channel to copy.
     */
    public Channel(Channel c) {

        if (c != null) {

            setId(c.getId());
            setName(c.getName());
            setFrequency(c.getFrequency());
            setNumber(c.getNumber());
            setReferenceNumber(c.getReferenceNumber());
            setAffiliate(c.getAffiliate());
            setCallSign(c.getCallSign());
            setListingId(c.getListingId());
        }
    }

    /**
     * A unique ID is associated with this object.
     *
     * @return An ID value as an int.
     */
    public int getId() {
        return (id);
    }

    /**
     * A unique ID is associated with this object.
     *
     * @param i An ID value as an int.
     */
    public void setId(int i) {
        id = i;
    }

    /**
     * A Channel has an associated name.  This is usually call letters for
     * OTA stations.  For example WKTV or for cable channels TBS or ESPN.
     *
     * @return The channel name.
     */
    public String getName() {
        return (name);
    }

    /**
     * A Channel has an associated name.  This is usually call letters for
     * OTA stations.  For example WKTV or for cable channels TBS or ESPN.
     *
     * @param s The channel name.
     */
    public void setName(String s) {
        name = s;
    }

    /**
     * A frequency value as an int.  An OTA station will have a VHF-UHF band
     * frequency value.
     *
     * @return The frequency as an int.
     */
    public int getFrequency() {
        return (frequency);
    }

    /**
     * A frequency value as an int.  An OTA station will have a VHF-UHF band
     * frequency value.
     *
     * @param i The frequency as an int.
     */
    public void setFrequency(int i) {
        frequency = i;
    }

    /**
     * The number of a channel as a String.  A string is used because an OTA
     * station might be something like "2.1" while a cable or satellite
     * station might be "254".  Since they can be formatted as an integer or
     * as a float value we make it a String.  As it leaves the possibility
     * to have the OTA be somethintg like "2_1".
     *
     * @return The number of the channel as a String.
     */
    public String getNumber() {
        return (number);
    }

    /**
     * The number of a channel as a String.  A string is used because an OTA
     * station might be something like "2.1" while a cable or satellite
     * station might be "254".  Since they can be formatted as an integer or
     * as a float value we make it a String.  As it leaves the possibility
     * to have the OTA be somethintg like "2_1".
     *
     * @param s The number of the channel as a String.
     */
    public void setNumber(String s) {
        number = s;
    }

    /**
     * The reference number of the channel is it's "natural" number.  What
     * we mean by that is a Channel that has a common channel number like
     * 24.1 but perhaps on a multichannel provider it might be some other
     * number like 868.  The Number property will be 868 but we want to
     * maintain it common channel number in the ReferenceNumber property.
     *
     * @return A String representing the reference number.
     */
    public String getReferenceNumber() {
        return (referenceNumber);
    }

    /**
     * The reference number of the channel is it's "natural" number.  What
     * we mean by that is a Channel that has a common channel number like
     * 24.1 but perhaps on a multichannel provider it might be some other
     * number like 868.  The Number property will be 868 but we want to
     * maintain it common channel number in the ReferenceNumber property.
     *
     * @param s A String representing the reference number.
     */
    public void setReferenceNumber(String s) {
        referenceNumber = s;
    }

    /**
     * This Channel is associated with a "listing" by the ListingId property.
     *
     * @return The listing ID as a String instance.
     */
    public String getListingId() {
        return (listingId);
    }

    /**
     * This Channel is associated with a "listing" by the ListingId property.
     *
     * @param s The listing ID as a String instance.
     */
    public void setListingId(String s) {
        listingId = s;
    }

    /**
     * This Channel has an affliate property.  Something like CBS for an
     * OTA channel.
     *
     * @return The affiliate as a String instance.
     */
    public String getAffiliate() {
        return (affiliate);
    }

    /**
     * This Channel has an affliate property.  Something like CBS for an
     * OTA channel.
     *
     * @param s The affiliate as a String instance.
     */
    public void setAffiliate(String s) {
        affiliate = s;
    }

    /**
     * This Channel has a call sign property.
     *
     * @return The the call sign as a String.
     */
    public String getCallSign() {
        return (callSign);
    }

    /**
     * This Channel has a call sign property.
     *
     * @param s The the call sign as a String.
     */
    public void setCallSign(String s) {
        callSign = s;
    }

    /**
     * Override to "Number Name".
     *
     * @return A String.
     */
    public String toString() {
        return (getNumber() + " " + getCallSign());
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

        } else if (!(o instanceof Channel)) {

            result = false;

        } else {

            Channel c = (Channel) o;
            String num = getNumber();
            if (num != null) {

                result = num.equals(c.getNumber());
            }
        }

        return (result);
    }

    /**
     * The standard hashcode override.
     *
     * @return An int value.
     */
    public int hashCode() {
        return (getNumber().hashCode());
    }

    /**
     * The comparable interface.
     *
     * @param c The given Channel instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(Channel c) throws ClassCastException {

        int result = 0;

        if (c == null) {

            throw new NullPointerException();
        }

        if (c == this) {

            result = 0;

        } else {

            Double num0 = Util.str2Double(getNumber(), 0.0);
            Double num1 = Util.str2Double(c.getNumber(), 0.0);
            result = num0.compareTo(num1);
        }

        return (result);
    }

}


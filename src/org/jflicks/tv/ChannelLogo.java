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

/**
 * A class to identify a Channel and an image logo.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ChannelLogo implements Serializable, Comparable<ChannelLogo> {

    private int channelId;
    private String url;
    private int height;
    private int width;
    private String md5;

    /**
     * Simple empty constructor.
     */
    public ChannelLogo() {
    }

    public int getChannelId() {
        return (channelId);
    }

    public void setChannelId(int i) {
        channelId = i;
    }

    public String getUrl() {
        return (url);
    }

    public void setUrl(String s) {
        url = s;
    }

    public int getHeight() {
        return (height);
    }

    public void setHeight(int i) {
        height = i;
    }

    public int getWidth() {
        return (width);
    }

    public void setWidth(int i) {
        width = i;
    }

    public String getMd5() {
        return (md5);
    }

    public void setMd5(String s) {
        md5 = s;
    }

    /**
     * The standard hashcode override.
     *
     * @return An int value.
     */
    public int hashCode() {
        return (getChannelId());
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

        } else if (!(o instanceof ChannelLogo)) {

            result = false;

        } else {

            Integer myiobj = Integer.valueOf(getChannelId());
            ChannelLogo c = (ChannelLogo) o;
            Integer iobj = Integer.valueOf(c.getChannelId());
            result = myiobj.equals(iobj);
        }

        return (result);
    }

    /**
     * The comparable interface.
     *
     * @param c The given ChannelLogo instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(ChannelLogo c) throws ClassCastException {

        int result = 0;

        if (c == null) {

            throw new NullPointerException();
        }

        if (c == this) {

            result = 0;

        } else {

            Integer c0 = Integer.valueOf(getChannelId());
            Integer c1 = Integer.valueOf(c.getChannelId());

            result = c0.compareTo(c1);
        }

        return (result);
    }

}


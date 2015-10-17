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
package org.jflicks.tv.programdata.sd.json;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * A container class to be able to mess with our properties file for
 * channels.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ChannelItem implements Serializable, Comparable<ChannelItem> {

    private String id;
    private String number;
    private String name;

    /**
     * Simple empty constructor.
     */
    public ChannelItem() {
    }

    public String getId() {
        return (id);
    }

    public void setId(String s) {
        id = s;
    }

    public String getNumber() {
        return (number);
    }

    public void setNumber(String s) {
        number = s;
    }

    public String getName() {
        return (name);
    }

    public void setName(String s) {
        name = s;
    }

    public static ChannelItem[] parse(String s) {

        ChannelItem[] result = null;

        if (s != null) {

            String[] lines = s.split("\n");
            if ((lines != null) && (lines.length > 0)) {

                ArrayList<ChannelItem> l = new ArrayList<ChannelItem>();
                for (int i = 0; i < lines.length; i++) {

                    String[] keyval = lines[i].split("=");
                    if ((keyval != null) && (keyval.length == 2)) {

                        String key = keyval[0];
                        String val = keyval[1];
                        String[] array = val.split("\\|");
                        if ((array != null) && (array.length == 2)) {

                            ChannelItem ci = new ChannelItem();
                            ci.setId(key);
                            ci.setNumber(array[0]);
                            ci.setName(array[1]);
                            l.add(ci);
                        }
                    }
                }

                if (l.size() > 0) {

                    result = l.toArray(new ChannelItem[l.size()]);
                }
            }
        }

        return (result);
    }

    public static String toTextFormat(ChannelItem[] array) {

        String result = null;

        if ((array != null) && (array.length > 0)) {

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; i++) {

                sb.append(array[i].getId());
                sb.append("=");
                sb.append(array[i].getNumber());
                sb.append("|");
                sb.append(array[i].getName());
                sb.append("\n");
            }

            if (sb.length() > 0) {

                result = sb.toString();
            }
        }

        return (result);
    }

    private Double str2Double(String s, double defaultValue) {

        double result = defaultValue;

        if (s != null) {

            try {

                result = Double.valueOf(s);

            } catch (Exception ex) {

                result = Double.valueOf(defaultValue);
            }
        }

        return (result);
    }

    public String toString() {

        return (getNumber() + " " + getName());
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

        } else if (!(o instanceof ChannelItem)) {

            result = false;

        } else {

            ChannelItem c = (ChannelItem) o;
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
     * @param c The given ChannelItem instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(ChannelItem c) throws ClassCastException {

        int result = 0;

        if (c == null) {

            throw new NullPointerException();
        }

        if (c == this) {

            result = 0;

        } else {

            Double num0 = str2Double(getNumber(), 0.0);
            Double num1 = str2Double(c.getNumber(), 0.0);
            result = num0.compareTo(num1);
        }

        return (result);
    }

}


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
package org.jflicks.configure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class implements the Configuration interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class BaseConfiguration implements Configuration, Serializable {

    private String name;
    private String source;
    private ArrayList<NameValue> nameValueList;

    /**
     * Simple empty constructor.
     */
    public BaseConfiguration() {

        setNameValueList(new ArrayList<NameValue>());
    }

    /**
     * Simple constructor to "clone" a BaseConfiguration instance.
     *
     * @param bc A given BaseConfiguration to "clone".
     */
    public BaseConfiguration(BaseConfiguration bc) {

        this();
        if (bc != null) {

            setName(bc.getName());
            setSource(bc.getSource());
            ArrayList<NameValue> l = bc.getNameValueList();
            if ((l != null) && (l.size() > 0)) {

                for (int i = 0; i < l.size(); i++) {

                    addNameValue(new NameValue(l.get(i)));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return (name);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The name.
     */
    public void setName(String s) {
        name = s;
    }

    /**
     * {@inheritDoc}
     */
    public String getSource() {
        return (source);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The source.
     */
    public void setSource(String s) {
        source = s;
    }

    /**
     * {@inheritDoc}
     */
    public NameValue[] getNameValues() {

        NameValue[] result = null;

        ArrayList<NameValue> l = getNameValueList();
        if (l != null) {

            result = l.toArray(new NameValue[l.size()]);
        }

        return (result);
    }

    /**
     * Convenience method to set this property.
     *
     * @param array An array of NameValue instances.
     */
    public void setNameValues(NameValue[] array) {

        ArrayList<NameValue> l = getNameValueList();
        if (l != null) {

            l.clear();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {
                    l.add(array[i]);
                }
            }
        }
    }

    private ArrayList<NameValue> getNameValueList() {
        return (nameValueList);
    }

    private void setNameValueList(ArrayList<NameValue> l) {
        nameValueList = l;
    }

    /**
     * Convenience method to add a NameValue instance to this configuration.
     *
     * @param nv The given NameValue instance to add.
     */
    public void addNameValue(NameValue nv) {

        ArrayList<NameValue> l = getNameValueList();
        if ((l != null) && (nv != null)) {
            l.add(nv);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getSummary() {

        String result = null;

        StringBuilder sb = new StringBuilder();
        String tmp = getName();
        if (tmp != null) {
            sb.append("Name: " + tmp + "\n");
        } else {
            sb.append("Name: No Name!\n");
        }

        tmp = getSource();
        if (tmp != null) {
            sb.append("Source: " + tmp + "\n\n");
        } else {
            sb.append("Source: No Source!\n\n");
        }

        ArrayList<NameValue> l = getNameValueList();
        if ((l != null) && (l.size() > 0)) {

            sb.append("Other properties:\n");
            for (int i = 0; i < l.size(); i++) {

                NameValue nv = l.get(i);
                if (nv != null) {

                    sb.append("\t" + nv.getName() + ": " + nv.getValue()
                        + "\n");
                }
            }

        } else {
            sb.append("No other properties defined!\n");
        }

        return (sb.toString());
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

        } else if (!(o instanceof BaseConfiguration)) {

            result = false;

        } else {

            BaseConfiguration bc = (BaseConfiguration) o;
            String src = bc.getSource();
            if (src != null) {

                result = src.equals(getSource());

                if (result) {

                    NameValue[] array = getNameValues();
                    NameValue[] bcarray = bc.getNameValues();
                    result = Arrays.equals(array, bcarray);
                }
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
        return (getSource().hashCode());
    }

    /**
     * The comparable interface.
     *
     * @param c The given Configuration instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(Configuration c) throws ClassCastException {

        int result = 0;

        if (c == null) {

            throw new NullPointerException();
        }

        if (c == this) {

            result = 0;

        } else {

            result = getSource().compareTo(c.getSource());
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public NameValue findNameValueByName(String s) {

        NameValue result = null;

        if (s != null) {

            NameValue[] array = getNameValues();
            if ((array != null) && (array.length > 0)) {

                for (int i = 0; i < array.length; i++) {

                    if (s.equals(array[i].getName())) {

                        result = array[i];
                        break;
                    }
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSource(String s) {

        boolean result = false;

        if (s != null) {

            result = s.equals(getSource());
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isName(String s) {

        boolean result = false;

        if (s != null) {

            result = s.equals(getName());
        }

        return (result);
    }

}


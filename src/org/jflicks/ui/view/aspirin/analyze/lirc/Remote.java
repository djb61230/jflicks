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
package org.jflicks.ui.view.aspirin.analyze.lirc;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This is a utility class to parse a lircd.conf file.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Remote implements Serializable, Comparable<Remote> {

    private String name;
    private ArrayList<String> buttonList;

    /**
     * Simple empty constructor.
     */
    public Remote() {

        setButtonList(new ArrayList<String>());
    }

    public String getName() {
        return (name);
    }

    public void setName(String s) {
        name = s;
    }

    private ArrayList<String> getButtonList() {
        return (buttonList);
    }

    private void setButtonList(ArrayList<String> l) {
        buttonList = l;
    }

    public String[] getButtons() {

        String[] result = null;

        ArrayList<String> l = getButtonList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new String[l.size()]);
        }

        return (result);
    }

    public void addButton(String s) {

        ArrayList<String> l = getButtonList();
        if ((l != null) && (s != null)) {

            if (!l.contains(s)) {

                l.add(s);
            }
        }
    }

    /**
     * The standard hashcode override.
     *
     * @return An int value.
     */
    public int hashCode() {
        return (getName().hashCode());
    }

    /**
     * The equals override method.
     *
     * @param o A given object to check.
     * @return True if the objects are equal.
     */
    public boolean equals(Object o) {

        boolean result = false;

        if (o == this) {

            result = true;

        } else if (!(o instanceof Remote)) {

            result = false;

        } else {

            Remote r = (Remote) o;
            String s = getName();
            if (s != null) {

                result = s.equals(r.getName());
            }
        }

        return (result);
    }

    /**
     * The comparable interface.
     *
     * @param r The given Remote instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(Remote r) throws ClassCastException {

        int result = 0;

        if (r == null) {

            throw new NullPointerException();
        }

        if (r == this) {

            result = 0;

        } else {

            String name0 = getName();
            String name1 = r.getName();
            if ((name0 != null) && (name1 != null)) {

                result = name0.compareTo(name1);
            }
        }

        return (result);
    }

    public String toString() {
        return (getName());
    }

}


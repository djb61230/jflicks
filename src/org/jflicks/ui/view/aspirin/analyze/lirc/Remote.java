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

import java.util.ArrayList;

/**
 * This is a utility class to parse a lircd.conf file.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Remote {

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

    public String toString() {
        return (getName());
    }

}


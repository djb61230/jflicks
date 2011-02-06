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
package org.jflicks.ui.view.fe;

import java.awt.Dimension;
import java.util.ArrayList;

/**
 * This is a display of a selection of String instances as a list.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class TextListPanel extends BaseListPanel {

    private ArrayList<String> buttonList;

    /**
     * Simple empty constructor.
     */
    public TextListPanel() {

        setTextList(new ArrayList<String>());
        setPropertyName("SelectedText");
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getObjects() {

        Object[] result = null;

        ArrayList<String> l = getTextList();
        if (l != null) {

            result = l.toArray(new Object[l.size()]);
        }

        return (result);
    }

    private ArrayList<String> getTextList() {
        return (buttonList);
    }

    private void setTextList(ArrayList<String> l) {
        buttonList = l;
    }

    /**
     * We list button names in our panel.
     *
     * @return An array of String instances.
     */
    public String[] getTexts() {

        String[] result = null;

        ArrayList<String> l = getTextList();
        if (l != null) {

            result = l.toArray(new String[l.size()]);
        }

        return (result);
    }

    /**
     * We list button names in our panel.
     *
     * @param array An array of String instances.
     */
    public void setTexts(String[] array) {

        ArrayList<String> l = getTextList();
        if (l != null) {

            l.clear();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    l.add(array[i]);
                }

                setSelectedObject(array[0]);
                setStartIndex(0);
            }
        }
    }

    /**
     * Convenience method to return the selected object as a String instance.
     *
     * @return A String instance.
     */
    public String getSelectedText() {
        return ((String) getSelectedObject());
    }

    public Dimension getPreferredSize() {

        int width = 0;
        int height = 0;

        String[] array = getTexts();
        if (array != null) {

            int tmp = (int) getMaxWidth(array);
            if (tmp > width) {
                width = tmp;
            }

            tmp = (int) (getMaxHeight() * (array.length + 1));
            if (tmp > height) {

                height = tmp;
            }
        }

        return (new Dimension(width, height));
    }

}


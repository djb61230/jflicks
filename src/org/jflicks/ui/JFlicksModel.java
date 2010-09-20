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
package org.jflicks.ui;

import java.util.ArrayList;

import org.jflicks.mvc.BaseModel;
import org.jflicks.nms.NMS;

/**
 * The model for our client programs for the JFLICKS system.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class JFlicksModel extends BaseModel {

    private ArrayList<NMS> nmsList;

    /**
     * Default constructor.
     */
    public JFlicksModel() {

        setNMSList(new ArrayList<NMS>());
    }

    /**
     * Provides the means to set or reset the model to a default state.
     */
    public void initDefault() {
    }

    /**
     * {@inheritDoc}
     */
    public void fireAllProperties() {

        firePropertyChange("NMS", null, getNMS());
    }

    private ArrayList<NMS> getNMSList() {
        return (nmsList);
    }

    private void setNMSList(ArrayList<NMS> l) {
        nmsList = l;
    }

    /**
     * A model has an array of NMS instances.
     *
     * @return The NMS instances contained in the model.
     */
    public NMS[] getNMS() {

        NMS[] result = null;

        ArrayList<NMS> l = getNMSList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new NMS[l.size()]);
        }

        return (result);
    }

    /**
     * A model has an array of NMS instances.
     *
     * @param array The NMS instances contained in the model.
     */
    public void setNMS(NMS[] array) {

        ArrayList<NMS> l = getNMSList();
        if (l != null) {

            NMS[] oldArray = null;
            if (l.size() > 0) {

                oldArray = l.toArray(new NMS[l.size()]);
            }

            l.clear();
            if ((array != null) && (array.length > 0)) {

                for (int i = 0; i < array.length; i++) {

                    l.add(array[i]);
                }
            }

            NMS[] newArray = null;
            if (l.size() > 0) {

                newArray = l.toArray(new NMS[l.size()]);
            }

            firePropertyChange("NMS", oldArray, newArray);
        }
    }

    /**
     * Find the NMS instance at the given index.  If the index is out of
     * range then null is returned.
     *
     * @param index The given index.
     * @return The NMS instance if it exists.
     */
    public NMS getNMSAt(int index) {

        NMS result = null;

        ArrayList<NMS> l = getNMSList();
        if ((l != null) && (l.size() > index)) {

            result = l.get(index);
        }

        return (result);
    }

    /**
     * Set the NMS instance at the given index.  If the index is out of
     * range then it is appended.
     *
     * @param index The given index.
     * @param nms The NMS instance.
     */
    public void setNMSAt(int index, NMS nms) {

        ArrayList<NMS> l = getNMSList();
        if (l != null) {

            NMS old = null;
            if (l.size() > index) {

                old = l.get(index);
                l.set(index, nms);

            } else if (l.size() == index) {

                l.add(nms);
            }

            fireIndexedPropertyChange("NMS", index, old, nms);
        }

    }

}

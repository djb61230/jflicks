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
package org.jflicks.rc;

import java.util.ArrayList;

/**
 * A Row is defined as a list of Cell instances.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Row {

    private ArrayList<Cell> cellList;

    /**
     * Default empty constructor.
     */
    public Row() {

        setCellList(new ArrayList<Cell>());
    }

    private ArrayList<Cell> getCellList() {
        return (cellList);
    }

    private void setCellList(ArrayList<Cell> l) {
        cellList = l;
    }

    /**
     * Add a Cell to our list.
     *
     * @param c A given Cell to add.
     */
    public void addCell(Cell c) {

        ArrayList<Cell> l = getCellList();
        if ((l != null) && (c != null)) {

            l.add(c);
        }
    }

    /**
     * Remove a Cell from our list.
     *
     * @param c A given Cell to remove.
     */
    public void removeCell(Cell c) {

        ArrayList<Cell> l = getCellList();
        if ((l != null) && (c != null)) {

            l.remove(c);
        }
    }

    /**
     * Clear all Cells.
     */
    public void clearCells() {

        ArrayList<Cell> l = getCellList();
        if (l != null) {

            l.clear();
        }
    }

    /**
     * An array of Cells from our list.
     *
     * @return An array of Cell instances.
     */
    public Cell[] getCells() {

        Cell[] result = null;

        ArrayList<Cell> l = getCellList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Cell[l.size()]);
        }

        return (result);
    }

    /**
     * An array of Cells from our list.
     *
     * @param array An array of Cell instances.
     */
    public void setCells(Cell[] array) {

        clearCells();
        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                addCell(array[i]);
            }
        }
    }

    /**
     * The number of Cell instances in our list.
     *
     * @return The number of Cells.
     */
    public int getCellCount() {

        int result = 0;

        ArrayList<Cell> l = getCellList();
        if ((l != null) && (l.size() > 0)) {

            result = l.size();
        }

        return (result);
    }

    /**
     * Given an index into our list find a Cell.  If the index is out of range
     * then return null.
     *
     * @param index A given index.
     * @return A Cell instance.
     */
    public Cell getCellAt(int index) {

        Cell result = null;

        ArrayList<Cell> l = getCellList();
        if ((l != null) && (l.size() > index)) {

            result = l.get(index);
        }

        return (result);
    }

}


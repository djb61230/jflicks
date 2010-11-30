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

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;

/**
 * A simple object that tries to represent a display, that thing connected
 * to a computer.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Display extends Rectangle {

    private static final int PIXEL_OFFSET = 0;
    private static final int DEFAULT_ROW = 160;
    private static final int DEFAULT_COLUMN = 90;

    private ArrayList<Row> rowList;
    private int rowIndex;
    private int columnIndex;

    /**
     * Default empty constructor.
     */
    public Display() {

        this(DEFAULT_ROW, DEFAULT_COLUMN);
    }

    /**
     * Constructor that sets the grid size as our screen is navigated via
     * grid not pixel.
     *
     * @param rowCount The number of rows.
     * @param columnCount The number of columns.
     */
    public Display(int rowCount, int columnCount) {

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();

        int width = (int) d.getWidth();
        int height = (int) d.getHeight();
        int x = PIXEL_OFFSET;
        int y = PIXEL_OFFSET;

        setRowList(new ArrayList<Row>());
        setBounds(x, y, width, height);

        setRowIndex(0);
        setColumnIndex(0);

        int rwidth = width - x;
        int rheight = height - y;
        int cwidth = rwidth / columnCount;
        if ((rwidth % columnCount) != 0) {
            cwidth++;
        }
        int cheight = rheight / rowCount;
        if ((rheight % rowCount) != 0) {
            cheight++;
        }
        for (int i = 0; i < rowCount; i++) {

            Row r = new Row();
            addRow(r);
            for (int j = 0; j < columnCount; j++) {

                Cell c = new Cell();
                c.setBounds(x + j * cwidth, y + i * cheight, cwidth, cheight);
                r.addCell(c);
            }
        }
    }

    private int getRowIndex() {
        return (rowIndex);
    }

    private void setRowIndex(int i) {
        rowIndex = i;
    }

    private int getColumnIndex() {
        return (columnIndex);
    }

    private void setColumnIndex(int i) {
        columnIndex = i;
    }

    private ArrayList<Row> getRowList() {
        return (rowList);
    }

    private void setRowList(ArrayList<Row> l) {
        rowList = l;
    }

    /**
     * Add a row to our list.
     *
     * @param r A given Row.
     */
    public void addRow(Row r) {

        ArrayList<Row> l = getRowList();
        if ((l != null) && (r != null)) {

            l.add(r);
        }
    }

    /**
     * Remove a row from our list.
     *
     * @param r A given Row.
     */
    public void removeRow(Row r) {

        ArrayList<Row> l = getRowList();
        if ((l != null) && (r != null)) {

            l.remove(r);
        }
    }

    /**
     * Clear our list of Rows.
     */
    public void clearRows() {

        ArrayList<Row> l = getRowList();
        if (l != null) {

            l.clear();
        }
    }

    /**
     * Our list of Rows as an array.
     *
     * @return An array of Rows.
     */
    public Row[] getRows() {

        Row[] result = null;

        ArrayList<Row> l = getRowList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Row[l.size()]);
        }

        return (result);
    }

    /**
     * Our list of Rows as an array.
     *
     * @param array An array of Rows.
     */
    public void setRows(Row[] array) {

        clearRows();
        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                addRow(array[i]);
            }
        }
    }

    /**
     * Figure the location if the user wanted to go up.
     *
     * @return A Point.
     */
    public Point getUp() {

        int r = getRowIndex();
        if (r > 0) {
            r--;
        }
        setRowIndex(r);

        Point result = getCurrentPoint();

        return (result);
    }

    /**
     * Figure the location if the user wanted to go down.
     *
     * @return A Point.
     */
    public Point getDown() {

        int r = getRowIndex();
        if (r < getMaxRow()) {
            r++;
        }
        setRowIndex(r);

        Point result = getCurrentPoint();

        return (result);
    }

    /**
     * Figure the location if the user wanted to go left.
     *
     * @return A Point.
     */
    public Point getLeft() {

        int c = getColumnIndex();
        if (c > 0) {
            c--;
        }
        setColumnIndex(c);

        Point result = getCurrentPoint();

        return (result);
    }

    /**
     * Figure the location if the user wanted to go right.
     *
     * @return A Point.
     */
    public Point getRight() {

        int c = getColumnIndex();
        if (c < getMaxColumn()) {
            c++;
        }
        setColumnIndex(c);

        Point result = getCurrentPoint();

        return (result);
    }

    private int getMaxRow() {

        int result = -1;

        ArrayList<Row> l = getRowList();
        if (l != null) {

            result = l.size();
        }

        return (result);
    }

    private int getMaxColumn() {

        int result = -1;

        ArrayList<Row> l = getRowList();
        if ((l != null) && (l.size() > 0)) {

            result = l.get(0).getCellCount();
        }

        return (result);
    }

    private Point getCurrentPoint() {

        Point result = null;

        Row row = getRowAt(getRowIndex());
        if (row != null) {

            Cell cell = row.getCellAt(getColumnIndex());
            if (cell != null) {

                result = cell.getHotPoint();
                if (result == null) {

                    result = new Point((int) cell.getX(), (int) cell.getY());
                }
            }
        }

        return (result);
    }

    /**
     * Given an index into our list find the proper Row.  If the index is out
     * of range then we return null.
     *
     * @param index A given index.
     * @return A Row instance.
     */
    public Row getRowAt(int index) {

        Row result = null;

        ArrayList<Row> l = getRowList();
        if ((l != null) && (l.size() > index)) {

            result = l.get(index);
        }

        return (result);
    }

    /**
     * Get the current location of the mouse and set our grid location
     * properly.  This ensures the next mouse move is from it's current
     * position.
     */
    public void fromMouse() {

        PointerInfo pi = MouseInfo.getPointerInfo();
        if (pi != null) {

            Point p = pi.getLocation();
            if (p != null) {

                Row[] allrows = getRows();
                if (allrows != null) {

                    for (int i = 0; i < allrows.length; i++) {

                        int index = find(allrows[i].getCells(), p);
                        if (index != -1) {

                            setColumnIndex(index);
                            setRowIndex(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    private int find(Cell[] array, Point p) {

        int result = -1;

        if ((array != null) && (p != null)) {

            for (int i = 0; i < array.length; i++) {

                if (array[i].contains(p)) {

                    result = i;
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * Just documenting that hashCode is not implemented for this extension.
     * Which is OK since it's not needed.
     *
     * @return The super result.
     */
    public int hashCode() {
        return (super.hashCode());
    }

    /**
     * Just documenting that equals is not implemented for this extension.
     * Which is OK since it's not needed.
     *
     * @param o A given object to compare.
     * @return The super result.
     */
    public boolean equals(Object o) {
        return (super.equals(o));
    }

}


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

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This is a base list panel class that handles all the drawing and
 * updating of the list.  Extensions concentrate on laying themselves
 * out and handling their own objects.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseListPanel extends BaseCustomizePanel {

    protected static final double HGAP = 0.02;
    protected static final double VGAP = 0.02;

    private JXLabel[] labels;
    private int visibleCount;
    private int selectedIndex;
    private int oldSelectedIndex;
    private int startIndex;
    private Animator[] animators;
    private Object selectedObject;
    private String propertyName;

    /**
     * Extensions need to make their objects displayed in the list available
     * to this base class so it can draw and maintain the list properly.
     *
     * @return An array of Objects.
     */
    public abstract Object[] getObjects();

    /**
     * Simple empty constructor.
     */
    public BaseListPanel() {

        setStartIndex(0);
        setOldSelectedIndex(-1);
        setSelectedIndex(0);
    }

    protected JXLabel[] getLabels() {
        return (labels);
    }

    protected void setLabels(JXLabel[] array) {
        labels = array;
    }

    protected Animator[] getAnimators() {
        return (animators);
    }

    protected void setAnimators(Animator[] array) {
        animators = array;
    }

    protected Object getSelectedObject() {
        return (selectedObject);
    }

    protected void setSelectedObject(Object o) {

        Object old = selectedObject;
        selectedObject = o;
        firePropertyChange(getPropertyName(), old, selectedObject);
    }

    protected String getPropertyName() {
        return (propertyName);
    }

    protected void setPropertyName(String s) {
        propertyName = s;
    }

    private int getCount() {

        int result = 0;

        Object[] array = getObjects();
        if (array != null) {

            result = array.length;
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void performControl() {
        applyColor();
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {

        JLayeredPane pane = getLayeredPane();
        if ((d != null) && (pane != null)) {

            // This most likely our screen size...
            double width = d.getWidth();
            double height = d.getHeight();

            // Compute our gaps...
            double hgap = width * HGAP;
            double vgap = height * VGAP;

            double realHeight = height - (vgap * 2.0);

            // We create and size the labels to the large size.  This
            // allows us to determine the maximum number of visible
            // labels we can show.  First make one label and compute
            // our max.
            JXLabel tst = new JXLabel("TEST IT DUDE");
            tst.setFont(getLargeFont());
            tst.setHorizontalTextPosition(SwingConstants.CENTER);
            tst.setHorizontalAlignment(SwingConstants.LEFT);
            Dimension fd = tst.getPreferredSize();

            double labelMaxHeight = 0.0;
            if (fd != null) {
                labelMaxHeight = fd.getHeight();
            }

            int vcount = (int) (realHeight / labelMaxHeight);
            JXLabel[] array = new JXLabel[vcount];
            tst.setText("");
            tst.setFont(getSmallFont());
            array[0] = tst;
            for (int i = 1; i < vcount; i++) {

                array[i] = new JXLabel();
                array[i].setFont(getSmallFont());
                array[i].setHorizontalTextPosition(SwingConstants.CENTER);
                array[i].setHorizontalAlignment(SwingConstants.LEFT);
            }

            setVisibleCount(vcount);
            setLabels(array);

            // Do the background.
            Color color = getPanelColor();
            color = new Color(color.getRed(), color.getGreen(),
                color.getBlue(), (int) (getPanelAlpha() * 255));
            MattePainter mpainter = new MattePainter(color);
            setBackgroundPainter(mpainter);
            setAlpha((float) getPanelAlpha());

            FontEvaluator feval = new FontEvaluator(getSmallFont(),
                getLargeFont(), getSmallFontSize(), getLargeFontSize());
            Animator[] anis = new Animator[array.length];
            double center = (realHeight - (vcount * labelMaxHeight)) / 2.0;
            double top = vgap + center;
            for (int i = 0; i < array.length; i++) {

                array[i].setBounds((int) hgap, (int) top, (int) width,
                    (int) labelMaxHeight);
                pane.add(array[i], Integer.valueOf(110));
                top += labelMaxHeight;
                anis[i] = PropertySetter.createAnimator(250, array[i],
                    "font", feval, getSmallFont(), getLargeFont());
            }

            setAnimators(anis);
        }
    }

    /**
     * Move the group of labels down a page.
     */
    public void movePageUp() {

        int count = getVisibleCount() - 1;
        if (count > 0) {

            for (int i = 0; i < count; i++) {

                moveUp();
            }
        }
    }

    /**
     * Move the group of labels down a page.
     */
    public void movePageDown() {

        int count = getVisibleCount() - 1;
        if (count > 0) {

            for (int i = 0; i < count; i++) {

                moveDown();
            }
        }
    }

    /**
     * Move the group of labels down one.
     */
    public void moveDown() {

        Object[] array = getObjects();
        if (array != null) {

            if (isWindowGreaterOrEqual()) {

                int selected = getSelectedIndex();
                if ((selected + 1) < array.length) {

                    setSelectedIndex(selected + 1);
                }

            } else {

                // We have more channels that can fit in the window.
                // First see if we can just move the selection.
                if (!isSelectedAtTheBottomWindow()) {

                    // Then we can just update the index and be done.
                    int selected = getSelectedIndex();
                    setSelectedIndex(selected + 1);

                } else {

                    // Ok we have to increment our start and leave the
                    // selected at the bottom as long as we haven't
                    // reached the end of the list.
                    if (!isSelectedAtTheBottomList()) {

                        int start = getStartIndex();
                        setStartIndex(start + 1);
                    }
                }
            }
        }
    }

    /**
     * Move the group of labels up one.
     */
    public void moveUp() {

        Object[] array = getObjects();
        if (array != null) {

            if (isWindowGreaterOrEqual()) {

                int selected = getSelectedIndex();
                if ((selected - 1) >= 0) {

                    setSelectedIndex(selected - 1);
                }

            } else {

                // We have more channels that can fit in the window.
                // First see if we can just move the selection.
                if (!isSelectedAtTheTopWindow()) {

                    // Then we can just update the index and be done.
                    int selected = getSelectedIndex();
                    setSelectedIndex(selected - 1);

                } else {

                    // Ok we have to decrement our start and leave the
                    // selected at the top as long as we haven't
                    // reached the top of the list.
                    if (!isSelectedAtTheTopList()) {

                        int start = getStartIndex();
                        setStartIndex(start - 1);
                    }
                }
            }
        }
    }

    protected void applyColor() {

        if (isControl()) {
            setBorder(BorderFactory.createLineBorder(getHighlightColor()));
        } else {
            setBorder(BorderFactory.createLineBorder(getUnselectedColor()));
        }

        JXLabel[] array = getLabels();
        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                if (isControl()) {

                    if (i == getSelectedIndex()) {

                        array[i].setForeground(getHighlightColor());

                    } else {

                        array[i].setForeground(getUnselectedColor());
                    }

                } else {

                    if (i == getSelectedIndex()) {

                        array[i].setForeground(getSelectedColor());

                    } else {

                        array[i].setForeground(getUnselectedColor());
                    }
                }
            }
        }
    }

    protected void animate() {

        JXLabel[] array = getLabels();
        Animator[] anis = getAnimators();
        if ((array != null) && (anis != null)) {

            int old = getOldSelectedIndex();
            int current = getSelectedIndex();

            // Stop all previous running animations...
            for (int i = 0; i < anis.length; i++) {

                if (anis[i].isRunning()) {

                    anis[i].stop();
                    array[i].setFont(getSmallFont());
                }
            }

            // Animate the old selected so it goes small...
            if ((old >= 0) && (old < array.length) && (old != current)) {

                anis[old].setStartDirection(Animator.Direction.BACKWARD);
                anis[old].setStartFraction(1.0f);
                anis[old].start();
            }

            // Animate the new selected so it goes large...
            if ((current >= 0) && (current < array.length)) {

                anis[current].setStartDirection(Animator.Direction.FORWARD);
                anis[current].setStartFraction(0.0f);
                anis[current].start();
            }
        }
    }

    public int getVisibleCount() {
        return (visibleCount);
    }

    protected void setVisibleCount(int i) {
        visibleCount = i;
    }

    /**
     * Which item is selected by index.
     *
     * @return The selected index.
     */
    public int getSelectedIndex() {
        return (selectedIndex);
    }

    /**
     * Which item is selected by index.
     *
     * @param i The selected index.
     */
    public void setSelectedIndex(int i) {

        // In case we are being set to an index out of range, fix it to
        // the last item.
        int tmp = getCount();
        if ((i + 1) > tmp) {

            if (tmp > 0) {
                i = tmp - 1;
            } else {
                i = 0;
            }
        }
        int old = selectedIndex;
        setOldSelectedIndex(old);
        selectedIndex = i;
        update();
    }

    protected int getOldSelectedIndex() {
        return (oldSelectedIndex);
    }

    protected void setOldSelectedIndex(int i) {
        oldSelectedIndex = i;
    }

    /**
     * We start at a particular index.
     *
     * @return The start index.
     */
    public int getStartIndex() {
        return (startIndex);
    }

    /**
     * We start at a particular index.
     *
     * @param i The start index.
     */
    public void setStartIndex(int i) {

        startIndex = i;
        update();
    }

    private boolean isWindowGreaterOrEqual() {

        boolean result = false;

        Object[] array = getObjects();
        if (array != null) {

            result = getVisibleCount() >= array.length;
        }

        return (result);
    }

    private boolean isSelectedAtTheBottomWindow() {
        return ((getVisibleCount() - 1) == getSelectedIndex());
    }

    private boolean isSelectedAtTheBottomList() {

        boolean result = false;

        Object[] array = getObjects();
        if (array != null) {

            Object selected = getSelectedObject();
            Object last = array[array.length - 1];

            if ((selected != null) && (last != null)) {

                result = selected.equals(last);
            }
        }

        return (result);
    }

    private boolean isSelectedAtTheTopWindow() {
        return (getSelectedIndex() == 0);
    }

    private boolean isSelectedAtTheTopList() {

        boolean result = false;

        Object[] array = getObjects();
        if (array != null) {

            Object selected = getSelectedObject();
            Object first = array[0];
            if ((selected != null) && (first != null)) {

                result = selected.equals(first);
            }
        }

        return (result);
    }

    /**
     * Update the UI.
     */
    protected void update() {

        Object[] array = getObjects();
        JXLabel[] labs = getLabels();
        if ((array != null) && (labs != null)) {

            int index = getStartIndex();
            for (int i = 0; i < labs.length; i++) {

                if (index < array.length) {

                    labs[i].setText(array[index].toString());

                } else {
                    labs[i].setText("");
                }

                index++;
            }

            applyColor();
            int sindex = getSelectedIndex() + getStartIndex();
            if (array.length > sindex) {
                setSelectedObject(array[sindex]);
            }

            animate();
        }
    }

}


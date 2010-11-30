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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports Labels in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class LabelPanel extends BaseCustomizePanel {

    /**
     * The default aspect ratio is 0.68 which will size each to the shape
     * of a movie poster in portrait mode.
     */
    public static final double DEFAULT_ASPECT_RATIO = 0.68;

    private static final double VERTICAL_GAP = 0.01;
    private static final double HORIZONTAL_GAP = 0.01;

    private int maxVisibleCount;
    private int visibleCount;
    private JXLabel leftTitleLabel;
    private JXLabel rightTitleLabel;
    private int orientation;
    private double aspectRatio;
    private JXLabel[] maxLabels;
    private JXLabel[] labels;
    private Animator[] animators;
    private ArrayList<TextIcon> textIconList;
    private TextIcon selectedTextIcon;
    private int labelWidth;
    private int labelHeight;
    private MattePainter mattePainter;
    private MattePainter highlightMattePainter;
    private boolean selectedHighlighted;

    /**
     * Simple empty constructor.
     */
    public LabelPanel() {

        this(3, null);
    }

    /**
     * Contructor where the visible labels can be controlled.
     *
     * @param count The label count.
     */
    public LabelPanel(int count) {

        this(count, null);
    }

    /**
     * Contructor where the visible labels can be controlled.
     *
     * @param count The label count.
     * @param title The title text.
     */
    public LabelPanel(int count, String title) {

        if ((count % 2) == 0) {

            throw new RuntimeException("Have to be an odd number of labels.");
        }
        setSelectedHighlighted(true);
        setMaxVisibleCount(count);
        setVisibleCount(count);
        setOrientation(SwingConstants.HORIZONTAL);
        setAspectRatio(DEFAULT_ASPECT_RATIO);
        setTextIconList(new ArrayList<TextIcon>());

        MattePainter painter = new MattePainter(getPanelColor());
        setMattePainter(painter);

        Color hc = getHighlightColor();
        Color highMatte = new Color(hc.getRed(), hc.getGreen(),
            hc.getBlue(), (int) (getPanelAlpha() * 255));
        MattePainter hpainter = new MattePainter(highMatte);
        setHighlightMattePainter(hpainter);

        JXLabel[] array = new JXLabel[count];
        for (int i = 0; i < count; i++) {

            array[i] = new JXLabel();
            array[i].setFont(getSmallFont());
            array[i].setVerticalTextPosition(SwingConstants.BOTTOM);
            array[i].setHorizontalTextPosition(SwingConstants.CENTER);
            array[i].setHorizontalAlignment(SwingConstants.CENTER);
            array[i].setForeground(getSelectedColor());
        }
        setMaxLabels(array);
        setLabels(array);

        if (title != null) {

            JXLabel ltitleLab = new JXLabel(title);
            ltitleLab.setFont(getSmallFont());
            ltitleLab.setTextRotation(JXLabel.VERTICAL_LEFT);
            ltitleLab.setHorizontalTextPosition(SwingConstants.CENTER);
            ltitleLab.setHorizontalAlignment(SwingConstants.CENTER);
            ltitleLab.setForeground(getSelectedColor());
            ltitleLab.setBackgroundPainter(getMattePainter());
            setLeftTitleLabel(ltitleLab);

            JXLabel rtitleLab = new JXLabel(title);
            rtitleLab.setFont(getSmallFont());
            rtitleLab.setTextRotation(JXLabel.VERTICAL_RIGHT);
            rtitleLab.setHorizontalTextPosition(SwingConstants.CENTER);
            rtitleLab.setHorizontalAlignment(SwingConstants.CENTER);
            rtitleLab.setForeground(getSelectedColor());
            rtitleLab.setBackgroundPainter(getMattePainter());
            setRightTitleLabel(rtitleLab);
        }

    }

    /**
     * Sometimes one doesn't want the extra highlighting so this property
     * allows it to be turned off.  Default is True.
     *
     * @return True if highlighting is desired.
     */
    public boolean isSelectedHighlighted() {
        return (selectedHighlighted);
    }

    /**
     * Sometimes one doesn't want the extra highlighting so this property
     * allows it to be turned off.  Default is True.
     *
     * @param b True if highlighting is desired.
     */
    public void setSelectedHighlighted(boolean b) {
        selectedHighlighted = b;
    }

    private int getMaxVisibleCount() {
        return (maxVisibleCount);
    }

    private void setMaxVisibleCount(int i) {
        maxVisibleCount = i;
    }

    private int getVisibleCount() {
        return (visibleCount);
    }

    private void setVisibleCount(int i) {
        visibleCount = i;
    }

    private JXLabel getLeftTitleLabel() {
        return (leftTitleLabel);
    }

    private void setLeftTitleLabel(JXLabel l) {
        leftTitleLabel = l;
    }

    private JXLabel getRightTitleLabel() {
        return (rightTitleLabel);
    }

    private void setRightTitleLabel(JXLabel l) {
        rightTitleLabel = l;
    }

    /**
     * The layout orientation of this LabelPane.  This must be set before
     * adding to a container or it will be ignored.
     *
     * @return The orientation as an it (use SwingConstants.VERTICAL or
     * SwingConstants.HORIZONTAL).
     */
    public int getOrientation() {
        return (orientation);
    }

    /**
     * The layout orientation of this LabelPane.  This must be set before
     * adding to a container or it will be ignored.
     *
     * @param i The orientation as an it (use SwingConstants.VERTICAL or
     * SwingConstants.HORIZONTAL).
     */
    public void setOrientation(int i) {
        orientation = i;
    }

    /**
     * The layout aspect ratio of this LabelPane.  This must be set before
     * adding to a container or it will be ignored.
     *
     * @return The spect ratio, a value between 0-1.
     */
    public double getAspectRatio() {
        return (aspectRatio);
    }

    /**
     * The layout aspect ratio of this LabelPane.  This must be set before
     * adding to a container or it will be ignored.
     *
     * @param d The spect ratio, a value between 0-1.
     */
    public void setAspectRatio(double d) {
        aspectRatio = d;
    }

    private JXLabel[] getMaxLabels() {
        return (maxLabels);
    }

    private void setMaxLabels(JXLabel[] array) {
        maxLabels = array;
    }

    private JXLabel[] getLabels() {
        return (labels);
    }

    private void setLabels(JXLabel[] array) {
        labels = array;
    }

    private Animator[] getAnimators() {
        return (animators);
    }

    private void setAnimators(Animator[] array) {
        animators = array;
    }

    /**
     * The width in pixels of the Labels in our pane.
     *
     * @return The width as an int value.
     */
    public int getLabelWidth() {
        return (labelWidth);
    }

    private void setLabelWidth(int i) {
        labelWidth = i;
    }

    /**
     * The height in pixels of the Labels in our pane.
     *
     * @return The height as an int value.
     */
    public int getLabelHeight() {
        return (labelHeight);
    }

    private void setLabelHeight(int i) {
        labelHeight = i;
    }

    private MattePainter getMattePainter() {
        return (mattePainter);
    }

    private void setMattePainter(MattePainter p) {
        mattePainter = p;
    }

    private MattePainter getHighlightMattePainter() {
        return (highlightMattePainter);
    }

    private void setHighlightMattePainter(MattePainter p) {
        highlightMattePainter = p;
    }

    /**
     * The currently selected TextIcon.
     *
     * @return A TextIcon instance.
     */
    public TextIcon getSelectedTextIcon() {
        return (selectedTextIcon);
    }

    /**
     * The currently selected TextIcon.
     *
     * @param ti A TextIcon instance.
     */
    public void setSelectedTextIcon(TextIcon ti) {

        TextIcon old = selectedTextIcon;
        selectedTextIcon = ti;
        firePropertyChange("SelectedTextIcon", old, ti);
    }

    /**
     * {@inheritDoc}
     */
    public void performControl() {

        JXLabel ltlabel = getLeftTitleLabel();
        JXLabel rtlabel = getRightTitleLabel();
        if ((ltlabel != null) && (rtlabel != null)) {

            if (isControl()) {

                setBorder(BorderFactory.createLineBorder(getHighlightColor()));
                ltlabel.setForeground(getHighlightColor());
                rtlabel.setForeground(getHighlightColor());

            } else {

                setBorder(BorderFactory.createLineBorder(getUnselectedColor()));
                ltlabel.setForeground(getUnselectedColor());
                rtlabel.setForeground(getUnselectedColor());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void performLayout(Dimension d) {

        JXLabel[] labs = getLabels();
        JLayeredPane pane = getLayeredPane();
        if ((d != null) && (labs != null) && (pane != null)) {

            double width = d.getWidth();
            double height = d.getHeight();
            int count = getVisibleCount();

            if (orientation == SwingConstants.HORIZONTAL) {

                double[] xarray = new double[labs.length];

                double center = width / 2.0;
                double vgap = height * VERTICAL_GAP;
                double labheight = height - (2 * vgap);
                double labwidth = height * getAspectRatio();
                double labspan = labwidth + (width * HORIZONTAL_GAP);

                int midIndex = count / 2;

                double ref = center - (labwidth / 2.0);
                xarray[midIndex] = ref;
                for (int i = midIndex - 1; i >= 0; i--) {

                    xarray[i] = ref - labspan;
                    ref -= labspan;
                }

                ref = xarray[midIndex];
                for (int i = midIndex + 1; i < xarray.length; i++) {

                    xarray[i] = ref + labspan;
                    ref += labspan;
                }

                // Set the Image size, fudging a bit.
                setLabelWidth((int) (labwidth - 20.0));
                setLabelHeight((int) (labheight - 20.0));

                Animator[] anis = new Animator[labs.length];
                double y = (height - labheight) / 2.0;
                for (int i = 0; i < labs.length; i++) {

                    JXPanel labelPanel = new JXPanel(new BorderLayout());
                    labelPanel.add(labs[i], BorderLayout.CENTER);
                    labelPanel.setOpaque(false);

                    labelPanel.setBounds((int) xarray[i], (int) y,
                        (int) labwidth, (int) labheight);
                    pane.add(labelPanel, Integer.valueOf(100));

                    anis[i] = PropertySetter.createAnimator(750, labelPanel,
                        "alpha", 0.0f, getAlpha());
                }
                setAnimators(anis);

                JXLabel ltitleLab = getLeftTitleLabel();
                if (ltitleLab != null) {

                    ltitleLab.setBounds(2, 2, 50, (int) labheight);
                    pane.add(ltitleLab, Integer.valueOf(100));
                }

                JXLabel rtitleLab = getRightTitleLabel();
                if (rtitleLab != null) {

                    rtitleLab.setBounds((int) (width - 50 - 2), 2, 50,
                        (int) labheight);
                    pane.add(rtitleLab, Integer.valueOf(100));
                }

            } else {

                double[] yarray = new double[labs.length];

                double center = width / 2.0;
                double vgap = height * VERTICAL_GAP;
                double labheight = height - (2 * vgap);
                double labwidth = height * getAspectRatio();
                double labspan = labheight + (height * VERTICAL_GAP);

                int midIndex = count / 2;

                double ref = center - (labheight / 2.0);
                yarray[midIndex] = ref;
                for (int i = midIndex - 1; i >= 0; i--) {

                    yarray[i] = ref - labspan;
                    ref -= labspan;
                }

                ref = yarray[midIndex];
                for (int i = midIndex + 1; i < yarray.length; i++) {

                    yarray[i] = ref + labspan;
                    ref += labspan;
                }

                // Set the Image size, fudging a bit.
                setLabelWidth((int) (labwidth - 20.0));
                setLabelHeight((int) (labheight - 20.0));

                Animator[] anis = new Animator[labs.length];
                double x = (width - labwidth) / 2.0;
                for (int i = 0; i < labs.length; i++) {

                    JXPanel labelPanel = new JXPanel(new BorderLayout());
                    labelPanel.add(labs[i], BorderLayout.CENTER);
                    labelPanel.setOpaque(false);

                    labelPanel.setBounds((int) x, (int) yarray[i],
                        (int) labwidth, (int) labheight);
                    pane.add(labelPanel, Integer.valueOf(100));

                    anis[i] = PropertySetter.createAnimator(750, labelPanel,
                        "alpha", 0.0f, getAlpha());
                }
                setAnimators(anis);

                JXLabel ltitleLab = getLeftTitleLabel();
                if (ltitleLab != null) {

                    ltitleLab.setBounds(2, 2, 50, (int) labheight);
                    pane.add(ltitleLab, Integer.valueOf(100));
                }

                JXLabel rtitleLab = getRightTitleLabel();
                if (rtitleLab != null) {

                    rtitleLab.setBounds((int) (width - 50 - 2), 2, 50,
                        (int) labheight);
                    pane.add(rtitleLab, Integer.valueOf(100));
                }
            }
        }
    }

    private ArrayList<TextIcon> getTextIconList() {
        return (textIconList);
    }

    private void setTextIconList(ArrayList<TextIcon> l) {
        textIconList = l;
    }

    /**
     * Acquire the defined TextIcon objects currently being displayed.
     *
     * @return An array of TextIcon instances.
     */
    public TextIcon[] getTextIcons() {

        TextIcon[] result = null;

        ArrayList<TextIcon> l = getTextIconList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new TextIcon[l.size()]);
        }

        return (result);
    }

    /**
     * The TextIcon instances displayed are supplied here.
     *
     * @param array An array of TextIcon objects.
     */
    public void setTextIcons(TextIcon[] array) {

        if (array == null) {

            throw new RuntimeException("Don't send null TextIcon array");
        }

        // First make sure we reset our count and array.
        setVisibleCount(getMaxVisibleCount());
        setLabels(getMaxLabels());
        JXLabel[] maxlabs = getMaxLabels();
        for (int i = 0; i < maxlabs.length; i++) {

            maxlabs[i].setText("");
            maxlabs[i].setIcon(null);
            maxlabs[i].setBackgroundPainter(null);
        }
        repaint();

        if (array.length < getMaxVisibleCount()) {

            // Crap not enough icons to fill our defined max.
            int count = getMaxVisibleCount();
            boolean done = false;
            while (!done) {

                count--;
                if (count < 1) {

                    throw new RuntimeException("Not enough TextIcon to show");

                } else if (array.length >= count) {

                    setVisibleCount(count);

                    JXLabel[] oldlabs = getMaxLabels();
                    JXLabel[] newlabs = new JXLabel[count];
                    if (count > 1) {

                        int start = count / 2;
                        if ((start + count) >= oldlabs.length) {
                            start--;
                        }
                        for (int i = 0; i < newlabs.length; i++) {

                            newlabs[i] = oldlabs[start++];
                        }

                    } else {

                        newlabs[0] = oldlabs[getMaxVisibleCount() / 2];
                    }

                    setLabels(newlabs);
                    done = true;
                }
            }

        }

        ArrayList<TextIcon> l = getTextIconList();
        if (l != null) {

            l.clear();
            for (int i = 0; i < array.length; i++) {

                adjust(array[i]);
                l.add(array[i]);
            }
        }

        // We start in the middle so we have to move the first item to it.
        int moves = centerIndex();
        for (int i = 0; i < moves; i++) {

            TextIcon last = l.remove(l.size() - 1);
            l.add(0, last);
        }
        if ((moves >= 0) && (moves < l.size())) {
            setSelectedTextIcon(l.get(moves));
        }

        update();
    }

    private void update() {

        ArrayList<TextIcon> l = getTextIconList();
        JXLabel[] labs = getLabels();
        Animator[] anis = getAnimators();
        if ((l != null) && (labs != null) && (anis != null)) {

            for (int i = 0; i < anis.length; i++) {

                if (anis[i].isRunning()) {
                    anis[i].stop();
                }
            }

            int mid = centerIndex();
            for (int i = 0; i < labs.length; i++) {

                if (i == mid) {

                    Icon icon = l.get(i).getSelectedIcon();
                    if (icon != null) {
                        labs[i].setIcon(icon);
                    } else {
                        labs[i].setIcon(l.get(i).getIcon());
                    }

                    if (isSelectedHighlighted()) {

                        labs[i].setBackgroundPainter(
                            getHighlightMattePainter());
                    }

                } else {

                    if (isSelectedHighlighted()) {

                        labs[i].setIcon(l.get(i).getIcon());
                        labs[i].setBackgroundPainter(getMattePainter());
                    }
                }

                if (labs[i].getIcon() == null) {
                    labs[i].setText(l.get(i).getText());
                }
            }

            for (int i = 0; i < anis.length; i++) {

                anis[i].start();
            }
        }
    }

    private int centerIndex() {

        int result = getVisibleCount() / 2;

        // If out visible count is an EVEN number then we have to subtract
        // one.  Well unless our count is 2.
        if (((getVisibleCount() % 2) == 0) && (getVisibleCount() != 2)) {
            result--;
        }

        return (result);
    }

    private void adjust(TextIcon ti) {

        if (ti != null) {

            ImageIcon ii = (ImageIcon) ti.getIcon();
            if (ii != null) {

                Image image = ii.getImage();
                if (image.getWidth(this) > getLabelWidth()) {

                    ii.setImage(image.getScaledInstance(getLabelWidth(),
                        getLabelHeight(), Image.SCALE_DEFAULT));
                }
            }
        }
    }

    /**
     * Go to the previous TextIcon.
     */
    public void prev() {

        ArrayList<TextIcon> l = getTextIconList();
        if (l != null) {

            TextIcon last = l.remove(l.size() - 1);
            l.add(0, last);
            setSelectedTextIcon(l.get(centerIndex()));
            update();
        }
    }

    /**
     * Go to the next TextIcon.
     */
    public void next() {

        ArrayList<TextIcon> l = getTextIconList();
        if (l != null) {

            TextIcon first = l.remove(0);
            l.add(first);
            setSelectedTextIcon(l.get(centerIndex()));
            update();
        }
    }

}


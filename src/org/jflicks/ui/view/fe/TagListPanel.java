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
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;

import org.jflicks.photomanager.Tag;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * This class supports Labels in a front end UI on a TV.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class TagListPanel extends BaseCustomizePanel {

    private static final double HGAP = 0.02;
    private static final double VGAP = 0.02;
    private static final int MAX_TO_CHECK_EQUAL = 1000;

    private JXLabel[] labels;
    private ArrayList<Tag> tagList;
    private ArrayList<Tag> copyTagList;
    private Tag selectedTag;
    private int visibleCount;
    private int selectedIndex;
    private int oldSelectedIndex;
    private int startIndex;
    private Animator[] animators;
    private Icon[] tabIcons;
    private HashMap<Tag, Tag[]> hiddenMap;

    /**
     * Simple empty constructor.
     */
    public TagListPanel() {

        setTagList(new ArrayList<Tag>());
        setStartIndex(0);
        setOldSelectedIndex(-1);
        setSelectedIndex(0);
        setHiddenMap(new HashMap<Tag, Tag[]>());
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

    private Icon[] getTabIcons() {
        return (tabIcons);
    }

    private void setTabIcons(Icon[] array) {
        tabIcons = array;
    }

    private Icon getTabIcon(int i) {

        Icon result = null;

        if (tabIcons != null) {

            if ((i >= 0) && (i < tabIcons.length)) {

                result = tabIcons[i];
            }
        }

        return (result);
    }

    private HashMap<Tag, Tag[]> getHiddenMap() {
        return (hiddenMap);
    }

    private void setHiddenMap(HashMap<Tag, Tag[]> m) {
        hiddenMap = m;
    }

    private ArrayList<Tag> getTagList() {
        return (tagList);
    }

    private void setTagList(ArrayList<Tag> l) {
        tagList = l;
    }

    private ArrayList<Tag> getCopyTagList() {
        return (copyTagList);
    }

    private void setCopyTagList(ArrayList<Tag> l) {
        copyTagList = l;
    }

    private Tag[] peekHiddenTagsByParent(Tag parent) {

        Tag[] result = null;

        HashMap<Tag, Tag[]> m = getHiddenMap();
        if ((m != null) && (parent != null)) {

            result = m.get(parent);
        }

        return (result);
    }

    private Tag[] getHiddenTagsByParent(Tag parent) {

        Tag[] result = null;

        HashMap<Tag, Tag[]> m = getHiddenMap();
        if ((m != null) && (parent != null)) {

            result = m.get(parent);
            m.remove(parent);
        }

        return (result);
    }

    private void putHiddenTagsByParent(Tag parent, Tag[] kids) {

        HashMap<Tag, Tag[]> m = getHiddenMap();
        if ((m != null) && (parent != null) && (kids != null)) {

            Arrays.sort(kids);
            m.put(parent, kids);
        }
    }

    /**
     * Convenience method to see if a Tag instance has it's children hidden.
     *
     * @param t A given Tag object to check.
     * @return True if the children are not in our list.
     */
    private boolean isHidden(Tag parent) {

        boolean result = false;

        HashMap<Tag, Tag[]> m = getHiddenMap();
        if ((m != null) && (parent != null)) {

            result = (m.get(parent) != null);
        }

        return (result);
    }

    /**
     * Convenience method to see if a Tag instance is in our list.
     *
     * @param t A given Tag object to check.
     * @return True if the Tag is in our list.
     */
    public boolean containsTag(Tag t) {

        boolean result = false;

        ArrayList<Tag> l = getTagList();
        if ((l != null) && (t != null)) {

            result = l.contains(t);
        }

        return (result);
    }

    /**
     * Convenience method to add a Tag instance.
     *
     * @param t A given Tag object to add.
     */
    public void addTag(Tag t) {

        ArrayList<Tag> l = getTagList();
        if ((l != null) && (t != null)) {

            if (!l.contains(t)) {

                l.add(t);
                Collections.sort(l);

                setSelectedTag(null);
                setStartIndex(0);
            }
        }
    }

    /**
     * Convenience method to add a set of Tag instances.
     *
     * @param array A given array of Tag objects to add.
     */
    public void addTags(Tag[] array) {

        ArrayList<Tag> l = getTagList();
        if ((l != null) && (array != null)) {

            boolean atLeastOne = false;
            for (int i = 0; i < array.length; i++) {

                Tag t = array[i];
                if ((t != null) && (!l.contains(t))) {

                    l.add(t);
                    atLeastOne = true;
                }
            }

            if (atLeastOne) {

                Collections.sort(l);
                setSelectedTag(null);
                setStartIndex(0);
            }
        }
    }

    /**
     * Convenience method to remove a Tag instance.
     *
     * @param t A given Tag object to remove.
     */
    public void removeTag(Tag t) {

        ArrayList<Tag> l = getTagList();
        if ((l != null) && (t != null)) {

            if (l.contains(t)) {

                l.remove(t);
                Collections.sort(l);

                setSelectedTag(null);
                setStartIndex(0);
                int tmp = getSelectedIndex();
                if (tmp >= l.size()) {
                    tmp = l.size() - 1;
                }
                setSelectedIndex(tmp);
            }
        }
    }

    /**
     * Convenience method to remove a set of Tag instances.
     *
     * @param array A given array of Tag objects to remove.
     */
    public void removeTags(Tag[] array) {

        ArrayList<Tag> l = getTagList();
        if ((l != null) && (array != null)) {

            boolean atLeastOne = false;
            for (int i = 0; i < array.length; i++) {

                Tag t = array[i];
                if ((t != null) && (l.contains(t))) {

                    l.remove(t);
                    atLeastOne = true;
                }
            }

            if (atLeastOne) {

                Collections.sort(l);
                setSelectedTag(null);
                setStartIndex(0);
                int tmp = getSelectedIndex();
                if (tmp >= l.size()) {
                    tmp = l.size() - 1;
                }
                setSelectedIndex(tmp);
            }
        }
    }

    /**
     * Convenience method to determine if the selected Tag is collapsed.
     *
     * @return True if it is collapsed.
     */
    public boolean isCollapsed() {
        return (isCollapsed(getSelectedTag()));
    }

    /**
     * Convenience method to determine if the given Tag is collapsed.
     *
     * @param t A given Tag instance.
     * @return True if it is collapsed.
     */
    public boolean isCollapsed(Tag t) {

        boolean result = false;

        ArrayList<Tag> l = getTagList();
        if ((l != null) && (t != null) && (!t.isLeaf())) {

            result = (peekHiddenTagsByParent(t) != null);
        }

        return (result);
    }

    /**
     * Convenience method to determine if the selected Tag is expanded.
     *
     * @return True if it is expanded.
     */
    public boolean isExpanded() {
        return (isExpanded(getSelectedTag()));
    }

    /**
     * Convenience method to determine if the given Tag is expanded.
     *
     * @param t A given Tag instance.
     * @return True if it is expanded.
     */
    public boolean isExpanded(Tag t) {

        boolean result = false;

        ArrayList<Tag> l = getTagList();
        if ((l != null) && (t != null) && (!t.isLeaf())) {

            result = (peekHiddenTagsByParent(t) == null);
        }

        return (result);
    }

    /**
     * Toggle the current Tag either.  This will either expand or collapse it.
     */
    public void toggle() {

        toggle(getSelectedTag());
    }

    private void collapse(Tag t) {

        ArrayList<Tag> l = getTagList();
        if ((l != null) && (t != null)) {

            Tag[] kids = t.toArray();
            if ((kids != null) && (kids.length > 1)) {

                kids = Arrays.copyOfRange(kids, 1, kids.length);

                putHiddenTagsByParent(t, kids);
                kids = t.getChildren();
                int index = l.indexOf(t);
                if (index != -1) {

                    for (int i = 0; i < kids.length; i++) {

                        l.remove(kids[i]);
                    }
                }
            }
        }
    }

    private void toggle(Tag current) {

        ArrayList<Tag> l = getTagList();
        if ((l != null) && (current != null) && (!current.isLeaf())) {

            int cindex = l.indexOf(current);
            Tag[] kids = getHiddenTagsByParent(current);
            if (kids != null) {

                // We need to show the kids now.
                for (int i = kids.length - 1; i >= 0; i--) {

                    if ((kids[i].isLeaf())
                        && (current.equals(kids[i].getParent()))) {

                        l.add(cindex + 1, kids[i]);

                    } else {

                        if (!isHidden(kids[i].getParent())) {

                            l.add(cindex + 1, kids[i]);
                        }
                    }
                }

            } else {

                // The kids are showing so we need to hide them.
                kids = current.toArray();
                if ((kids != null) && (kids.length > 0)) {

                    kids = Arrays.copyOfRange(kids, 1, kids.length);

                    putHiddenTagsByParent(current, kids);
                    for (int i = 0; i < kids.length; i++) {

                        l.remove(kids[i]);
                    }

                    // We have removed some so lets make sure that we
                    // fill in if we have to do so.
                    if (l.size() > getVisibleCount()) {

                        int showing = l.size() - getStartIndex();
                        if (showing < getVisibleCount()) {

                            setStartIndex(getStartIndex()
                                - (getVisibleCount() - showing));
                        }

                    } else {

                        setStartIndex(0);
                    }
                }
            }

            update();
        }
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

                array[i].setBounds((int) hgap, (int) top,
                    (int) (width - (2 * hgap)),
                    (int) labelMaxHeight);
                pane.add(array[i], Integer.valueOf(110));
                top += labelMaxHeight;
                anis[i] = PropertySetter.createAnimator(250, array[i],
                    "font", feval, getSmallFont(), getLargeFont());
            }

            setAnimators(anis);

            if (getTabIcons() == null) {

                Icon[] tabs = new Icon[6];
                int indent = (int) (hgap * 4.0);
                int current = 1;
                for (int i = 0; i < tabs.length; i++) {

                    BufferedImage bi = new BufferedImage(current, 10,
                        BufferedImage.TYPE_INT_ARGB);
                    current += indent;
                    tabs[i] = new ImageIcon(bi);
                }

                setTabIcons(tabs);
            }
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

        Tag[] tarray = getTags();
        if (tarray != null) {

            if (isWindowGreaterOrEqual()) {

                int selected = getSelectedIndex();
                if ((selected + 1) < tarray.length) {

                    setSelectedIndex(selected + 1);
                }

            } else {

                // We have more recordings that can fit in the window.
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

        Tag[] tarray = getTags();
        if (tarray != null) {

            if (isWindowGreaterOrEqual()) {

                int selected = getSelectedIndex();
                if ((selected - 1) >= 0) {

                    setSelectedIndex(selected - 1);
                }

            } else {

                // We have more recordings that can fit in the window.
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

    /**
     * Retrieve the currently selected Tag.
     *
     * @return A Tag instance.
     */
    public Tag getSelectedTag() {
        return (selectedTag);
    }

    /**
     * Set the currently selected Tag.
     *
     * @param t A Tag instance.
     */
    public void setSelectedTag(Tag t) {

        Tag old = selectedTag;
        selectedTag = t;
        firePropertyChange("SelectedTag", old, selectedTag);
    }

    private int getTagCount() {

        int result = 0;

        ArrayList<Tag> l = getTagList();
        if (l != null) {

            result = l.size();
        }

        return (result);
    }

    private void traverse(Tag root, Tag t, ArrayList<Tag> l,
        HashMap<Tag, Tag[]> m) {

        if ((root != null) && (t != null) && (l != null) && (m != null)) {

            if (root.equals(t.getParent())) {

                if (!l.contains(t)) {
                    l.add(t);
                }
            }

            if (!t.isLeaf()) {

                if (!t.isRoot()) {

                    Tag[] all = t.toArray();
                    all = Arrays.copyOfRange(all, 1, all.length);
                    Arrays.sort(all);
                    m.put(t, all);
                }

                Tag[] kids = t.getChildren();
                for (int i = 0; i < kids.length; i++) {

                    traverse(root, kids[i], l, m);
                }
            }
        }
    }

    /**
     * The preferred way to setup this list is to supply the root tag.  Doing
     * this then the list can be built more efficiently when one has a large
     * number of tags to display.
     *
     * @return A Tag instance that is a root.
     */
    public Tag getRootTag() {
        return (null);
    }

    /**
     * The preferred way to setup this list is to supply the root tag.  Doing
     * this then the list can be built more efficiently when one has a large
     * number of tags to display.
     *
     * @param t A Tag instance that is a root.
     */
    public void setRootTag(Tag t) {

        HashMap<Tag, Tag[]> m = getHiddenMap();
        ArrayList<Tag> l = getTagList();
        if ((l != null) && (m != null)) {

            m.clear();
            l.clear();
            if (t != null) {

                traverse(t, t, l, m);
            }

            Collections.sort(l);
            setCopyTagList(new ArrayList<Tag>(l));
        }

        setSelectedTag(null);
        setStartIndex(0);
    }

    /**
     * We display am array of tags.
     *
     * @return An array of Tag instances.
     */
    public Tag[] getTags() {

        Tag[] result = null;

        ArrayList<Tag> l = getTagList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Tag[l.size()]);
        }

        return (result);
    }

    /**
     * We display am array of tags.
     *
     * @param array An array of Tag instances.
     */
    public void setTags(Tag[] array) {

        if (!isTagsEqual(array)) {

            ArrayList<Tag> l = getTagList();
            if (l != null) {

                l.clear();
                if ((array != null) && (array.length > 0)) {

                    for (int i = 0; i < array.length; i++) {

                        Tag t = array[i];
                        if (t != null) {

                            l.add(t);
                        }
                    }
                }

                Collections.sort(l);
                setCopyTagList(new ArrayList<Tag>(l));

                // Lets as a default collapse all Tags.
                ArrayList<Tag> copy = getCopyTagList();
                if (copy != null) {

                    ArrayList<Tag> levels = new ArrayList<Tag>(copy);
                    Collections.sort(levels, new TagSortByLevel());

                    int level = 5;
                    for (int j = 0; j < levels.size(); j++) {

                        Tag tmp = levels.get(j);
                        if (!tmp.isLeaf()) {

                            collapse(tmp);
                        }
                    }
                }
            }
        }

        setSelectedTag(null);
        setStartIndex(0);
    }

    private boolean isTagsEqual(Tag[] array) {

        boolean result = false;

        if (array != null) {

            ArrayList<Tag> copy = getCopyTagList();
            if (copy != null) {

                // We first assume they are then change when we find a
                // descrepancy.
                result = true;

                if (copy.size() == array.length) {

                    Arrays.sort(array);
                    int max = array.length;
                    if (max > MAX_TO_CHECK_EQUAL) {
                        max = MAX_TO_CHECK_EQUAL;
                    }

                    for (int i = 0; i < max; i++) {

                        if (!array[i].equals(copy.get(i))) {

                            result = false;
                            break;
                        }
                    }

                } else {

                    result = false;
                }
            }
        }

        return (result);
    }

    /**
     * Update the UI.
     */
    private void update() {

        JXLabel[] labs = getLabels();
        if (labs != null) {

            Tag[] tarray = getTags();
            if (tarray != null) {

                int index = getStartIndex();
                for (int i = 0; i < labs.length; i++) {

                    if (index < tarray.length) {

                        if (tarray[index].isLeaf()) {

                            labs[i].setText(" " + tarray[index].getName());

                        } else {

                            int nextIndex = index + 1;
                            if (nextIndex < tarray.length) {

                                Tag nextParent = tarray[nextIndex].getParent();

                                if (nextParent.equals(tarray[index])) {
                                    labs[i].setText("-"
                                        + tarray[index].getName());
                                } else {
                                    labs[i].setText("+"
                                        + tarray[index].getName());
                                }

                            } else {
                                labs[i].setText("+" + tarray[index].getName());
                            }
                        }
                        int level = tarray[index].getLevel() - 1;
                        if (level >= 0) {

                            labs[i].setIcon(getTabIcon(level));

                        } else {

                            labs[i].setIcon(null);
                        }

                    } else {

                        labs[i].setText("");
                    }

                    index++;
                }

            } else {

                for (int i = 0; i < labs.length; i++) {

                    labs[i].setIcon(null);
                    labs[i].setText("");
                }
            }

            applyColor();
            int sindex = getSelectedIndex() + getStartIndex();
            if (sindex < 0) {
                sindex = 0;
            }
            if ((tarray != null) && (tarray.length > sindex)) {
                setSelectedTag(tarray[sindex]);
            } else {
                setSelectedTag(null);
            }

            animate();
        }
    }

    private void applyColor() {

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

    private void animate() {

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

    private int getVisibleCount() {
        return (visibleCount);
    }

    private void setVisibleCount(int i) {
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
        int tmp = getTagCount();
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

    private int getOldSelectedIndex() {
        return (oldSelectedIndex);
    }

    private void setOldSelectedIndex(int i) {
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

        Tag[] tarray = getTags();
        if (tarray != null) {

            result = getVisibleCount() >= tarray.length;
        }

        return (result);
    }

    private boolean isSelectedAtTheBottomWindow() {
        return ((getVisibleCount() - 1) == getSelectedIndex());
    }

    private boolean isSelectedAtTheBottomList() {

        boolean result = false;

        Tag[] tarray = getTags();
        if (tarray != null) {

            Tag selected = getSelectedTag();
            Tag last = tarray[tarray.length - 1];

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

        Tag[] tarray = getTags();
        if (tarray != null) {

            Tag selected = getSelectedTag();
            Tag first = tarray[0];
            if ((selected != null) && (first != null)) {

                result = selected.equals(first);
            }
        }

        return (result);
    }

    static class TagSortByLevel implements Comparator<Tag>, Serializable {

        public int compare(Tag t0, Tag t1) {

            return (t1.getLevel() - t0.getLevel());
        }

    }

}


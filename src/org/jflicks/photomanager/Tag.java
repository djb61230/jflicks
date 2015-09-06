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
package org.jflicks.photomanager;

import java.io.Serializable;
import java.util.ArrayList;

import org.jflicks.util.LogUtil;

/**
 * This class contains all the properties representing a tag.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Tag implements Serializable, Comparable<Tag> {

    private int id;
    private int parentId;
    private String name;
    private Tag parent;
    private ArrayList<Tag> childrenList;
    private int level = -1;
    private String path = null;

    /**
     * Simple empty constructor.
     */
    public Tag() {

        setChildrenList(new ArrayList<Tag>());
    }

    /**
     * Constructor to "clone" a Tag instance.
     *
     * @param t A given Tag.
     */
    public Tag(Tag t) {

        this();
        setId(t.getId());
        setParentId(t.getParentId());
        setName(t.getName());
        setParent(t.getParent());
        setChildren(t.getChildren());
    }

    /**
     * An Id is associated with this object.
     *
     * @return An Id value as an int.
     */
    public int getId() {
        return (id);
    }

    /**
     * An Id is associated with this object.
     *
     * @param i An Id value as an int.
     */
    public void setId(int i) {
        id = i;
    }

    /**
     * A ParentId is associated with this object so it's possible to find
     * a parent even if the Parent property is null.
     *
     * @return A ParentId value as an int.
     */
    public int getParentId() {
        return (parentId);
    }

    /**
     * A ParentId is associated with this object so it's possible to find
     * a parent even if the Parent property is null.
     *
     * @param i A ParentId value as an int.
     */
    public void setParentId(int i) {
        parentId = i;
    }

    /**
     * A Name is associated with this object.
     *
     * @return A Name value as a String.
     */
    public String getName() {
        return (name);
    }

    /**
     * A Name is associated with this object.
     *
     * @param s A Name value as a String.
     */
    public void setName(String s) {
        name = s;
    }

    /**
     * The parent of this tag.
     *
     * @return The parent Tag if it exists.
     */
    public Tag getParent() {
        return (parent);
    }

    /**
     * The parent of this tag.
     *
     * @param t The parent Tag if it exists.
     */
    public void setParent(Tag t) {
        parent = t;
    }

    /**
     * All of our kids.
     *
     * @return An array of Tag instances.
     */
    public Tag[] getChildren() {

        Tag[] result = null;

        ArrayList<Tag> l = getChildrenList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Tag[l.size()]);
        }

        return (result);
    }

    /**
     * All of our kids.
     *
     * @param array An array of Tag instances.
     */
    public void setChildren(Tag[] array) {

        ArrayList<Tag> l = getChildrenList();

        if (l != null) {

            l.clear();

            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    l.add(array[i]);
                }
            }
        }
    }

    /**
     * Add a child.
     *
     * @param t A tag to add.
     */
    public void addChild(Tag t) {

        ArrayList<Tag> l = getChildrenList();

        if ((t != null) && (l != null)) {

            l.add(t);
            t.setParent(this);
        }
    }

    /**
     * Remove a child.
     *
     * @param t A tag to remove.
     */
    public void removeChild(Tag t) {

        ArrayList<Tag> l = getChildrenList();

        if ((t != null) && (l != null)) {

            l.remove(t);
        }
    }

    private ArrayList<Tag> getChildrenList() {
        return (childrenList);
    }

    private void setChildrenList(ArrayList<Tag> l) {
        childrenList = l;
    }

    /**
     * Convenience method to determine if this Tag is the one and only root.
     *
     * @return True if we are root.
     */
    public boolean isRoot() {
        return (getParent() == null);
    }

    /**
     * Convenience method to determine if we are childless.
     *
     * @return True if we are a leaf.
     */
    public boolean isLeaf() {
        return (getChildren() == null);
    }

    /**
     * The level deep into the Tag tree we reside.  Root would be zero.
     *
     * @return An int value.
     */
    public int getLevel() {

        if (level == -1) {

            level = 0;

            Tag p = getParent();
            while (p != null) {

                level++;
                p = p.getParent();
            }
        }

        return (level);
    }

    /**
     * Return the number of decendants.
     *
     * @return The count as an int value.
     */
    public int count() {

        int result = 1;

        if (!isLeaf()) {

            Tag[] kids = getChildren();
            for (int i = 0; i < kids.length; i++) {

                result += kids[i].count();
            }
        }

        return (result);
    }

    /**
     * Turn this Tag and it's parents into a String separated by "/"
     * characters.
     *
     * @return A String instance.
     */
    public String toPath() {

        if (path == null) {

            if (isRoot()) {

                path = "";

            } else {

                Tag p = getParent();
                path = p.toPath() + "/" + getName();
            }
        }

        return (path);
    }

    /**
     * Convenience method to get a child with the given name.
     *
     * @param s A given name as a String.
     * @return A Tag if it exists.
     */
    public Tag getChildByName(String s) {

        Tag result = null;

        Tag[] kids = getChildren();
        if ((kids != null) && (s != null)) {

            for (int i = 0; i < kids.length; i++) {

                if (s.equals(kids[i].getName())) {

                    result = kids[i];
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * Convenience method to see if a child exists with the given name.
     *
     * @param s A given name as a String.
     * @return True if it exists.
     */
    public boolean hasChildByName(String s) {

        boolean result = false;

        Tag[] kids = getChildren();
        if ((kids != null) && (s != null)) {

            for (int i = 0; i < kids.length; i++) {

                if (s.equals(kids[i].getName())) {

                    result = true;
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * Flatten out out Tag to an array of Tag instances.
     *
     * @return An arrat of Tag objects.
     */
    public Tag[] toArray() {

        Tag[] result = null;

        ArrayList<Tag> l = new ArrayList<Tag>();
        toArray(this, l);
        if (l.size() > 0) {

            result = l.toArray(new Tag[l.size()]);
        }

        return (result);
    }

    private void toArray(Tag t, ArrayList<Tag> l) {

        if (t != null) {

            l.add(t);
            Tag[] kids = t.getChildren();
            if (kids != null) {

                for (int i = 0; i < kids.length; i++) {

                    toArray(kids[i], l);
                }
            }
        }
    }

    /**
     * The standard hashcode override.
     *
     * @return An int value.
     */
    public int hashCode() {
        return (toPath().hashCode());
    }

    /**
     * We override equals.
     *
     * @param o A given object to compare.
     * @return True when equal.
     */
    public boolean equals(Object o) {

        boolean result = false;

        if (o == this) {

            result = true;

        } else if (!(o instanceof Tag)) {

            result = false;

        } else {

            Tag t = (Tag) o;
            String s = toPath();
            if (s != null) {

                result = s.equals(t.toPath());
            }
        }

        return (result);
    }

    /**
     * The comparable interface.
     *
     * @param t The given Tag instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(Tag t) throws ClassCastException {

        int result = 0;

        if (t == null) {

            throw new NullPointerException();
        }

        if (t == this) {

            result = 0;

        } else {

            String path0 = toPath();
            String path1 = t.toPath();
            if ((path0 != null) && (path1 != null)) {

                result = path0.compareTo(path1);
            }
        }

        return (result);
    }

    /**
     * Debug method to print out our root Tag.
     */
    public void dump() {

        LogUtil.log(LogUtil.INFO, "id: " + getId() + " name <" + getName() + "> path <" + toPath() + ">");
        Tag[] kids = getChildren();
        if (kids != null) {

            for (int i = 0; i < kids.length; i++) {

                kids[i].dump();
            }
        }
    }

}

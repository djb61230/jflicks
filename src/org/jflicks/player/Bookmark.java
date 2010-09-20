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
package org.jflicks.player;

import java.io.Serializable;

import org.jflicks.util.RandomGUID;

/**
 * This class has all the properties to define a bookmark.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Bookmark implements Serializable {

    private String id;
    private int time;
    private long position;
    private boolean preferTime;

    /**
     * Simple empty constructor.
     */
    public Bookmark() {

        setId(RandomGUID.createGUID());
    }

    /**
     * Constructor to "clone" a Bookmark instance.
     *
     * @param b A given Bookmark.
     */
    public Bookmark(Bookmark b) {

        setId(b.getId());
        setTime(b.getTime());
        setPosition(b.getPosition());
        setPreferTime(b.isPreferTime());
    }

    /**
     * A Bookmark has an ID which links it to a Recording instance.
     *
     * @return A unique Id.
     */
    public String getId() {
        return (id);
    }

    /**
     * A Bookmark has an ID which links it to a Recording instance.
     *
     * @param s A unique Id.
     */
    public void setId(String s) {
        id = s;
    }

    /**
     * The time in seconds where the bookmark is located.
     *
     * @return The time in seconds as an int.
     */
    public int getTime() {
        return (time);
    }

    /**
     * The time in seconds where the bookmark is located.
     *
     * @param i The time in seconds as an int.
     */
    public void setTime(int i) {
        time = i;
    }

    /**
     * A position that defines the location in a video where a bookmark
     * resides.  This is usually the byte offset into a file.
     *
     * @return A long that defines a position.
     */
    public long getPosition() {
        return (position);
    }

    /**
     * A position that defines the location in a video where a bookmark
     * resides.  This is usually the byte offset into a file.
     *
     * @param l A long that defines a position.
     */
    public void setPosition(long l) {
        position = l;
    }

    /**
     * Both the position and time properties signify where the bookmark is
     * located.  This property sets which one should be used.  Some video
     * files may work better with a time offset than a file position.
     *
     * @return True if Time is to be used.
     */
    public boolean isPreferTime() {
        return (preferTime);
    }

    /**
     * Both the position and time properties signify where the bookmark is
     * located.  This property sets which one should be used.  Some video
     * files may work better with a time offset than a file position.
     *
     * @param b True if Time is to be used.
     */
    public void setPreferTime(boolean b) {
        preferTime = b;
    }

}


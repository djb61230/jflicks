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

import java.util.ArrayList;

/**
 * A set of utility methods useful in PhotoManager implementations or classes
 * that use one.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class PhotoUtil {

    /**
     * When asking for Photo instances with a set of Tag inputs, the
     * ALL operation means returned photos each need to be tagged by the
     * given Tag instances.
     */
    public static final int ALL = 1;

    /**
     * When asking for Photo instances with a set of Tag inputs, the
     * ANY operation means returned photos need to be tagged by at
     * least on given Tag instance.
     */
    public static final int ANY = 2;

    private PhotoUtil() {
    }

    /**
     * Acquire a set of Photo instances conforming to the input operation
     * and set of Tag instances.
     *
     * @param operation Either ALL or ANY.
     * @param photos A set of Photo instances.
     * @param tags A set of Tag instances.
     * @return An array of Photo instances.
     */
    public static Photo[] getTaggedPhotos(int operation, Photo[] photos,
        Tag[] tags) {

        Photo[] result = null;

        if ((photos != null) && (tags != null)) {

            ArrayList<Photo> l = new ArrayList<Photo>();
            for (int i = 0; i < photos.length; i++) {

                switch (operation) {

                case ALL:

                    if (containsAll(photos[i].getTagPaths(), tags)) {

                        l.add(photos[i]);
                    }
                    break;

                default:
                case ANY:

                    if (containsAny(photos[i].getTagPaths(), tags)) {

                        l.add(photos[i]);
                    }
                    break;
                }
            }

            if (l.size() > 0) {

                result = l.toArray(new Photo[l.size()]);
            }
        }

        return (result);
    }

    private static boolean containsAll(String[] ptags, Tag[] stags) {

        boolean result = false;

        if ((ptags != null) && (stags != null)) {

            result = true;
            for (int i = 0; i < stags.length; i++) {

                Tag tmp = stags[i];
                if (!contains(ptags, tmp)) {

                    result = false;
                    break;
                }
            }
        }

        return (result);
    }

    private static boolean containsAny(String[] ptags, Tag[] stags) {

        boolean result = false;

        if ((ptags != null) && (stags != null)) {

            for (int i = 0; i < stags.length; i++) {

                Tag tmp = stags[i];
                if (contains(ptags, tmp)) {

                    result = true;
                    break;
                }
            }
        }

        return (result);
    }

    private static boolean contains(String[] ptags, Tag t) {

        boolean result = false;

        if ((ptags != null) && (t != null) && (t.isLeaf())) {

            for (int i = 0; i < ptags.length; i++) {

                String tmp = t.toPath();
                if (tmp != null) {

                    if (tmp.equals(ptags[i])) {

                        System.out.println("tmp <" + tmp + "> <" + ptags[i]
                            + ">");
                        result = true;
                        break;
                    }
                }
            }
        }

        return (result);
    }

}


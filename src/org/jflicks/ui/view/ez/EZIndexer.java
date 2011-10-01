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
package org.jflicks.ui.view.ez;

import java.io.Serializable;

import org.jflicks.configure.Configuration;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.util.Util;

/**
 * Simple class to collect and maintain data from a set of Configuration
 * instances.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class EZIndexer implements Serializable, Comparable<EZIndexer> {

    private String title;
    private String description;

    /**
     * Default constructor.
     */
    public EZIndexer() {
    }

    public EZIndexer(EZIndexer i) {

        if (i != null) {

            setTitle(i.getTitle());
            setDescription(i.getDescription());
        }
    }

    public String getTitle() {
        return (title);
    }

    public void setTitle(String s) {
        title = s;
    }

    public String getDescription() {
        return (description);
    }

    public void setDescription(String s) {
        description = s;
    }

    public String toString() {
        return (getDescription());
    }

    /**
     * The standard hashcode override.
     *
     * @return An int value.
     */
    public int hashCode() {

        String tmp = getTitle() + getDescription();
        return (tmp.hashCode());
    }

    /**
     * The equals override method.
     *
     * @param o A gven object to check.
     * @return True if the objects are equal.
     */
    public boolean equals(Object o) {

        boolean result = false;

        if (o == this) {

            result = true;

        } else if (!(o instanceof EZIndexer)) {

            result = false;

        } else {

            EZIndexer i = (EZIndexer) o;
            String tmp = getTitle();
            if (tmp != null) {

                result = tmp.equals(i.getTitle());

            } else {

                result = i.getTitle() == null;
            }

            if (result) {

                String desc = getDescription();
                if (desc != null) {

                    result = desc.equals(i.getDescription());

                } else {

                    result = i.getDescription() == null;
                }
            }
        }

        return (result);
    }

    /**
     * The comparable interface.
     *
     * @param r The given EZRecorder instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(EZIndexer i) throws ClassCastException {

        int result = 0;

        if (i == null) {

            throw new NullPointerException();
        }

        if (i == this) {

            result = 0;

        } else {

            String title0 = getTitle();
            String title1 = i.getTitle();
            result = title0.compareTo(title1);
        }

        return (result);
    }

}

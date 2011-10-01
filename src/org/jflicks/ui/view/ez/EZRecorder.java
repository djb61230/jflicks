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
public class EZRecorder implements Serializable, Comparable<EZRecorder> {

    private String name;
    private EZIndexer indexer;
    private String listingName;
    private String listType;
    private String[] channelList;

    /**
     * Default constructor.
     */
    public EZRecorder() {
    }

    public EZRecorder(EZRecorder r) {

        if (r != null) {

            setName(r.getName());
            setIndexer(r.getIndexer());
            setListingName(r.getListingName());
            setListType(r.getListType());

            String[] chans = r.getChannelList();
            if (chans != null) {

                String[] nchans = new String[chans.length];
                for (int i = 0; i < nchans.length; i++) {

                    nchans[i] = new String(chans[i]);
                }

                setChannelList(nchans);
            }
        }
    }

    public String getName() {
        return (name);
    }

    public void setName(String s) {
        name = s;
    }

    public EZIndexer getIndexer() {
        return (indexer);
    }

    public void setIndexer(EZIndexer i) {
        indexer = i;
    }

    public String getListingName() {
        return (listingName);
    }

    public void setListingName(String s) {
        listingName = s;
    }

    public String getListType() {
        return (listType);
    }

    public void setListType(String s) {
        listType = s;
    }

    public String[] getChannelList() {
        return (channelList);
    }

    public void setChannelList(String[] array) {
        channelList = array;
    }

    public String toString() {
        return (getName());
    }

    /**
     * The standard hashcode override.
     *
     * @return An int value.
     */
    public int hashCode() {

        String istr = "";
        if (getIndexer() != null) {
            istr = getIndexer().toString();
        }
        String tmp = getName() + istr + getListingName() + getListType();
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

        } else if (!(o instanceof EZRecorder)) {

            result = false;

        } else {

            EZRecorder r = (EZRecorder) o;
            String name = getName();
            if (name != null) {

                result = name.equals(r.getName());

            } else {

                result = r.getName() == null;
            }

            if (result) {

                EZIndexer indexer = getIndexer();
                if (indexer != null) {

                    result = indexer.equals(r.getIndexer());

                } else {

                    result = r.getIndexer() == null;
                }

                if (result) {

                    String lname = getListingName();
                    if (lname != null) {

                        result = lname.equals(r.getListingName());

                    } else {

                        result = r.getListingName() == null;
                    }

                    if (result) {

                        String ltype = getListType();
                        if (lname != null) {

                            result = ltype.equals(r.getListType());

                        } else {

                            result = r.getListType() == null;
                        }

                        if (result) {

                            String[] chans = getChannelList();
                            if (chans != null) {

                                String[] compchans = r.getChannelList();
                                if ((compchans != null)
                                    && (compchans.length == chans.length)) {

                                    for (int i = 0; i < chans.length; i++) {

                                        if (!chans[i].equals(compchans[i])) {

                                            result = false;
                                            break;
                                        }
                                    }

                                } else {

                                    result = false;
                                }

                            } else {

                                result = r.getChannelList() == null;
                            }
                        }
                    }
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
    public int compareTo(EZRecorder r) throws ClassCastException {

        int result = 0;

        if (r == null) {

            throw new NullPointerException();
        }

        if (r == this) {

            result = 0;

        } else {

            String name0 = getName();
            String name1 = r.getName();
            result = name0.compareTo(name1);
        }

        return (result);
    }

}

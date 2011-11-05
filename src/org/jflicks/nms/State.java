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
package org.jflicks.nms;

import java.util.ArrayList;

/**
 * This class represents the current state of a running NMS.  The class
 * also has the capability to be "merged" with other state objects so a
 * "set" of NMS instances can be represented.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class State {

    private ArrayList<String> videoCategoryList;
    private boolean supportsLiveTV;
    private boolean supportsOnDemand;
    private boolean availableRecordings;

    public State() {

        setVideoCategoryList(new ArrayList<String>());
    }

    public State(State s) {

        this();
        if (s != null) {

            setVideoCategories(s.getVideoCategories());
            setSupportsLiveTV(s.supportsLiveTV());
            setSupportsOnDemand(s.supportsOnDemand());
            setAvailableRecordings(s.hasAvailableRecordings());
        }
    }

    public State(NMS n) {

        this();
        if (n != null) {

            mergeVideoCategories(this, computeVideoCategories(n.getVideos()));
            setSupportsLiveTV(n.supportsLiveTV());
            setSupportsOnDemand(n.supportsOnDemand());
            setAvailableRecordings(n.getRecordings() != null);
        }
    }

    private ArrayList<String> getVideoCategoryList() {
        return (videoCategoryList);
    }

    private void setVideoCategoryList(ArrayList<String> l) {
        videoCategoryList = l;
    }

    public String[] getVideoCategories() {

        String[] result = null;

        ArrayList<String> l = getVideoCategoryList();

        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new String[l.size()]);
        }

        return (result);
    }

    private void setVideoCategories(String[] array) {

        ArrayList<String> l = getVideoCategoryList();
        if (l != null) {

            l.clear();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    if (!l.contains(array[i])) {
                        l.add(array[i]);
                    }
                }
            }
        }
    }

    public boolean hasAvailableRecordings() {
        return (availableRecordings);
    }

    private void setAvailableRecordings(boolean b) {
        availableRecordings = b;
    }

    public boolean supportsLiveTV() {
        return (supportsLiveTV);
    }

    private void setSupportsLiveTV(boolean b) {
        supportsLiveTV = b;
    }

    public boolean supportsOnDemand() {
        return (supportsLiveTV);
    }

    private void setSupportsOnDemand(boolean b) {
        supportsOnDemand = b;
    }

    private String[] computeVideoCategories(Video[] array) {

        String[] result = null;

        if ((array != null) && (array.length > 0)) {

            ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < array.length; i++) {

                String tmp = array[i].getCategory();
                if (tmp != null) {

                    tmp = tmp.trim();
                    if ((tmp.length() > 0) && (!list.contains(tmp))) {
                        list.add(tmp);
                    }
                }
            }

            if (list.size() > 0) {

                result = list.toArray(new String[list.size()]);
            }
        }

        return (result);
    }

    private void mergeVideoCategories(State s, String[] array) {

        if ((s != null) && (array != null)) {

            ArrayList<String> l = s.getVideoCategoryList();
            if (l != null) {

                for (int i = 0; i < array.length; i++) {

                    if (!l.contains(array[i])) {
                        l.add(array[i]);
                    }
                }
            }
        }
    }

    public State merge(State s) {

        State result = new State(this);

        if (s != null) {

            mergeVideoCategories(result, s.getVideoCategories());

            if (s.supportsLiveTV()) {
                result.setSupportsLiveTV(true);
            }

            if (s.supportsOnDemand()) {
                result.setSupportsOnDemand(true);
            }

            if (s.hasAvailableRecordings()) {
                result.setAvailableRecordings(true);
            }
        }

        return (result);
    }

}


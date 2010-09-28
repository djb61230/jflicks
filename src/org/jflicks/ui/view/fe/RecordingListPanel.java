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

import java.util.ArrayList;

import org.jflicks.tv.Recording;

import org.jdesktop.swingx.JXLabel;

/**
 * Display Recording instances in a list.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordingListPanel extends BaseListPanel {

    private ArrayList<Recording> recordingList;
    private boolean completeDescription;
    private boolean useTitle;

    /**
     * Simple empty constructor.
     */
    public RecordingListPanel() {

        setRecordingList(new ArrayList<Recording>());
        setPropertyName("SelectedRecording");
    }

    /**
     * The complete description is the 'Title - "Subtitle"'.  If not
     * set just the subtitle is displayed.
     *
     * @return True is a complete description is wanted.
     */
    public boolean isCompleteDescription() {
        return (completeDescription);
    }

    /**
     * The complete description is the 'Title - "Subtitle"'.  If not
     * set just the subtitle is displayed.
     *
     * @param b True is a complete description is wanted.
     */
    public void setCompleteDescription(boolean b) {
        completeDescription = b;
    }

    /**
     * The button text can either be the Title or Subtitle property of a
     * Recording.  This signifies if the Title is used.
     *
     * @return True if the Title text is used.
     */
    public boolean isUseTitle() {
        return (useTitle);
    }

    /**
     * The button text can either be the Title or Subtitle property of a
     * Recording.  This signifies if the Title is used.
     *
     * @param b True if the Title text is used.
     */
    public void setUseTitle(boolean b) {
        useTitle = b;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getObjects() {

        Object[] result = null;

        ArrayList<Recording> l = getRecordingList();
        if (l != null) {

            result = l.toArray(new Object[l.size()]);
        }

        return (result);
    }

    /**
     * Retrieve the currently selected Recording.
     *
     * @return A Recording instance.
     */
    public Recording getSelectedRecording() {
        return ((Recording) getSelectedObject());
    }

    public void setSelectedRecording(Recording r) {
        setSelectedObject(r);
    }

    private ArrayList<Recording> getRecordingList() {
        return (recordingList);
    }

    private void setRecordingList(ArrayList<Recording> l) {
        recordingList = l;
    }

    /**
     * We list recording in our panel.
     *
     * @return An array of Recording instances.
     */
    public Recording[] getRecordings() {

        Recording[] result = null;

        ArrayList<Recording> l = getRecordingList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new Recording[l.size()]);
        }

        return (result);
    }

    /**
     * We list recording in our panel.
     *
     * @param array An array of Recording instances.
     */
    public void setRecordings(Recording[] array) {

        ArrayList<Recording> l = getRecordingList();
        if (l != null) {

            l.clear();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    l.add(array[i]);
                }

                //setSelectedObject(null);
                //setStartIndex(0);
            }
        }
    }

    /**
     * Be able to update just one of the Recording instances we are
     * using.  It has likely updated in one or more of it's properties
     * and we need to ensure we have the changes.
     *
     * @param r A given Recording to update.
     */
    public void updateRecording(Recording r) {

        if (r != null) {

            // First thing is to update the Recording in our list.
            int index = getRecordingById(r.getId());
            if (index != -1) {

                ArrayList<Recording> l = getRecordingList();
                if (l != null) {

                    synchronized (l) {

                        if (l.size() > index) {

                            l.remove(index);
                            l.add(index, r);
                        }
                    }
                }
            }
        }
    }

    private int getRecordingById(String s) {

        int result = -1;

        ArrayList<Recording> l = getRecordingList();
        if ((s != null) && (l != null) && (l.size() > 0)) {

            for (int i = 0; i < l.size(); i++) {

                if (s.equals(l.get(i).getId())) {

                    result = i;
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * Update the UI.  We override because sometimes we want to display
     * our text differently.
     */
    protected void update() {

        ArrayList<Recording> l = getRecordingList();
        JXLabel[] labs = getLabels();
        if ((l != null) && (labs != null)) {

            int index = getStartIndex();
            for (int i = 0; i < labs.length; i++) {

                if (index < l.size()) {

                    if (isCompleteDescription()) {

                        String tmp = l.get(index).getSubtitle();
                        if (tmp != null) {

                            String complete = l.get(index).getTitle()
                                + " - " + "\"" + tmp + "\"";
                            labs[i].setText(complete);

                        } else {
                            labs[i].setText(l.get(index).getTitle());
                        }

                    } else {

                        String tmp = null;
                        if (isUseTitle()) {
                            tmp = l.get(index).getTitle();
                        } else {
                            tmp = l.get(index).getSubtitle();
                        }
                        if (tmp == null) {
                            tmp = l.get(index).getTitle();
                        }
                        labs[i].setText(tmp);
                    }

                } else {

                    labs[i].setText("");
                }

                index++;
            }

            applyColor();
            int sindex = getSelectedIndex() + getStartIndex();
            if (sindex < 0) {
                sindex = 0;
            }
            if (l.size() > sindex) {
                setSelectedObject(l.get(sindex));
            }

            animate();
        }
    }

}


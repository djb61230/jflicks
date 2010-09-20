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
package org.jflicks.tv.scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.jflicks.tv.Channel;
import org.jflicks.tv.recorder.Recorder;

/**
 * This class maintains the usage of a Recorder in the near term.  A class
 * is needed that can help determine whether a Recorder is available to
 * record at some point in time, or can record a given Channel.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecorderInformation {

    private Recorder recorder;
    private Channel[] channels;
    private ArrayList<TimeRange> timeRangeList;

    /**
     * Simple constructor.
     */
    public RecorderInformation() {

        setTimeRangeList(new ArrayList<TimeRange>());
    }

    private ArrayList<TimeRange> getTimeRangeList() {
        return (timeRangeList);
    }

    private void setTimeRangeList(ArrayList<TimeRange> l) {
        timeRangeList = l;
    }

    /**
     * Add the given time range.  No checking is done to verify that it
     * "fits" as we assume the IsBusyAt method was called before this
     * one.  So beware.  Also we keep the list of TimeRanges sorted.
     *
     * @param tr A given TimeRange to add.
     */
    public void addTimeRange(TimeRange tr) {

        ArrayList<TimeRange> list = getTimeRangeList();
        if ((tr != null) && (list != null)) {

            list.add(tr);
            Collections.sort(list);
        }
    }

    /**
     * Convenience method to clear our list of TimeRanges.
     */
    public void clear() {

        ArrayList<TimeRange> list = getTimeRangeList();
        if (list != null) {

            list.clear();
        }
    }

    /**
     * A Recorder that is the subject of our information.
     *
     * @return A Recorder instance.
     */
    public Recorder getRecorder() {
        return (recorder);
    }

    /**
     * A Recorder that is the subject of our information.
     *
     * @param r A Recorder instance.
     */
    public void setRecorder(Recorder r) {
        recorder = r;
    }

    /**
     * We need to keep a list of the Channels supported by the Recorder.
     *
     * @return An array of Channels.
     */
    public Channel[] getChannels() {

        Channel[] result = null;

        if (channels != null) {

            result = Arrays.copyOf(channels, channels.length);
        }

        return (result);
    }

    /**
     * We need to keep a list of the Channels supported by the Recorder.
     *
     * @param array An array of Channels.
     */
    public void setChannels(Channel[] array) {

        if (array != null) {
            channels = Arrays.copyOf(array, array.length);
        } else {
            channels = null;
        }
    }

    /**
     * Determine if the given Channel is supported by our Recorder.
     *
     * @param c A given Channel to check.
     * @return True if the given Channel is in our list of Channels.
     */
    public boolean supports(Channel c) {

        boolean result = false;

        Channel[] array = getChannels();
        if ((c != null) && (array != null)) {

            for (int i = 0; i < array.length; i++) {

                if (array[i].equals(c)) {

                    result = true;
                    break;
                }
            }
        }

        return (result);
    }

    /**
     * Determine if the block of time passed here interferes with a time
     * that already has been reserved for the Recorder.
     *
     * @param tr A given TimeRange instance to check.
     * @return True if there would be a time conflict with the given TimeRange.
     */
    public boolean isBusyAt(TimeRange tr) {

        boolean result = false;

        ArrayList<TimeRange> list = getTimeRangeList();
        if ((tr != null) && (list != null)) {

            for (int i = 0; i < list.size(); i++) {

                TimeRange tmp = list.get(i);
                if (tmp.overlaps(tr)) {

                    result = true;
                    break;
                }
            }
        }

        return (result);
    }

}


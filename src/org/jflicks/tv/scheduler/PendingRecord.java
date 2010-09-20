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

import java.io.File;

import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.recorder.Recorder;

/**
 * This class has all the properties needed to encapsulate a recording that
 * is pending in the future.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class PendingRecord implements Comparable<PendingRecord> {

    /**
     * This recording is valid but it will be recorded later instead.
     */
    public static final int LATER = 0;

    /**
     * This recording will be recorded earlier.
     */
    public static final int EARLIER = 1;

    /**
     * This recording will not happen since it has been recorded before.
     */
    public static final int PREVIOUS_RECORD = 2;

    /**
     * This recording is ready to be executed at it's proper time.
     */
    public static final int READY = 3;

    /**
     * This recording currently has a conflict with another.
     */
    public static final int CONFLICT = 4;

    /**
     * This recording currently has undetermined status.
     */
    public static final int UNDETERMINED = 5;

    private String name;
    private Channel channel;
    private Recorder recorder;
    private long start;
    private long duration;
    private File file;
    private String showId;
    private int status;
    private boolean laterAvailable;
    private boolean earlierAvailable;
    private RecordingRule recordingRule;
    private Recording recording;

    /**
     * Simple empty constructor.
     */
    public PendingRecord() {

        setStatus(UNDETERMINED);
    }

    /**
     * A name for this PendingRecord, usually the show title.
     *
     * @return A name.
     */
    public String getName() {
        return (name);
    }

    /**
     * A name for this PendingRecord, usually the show title.
     *
     * @param s A name.
     */
    public void setName(String s) {
        name = s;
    }

    /**
     * Need to have a Channel to know where to record.
     *
     * @return A Channel instance.
     */
    public Channel getChannel() {
        return (channel);
    }

    /**
     * Need to have a Channel to know where to record.
     *
     * @param c A Channel instance.
     */
    public void setChannel(Channel c) {
        channel = c;
    }

    /**
     * Need to have a Recorder to know where to record.
     *
     * @return A Recorder instance.
     */
    public Recorder getRecorder() {
        return (recorder);
    }

    /**
     * Need to have a Recorder to know where to record.
     *
     * @param r A Recorder instance.
     */
    public void setRecorder(Recorder r) {
        recorder = r;
    }

    /**
     * Need to know when to record.
     *
     * @return The time in Unix epoch.
     */
    public long getStart() {
        return (start);
    }

    /**
     * Need to know when to record.
     *
     * @param l The time in Unix epoch.
     */
    public void setStart(long l) {
        start = l;
    }

    /**
     * Need to know how long to record.
     *
     * @return The duration in seconds.
     */
    public long getDuration() {
        return (duration);
    }

    /**
     * Need to know how long to record.
     *
     * @param l The duration in seconds.
     */
    public void setDuration(long l) {
        duration = l;
    }

    /**
     * The destination File for the recording.
     *
     * @return The File instance.
     */
    public File getFile() {
        return (file);
    }

    /**
     * The destination File for the recording.
     *
     * @param f The File instance.
     */
    public void setFile(File f) {
        file = f;
    }

    /**
     * In case a we want access to the Show object we keep a showId property
     * in this class.
     *
     * @return A show id value.
     */
    public String getShowId() {
        return (showId);
    }

    /**
     * In case a we want access to the Show object we keep a showId property
     * in this class.
     *
     * @param s A show id value.
     */
    public void setShowId(String s) {
        showId = s;
    }

    /**
     * We keep a reference to the RecordingRule this PendingRecord was
     * generated from.
     *
     * @return The RecordingRule from whence this pendingRecord originated.
     */
    public RecordingRule getRecordingRule() {
        return (recordingRule);
    }

    /**
     * We keep a reference to the RecordingRule this PendingRecord was
     * generated from.
     *
     * @param rr The RecordingRule from whence this pendingRecord originated.
     */
    public void setRecordingRule(RecordingRule rr) {
        recordingRule = rr;
    }

    /**
     * As a convenience we keep a Recording instance in the PendingRecord
     * for easy storage into the data base if the PendingRecord is executed.
     *
     * @return A Recording instance.
     */
    public Recording getRecording() {
        return (recording);
    }

    /**
     * As a convenience we keep a Recording instance in the PendingRecord
     * for easy storage into the data base if the PendingRecord is executed.
     *
     * @param r A Recording instance.
     */
    public void setRecording(Recording r) {
        recording = r;
    }

    /**
     * The status of this PendingRecord.
     *
     * @return The status as an int.
     */
    public int getStatus() {
        return (status);
    }

    /**
     * The status of this PendingRecord.
     *
     * @param i The status as an int.
     */
    public void setStatus(int i) {
        status = i;
    }

    /**
     * Convenience method to get a String description of the status.
     *
     * @return A String instance.
     */
    public String getStatusAsString() {

        String result = null;

        if (isPreviousRecordStatus()) {
            result = NMSConstants.PREVIOUSLY_RECORDED;
        } else if (isLaterStatus()) {
            result = NMSConstants.LATER;
        } else if (isEarlierStatus()) {
            result = NMSConstants.EARLIER;
        } else if (isReadyStatus()) {
            result = NMSConstants.READY;
        } else if (isConflictStatus()) {
            result = NMSConstants.CONFLICT;
        } else if (isUndeterminedStatus()) {
            result = NMSConstants.UNDETERMINED;
        }

        return (result);
    }

    /**
     * Convenience method to see if this has already been recorded.
     *
     * @return True if previously recorded.
     */
    public boolean isPreviousRecordStatus() {
        return (getStatus() == PREVIOUS_RECORD);
    }

    /**
     * Convenience method to see if this should be recorded later.
     *
     * @return True if determined it will be recorded later.
     */
    public boolean isLaterStatus() {
        return (getStatus() == LATER);
    }

    /**
     * Convenience method to see if this will be recorded earlier.
     *
     * @return True if determined it will be recorded earlier.
     */
    public boolean isEarlierStatus() {
        return (getStatus() == EARLIER);
    }

    /**
     * Convenience method to see if this ready to be recorded.
     *
     * @return True if all is go.
     */
    public boolean isReadyStatus() {
        return (getStatus() == READY);
    }

    /**
     * Convenience method to see if this status is a conflict and cannot
     * record.
     *
     * @return True if status is conflict.
     */
    public boolean isConflictStatus() {
        return (getStatus() == CONFLICT);
    }

    /**
     * Convenience method to see if this status has been determined.
     *
     * @return True if status is currently unknown.
     */
    public boolean isUndeterminedStatus() {
        return (getStatus() == UNDETERMINED);
    }

    /**
     * Signify this recording rule satisfied by a later airing of this show.
     *
     * @return True if this show is available to record later.
     */
    public boolean isLaterAvailable() {
        return (laterAvailable);
    }

    /**
     * Signify this recording rule satisfied by a later airing of this show.
     *
     * @param b True if this show is available to record later.
     */
    public void setLaterAvailable(boolean b) {
        laterAvailable = b;
    }

    /**
     * Signify this recording rule satisfied by an earlier airing of this show.
     *
     * @return True if this show is available to record earlier.
     */
    public boolean isEarlierAvailable() {
        return (earlierAvailable);
    }

    /**
     * Signify this recording rule satisfied by an earlier airing of this show.
     *
     * @param b True if this show is available to record earlier.
     */
    public void setEarlierAvailable(boolean b) {
        earlierAvailable = b;
    }

    /**
     * Override the hashcode.
     *
     * @return An int value.
     */
    public int hashCode() {

        Long obj = Long.valueOf(getStart());
        return (obj.hashCode());
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

        } else if (!(o instanceof PendingRecord)) {

            result = false;

        } else {

            PendingRecord tr = (PendingRecord) o;

            Long start0 = Long.valueOf(getStart());
            Long start1 = Long.valueOf(tr.getStart());
            if ((start0 != null) && (start1 != null)) {

                result = start0.equals(start1);
            }
        }

        return (result);
    }

    /**
     * The comparable interface.
     *
     * @param pr The given PendingRecord instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(PendingRecord pr) throws ClassCastException {

        int result = 0;

        if (pr == null) {

            throw new NullPointerException();
        }

        if (pr == this) {

            result = 0;

        } else {

            Long start0 = Long.valueOf(getStart());
            Long start1 = Long.valueOf(pr.getStart());
            result = start0.compareTo(start1);
        }

        return (result);
    }

}


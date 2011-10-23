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

import org.jflicks.configure.Config;
import org.jflicks.nms.NMS;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.ShowAiring;
import org.jflicks.tv.Upcoming;
import org.jflicks.tv.recorder.Recorder;

/**
 * This interface defines the methods that allow for the creation of
 * scheduling services.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Scheduler extends Config {

    /**
     * The Scheduler interface needs a title property.
     */
    String TITLE_PROPERTY = "Scheduler-Title";

    /**
     * The title of this schedule service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * Retrieve the currently configured Listing names.  No sense in
     * scheduling a program if there isn't a recorder that could do
     * the job.
     *
     * @return A String array og Listing names.
     */
    String[] getConfiguredListingNames();

    /**
     * Convenience method to retrieve the Recorders which are configured
     * to record for a Listing.
     *
     * @return An array of Recorder instances.
     */
    Recorder[] getConfiguredRecorders();

    /**
     * Convenience method to retrieve the directories which are configured
     * for recordings to be saved.
     *
     * @return An array of String instances.
     */
    String[] getConfiguredRecordingDirecories();

    /**
     * Acquire all the current RecordingRule instances that are defined.
     *
     * @return An array of RecordingRule objects.
     */
    RecordingRule[] getRecordingRules();

    /**
     * Add the given RecordingRule.
     *
     * @param rr A given RecordingRule to add.
     */
    void addRecordingRule(RecordingRule rr);

    /**
     * Remove the given RecordingRule.
     *
     * @param rr A given RecordingRule to remove.
     */
    void removeRecordingRule(RecordingRule rr);

    /**
     * Acquire all the current Recording instances that are defined.
     *
     * @return An array of Recording objects.
     */
    Recording[] getRecordings();

    /**
     * Update the Recording.  Perhaps some post processing execution may have
     * changed some properties of the given Recording.  If so it needs to be
     * updated by the Scheduler to reflect these changes.
     *
     * @param r A given Recording to update.
     */
    void updateRecording(Recording r);

    /**
     * Index the Recording.  Generally Recorders make transport stream
     * mpg files so we may want a way to index them so a Player will
     * handle playing them a bit better.
     *
     * @param s A given indexing task name to use.
     * @param r A given Recording raw video to index.
     */
    void indexRecording(String s, Recording r);

    /**
     * Add the given Recording.
     *
     * @param r A given Recording to add.
     */
    void addRecording(Recording r);

    /**
     * Remove the given Recording.
     *
     * @param r A given Recording to remove.
     */
    void removeRecording(Recording r);

    /**
     * The scheduler needs access to the NMS since it has some convenience
     * methods to get guide data.
     *
     * @return A NMS instance.
     */
    NMS getNMS();

    /**
     * The scheduler needs access to the NMS since it has some convenience
     * methods to get guide data.  On discovery of a Scheduler, a NMS should
     * set this property.
     *
     * @param n A NMS instance.
     */
    void setNMS(NMS n);

    /**
     * A Sceduler needs to keep track whether it has already recorded a show.
     *
     * @param showId A given show Id to check.
     * @return true if already recorded in the past.
     */
    boolean isAlreadyRecorded(String showId);

    /**
     * Request that the scheduler update it's recording queue.
     */
    void requestRescheduling();

    /**
     * Request that the scheduler update it's internal cache.
     */
    void rebuildCache();

    /**
     * Given a Channel and a series Id, find all the ShowAiring instances.
     *
     * @param c A given Channel.
     * @param seriesId A given series Id.
     * @return An array of ShowAiring instances.
     */
    ShowAiring[] getShowAiringsByChannelAndSeriesId(Channel c, String seriesId);

    /**
     * Acquire the PendingRecord instances that are ready to start in the
     * next 60 seconds.  They will be "dequeued" and will not be available
     * in any future call.
     *
     * @return An Array of PendingRecord objects.
     */
    PendingRecord[] getReadyPendingRecords();

    /**
     * A scheduler also decides on a stragedy to create a output file
     * for a recording.  It needs to take in account balancing the use
     * of multiple directory paths and managing disk usage.
     *
     * @param pr A given PendingRecord that may be used in determining the
     * definition of the File.
     * @return A File instance.
     */
    File createFile(PendingRecord pr);

    /**
     * Acquire an array of Upcoming instances that detail the scheduled
     * recordings.
     *
     * @return An array of Upcoming objects.
     */
    Upcoming[] getUpcomings();

    /**
     * Add the given RecordedShow.
     *
     * @param rs A given RecordedShow to add.
     */
    void addRecordedShow(RecordedShow rs);

    /**
     * Remove the given RecordedShow.
     *
     * @param rs A given RecordedShow to remove.
     */
    void removeRecordedShow(RecordedShow rs);

    /**
     * Given a Recorder find it's listing name.
     *
     * @param r A given Recorder instance.
     * @return A Listing name if it exists.
     */
    String getListingNameByRecorder(Recorder r);

    /**
     * Not all channels necessarily are configured to be recorded at
     * the current time. Use this method just to get the channels that
     * actually could be recorded.
     *
     * @return The Channel instances that could be recorded.
     */
    Channel[] getRecordableChannels();
}


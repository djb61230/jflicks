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
package org.jflicks.tv;

import java.io.Serializable;
import java.util.Arrays;

import org.jflicks.util.RandomGUID;

/**
 * This class contains all the properties representing a recording rule.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RecordingRule implements Serializable, Comparable<RecordingRule> {

    /**
     * A rule which to signify to not record.
     */
    public static final int DO_NOT_RECORD_TYPE = 0;

    /**
     * A rule designed to happen just once.
     */
    public static final int ONCE_TYPE = 1;

    /**
     * Record every showing on the channel defined by the ChannelId
     * property.
     */
    public static final int SERIES_TYPE = 2;

    private static final String DO_NOT_RECORD_TEXT = "Do Not Record";
    private static final String ONCE_TEXT = "Once";
    private static final String SERIES_TEXT = "Series";

    /**
     * The highest priority.
     */
    public static final int HIGHEST_PRIORITY = 0;

    /**
     * A high priority.
     */
    public static final int HIGH_PRIORITY = 1;

    /**
     * The "normal" priority.
     */
    public static final int NORMAL_PRIORITY = 2;

    /**
     * A low priority.
     */
    public static final int LOW_PRIORITY = 3;

    /**
     * The lowest priority.
     */
    public static final int LOWEST_PRIORITY = 4;

    private static final String HIGHEST_TEXT = "Highest";
    private static final String HIGH_TEXT = "High";
    private static final String NORMAL_TEXT = "Normal";
    private static final String LOW_TEXT = "Low";
    private static final String LOWEST_TEXT = "Lowest";

    /**
     * Control how the compareTo method behaves by having it sort by
     * the name of the recording rule.
     */
    public static final int SORT_BY_NAME = 0;

    /**
     * Control how the compareTo method behaves by having it sort by
     * the priority of the recording rule.
     */
    public static final int SORT_BY_PRIORITY = 1;

    private String id;
    private String name;
    private int type;
    private int channelId;
    private String showId;
    private String seriesId;
    private long duration;
    private int priority;
    private int sortBy;
    private ShowAiring showAiring;
    private Task[] tasks;
    private int beginPadding;
    private int endPadding;
    private String hostPort;

    /**
     * Simple empty constructor.
     */
    public RecordingRule() {

        setId(RandomGUID.createGUID());
        setSortBy(SORT_BY_NAME);
    }

    /**
     * Convenience constructor to build a recording rule from a ShowAiring
     * instance.  This will create a ONCE recording..
     *
     * @param sa A given ShowAiring instance.
     */
    public RecordingRule(ShowAiring sa) {

        this();
        setType(ONCE_TYPE);
        setShowAiring(sa);
    }

    /**
     * Constructor to "clone" a RecordingRule instance.
     *
     * @param rr A given RecordingRule.
     */
    public RecordingRule(RecordingRule rr) {

        setId(rr.getId());
        setName(rr.getName());
        setType(rr.getType());
        setChannelId(rr.getChannelId());
        setShowId(rr.getShowId());
        setSeriesId(rr.getSeriesId());
        setDuration(rr.getDuration());
        setPriority(rr.getPriority());
        setSortBy(rr.getSortBy());
        setShowAiring(rr.getShowAiring());
        setBeginPadding(rr.getBeginPadding());
        setEndPadding(rr.getEndPadding());
        setHostPort(rr.getHostPort());

        Task[] array = rr.getTasks();
        if (array != null) {

            Task[] ntasks = new Task[array.length];
            for (int i = 0; i < ntasks.length; i++) {

                ntasks[i] = new Task(array[i]);
            }

            setTasks(ntasks);
        }
    }

    /**
     * A unique ID is associated with this object.
     *
     * @return An ID value as a String.
     */
    public String getId() {
        return (id);
    }

    /**
     * A unique ID is associated with this object.
     *
     * @param s An ID value as a String.
     */
    public void setId(String s) {
        id = s;
    }

    /**
     * There is a Name property.
     *
     * @return The name.
     */
    public String getName() {
        return (name);
    }

    /**
     * There is a Name property.
     *
     * @param s The name.
     */
    public void setName(String s) {
        name = s;
    }

    /**
     * The type of the recording rule.
     *
     * @return The type as an int.
     */
    public int getType() {
        return (type);
    }

    /**
     * The type of the recording rule.
     *
     * @param i The type as an int.
     */
    public void setType(int i) {
        type = i;
    }

    /**
     * Convenience method to see if the type is DO_NOT_RECORD.
     *
     * @return True if the type is DO_NOT_RECORD.
     */
    public boolean isDoNotRecordType() {
        return (getType() == DO_NOT_RECORD_TYPE);
    }

    /**
     * Convenience method to see if the type is ONCE.
     *
     * @return True if the type is ONCE.
     */
    public boolean isOnceType() {
        return (getType() == ONCE_TYPE);
    }

    /**
     * Convenience method to see if the type is SERIES.
     *
     * @return True if the type is SERIES.
     */
    public boolean isSeriesType() {
        return (getType() == SERIES_TYPE);
    }

    /**
     * A channel ID is associated with this object.
     *
     * @return An ID value as an int.
     */
    public int getChannelId() {
        return (channelId);
    }

    /**
     * A channel ID is associated with this object.
     *
     * @param i An ID value as an int.
     */
    public void setChannelId(int i) {
        channelId = i;
    }

    /**
     * This Rule is associated with a Show by the ShowId property.  This
     * is used for "one-time" recordings.
     *
     * @return The Show ID as a String instance.
     */
    public String getShowId() {
        return (showId);
    }

    /**
     * This Rule is associated with a Show by the ShowId property.  This
     * is used for "one-time" recordings.
     *
     * @param s The Show ID as a String instance.
     */
    public void setShowId(String s) {
        showId = s;
    }

    /**
     * This Rule is associated with a "series" by the SeriesId property.
     *
     * @return The series ID as a String instance.
     */
    public String getSeriesId() {
        return (seriesId);
    }

    /**
     * This Rule is associated with a "series" by the SeriesId property.
     *
     * @param s The series ID as a String instance.
     */
    public void setSeriesId(String s) {
        seriesId = s;
    }

    /**
     * The priority of the recording rule.
     *
     * @return The priority as an int.
     */
    public int getPriority() {
        return (priority);
    }

    /**
     * The priority of the recording rule.
     *
     * @param i The priority as an int.
     */
    public void setPriority(int i) {
        priority = i;
    }

    /**
     * Sort by Name or Priority.
     *
     * @return An int value.
     */
    public int getSortBy() {
        return (sortBy);
    }

    /**
     * Sort by Name or Priority.
     *
     * @param i An int value.
     */
    public void setSortBy(int i) {
        sortBy = i;
    }

    /**
     * Convenience method to see if sorting is by name.
     *
     * @return True is sorting is set to SORT_BY_NAME.
     */
    public boolean isSortByName() {
        return (getSortBy() == SORT_BY_NAME);
    }

    /**
     * Convenience method to see if sorting is by priority.
     *
     * @return True is sorting is set to SORT_BY_PRIORITY.
     */
    public boolean isSortByPriority() {
        return (getSortBy() == SORT_BY_PRIORITY);
    }

    /**
     * If this is a ONCE recording we keep how long to record so we don't
     * to look it up later.
     *
     * @return A long value representing the number of seconds to record.
     */
    public long getDuration() {
        return (duration);
    }

    /**
     * If this is a ONCE recording we keep how long to record so we don't
     * to look it up later.
     *
     * @param l A long value representing the number of seconds to record.
     */
    public void setDuration(long l) {
        duration = l;
    }

    /**
     * We use this property when a RecordingRule is intended to record a
     * particular show when it's a ONCE_TYPE.
     *
     * @return A ShowAiring instance.
     */
    public ShowAiring getShowAiring() {
        return (showAiring);
    }

    /**
     * We use this property when a RecordingRule is intended to record a
     * particular show when it's a ONCE_TYPE.
     *
     * @param sa A ShowAiring instance.
     */
    public void setShowAiring(ShowAiring sa) {
        showAiring = sa;
    }

    /**
     * The Task instances associated with this rule.  The Task instances
     * are meant to be executed in the order they appear in this array.
     *
     * @return An array of Task instances.
     */
    public Task[] getTasks() {

        Task[] result = null;

        if (tasks != null) {

            result = Arrays.copyOf(tasks, tasks.length);
        }

        return (result);
    }

    /**
     * The Task instances associated with this rule.  The Task instances
     * are meant to be executed in the order they appear in this array.
     *
     * @param array An array of Task instances.
     */
    public void setTasks(Task[] array) {

        if (array != null) {
            tasks = Arrays.copyOf(array, array.length);
        } else {
            tasks = null;
        }
    }

    /**
     * The begin time for a rule can be altered by padding N number of
     * seconds.  If this is negative the recording will begin earlier,
     * positive it will begin later.
     *
     * @return The number of seconds to adjust the begin time.
     */
    public int getBeginPadding() {
        return (beginPadding);
    }

    /**
     * The begin time for a rule can be altered by padding N number of
     * seconds.  If this is negative the recording will begin earlier,
     * positive it will begin later.
     *
     * @param i The number of seconds to adjust the begin time.
     */
    public void setBeginPadding(int i) {
        beginPadding = i;
    }

    /**
     * The end time for a rule can be altered by padding N number of
     * seconds.  If this is negative the recording will end earlier,
     * positive it will end later.
     *
     * @return The number of seconds to adjust the end time.
     */
    public int getEndPadding() {
        return (endPadding);
    }

    /**
     * The end time for a rule can be altered by padding N number of
     * seconds.  If this is negative the recording will end earlier,
     * positive it will end later.
     *
     * @param i The number of seconds to adjust the end time.
     */
    public void setEndPadding(int i) {
        endPadding = i;
    }

    /**
     * Clients can tell the source of an instance of RecordingRule by this
     * host and port of where it's NMS is running.  It is in the format
     * of host:port.
     *
     * @return The host as a String.
     */
    public String getHostPort() {
        return (hostPort);
    }

    /**
     * Clients can tell the source of an instance of RecordingRule by this
     * host and port of where it's NMS is running.  It is in the format
     * of host:port.
     *
     * @param s The host as a String.
     */
    public void setHostPort(String s) {
        hostPort = s;
    }

    /**
     * The standard hashcode override.
     *
     * @return An int value.
     */
    public int hashCode() {
        return (getName().hashCode());
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

        } else if (!(o instanceof RecordingRule)) {

            result = false;

        } else {

            RecordingRule rr = (RecordingRule) o;
            String s = rr.getName();
            if (s != null) {

                result = s.equals(getName());
            }
        }

        return (result);
    }

    /**
     * The comparable interface.
     *
     * @param rr The given RecordingRule instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(RecordingRule rr) throws ClassCastException {

        int result = 0;

        if (rr == null) {

            throw new NullPointerException();
        }

        if (rr == this) {

            result = 0;

        } else {

            switch (getSortBy()) {

            default:
            case SORT_BY_NAME:
                String s = getName();
                if (s != null) {

                    result = s.compareTo(rr.getName());
                }
                break;

            case SORT_BY_PRIORITY:

                Integer sort0 = Integer.valueOf(getPriority());
                Integer sort1 = Integer.valueOf(rr.getPriority());
                if ((sort0 != null) && (sort1 != null)) {

                    result = sort0.compareTo(sort1);
                }
                break;

            }

        }

        return (result);
    }

    /**
     * We define the type values in static variables as we do not see
     * these changing often.  When new capability is added this array needs
     * to be updated.
     *
     * @return An array of int values.
     */
    public static int[] getTypes() {

        int[] result = {
            DO_NOT_RECORD_TYPE, ONCE_TYPE, SERIES_TYPE
        };

        return (result);
    }

    /**
     * We define the type names in static variables as we do not see
     * these changing often.  When new capability is added this array needs
     * to be updated.
     *
     * @return An array of String instances.
     */
    public static String[] getTypeNames() {

        String[] result = {
            DO_NOT_RECORD_TEXT, ONCE_TEXT, SERIES_TEXT
        };

        return (result);
    }

    /**
     * We define the priority values in static variables as we do not see
     * these changing often.  When new capability is added this array needs
     * to be updated.
     *
     * @return An array of int values.
     */
    public static int[] getPriorities() {

        int[] result = {
            HIGHEST_PRIORITY, HIGH_PRIORITY, NORMAL_PRIORITY, LOW_PRIORITY,
            LOWEST_PRIORITY
        };

        return (result);
    }

    /**
     * We define the priority names in static variables as we do not see
     * these changing often.  When new capability is added this array needs
     * to be updated.
     *
     * @return An array of String instances.
     */
    public static String[] getPriorityNames() {

        String[] result = {
            HIGHEST_TEXT, HIGH_TEXT, NORMAL_TEXT, LOW_TEXT, LOWEST_TEXT
        };

        return (result);
    }

    /**
     * Override by returning the Name property.
     *
     * @return The Name property.
     */
    public String toString() {
        return (getName());
    }

}


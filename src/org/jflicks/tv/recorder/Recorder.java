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
package org.jflicks.tv.recorder;

import java.beans.PropertyChangeListener;
import java.io.File;

import org.jflicks.configure.Config;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Recording;

/**
 * This interface defines the methods that allow for the creation of recording
 * services.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Recorder extends Config {

    /**
     * The Recorder interface needs a title property.
     */
    String TITLE_PROPERTY = "Recorder-Title";

    /**
     * The title of this record service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * The device name of this record service.
     *
     * @return The device name as a String.
     */
    String getDevice();

    /**
     * The Recorder has a notion of the name of some task
     * or process that can accomplish indexing of the raw
     * transport stream video that this recorder can create.
     * This is some sort of symbolic name, not something
     * that is a script or anything concrete.  What actually
     * happens is that the scheduler when being told that a
     * recording has completed, it will use this symbolic
     * name to try to find some post processing worker to
     * accomplish the indexing of the raw mpg.  We don't want
     * this work to be done by the recorder since it may be
     * needed to record something else soon after finishing
     * a recording - we need the system to handle this work
     * instead.  If null then no indexing will be done.
     *
     * @return The indexer name as a String.
     */
    String getIndexerName();

    /**
     * When the Recording was started.
     *
     * @return The time the recorder started recording it's most recent
     * recording.
     */
    long getStartedAt();

    /**
     * Start recording.
     *
     * @param c A given Channel to record.
     * @param duration The length in seconds the recording should be.
     * @param output The local File where the video stream data will be stored.
     * @param live True when the recording live TV.
     */
    void startRecording(Channel c, long duration, File output, boolean live);

    /**
     * Stop recording.
     */
    void stopRecording();

    /**
     * Start streaming.
     *
     * @param c A given Channel to record.
     * @param host A given host to stream data.
     * @param port A given port to stream data.
     */
    void startStreaming(Channel c, String host, int port);

    /**
     * While a recorder is recording or streaming, change the channel
     * without stopping the stream.  Not all recorders can do this so
     * check by using isQuickTunable().
     *
     * @param c A given Channel to tune.
     */
    void quickTune(Channel c);

    /**
     * Stop streaming.
     */
    void stopStreaming();

    /**
     * Simple method to find out if the Recorder is currently recording.
     *
     * @return True if in record mode.
     */
    boolean isRecording();

    /**
     * Simple method to find out if the Recorder is currently recording
     * live TV.
     *
     * @return True if in record mode and doing live TV.
     */
    boolean isRecordingLiveTV();

    /**
     * Simple method to find out if the Recorder is currently recording,
     * and is recording the given Recording.
     *
     * @param r A given recorder to check.
     * @return True if in record mode and specifically doing the Recording.
     */
    boolean isRecording(Recording r);

    /**
     * Some recorders are able to change channels "on the fly" without
     * disrupting recording or streaming.  Then of course other recorders
     * cannot handle changing the stream this way.
     *
     * @return True if the recorder can perform a quick tune.
     */
    boolean isQuickTunable();

    /**
     * The Channel of the most recent or current recording.
     *
     * @return A Channel instance.
     */
    Channel getChannel();

    /**
     * The duration in seconds of the most recent or current recording will
     * be given that it is not stopped prematurly.
     *
     * @return The duration in seconds.
     */
    long getDuration();

    /**
     * The local File instance where the most recent or current recording
     * is being saved.
     *
     * @return A File instance containing the streamed video data.
     */
    File getDestination();

    /**
     * The typical file extension that is associated with the video data
     * supplied by this recorder.
     *
     * @return A String file extension without a ".".
     */
    String getExtension();

    /**
     * The host to receive a stream.
     *
     * @return A String representing a host that is to receive data.
     */
    String getHost();

    /**
     * The port to receive a stream.
     *
     * @return An int representing a port that is to receive data.
     */
    int getPort();

    /**
     * If the user has a list of Channel names, it means they are the only
     * channels that this recorder can actually record.  They need to be a
     * subset of the channels from the Program Listing associated with the
     * Recorder.
     *
     * @return True if list is to be interpreted as a whitelist.
     */
    boolean isWhiteList();

    /**
     * If the user has a list of Channel names, it means they are exception
     * channels that this recorder cannot actually record.  They need to be a
     * subset of the channels from the Program Listing associated with the
     * Recorder.
     *
     * @return True if list is to be interpreted as a blacklist.
     */
    boolean isBlackList();

    /**
     * A list of custom channels controlled by the user to further customize
     * a list of channels from a ProgramData listing that is associated with
     * this Recorder.
     *
     * @return An array of Channel names.
     */
    String[] getChannelNameList();

    /**
     * Convenience method to analyze the custom list of channels and apply
     * them to an array of Channels to return an actual list of correct
     * Channels that are recordable by this Recorder.
     *
     * @param array An array of Channel instances.
     * @return A subset of channels that are a true list of recordable ones.
     */
    Channel[] getCustomChannels(Channel[] array);

    /**
     * Each recorder has a certain way that it needs to scan for
     * channels that it may receive.  Certain recorders may need
     * some sort of configuration file to aid it in tuning to a
     * particular channel.
     *
     * @param array The set of Channel instances associated with
     * this recorder.
     * @param type We supply a scan type here so the recorder can
     * override it's frequency type.
     */
    void performScan(Channel[] array, String type);

    /**
     * Not all recorders will have a Tuner so scanning does not
     * make any sense.
     *
     * @return True if a scan is supported or needed.
     */
    boolean supportsScan();

    /**
     * Add a listener.
     *
     * @param l A given listener.
     */
    void addPropertyChangeListener(PropertyChangeListener l);

    /**
     * Add a listener.
     *
     * @param name A property name.
     * @param l A given listener.
     */
    void addPropertyChangeListener(String name, PropertyChangeListener l);

    /**
     * Remove a listener.
     *
     * @param l A given listener.
     */
    void removePropertyChangeListener(PropertyChangeListener l);

    /**
     * Remove a listener.
     *
     * @param name A property name.
     * @param l A given listener.
     */
    void removePropertyChangeListener(String name, PropertyChangeListener l);
}


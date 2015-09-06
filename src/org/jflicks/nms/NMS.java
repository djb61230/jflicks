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

import org.jflicks.autoart.AutoArt;
import org.jflicks.configure.Config;
import org.jflicks.configure.Configuration;
import org.jflicks.photomanager.Photo;
import org.jflicks.photomanager.PhotoManager;
import org.jflicks.photomanager.Tag;
import org.jflicks.trailer.Trailer;
import org.jflicks.tv.Airing;
import org.jflicks.tv.Channel;
import org.jflicks.tv.LiveTV;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;
import org.jflicks.tv.Task;
import org.jflicks.tv.Upcoming;
import org.jflicks.tv.live.Live;
import org.jflicks.tv.ondemand.OnDemand;
import org.jflicks.tv.ondemand.StreamSession;
import org.jflicks.tv.programdata.ProgramData;
import org.jflicks.tv.postproc.PostProc;
import org.jflicks.tv.recorder.Recorder;
import org.jflicks.tv.scheduler.Scheduler;
import org.jflicks.videomanager.VideoManager;

/**
 * This interface defines the methods that allow a client to configure
 * and access network media.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface NMS extends Config {

    /**
     * The Recorder interface needs a title property.
     */
    String TITLE_PROPERTY = "NMS-Title";

    /**
     * The title of this NMS service.
     *
     * @return The title as a String.
     */
    String getTitle();

    /**
     * A NMS service is presumed to be working from a particular host.
     *
     * @return The host as a String.
     */
    String getHost();

    /**
     * A NMS service is presumed to be working from a particular port.
     *
     * @return The port as an int.
     */
    int getPort();

    /**
     * A http service may be deployed with an NMS service.  It can be used to
     * stream audio or have access to images and other static info.
     *
     * @return The http port as an int.
     */
    int getHttpPort();

    /**
     * An NMS has one or more Recorders associated with it.
     *
     * @return The recorders available to this NMS.
     */
    Recorder[] getRecorders();

    /**
     * An NMS has one or more configured Recorders associated with it.  By
     * configured we mean an schedule or guide is associated with it.
     *
     * @return The recorders configured to this NMS.
     */
    Recorder[] getConfiguredRecorders();

    /**
     * Acquire a particular Recorder that matches the given device name.
     *
     * @param s A given device name.
     * @return A Recorder if it exists.
     */
    Recorder getRecorderByDevice(String s);

    /**
     * An NMS has one or more program data suppliers associated with it.
     *
     * @return The program data services available to this NMS.
     */
    ProgramData[] getProgramData();

    /**
     * True when a ProgramData exists.
     *
     * @return True when available.
     */
    boolean hasProgramData();

    /**
     * True when a ProgramData is running currently.
     *
     * @return True when running.
     */
    boolean isProgramDataUpdatingNow();

    /**
     * Get the time the next time the ProgramData is due to run.
     *
     * @return A long value.
     */
    long getProgramDataNextTimeToRun();

    /**
     * An NMS can be asked to have it's ProgramData objects to update their
     * guide data.  This allows clients to force new guide data.  This is a
     * request to get the guide data at the earliest convenience.  So this
     * will respond quickly as it will do the update later.  We return a
     * boolean to signify it will be done or not.  Since it's a request we
     * can refuse.
     *
     * @return True when the request is granted.
     */
    boolean requestProgramDataUpdate();

    /**
     * We have one and only one Scheduler.  We track them normally so we
     * will use the latest one we find.
     *
     * @return A Scheduler instance.
     */
    Scheduler getScheduler();

    /**
     * We have one and only one Live.  We track them normally so we
     * will use the latest one we find.
     *
     * @return A Live instance.
     */
    Live getLive();

    /**
     * We have one and only one PhotoManager.  We track them normally so we
     * will use the latest one we find.
     *
     * @return A PhotoManager instance.
     */
    PhotoManager getPhotoManager();

    /**
     * We have one and only one VideoManager.  We track them normally so we
     * will use the latest one we find.
     *
     * @return A VideoManager instance.
     */
    VideoManager getVideoManager();

    /**
     * We have one and only one AutoArt service.  We track them normally so we
     * will use the latest one we find.
     *
     * @return An AutoArt instance.
     */
    AutoArt getAutoArt();

    /**
     * We have one and only one PostProc.  We track them normally so we
     * will use the latest one we find.
     *
     * @return A PostProc instance.
     */
    PostProc getPostProc();

    /**
     * An NMS has one or more Trailer sources associated with it.
     *
     * @return The Trailer sources available to this NMS.
     */
    Trailer[] getTrailers();

    /**
     * An NMS has one or more OnDemand sources associated with it.
     *
     * @return The OnDemand sources available to this NMS.
     */
    OnDemand[] getOnDemands();

    /**
     * Allow the user to find the currently defined Task instances.
     *
     * @return An array of Task instances.
     */
    Task[] getTasks();

    /**
     * The components of the NMS have configuration information.
     *
     * @return An array of Configuration instances.
     */
    Configuration[] getConfigurations();

    /**
     * Acquire the currently defined recording rules for this NMS.
     *
     * @return An array of RecordingRule instances.
     */
    RecordingRule[] getRecordingRules();

    /**
     * Retrieve all the current Recording instances on this server.  A
     * Recording has all the information needed to identify the program.
     *
     * @return An array of Recording objects.
     */
    Recording[] getRecordings();

    /**
     * Get a particular Recording by Id.
     *
     * @param id A given Id.
     * @return A Recording if found.
     */
    Recording getRecordingById(String id);

    /**
     * We allow the user to delete a recording via this method.
     *
     * @param r A given Recording to remove.
     * @param allowRerecord When TRUE any record that it has been previously
     * recorded will be removed.
     */
    void removeRecording(Recording r, boolean allowRerecord);

    /**
     * We need to allow the user to stop a recording while it's in
     * progress.
     *
     * @param r The given Recording to stop.
     */
    void stopRecording(Recording r);

    /**
     * Retrieve all the current Video instances on this server.  A
     * Video has all the information needed to identify it.
     *
     * @return An array of Video objects.
     */
    Video[] getVideos();

    /**
     * Get a particular Video by Id.
     *
     * @param id A given Id.
     * @return A Video if found.
     */
    Video getVideoById(String id);

    /**
     * We allow the user to delete a video via this method.
     *
     * @param v A given Video to remove.
     */
    void removeVideo(Video v);

    /**
     * Acquire the specific Configuration by the source name.
     *
     * @param s A given source name.
     * @return A Configuration instance.
     */
    Configuration getConfigurationBySource(String s);

    /**
     * Sometimes a Configuration should be deleted.  This should be used
     * sparingly since a default configuration will always be generated.
     * However sometimes they need to be deleted especially when services
     * are removed, there really isn't a good way to dynamically remove
     * these without screwing over the user if some service fails to load
     * or is mistakenly deleted.  At least this way the user has to get
     * rid of it on purpose.
     *
     * @param c A given Configuration to delete.
     */
    void removeConfiguration(Configuration c);

    /**
     * An NMS consists of a set of Configuration instances and using this
     * method one of them can be updated or saved.
     *
     * @param c A give Configuration instance.
     * @param force Overwite if the Configuration already exists.
     */
    void save(Configuration c, boolean force);

    /**
     * The NMS can save images into it's "web space" for later retrieval
     * by frontend clients.  There are three images types - BANNER_IMAGE_TYPE,
     * FANART_IMAGE_TYPE, and POSTER_IMAGE_TYPE.  An Id argument is required
     * to link the image to a Video or Recording.
     *
     * @param imageType One of the defined image types in NMSConstants.
     * @param url The web url where the image can be fetched.
     * @param id The Id that the image refers.
     */
    void save(int imageType, String url, String id);

    /**
     * The NMS can save images into it's "web space" for later retrieval
     * by frontend clients.  There are three images types - BANNER_IMAGE_TYPE,
     * FANART_IMAGE_TYPE, and POSTER_IMAGE_TYPE.  An Id argument is required
     * to link the image to a Video or Recording.
     *
     * @param imageType One of the defined image types in NMSConstants.
     * @param data The file image loaded as a byte array.
     * @param id The Id that the image refers.
     */
    void save(int imageType, byte[] data, String id);

    /**
     * The NMS can save a Video whose properties have been edited by the
     * user.
     *
     * @param v A given Video to save.
     */
    void save(Video v);

    /**
     * Convenience method to add a new or updated RecordingRule.
     *
     * @param rr A given RecordingRule instance.
     */
    void schedule(RecordingRule rr);

    /**
     * Get the channels defined by the ProgramData property.
     *
     * @return The Channel instances defined by the ProgramData.
     */
    Channel[] getChannels();

    /**
     * Not all channels necessarily are configured to be recorded at
     * the current time.  Use this method just to get the channels
     * that actually could be recorded.
     *
     * @return The Channel instances that could be recorded.
     */
    Channel[] getRecordableChannels();

    /**
     * Acquire the Channels for a Listing given the Listing name.
     *
     * @param s A listing name.
     * @return An array of Channels.
     */
    Channel[] getChannelsByListingName(String s);

    /**
     * Given a Channel instance return an array of it's Airing
     * instances.
     *
     * @param c A given Channel instance.
     * @return An array of Airing instances.
     */
    Airing[] getAiringsByChannel(Channel c);

    /**
     * Given a Channel instance return an array of it's scheduled
     * shows to be aired.
     *
     * @param c A given Channel instance.
     * @return An array of ShowAiring instances.
     */
    ShowAiring[] getShowAiringsByChannel(Channel c);

    /**
     * Given a Channel, and a series Id, find all the ShowAiring instances
     * that match.  For example, using this method one could find all
     * the times "Seinfeld" was in the schedule for a particular Channel.
     *
     * @param c A given Channel to look for.
     * @param seriesId Eash series type has a unique Id.
     * @return An array of ShowAiring instances.
     */
    ShowAiring[] getShowAiringsByChannelAndSeriesId(Channel c, String seriesId);

    /**
     * Search the current set of ShowAiring instances using the given pattern.
     *
     * @param pattern A String query.
     * @param searchType Either SEARCH_TITLE, SEARCH_DESCRIPTION,
     * or SEARCH_TITLE_AND_DESCRIPTION.
     * @return An array of ShowAiring instances that conform to the
     * given pattern.
     */
    ShowAiring[] getShowAirings(String pattern, int searchType);

    /**
     * Convenience method to find a Channel given an Id.
     *
     * @param id A given Id as an int.
     * @param lid A given listing Id.
     * @return A Channel if one exists with the given id.
     */
    Channel getChannelById(int id, String lid);

    /**
     * Convenience method to find a Show given an Id.
     *
     * @param id A given Id as an int.
     * @return A Show if one exists with the given id.
     */
    Show getShowById(String id);

    /**
     * Convenience method to get the Upcoming recordings from the
     * Scheduler.
     *
     * @return An array of Upcoming instances.
     */
    Upcoming[] getUpcomings();

    /**
     * An Upcoming instance can be used to either forget a Recording ever
     * happened which would mean a new Recording would occur, or pretend
     * a Recording did happen so not to record it currently.  The status
     * of the Upcoming instance is used to either forget or remember.
     *
     * @param u A given Upcoming instance to override.
     */
    void overrideUpcoming(Upcoming u);

    /**
     * Perform a video scan and update from found files on disk.  This will
     * only add new files found or will update a Video's Path property if it
     * has changed.  This is nice if you need to move files around. the
     * metadata will not get "lost".  If you remove a file physically from
     * disk using an OS program, the database can get "out of sync".  You
     * need to use a client tool and also remove the video from the database
     * or else it will still show up in your UI because it will still be
     * returned from a getVideos() call.
     */
    void videoScan();

    /**
     * Not every video has available artwork from the Internet and of
     * course HomeVideo certainly won't.  Here we can have artwork generated
     * by grabbing frames from the actual video.  The user can give a specific
     * offset in seconds in case they have an idea where they want to grab.
     *
     * @param v A given Video instance.
     * @param seconds The number of seconds into the Video.
     */
    void generateArtwork(Video v, int seconds);

    /**
     * Convenience method to allow easy sending of messages onto the event
     * service.
     *
     * @param s The given text message to send.
     */
    void sendMessage(String s);

    /**
     * Begin a LiveTV session.
     *
     * @return A LiveTV instance.
     */
    LiveTV openSession();

    /**
     * Begin a LiveTV session.
     *
     * @param channelNumber The Channel we wish to start.
     * @return A LiveTV instance.
     */
    LiveTV openSession(String channelNumber);

    /**
     * Begin a LiveTV session.
     *
     * @param host The host destination for video packets.
     * @param port The port destination for video packets.
     * @return A LiveTV instance.
     */
    LiveTV openSession(String host, int port);

    /**
     * Begin a LiveTV session.
     *
     * @param host The host destination for video packets.
     * @param port The port destination for video packets.
     * @param channelNumber The Channel we wish to start.
     * @return A LiveTV instance.
     */
    LiveTV openSession(String host, int port, String channelNumber);

    /**
     * Change to the given Channel.
     *
     * @param l A LiveTV instance.
     * @param c A given Channel.
     * @return A LiveTV instance (possible the same object passed in).
     */
    LiveTV changeChannel(LiveTV l, Channel c);

    /**
     * Stop a LiveTV session.
     *
     * @param l A LiveTV instance.
     */
    void closeSession(LiveTV l);

    /**
     * The root Tag contains all defined Tags.
     *
     * @return The root Tag instance.
     */
    Tag getRootTag();

    /**
     * Acquire all the Photo instances currently defined.
     *
     * @return An array of Photo instances.
     */
    Photo[] getPhotos();

    /**
     * Have the configured PhotoManager do a photo scan.
     */
    void photoScan();

    /**
     * The current set of Trailers as a streamable set of URLs with the
     * first item being an intro and the rest being video files, most recent
     * listed first.
     *
     * @return An array of URLs as String instances.
     */
    String[] getTrailerURLs();

    /**
     * The path where movie trailers are located.
     *
     * @return A String instance.
     */
    String getTrailerHome();

    /**
     * The full path where a movie trailers intro is located.
     *
     * @return A String instance.
     */
    String getTrailerIntro();

    /**
     * The full path where a feature intro in 16:9 aspect ratio is located.
     *
     * @return A String instance.
     */
    String getFeatureIntro169();

    /**
     * The full path where a feature intro in 2.35:1 aspect ratio is located.
     *
     * @return A String instance.
     */
    String getFeatureIntro235();

    /**
     * The full path where a feature intro in 4:3 aspect ratio is located.
     *
     * @return A String instance.
     */
    String getFeatureIntro43();

    /**
     * An NMS has a group name assigned to it so clients can ignore any
     * NMS that they want to ignore.  This is very helpful when testing new
     * features so the "production" clients can be made to NOT see a server
     * that might be in testing mode.
     *
     * @return A String instance.
     */
    String getGroupName();

    /**
     * An NMS needs to know the how to transform file paths to Stream URLs.
     * This property helps the NMS convert a real path to a stream URL.
     *
     * @return A String path.
     */
    String[] getStreamPaths();

    /**
     * An NMS needs to know the document root of the local HTTP server.
     *
     * @return A String path.
     */
    String getDocumentRoot();

    /**
     * An NMS needs to know the port of the local server streaming video.
     *
     * @return An int value.
     */
    int getStreamPort();

    /**
     * Just get the names of the available OnDemand services running.
     *
     * @return An array of names.
     */
    String[] getOnDemandNames();

    /**
     * Hand off control to a Recorder to perform a channel scan.  This method
     * returns immediately as a scan does take some time.  If the scan has
     * been successfully started then return True.
     *
     * @param recorderSource The Recorder Source property which is used to
     * locate the proper Recorder instance so it's scan can be executed.
     * @param type The type of scan, OTA or CABLE.
     * @return True on successful start.
     */
    boolean performChannelScan(String recorderSource, String type);

    /**
     * Open a session to start viewing from an OnDemand source.
     *
     * @param onDemandName The name of the Ondemand name service.
     * @param host A given host where packets are sent.
     * @param port The port where packets are sent.
     * @return A StreamSession instance.
     */
    StreamSession openSession(String onDemandName, String host, int port);

    /**
     * Perform a command for the given session.  The type defines the
     * command itself.
     *
     * @param ss A given StreamSession instance.
     * @param type The type of command.
     */
    void command(StreamSession ss, int type);

    /**
     * Close a previously opened stream session.
     *
     * @param ss This instance is needed so resources are properly cleaned up.
     */
    void closeSession(StreamSession ss);

    /**
     * We want to be able to allow users to find out what the current
     * state of an NMS is like.  The State class will represent this
     * information.
     *
     * @return A State instance.
     */
    State getState();

    /**
     * Of course all services are optional so this method can be used to
     * see if Live TV is supported.
     *
     * @return True if Live TV is supported.
     */
    boolean supportsLiveTV();

    /**
     * Of course all services are optional so this method can be used to
     * see if OnDemand is supported.
     *
     * @return True if Live TV is supported.
     */
    boolean supportsOnDemand();

    /**
     * Of course all services are optional so this method can be used to
     * see if the OnDemand is supported that has the given name.
     *
     * @param name A name of a OnDemand service.
     * @return True if Live TV is supported.
     */
    boolean supportsOnDemand(String name);
}


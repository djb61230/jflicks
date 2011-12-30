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

/**
 * This class defines system-wide constant values.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class NMSConstants {

    /**
     * The NMS needs a property called "Image Home".
     */
    public static final String IMAGE_HOME = "Image Home";

    /**
     * The Banner image type.
     */
    public static final int BANNER_IMAGE_TYPE = 0;

    /**
     * The Fanart image type.
     */
    public static final int FANART_IMAGE_TYPE = 1;

    /**
     * The Poster image type.
     */
    public static final int POSTER_IMAGE_TYPE = 2;

    /**
     * The configuration name for a "Live" service.
     */
    public static final String LIVE_NAME = "Live";

    /**
     * The channel number to start with when doing live TV.
     */
    public static final String LIVE_START_CHANNEL = "Live Start Channel";

    /**
     * The channel number to start with when doing live TV.
     */
    public static final String LIVE_DIRECTORY = "Live Directory";

    /**
     * The configuration name for a "OnDemand" service.
     */
    public static final String ON_DEMAND_NAME = "OnDemand";

    /**
     * The configuration name for a "Photo Manager" service.
     */
    public static final String PHOTO_MANAGER_NAME = "Photo Manager";

    /**
     * The channel number to start with when doing live TV.
     */
    public static final String PHOTO_MANAGER_URL = "Photo Manager URL";

    /**
     * The configuration name for a "Post Proc" service.
     */
    public static final String POST_PROC_NAME = "Post Proc";

    /**
     * The PostProc needs a property called "Maximum Jobs".
     */
    public static final String POST_PROC_MAXIMUM_JOBS = "Maximum Jobs";

    /**
     * The configuration name for a "Program Data" service.
     */
    public static final String PROGRAM_DATA_NAME = "Program Data";

    /**
     * The configuration name for a "Recorder" service.
     */
    public static final String RECORDER_NAME = "Recorder";

    /**
     * The configuration name for a Recorder service "Audio Input".
     */
    public static final String AUDIO_INPUT_NAME = "Audio Input";

    /**
     * The configuration name for a Recorder service "Video Input".
     */
    public static final String VIDEO_INPUT_NAME = "Video Input";

    /**
     * The configuration name for a Recorder service "Frequency Table".
     */
    public static final String FREQUENCY_TABLE_NAME = "Frequency Table";

    /**
     * Some Recorder instances may need to use the channel name to change
     * channels.  That can be controlled with this property.
     */
    public static final String USE_CHANNEL_NAME = "Use Channel Name";

    /**
     * The configuration name for a Recorder service to
     * "Change Channel Script".
     */
    public static final String CHANGE_CHANNEL_SCRIPT_NAME =
        "Change Channel Script";

    /**
     * The configuration name for a Recorder service to flag that channel
     * change is ready.
     */
    public static final String CHANGE_CHANNEL_READY_TEXT =
        "Change Channel Ready Text";

    /**
     * The configuration recording indexer name for a Recorder service to
     * perform a recording indexer.
     */
    public static final String RECORDING_INDEXER_NAME = "Recording Indexer";

    /**
     * A Recorder can have a custom Channel list associated with it.  Using
     * this a user can say a particular Recorder only supports a subset
     * of Channels from a ProgramData listing.
     */
    public static final String CUSTOM_CHANNEL_LIST = "Custom Channel List";

    /**
     * The type of CUSTOM_CHANNEL_LIST, either LIST_IS_A_BLACKLIST or
     * LIST_IS_A_WHITELIST.
     */
    public static final String CUSTOM_CHANNEL_LIST_TYPE =
        "Custom Channel List Type";

    /**
     * The CUSTOM_CHANNEL_LIST is to be ignored.
     */
    public static final String LIST_IS_IGNORED = "List is ignored";

    /**
     * The CUSTOM_CHANNEL_LIST is interpreted as a blacklist of channels
     * that the Recorder cannot record.  But it can record the rest of the
     * Channels from the ProgramData listing.
     */
    public static final String LIST_IS_A_BLACKLIST = "List is a blacklist";

    /**
     * The CUSTOM_CHANNEL_LIST is interpreted as a whitelist of channels
     * that the Recorder can record.  It cannot record the rest of the
     * Channels from the ProgramData listing.
     */
    public static final String LIST_IS_A_WHITELIST = "List is a whitelist";

    /**
     * The configuration name for a "NMS" service.
     */
    public static final String NMS_NAME = "NMS";

    /**
     * There should be at most just one NMS service and it's source
     * name is defined as "NMS System" service.
     */
    public static final String NMS_SOURCE = "NMS System";

    /**
     * The configuration name for a "Scheduler" service.
     */
    public static final String SCHEDULER_NAME = "Scheduler";

    /**
     * There should be at most just one Scheduler service and it's source
     * name is defined as "Scheduler System" service.  We can envision
     * not having a Scheduler service in an NMS that does not have PVR
     * functionality.
     */
    public static final String SCHEDULER_SOURCE = "Scheduler System";

    /**
     * The configuration name for a "Video Manager" service.
     */
    public static final String VIDEO_MANAGER_NAME = "Video Manager";

    /**
     * The configuration name for a "Auto Art" service.
     */
    public static final String AUTO_ART_NAME = "Auto Art";

    /**
     * The NMS needs a configuration of a set of one or more
     * file system directories where videos can be found.
     */
    public static final String VIDEO_DIRECTORIES = "Video Directories";

    /**
     * The NMS needs a configuration of a set of one or more
     * video file name extensions that identify a video file.
     */
    public static final String VIDEO_EXTENSIONS = "Video Extensions";

    /**
     * The NMS needs a configuration of where downloaded trailers are
     * located.
     */
    public static final String TRAILER_HOME = "Trailer Home";

    /**
     * The NMS needs a configuration of where a intro file for
     * playing trailers is located.
     */
    public static final String TRAILER_INTRO = "Trailer Intro";

    /**
     * The NMS needs a configuration of where a intro file for
     * playing a feature movie is located.  This is in 16:9 aspect ratio.
     */
    public static final String FEATURE_INTRO_169 = "Feature Intro 16:9";

    /**
     * The NMS needs a configuration of where a intro file for
     * playing a feature movie is located.  This is in 2.35:1 aspect ratio.
     */
    public static final String FEATURE_INTRO_235 = "Feature Intro 2.35:1";

    /**
     * The NMS needs a configuration of where a intro file for
     * playing a feature movie is located.  This is in 4:3 aspect ratio.
     */
    public static final String FEATURE_INTRO_43 = "Feature Intro 4:3";

    /**
     * An NMS can be put in logical groups by giving an instance a
     * group name.
     */
    public static final String GROUP_NAME = "Group Name";

    /**
     * The NMS needs to have an idea where it's local HTTP server has it's
     * document root.  Since an NMS deals with real paths it needs to be
     * told where it can find the HTTP server files are served.
     */
    public static final String HTTP_DOCUMENT_ROOT = "HTTP Document Root";

    /**
     * Real files reside on locations that may need translation from
     * their real path to where the HTTP server "sees" them, this is
     * usually via some link if the real path is on another drive.
     */
    public static final String STREAM_PATHS = "Stream Paths";

    /**
     * Category of Video called "Movie".
     */
    public static final String VIDEO_MOVIE = "Movie";

    /**
     * Category of Video called "TV".
     */
    public static final String VIDEO_TV = "TV";

    /**
     * Category of Video called "Home".
     */
    public static final String VIDEO_HOME = "Home";

    /**
     * Category of Video called "Exercise".
     */
    public static final String VIDEO_EXERCISE = "Exercise";

    /**
     * Description for a Recorder service.
     */
    public static final String RECORDING_DEVICE = "Recording Device";

    /**
     * Recorders and ProgramData Listings need to be "connected" to allow
     * for recordings.  There can be Recorders that are found and known
     * about but may not want to be used.  So they can't be used unless
     * they are matched with a listing name.  They will default to this
     * value which is "Not Connected".
     */
    public static final String NOT_CONNECTED = "Not Connected";

    /**
     * The Scheduler needs a configuration of a set of one or more
     * file system directories where recordings can be placed.
     */
    public static final String RECORDING_DIRECTORIES = "Recording Directories";

    /**
     * The Http Stream service will make static content that are streams
     * available using the HTTP_STREAM_NAME definition.
     */
    public static final String HTTP_STREAM_NAME = "stream";

    /**
     * The Http Stream service will make static content that are images
     * available using the HTTP_IMAGES_NAME definition.
     */
    public static final String HTTP_IMAGES_NAME = "images";

    /**
     * The configuration name for a "Trailer" service.
     */
    public static final String TRAILER_NAME = "Trailer";

    /**
     * The configuration name for a "Web" service.
     */
    public static final String WEB_NAME = "Web";

    /**
     * The Web service might access RSS feeds.
     */
    public static final String WEB_RSS_FEEDS = "Feed URLs";

    /**
     * Defined Movie Genre "Action".
     */
    public static final String ADVENTURE_GENRE = "Adventure";

    /**
     * Defined Movie Genre "Christmas".
     */
    public static final String CHRISTMAS_GENRE = "Christmas";

    /**
     * Defined Movie Genre "Comedy".
     */
    public static final String COMEDY_GENRE = "Comedy";

    /**
     * Defined Movie Genre "Drama".
     */
    public static final String DRAMA_GENRE = "Drama";

    /**
     * Defined Movie Genre "Family".
     */
    public static final String FAMILY_GENRE = "Family";

    /**
     * Defined Movie Genre "Horror".
     */
    public static final String HORROR_GENRE = "Horror";

    /**
     * Defined Movie Genre "Musical".
     */
    public static final String MUSICAL_GENRE = "Musical";

    /**
     * Defined Movie Genre "Mystery".
     */
    public static final String MYSTERY_GENRE = "Mystery";

    /**
     * Defined Movie Genre "Now Showing".
     */
    public static final String NOW_SHOWING_GENRE = "Now Showing";

    /**
     * Defined Movie Genre "Romantic Comedy".
     */
    public static final String ROMANTIC_COMEDY_GENRE = "Romantic Comedy";

    /**
     * Defined Movie Genre "Scifi".
     */
    public static final String SCIFI_GENRE = "Scifi";

    /**
     * Defined Movie Genre "Thriller".
     */
    public static final String THRILLER_GENRE = "Thriller";

    /**
     * Defined Movie Genre "War".
     */
    public static final String WAR_GENRE = "War";

    /**
     * Defined Movie Genre "Western".
     */
    public static final String WESTERN_GENRE = "Western";

    /**
     * Defined Movie Genre "Western".
     */
    public static final String UNKNOWN_GENRE = "Unknown";

    /**
     * Aspect Ratio 16x9.
     */
    public static final String ASPECT_RATIO_16X9 = "16x9";

    /**
     * Aspect Ratio 4x3.
     */
    public static final String ASPECT_RATIO_4X3 = "4x3";

    /**
     * Aspect Ratio 2.35:1.
     */
    public static final String ASPECT_RATIO_235X1 = "2.35:1";

    /**
     * Dolby Digital 5.1.
     */
    public static final int AUDIO_DOLBY_DIGITAL_5_1 = 1;

    /**
     * Dolby Digital 2.0.
     */
    public static final int AUDIO_DOLBY_DIGITAL_2_0 = 2;

    /**
     * Video in 1080i.
     */
    public static final int VIDEO_1080I = 1;

    /**
     * Video in 1080p.
     */
    public static final int VIDEO_1080P = 2;

    /**
     * Video in 720p.
     */
    public static final int VIDEO_720P = 3;

    /**
     * Video in 480i.
     */
    public static final int VIDEO_480I = 4;

    /**
     * Video in 480p.
     */
    public static final int VIDEO_480P = 5;

    /**
     * Communication from the NMS to clients is handled by sending messages
     * via the EventAdmin service.  We define a message to tell clients that
     * a Recording has been updated in some way and they should go get it
     * so they have the updated information.
     */
    public static final String MESSAGE_RECORDING_UPDATE = "RecordingUpdate";

    /**
     * Communication from the NMS to clients is handled by sending messages
     * via the EventAdmin service.  We define a message to tell clients that
     * a Recording has been added and they should go get all recordings and
     * update their lists.
     */
    public static final String MESSAGE_RECORDING_ADDED = "RecordingAdded";

    /**
     * Communication from the NMS to clients is handled by sending messages
     * via the EventAdmin service.  We define a message to tell clients that
     * a Recording has been removed and they should go get all recordings and
     * update their lists.
     */
    public static final String MESSAGE_RECORDING_REMOVED = "RecordingRemoved";

    /**
     * Communication from the NMS to clients is handled by sending messages
     * via the EventAdmin service.  We define a message to tell clients that
     * a RecordingRule has been updated in some way and they should go get all
     * rules and update their lists.
     */
    public static final String MESSAGE_RULE_UPDATE = "RecordingRuleUpdate";

    /**
     * Communication from the NMS to clients is handled by sending messages
     * via the EventAdmin service.  We define a message to tell clients that
     * a RecordingRule has been added and they should go get all rules and
     * update their lists.
     */
    public static final String MESSAGE_RULE_ADDED = "RecordingRuleAdded";

    /**
     * Communication from the NMS to clients is handled by sending messages
     * via the EventAdmin service.  We define a message to tell clients that
     * the upcoming recording schedule has been updated and they should go
     * get all upcoming recordings.
     */
    public static final String MESSAGE_SCHEDULE_UPDATE = "ScheduleUpdate";

    /**
     * Communication from the NMS to clients is handled by sending messages
     * via the EventAdmin service.  We define a message to tell clients that
     * there is some update from a Recorder scan performance.
     */
    public static final String MESSAGE_RECORDER_SCAN_UPDATE = "RecorderScan";

    /**
     * A search defined to only examine the title.
     */
    public static final int SEARCH_TITLE = 1;

    /**
     * A search defined to only examine the description.
     */
    public static final int SEARCH_DESCRIPTION = 2;

    /**
     * A search defined to examine both the title and description.
     */
    public static final int SEARCH_TITLE_DESCRIPTION = 3;

    /**
     * Status of an upcoming recording when it has been done previously.
     */
    public static final String PREVIOUSLY_RECORDED = "Previously Recorded";

    /**
     * Status of an upcoming recording when it will be recorded later.
     */
    public static final String LATER = "Later";

    /**
     * Status of an upcoming recording when it will be recorded earlier.
     */
    public static final String EARLIER = "Earlier";

    /**
     * Status of an upcoming recording when it is ready.
     */
    public static final String READY = "Ready";

    /**
     * Status of an upcoming recording when it has a conflict.
     */
    public static final String CONFLICT = "Conflict";

    /**
     * Status of an upcoming recording when it is undetermined.
     */
    public static final String UNDETERMINED = "Undetermined";

    /**
     * A constant for services that may need to define a host property.
     */
    public static final String HOST = "Host";

    /**
     * A constant for services that may need to define a port property.
     */
    public static final String PORT = "Port";

    /**
     * A constant for services that may need to do work every once in
     * a while.
     */
    public static final String UPDATE_TIME_IN_MINUTES =
        "Update Time In Minutes";

    /**
     * A constant for services that may need to define a user name property.
     */
    public static final String USER_NAME = "User Name";

    /**
     * A constant for services that may need to define a password property.
     */
    public static final String PASSWORD = "Password";

    /**
     * A constant for identifying over the air frequency type.
     */
    public static final String OTA = "Over the air";

    /**
     * A constant for identifying over the cable tv frequency type.
     */
    public static final String CABLE = "Cable";

    private NMSConstants() {
    }

}


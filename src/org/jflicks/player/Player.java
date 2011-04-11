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
package org.jflicks.player;

import java.awt.Frame;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import javax.swing.JLayeredPane;

/**
 * This interface defines the methods that allow for the creation of Media
 * Players.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Player {

    /**
     * The Player interface needs a title property.
     */
    String TITLE_PROPERTY = "Player-Title";

    /**
     * The Player interface needs a handle property.
     */
    String HANDLE_PROPERTY = "Player-Handle";

    /**
     * A player that can handle video files.
     */
    String PLAYER_VIDEO = "Video";

    /**
     * A player that can handle video files that are raw transport
     * streams.
     */
    String PLAYER_VIDEO_TRANSPORT_STREAM = "VideoTransportStream";

    /**
     * A player that can handle web video, most likely a browser.
     */
    String PLAYER_VIDEO_WEB = "VideoWeb";

    /**
     * A player that can handle web video, most likely a browser.
     */
    String PLAYER_APPLICATION = "Application";

    /**
     *
     * A player that can handle udp stream video.
     */
    String PLAYER_VIDEO_STREAM_UDP = "VideoStreamUDP";

    /**
     * A player that can handle a dvd.
     */
    String PLAYER_VIDEO_DVD = "VideoDVD";

    /**
     * A player that can handle audio.
     */
    String PLAYER_AUDIO = "Audio";

    /**
     * A player that can handle a photo slideshow.
     */
    String PLAYER_SLIDESHOW = "Slideshow";

    /**
     * The player supplies a unique name.
     *
     * @return The title as a string.
     */
    String getTitle();

    /**
     * A player handles a type of media.
     *
     * @return The type as a String.
     */
    String getType();

    /**
     * Convenience method to see if the player type is PLAYER_AUDIO.
     *
     * @return True if this player works with audio.
     */
    boolean isAudioType();

    /**
     * Convenience method to see if the player type is PLAYER_SLIDESHOW.
     *
     * @return True if this player works with audio.
     */
    boolean isSlideshowType();

    /**
     * Convenience method to see if the player type is PLAYER_VIDEO.
     *
     * @return True if this player works with video.
     */
    boolean isVideoType();

    /**
     * Convenience method to see if the player type is PLAYER_VIDEO_DVD.
     *
     * @return True if this player works with video.
     */
    boolean isVideoDVDType();

    /**
     * Convenience method to see if the player type is PLAYER_VIDEO_STREAM_UDP.
     *
     * @return True if this player works with video.
     */
    boolean isVideoStreamUdpType();

    /**
     * Convenience method to see if the player type is
     * PLAYER_VIDEO_TRANSPORT_STREAM.
     *
     * @return True if this player works with video.
     */
    boolean isVideoTransportStreamType();

    /**
     * Convenience method to see if the player type is PLAYER_VIDEO_WEB.
     *
     * @return True if this player works with video from the web.
     */
    boolean isVideoWebType();

    /**
     * Play the supplied URL (or URLs).
     *
     * @param urls One or more URL based source of media data.
     */
    void play(String ... urls);

    /**
     * Play the supplied URL at the given bookmark.
     *
     * @param url A URL based source of media data.
     * @param b A bookmark to start.
     */
    void play(String url, Bookmark b);

    /**
     * A player knows if it is currently playing.  Being paused still means
     * it is playing though.  After "play" is called only "stop" makes this
     * false.
     *
     * @return True is the player is playing.
     */
    boolean isPlaying();

    /**
     * Stop playing the media.  And return control back to the caller.
     */
    void stop();

    /**
     * Seek to a position from the current position a value of N seconds.
     * A value of less than zero will skip back.
     *
     * @param seconds The number of seconds to seek.
     */
    void seek(int seconds);

    /**
     * Seek to a position from the start of the media N seconds.
     *
     * @param seconds The number of seconds to position.
     */
    void seekPosition(int seconds);

    /**
     * Seek to a position that is a percentage of the length of media.
     *
     * @param percentage The percentage of the media to position.
     */
    void seekPosition(double percentage);

    /**
     * A player could control a set of files and this methods says to go
     * to the next one in it's list.  It there isn't a next then
     * it does nothing.
     */
    void next();

    /**
     * A player could control a set of files and this methods says to go
     * to the previous one in it's list.  It there isn't a previous then
     * it does nothing.
     */
    void previous();

    /**
     * A Player may respond to a "guide" command.  This is of course optional
     * if a player does not support any sort of guide.
     */
    void guide();

    /**
     * A Player may respond to an "up" command.  This is of course optional
     * if a player does not support any sort of navigation.
     */
    void up();

    /**
     * A Player may respond to a "down" command.  This is of course optional
     * if a player does not support any sort of navigation.
     */
    void down();

    /**
     * A Player may respond to a "left" command.  This is of course optional
     * if a player does not support any sort of navigation.
     */
    void left();

    /**
     * A Player may respond to a "right" command.  This is of course optional
     * if a player does not support any sort of navigation.
     */
    void right();

    /**
     * A Player may respond to a "enter" command.  This is of course optional
     * if a player does not support any sort of navigation.
     */
    void enter();

    /**
     * Change the current audiosync value by an offset value.  The player
     * maintains the current setting (0.0 as a default) and will use it to
     * adjust by the given offset.
     *
     * @param offset Audio sync change value.
     */
    void audiosync(double offset);

    /**
     * Some Players may not be able to seek.  Try to ever make an iPod seek?
     *
     * @return True if the Player is capable to seek.
     */
    boolean supportsSeek();

    /**
     * Pause or unpause the Player.
     *
     * @param b If true then pause, otherwise unpause.
     */
    void pause(boolean b);

    /**
     * Some Players may not be able to pause.
     *
     * @return True if the Player is capable of pausing.
     */
    boolean supportsPause();

    /**
     * A player knows if it is paused currently.
     *
     * @return True is the player is paused.
     */
    boolean isPaused();

    /**
     * If the player naturally finished playing without user interaction
     * we set the Completed property to true.
     *
     * @return True When finished nicely.
     */
    boolean isCompleted();

    /**
     * If the player naturally finished playing without user interaction
     * we set the Completed property to true.
     *
     * @param b True When finished nicely.
     */
    void setCompleted(boolean b);

    /**
     * The player might be able to auto skip (over commercials perhaps) and
     * by setting this property the player will do the skipping without
     * any other input from the user.
     *
     * @return True is auto skip is desired.
     */
    boolean isAutoSkip();

    /**
     * The player might be able to auto skip (over commercials perhaps) and
     * by setting this property the player will do the skipping without
     * any other input from the user.
     *
     * @param b True is auto skip is desired.
     */
    void setAutoSkip(boolean b);

    /**
     * Some players might be able to auto skip over parts of the video.
     * This of course usually means the skipping of commercials and this
     * can have the commercials skipped by the player without any interaction
     * from the user.
     *
     * @return True if auto skip is supported.
     */
    boolean supportsAutoSkip();

    /**
     * Find out the current state of the player.
     *
     * @return A PlayState instance.
     */
    PlayState getPlayState();

    /**
     * The Player can have a defined space where it is to play it's content.
     * If this is not set then the player should display in "fullscreen".
     *
     * @return A defined Rectangle space.
     */
    Rectangle getRectangle();

    /**
     * The Player can have a defined space where it is to play it's content.
     * If this is not set then the player should display in "fullscreen".
     *
     * @param r A defined Rectangle space.
     */
    void setRectangle(Rectangle r);

    /**
     * When playing transport streams, a player can have an issue with
     * the actual length of a video.  We have a property that the Player
     * can be given as a hint to the length of the video in seconds.
     *
     * @return The video length hint value.
     */
    long getLengthHint();

    /**
     * When playing transport streams, a player can have an issue with
     * the actual length of a video.  We have a property that the Player
     * can be given as a hint to the length of the video in seconds.
     *
     * @param l The video length hint value.
     */
    void setLengthHint(long l);

    /**
     * Players can use their parents Frame instance to aid in making their
     * own Frame or Window.
     *
     * @return A Frame instance.
     */
    Frame getFrame();

    /**
     * Players can use their parents Frame instance to aid in making their
     * own Frame or Window.
     *
     * @param f A Frame instance.
     */
    void setFrame(Frame f);

    /**
     * The idea is that Players can display their content on any sort of
     * component but we want them to lay it out onto a JLayeredPane.  We
     * then may want to get that layered pane to display some "popup"
     * information.  We realize that a Player may NOT have control of
     * laying out of the screen component so it is quite legal to return
     * a null value here if the Player does not actually use the layered pane.
     *
     * @return A JLayeredPane instance if it exists.
     */
    JLayeredPane getLayeredPane();

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


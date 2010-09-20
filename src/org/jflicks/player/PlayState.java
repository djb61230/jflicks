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

/**
 * This class is a base implementation of the Player interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class PlayState {

    private boolean playing;
    private boolean paused;
    private double time;
    private long position;

    /**
     * Simple empty constructor.
     */
    public PlayState() {
    }

    /**
     * The Player could be playing as opposed to stopped.
     *
     * @return A boolean primitive.
     */
    public boolean isPlaying() {
        return (playing);
    }

    /**
     * The Player could be playing as opposed to stopped.
     *
     * @param b A boolean primitive.
     */
    public void setPlaying(boolean b) {
        playing = b;
    }

    /**
     * The Player could be paused.
     *
     * @return A boolean primitive.
     */
    public boolean isPaused() {
        return (paused);
    }

    /**
     * The Player could be paused.
     *
     * @param b A boolean primitive.
     */
    public void setPaused(boolean b) {
        paused = b;
    }

    /**
     * The notion of the current time in the video.  Please note that this
     * may not mean the beginning is zero.
     *
     * @return The time as a double and in seconds.
     */
    public double getTime() {
        return (time);
    }

    /**
     * The notion of the current time in the video.  Please note that this
     * may not mean the beginning is zero.
     *
     * @param d The time as a double and in seconds.
     */
    public void setTime(double d) {
        time = d;
    }

    /**
     * A position that defines the location in a video where a bookmark
     * resides.  This is usually the byte offset into a file.
     *
     * @return A long that defines a position.
     */
    public long getPosition() {
        return (position);
    }

    /**
     * A position that defines the location in a video where a bookmark
     * resides.  This is usually the byte offset into a file.
     *
     * @param l A long that defines a position.
     */
    public void setPosition(long l) {
        position = l;
    }

}


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
package org.jflicks.stb;

/**
 * This interface defines the methods that allow for the interaction
 * between set top boxes and our code.  Right now we are just concerned
 * with changing the channel.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface STB {

    /**
     * The STB interface needs a title property.
     */
    String TITLE_PROPERTY = "STB-Title";

    /**
     * The STB supplies a unique name.
     *
     * @return The title as a string.
     */
    String getTitle();

    /**
     * Change the channel given a channel as a String.
     *
     * @param s A given channel as a String.
     */
    void changeChannel(String s);
}


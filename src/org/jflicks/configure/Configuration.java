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
package org.jflicks.configure;

/**
 * This interface encapsulates the notion of a Configuration.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Configuration extends Comparable<Configuration> {

    /**
     * The name of the configuration.
     *
     * @return The name.
     */
    String getName();

    /**
     * The source of the configuration.
     *
     * @return The source.
     */
    String getSource();

    /**
     * The summary of the current state of the configuration.
     *
     * @return The summary.
     */
    String getSummary();

    /**
     * The set of NameValue instances that define this configuration.
     *
     * @return An array of NameValue instances.
     */
    NameValue[] getNameValues();

    /**
     * Convenience method to locate a NameValue given the name.
     *
     * @param s A given name string.
     * @return A NameValue instance if found.
     */
    NameValue findNameValueByName(String s);

    /**
     * Convenience method to see if a given name String matches.
     *
     * @param s A given name string.
     * @return True if the name matches.
     */
    boolean isName(String s);

    /**
     * Convenience method to see if a given source String matches.
     *
     * @param s A given source string.
     * @return True if the source matches.
     */
    boolean isSource(String s);
}


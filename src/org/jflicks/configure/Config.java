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
 * This interface gives objects a way to support configuration.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public interface Config {

    /**
     * The property name to load which has the default configuration.
     *
     * @return A String path.
     */
    String getPropertiesName();

    /**
     * The default Configuration of this class that will due in case of no
     * user customization of the configuration.
     *
     * @return The Configuration.
     */
    Configuration getDefaultConfiguration();

    /**
     * The Configuration of this class.
     *
     * @return The Configuration.
     */
    Configuration getConfiguration();

    /**
     * The Configuration of this class.
     *
     * @param c The Configuration.
     */
    void setConfiguration(Configuration c);
}


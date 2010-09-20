/*
    This file is part of ATM.

    ATM is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ATM is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ATM.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.jflicks.metadata.thetvdb;

import org.jflicks.metadata.BaseMetadata;
import org.jflicks.metadata.SearchPanel;

/**
 * A Metadata implementation to access thetvdb.com.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class TheTVDBMetadata extends BaseMetadata {

    /**
     * Simple constructor.
     */
    public TheTVDBMetadata() {

        setTitle("The TV DB Metadata");
        setTVData(true);
    }

    /**
     * {@inheritDoc}
     */
    public SearchPanel getSearchPanel() {
        return (new TheTVDBSearch());
    }

}


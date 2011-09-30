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
package org.jflicks.transfer;

import org.jflicks.log.BaseLog;
import org.jflicks.tv.Recording;

/**
 * This class is a base implementation of the Transfer interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseTransfer extends BaseLog implements Transfer {

    private String title;
    private Recording recording;

    /**
     * Simple empty constructor.
     */
    public BaseTransfer() {
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return (title);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given title value.
     */
    public void setTitle(String s) {
        title = s;
    }

    public Recording getRecording() {
        return (recording);
    }

    public void setRecording(Recording r) {
        recording = r;
    }

}

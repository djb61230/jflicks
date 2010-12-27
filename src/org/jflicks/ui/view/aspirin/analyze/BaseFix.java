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
package org.jflicks.ui.view.aspirin.analyze;

import org.jflicks.job.AbstractJob;

/**
 * This class is a base implementation of the Fix interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseFix extends AbstractJob implements Fix {

    private boolean successful;

    /**
     * Simple empty constructor.
     */
    public BaseFix() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSuccessful() {
        return (successful);
    }

    /**
     * Convenience method to set this property.
     *
     * @param b True if success has been achieved.
     */
    public void setSuccessful(boolean b) {
        successful = b;
    }

}

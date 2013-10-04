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
package org.jflicks.tv.ondemand.appletv;

import org.jflicks.tv.ondemand.BaseOnDemand;
import org.jflicks.tv.ondemand.StreamSession;

/**
 * AppleTV is a set-top box that can access Netflix and other online video
 * sources.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class AppleTVOnDemand extends BaseOnDemand {

    /**
     * Simple empty constructor.
     */
    public AppleTVOnDemand() {

        setTitle("Apple TV");
    }

    /**
     * {@inheritDoc}
     */
    public void command(StreamSession ss, int type) {

        System.out.println("AppleTVOnDemand: command : " + type);
    }

}


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
package org.jflicks.restlet.nms;

import org.jflicks.restlet.BaseApplication;

import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * This class is an implementation of a restlet application.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class NMSApplication extends BaseApplication {

    /**
     * Simple empty constructor.
     */
    public NMSApplication() {
    }

    @Override
    public Restlet createInboundRoot() {

        Router router = new Router(getContext());

        router.attach("/nms/{version}/{lang}/recordings.{format}",
            RecordingResource.class);
        router.attach("/nms/{version}/{lang}/recordingrules.{format}",
            RecordingRuleResource.class);
        router.attach("/nms/{version}/{lang}/state.{format}",
            StateResource.class);
        router.attach("/nms/{version}/{lang}/channels.{format}",
            ChannelResource.class);
        router.attach("/nms/{version}/{lang}/tasks.{format}",
            TaskResource.class);
        router.attach("/nms/{version}/{lang}/upcomings.{format}",
            UpcomingResource.class);
        router.attach("/nms/{version}/{lang}/videos.{format}",
            VideoResource.class);

        return (router);
    }

}


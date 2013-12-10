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
import org.restlet.resource.ClientResource;
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

        setName("RESTful jflicks media system");
        setDescription("Access and control local jflicks server component.");
        setOwner("jflicks.org");
        setAuthor("Doug Barnum, copyright 2012");
        setAlias("nms");
    }

    @Override
    public Restlet createInboundRoot() {

        Router router = new Router(getContext());

        router.attach("/", RootResource.class);
        router.attach("/{version}/recordings.{format}",
            RecordingResource.class);
        router.attach("/{version}/recordings/{title}.{format}",
            RecordingByTitleResource.class);
        router.attach("/{version}/recording/titles.{format}",
            RecordingTitleResource.class);
        router.attach("/{version}/recording/{recordingId}/"
            + "{allowRerecord}", DeleteRecordingResource.class);
        router.attach("/{version}/recordingrules.{format}",
            RecordingRuleResource.class);
        router.attach("/{version}/recordingrule/{ruleId}",
            RecordingRuleResource.class);
        router.attach("/{version}/state.{format}", StateResource.class);
        router.attach("/{version}/search/{term}.{format}",
            SearchResource.class);
        router.attach("/{version}/channels.{format}",
            ChannelResource.class);
        router.attach("/{version}/schedule", ScheduleResource.class);
        router.attach("/{version}/tasks.{format}", TaskResource.class);
        router.attach("/{version}/upcomings.{format}",
            UpcomingResource.class);
        router.attach("/{version}/upcoming", UpcomingResource.class);
        router.attach("/{version}/videos.{format}", VideoResource.class);
        router.attach("/{version}/guide/{channelId}.{format}",
            GuideChannelResource.class);

        return (router);
    }

    public static void main(String[] args) throws Exception {

        ClientResource service = new ClientResource("http://localhost:8182/");
        System.out.println(service.options().getText());
    }

}


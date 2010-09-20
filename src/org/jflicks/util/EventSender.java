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
package org.jflicks.util;

import java.util.Hashtable;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A utility class that allows users to send events easily.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class EventSender extends AbstractJob {

    /**
     * Our message topic definition name.
     */
    public static final String MESSAGE_TOPIC_NAME = "message";

    /**
     * Our message topic definition path.
     */
    public static final String MESSAGE_TOPIC_PATH =
        "org/jflicks/MESSAGE";

    private ServiceTracker eventServiceTracker;
    private static EventSender instance;

    /**
     * Acquire an instance of EventSender.
     *
     * @param bc The required BundleContext argument.
     * @return An EventSender instance.
     */
    public static synchronized EventSender getInstance(BundleContext bc) {

        if (instance == null) {

            instance = new EventSender(bc);
            JobContainer jc = JobManager.getJobContainer(instance);
            jc.start();
        }

        return (instance);
    }

    private EventSender(BundleContext bc) {

        setSleepTime(30000);
        ServiceTracker st =
            new ServiceTracker(bc, EventAdmin.class.getName(), null);
        setEventServiceTracker(st);
    }

    private ServiceTracker getEventServiceTracker() {
        return (eventServiceTracker);
    }

    private void setEventServiceTracker(ServiceTracker est) {
        eventServiceTracker = est;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        ServiceTracker st = getEventServiceTracker();
        if (st != null) {
            st.open();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());
        }

    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        ServiceTracker st = getEventServiceTracker();
        if (st != null) {
            st.close();
        }

        setTerminate(true);
    }

    /**
     * Allow an easy way for users to send a text message.
     *
     * @param s A given text message to send.
     */
    public void sendMessage(String s) {

        ServiceTracker st = getEventServiceTracker();
        if ((st != null) && (s != null)) {

            EventAdmin ea = (EventAdmin) st.getService();
            if (ea != null) {

                Hashtable<String, String> props =
                    new Hashtable<String, String>();
                props.put(MESSAGE_TOPIC_NAME, s);
                Event evt = new Event(MESSAGE_TOPIC_PATH, props);
                ea.postEvent(evt);
            }
        }
    }

}

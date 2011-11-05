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
package org.jflicks.stream.http;

import org.jflicks.util.BaseActivator;

import org.osgi.framework.BundleContext;

/**
 * Simple activater that uses the OSGi http service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private HttpServiceTracker httpServiceTracker;
    private HttpServiceTracker rokuFeedHttpServiceTracker;
    private HttpServiceTracker gtvFeedHttpServiceTracker;
    private NMSTracker rokuFeedNMSTracker;
    private NMSTracker gtvFeedNMSTracker;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);
        HttpStream hs = new HttpStream();
        hs.setBundleContext(bc);

        HttpServiceTracker tracker = new HttpServiceTracker(bc, hs);
        setHttpServiceTracker(tracker);
        tracker.open();

        RokuFeed rf = new RokuFeed();
        rf.setBundleContext(bc);
        tracker = new HttpServiceTracker(bc, rf);
        setRokuFeedHttpServiceTracker(tracker);
        tracker.open();

        NMSTracker ntracker = new NMSTracker(bc, rf);
        setRokuFeedNMSTracker(ntracker);
        ntracker.open();

        GtvFeed gtv = new GtvFeed();
        gtv.setBundleContext(bc);
        tracker = new HttpServiceTracker(bc, gtv);
        setGtvFeedHttpServiceTracker(tracker);
        tracker.open();

        ntracker = new NMSTracker(bc, gtv);
        setGtvFeedNMSTracker(ntracker);
        ntracker.open();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        HttpServiceTracker tracker = getHttpServiceTracker();
        if (tracker != null) {
            tracker.close();
        }

        tracker = getRokuFeedHttpServiceTracker();
        if (tracker != null) {
            tracker.close();
        }

        tracker = getGtvFeedHttpServiceTracker();
        if (tracker != null) {
            tracker.close();
        }
    }

    private HttpServiceTracker getHttpServiceTracker() {
        return (httpServiceTracker);
    }

    private void setHttpServiceTracker(HttpServiceTracker t) {
        httpServiceTracker = t;
    }

    private HttpServiceTracker getRokuFeedHttpServiceTracker() {
        return (rokuFeedHttpServiceTracker);
    }

    private void setRokuFeedHttpServiceTracker(HttpServiceTracker t) {
        rokuFeedHttpServiceTracker = t;
    }

    private HttpServiceTracker getGtvFeedHttpServiceTracker() {
        return (gtvFeedHttpServiceTracker);
    }

    private void setGtvFeedHttpServiceTracker(HttpServiceTracker t) {
        gtvFeedHttpServiceTracker = t;
    }

    private NMSTracker getRokuFeedNMSTracker() {
        return (rokuFeedNMSTracker);
    }

    private void setRokuFeedNMSTracker(NMSTracker t) {
        rokuFeedNMSTracker = t;
    }

    private NMSTracker getGtvFeedNMSTracker() {
        return (gtvFeedNMSTracker);
    }

    private void setGtvFeedNMSTracker(NMSTracker t) {
        gtvFeedNMSTracker = t;
    }

}

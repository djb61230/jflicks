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
package org.jflicks.trailer.apple;

import java.util.Hashtable;

import org.jflicks.db.Db4oServiceTracker;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.trailer.Trailer;
import org.jflicks.util.BaseActivator;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simple activator for the Apple Trailer service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private Db4oServiceTracker db4oServiceTracker;
    private AppleTrailer appleTrailer;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        AppleTrailer at = new AppleTrailer();
        setAppleTrailer(at);

        Db4oServiceTracker t = new Db4oServiceTracker(bc, at);
        setDb4oServiceTracker(t);
        t.open();

        AppleTrailerJob job = new AppleTrailerJob(at);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(Trailer.TITLE_PROPERTY, at.getTitle());

        bc.registerService(Trailer.class.getName(), at, dict);
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        AppleTrailer at = getAppleTrailer();
        if (at != null) {
            at.close();
        }

        Db4oServiceTracker t = getDb4oServiceTracker();
        if (t != null) {
            t.close();
        }

        JobContainer jc = getJobContainer();
        if (jc != null) {
            jc.stop();
        }
    }

    private AppleTrailer getAppleTrailer() {
        return (appleTrailer);
    }

    private void setAppleTrailer(AppleTrailer at) {
        appleTrailer = at;
    }

    private Db4oServiceTracker getDb4oServiceTracker() {
        return (db4oServiceTracker);
    }

    private void setDb4oServiceTracker(Db4oServiceTracker t) {
        db4oServiceTracker = t;
    }

}

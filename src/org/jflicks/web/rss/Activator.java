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
package org.jflicks.web.rss;

import java.util.Hashtable;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.util.BaseActivator;
import org.jflicks.web.Web;

import org.osgi.framework.BundleContext;

/**
 * Simple activator to start the RSS source.  Runs a job that accesses RSS
 * news feeds.
 *
 * @author Doug Barnum
 */
public class Activator extends BaseActivator {

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        RSS rss = new RSS();
        RSSJob job = new RSSJob(rss);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(Web.TITLE_PROPERTY, rss.getTitle());

        bc.registerService(Web.class.getName(), rss, dict);
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        JobContainer jc = getJobContainer();
        if (jc != null) {
            jc.stop();
        }
    }

}

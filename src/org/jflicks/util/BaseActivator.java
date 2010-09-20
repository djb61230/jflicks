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

import org.jflicks.job.JobContainer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * A base Activator where we can put some common code.  These tend to be
 * pretty cookie cutter, so far we notice we tend to start/stop a job in
 * the activator methods.  So having a JobContainer property is handy
 * and reduces code.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseActivator implements BundleActivator {

    private BundleContext bundleContext;
    private JobContainer jobContainer;

    /**
     * We need to keep around a BundleContext when we stop, or we usually
     * do.
     *
     * @return The BundleContext we were started with as a parameter.
     */
    public BundleContext getBundleContext() {
        return (bundleContext);
    }

    /**
     * We need to keep around a BundleContext when we stop, or we usually
     * do.
     *
     * @param bc The BundleContext we were started with as a parameter.
     */
    public void setBundleContext(BundleContext bc) {
        bundleContext = bc;
    }

    /**
     * We usually start our service off in a Job so we need the JobContainer
     * for when we are asked to stop.
     *
     * @return A JobContainer we started.
     */
    public JobContainer getJobContainer() {
        return (jobContainer);
    }

    /**
     * We usually start our service off in a Job so we need the JobContainer
     * for when we are asked to stop.
     *
     * @param j A JobContainer we started.
     */
    public void setJobContainer(JobContainer j) {
        jobContainer = j;
    }

}

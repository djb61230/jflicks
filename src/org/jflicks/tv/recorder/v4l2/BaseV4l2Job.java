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
package org.jflicks.tv.recorder.v4l2;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobListener;
import org.jflicks.job.SystemJob;

/**
 * An abstract base class supporting V4l2 devices by using the command
 * line program v4l2-ctl.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseV4l2Job extends AbstractJob implements JobListener {

    private SystemJob systemJob;
    private JobContainer jobContainer;
    private String device;

    /**
     * Simple no argument constructor.
     */
    public BaseV4l2Job() {
    }

    protected SystemJob getSystemJob() {
        return (systemJob);
    }

    protected void setSystemJob(SystemJob j) {
        systemJob = j;
    }

    protected JobContainer getJobContainer() {
        return (jobContainer);
    }

    protected void setJobContainer(JobContainer j) {
        jobContainer = j;
    }

    /**
     * The V4l2 type of recorders user a device path like /dev/videoN
     * where N is a number.  We keep this path in the device property.
     *
     * @return A device path.
     */
    public String getDevice() {
        return (device);
    }

    /**
     * The V4l2 type of recorders user a device path like /dev/videoN
     * where N is a number.  We keep this path in the device property.
     *
     * @param s A device path.
     */
    public void setDevice(String s) {
        device = s;
    }

}

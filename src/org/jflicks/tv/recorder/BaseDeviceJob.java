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
package org.jflicks.tv.recorder;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobListener;
import org.jflicks.job.SystemJob;

/**
 * An abstract base class supporting Device devices by using the command
 * line program v4l2-ctl.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseDeviceJob extends AbstractJob implements JobListener {

    private PropertyChangeSupport propertyChangeSupport;

    private SystemJob systemJob;
    private JobContainer jobContainer;
    private String device;

    /**
     * Simple no argument constructor.
     */
    public BaseDeviceJob() {

        setPropertyChangeSupport(new PropertyChangeSupport(this));
    }

    private PropertyChangeSupport getPropertyChangeSupport() {
        return (propertyChangeSupport);
    }

    private void setPropertyChangeSupport(PropertyChangeSupport pcs) {
        propertyChangeSupport = pcs;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {

        PropertyChangeSupport pcs = getPropertyChangeSupport();
        if (pcs != null) {

            pcs.addPropertyChangeListener(l);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {

        PropertyChangeSupport pcs = getPropertyChangeSupport();
        if (pcs != null) {

            pcs.removePropertyChangeListener(l);
        }
    }

    public void firePropertyChange(String s, Object oldValue,
        Object newValue) {

        PropertyChangeSupport pcs = getPropertyChangeSupport();
        if ((pcs != null) && (s != null)) {

            pcs.firePropertyChange(s, oldValue, newValue);
        }
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
     * The Device type of recorders user a device path like /dev/videoN
     * where N is a number.  We keep this path in the device property.
     *
     * @return A device path.
     */
    public String getDevice() {
        return (device);
    }

    /**
     * The Device type of recorders user a device path like /dev/videoN
     * where N is a number.  We keep this path in the device property.
     *
     * @param s A device path.
     */
    public void setDevice(String s) {
        device = s;
    }

}

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
package org.jflicks.tv.recorder.jhdhr;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobListener;
import org.jflicks.job.SystemJob;

/**
 * Simple base class to contain properties that extensions will need.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseHDHRJob extends AbstractJob implements JobListener {

    private SystemJob systemJob;
    private JobContainer jobContainer;
    private String id;
    private int tuner;
    private String frequencyType;

    /**
     * Simple no argument constructor.
     */
    public BaseHDHRJob() {
    }

    /**
     * The ID in this case is the ID of the HDHR on the network.
     *
     * @return The ID of the desired HDHR to set the program.
     */
    public String getId() {
        return (id);
    }

    /**
     * The ID in this case is the ID of the HDHR on the network.
     *
     * @param s The ID of the desired HDHR to set the program.
     */
    public void setId(String s) {
        id = s;
    }

    /**
     * Most HDHR devices have more than one channel.  They are numbered
     * starting at zero.
     *
     * @return The tuner number.
     */
    public int getTuner() {
        return (tuner);
    }

    /**
     * Most HDHR devices have more than one channel.  They are numbered
     * starting at zero.
     *
     * @param i The tuner number.
     */
    public void setTuner(int i) {
        tuner = i;
    }

    /**
     * Jobs may need the FrequencyType properties configured for an
     * HDHomerun Recorder.
     *
     * @return The FrequencyType as a String.
     */
    public String getFrequencyType() {
        return (frequencyType);
    }

    /**
     * Jobs may need the FrequencyType properties configured for an
     * HDHomerun Recorder.
     *
     * @param s The FrequencyType as a String.
     */
    public void setFrequencyType(String s) {
        frequencyType = s;
    }

    /**
     * An instance of SystemJob is used to run a command line program.
     *
     * @return A SystemJob instance.
     */
    protected SystemJob getSystemJob() {
        return (systemJob);
    }

    /**
     * An instance of SystemJob is used to run a command line program.
     *
     * @param j A SystemJob instance.
     */
    protected void setSystemJob(SystemJob j) {
        systemJob = j;
    }

    /**
     * An instance of JobContainer is needed to run a SystemJob.
     *
     * @return A JobContainer instance.
     */
    protected JobContainer getJobContainer() {
        return (jobContainer);
    }

    /**
     * An instance of JobContainer is needed to run a SystemJob.
     *
     * @param j A JobContainer instance.
     */
    protected void setJobContainer(JobContainer j) {
        jobContainer = j;
    }

}

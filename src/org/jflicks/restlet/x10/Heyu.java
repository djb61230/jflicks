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
package org.jflicks.restlet.x10;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;

import org.apache.commons.io.FileUtils;

/**
 * Run a series of commands, assumed to be Heyu commands.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class Heyu implements JobListener {

    private int currentJobIndex = 0;
    private String[] commands;
    private SystemJob[] systemJobs = null;
    private JobContainer jobContainer = null;

    /**
     * Default empty constructor.
     */
    public Heyu(String[] array) {

        setCommands(array);
    }

    public String[] getCommands() {
        return (commands);
    }

    private void setCommands(String[] array) {
        commands = array;
    }

    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            currentJobIndex++;
            if (currentJobIndex < systemJobs.length) {

                System.out.println("Working on command "
                    + (currentJobIndex + 1)
                    + " of "
                    + systemJobs.length);
                System.out.println("Starting <"
                    + systemJobs[currentJobIndex].getCommand() + ">");
                jobContainer =
                    JobManager.getJobContainer(systemJobs[currentJobIndex]);
                systemJobs[currentJobIndex].addJobListener(this);
                jobContainer.start();

            } else {

                System.out.println("Done!");
            }

        } else if (event.getType() == JobEvent.UPDATE) {
            System.out.println(event.getMessage());
        }
    }

    public void process() {

        String[] array = getCommands();
        if ((array != null) && (array.length > 0)) {

            System.out.println("Doing " + array.length + " commands.");
            systemJobs = new SystemJob[array.length];
            for (int i = 0; i < array.length; i++) {

                systemJobs[i] = SystemJob.getInstance(array[i]);
            }

            // Now we start the first job.
            System.out.println("Starting <"
                + systemJobs[0].getCommand() + ">");
            jobContainer = JobManager.getJobContainer(systemJobs[0]);
            systemJobs[0].addJobListener(this);
            jobContainer.start();
        }
    }

}


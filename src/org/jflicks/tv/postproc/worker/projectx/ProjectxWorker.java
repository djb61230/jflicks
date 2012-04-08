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
package org.jflicks.tv.postproc.worker.projectx;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.tv.Recording;
import org.jflicks.tv.postproc.RecordingLengthJob;
import org.jflicks.tv.postproc.worker.BaseWorker;
import org.jflicks.tv.postproc.worker.WorkerEvent;

/**
 * A generic indexer that just runs a command line program.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ProjectxWorker extends BaseWorker implements JobListener {

    /**
     * Simple default constructor.
     */
    public ProjectxWorker() {

        setTitle("ProjectxWorker");
        setDescription("Fast mkv file with same quality as source.");
        setHeavy(false);
        setDefaultRun(false);
        setUserSelectable(false);
        setIndexer(true);
    }

    /**
     * {@inheritDoc}
     */
    public void work(Recording r) {

        if (r != null) {

            ProjectxJob job = new ProjectxJob(r, this);
            job.addJobListener(this);
            JobContainer jc = JobManager.getJobContainer(job);
            addJobContainer(jc);
            jc.start();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            log(INFO, "ProjectxWorker: completed");
            ProjectxJob job = (ProjectxJob) event.getSource();
            removeJobContainer(job);

            Recording r = job.getRecording();
            long seconds = RecordingLengthJob.getRecordingLength(r);
            if (seconds != 0L) {
                r.setDuration(seconds);
            }

            fireWorkerEvent(WorkerEvent.COMPLETE, r, true);

        } else {

            //log(DEBUG, "ProjectxWorker: " + event.getMessage());
        }
    }

}


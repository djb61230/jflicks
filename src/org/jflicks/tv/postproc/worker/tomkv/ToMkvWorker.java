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
package org.jflicks.tv.postproc.worker.tomkv;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.tv.Recording;
import org.jflicks.tv.postproc.worker.BaseWorker;
import org.jflicks.tv.postproc.worker.WorkerEvent;

/**
 * Worker implementation that can convert a transport stream mpg file to
 * an mkv indexed file.  This should improve playback especially for
 * seeking.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ToMkvWorker extends BaseWorker implements JobListener {

    /**
     * Simple default constructor.
     */
    public ToMkvWorker() {

        setTitle("ToMkvWorker");
        setDescription("Create an mkv file from an TS mpg file.");
        setHeavy(false);
        setDefaultRun(false);
        setUserSelectable(false);
    }

    /**
     * {@inheritDoc}
     */
    public void work(Recording r) {

        if (r != null) {

            ToMkvJob job = new ToMkvJob(r);
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

            System.out.println("ToMkvWorker: completed");
            ToMkvJob job = (ToMkvJob) event.getSource();
            removeJobContainer(job);
            fireWorkerEvent(WorkerEvent.COMPLETE, job.getRecording(), true);

        } else {

            //System.out.println("ToMkvWorker: "
            //    + event.getMessage());
        }
    }

}


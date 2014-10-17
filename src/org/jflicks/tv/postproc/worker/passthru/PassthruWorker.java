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
package org.jflicks.tv.postproc.worker.passthru;

import java.io.File;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.tv.Recording;
import org.jflicks.tv.postproc.RecordingLengthJob;
import org.jflicks.tv.postproc.worker.BaseWorker;
import org.jflicks.tv.postproc.worker.ConcatJob;
import org.jflicks.tv.postproc.worker.WorkerEvent;

/**
 * A generic indexer that just runs a command line program.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class PassthruWorker extends BaseWorker implements JobListener {

    private ConcatJob concatJob;
    private Recording recording;

    /**
     * Simple default constructor.
     */
    public PassthruWorker() {

        setTitle("Mp4PassthruWorker");
        setDescription("MP4 that preserves digial audio passthru.");
        setHeavy(false);
        setDefaultRun(false);
        setUserSelectable(false);
        setIndexer(true);
    }

    private ConcatJob getConcatJob() {
        return (concatJob);
    }

    private void setConcatJob(ConcatJob j) {
        concatJob = j;
    }

    private Recording getRecording() {
        return (recording);
    }

    private void setRecording(Recording r) {
        recording = r;
    }

    /**
     * {@inheritDoc}
     */
    public void work(Recording r) {

        if (r != null) {

            setRecording(r);

            // If the recording was created via HLS, then we first
            // have to concat all the streams together.  We can know
            // if this is needed because the Recording path will not
            // currently exist.  However if it does exist, then we
            // just move along and do our indexing.
            File f = new File(r.getPath());
            if (f.exists()) {

                PassthruJob job = new PassthruJob(r, this);
                job.addJobListener(this);
                JobContainer jc = JobManager.getJobContainer(job);
                addJobContainer(jc);
                jc.start();

            } else {

                // First we take all the HLS files and make one ts file.
                File dir = f.getParentFile();
                String prefix = f.getName();
                prefix = prefix.substring(0, prefix.lastIndexOf("."));
                ConcatJob job = new ConcatJob(prefix, dir);
                job.addJobListener(this);
                setConcatJob(job);
                JobContainer jc = JobManager.getJobContainer(job);
                jc.start();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            if (event.getSource() == getConcatJob()) {

                PassthruJob job = new PassthruJob(getRecording(), this);
                job.addJobListener(this);
                JobContainer jc = JobManager.getJobContainer(job);
                addJobContainer(jc);
                jc.start();

            } else {

                log(INFO, "PassthruWorker: completed");
                PassthruJob job = (PassthruJob) event.getSource();
                removeJobContainer(job);

                Recording r = job.getRecording();
                long seconds = RecordingLengthJob.getRecordingLength(r);
                if (seconds != 0L) {
                    r.setDuration(seconds);
                }

                fireWorkerEvent(WorkerEvent.COMPLETE, r, true);
            }

        } else {

            //log(DEBUG, "PassthruWorker: " + event.getMessage());
        }
    }

}


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
package org.jflicks.tv.postproc.worker.indexer;

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
public class IndexerWorker extends BaseWorker implements JobListener {

    private ConcatJob concatJob;
    private Recording recording;
    private String commandLine;
    private String extension;

    /**
     * Simple default constructor.
     */
    public IndexerWorker() {

        setTitle("IndexerWorker");
        setDescription("IndexerWorker");
        setHeavy(true);
        setDefaultRun(false);
        setUserSelectable(false);
        setIndexer(true);
    }

    public String getCommandLine() {
        return (commandLine);
    }

    public void setCommandLine(String s) {
        commandLine = s;
    }

    public String getExtension() {
        return (extension);
    }

    public void setExtension(String s) {
        extension = s;
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

                IndexerJob job = new IndexerJob(r, this,
                    getCommandLine(), getExtension());
                job.addJobListener(this);
                JobContainer jc = JobManager.getJobContainer(job);
                addJobContainer(jc);
                jc.start();

            } else {

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

                IndexerJob job = new IndexerJob(getRecording(), this,
                    getCommandLine(), getExtension());
                job.addJobListener(this);
                JobContainer jc = JobManager.getJobContainer(job);
                addJobContainer(jc);
                jc.start();

            } else {

                log(INFO, "IndexerWorker: completed");
                IndexerJob job = (IndexerJob) event.getSource();
                removeJobContainer(job);

                Recording r = job.getRecording();
                long seconds = RecordingLengthJob.getRecordingLength(r);
                if (seconds != 0L) {
                    r.setDuration(seconds);
                }

                fireWorkerEvent(WorkerEvent.COMPLETE, r, true);
            }

        } else {

            //log(DEBUG, "IndexerWorker: " + event.getMessage());
        }
    }


}


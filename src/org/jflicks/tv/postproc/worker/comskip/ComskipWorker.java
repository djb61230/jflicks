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
package org.jflicks.tv.postproc.worker.comskip;

import java.util.ArrayList;

import org.jflicks.job.JobContainer;
import org.jflicks.job.Job;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.tv.Recording;
import org.jflicks.tv.postproc.worker.BaseWorker;
import org.jflicks.tv.postproc.worker.WorkerEvent;

/**
 * Worker implementation that can flag a Recording using the comskip
 * program.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ComskipWorker extends BaseWorker implements JobListener {

    private ArrayList<JobContainer> jobContainerList;

    /**
     * Simple default constructor.
     */
    public ComskipWorker() {

        setTitle("ComskipWorker");
        setDescription("Commercial flagging using Comskip");
        setHeavy(true);

        setJobContainerList(new ArrayList<JobContainer>());
    }

    private ArrayList<JobContainer> getJobContainerList() {
        return (jobContainerList);
    }

    private void setJobContainerList(ArrayList<JobContainer> l) {
        jobContainerList = l;
    }

    private void addJobContainer(JobContainer jc) {

        ArrayList<JobContainer> l = getJobContainerList();
        if ((jc != null) && (l != null)) {
            l.add(jc);
        }
    }

    private void removeJobContainer(Job j) {

        ArrayList<JobContainer> l = getJobContainerList();
        if ((j != null) && (l != null)) {

            int index = -1;
            for (int i = 0; i < l.size(); i++) {

                if (l.get(i).getJob() == j) {

                    index = i;
                    break;
                }
            }

            if (index != -1) {

                l.remove(index);
            }
        }
    }

    private void removeJobContainer(Recording r) {

        ArrayList<JobContainer> l = getJobContainerList();
        if ((r != null) && (l != null)) {

            int index = -1;
            for (int i = 0; i < l.size(); i++) {

                ComskipJob job = (ComskipJob) l.get(i).getJob();
                Recording tmp = job.getRecording();
                if (tmp.equals(r)) {

                    index = i;
                    break;
                }
            }

            if (index != -1) {

                JobContainer jc = l.get(index);
                jc.stop();
                l.remove(index);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void work(Recording r) {

        if (r != null) {

            ComskipJob job = new ComskipJob(r);
            job.addJobListener(this);
            JobContainer jc = JobManager.getJobContainer(job);
            addJobContainer(jc);
            jc.start();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void cancel(Recording r) {

        removeJobContainer(r);
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            System.out.println("ComskipWorker: completed");
            ComskipJob job = (ComskipJob) event.getSource();
            removeJobContainer(job);
            fireWorkerEvent(WorkerEvent.COMPLETE, job.getRecording(), true);

        } else {

            //System.out.println("ComskipWorker: " + event.getMessage());
        }
    }

}


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
package org.jflicks.tv.postproc.system;

import java.util.ArrayList;

import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Task;
import org.jflicks.tv.postproc.BasePostProc;
import org.jflicks.tv.postproc.worker.Worker;
import org.jflicks.tv.postproc.worker.WorkerEvent;

/**
 * Class that implements the PostProc service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SystemPostProc extends BasePostProc {

    private ArrayList<WorkerRecording> heavyWorkerRecordingList;
    private ArrayList<WorkerRecording> lightWorkerRecordingList;

    /**
     * Simple default constructor.
     */
    public SystemPostProc() {

        setTitle("System Post Proc");
        setHeavyWorkerRecordingList(new ArrayList<WorkerRecording>());
        setLightWorkerRecordingList(new ArrayList<WorkerRecording>());
    }

    private ArrayList<WorkerRecording> getHeavyWorkerRecordingList() {
        return (heavyWorkerRecordingList);
    }

    private void setHeavyWorkerRecordingList(ArrayList<WorkerRecording> l) {
        heavyWorkerRecordingList = l;
    }

    private void addHeavyWorkerRecording(WorkerRecording wr) {

        ArrayList<WorkerRecording> l = getHeavyWorkerRecordingList();
        if ((wr != null) && (l != null)) {

            synchronized (l) {

                l.add(wr);
            }
        }
    }

    /**
     * Perhaps after popping the heavy WorkerRecording, it may be determined
     * that it's not "ready" for work just yet.  This method allows it to be
     * placed back at the front of the queue, to wait some time before
     * trying again.
     *
     * @param wr A given WorkerRecording to be at the front of the queue.
     */
    public void pushHeavyWorkerRecording(WorkerRecording wr) {

        ArrayList<WorkerRecording> l = getHeavyWorkerRecordingList();
        if ((wr != null) && (l != null)) {

            synchronized (l) {

                l.add(0, wr);
            }
        }
    }

    /**
     * We have a queue of heavy WorkerRecording instances that need to be done.
     * This method will return the next one that should be done.
     *
     * @return A WorkerRecording instance.
     */
    public WorkerRecording popHeavyWorkerRecording() {

        WorkerRecording result = null;

        ArrayList<WorkerRecording> l = getHeavyWorkerRecordingList();
        if ((l != null) && (l.size() > 0)) {

            synchronized (l) {

                result = l.get(0);
                l.remove(0);
            }
        }

        return (result);
    }

    private ArrayList<WorkerRecording> getLightWorkerRecordingList() {
        return (lightWorkerRecordingList);
    }

    private void setLightWorkerRecordingList(ArrayList<WorkerRecording> l) {
        lightWorkerRecordingList = l;
    }

    private void addLightWorkerRecording(WorkerRecording wr) {

        ArrayList<WorkerRecording> l = getLightWorkerRecordingList();
        if ((wr != null) && (l != null)) {

            synchronized (l) {

                l.add(wr);
            }
        }
    }

    /**
     * Perhaps after popping the light WorkerRecording, it may be determined
     * that it's not "ready" for work just yet.  This method allows it to be
     * placed back at the front of the queue, to wait some time before trying
     * again.
     *
     * @param wr A given WorkerRecording to be at the front of the queue.
     */
    public void pushLightWorkerRecording(WorkerRecording wr) {

        ArrayList<WorkerRecording> l = getLightWorkerRecordingList();
        if ((wr != null) && (l != null)) {

            synchronized (l) {

                l.add(0, wr);
            }
        }
    }

    /**
     * We have a queue of light WorkerRecording instances that need to be done.
     * This method will return the next one that should be done.
     *
     * @return A WorkerRecording instance.
     */
    public WorkerRecording popLightWorkerRecording() {

        WorkerRecording result = null;

        ArrayList<WorkerRecording> l = getLightWorkerRecordingList();
        if ((l != null) && (l.size() > 0)) {

            synchronized (l) {

                result = l.get(0);
                l.remove(0);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void addProcessing(RecordingRule rr, Recording r) {

        if ((rr != null) && (r != null)) {

            Task[] array = rr.getTasks();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    if (array[i].isRun()) {

                        Worker w = getWorkerByTitle(array[i].getTitle());
                        if (w != null) {

                            System.out.println("Time to queue up a worker...");
                            WorkerRecording wr = new WorkerRecording();
                            wr.setWorker(w);
                            wr.setRecording(r);
                            if (w.isHeavy()) {
                                addHeavyWorkerRecording(wr);
                            } else {
                                addLightWorkerRecording(wr);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void workerUpdate(WorkerEvent event) {

        if (event.isUpdateRecording()) {

            Recording r = event.getRecording();
            if (r != null) {

                System.out.println("workerUpdate: updating Recording in db");
                updateRecording(r);
            }
        }
    }

}


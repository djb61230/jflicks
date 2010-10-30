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
package org.jflicks.tv.scheduler.system;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobManager;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Recording;
import org.jflicks.tv.recorder.Recorder;
import org.jflicks.tv.scheduler.PendingRecord;
import org.jflicks.tv.scheduler.RecordedShow;

/**
 * This job will run and queue recording jobs.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SystemSchedulerJob extends AbstractJob
    implements PropertyChangeListener {

    private SystemScheduler systemScheduler;
    private HashMap<Recorder, Recording> recordingHashMap;

    /**
     * This job supports the SystemScheduler plugin.
     *
     * @param s A given SystemScheduler instance.
     */
    public SystemSchedulerJob(SystemScheduler s) {

        setSystemScheduler(s);
        setRecordingHashMap(new HashMap<Recorder, Recording>());
        setSleepTime(15000);
    }

    private SystemScheduler getSystemScheduler() {
        return (systemScheduler);
    }

    private void setSystemScheduler(SystemScheduler s) {
        systemScheduler = s;
    }

    private HashMap<Recorder, Recording> getRecordingHashMap() {
        return (recordingHashMap);
    }

    private void setRecordingHashMap(HashMap<Recorder, Recording> m) {
        recordingHashMap = m;
    }

    private void addRecording(Recorder r, Recording rec) {

        HashMap<Recorder, Recording> m = getRecordingHashMap();
        if ((m != null) && (r != null) && (rec != null)) {

            m.put(r, rec);
        }

    }

    private void notifyClients(String message) {

        SystemScheduler ss = getSystemScheduler();
        if (ss != null) {

            NMS n = ss.getNMS();
            if (n != null) {

                n.sendMessage(message);
            }
        }
    }

    private Recording getRecording(Recorder r) {

        Recording result = null;

        HashMap<Recorder, Recording> m = getRecordingHashMap();
        if ((m != null) && (r != null)) {

            result = m.get(r);
        }

        return (result);
    }

    private void removeRecording(Recorder r) {

        HashMap<Recorder, Recording> m = getRecordingHashMap();
        if ((m != null) && (r != null)) {

            m.remove(r);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setTerminate(false);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        // Run the request first after a little sleep...
        JobManager.sleep(getSleepTime());
        SystemScheduler ss = getSystemScheduler();
        if (ss != null) {
            ss.requestRescheduling();
        }

        while (!isTerminate()) {

            // Get the recordings due to start in the next 60 seconds.
            PendingRecord[] array = ss.getReadyPendingRecords();
            if (array != null) {

                // We have recordings to launch.
                boolean done = false;
                int index = 0;
                int retries = 0;
                while (!done) {

                    long now = System.currentTimeMillis();
                    if (array[index].getStart() < now) {

                        // Ok, time to launch the recording...
                        Recorder recorder = array[index].getRecorder();
                        if (!recorder.isRecording()) {

                            Recording crec = array[index].getRecording();
                            addRecording(recorder, crec);
                            recorder.addPropertyChangeListener("Recording",
                                this);
                            System.out.println("recording on :"
                                + array[index].getChannel()
                                + " "
                                + new Date(now));
                            long dur = array[index].getDuration();
                            dur -= ((now - array[index].getStart()) / 1000);
                            System.out.println("adjust duration to: "
                                + dur);
                            crec.setRealStart(now);
                            recorder.startRecording(array[index].getChannel(),
                                dur, array[index].getFile(), false);
                            index++;
                            if (index == array.length) {
                                done = true;
                            }

                            notifyClients(NMSConstants.MESSAGE_RECORDING_ADDED);

                        } else if (retries < 15) {

                            System.out.println("hmm, recorder busy will retry");
                            JobManager.sleep(1000);
                            retries++;
                            if (recorder.isRecordingLiveTV()) {

                                recorder.stopRecording();
                            }

                        } else {

                            System.out.println("Recorder stayed busy. Give up");
                            index++;
                            if (index == array.length) {
                                done = true;
                            }
                        }

                    } else {

                        // Sleep 5 seconds.
                        JobManager.sleep(5000);
                    }
                }
            }

            JobManager.sleep(getSleepTime());

            // Check to see if we have any imports to work on.
            checkImports();
        }

    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
    }

    /**
     * We listen for when a Recorder has finished so we can update the
     * Recording instance to reflect that it is no longer recording.
     *
     * @param event A given PropertyChangeEvent object.
     */
    public void propertyChange(PropertyChangeEvent event) {

        Boolean bobj = (Boolean) event.getNewValue();
        if ((bobj != null) && (!bobj.booleanValue())) {

            // A Recorder finished!!
            System.out.println("Recorder: " + event.getSource() + " finished");
            Recorder r = (Recorder) event.getSource();
            SystemScheduler ss = getSystemScheduler();
            if ((r != null) && (ss != null)) {

                Recording rec = getRecording(r);
                if (rec != null) {

                    rec.setCurrentlyRecording(false);
                    long now = System.currentTimeMillis();
                    rec.setDuration((now - rec.getRealStart()) / 1000L);
                    System.out.println("Setting true duration: "
                        + rec.getDuration());
                    ss.updateRecording(rec);
                    removeRecording(r);
                    r.removePropertyChangeListener("Recording", this);
                }
            }
        }
    }

    private void checkImports() {

        SystemScheduler ss = getSystemScheduler();
        if (ss != null) {

            File home = new File(".");
            File importdir = new File(home, "import");
            if ((importdir.exists()) && (importdir.isDirectory())) {

                File recorded = new File(importdir, "recorded.txt");
                if ((recorded.exists()) && (recorded.isFile())) {

                    System.out.println("SystemScheduler: importing recorded");
                    try {

                        // We expect a programid one per line.
                        BufferedReader br =
                            new BufferedReader(new FileReader(recorded));
                        String line = null;
                        int count = 0;
                        while ((line = br.readLine()) != null) {

                            RecordedShow rs = new RecordedShow(line);
                            ss.addRecordedShow(rs);
                            count++;
                        }

                        br.close();
                        if (!recorded.delete()) {

                            System.out.println("checkImports: delete failed");
                        }

                        System.out.println("SystemScheduler: imported " + count
                            + " shows from recorded.txt");

                    } catch (IOException ex) {

                        System.out.println("checkImports: " + ex.getMessage());
                    }
                }
            }
        }
    }

}

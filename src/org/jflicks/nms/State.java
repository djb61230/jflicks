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
package org.jflicks.nms;

import java.io.File;
import java.util.ArrayList;

import org.jflicks.tv.Recording;
import org.jflicks.tv.recorder.Recorder;
import org.jflicks.tv.scheduler.Scheduler;

/**
 * This class represents the current state of a running NMS.  The class
 * also has the capability to be "merged" with other state objects so a
 * "set" of NMS instances can be represented.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class State {

    private ArrayList<String> videoCategoryList;
    private boolean supportsLiveTV;
    private boolean supportsOnDemand;
    private boolean availableRecordings;
    private long capacity;
    private long free;
    private int videoCount;
    private int recordingCount;
    private int recorderCount;
    private int recorderBusyCount;

    public State() {

        setVideoCategoryList(new ArrayList<String>());
    }

    public State(State s) {

        this();
        if (s != null) {

            setVideoCategories(s.getVideoCategories());
            setSupportsLiveTV(s.supportsLiveTV());
            setSupportsOnDemand(s.supportsOnDemand());
            setAvailableRecordings(s.hasAvailableRecordings());
            setCapacity(s.getCapacity());
            setFree(s.getFree());
            setVideoCount(s.getVideoCount());
            setRecordingCount(s.getRecordingCount());
            setRecorderCount(s.getRecorderCount());
            setRecorderBusyCount(s.getRecorderBusyCount());
        }
    }

    public State(NMS n) {

        this();
        if (n != null) {

            mergeVideoCategories(this, computeVideoCategories(n.getVideos()));
            setSupportsLiveTV(n.supportsLiveTV());
            setSupportsOnDemand(n.supportsOnDemand());
            Recording[] recs = n.getRecordings();
            setAvailableRecordings(recs != null);
            if (recs != null) {
                setRecordingCount(recs.length);
            }

            Video[] vids = n.getVideos();
            if (vids != null) {
                setVideoCount(vids.length);
            }

            Scheduler sched = n.getScheduler();
            if (sched != null) {

                String[] farray = sched.getConfiguredRecordingDirectories();
                if ((farray != null) && (farray.length > 0)) {

                    long total = 0L;
                    long avail = 0L;
                    for (int i = 0; i < farray.length; i++) {

                        File tfile = new File(farray[i]);
                        if ((tfile.exists()) && (tfile.isDirectory())) {


                            total += tfile.getTotalSpace();
                            avail += tfile.getUsableSpace();
                        }
                    }

                    setCapacity(total);
                    setFree(avail);
                }
            }

            Recorder[] rarray = n.getRecorders();
            if ((rarray != null) && (rarray.length > 0)) {

                setRecorderCount(rarray.length);
                int using = 0;
                for (int i = 0; i < rarray.length; i++) {

                    if (rarray[i].isRecording()) {
                        using++;
                    }
                }

                setRecorderBusyCount(using);
            }
        }
    }

    private ArrayList<String> getVideoCategoryList() {
        return (videoCategoryList);
    }

    private void setVideoCategoryList(ArrayList<String> l) {
        videoCategoryList = l;
    }

    public String[] getVideoCategories() {

        String[] result = null;

        ArrayList<String> l = getVideoCategoryList();

        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new String[l.size()]);
        }

        return (result);
    }

    private void setVideoCategories(String[] array) {

        ArrayList<String> l = getVideoCategoryList();
        if (l != null) {

            l.clear();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    if (!l.contains(array[i])) {
                        l.add(array[i]);
                    }
                }
            }
        }
    }

    public boolean hasAvailableRecordings() {
        return (availableRecordings);
    }

    private void setAvailableRecordings(boolean b) {
        availableRecordings = b;
    }

    public boolean supportsLiveTV() {
        return (supportsLiveTV);
    }

    private void setSupportsLiveTV(boolean b) {
        supportsLiveTV = b;
    }

    public boolean supportsOnDemand() {
        return (supportsLiveTV);
    }

    private void setSupportsOnDemand(boolean b) {
        supportsOnDemand = b;
    }

    private String[] computeVideoCategories(Video[] array) {

        String[] result = null;

        if ((array != null) && (array.length > 0)) {

            ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < array.length; i++) {

                String tmp = array[i].getCategory();
                if (tmp != null) {

                    tmp = tmp.trim();
                    if ((tmp.length() > 0) && (!list.contains(tmp))) {
                        list.add(tmp);
                    }
                }
            }

            if (list.size() > 0) {

                result = list.toArray(new String[list.size()]);
            }
        }

        return (result);
    }

    private void mergeVideoCategories(State s, String[] array) {

        if ((s != null) && (array != null)) {

            ArrayList<String> l = s.getVideoCategoryList();
            if (l != null) {

                for (int i = 0; i < array.length; i++) {

                    if (!l.contains(array[i])) {
                        l.add(array[i]);
                    }
                }
            }
        }
    }

    public State merge(State s) {

        State result = new State(this);

        if (s != null) {

            mergeVideoCategories(result, s.getVideoCategories());

            if (s.supportsLiveTV()) {
                result.setSupportsLiveTV(true);
            }

            if (s.supportsOnDemand()) {
                result.setSupportsOnDemand(true);
            }

            if (s.hasAvailableRecordings()) {
                result.setAvailableRecordings(true);
            }

            result.setCapacity(result.getCapacity() + s.getCapacity());
            result.setFree(result.getFree() + s.getFree());
            result.setVideoCount(result.getVideoCount() + s.getVideoCount());
            result.setRecordingCount(result.getRecordingCount()
                + s.getRecordingCount());
            result.setRecorderCount(result.getRecorderCount()
                + s.getRecorderCount());
            result.setRecorderBusyCount(result.getRecorderBusyCount()
                + s.getRecorderBusyCount());
        }

        return (result);
    }

    public long getCapacity() {
        return (capacity);
    }

    public void setCapacity(long l) {
        capacity = l;
    }

    public long getFree() {
        return (free);
    }

    public void setFree(long l) {
        free = l;
    }

    public int getVideoCount() {
        return (videoCount);
    }

    public void setVideoCount(int i) {
        videoCount = i;
    }

    public int getRecordingCount() {
        return (recordingCount);
    }

    public void setRecordingCount(int i) {
        recordingCount = i;
    }

    public int getRecorderCount() {
        return (recorderCount);
    }

    public void setRecorderCount(int i) {
        recorderCount = i;
    }

    public int getRecorderBusyCount() {
        return (recorderBusyCount);
    }

    public void setRecorderBusyCount(int i) {
        recorderBusyCount = i;
    }

}


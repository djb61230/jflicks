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
package org.jflicks.tv.scheduler;

import java.io.File;
import java.io.Serializable;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import org.jflicks.configure.BaseConfig;
import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Airing;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;
import org.jflicks.tv.Upcoming;
import org.jflicks.tv.recorder.Recorder;
import org.jflicks.util.LogUtil;

/**
 * This class is a base implementation of the Scheduler interface.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public abstract class BaseScheduler extends BaseConfig implements Scheduler {

    private NMS nms;
    private String title;
    private ArrayList<PendingRecord> pendingRecordList;
    private ArrayList<PendingRecord> workPendingRecordList;
    private int robinIndex;

    /**
     * Simple empty constructor.
     */
    public BaseScheduler() {

        setPendingRecordList(new ArrayList<PendingRecord>());
        setWorkPendingRecordList(new ArrayList<PendingRecord>());
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return (title);
    }

    /**
     * Convenience method to set this property.
     *
     * @param s The given title value.
     */
    public void setTitle(String s) {
        title = s;
    }

    /**
     * {@inheritDoc}
     */
    public NMS getNMS() {
        return (nms);
    }

    /**
     * {@inheritDoc}
     */
    public void setNMS(NMS n) {
        nms = n;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getConfiguredListingNames() {

        String[] result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue[] array = c.getNameValues();
            if (array != null) {

                ArrayList<String> l = new ArrayList<String>();
                for (int i = 0; i < array.length; i++) {

                    String desc = array[i].getDescription();
                    if ((desc != null) && (desc.equals(NMSConstants.RECORDING_DEVICE))) {

                        String tmp = array[i].getValue();
                        if ((tmp != null) && (!tmp.equals(NMSConstants.NOT_CONNECTED))) {

                            if (!l.contains(tmp)) {
                                l.add(tmp);
                            }
                        }
                    }
                }

                if (l.size() > 0) {

                    result = l.toArray(new String[l.size()]);
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Recorder[] getConfiguredRecorders() {

        Recorder[] result = null;

        NMS n = getNMS();
        Configuration c = getConfiguration();
        if ((c != null) && (n != null)) {

            NameValue[] array = c.getNameValues();
            if (array != null) {

                ArrayList<Recorder> l = new ArrayList<Recorder>();
                for (int i = 0; i < array.length; i++) {

                    String desc = array[i].getDescription();
                    if ((desc != null) && (desc.equals(NMSConstants.RECORDING_DEVICE))) {

                        String tmp = array[i].getValue();
                        if ((tmp != null) && (!tmp.equals(NMSConstants.NOT_CONNECTED))) {

                            // Ok found a connected Recorder.  We need to get
                            // the device from the name.  It should be the
                            // last token.
                            String name = array[i].getName();
                            if (name != null) {

                                name = name.substring(name.lastIndexOf(" "));
                                name = name.trim();
                                Recorder rec = n.getRecorderByDevice(name);
                                if (rec != null) {

                                    l.add(rec);
                                }
                            }
                        }
                    }
                }

                if (l.size() > 0) {

                    // Before we go back we want to put our "preferred" recorders first.
                    ArrayList<Recorder> plist = new ArrayList<Recorder>();
                    for (int i = 0; i < l.size(); i++) {

                        Recorder r = l.get(i);
                        if (r.isPreferred()) {

                            plist.add(r);
                        }
                    }

                    for (int i = 0; i < l.size(); i++) {

                        Recorder r = l.get(i);
                        if (!r.isPreferred()) {

                            plist.add(r);
                        }
                    }

                    result = plist.toArray(new Recorder[plist.size()]);
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getConfiguredRecordingDirectories() {

        String[] result = null;

        Configuration c = getConfiguration();
        if (c != null) {

            NameValue nv =
                c.findNameValueByName(NMSConstants.RECORDING_DIRECTORIES);
            if (nv != null) {

                result = nv.valueToArray();
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Channel[] getRecordableChannels() {

        Channel[] result = null;

        Recorder[] rarray = getConfiguredRecorders();
        if ((rarray != null) && (rarray.length > 0)) {

            ArrayList<Channel> clist = new ArrayList<Channel>();
            for (int i = 0; i < rarray.length; i++) {

                Channel[] carray = getChannelsByRecorder(rarray[i]);
                if ((carray != null) && (carray.length > 0)) {

                    for (int j = 0; j < carray.length; j++) {

                        if (!clist.contains(carray[j])) {

                            clist.add(carray[j]);
                        }
                    }
                }
            }

            if (clist.size() > 0) {

                result = clist.toArray(new Channel[clist.size()]);
                Arrays.sort(result);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public String getListingNameByRecorder(Recorder r) {

        String result = null;

        Configuration c = getConfiguration();
        NMS n = getNMS();
        if ((c != null) && (n != null) && (r != null)) {

            NameValue[] array = c.getNameValues();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    String desc = array[i].getDescription();
                    if ((desc != null)
                        && (desc.equals(NMSConstants.RECORDING_DEVICE))) {

                        String tmp = array[i].getValue();
                        if ((tmp != null)
                            && (!tmp.equals(NMSConstants.NOT_CONNECTED))) {

                            // Ok found a connected Recorder.  We need to get
                            // the device from the name.  It should be the
                            // last token.
                            String dev = array[i].getName();
                            if (dev != null) {

                                dev = dev.substring(dev.lastIndexOf(" "));
                                dev = dev.trim();
                                if (dev.equals(r.getDevice())) {

                                    // Ok found the recorder.
                                    result = tmp;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return (result);
    }

    protected Channel[] getChannelsByRecorder(Recorder r) {

        Channel[] result = null;

        Configuration c = getConfiguration();
        NMS n = getNMS();
        if ((c != null) && (n != null) && (r != null)) {

            NameValue[] array = c.getNameValues();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    String desc = array[i].getDescription();
                    if ((desc != null) && (desc.equals(NMSConstants.RECORDING_DEVICE))) {

                        String tmp = array[i].getValue();
                        if ((tmp != null) && (!tmp.equals(NMSConstants.NOT_CONNECTED))) {

                            // Ok found a connected Recorder.  We need to get
                            // the device from the name.  It should be the
                            // last token.
                            String dev = array[i].getName();
                            if (dev != null) {

                                dev = dev.substring(dev.lastIndexOf(" "));
                                dev = dev.trim();
                                if (dev.equals(r.getDevice())) {

                                    // Ok found the recorder.
                                    LogUtil.log(LogUtil.DEBUG, "4GN listing name <" + tmp + ">");
                                    result = n.getChannelsByListingName(tmp);
                                    LogUtil.log(LogUtil.DEBUG, "After n.getChannelsByListingName(tmp)");

                                    // The result is all the channels that
                                    // are defined by the listing.  However
                                    // this particular Recorder may really
                                    // just support a subset of channels,
                                    // or have a list of channels that it
                                    // doesn't support.
                                    result = r.getCustomChannels(result);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return (result);
    }

    protected ArrayList<PendingRecord> getPendingRecordList() {
        return (pendingRecordList);
    }

    private void setPendingRecordList(ArrayList<PendingRecord> l) {
        pendingRecordList = l;
    }

    private ArrayList<PendingRecord> getWorkPendingRecordList() {
        return (workPendingRecordList);
    }

    private void setWorkPendingRecordList(ArrayList<PendingRecord> l) {
        workPendingRecordList = l;
    }

    /**
     * {@inheritDoc}
     */
    public PendingRecord[] getReadyPendingRecords() {

        PendingRecord[] result = null;

        ArrayList<PendingRecord> list = getPendingRecordList();
        if ((list != null) && (list.size() > 0)) {

            ArrayList<PendingRecord> readylist = null;
            long time = System.currentTimeMillis() + 60000;
            int count = 0;

            for (int i = 0; i < list.size(); i++) {

                PendingRecord pr = list.get(i);
                if (pr.getStart() < time) {

                    // Time has come to get rid of this PendingRecord,
                    // whether it's going to be recorded or not.
                    count++;

                    // Now if it's ready, add it to our ready list.
                    if (pr.isReadyStatus()) {

                        if (readylist == null) {

                            readylist = new ArrayList<PendingRecord>();
                        }

                        readylist.add(pr);
                    }
                }
            }

            // Remove the ones that have time expired...
            if (count > 0) {

                for (int i = 0; i < count; i++) {

                    list.remove(0);
                }
            }

            if (readylist != null) {

                for (int i = 0; i < readylist.size(); i++) {

                    PendingRecord pr = readylist.get(i);
                    if (pr != null) {

                        addRecordedShow(new RecordedShow(pr.getShowId()));
                        Recording rec = pr.getRecording();
                        if (rec != null) {

                            rec.setCurrentlyRecording(true);
                            addRecording(rec);
                        }

                        /*
                         * Note we will remove this rule when indexer is called.
                        RecordingRule rr = pr.getRecordingRule();
                        if ((rr != null) && (rr.isOnceType())) {

                            removeRecordingRule(rr);
                        }
                        */
                    }

                }

                result = readylist.toArray(new PendingRecord[readylist.size()]);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public File createFile(PendingRecord pr) {

        File result = null;

        String[] array = getConfiguredRecordingDirectories();
        if ((array != null) && (array.length > 0)) {

            File dir = null;
            int checks = 0;

            // Make sure the directory has at least 6-gig available...
            while (checks < array.length) {

                // We keep a robinIndex to rotate around directories.
                if (robinIndex >= array.length) {

                    robinIndex = 0;
                }

                // Use the current directory and then incr.
                dir = new File(array[robinIndex++]);
                if (dir.getUsableSpace() > 16442450944L) {

                    // We are done.
                    checks = array.length;

                } else {

                    LogUtil.log(LogUtil.INFO, "Skipping " + dir
                        + " as there is not enough room.");
                    checks++;
                    dir = null;
                }
            }

            if (dir != null) {

                StringBuffer sb = new StringBuffer();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
                sdf.format(new Date(pr.getStart()), sb, new FieldPosition(0));
                Recorder rec = pr.getRecorder();
                if (rec != null) {

                    String ext = rec.getExtension();
                    if (ext != null) {

                        sb.append("." + ext);

                    } else {

                        sb.append(".mpg");
                    }

                } else {

                    sb.append(".mpg");
                }
                sb.insert(0, pr.getShowId() + "_");

                String cleanName = processName(pr.getRecording());
                if (cleanName != null) {

                    sb.insert(0, cleanName + "_");
                }

                result = new File(dir, sb.toString());

            } else {

                LogUtil.log(LogUtil.WARNING, "No configured directories have space!");
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Upcoming[] getUpcomings() {

        Upcoming[] result = null;

        ArrayList<PendingRecord> list = getPendingRecordList();
        if ((list != null) && (list.size() > 0)) {

            String[] priorities = RecordingRule.getPriorityNames();
            result = new Upcoming[list.size()];
            for (int i = 0; i < list.size(); i++) {

                PendingRecord pr = list.get(i);
                RecordingRule rr = pr.getRecordingRule();

                result[i] = new Upcoming();
                result[i].setShowId(pr.getShowId());
                if (rr != null) {

                    result[i].setTitle(rr.getName());
                    result[i].setPriority(priorities[rr.getPriority()]);

                } else {

                    result[i].setTitle("unknown");
                    result[i].setPriority("unknown");
                }

                Recording rec = pr.getRecording();
                if (rec != null) {

                    result[i].setSubtitle(rec.getSubtitle());
                    result[i].setDescription(rec.getDescription());
                    result[i].setSeriesId(rec.getSeriesId());
                    result[i].setDate(rec.getDate());

                } else {

                    result[i].setSubtitle("unknown");
                    result[i].setDescription("unknown");
                }

                Channel c = pr.getChannel();
                if (c != null) {

                    result[i].setChannelNumber(c.getNumber());
                    result[i].setChannelName(c.getName());

                } else {

                    result[i].setChannelNumber("unknown");
                    result[i].setChannelName("unknown");
                }

                result[i].setDuration(((pr.getDuration() + 1) / 60)
                    + " minutes");
                Date d = new Date(pr.getStart());
                result[i].setStart(d.toString());
                result[i].setStatus(pr.getStatusAsString());

                Recorder r = pr.getRecorder();
                if (r != null) {

                    result[i].setRecorderName(r.getTitle() + " "
                        + r.getDevice());
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void requestRescheduling() {

        // For now we just do it.  Perhaps in the future we might need
        // to take care in case we have different users asking at the
        // same time.
        LogUtil.log(LogUtil.INFO, "requestRescheduling updatePendingRecords");
        updatePendingRecords();
        LogUtil.log(LogUtil.INFO, "requestRescheduling updatePendingRecords done");
    }

    protected void updatePendingRecords() {

        LogUtil.log(LogUtil.INFO, "Running updatePendingRecords");

        ArrayList<PendingRecord> workList = getWorkPendingRecordList();
        NMS n = getNMS();
        Recorder[] recs = getConfiguredRecorders();
        if ((workList != null) && (n != null) && (recs != null)) {

            LogUtil.log(LogUtil.DEBUG, "CONFIGURED RECORDERS COUNT: " + recs.length);
            RecorderInformation[] ris = new RecorderInformation[recs.length];
            for (int i = 0; i < ris.length; i++) {

                ris[i] = new RecorderInformation();
                ris[i].setRecorder(recs[i]);
                ris[i].setChannels(getChannelsByRecorder(recs[i]));
                if (recs[i].isRecording()) {

                    LogUtil.log(LogUtil.DEBUG, "recorder: " + recs[i]
                        + " is recording now...adding time range");
                    long started = recs[i].getStartedAt();
                    TimeRange tr = new TimeRange(started,
                        started + (recs[i].getDuration() * 1000));
                    ris[i].addTimeRange(tr);

                } else {

                    LogUtil.log(LogUtil.DEBUG, "recorder " + recs[i] + " is NOT recording now");
                }
            }
            workList.clear();

            RecordingRule[] rules = getRecordingRules();
            if (rules != null) {

                // Now we need to sort the rules by priority so higher
                // priority rules get done first.
                for (int i = 0; i < rules.length; i++) {
                    rules[i].setSortBy(RecordingRule.SORT_BY_PRIORITY);
                }
                Arrays.sort(rules);

                for (int i = 0; i < rules.length; i++) {

                    // Right now we just have ONCE or SERIES recording types.
                    // Perhaps more in the future but for now...first see
                    // what channel we are talking about...
                    Channel chan = n.getChannelById(rules[i].getChannelId(), rules[i].getListingId());

                    LogUtil.log(LogUtil.DEBUG, "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                    LogUtil.log(LogUtil.DEBUG, "chan: " + chan);
                    LogUtil.log(LogUtil.DEBUG, "id: " + rules[i].getChannelId());
                    LogUtil.log(LogUtil.DEBUG, "id: " + rules[i].getListingId());
                    LogUtil.log(LogUtil.DEBUG, "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                    if (chan != null) {

                        long now = System.currentTimeMillis();
                        ShowAiring[] sas = null;

                        int type = rules[i].getType();
                        if (type == RecordingRule.ONCE_TYPE) {

                            ShowAiring sa = rules[i].getShowAiring();
                            if (sa != null) {

                                if (!sa.isOver()) {

                                    sas = new ShowAiring[1];
                                    sas[0] = sa;

                                    // Even record if it's been done before.
                                    removeRecordedShow(
                                        new RecordedShow(sa.getShow()));

                                } else {

                                    removeRecordingRule(rules[i]);
                                }

                            } else {

                                LogUtil.log(LogUtil.DEBUG, "We have a ONCE but no ShowAiring!");
                            }

                        } else if (type == RecordingRule.SERIES_TYPE) {

                            sas = getShowAiringsByChannelAndSeriesId(chan,
                                    rules[i].getSeriesId());
                        }

                        if (sas != null) {

                            Arrays.sort(sas);
                            for (int j = 0; j < sas.length; j++) {

                                Airing air = sas[j].getAiring();
                                Show show = sas[j].getShow();
                                if ((air != null) && (show != null)) {

                                    Date date = air.getAirDate();
                                    if (date != null) {

                                        long slen = air.getDuration() * 1000;
                                        if (now < date.getTime()) {

                                            // This hasn't happended yet so
                                            // put er in.
                                            PendingRecord pr = new PendingRecord();
                                            pr.setRecordingRule(rules[i]);
                                            pr.setName(show.getTitle());
                                            pr.setShowId(show.getId());
                                            pr.setChannel(chan);
                                            pr.setDuration(air.getDuration() - 10 + rules[i].getEndPadding());
                                            pr.setStart(date.getTime() + (rules[i].getBeginPadding() * 1000));

                                            Recording rec = new Recording(sas[j]);
                                            pr.setRecording(rec);
                                            rec.setRecordingRuleId(pr.getRecordingRule().getId());
                                            workList.add(pr);

                                        } else if (now < (date.getTime() + slen)) {

                                            // This could be just requested
                                            // to record.  We only put it
                                            // in if it's not currently being
                                            // recorded now.
                                            if (!isRecordingNow(sas[j])) {

                                                PendingRecord pr = new PendingRecord();
                                                pr.setRecordingRule(rules[i]);
                                                pr.setName(show.getTitle());
                                                pr.setShowId(show.getId());
                                                pr.setChannel(chan);
                                                pr.setDuration(air.getDuration() - 10 + rules[i].getEndPadding());
                                                pr.setStart(date.getTime() + (rules[i].getBeginPadding() * 1000));

                                                Recording rec = new Recording(sas[j]);
                                                rec.setRecordingRuleId(pr.getRecordingRule().getId());
                                                pr.setRecording(rec);
                                                workList.add(pr);
                                            }
                                        }

                                    } else {

                                        LogUtil.log(LogUtil.DEBUG, "We have an Airing but "
                                            + " the Date is NULL.");
                                    }

                                } else {

                                    LogUtil.log(LogUtil.DEBUG, "We have a ShowAiring but "
                                        + " the Show or Airing is NULL.");
                                }
                            }
                        }
                    }
                }
            }

            // At this point we have all our PendingRecord instances created.
            // Now we work on eliminating them if we have recorded them
            // before or if we don't have a recorder available.
            LogUtil.log(LogUtil.DEBUG, "pending record count: " + workList.size()
                + " before checking whether previous recorded");
            for (int i = 0; i < workList.size(); i++) {

                PendingRecord pr = workList.get(i);
                if (isAlreadyRecorded(pr.getShowId())) {

                    pr.setStatus(PendingRecord.PREVIOUS_RECORD);
                }
            }

            // Next we need to update the "laterAvailable" and
            // "earlierAvailable" flags to help us sort it out later.
            checkDuplicates(workList);

            // Next we need to assign a Recorder.  Here the "status" could
            // turn to be a "conflict" or "later".
            for (int i = 0; i < workList.size(); i++) {

                PendingRecord pr = workList.get(i);
                if ((!pr.isEarlierStatus()) && (!pr.isPreviousRecordStatus())) {

                    Channel channel = pr.getChannel();
                    long start = pr.getStart();
                    long end = start + (pr.getDuration() * 1000);
                    TimeRange tr = new TimeRange(start, end);
                    for (int j = 0; j < ris.length; j++) {

                        if (ris[j].supports(channel)) {

                            if (!ris[j].isBusyAt(tr)) {

                                ris[j].addTimeRange(tr);
                                pr.setRecorder(ris[j].getRecorder());
                                File f = createFile(pr);
                                pr.setFile(f);
                                Recording rec = pr.getRecording();
                                rec.setPath(f.getPath());
                                pr.setStatus(PendingRecord.READY);

                                // Now lets flag any duplicates there
                                // might be since we have this recording
                                // covered here.
                                if (pr.isLaterAvailable()) {

                                    flagDuplicates(workList, pr);
                                }
                                break;

                            } else {

                                LogUtil.log(LogUtil.DEBUG, "recorder busy: " + ris[j]);
                            }

                        } else {

                            LogUtil.log(LogUtil.DEBUG, "supports channel failed...");
                        }
                    }
                }

                if (pr.isUndeterminedStatus()) {

                    if (pr.isLaterAvailable()) {

                        pr.setStatus(PendingRecord.LATER);

                    } else {

                        pr.setStatus(PendingRecord.CONFLICT);
                    }
                }
            }

            for (int i = 0; i < workList.size(); i++) {

                PendingRecord pr = workList.get(i);
                if (pr.isUndeterminedStatus()) {

                    LogUtil.log(LogUtil.WARNING, "PROBLEM: Should all be solved by now");
                }
            }

            // So at this point the working list should be all set.
            // So clear the regular list and copy over the working one.
            ArrayList<PendingRecord> realList = getPendingRecordList();
            if (realList != null) {

                synchronized (realList) {

                    Collections.sort(workList);
                    realList.clear();
                    realList.addAll(workList);
                }

                dump();

                n.sendMessage(NMSConstants.MESSAGE_SCHEDULE_UPDATE);
            }
        }
    }

    private String processName(Recording r) {

        String result = null;

        if (r != null) {

            String title = r.getTitle();
            if (title != null) {

                // Try the subtitle too.
                String sub = r.getSubtitle();
                if (sub != null) {

                    title = title + " " + sub;
                }

                // Only alphas and spaces.
                title = title.replaceAll("[^a-zA-Z\\s]", "");

                // Trim the ends of spaces.
                title = title.trim();

                // At most one space at a time.
                title = title.replaceAll(" +", " ");

                // Turn spaces to underscores.
                result = title.replaceAll(" ", "_");

            } else {

                LogUtil.log(LogUtil.DEBUG, "processName Recording title null!");
            }

        } else {

            LogUtil.log(LogUtil.DEBUG, "processName Recording null!");
        }

        return (result);
    }

    private boolean isRecordingNow(ShowAiring sa) {

        boolean result = false;

        if (sa != null) {

            Airing airing = sa.getAiring();
            Recorder[] array = getConfiguredRecorders();
            if ((array != null) && (airing != null)) {

                int cid = airing.getChannelId();
                for (int i = 0; i < array.length; i++) {

                    if (array[i].isRecording()) {

                        Channel c = array[i].getChannel();
                        if (c != null) {

                            if (cid == c.getId()) {

                                result = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        LogUtil.log(LogUtil.DEBUG, "isRecordingNow: " + sa + " " + result);

        return (result);
    }

    private void flagDuplicates(ArrayList<PendingRecord> list,
        PendingRecord pr) {

        if ((list != null) && (pr != null)) {

            String showId = pr.getShowId();
            if ((showId != null) && (!pr.isOnceType())) {

                for (int i = 0; i < list.size(); i++) {

                    PendingRecord tmp = list.get(i);
                    if ((tmp != pr) && (!tmp.isOnceType())) {

                        if (showId.equals(tmp.getShowId())) {

                            tmp.setStatus(PendingRecord.EARLIER);
                        }
                    }
                }
            }
        }
    }

    private void checkDuplicates(ArrayList<PendingRecord> list) {

        // We cannot assume the list is sorted in anyway so we have to
        // collect all duplicated by searching through the list.
        if ((list != null) && (list.size() > 1)) {

            ArrayList<PendingRecord> duplist = new ArrayList<PendingRecord>();

            for (int i = 0; i < list.size(); i++) {

                PendingRecord pr0 = list.get(i);
                String showId = pr0.getShowId();
                if ((pr0.isUndeterminedStatus()) && (showId != null)) {

                    duplist.clear();
                    //for (int j = 0; j < list.size(); j++) {
                    for (int j = i + 1; j < list.size(); j++) {

                        if (i != j) {

                            PendingRecord pr1 = list.get(j);
                            if (!pr1.isOnceType()) {

                                if (pr1.isUndeterminedStatus()) {

                                    if (showId.equals(pr1.getShowId())) {

                                        if (!duplist.contains(pr0)) {
                                            duplist.add(pr0);
                                        }
                                        if (!duplist.contains(pr1)) {
                                            duplist.add(pr1);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (duplist.size() > 0) {

                        Collections.sort(duplist);
                        for (int k = 0; k < duplist.size(); k++) {

                            if (k == 0) {

                                // First item in the list.
                                duplist.get(k).setLaterAvailable(true);
                                duplist.get(k).setEarlierAvailable(false);

                            } else if ((k + 1) < duplist.size()) {

                                // A middle item.
                                duplist.get(k).setLaterAvailable(true);
                                duplist.get(k).setEarlierAvailable(true);

                            } else {

                                // This is the last item in the list.
                                duplist.get(k).setLaterAvailable(false);
                                duplist.get(k).setEarlierAvailable(true);
                            }
                        }
                    }
                }
            }
        }
    }

    private void dump() {

        ArrayList<PendingRecord> realList = getPendingRecordList();
        if (realList != null) {

            int readyCount = 0;
            for (int i = 0; i < realList.size(); i++) {

                PendingRecord pr = realList.get(i);
                LogUtil.log(LogUtil.DEBUG, "Channel: " + pr.getChannel());
                LogUtil.log(LogUtil.DEBUG, "Duration: " + pr.getDuration());
                LogUtil.log(LogUtil.DEBUG, "File: " + pr.getFile());
                LogUtil.log(LogUtil.DEBUG, "Name: " + pr.getName());
                LogUtil.log(LogUtil.DEBUG, "Recorder: " + pr.getRecorder());
                LogUtil.log(LogUtil.DEBUG, "ShowId: " + pr.getShowId());
                LogUtil.log(LogUtil.DEBUG, "Start: " + new Date(pr.getStart()));
                LogUtil.log(LogUtil.DEBUG, "Status: " + pr.getStatus());
                LogUtil.log(LogUtil.DEBUG, "-----------------------------------");

                if (pr.isReadyStatus()) {
                    readyCount++;
                }
            }

            LogUtil.log(LogUtil.INFO, "There are " + readyCount + " recordings scheduled.");
        }
    }

    static class PendingRecordByTime implements Comparator<PendingRecord>,
        Serializable {

        public int compare(PendingRecord pr0, PendingRecord pr1) {

            Long l0 = Long.valueOf(pr0.getStart());
            Long l1 = Long.valueOf(pr1.getStart());

            return (l0.compareTo(l1));
        }
    }

}


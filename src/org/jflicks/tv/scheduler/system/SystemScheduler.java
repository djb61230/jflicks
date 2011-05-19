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

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jflicks.db.DbWorker;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Airing;
import org.jflicks.tv.Channel;
import org.jflicks.tv.Recording;
import org.jflicks.tv.RecordingRule;
import org.jflicks.tv.Show;
import org.jflicks.tv.ShowAiring;
import org.jflicks.tv.Task;
import org.jflicks.tv.postproc.PostProc;
import org.jflicks.tv.postproc.worker.Worker;
import org.jflicks.tv.scheduler.BaseScheduler;
import org.jflicks.tv.scheduler.RecordedShow;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.osgi.Db4oService;
import com.db4o.query.Predicate;

/**
 * Class that can control recording schedules.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SystemScheduler extends BaseScheduler implements DbWorker {

    private ObjectContainer objectContainer;
    private ObjectContainer cacheObjectContainer;
    private ObjectContainer recordedObjectContainer;
    private ObjectContainer recordingObjectContainer;
    private Db4oService db4oService;

    /**
     * Simple default constructor.
     */
    public SystemScheduler() {

        setTitle("SystemScheduler");
    }

    /**
     * We use the Db4oService to persist the recording settings.
     *
     * @return A Db4oService instance.
     */
    public Db4oService getDb4oService() {
        return (db4oService);
    }

    /**
     * We use the Db4oService to persist the recording settings.
     *
     * @param s A Db4oService instance.
     */
    public void setDb4oService(Db4oService s) {
        db4oService = s;
    }

    private synchronized ObjectContainer getObjectContainer() {

        if (objectContainer == null) {

            Db4oService s = getDb4oService();
            if (s != null) {

                com.db4o.config.Configuration config = s.newConfiguration();
                config.objectClass(RecordingRule.class).cascadeOnActivate(true);
                config.objectClass(RecordingRule.class).cascadeOnUpdate(true);
                objectContainer = s.openFile(config, "db/sched.dat");
            }
        }

        return (objectContainer);
    }

    private synchronized ObjectContainer getCacheObjectContainer() {

        if (cacheObjectContainer == null) {

            Db4oService s = getDb4oService();
            if (s != null) {

                com.db4o.config.Configuration config = s.newConfiguration();
                config.objectClass(ShowAiring.class).cascadeOnUpdate(true);
                cacheObjectContainer = s.openFile(config, "db/sacache.dat");

            }
        }

        return (objectContainer);
    }

    private synchronized ObjectContainer getRecordedObjectContainer() {

        if (recordedObjectContainer == null) {

            Db4oService s = getDb4oService();
            if (s != null) {

                com.db4o.config.Configuration config = s.newConfiguration();
                config.objectClass(
                    RecordedShow.class).objectField("showId").indexed(true);
                recordedObjectContainer = s.openFile(config, "db/recorded.dat");
            }
        }

        return (recordedObjectContainer);
    }

    private synchronized ObjectContainer getRecordingObjectContainer() {

        if (recordingObjectContainer == null) {

            Db4oService s = getDb4oService();
            if (s != null) {

                com.db4o.config.Configuration config = s.newConfiguration();
                config.objectClass(Recording.class).cascadeOnUpdate(true);
                recordingObjectContainer =
                    s.openFile(config, "db/recordings.dat");
            }
        }

        return (recordingObjectContainer);
    }

    /**
     * Close up all resources.
     */
    public void close() {

        if (objectContainer != null) {

            boolean result = objectContainer.close();
            log(DEBUG, "SystemScheduler: closed " + result);
            objectContainer = null;

        } else {

            log(DEBUG, "SystemScheduler: Tried to close "
                + "but objectContainer null.");
        }

        if (cacheObjectContainer != null) {

            boolean result = cacheObjectContainer.close();
            log(DEBUG, "SystemScheduler: (cache) closed " + result);
            cacheObjectContainer = null;

        } else {

            log(DEBUG, "SystemScheduler: Tried to close "
                + "but cacheObjectContainer null.");
        }

        if (recordedObjectContainer != null) {

            boolean result = recordedObjectContainer.close();
            log(DEBUG, "SystemScheduler (recorded): closed " + result);
            recordedObjectContainer = null;

        } else {

            log(DEBUG, "SystemScheduler: Tried to close "
                + "but recordedObjectContainer null.");
        }

        if (recordingObjectContainer != null) {

            boolean result = recordingObjectContainer.close();
            log(DEBUG, "SystemScheduler (recording): closed " + result);
            recordingObjectContainer = null;

        } else {

            log(DEBUG, "SystemScheduler: Tried to close "
                + "but recordingObjectContainer null.");
        }
    }

    /**
     * {@inheritDoc}
     */
    public RecordingRule[] getRecordingRules() {

        RecordingRule[] result = null;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            ObjectSet<RecordingRule> os =
                oc.queryByExample(RecordingRule.class);
            if (os != null) {

                result = os.toArray(new RecordingRule[os.size()]);
                Arrays.sort(result);

                // At this point we should reconcile the current
                // workers with the Task definitions of the RecordingRule
                // instances.  If something needs to change it should be
                // written out to disk.  This *could* be bad for users if
                // using workers or more often they add them.  This will
                // dynamically find them without them having to fix it
                // manually.
                if (result != null) {

                    for (int i = 0; i < result.length; i++) {
                        reconcileRecordingRule(result[i]);
                    }
                }
            }
        }

        return (result);
    }

    private int contains(Task[] array, Task t) {

        int result = -1;

        if ((array != null) && (t != null)) {

            String title = t.getTitle();
            if (title != null) {

                for (int i = 0; i < array.length; i++) {

                    if (title.equals(array[i].getTitle())) {

                        result = i;
                        break;
                    }
                }
            }
        }

        return (result);
    }

    private boolean hasTask(Worker[] array, Task t) {

        boolean result = false;

        if ((array != null) && (t != null)) {

            String title = t.getTitle();
            if (title != null) {

                for (int i = 0; i < array.length; i++) {

                    if (title.equals(array[i].getTitle())) {

                        result = true;
                        break;
                    }
                }
            }
        }

        return (result);
    }

    private boolean hasWorker(Task[] array, Worker w) {

        boolean result = false;

        if ((array != null) && (w != null)) {

            String title = w.getTitle();
            if (title != null) {

                for (int i = 0; i < array.length; i++) {

                    if (title.equals(array[i].getTitle())) {

                        result = true;
                        break;
                    }
                }
            }
        }

        return (result);
    }

    private void reconcileRecordingRule(RecordingRule rr) {

        NMS n = getNMS();
        if ((n != null) && (rr != null)) {

            PostProc pp = n.getPostProc();
            if (pp != null) {

                Worker[] warray = pp.getWorkers();
                Task[] tarray = rr.getTasks();
                if ((warray != null) && (tarray != null)) {

                    ArrayList<Worker> wmissing = new ArrayList<Worker>();
                    for (int i = 0; i < warray.length; i++) {

                        if (!hasWorker(tarray, warray[i])) {

                            wmissing.add(warray[i]);
                        }
                    }

                    ArrayList<Task> tmissing = new ArrayList<Task>();
                    for (int i = 0; i < tarray.length; i++) {

                        if (!hasTask(warray, tarray[i])) {

                            tmissing.add(tarray[i]);
                        }
                    }

                    // At this point we have lists that have the
                    // "mis-matches".  If either has anything in it
                    // then we need to create a new Task array and
                    // make it coincide with the Worker array.
                    if ((wmissing.size() > 0) || (tmissing.size() > 0)) {

                        // A fresh set of Tasks at their default settings.
                        // However we don't want to lose a setting the user
                        // has changed so we want to pick and choose from
                        // the old array and new array.  Only use new array
                        // if it is new to us.
                        ArrayList<Task> tasklist = new ArrayList<Task>();
                        Task[] fresh = n.getTasks();
                        if ((fresh != null) && (fresh.length > 0)) {

                            for (int i = 0; i < fresh.length; i++) {

                                int index = contains(tarray, fresh[i]);
                                if (index != -1) {

                                    tasklist.add(tarray[index]);

                                } else {

                                    tasklist.add(fresh[i]);
                                }
                            }
                        }

                        // Update the proper tasks.
                        Task[] tasks = null;
                        if (tasklist.size() > 0) {

                            tasks = tasklist.toArray(new Task[tasklist.size()]);
                        }

                        if (tasks != null) {

                            Arrays.sort(tasks, new TaskByDescription());
                        }

                        log(INFO, "We need to update the RecordingRule since "
                            + "the tasks have changed.");
                        rr.setTasks(tasks);
                        addRecordingRule(rr);
                    }
                }
            }
        }
    }

    private RecordingRule getRecordingRuleById(String id) {

        RecordingRule result = null;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            final String sid = id;
            List<RecordingRule> rules =
                oc.query(new Predicate<RecordingRule>() {

                public boolean match(RecordingRule rr) {
                    return (sid.equals(rr.getId()));
                }
            });

            if ((rules != null) && (rules.size() > 0)) {
                result = rules.get(0);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void addRecordingRule(RecordingRule rr) {

        ObjectContainer oc = getObjectContainer();
        if ((rr != null) && (oc != null)) {

            // First remove this rule if it already exists.
            removeRecordingRule(rr);
            oc.store(new RecordingRule(rr));
            oc.commit();
            addCacheFromRecordingRule(rr);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeRecordingRule(RecordingRule rr) {

        ObjectContainer oc = getObjectContainer();
        if ((rr != null) && (oc != null)) {

            final String name = rr.getName();
            List<RecordingRule> rules =
                oc.query(new Predicate<RecordingRule>() {

                public boolean match(RecordingRule rr) {
                    return (name.equals(rr.getName()));
                }
            });

            if (rules != null) {

                log(DEBUG, "Found " + rules.size() + " rules");
                for (int i = 0; i < rules.size(); i++) {

                    RecordingRule trule = rules.get(i);
                    if (trule.getId().equals(rr.getId())) {

                        log(DEBUG, "Removing " + trule.getId());
                        oc.delete(trule);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Recording[] getRecordings() {

        Recording[] result = null;

        ObjectContainer oc = getRecordingObjectContainer();
        if (oc != null) {

            ObjectSet<Recording> os = oc.queryByExample(Recording.class);
            if (os != null) {

                result = os.toArray(new Recording[os.size()]);
                Arrays.sort(result);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void updateRecording(Recording r) {

        ObjectContainer oc = getRecordingObjectContainer();
        if ((r != null) && (oc != null)) {

            removeRecording(r);

            // Before we continue, lets make sure the recording is on disk.
            // There is a chance that the user has deleted it underneath us
            // and we are putting a bogus object.
            File ondisk = new File(r.getPath());
            if ((ondisk.exists()) && (ondisk.isFile())) {

                oc.store(new Recording(r));
                oc.commit();

                // Tell clients via the NMS.
                NMS n = getNMS();
                if (n != null) {

                    n.sendMessage(NMSConstants.MESSAGE_RECORDING_UPDATE
                        + " : " + r.getId());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void indexRecording(String s, Recording r) {

        if ((s != null) && (r != null)) {

            NMS n = getNMS();
            if (n != null) {

                PostProc pp = n.getPostProc();
                if (pp != null) {

                    pp.addProcessing(s, r);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addRecording(Recording r) {

        ObjectContainer oc = getRecordingObjectContainer();
        if ((r != null) && (oc != null)) {

            removeRecording(r);
            oc.store(new Recording(r));
            oc.commit();

            // We have added a recording so it's time to queue up any
            // post processing needed to be done.
            RecordingRule rr = getRecordingRuleById(r.getRecordingRuleId());
            NMS n = getNMS();
            if ((rr != null) && (n != null)) {

                PostProc pp = n.getPostProc();
                if (pp != null) {

                    pp.addProcessing(rr, r);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeRecording(Recording r) {

        ObjectContainer oc = getRecordingObjectContainer();
        if ((r != null) && (oc != null)) {

            final String id = r.getId();
            List<Recording> recs = oc.query(new Predicate<Recording>() {

                public boolean match(Recording r) {
                    return (id.equals(r.getId()));
                }
            });

            if (recs != null) {

                // We will delete them all but we should have only found 1.
                for (int i = 0; i < recs.size(); i++) {
                    oc.delete(recs.get(i));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAlreadyRecorded(String showId) {

        boolean result = false;

        ObjectContainer oc = getRecordedObjectContainer();
        if ((showId != null) && (oc != null)) {

            final String sid = showId;
            List<RecordedShow> shows =
                oc.query(new Predicate<RecordedShow>() {

                public boolean match(RecordedShow rs) {
                    return (sid.equals(rs.getShowId()));
                }
            });

            if (shows != null) {

                result = (shows.size() > 0);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void addRecordedShow(RecordedShow rs) {

        ObjectContainer oc = getRecordedObjectContainer();
        if ((rs != null) && (oc != null)) {

            removeRecordedShow(rs);
            oc.store(rs);
            oc.commit();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeRecordedShow(RecordedShow rs) {

        ObjectContainer oc = getRecordedObjectContainer();
        if ((rs != null) && (oc != null)) {

            final String sid = rs.getShowId();
            List<RecordedShow> shows =
                oc.query(new Predicate<RecordedShow>() {

                public boolean match(RecordedShow rs) {
                    return (sid.equals(rs.getShowId()));
                }
            });

            if (shows != null) {

                // We will delete them all but we should have only found 1.
                for (int i = 0; i < shows.size(); i++) {
                    oc.delete(shows.get(i));
                }
            }
        }
    }

    private void purge(ObjectContainer db, Class c) {

        if ((db != null) && (c != null)) {

            ObjectSet result = db.queryByExample(c);
            while (result.hasNext()) {
                db.delete(result.next());
            }
        }
    }

    private void addShow(Show s) {

        ObjectContainer oc = getCacheObjectContainer();
        if ((s != null) && (oc != null)) {

            oc.store(s);
            oc.commit();
        }
    }

    private void removeShow(Show s) {

        ObjectContainer oc = getCacheObjectContainer();
        if ((s != null) && (oc != null)) {

            final String id = s.getId();
            List<Show> shows = oc.query(new Predicate<Show>() {

                public boolean match(Show s) {
                    return (id.equals(s.getId()));
                }
            });

            if (shows != null) {

                // We will delete them all but we should have only found 1.
                for (int i = 0; i < shows.size(); i++) {
                    oc.delete(shows.get(i));
                }
            }
        }
    }

    private void addAiring(Airing a) {

        ObjectContainer oc = getCacheObjectContainer();
        if ((a != null) && (oc != null)) {

            oc.store(a);
            oc.commit();
        }
    }

    private void removeAiring(Airing a) {

        ObjectContainer oc = getCacheObjectContainer();
        if ((a != null) && (oc != null)) {

            final int cid = a.getChannelId();
            List<Airing> airings = oc.query(new Predicate<Airing>() {

                public boolean match(Airing a) {
                    return (cid == a.getChannelId());
                }
            });

            if (airings != null) {

                // We will delete them all but we should have only found 1.
                for (int i = 0; i < airings.size(); i++) {
                    oc.delete(airings.get(i));
                }
            }
        }
    }

    private void addCacheFromRecordingRule(RecordingRule rr) {

        NMS n = getNMS();
        if ((rr != null) && (n != null)) {

            if (rr.isOnceType()) {

                ShowAiring sa = rr.getShowAiring();
                if (sa != null) {

                    Show s = sa.getShow();
                    Airing a = sa.getAiring();
                    if ((s != null) && (a != null)) {

                        addShow(s);
                        addAiring(a);

                    } else {

                        if (s == null) {
                            log(DEBUG, "A ONCE recording doesnt have a Show");
                        }

                        if (a == null) {
                            log(DEBUG, "A ONCE recording doesnt have a Airing");
                        }
                    }

                } else {

                    log(DEBUG, "A ONCE recording does not have a ShowAiring");
                }

            } else {

                Channel c =
                    n.getChannelById(rr.getChannelId(), rr.getListingId());
                String seriesId = rr.getSeriesId();
                ShowAiring[] array =
                    n.getShowAiringsByChannelAndSeriesId(c, seriesId);
                if (array != null) {

                    for (int i = 0; i < array.length; i++) {

                        addShow(array[i].getShow());
                        addAiring(array[i].getAiring());
                    }
                }
            }
        }
    }

    private Airing[] getAiringsByChannel(Channel c) {

        Airing[] result = null;

        ObjectContainer oc = getCacheObjectContainer();
        if ((oc != null) && (c != null)) {

            final int id = c.getId();

            List<Airing> airings = oc.query(new Predicate<Airing>() {

                public boolean match(Airing a) {

                    return (id == a.getChannelId());
                }
            });

            if ((airings != null) && (airings.size() > 0)) {

                ArrayList<Airing> list = new ArrayList<Airing>();
                for (int i = 0; i < airings.size(); i++) {

                    Airing a = airings.get(i);
                    if (!list.contains(a)) {

                        list.add(a);
                    }
                }

                if (list.size() > 0) {

                    result = list.toArray(new Airing[list.size()]);
                }
            }
        }

        return (result);
    }

    private Show getShowByAiring(Airing a) {

        Show result = null;

        ObjectContainer oc = getCacheObjectContainer();
        if ((oc != null) && (a != null)) {

            final String id = a.getShowId();

            List<Show> shows = oc.query(new Predicate<Show>() {

                public boolean match(Show show) {
                    return (id.equals(show.getId()));
                }
            });

            if ((shows != null) && (shows.size() > 0)) {

                result = shows.get(0);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void rebuildCache() {

        ObjectContainer oc = getCacheObjectContainer();
        if (oc != null) {

            // First need to purge the cache.
            purge(oc, Show.class);
            purge(oc, Airing.class);
            RecordingRule[] rules = getRecordingRules();
            if (rules != null) {

                // For each rule, get from the guide data and add to cache
                for (int i = 0; i < rules.length; i++) {

                    addCacheFromRecordingRule(rules[i]);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public ShowAiring[] getShowAiringsByChannelAndSeriesId(Channel c,
        String seriesId) {

        ShowAiring[] result = null;

        ObjectContainer oc = getCacheObjectContainer();
        if ((c != null) && (seriesId != null) && (oc != null)) {

            Airing[] airings = getAiringsByChannel(c);
            if (airings != null) {

                ArrayList<ShowAiring> list = new ArrayList<ShowAiring>();
                for (int i = 0; i < airings.length; i++) {

                    Show show = getShowByAiring(airings[i]);
                    if (show != null) {

                        if (seriesId.equals(show.getSeriesId())) {

                            // We have one.
                            ShowAiring sa = new ShowAiring(show, airings[i]);
                            list.add(sa);
                        }
                    }
                }

                if (list.size() > 0) {

                    result = list.toArray(new ShowAiring[list.size()]);
                }
            }
        }

        return (result);
    }

    static class TaskByDescription implements Comparator<Task>, Serializable {

        public int compare(Task t0, Task t1) {

            String desc0 = t0.getDescription();
            if (desc0 == null) {
                desc0 = "";
            }
            String desc1 = t1.getDescription();
            if (desc1 == null) {
                desc1 = "";
            }

            return (desc0.compareTo(desc1));
        }
    }

}


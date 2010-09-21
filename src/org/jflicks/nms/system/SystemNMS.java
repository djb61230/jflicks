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
package org.jflicks.nms.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.jflicks.db.DbWorker;
import org.jflicks.configure.BaseConfiguration;
import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.nms.BaseNMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Listing;
import org.jflicks.tv.live.Live;
import org.jflicks.tv.ondemand.OnDemand;
import org.jflicks.tv.postproc.PostProc;
import org.jflicks.tv.programdata.ProgramData;
import org.jflicks.tv.recorder.Recorder;
import org.jflicks.tv.scheduler.Scheduler;
import org.jflicks.web.Web;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.osgi.Db4oService;
import com.db4o.query.Predicate;

/**
 * This is our implementation of an NMS.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SystemNMS extends BaseNMS implements DbWorker {

    private ObjectContainer objectContainer;
    private Db4oService db4oService;

    /**
     * Default empty constructor.
     */
    public SystemNMS() {

        setTitle("SystemNMS");
    }

    /**
     * We use the Db4oService to persist the configuration data.
     *
     * @return A Db4oService instance.
     */
    public Db4oService getDb4oService() {
        return (db4oService);
    }

    /**
     * We use the Db4oService to persist the configuration data.
     *
     * @param s A Db4oService instance.
     */
    public void setDb4oService(Db4oService s) {
        db4oService = s;

        if (s != null) {

            // Now that we have a data base service we should make sure
            // services we use have saved their default configurations.
            saveDefaultConfigurations();
        }
    }

    private synchronized ObjectContainer getObjectContainer() {

        if (objectContainer == null) {

            Db4oService s = getDb4oService();
            if (s != null) {

                com.db4o.config.Configuration config = s.newConfiguration();
                objectContainer = s.openFile(config, "db/config.dat");

            } else {

                System.out.println("SystemNMS: Db4oService null!");
            }
        }

        return (objectContainer);
    }

    /**
     * {@inheritDoc}
     */
    public Configuration[] getConfigurations() {

        Configuration[] result = null;

        ObjectContainer oc = getObjectContainer();
        if (oc != null) {

            ObjectSet<Configuration> os =
                oc.queryByExample(Configuration.class);
            if (os != null) {

                result = os.toArray(new Configuration[os.size()]);
                examineOnDemand(result);
                examineScheduler(result);
                Arrays.sort(result, new ConfigurationSortByName());
            }
        }

        return (result);
    }

    private Configuration findConfigurationBySource(String s,
        Configuration[] array) {

        Configuration result = null;

        if ((s != null) && (array != null)) {

            for (int i = 0; i < array.length; i++) {

                if (array[i].isSource(s)) {

                    result = array[i];
                    break;
                }
            }
        }

        return (result);
    }

    private void saveDefaultConfigurations() {

        Configuration def = getDefaultConfiguration();
        System.out.println("SystemNMS: def: " + def);
        save(def, false);
        setConfiguration(getConfigurationBySource(def.getSource()));
        System.out.println("SystemNMS: conf: " + getConfiguration());

        Scheduler s = getScheduler();
        if (s != null) {

            save(s.getDefaultConfiguration(), false);
        }

        Live l = getLive();
        if (l != null) {

            save(l.getDefaultConfiguration(), false);
        }

        PostProc pp = getPostProc();
        if (pp != null) {

            save(pp.getDefaultConfiguration(), false);
        }

        Recorder[] recorders = getRecorders();
        if (recorders != null) {

            for (int i = 0; i < recorders.length; i++) {

                save(recorders[i].getDefaultConfiguration(), false);
            }
        }

        ProgramData[] pds = getProgramData();
        if (pds != null) {

            for (int i = 0; i < pds.length; i++) {

                save(pds[i].getDefaultConfiguration(), false);
            }
        }

        Web[] webs = getWebs();
        if (webs != null) {

            for (int i = 0; i < webs.length; i++) {

                save(webs[i].getDefaultConfiguration(), false);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeConfiguration(Configuration c) {

        ObjectContainer oc = getObjectContainer();
        if ((c != null) && (oc != null)) {

            final String source = c.getSource();
            List<Configuration> confs =
                oc.query(new Predicate<Configuration>() {

                public boolean match(Configuration c) {
                    return (source.equals(c.getSource()));
                }
            });

            if (confs != null) {

                // We will delete them all but we should have only found 1.
                for (int i = 0; i < confs.size(); i++) {
                    oc.delete(confs.get(i));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void save(Configuration c, boolean force) {

        ObjectContainer oc = getObjectContainer();
        System.out.println("save: force: " + force + " " + c + " " + oc);
        if ((c != null) && (oc != null)) {

            BaseConfiguration newc =
                new BaseConfiguration((BaseConfiguration) c);
            final String source = newc.getSource();
            if (source != null) {

                List<Configuration> configs =
                    oc.query(new Predicate<Configuration>() {

                    public boolean match(Configuration c) {
                        return (source.equals(c.getSource()));
                    }
                });

                if (configs != null) {

                    boolean foundOldOne = (configs.size() > 0);
                    if ((foundOldOne) && (force)) {

                        // We will delete them all but we should have
                        // only found 1.
                        for (int i = 0; i < configs.size(); i++) {
                            oc.delete(configs.get(i));
                        }
                    }

                    if ((!foundOldOne) || (force)) {

                        System.out.println("we should save it...");
                        oc.store(newc);
                        oc.commit();
                        updateConfiguration(newc);
                    }
                }
            }
        }
    }

    private void updateConfiguration(Configuration c) {

        System.out.println("updateConfiguration c: " + c);
        if (c != null) {

            String name = c.getName();
            if (name != null) {

                if (name.equals(NMSConstants.NMS_NAME)) {

                    setConfiguration(c);

                } else if (name.equals(NMSConstants.SCHEDULER_NAME)) {

                    Scheduler s = getScheduler();
                    if (s != null) {

                        s.setConfiguration(c);
                    }

                } else if (name.equals(NMSConstants.LIVE_NAME)) {

                    Live l = getLive();
                    if (l != null) {

                        l.setConfiguration(c);
                    }

                } else if (name.equals(NMSConstants.POST_PROC_NAME)) {

                    PostProc pp = getPostProc();
                    if (pp != null) {

                        pp.setConfiguration(c);
                    }

                } else if (name.equals(NMSConstants.RECORDER_NAME)) {

                    String source = c.getSource();
                    if (source != null) {

                        Recorder[] array = getRecorders();
                        if (array != null) {

                            Recorder r = null;
                            for (int i = 0; i < array.length; i++) {

                                String tmp = array[i].getTitle() + " "
                                    + array[i].getDevice();
                                if (source.equals(tmp)) {

                                    r = array[i];
                                    break;
                                }
                            }

                            if (r != null) {
                                r.setConfiguration(c);
                            }
                        }
                    }

                } else if (name.equals(NMSConstants.PROGRAM_DATA_NAME)) {

                    String source = c.getSource();
                    if (source != null) {

                        ProgramData[] array = getProgramData();
                        if (array != null) {

                            ProgramData pd = null;
                            for (int i = 0; i < array.length; i++) {

                                if (source.equals(array[i].getTitle())) {

                                    pd = array[i];
                                    break;
                                }
                            }

                            if (pd != null) {
                                pd.setConfiguration(c);
                            }
                        }
                    }

                } else if (name.equals(NMSConstants.WEB_NAME)) {

                    String source = c.getSource();
                    if (source != null) {

                        Web[] array = getWebs();
                        if (array != null) {

                            Web web = null;
                            for (int i = 0; i < array.length; i++) {

                                if (source.equals(array[i].getTitle())) {

                                    web = array[i];
                                    break;
                                }
                            }

                            if (web != null) {
                                web.setConfiguration(c);
                            }
                        }
                    }
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

    /**
     * Close up all resources.
     */
    public void close() {

        if (objectContainer != null) {

            boolean result = objectContainer.close();
            System.out.println("SystemNMS: closed " + result);
            objectContainer = null;

        } else {

            System.out.println("SystemNMS: Tried to close "
                + "but objectContainer null.");
        }
    }

    private String[] getListingNames() {

        String[] result = null;

        ArrayList<String> l = new ArrayList<String>();
        l.add(NMSConstants.NOT_CONNECTED);

        ProgramData[] array = getProgramData();
        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                Listing[] lists = array[i].getListings();
                if (lists != null) {

                    for (int j = 0; j < lists.length; j++) {

                        l.add(lists[j].getName());
                    }
                }
            }

            result = l.toArray(new String[l.size()]);
        }

        return (result);
    }

    private void examineOnDemand(Configuration[] array) {

        if (array != null) {

            // We have to examine any OnDemand services that we
            // have deployed.  Since an OnDemand service takes
            // a fulltime Recorder we need give them a property
            // to link to one.
            OnDemand[] ods = getOnDemands();
            Recorder[] recs = getRecorders();
            if ((ods != null) && (recs != null)) {

                boolean changed = false;
                ArrayList<String> rlist = new ArrayList<String>();
                rlist.add(NMSConstants.NOT_CONNECTED);
                for (int i = 0; i < recs.length; i++) {
                    rlist.add(recs[i].getTitle() + " " + recs[i].getDevice());
                }

                for (int i = 0; i < ods.length; i++) {

                    Configuration c = ods[i].getConfiguration();
                    BaseConfiguration work = null;
                    for (int j = 0; j < array.length; j++) {

                        if (array[j].equals(c)) {

                            work = (BaseConfiguration) array[j];
                            break;
                        }
                    }

                    if (work != null) {

                        // We have the Configuration that we have to
                        // examine.  See if a Recorder property exists
                        // right now.
                        NameValue old = work.findNameValueByName(
                            NMSConstants.RECORDING_DEVICE);
                        if (old == null) {

                            // We need to make one.
                            NameValue nv = new NameValue();
                            nv.setName(NMSConstants.RECORDING_DEVICE);
                            nv.setDescription(
                                NMSConstants.RECORDING_DEVICE);
                            nv.setValue(NMSConstants.NOT_CONNECTED);
                            nv.setDefaultValue(NMSConstants.NOT_CONNECTED);
                            nv.setType(nv.toType("STRING_FROM_CHOICE_TYPE"));
                            nv.setChoices(
                                rlist.toArray(new String[rlist.size()]));
                            work.addNameValue(nv);
                            changed = true;

                        } else {

                            // We have an old one but we may need to change
                            // the Recorder selection.
                            String value = old.getValue();
                        }

                        if (changed) {

                            save(work, true);
                        }
                    }
                }
            }
        }
    }

    private void examineScheduler(Configuration[] array) {

        if (array != null) {

            // We have to make sure that the Scheduler has the proper
            // configuration data for the ProgramData and Recorder
            // instances.  The user has to connect these in some way
            // and there is no way the default configuration can preset
            // them.  So we do a little work here to try to maintain
            // the Scheduler Configuration matches the current state.

            Configuration c =
                findConfigurationBySource(NMSConstants.SCHEDULER_SOURCE, array);
            if (c != null) {

                BaseConfiguration bc = (BaseConfiguration) c;

                // This will be at least one item called "Not Connected"
                String[] listings = getListingNames();

                boolean changed = false;
                Recorder[] records = getRecorders();
                if ((records != null) && (listings != null)) {

                    for (int i = 0; i < records.length; i++) {

                        Configuration rc =
                            findConfigurationBySource(records[i].getTitle()
                                + " " + records[i].getDevice(), array);

                        if (rc != null) {

                            NameValue old =
                                c.findNameValueByName(rc.getSource());
                            if (old == null) {

                                // We need to create one.
                                NameValue nv = new NameValue();
                                nv.setName(rc.getSource());
                                nv.setDescription(
                                    NMSConstants.RECORDING_DEVICE);
                                nv.setValue(listings[0]);
                                nv.setDefaultValue(listings[0]);
                                nv.setType(
                                    nv.toType("STRING_FROM_CHOICE_TYPE"));
                                nv.setChoices(listings);
                                bc.addNameValue(nv);
                                changed = true;

                            } else {

                                if (!Arrays.equals(old.getChoices(),
                                    listings)) {

                                    old.setChoices(listings);
                                    changed = true;
                                }
                            }
                        }
                    }
                }

                if (changed) {

                    save(bc, true);
                }
            }
        }
    }

    // This method is trying to delete old "connections" between a recorder
    // and program data.  If the user removes a recorder or changes the
    // program service (less likely) then these properties hang out in the
    // configuration.  As the system boots they are added if need be but
    // eventually they need to be deleted if no longer valid.  It would be
    // nice to do this automatically but we cannot do it until all services
    // have started.  So we have to figure out the best way to go about
    // calling this....
    private void schedulerConnectCheck(Configuration c) {

        if (c != null) {

            BaseConfiguration bc = (BaseConfiguration) c;

            // Now lets remove any old connections if they no longer
            // are valid.  This will clean up some stuff when recorders
            // are removed.
            NameValue[] all = bc.getNameValues();
            if (all != null) {

                boolean connectchanged = false;
                ArrayList<NameValue> good = new ArrayList<NameValue>();
                for (int i = 0; i < all.length; i++) {

                    String name = all[i].getName();
                    if (name.equals(NMSConstants.RECORDING_DIRECTORIES)) {

                        good.add(all[i]);

                    } else {

                        name = name.substring(name.indexOf(" "));
                        name = name.trim();

                        // OK name should be just the device name.
                        if ((!name.endsWith("null"))
                            && (getRecorderByDevice(name) != null)) {

                            good.add(all[i]);

                        } else {

                            connectchanged = true;
                        }
                    }
                }

                if (connectchanged) {

                    if (good.size() > 0) {

                        bc.setNameValues(good.toArray(
                            new NameValue[good.size()]));

                    } else {

                        bc.setNameValues(null);
                    }

                    save(bc, true);
                }
            }
        }
    }

    static class ConfigurationSortByName implements Comparator<Configuration>,
        Serializable {

        public int compare(Configuration c0, Configuration c1) {

            return (c0.getName().compareTo(c1.getName()));
        }
    }

}


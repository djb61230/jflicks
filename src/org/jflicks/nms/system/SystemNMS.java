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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.jflicks.autoart.AutoArt;
import org.jflicks.configure.BaseConfiguration;
import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.nms.BaseNMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.photomanager.PhotoManager;
import org.jflicks.trailer.Trailer;
import org.jflicks.tv.Listing;
import org.jflicks.tv.live.Live;
import org.jflicks.tv.ondemand.OnDemand;
import org.jflicks.tv.postproc.PostProc;
import org.jflicks.tv.programdata.ProgramData;
import org.jflicks.tv.recorder.Recorder;
import org.jflicks.tv.scheduler.Scheduler;
import org.jflicks.util.ExtensionsFilter;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;
import org.jflicks.videomanager.VideoManager;

import fi.iki.elonen.SimpleWebServer;

/**
 * This is our implementation of an NMS.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SystemNMS extends BaseNMS {

    private SimpleWebServer simpleWebServer;

    /**
     * Default empty constructor.
     */
    public SystemNMS() {

        setTitle("SystemNMS");
        saveDefaultConfigurations();
    }

    public void startWebServer() {

        if (simpleWebServer == null) {

            int port = getConfiguredStreamPort();
            ArrayList<File> rootDirs= new ArrayList<File>();
            String[] paths = getConfiguredStreamPaths();
            if ((paths != null) && (paths.length > 0)) {

                for (int i = 0; i < paths.length; i++) {

                    rootDirs.add(new File(paths[i]));
                }
            }

            simpleWebServer = new SimpleWebServer(null, port, rootDirs, false);
            try {

                simpleWebServer.start();

            } catch (Exception ex) {

                LogUtil.log(LogUtil.INFO, "simpleWebServer problem: " + ex.getMessage());
                simpleWebServer = null;
            }
        }
    }

    public void stopWebServer() {

        if (simpleWebServer != null) {

            simpleWebServer.stop();
            simpleWebServer = null;
        }
    }

    private File createConfigurationFile(Configuration c) {

        File result = null;

        if (c != null) {

            File db = new File("db");
            if ((db.exists()) && (db.isDirectory())) {

                String s = c.getName() + "-" + c.getSource() + ".properties";

                // We have to do something with path / chars.  Crap.  We
                // will just have to turn into something odd...
                s = s.replaceAll("/", "SLASH");
                result = new File(db, s);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public Configuration[] getConfigurations() {

        Configuration[] result = null;

        File db = new File("db");
        if ((db.exists()) && (db.isDirectory())) {

            String[] props = {
                "properties"
            };

            ExtensionsFilter exts = new ExtensionsFilter(props);
            File[] fprops = db.listFiles(exts);
            if ((fprops != null) && (fprops.length > 0)) {

                ArrayList<Configuration> l = new ArrayList<Configuration>();
                for (int i = 0; i < fprops.length; i++) {

                    Configuration c =
                        fromProperties(Util.findProperties(fprops[i]));
                    if (c != null) {

                        l.add(c);
                    }
                }

                if (l.size() > 0) {

                    result = l.toArray(new Configuration[l.size()]);
                    examineOnDemand(result);
                    examineScheduler(result);
                    Arrays.sort(result, new ConfigurationSortByName());
                }
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
        LogUtil.log(LogUtil.DEBUG, "SystemNMS: def: " + def);
        save(def, false);
        setConfiguration(getConfigurationBySource(def.getSource()));
        LogUtil.log(LogUtil.DEBUG, "SystemNMS: conf: " + getConfiguration());

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

        OnDemand[] ons = getOnDemands();
        if (ons != null) {

            for (int i = 0; i < ons.length; i++) {

                save(ons[i].getDefaultConfiguration(), false);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeConfiguration(Configuration c) {

        File f = createConfigurationFile(c);
        if ((f != null) && (f.exists()) && (f.isFile())) {

            boolean result = f.delete();
            if (!result) {

                LogUtil.log(LogUtil.WARNING, "Failed to delete " + f.getPath());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void save(Configuration c, boolean force) {

        LogUtil.log(LogUtil.DEBUG, "save: c: " + c + " force: " + force);
        if (c != null) {

            File f = createConfigurationFile(c);
            LogUtil.log(LogUtil.DEBUG, "save: f: " + f);
            if ((f != null) && (f.exists()) && (f.isFile())) {

                // The Configuration does exist.  We over write only if
                // force is true.
                if (force) {

                    Util.writeProperties(f, toProperties(c));
                    updateConfiguration(c);
                }

            } else if (f != null) {

                // Ok we have a good path.  We will save it anyway.
                Util.writeProperties(f, toProperties(c));
                updateConfiguration(c);
            }
        }
    }

    private void updateConfiguration(Configuration c) {

        LogUtil.log(LogUtil.DEBUG, "updateConfiguration c: " + c);
        if (c != null) {

            String name = c.getName();
            if (name != null) {

                if (name.equals(NMSConstants.NMS_NAME)) {

                    setConfiguration(c);
                    stopWebServer();
                    startWebServer();

                } else if (name.equals(NMSConstants.SCHEDULER_NAME)) {

                    Scheduler s = getScheduler();
                    if (s != null) {

                        s.setConfiguration(c);
                    }

                } else if (name.equals(NMSConstants.PHOTO_MANAGER_NAME)) {

                    PhotoManager pm = getPhotoManager();
                    if (pm != null) {

                        pm.setConfiguration(c);
                    }

                } else if (name.equals(NMSConstants.VIDEO_MANAGER_NAME)) {

                    VideoManager vm = getVideoManager();
                    if (vm != null) {

                        vm.setConfiguration(c);
                    }

                } else if (name.equals(NMSConstants.AUTO_ART_NAME)) {

                    AutoArt aa = getAutoArt();
                    if (aa != null) {

                        aa.setConfiguration(c);
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

                } else if (name.equals(NMSConstants.ON_DEMAND_NAME)) {

                    String source = c.getSource();
                    if (source != null) {

                        OnDemand[] array = getOnDemands();
                        if (array != null) {

                            OnDemand od = null;
                            for (int i = 0; i < array.length; i++) {

                                if (source.equals(array[i].getTitle())) {

                                    od = array[i];
                                    break;
                                }
                            }

                            if (od != null) {
                                od.setConfiguration(c);
                            }
                        }
                    }

                } else if (name.equals(NMSConstants.TRAILER_NAME)) {

                    String source = c.getSource();
                    if (source != null) {

                        Trailer[] array = getTrailers();
                        if (array != null) {

                            Trailer t = null;
                            for (int i = 0; i < array.length; i++) {

                                if (source.equals(array[i].getTitle())) {

                                    t = array[i];
                                    break;
                                }
                            }

                            if (t != null) {
                                t.setConfiguration(c);
                            }
                        }
                    }

                } else {

                    LogUtil.log(LogUtil.WARNING, "Not handling update of "
                        + c.getName() + "-" + c.getSource());
                }
            }
        }
    }

    /**
     * Close up all resources.
     */
    public void close() {
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


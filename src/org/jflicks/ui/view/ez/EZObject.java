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
package org.jflicks.ui.view.ez;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import org.jflicks.configure.Configuration;
import org.jflicks.configure.NameValue;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Task;
import org.jflicks.util.Util;

/**
 * Simple class to collect and maintain data from a set of Configuration
 * instances.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class EZObject implements Serializable {

    private int maxJobs;
    private int updateTimeInMinutes;
    private String userName;
    private String password;
    private String[] listingNames;
    private EZRecorder[] recorders;
    private EZIndexer[] indexers;

    /**
     * Constructor using an EZObject instance to copy.
     *
     * @param eobj An EZObject instance.
     */
    public EZObject(EZObject eobj) {

        if (eobj != null) {

            setMaxJobs(eobj.getMaxJobs());
            setUpdateTimeInMinutes(eobj.getUpdateTimeInMinutes());
            setUserName(eobj.getUserName());
            setPassword(eobj.getPassword());

            String[] lnames = eobj.getListingNames();
            if (lnames != null) {

                String[] nlnames = new String[lnames.length];
                for (int i = 0; i < nlnames.length; i++) {

                    nlnames[i] = new String(lnames[i]);
                }

                setListingNames(nlnames);
            }

            EZRecorder[] recs = eobj.getRecorders();
            if (recs != null) {

                EZRecorder[] nrecs = new EZRecorder[recs.length];
                for (int i = 0; i < nrecs.length; i++) {

                    nrecs[i] = new EZRecorder(recs[i]);
                }

                setRecorders(nrecs);
            }

            EZIndexer[] inds = eobj.getIndexers();
            if (inds != null) {

                EZIndexer[] ninds = new EZIndexer[inds.length];
                for (int i = 0; i < ninds.length; i++) {

                    ninds[i] = new EZIndexer(inds[i]);
                }

                setIndexers(ninds);
            }
        }
    }

    /**
     * Constructor using Configuration instances to initialize.
     *
     * @param array An array of Configuration instances.
     * @param tarray An array of Task instances.
     */
    public EZObject(Configuration[] array, Task[] tarray) {

        if (tarray != null) {

            ArrayList<EZIndexer> elist = new ArrayList<EZIndexer>();

            // Make a dummy one since one does not need to be set.
            EZIndexer ind = new EZIndexer();
            ind.setTitle("");
            ind.setDescription("");
            elist.add(ind);
            for (int i = 0; i < tarray.length; i++) {

                System.out.println("poop: " + tarray[i].isIndexer());
                if (tarray[i].isIndexer()) {

                    ind = new EZIndexer();
                    ind.setTitle(tarray[i].getTitle());
                    ind.setDescription(tarray[i].getDescription());
                    elist.add(ind);
                }
            }

            if (elist.size() > 0) {

                setIndexers(elist.toArray(new EZIndexer[elist.size()]));
            }
        }

        if (array != null) {

            Configuration c =
                findConfigurationBySource(array, "System Post Proc");
            if (c != null) {

                NameValue nv =
                    c.findNameValueByName(NMSConstants.POST_PROC_MAXIMUM_JOBS);
                if (nv != null) {

                    setMaxJobs(Integer.parseInt(nv.getValue()));
                }
            }

            c = findConfigurationBySource(array, "System Auto Art");
            if (c != null) {

                NameValue nv =
                    c.findNameValueByName(NMSConstants.UPDATE_TIME_IN_MINUTES);
                if (nv != null) {

                    setUpdateTimeInMinutes(Integer.parseInt(nv.getValue()));
                }
            }

            c = findConfigurationBySource(array, "Schedules Direct");
            if (c != null) {

                NameValue nv = c.findNameValueByName(NMSConstants.USER_NAME);
                if (nv != null) {

                    setUserName(nv.getValue());
                }

                nv = c.findNameValueByName(NMSConstants.PASSWORD);
                if (nv != null) {

                    setPassword(nv.getValue());
                }
            }

            Configuration[] recs =
                findConfigurationByName(array, NMSConstants.RECORDER_NAME);
            String recsource = null;
            if (recs != null) {

                EZRecorder[] rnames = new EZRecorder[recs.length];
                for (int i = 0; i < rnames.length; i++) {

                    rnames[i] = new EZRecorder();
                    rnames[i].setName(recs[i].getSource());
                    NameValue inv = recs[i].findNameValueByName(
                        NMSConstants.RECORDING_INDEXER_NAME);
                    if (inv != null) {
                        rnames[i].setIndexer(getIndexerByTitle(inv.getValue()));
                    }
                    rnames[i].setListingName(getListingName(array,
                        rnames[i].getName()));

                    inv = recs[i].findNameValueByName(
                        NMSConstants.CUSTOM_CHANNEL_LIST_TYPE);
                    if (inv != null) {
                        rnames[i].setListType(inv.getValue());
                    }

                    inv = recs[i].findNameValueByName(
                        NMSConstants.CUSTOM_CHANNEL_LIST);
                    if (inv != null) {
                        rnames[i].setChannelList(inv.valueToArray());
                    }

                    if (i == 0) {
                        recsource = rnames[i].getName();
                    }
                }
                Arrays.sort(rnames);
                setRecorders(rnames);
            }

            System.out.println("recsource: " + recsource);
            if (recsource != null) {

                setListingNames(getListingNames(array, recsource, true));
            }
        }
    }

    public int getMaxJobs() {
        return (maxJobs);
    }

    public void setMaxJobs(int i) {
        maxJobs = i;
    }

    public int getUpdateTimeInMinutes() {
        return (updateTimeInMinutes);
    }

    public void setUpdateTimeInMinutes(int i) {
        updateTimeInMinutes = i;
    }

    public String getUserName() {
        return (userName);
    }

    public void setUserName(String s) {
        userName = s;
    }

    public String getPassword() {
        return (password);
    }

    public void setPassword(String s) {
        password = s;
    }

    public EZRecorder[] getRecorders() {
        return (recorders);
    }

    public void setRecorders(EZRecorder[] array) {
        recorders = array;
    }

    public EZIndexer[] getIndexers() {
        return (indexers);
    }

    public void setIndexers(EZIndexer[] array) {
        indexers = array;
    }

    public String[] getListingNames() {
        return (listingNames);
    }

    public void setListingNames(String[] array) {
        listingNames = array;
    }

    private EZIndexer getIndexerByTitle(String s) {

        EZIndexer result = null;

        EZIndexer[] array = getIndexers();
        if ((s != null) && (array != null)) {

            for (int i = 0; i < array.length; i++) {

                if (s.equals(array[i].getTitle())) {

                    result = array[i];
                    break;
                }
            }
        }

        return (result);
    }

    private Configuration findConfigurationBySource(Configuration[] array,
        String source) {

        Configuration result = null;

        if ((array != null) && (source != null)) {

            for (int i = 0; i < array.length; i++) {

                if (source.equals(array[i].getSource())) {

                    result = array[i];
                    break;
                }
            }
        }

        return (result);
    }

    private String[] getListingNames(Configuration[] array, String source,
        boolean justreal) {

        String[] result = null;

        if (source != null) {

            Configuration c =
                findConfigurationBySource(array, "Scheduler System");
            if (c != null) {

                NameValue nv = c.findNameValueByName(source);
                if (nv != null) {

                    String[] all = nv.getChoices();
                    if (justreal) {

                        if ((all != null) && (all.length > 1)) {

                            result = new String[all.length - 1];
                            for (int i = 0; i < result.length; i++) {

                                result[i] = all[i + 1];
                            }
                        }

                    } else {

                        result = all;
                    }
                }
            }
        }

        return (result);
    }

    private String getListingName(Configuration[] array, String source) {

        String result = null;

        if (source != null) {

            Configuration c =
                findConfigurationBySource(array, "Scheduler System");
            if (c != null) {

                NameValue nv = c.findNameValueByName(source);
                if (nv != null) {

                    result = nv.getValue();
                }
            }
        }

        return (result);
    }

    private Configuration[] findConfigurationByName(Configuration[] array,
        String name) {

        Configuration[] result = null;

        if ((array != null) && (name != null)) {

            ArrayList<Configuration> l = new ArrayList<Configuration>();
            for (int i = 0; i < array.length; i++) {

                if (name.equals(array[i].getName())) {

                    l.add(array[i]);
                }
            }

            if (l.size() > 0) {

                result = l.toArray(new Configuration[l.size()]);
            }
        }

        return (result);
    }

    /**
     * The standard hashcode override.
     *
     * @return An int value.
     */
    public int hashCode() {

        StringBuilder sb = new StringBuilder();
        sb.append(getMaxJobs());
        sb.append(getUpdateTimeInMinutes());
        sb.append(getUserName());
        sb.append(getPassword());
        String[] lnames = getListingNames();
        if (lnames != null) {

            for (int i = 0; i < lnames.length; i++) {
                sb.append(lnames[i]);
            }
        }

        EZRecorder[] recs = getRecorders();
        if (recs != null) {

            for (int i = 0; i < recs.length; i++) {
                sb.append(recs[i].toString());
            }
        }

        return (sb.toString().hashCode());
    }

    /**
     * The equals override method.
     *
     * @param o A gven object to check.
     * @return True if the objects are equal.
     */
    public boolean equals(Object o) {

        boolean result = false;

        if (o == this) {

            result = true;

        } else if (!(o instanceof EZObject)) {

            result = false;

        } else {

            EZObject eobj = (EZObject) o;
            if ((getUpdateTimeInMinutes() == eobj.getUpdateTimeInMinutes())
                && (getMaxJobs() == eobj.getMaxJobs())) {

                String name = getUserName();
                if (name != null) {

                    result = name.equals(eobj.getUserName());

                } else {

                    result = eobj.getUserName() == null;
                }

                if (result) {

                    String pw = getPassword();
                    if (pw != null) {

                        result = pw.equals(eobj.getPassword());

                    } else {

                        result = eobj.getPassword() == null;
                    }

                    if (result) {

                        String[] lnames = getListingNames();
                        if (lnames != null) {

                            String[] complnames = eobj.getListingNames();
                            if ((complnames != null)
                                && (complnames.length == lnames.length)) {

                                for (int i = 0; i < lnames.length; i++) {

                                    if (!lnames[i].equals(complnames[i])) {

                                        result = false;
                                        break;
                                    }
                                }

                            } else {

                                result = false;
                            }

                        } else {

                            result = eobj.getListingNames() == null;
                        }

                        if (result) {

                            EZRecorder[] recs = getRecorders();
                            if (recs != null) {

                                EZRecorder[] comprecs = eobj.getRecorders();
                                if ((comprecs != null)
                                    && (comprecs.length == recs.length)) {

                                    for (int i = 0; i < recs.length; i++) {

                                        if (!recs[i].equals(comprecs[i])) {

                                            result = false;
                                            break;
                                        }
                                    }

                                } else {

                                    result = false;
                                }

                            } else {

                                result = eobj.getRecorders() == null;
                            }
                        }
                    }
                }
            }
        }

        return (result);
    }

}

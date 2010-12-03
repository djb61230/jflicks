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
package org.jflicks.tv.recorder.v4l2;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.jflicks.configure.NameValue;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.util.Util;

/**
 * This job finds the defined control configuration for a V4l2 device.  It
 * uses the v4l2-ctl program to find these control items for any particular
 * device.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ListControlMenuJob extends BaseV4l2Job {

    private ArrayList<NameValue> nameValueList;

    /**
     * Simple no argument constructor.
     */
    public ListControlMenuJob() {

        setNameValueList(new ArrayList<NameValue>());
    }

    private ArrayList<NameValue> getNameValueList() {
        return (nameValueList);
    }

    private void setNameValueList(ArrayList<NameValue> l) {
        nameValueList = l;
    }

    private void addNameValue(NameValue nv) {

        ArrayList<NameValue> l = getNameValueList();
        if ((l != null) && (nv != null)) {

            l.add(nv);
        }
    }

    private void clearNameValues() {

        ArrayList<NameValue> l = getNameValueList();
        if (l != null) {
            l.clear();
        }
    }

    /**
     * The output is available in a set of NameValue instances.
     *
     * @return An array of NameValue objects.
     */
    public NameValue[] getNameValues() {

        NameValue[] result = null;

        ArrayList<NameValue> l = getNameValueList();
        if ((l != null) && (l.size() > 0)) {

            result = l.toArray(new NameValue[l.size()]);
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        clearNameValues();
        setTerminate(false);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        SystemJob job = SystemJob.getInstance("v4l2-ctl -d " + getDevice()
            + " --list-ctrls-menus");
        setSystemJob(job);
        job.addJobListener(this);
        JobContainer jc = JobManager.getJobContainer(job);
        setJobContainer(jc);
        jc.start();

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());
        }

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
        JobContainer jc = getJobContainer();
        if (jc != null) {

            jc.stop();
            setJobContainer(null);
        }
    }

    private String stripType(String s) {

        String result = s;

        if (s != null) {

            int index = s.indexOf("(");
            if (index != -1) {

                result = result.substring(0, index);
                result = result.trim();
            }
        }

        return (result);
    }

    private NameValue parseBooleanField(String tag, String value) {

        NameValue result = null;

        if ((tag != null) && (value != null)) {

            result = new NameValue();
            String name = stripType(tag);
            result.setName(name);
            result.setDescription(name);
            result.setType(NameValue.BOOLEAN_TYPE);

            int count = 0;
            StringTokenizer st = new StringTokenizer(value, "= ");
            while (st.hasMoreTokens()) {

                String token = st.nextToken();
                token = token.trim();
                if ((count % 2) == 0) {

                    // We expect a name.
                    tag = token;

                } else {

                    // We expect a value which goes with the previous tag.
                    if (tag.equals("default")) {
                        result.setDefaultValue(token);
                    } else if (tag.equals("value")) {
                        result.setValue(token);
                    }
                }

                count++;
            }
        }

        return (result);
    }

    private NameValue parseIntegerField(String tag, String value) {

        NameValue result = null;

        if ((tag != null) && (value != null)) {

            result = new NameValue();
            String name = stripType(tag);
            result.setName(name);
            result.setDescription(name);
            result.setType(NameValue.INTEGER_TYPE);

            int count = 0;
            StringTokenizer st = new StringTokenizer(value, "= ");
            while (st.hasMoreTokens()) {

                String token = st.nextToken();
                token = token.trim();
                if ((count % 2) == 0) {

                    // We expect a name.
                    tag = token;

                } else {

                    // We expect a value which goes with the previous tag.
                    if (tag.equals("min")) {
                        result.setMin(Util.str2int(token, 0));
                    } else if (tag.equals("max")) {
                        result.setMax(Util.str2int(token, 0));
                    } else if (tag.equals("step")) {
                        result.setStep(Util.str2int(token, 0));
                    } else if (tag.equals("default")) {
                        result.setDefaultValue(token);
                    } else if (tag.equals("value")) {
                        result.setValue(token);
                    }
                }

                count++;
            }
        }

        return (result);
    }

    private NameValue parseMenuField(String tag, String value, String[] array) {

        NameValue result = null;

        if ((tag != null) && (value != null)) {

            result = new NameValue();
            String name = stripType(tag);
            result.setName(name);
            result.setDescription(name);
            result.setType(NameValue.STRING_FROM_CHOICE_TYPE);

            int defaultIndex = 0;
            int valueIndex = 0;
            int count = 0;
            int min = 0;
            int max = 0;
            StringTokenizer st = new StringTokenizer(value, "= ");
            while (st.hasMoreTokens()) {

                String token = st.nextToken();
                token = token.trim();
                if ((count % 2) == 0) {

                    // We expect a name.
                    tag = token;

                } else {

                    // We expect a value which goes with the previous tag.
                    if (tag.equals("default")) {

                        defaultIndex = Util.str2int(token, defaultIndex);
                        result.setDefaultValue("" + defaultIndex);

                    } else if (tag.equals("value")) {

                        valueIndex = Util.str2int(token, valueIndex);
                        result.setValue("" + valueIndex);

                    } else if (tag.equals("min")) {

                        min = Util.str2int(token, min);

                    } else if (tag.equals("max")) {

                        max = Util.str2int(token, max);
                    }
                }

                count++;
            }

            // We should have valid indices for default and value at this
            // point.
            if (array != null) {

                result.setChoices(array);
                if ((defaultIndex >= 0) && (defaultIndex < array.length)) {
                    result.setDefaultValue(array[defaultIndex]);
                }
                if ((valueIndex >= 0) && (valueIndex < array.length)) {
                    result.setValue(array[valueIndex]);
                }

            } else {

                array = new String[2];
                array[0] = "" + min;
                array[1] = "" + max;
                result.setChoices(array);
            }
        }

        return (result);
    }

    private boolean isIntTag(String tag) {

        boolean result = false;

        if (tag != null) {
            result = tag.indexOf("(int)") != -1;
        }

        return (result);
    }

    private boolean isBoolTag(String tag) {

        boolean result = false;

        if (tag != null) {
            result = tag.indexOf("(bool)") != -1;
        }

        return (result);
    }

    private boolean isMenuTag(String tag) {

        boolean result = false;

        if (tag != null) {
            result = tag.indexOf("(menu)") != -1;
        }

        return (result);
    }

    private boolean isMenuChoice(String tag) {

        boolean result = false;

        if (tag != null) {

            int index = tag.indexOf(":");
            if (index != -1) {

                tag = tag.substring(0, tag.indexOf(":"));
                try {

                    Integer.parseInt(tag);
                    result = true;

                } catch (NumberFormatException ex) {
                    result = false;
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            SystemJob job = getSystemJob();
            if (job != null) {

                String output = job.getOutputText();
                setJobContainer(null);
                if (output != null) {

                    ArrayList<String> choices = new ArrayList<String>();
                    String[] array = output.split("\n");
                    if (array != null) {

                        for (int i = 0; i < array.length; i++) {

                            String line = array[i];
                            line = line.trim();
                            int index = line.indexOf(":") + 1;

                            if (index != 0) {

                                String tag = line.substring(0, index - 1);
                                if (tag != null) {
                                    tag = tag.trim();
                                }

                                String val = line.substring(index);
                                if (val != null) {
                                    val = val.trim();
                                }

                                // We do have a tag/value pair.  We check if
                                // the tag is a "int", "bool" or "menu".
                                if (isIntTag(tag)) {
                                    addNameValue(parseIntegerField(tag, val));
                                } else if (isBoolTag(tag)) {
                                    addNameValue(parseBooleanField(tag, val));
                                } else if (isMenuTag(tag)) {

                                    // We have to get the next lines which
                                    // represent the menu choices.
                                    choices.clear();
                                    for (int j = i + 1; j < array.length; j++) {

                                        String smenu = array[j].trim();
                                        if (isMenuChoice(smenu)) {

                                            smenu = smenu.substring(
                                                smenu.indexOf(":") + 1);
                                            smenu = smenu.trim();
                                            choices.add(smenu);

                                        } else {

                                            break;
                                        }
                                    }

                                    // Here we have all the choices...
                                    String[] ary = null;

                                    if (choices.size() > 0) {

                                        ary = choices.toArray(
                                            new String[choices.size()]);
                                    }
                                    addNameValue(parseMenuField(tag, val, ary));

                                    // Now skip the choices...
                                    i += choices.size();
                                }
                            }
                        }
                    }
                }

                stop();
            }
        }
    }

}

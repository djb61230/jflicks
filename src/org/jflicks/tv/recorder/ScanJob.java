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
package org.jflicks.tv.recorder;

import java.util.HashMap;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ScanJob extends BaseDeviceJob {

    private static final String SERVICE_LINE = "service is running";
    private static final String CHANNEL_TEXT = "Channel number:";
    private static final String NAME_TEXT = "Name: '";
    private static final String VSB_BAD_TEXT = "VSB_8";
    private static final String VSB_GOOD_TEXT = "8VSB";

    private String[] args;
    private String fileText;

    /**
     * Simple no argument constructor.
     *
     * @param args The arguments to use.
     */
    public ScanJob(String[] args) {

        setArgs(args);
    }

    /**
     * Can't do much work with out arguments to run as a system job.
     *
     * @return An array of String instances.
     */
    public String[] getArgs() {
        return (args);
    }

    private void setArgs(String[] array) {
        args = array;
    }

    private String ensurePrintable(String s) {

        String result = s;

        if (result != null) {

            result = result.trim();
            char[] chars = result.toCharArray();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < chars.length; i++) {

                if ((Character.isLetterOrDigit(chars[i]))
                    || (Character.isSpaceChar(chars[i]))
                    || (chars[i] == '.')
                    || (chars[i] == ':')
                    || (chars[i] == '-')
                    || (chars[i] == '_')) {

                    if (chars[i] == ':') {
                        sb.append(".");
                    } else {
                        sb.append(chars[i]);
                    }
                }
            }

            result = sb.toString();
        }

        return (result);
    }

    private String parseChannel(String s) {

        String result = null;

        if (s != null) {

            int index = s.indexOf(CHANNEL_TEXT);
            if (index != -1) {

                index += CHANNEL_TEXT.length();
                int lastIndex = s.indexOf(".", index);
                if (lastIndex != -1) {

                    result = s.substring(index, lastIndex);
                    if (result != null) {

                        result = ensurePrintable(result);
                    }
                }
            }
        }

        return (result);
    }

    private String parseName(String s) {

        String result = null;

        if (s != null) {

            int index = s.indexOf(NAME_TEXT);
            if (index != -1) {

                index += NAME_TEXT.length();
                int lastIndex = s.lastIndexOf("'");
                if ((lastIndex != -1) && (lastIndex > index)) {

                    result = s.substring(index, lastIndex);
                    if (result != null) {

                        result = ensurePrintable(result);
                    }

                } else {

                    // This is a hack as a name sometimes doesn't
                    // have an ending tick.  So just take the rest...
                    result = ensurePrintable(s.substring(index + 1));
                }
            }
        }

        return (result);
    }

    /**
     * Convenience method to parse out the output from the scan program
     * and format the text so it is proper for our use.
     *
     * @return Text usable to write channel config to a conf file.
     */
    public String getFileText() {

        if (fileText == null) {

            SystemJob job = getSystemJob();
            if (job != null) {

                String output = job.getOutputText();
                if (output != null) {

                    String[] array = output.split("\n");
                    if ((array != null) && (array.length > 0)) {

                        StringBuilder sb = new StringBuilder();

                        boolean dumpMode = false;
                        HashMap<String, String> hm =
                            new HashMap<String, String>();
                        for (int i = 0; i < array.length; i++) {

                            if (array[i].startsWith("dumping")) {

                                dumpMode = true;
                                continue;
                            }

                            if (dumpMode) {

                                if (array[i].indexOf(VSB_BAD_TEXT) != -1) {

                                    array[i] = array[i].replaceAll(VSB_BAD_TEXT,
                                        VSB_GOOD_TEXT);
                                }

                                int index = array[i].indexOf(":");
                                if (index != -1) {

                                    String name = array[i].substring(0, index);
                                    name = ensurePrintable(name);
                                    String rest = array[i].substring(index);

                                    String channel = hm.get(name);
                                    if (channel != null) {

                                        sb.append(channel);
                                        sb.append(rest);
                                        sb.append("\n");
                                    }
                                }

                            } else {

                                // We are at the beginning and we look for
                                // lines that "define" a channel number by
                                // it's name - we need to substitute it later...
                                if (array[i].startsWith(SERVICE_LINE)) {

                                    String channel = parseChannel(array[i]);
                                    String name = parseName(array[i]);
                                    if ((channel != null) && (name != null)) {

                                        hm.put(name, channel);
                                    }
                                }
                            }
                        }

                        if (sb.length() > 0) {

                            fileText = sb.toString();
                        }
                    }
                }
            }
        }

        return (fileText);
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

        SystemJob job = null;
        String[] myargs = getArgs();
        if ((myargs != null) && (args.length > 0)) {

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < myargs.length; i++) {

                if (i > 0) {

                    sb.append(" ");
                }

                sb.append(args[i]);
            }
            job = SystemJob.getInstance(sb.toString());
            fireJobEvent(JobEvent.UPDATE, "command:<" + job.getCommand() + ">");
            setSystemJob(job);
            job.addJobListener(this);
            JobContainer jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            jc.start();

        } else {

            setTerminate(true);
        }

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

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            SystemJob job = getSystemJob();
            if (job != null) {

                fireJobEvent(JobEvent.UPDATE, "ProgramJob: exit: "
                    + job.getExitValue());
                getFileText();
                stop();
            }

        } else {

            fireJobEvent(JobEvent.UPDATE, event.getMessage());
        }
    }

    /**
     * Test main method.
     *
     * @param args Arguments to run as a system job.
     */
    public static void main(String[] args) {

        ScanJob job = new ScanJob(args);
        JobContainer jc = JobManager.getJobContainer(job);
        jc.start();
    }

}

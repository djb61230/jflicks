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
package org.jflicks.job;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.jflicks.util.Util;

/**
 * This class will run any system executable.  It uses Input and Output jobs
 * to monitor and control stdin and stdout from the system process.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class SystemJob extends AbstractJob implements JobListener {

    private String shell;
    private String shellSwitch;
    private String command;
    private File working;
    private String outputText;

    private Process process;
    private int exitValue;
    private InputStreamJob inputStreamJob;
    private JobContainer inputJobContainer;
    private OutputStreamJob outputStreamJob;
    private JobContainer outputJobContainer;

    private SystemJob() {
    }

    private SystemJob(String shell, String shellSwitch, String command,
        File working) {

        setShell(shell);
        setShellSwitch(shellSwitch);
        setCommand(command);
        setWorking(working);
    }

    /**
     * We control the instances of these jobs because they are configured
     * differently based upon platform.  This insulates the user from
     * worrying about these details.
     *
     * @param command The command line arguments (including program name).
     * @return A SystemJob instance that can be controlled with a JobContainer.
     */
    public static SystemJob getInstance(String command) {

        SystemJob result = null;

        boolean win = false;
        if (Util.isWindows()) {
            result = new SystemJob("cmd.exe", "/C", command, null);
        } else {
        //} else if (Util.isLinux()) {
            result = new SystemJob("/bin/bash", "-c", command, null);
        }

        return (result);
    }

    /**
     * We control the instances of these jobs because they are configured
     * differently based upon platform.  This insulates the user from
     * worrying about these details.
     *
     * @param command The command line arguments (including program name).
     * @return A SystemJob instance that can be controlled with a JobContainer.
     */
    public static SystemJob getInstance(String command, File working) {

        SystemJob result = null;

        boolean win = false;
        if (Util.isWindows()) {
            result = new SystemJob("cmd.exe", "/C", command, working);
        } else {
        //} else if (Util.isLinux()) {
            result = new SystemJob("/bin/bash", "-c", command, working);
        }

        return (result);
    }

    private String getShell() {
        return (shell);
    }

    private void setShell(String s) {
        shell = s;
    }

    private String getShellSwitch() {
        return (shellSwitch);
    }

    private void setShellSwitch(String s) {
        shellSwitch = s;
    }

    /**
     * The input command defining the system call.
     *
     * @return The command string.
     */
    public String getCommand() {
        return (command);
    }

    private void setCommand(String s) {
        command = s;
    }

    /**
     * The input command defining the system call.
     *
     * @return The command string.
     */
    public File getWorking() {
        return (working);
    }

    private void setWorking(File f) {
        working = f;
    }

    /**
     * All output from the job.  Available on job completion.
     *
     * @return The output as a string.
     */
    public String getOutputText() {
        return (outputText);
    }

    private void setOutputText(String s) {
        outputText = s;
    }

    public Process getProcess() {
        return (process);
    }

    private void setProcess(Process p) {
        process = p;
    }

    /**
     * The exit value from the operating system on job completion.
     *
     * @return An exit value as an int.
     */
    public int getExitValue() {
        return (exitValue);
    }

    private void setExitValue(int i) {
        exitValue = i;
    }

    /**
     * Write to the stdin of the system process.
     *
     * @param array A byte array of data.
     * @param offset The offset into the array.
     * @param length The number of bytes to write.
     * @throws IOException on error.
     */
    public void write(byte[] array, int offset, int length) throws IOException {

        if ((outputStreamJob != null) && (array != null)) {

            outputStreamJob.write(array, offset, length);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        if (event.getType() != JobEvent.COMPLETE) {

            fireJobEvent(event);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        ProcessBuilder pb = new ProcessBuilder(getShell(), getShellSwitch(),
            getCommand());
        if (pb != null) {

            try {

                // We want to set the PATH variable in case a System
                // property is set.
                String jflicksPath = System.getProperty("jflicks.path");
                if (jflicksPath != null) {

                    String pathsep = System.getProperty("path.separator");
                    Map<String, String> env = pb.environment();
                    String oldpath = env.get("PATH");
                    if (oldpath != null) {
                        env.put("PATH", jflicksPath + pathsep + oldpath);
                    } else {
                        env.put("PATH", jflicksPath);
                    }
                }
                File dir = getWorking();
                if (dir != null) {

                    pb.directory(dir);
                }

                pb.redirectErrorStream(true);
                setProcess(pb.start());

            } catch (Exception ex) {
                setProcess(null);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        Process p = getProcess();
        if (p != null) {

            try {

                inputStreamJob = new InputStreamJob(p.getInputStream());
                inputJobContainer = JobManager.getJobContainer(inputStreamJob);
                outputStreamJob = new OutputStreamJob(p.getOutputStream());
                outputJobContainer =
                    JobManager.getJobContainer(outputStreamJob);
                if ((inputStreamJob != null) && (inputJobContainer != null)
                    && (outputStreamJob != null)
                    && (outputJobContainer != null)) {

                    inputStreamJob.addJobListener(this);
                    inputJobContainer.start();
                    JobManager.sleep(1000);
                    outputStreamJob.addJobListener(this);
                    outputJobContainer.start();
                    JobManager.sleep(1000);

                    setExitValue(p.waitFor());
                    setOutputText(inputStreamJob.getOutputText());
                    inputJobContainer.stop();
                    inputJobContainer = null;
                    inputStreamJob.removeJobListener(this);
                    inputStreamJob = null;
                    outputJobContainer.stop();
                    outputJobContainer = null;
                    outputStreamJob.removeJobListener(this);
                    outputStreamJob = null;
                }

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        setTerminate(true);
        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
        Process p = getProcess();
        if (p != null) {

            try {

                p.destroy();
                setExitValue(p.waitFor());
                setProcess(null);

                if (inputJobContainer != null) {

                    inputJobContainer.stop();
                    inputStreamJob.removeJobListener(this);
                }

                if (outputJobContainer != null) {

                    outputJobContainer.stop();
                    outputStreamJob.removeJobListener(this);
                }

            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

}


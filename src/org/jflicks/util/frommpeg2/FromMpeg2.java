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
package org.jflicks.util.frommpeg2;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;

import org.apache.commons.io.FileUtils;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class FromMpeg2 implements JobListener {

    private static int currentJobIndex = 0;
    private static SystemJob[] systemJobs = null;
    private static JobContainer jobContainer = null;

    /**
     * Default empty constructor.
     */
    private FromMpeg2() {
    }

    private static File[] findMkvs(String[] dirs) {

        File[] result = null;

        if (dirs != null) {

            ArrayList<File> list = new ArrayList<File>();
            String[] ext = {
                "mkv"
            };
            for (int i = 0; i < dirs.length; i++) {

                File fdir = new File(dirs[i]);
                Collection<File> coll = FileUtils.listFiles(fdir, ext, true);
                if (coll != null) {

                    list.addAll(coll);
                }
            }

            if (list.size() > 0) {

                // Now we want to only get mkv files that do not have an
                // mp4 counterpart.
                ArrayList<File> todo = new ArrayList<File>();
                for (int i = 0; i < list.size(); i++) {

                    File f = list.get(i);
                    String name = f.getName();
                    if (!name.startsWith(".")) {

                        String path = f.getPath();
                        path = path.substring(0, path.lastIndexOf("."));
                        path = path + ".mp4";
                        System.out.println("path <" + path + ">");
                        File mp4f = new File(path);
                        if (!mp4f.exists()) {

                            todo.add(f);
                        }
                    }
                }

                if (todo.size() > 0) {

                    result = todo.toArray(new File[todo.size()]);
                }
            }
        }

        return (result);
    }

    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            currentJobIndex++;
            if (currentJobIndex < systemJobs.length) {

                System.out.println("Working on file "
                    + (currentJobIndex / 2)
                    + " of "
                    + (systemJobs.length / 2));
                System.out.println("Starting <"
                    + systemJobs[currentJobIndex].getCommand() + ">");
                jobContainer =
                    JobManager.getJobContainer(systemJobs[currentJobIndex]);
                systemJobs[currentJobIndex].addJobListener(this);
                jobContainer.start();

            } else {

                System.out.println("Done!");
                System.exit(0);
            }

        } else if (event.getType() == JobEvent.UPDATE) {
            System.out.println(event.getMessage());
        }
    }

    /**
     * Simple main method that dumps the system properties to stdout.
     *
     * @param args Arguments that happen to be ignored.
     */
    public static void main(String[] args) {

        FromMpeg2 from = new FromMpeg2();
        File[] array = from.findMkvs(args);
        if ((array != null) && (array.length > 0)) {

            System.out.println("Doing " + array.length + " conversions.");
            int index = 0;
            systemJobs = new SystemJob[array.length * 2];
            for (int i = 0; i < array.length; i++) {

                // We need a SystemJob that encode video to h264.
                File f = array[i];
                String destname = f.getName();
                destname = destname.substring(0, destname.lastIndexOf("."));
                destname = destname + ".mp4";
                File parent = f.getParentFile();
                File fred = new File(parent, "fred.mp4");
                fred.deleteOnExit();
                File dest = new File(parent, destname);
                String command0 = "ffmpeg -y -i " + f.getPath()
                    + " -vcodec libx264"
                    + " -preset superfast"
                    + " -tune zerolatency"
                    + " -threads 2"
                    + " -s 1280x720"
                    + " -flags"
                    + " -global_header"
                    + " -acodec copy"
                    + " -async 1"
                    + " " + fred.getPath();
                from.systemJobs[index++] = SystemJob.getInstance(command0);

                String command1 = "ffmpeg -y -i " + fred.getPath()
                    + " -vcodec copy"
                    + " -acodec libfdk_aac"
                    + " " + dest.getPath();
                from.systemJobs[index++] = SystemJob.getInstance(command1);
            }

            // Now we start the first job.
            System.out.println("Starting <"
                + systemJobs[0].getCommand() + ">");
            jobContainer = JobManager.getJobContainer(systemJobs[0]);
            systemJobs[0].addJobListener(from);
            jobContainer.start();
        }
    }

}


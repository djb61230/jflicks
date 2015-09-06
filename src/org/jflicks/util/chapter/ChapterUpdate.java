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
package org.jflicks.util.chapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.tv.Commercial;
import org.jflicks.util.Util;

import org.apache.commons.io.FileUtils;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class ChapterUpdate implements JobListener {

    private static int currentJobIndex = 0;
    private static SystemJob[] systemJobs = null;
    private static JobContainer jobContainer = null;

    /**
     * Default empty constructor.
     */
    private ChapterUpdate() {
    }

    private static String formatSeconds(int secsIn) {

        int hours = secsIn / 3600;
        int remainder = secsIn % 3600;
        int minutes = remainder / 60;
        int seconds = remainder % 60;

        return ( (hours < 10 ? "0" : "") + hours
        + ":" + (minutes < 10 ? "0" : "") + minutes
        + ":" + (seconds< 10 ? "0" : "") + seconds );
    }

    private static File[] findEdls(String[] dirs) {

        File[] result = null;

        if (dirs != null) {

            ArrayList<File> list = new ArrayList<File>();
            String[] ext = {
                "edl"
            };
            for (int i = 0; i < dirs.length; i++) {

                File fdir = new File(dirs[i]);
                Collection<File> coll = FileUtils.listFiles(fdir, ext, true);
                if (coll != null) {

                    list.addAll(coll);
                }
            }

            if (list.size() > 0) {

                result = list.toArray(new File[list.size()]);
            }
        }

        return (result);
    }

    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            currentJobIndex++;
            if (currentJobIndex < systemJobs.length) {

                System.out.println("Working on file "
                    + (currentJobIndex + 1)
                    + " of "
                    + (systemJobs.length));
                System.out.println("Starting <" + systemJobs[currentJobIndex].getCommand() + ">");
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

        ChapterUpdate cu = new ChapterUpdate();
        File[] array = cu.findEdls(args);
        if ((array != null) && (array.length > 0)) {

            // 1) Use the .edl file to make an mp4 chapter file.
            // 2) Call mp4chaps to update.
            System.out.println("Doing " + array.length + " updates.");
            int index = 0;
            systemJobs = new SystemJob[array.length];
            for (int i = 0; i < array.length; i++) {

                // We need a SystemJob that will update the chapters.
                File f = array[i];
                String mp4name = f.getPath();
                mp4name = mp4name.substring(0, mp4name.lastIndexOf("."));
                String chapterTxt = mp4name + ".ts.chapters.txt";
                File chapterTxtfile = new File(chapterTxt);
                mp4name = mp4name + ".ts.mp4";
                File mp4file = new File(mp4name);
                String command = "mp4chaps -i " + mp4file.getPath();
                cu.systemJobs[index++] = SystemJob.getInstance(command);

                // Now we need to write out the .chapters.txt
                Commercial[] coms = Commercial.fromEDL(f);
                if ((coms != null) && (coms.length > 0)) {

                    StringBuilder sb = new StringBuilder();
                    sb.append("00:00:00.000 Chapter 1\n");
                    for (int j = 0; j < coms.length; j++) {

                        String fmt = formatSeconds(coms[j].getEnd());
                        sb.append(fmt + ".000 Chapter " + (j + 2) + "\n");
                    }

                    try {

                        Util.writeTextFile(chapterTxtfile, sb.toString());

                    } catch (Exception ex) {

                        System.out.println(ex.getMessage());
                    }
                }
            }

            // Now we start the first job.
            System.out.println("Starting <"
                + systemJobs[0].getCommand() + ">");
            jobContainer = JobManager.getJobContainer(systemJobs[0]);
            systemJobs[0].addJobListener(cu);
            jobContainer.start();
        }
    }

}


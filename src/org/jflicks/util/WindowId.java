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
package org.jflicks.util;

import org.jflicks.job.JobEvent;
import org.jflicks.job.JobContainer;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;

/**
 * Find the window id of a window on the system.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class WindowId implements JobListener {

    private String id;
    private boolean working;
    private static WindowId instance = new WindowId();

    private WindowId() {
    }

    public static WindowId getInstance() {
        return (instance);
    }

    public synchronized String getWindowId(String name) {

        setId(null);
        setWorking(true);
        SystemJob job =
            SystemJob.getInstance("xwininfo -name \"" + name + "\" -tree");
        job.addJobListener(this);
        JobContainer jc = JobManager.getJobContainer(job);
        jc.start();
        System.out.println(job.getCommand());
        while (isWorking()) {

            JobManager.sleep(200);
        }

        return (getId());
    }

    private boolean isWorking() {
        return (working);
    }

    private void setWorking(boolean b) {
        working = b;
    }

    private String getId() {
        return (id);
    }

    private void setId(String s) {
        id = s;
    }

    public void jobUpdate(JobEvent event) {

        if (event.getType() == JobEvent.COMPLETE) {

            SystemJob job = (SystemJob) event.getSource();
            String text = job.getOutputText();
            if (text != null) {

                System.out.println("test <" + text + ">");
                String[] lines = text.split("\n");
                for (int i = 0; i < lines.length; i++) {

                    int index = lines[i].indexOf("\"Content window");
                    if (index != -1) {

                        // Found our window id line.
                        String tmp = lines[i].substring(0, index);
                        setId(tmp.trim());
                    }
                }
                setWorking(false);
                /*
                int index = text.indexOf("Window id:");
                if (index != -1) {

                    index += 11;
                    setId(text.substring(index, text.indexOf(" ", index)));
                    setWorking(false);
                }
                */
            }
        }
    }

}

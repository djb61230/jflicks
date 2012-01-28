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
package org.jflicks.nativeutil;

import java.lang.reflect.Field;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

/**
 * Be able to kill misbehaving processes on Windows.  Don't have to
 * do this on Linux.  :-)
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class WindowsKill {

    /**
     * Default empty constructor.
     */
    private WindowsKill() {
    }

    static interface Kernel32 extends Library {

        static Kernel32 INSTANCE =
            (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
        int GetProcessId(Long hProcess);
    }

    public static int getPid(Process p) {

        int result = 0;

        Field f;
        if ((Platform.isWindows()) && (p != null)) {

            try {

                f = p.getClass().getDeclaredField("handle");
                f.setAccessible(true);
                result = Kernel32.INSTANCE.GetProcessId((Long) f.get(p));

            } catch (Exception ex) {

                System.out.println(ex.getMessage());
            }
        }

        return (result);
    }

    public static void kill(Process p) {

        int pid = getPid(p);
        if (pid != 0) {

            SystemJob job =
                SystemJob.getInstance("pskill -t " + pid);
            System.out.println(job.getCommand());
            JobContainer jc = JobManager.getJobContainer(job);
            jc.start();
        }
    }

}

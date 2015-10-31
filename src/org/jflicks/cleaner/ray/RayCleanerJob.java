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
package org.jflicks.cleaner.ray;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Date;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobManager;
import org.jflicks.nms.NMS;
import org.jflicks.tv.Recording;
import org.jflicks.tv.recorder.Recorder;
import org.jflicks.util.LogUtil;

import org.apache.commons.io.FileUtils;

/**
 * This job supports the Ray Cleaner service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class RayCleanerJob extends AbstractJob {

    private RayCleaner rayCleaner;

    /**
     * This job supports the RayCleaner plugin.
     *
     * @param rc A RayCleaner instance.
     */
    public RayCleanerJob(RayCleaner rc) {

        setRayCleaner(rc);

        // First run we will go after 2 minutes
        setSleepTime(120000);
    }

    private RayCleaner getRayCleaner() {
        return (rayCleaner);
    }

    private void setRayCleaner(RayCleaner r) {
        rayCleaner = r;
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

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());

            RayCleaner rc = getRayCleaner();
            if (rc != null) {

                NMS nms = rc.getNMS();
                if (nms != null) {

                    if (isSystemReady(nms)) {

                        Recording[] recs = nms.getRecordings();
                        if ((recs != null) && (recs.length > 0)) {

                            for (int i = 0; i < recs.length; i++) {

                                if (isReadyToClean(recs[i], rc.getRecordingMinimumAge())) {

                                    clean(recs[i]);
                                }
                            }

                        } else {

                            LogUtil.log(LogUtil.DEBUG, "Cleaner NO - no recordings");
                        }

                    } else {

                        LogUtil.log(LogUtil.DEBUG, "Cleaner NO - something recording");
                    }
                }
            }

            setSleepTime(rc.getTimeBetweenCleanings() * 1000);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        setTerminate(true);
    }

    private boolean isSystemReady(NMS n) {

        boolean result = true;

        if (n != null) {

            Recorder[] array = n.getRecorders();
            if (array != null) {

                for (int i = 0; i < array.length; i++) {

                    if (array[i].isRecording()) {

                        result = false;
                        break;
                    }
                }
            }
        }

        return (result);
    }

    private boolean isReadyToClean(Recording r, int seconds) {

        boolean result = false;

        if (r != null) {

            // Double check is isnt recording.
            if (!r.isCurrentlyRecording()) {

                // Now see if the indexer has finished.
                String path = r.getPath();
                String ext = r.getIndexedExtension();
                if ((path != null) && (ext != null)) {

                    File f = new File(path + "." + ext);
                    if ((f.exists()) && (f.isFile())) {

                        Date d = r.getDate();
                        if (d != null) {

                            long lsecs = (long) seconds;
                            lsecs *= 1000;
                            long when = d.getTime() + r.getDuration() + lsecs;
                            long now = System.currentTimeMillis();
                            if (now > when) {

                                LogUtil.log(LogUtil.DEBUG, "Cleaner YES to clean: " + r);
                                result = true;

                            } else {

                                LogUtil.log(LogUtil.DEBUG, "Cleaner NO too young: " + r);
                            }

                        } else {

                            LogUtil.log(LogUtil.DEBUG, "Cleaner no date cannot determine age: " + r);
                        }

                    } else {

                        LogUtil.log(LogUtil.DEBUG, "Cleaner no not indexed: " + r);
                    }

                } else {

                    LogUtil.log(LogUtil.DEBUG, "Cleaner no not indexed: " + r);
                }

            } else {

                LogUtil.log(LogUtil.DEBUG, "Cleaner no it's still recording: " + r);
            }
        }

        return (result);
    }

    private void clean(Recording r) {

        if (r != null) {

            String path = r.getPath();
            File fpath = new File(path);
            String prefix = fpath.getName();
            prefix = prefix.substring(0, prefix.length() - 2);
            File parent = fpath.getParentFile();
            File[] files = parent.listFiles(new HlsFileFilter(prefix));
            if ((files != null) && (files.length > 0)) {

                for (int i = 0; i < files.length; i++) {

                    try {

                        FileUtils.forceDelete(files[i]);
                        LogUtil.log(LogUtil.DEBUG, "Cleaner got: " + files[i].getPath());

                    } catch (IOException ex) {

                        LogUtil.log(LogUtil.WARNING, "Cleaner could not delete: " + ex.getMessage());
                    }
                }

                // Last thing to get is the m3u8 file.  It's different that it doesn't have the
                // .ts before the .m3u8
                File m3u8 = new File(parent, prefix + "m3u8");
                if ((m3u8.exists()) && (m3u8.isFile())) {

                    try {

                        FileUtils.forceDelete(m3u8);
                        LogUtil.log(LogUtil.DEBUG, "Cleaner got m3u8: " + m3u8.getPath());

                    } catch (IOException ex) {

                        LogUtil.log(LogUtil.WARNING, "Cleaner could not delete m3u8: " + ex.getMessage());
                    }
                }
            }
        }

    }

    private class HlsFileFilter implements FileFilter {

        private String prefix;

        public HlsFileFilter(String s) {
            prefix = s;
        }

        public boolean accept(File f) {

            boolean result = false;

            if ((prefix != null) && (f != null)) {

                String name = f.getName();
                if ((name.startsWith(prefix)) && (name.endsWith(".ts"))) {

                    result = true;
                }
            }

            return (result);
        }
    }

}

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
package org.jflicks.tv.postproc.worker.comsilentblack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jflicks.job.JobContainer;
import org.jflicks.job.JobEvent;
import org.jflicks.job.JobListener;
import org.jflicks.job.JobManager;
import org.jflicks.job.SystemJob;
import org.jflicks.tv.Commercial;
import org.jflicks.tv.Recording;
import org.jflicks.tv.postproc.worker.BaseWorker;
import org.jflicks.tv.postproc.worker.BaseWorkerJob;
import org.jflicks.util.DetectRatingPlan;
import org.jflicks.util.DetectRatingRectangle;
import org.jflicks.util.DetectResult;
import org.jflicks.util.LogUtil;
import org.jflicks.util.Util;

import org.apache.commons.io.FileUtils;

/**
 * This job starts a system job that runs comskip.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ComSilentBlackJob extends BaseWorkerJob implements JobListener {

    private static final int MODE_SILENT = 1;
    private static final int MODE_BLACK = 2;
    private static final int MODE_RATING = 3;
    private static final int MODE_CHAPTER = 4;

    private static final int TYPE_IGNORE = 1;
    private static final int TYPE_START = 2;
    private static final int TYPE_END = 3;

    private String mp4Path;
    private String devNull;
    private String silenceText;
    private String blackText;
    private int mode;
    private File directory;
    private int backup;
    private int span;
    private boolean verbose;
    private DetectRatingPlan[] detectRatingPlans;

    /**
     * Constructor with one required argument.
     *
     * @param r A Recording to check for commercials.
     * @param bw The Worker associated with this Job.
     */
    public ComSilentBlackJob(Recording r, BaseWorker bw) {

        super(r, bw);

        // Check the recording for completion every minute.
        setSleepTime(60000);
        //setSleepTime(5000);
    }

    private int getMode() {
        return (mode);
    }

    private void setMode(int i) {
        mode = i;
    }

    private boolean isModeSilent() {
        return (getMode() == MODE_SILENT);
    }

    private boolean isModeBlack() {
        return (getMode() == MODE_BLACK);
    }

    private boolean isModeRating() {
        return (getMode() == MODE_RATING);
    }

    private boolean isModeChapter() {
        return (getMode() == MODE_CHAPTER);
    }

    private String getSilenceText() {
        return (silenceText);
    }

    private void setSilenceText(String s) {
        silenceText = s;
    }

    private String getBlackText() {
        return (blackText);
    }

    private void setBlackText(String s) {
        blackText = s;
    }

    private String getMp4Path() {
        return (mp4Path);
    }

    private void setMp4Path(String s) {
        mp4Path = s;
    }

    private String getDevNull() {
        return (devNull);
    }

    private void setDevNull(String s) {
        devNull = s;
    }

    /**
     * We want to actually adjust the break a few seconds.
     *
     * @return An int value in seconds.
     */
    public int getBackup() {
        return (backup);
    }

    /**
     * We want to actually adjust the break a few seconds.
     *
     * @param i An int value in seconds.
     */
    public void setBackup(int i) {
        backup = i;
    }

    /**
     * The time between each frame.  Defaults to five.
     *
     * @return The span as an int value.
     */
    public int getSpan() {
        return (span);
    }

    /**
     * The time between each frame.  Defaults to five.
     *
     * @param i The span as an int value.
     */
    public void setSpan(int i) {
        span = i;
    }

    /**
     * Turning on verbose will send messages to the console and leave
     * working images on disk.  This is handy for debugging.
     *
     * @return True when the program should be verbose.
     */
    public boolean isVerbose() {
        return (verbose);
    }

    /**
     * Turning on verbose will send messages to the console and leave
     * working images on disk.  This is handy for debugging.
     *
     * @param b True when the program should be verbose.
     */
    public void setVerbose(boolean b) {
        verbose = b;
    }

    /**
     * We have a set of plans to help us find the logos.
     *
     * @return An array of DetectRatingPlan instances.
     */
    public DetectRatingPlan[] getDetectRatingPlans() {
        return (detectRatingPlans);
    }

    /**
     * We have a set of plans to help us find the logos.
     *
     * @param array An array of DetectRatingPlan instances.
     */
    public void setDetectRatingPlans(DetectRatingPlan[] array) {
        detectRatingPlans = array;
    }

    private File getDirectory() {
        return (directory);
    }

    private void setDirectory(File f) {
        directory = f;
    }

    private File createTempFile() {

        File result = null;

        try {

            File dir = File.createTempFile("comrat", "work");
            if (!dir.delete()) {
                LogUtil.log(LogUtil.INFO, dir.getPath() + " not found");
            }
            if (dir.mkdir()) {

                result = dir;

            } else {

                LogUtil.log(LogUtil.INFO, "Failed to make " + dir.getPath());
            }

        } catch (IOException ex) {

            result = null;
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {

        setMode(MODE_SILENT);
        Recording r = getRecording();
        if (r != null) {

            File dir = createTempFile();
            setDirectory(dir);

            setMp4Path(r.getPath() + ".mp4");
            String dn = "/dev/null";
            if (Util.isWindows()) {

                dn = "NUL";
            }

            setDevNull(dn);

            // First job to set up is silence detection.
            SystemJob job = SystemJob.getInstance("ffmpeg -i "
                + getMp4Path()
                + " -filter_complex \"[0:a]silencedetect=n=-20dB:d=1[outa]\" -map [outa] -f mp3 -y "
                + dn);
            job.addJobListener(this);
            setSystemJob(job);
            JobContainer jc = JobManager.getJobContainer(job);
            setJobContainer(jc);
            LogUtil.log(LogUtil.INFO, "Will start after indexing done: " + job.getCommand());
            setTerminate(false);

        } else {

            LogUtil.log(LogUtil.INFO, "Recording is null - quitting.");
            setTerminate(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        boolean working = false;

        while (!isTerminate()) {

            JobManager.sleep(getSleepTime());

            if (!working) {

                Recording r = getRecording();
                if (!r.isCurrentlyRecording()) {

                    File indexed = new File(getMp4Path());
                    if (indexed.exists()) {

                        LogUtil.log(LogUtil.INFO, "indexer done for " + r.getTitle() + " kick off silencedetect.");

                        // We are ready to start ffmpeg.
                        JobContainer jc = getJobContainer();
                        if (jc != null) {

                            jc.start();
                            working = true;
                            LogUtil.log(LogUtil.INFO, "Actually kicked off silencedetect ffmpeg " + r.getTitle());
                        }

                    } else {

                        LogUtil.log(LogUtil.INFO, "We don't start until after indexing. " + r.getTitle());
                        LogUtil.log(LogUtil.INFO, "Don't find <" + indexed.getPath() + ">");
                    }

                } else {

                    LogUtil.log(LogUtil.INFO, "Recording still on. Waiting til finished to process. " + r.getTitle());
                }
            }
        }

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {

        try {
            FileUtils.deleteDirectory(getDirectory());
        } catch (Exception ex) {
        }

        setDirectory(null);

        setTerminate(true);
    }

    /**
     * {@inheritDoc}
     */
    public void jobUpdate(JobEvent event) {

        File dir = getDirectory();
        Recording r = getRecording();

        if ((dir != null) && (r != null)) {

            if (event.getType() == JobEvent.COMPLETE) {

                if (isModeSilent()) {

                    // We have finished the silencedetect so we have to fire off blackdetect.
                    SystemJob job = getSystemJob();
                    setSilenceText(job.getOutputText());

                    // Next job to set up is black detection.
                    job = SystemJob.getInstance("ffmpeg -i "
                        + getMp4Path() + " -vf blackdetect=d=0.1:pix_th=.1 -f rawvideo -y "
                        + getDevNull());
                    job.addJobListener(this);
                    setSystemJob(job);
                    JobContainer jc = JobManager.getJobContainer(job);
                    setJobContainer(jc);
                    jc.start();
                    setMode(MODE_BLACK);
                    LogUtil.log(LogUtil.INFO, "started: " + job.getCommand());

                } else if (isModeBlack()) {

                    // We have finished the blackdetect so we have to fire off rating.
                    SystemJob job = getSystemJob();
                    setBlackText(job.getOutputText());

                    job = SystemJob.getInstance("ffmpeg -i "
                        + getMp4Path() + " -r 1/" + getSpan() + " -s hd480 "
                        + dir.getPath() + File.separator + "frame-%6d.jpg");
                    job.addJobListener(this);
                    setSystemJob(job);
                    JobContainer jc = JobManager.getJobContainer(job);
                    setJobContainer(jc);
                    jc.start();
                    setMode(MODE_RATING);
                    LogUtil.log(LogUtil.INFO, "started: " + job.getCommand());

                } else if (isModeRating()) {

                    // We can reconcile our silent and black data.
                    Detection[] sbarray = process(getSilenceText(), getBlackText());
                    if ((sbarray != null) && (sbarray.length > 0)) {

                        LogUtil.log(LogUtil.INFO, "Found " + sbarray.length + " silent/blacks");
                        for (int i = 0; i < sbarray.length; i++) {

                            LogUtil.log(LogUtil.DEBUG, "index " + i + " " + sbarray[i]);
                            LogUtil.log(LogUtil.DEBUG, "time " + i + " "
                                + formatSeconds(sbarray[i].getStart().intValue()));
                        }
                    }

                    Commercial[] sbcoms = toCommercials(sbarray);
                    Commercial[] ratcoms = toCommercialsFromRating(processRating());

                    Commercial[] coms = selectBest(r, sbcoms, ratcoms);

                    LogUtil.log(LogUtil.DEBUG, "Determined commercials: " + coms);
                    if ((coms != null) && (coms.length > 0)) {

                        String origPath = r.getPath();
                        for (int i = 0; i < coms.length; i++) {

                            LogUtil.log(LogUtil.DEBUG, "c[" + i + "] start: " + coms[i].getStart()
                                + " end: " + coms[i].getEnd());
                        }

                        r.setCommercials(coms);

                        // Next we want to write a chapter file for mp4chaps.
                        String ext = r.getIndexedExtension();
                        if ((ext != null) && (ext.equals("mp4"))) {

                            coms = r.getCommercials();
                            if ((coms != null) && (coms.length > 0)) {

                                StringBuilder sb = new StringBuilder();
                                sb.append("00:00:00.000 Chapter 1\n");
                                copyFrame(origPath, 1, 1);
                                for (int i = 0; i < coms.length; i++) {

                                    String fmt = formatSeconds(coms[i].getEnd());
                                    sb.append(fmt + ".000 Chapter " + (i + 2) + "\n");
                                    copyFrame(origPath, i + 2, coms[i].getEnd());
                                }

                                File file = new File(origPath + ".chapters.txt");
                                try {

                                    setMode(MODE_CHAPTER);
                                    Util.writeTextFile(file, sb.toString());
                                    SystemJob job = SystemJob.getInstance("mp4chaps -i \"" + origPath + ".mp4\"");
                                    job.addJobListener(this);
                                    JobContainer jc = JobManager.getJobContainer(job);
                                    LogUtil.log(LogUtil.INFO, "will start: " + job.getCommand());
                                    jc.start();

                                } catch (Exception ex) {

                                    LogUtil.log(LogUtil.INFO, "Couldn't do chapters");
                                    stop();
                                }

                            } else {

                                stop();
                            }

                        } else {

                            LogUtil.log(LogUtil.INFO, "Found no silence/black locations!");
                            stop();
                        }

                    } else {

                        stop();
                    }

                } else if (isModeChapter()) {

                    stop();
                }
            }
        }
    }

    private Detection[] merge(Detection[] one, Detection[] two) {

        Detection[] result = null;

        ArrayList<Detection> l = new ArrayList<Detection>();

        if ((one != null) && (one.length > 0)) {

            List<Detection> onelist = Arrays.asList(one);
            l.addAll(onelist);
        }

        if ((two != null) && (two.length > 0)) {

            List<Detection> twolist = Arrays.asList(two);
            l.addAll(twolist);
        }

        if (l.size() > 0) {

            result = l.toArray(new Detection[l.size()]);
            Arrays.sort(result);
        }

        return (result);
    }

    private Detection[] process(String silence, String black) {

        Detection[] result = null;

        if ((silence != null) && (black != null)) {

            Detection[] sdetect = Detection.parseSilence(silence);
            Detection[] bdetect = Detection.parseBlack(black);
            if ((sdetect != null) && (sdetect.length > 0) && (bdetect != null) && (bdetect.length > 0)) {

                List<Detection> blist = Arrays.asList(bdetect);
                ArrayList<Detection> list = new ArrayList<Detection>();
                for (int i = 0; i < sdetect.length; i++) {

                    if (blist.contains(sdetect[i])) {

                        list.add(sdetect[i]);
                    }
                }

                if (list.size() > 0) {

                    // We should put in a Detection for the start of the video.  If the
                    // first silent/black frame is the first commercial, we lose it because
                    // this first part of the show is not counted.
                    Detection begin = new Detection();
                    begin.setStart(Double.valueOf(0));
                    begin.setEnd(Double.valueOf(0));
                    list.add(0, begin);
                    result = list.toArray(new Detection[list.size()]);
                }
            }
        }

        return (result);
    }

    private Detection[] processRating() {

        Detection[] result = null;

        LogUtil.log(LogUtil.INFO, "Frame grab finished...");
        File dir = getDirectory();
        Recording r = getRecording();
        if ((dir != null) && (r != null)) {

            // ffmpeg finished, now we need to look for the rating
            // frames.
            try {

                DetectRatingRectangle drr = new DetectRatingRectangle();
                drr.setBackup(getBackup());
                drr.setSpan(getSpan());
                LogUtil.log(LogUtil.INFO, "Start processing of frames..." + dir);
                DetectResult[] array = drr.processDirectory(dir, "jpg", getDetectRatingPlans(), isVerbose());
                LogUtil.log(LogUtil.INFO, "Finished processing of frames...");
                if ((array != null) && (array.length > 0)) {

                    ArrayList<Detection> dlist = new ArrayList<Detection>();
                    for (int i = 0; i < array.length; i++) {

                        Detection ratingd = new Detection();
                        Double dobj = Double.valueOf(array[i].getTime());
                        ratingd.setStart(dobj);
                        ratingd.setEnd(dobj);
                        dlist.add(ratingd);
                    }

                    if (dlist.size() > 0) {

                        result = dlist.toArray(new Detection[dlist.size()]);
                    }

                } else {

                    LogUtil.log(LogUtil.INFO, "Didn't find any rating frames! " + r.getTitle());
                }

            } catch (IOException ex) {

                LogUtil.log(LogUtil.INFO, "Comrat IO bad news.");
            }
        }

        return (result);
    }

    private void copyFrame(String path, int index, int seconds) {

        File dir = getDirectory();
        if ((dir != null) && (path != null)) {

            File f = getFrameBySeconds(seconds);
            if (f != null) {

                try {

                    String framePath = path + ".cframe" + index + ".jpg";
                    FileUtils.copyFile(f, new File(framePath));

                } catch (Exception ex) {

                    LogUtil.log(LogUtil.WARNING, "copy frame failed: " + ex.getMessage());
                }
            }
        }
    }

    private File getFrameBySeconds(int seconds) {

        File result = null;

        File dir = getDirectory();
        if (dir != null) {

            // We up the index by an extra 2 so we are sure to get a "show" screen shot.
            // Or more likely anyway.
            int index = seconds / getSpan() + 4;
            String fileName = String.format("frame-%06d.jpg", index);
            LogUtil.log(LogUtil.INFO, "frame fileName: " + fileName);
            result = new File(dir, fileName);
            if (result.exists()) {
                LogUtil.log(LogUtil.INFO, "Found frame for second: " + seconds + " time: " + formatSeconds(seconds));
            } else {
                LogUtil.log(LogUtil.INFO, "NOT Found frame for second: " + seconds);
                result = null;
            }
        }

        return (result);
    }

    private int[] makeTypes(Detection[] array) {

        int[] result = null;

        if ((array != null) && (array.length > 0)) {

            result = new int[array.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = TYPE_IGNORE;
            }
        }

        return (result);
    }

    private int[] makeSpans(Detection[] array) {

        int[] result = null;

        if ((array != null) && (array.length > 1)) {

            result = new int[array.length - 1];
            for (int i = 0; i < result.length; i++) {
                result[i] = array[i + 1].getStart().intValue() - array[i].getStart().intValue();
            }
        }

        return (result);
    }

    private void markStarts(int[] spans, int[] types) {

        if ((spans != null) && (types != null)) {

            for (int i = 0; i < spans.length; i++) {

                if (spans[i] > 300) {

                    types[i + 1] = TYPE_START;
                }
            }
        }
    }

    private void markEnds(int[] spans, int[] types) {

        if ((spans != null) && (types != null)) {

            int index = 0;
            for (int i = 0; i < types.length; i++) {

                if (types[i] == TYPE_START) {

                    index = i;
                    break;
                }
            }

            // We are at the first commercial.  Now we can set the ends.
            for (int i = index + 1; i < spans.length; i++ ) {

                if (spans[i] > 300) {

                    types[i] = TYPE_END;
                }
            }
        }
    }

    private Commercial[] selectBest(Recording r, Commercial[] fromSilentBlack, Commercial[] fromRating) {

        Commercial[] result = null;

        if (r != null) {

            if ((fromSilentBlack == null) && (fromRating != null)) {
                result = fromRating;
            } else if ((fromSilentBlack != null) && (fromRating == null)) {
                result = fromSilentBlack;
            } else if ((fromSilentBlack != null) && (fromRating != null)) {

                // Ok we have to choose.
                if (fromSilentBlack.length >= fromRating.length) {
                    result = fromSilentBlack;
                } else {
                    result = fromRating;
                }
            }
        }

        return (result);
    }

    private Commercial[] toCommercialsFromRating(Detection[] array) {

        Commercial[] result = null;

        if ((array != null) && (array.length > 0)) {

            // We know we only have the "end" of a commercial.  So we
            // take that into account.  The symbol can come into play
            // in the promo area too.  So let's eliminate those if they
            // are too close.
            ArrayList<Detection> dlist = new ArrayList<Detection>();
            for (int i = 0; i < array.length - 1; i++) {

                int time0 = array[i].getStart().intValue();
                int time1 = array[i + 1].getStart().intValue();
                if ((time1 - time0) > 300) {

                    dlist.add(array[i]);
                }
            }

            ArrayList<Commercial> list = new ArrayList<Commercial>();
            for (int i = 0; i < dlist.size(); i++) {

                int end = dlist.get(i).getStart().intValue();

                // We skip the first three minutes.
                if (end > 180) {

                    Commercial c = new Commercial();
                    c.setStart(end - 180);
                    c.setEnd(end);
                    list.add(c);
                }
            }

            if (list.size() > 0) {

                result = list.toArray(new Commercial[list.size()]);
            }
        }

        return (result);
    }

    private Commercial[] toCommercials(Detection[] array) {

        Commercial[] result = null;

        if ((array != null) && (array.length > 1)) {

            int[] types = makeTypes(array);
            int[] spans = makeSpans(array);
            markStarts(spans, types);
            markEnds(spans, types);
            ArrayList<Commercial> list = new ArrayList<Commercial>();
            int expect = TYPE_START;
            Commercial current = null;
            for (int i = 0; i < types.length; i++) {

                if (expect == types[i]) {

                    if (expect == TYPE_START) {

                        current = new Commercial();
                        current.setStart(array[i].getStart().intValue());
                        expect = TYPE_END;

                    } else if (expect == TYPE_END) {

                        current.setEnd(array[i].getStart().intValue());
                        list.add(current);
                        expect = TYPE_START;
                    }
                }
            }

            if (list.size() > 0) {

                result = list.toArray(new Commercial[list.size()]);
            }
        }

        return (result);
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

    public static void main(String[] args) {

        Recording r = new Recording();
        //r.setPath("./EP021835830007_2015_10_26_22_00.ts");
        //r.setPath("./EP008487640032_2015_09_05_18_00.ts");
        //r.setPath("./EP011581290130_2015_10_23_21_00.ts");
        //r.setPath("./EP019224320018_2015_10_25_22_00.ts");
        r.setPath("./EP003670780096_2015_10_28_20_00.ts");
        r.setCurrentlyRecording(false);
        r.setIndexedExtension("mp4");
        r.setTitle("csi");

        ComSilentBlackWorker w = new ComSilentBlackWorker();
        ComSilentBlackJob job = new ComSilentBlackJob(r, w);
        job.setSpan(3);
        job.setVerbose(false);
        job.setBackup(3);
        DetectRatingPlan[] plans = new DetectRatingPlan[5];
        plans[0] = new DetectRatingPlan();
        plans[0].setType(1);
        plans[0].setRed(0);
        plans[0].setGreen(0);
        plans[0].setBlue(0);
        plans[0].setRange(70);
        plans[1] = new DetectRatingPlan();
        plans[1].setType(0);
        plans[1].setRed(255);
        plans[1].setGreen(255);
        plans[1].setBlue(255);
        plans[1].setRange(70);
        plans[2] = new DetectRatingPlan();
        plans[2].setType(1);
        plans[2].setRed(14);
        plans[2].setGreen(105);
        plans[2].setBlue(132);
        plans[2].setRange(70);
        plans[3] = new DetectRatingPlan();
        plans[3].setType(0);
        plans[3].setRed(82);
        plans[3].setGreen(188);
        plans[3].setBlue(56);
        plans[3].setRange(100);
        plans[4] = new DetectRatingPlan();
        plans[4].setType(0);
        plans[4].setRed(220);
        plans[4].setGreen(229);
        plans[4].setBlue(235);
        plans[4].setRange(70);

        job.setDetectRatingPlans(plans);

        JobContainer jc = JobManager.getJobContainer(job);
        jc.start();
    }
}


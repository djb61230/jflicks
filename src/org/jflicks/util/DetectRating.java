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

import java.io.File;
import java.io.IOException;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;

/**
 * Simple class that will try to see if a set of Rating Symbol images
 * are contained in a larger image (screenshot).
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class DetectRating {

    /**
     * The rating logo can be basically black in color.  This is a "hint"
     * that our code needs.
     */
    public static final int BLACK_TYPE = 0;

    /**
     * The rating logo can be basically white in color.  This is a "hint"
     * that our code needs.
     */
    public static final int WHITE_TYPE = 1;

    private int backup;
    private int span;
    private RatingImage[] ratingImages;

    /**
     * Default empty constructor.
     */
    public DetectRating() {

        setBackup(0);
        setSpan(5);
    }

    /**
     * The rating frame is usually put up a few seconds after the show has
     * restarted, so the backup property allows a constant number of seconds
     * to be offset to account for this time.
     *
     * @return An int value in seconds.
     */
    public int getBackup() {
        return (backup);
    }

    /**
     * The rating frame is usually put up a few seconds after the show has
     * restarted, so the backup property allows a constant number of seconds
     * to be offset to account for this time.
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

    private void moveToTop(int index) {

        if ((ratingImages != null) && (index > 0)) {

            ArrayList<RatingImage> l = new ArrayList<RatingImage>();
            l.add(ratingImages[index]);
            for (int i = 0; i < ratingImages.length; i++) {

                if (i != index) {

                    l.add(ratingImages[i]);
                }
            }

            ratingImages = l.toArray(new RatingImage[l.size()]);
        }
    }

    private RatingImage[] getRatingImages(String path) {

        if (ratingImages == null) {

            if (path != null) {

                File dir = new File(path);
                if ((dir.exists()) && (dir.isDirectory())) {

                    String[] exts = {
                        ".png",
                        ".jpg"
                    };

                    ExtensionsFilter ef = new ExtensionsFilter(exts);
                    File[] files = dir.listFiles(ef);
                    if ((files != null) && (files.length > 0)) {

                        // Ok we have some rating images we can load.
                        ArrayList<RatingImage> l = new ArrayList<RatingImage>();
                        for (int i = 0; i < files.length; i++) {

                            try {

                                BufferedImage bi = ImageIO.read(files[i]);
                                int w = bi.getWidth();
                                int h = bi.getHeight();
                                int[] data = bi.getRGB(0, 0, w, h, null, 0, w);
                                RatingImage ri = new RatingImage(
                                    files[i].getPath(), data, w, h);

                                l.add(ri);

                            } catch (IOException ex) {

                                System.out.println(ex.getMessage());
                            }
                        }

                        if (l.size() > 0) {

                            ratingImages = l.toArray(new RatingImage[l.size()]);
                        }
                    }
                }
            }
        }

        return (ratingImages);
    }

    private double compare(int[] first, int[] firstalpha, int[] second,
        double accept) {

        double result = 0.0;

        if ((first != null) && (second != null)
            && (first.length == second.length)) {

            double dmax = (double) first.length;
            int need = (int) (dmax * accept);
            int count = 0;
            for (int i = 0; i < first.length; i++) {

                if (first[i] == second[i]) {

                    count++;

                } else if ((need - count) > (first.length - i)) {

                    // If we can't possibly make it, then quit now.
                    break;
                }
            }

            if (count > 0) {

                result = (double) (((double) count) / ((double) first.length));
            }
        }

        return (result);
    }

    private boolean fill(int x, int y, int[] src, int srcw, int srch,
        int[] dest, int destw, int desth) {

        boolean result = false;

        if ((src != null) && (dest != null)) {

            if (((x + destw) < srcw) && ((y + desth) < srch)) {

                // We have a valid subset.
                result = true;

                int dindex = 0;
                for (int row = 0; row < desth; row++) {

                    int index = y * srcw + x;
                    for (int col = 0; col < destw; col++) {

                        dest[dindex++] = src[index++];
                    }

                    y++;
                }
            }
        }

        return (result);
    }

    private double analyzeRatingImage(int[] data, int w, int h,
        RatingImage ri, double accept) {

        double result = 0.0;

        if ((ri != null) && (data != null)) {

            int[] ridata = ri.getData();
            int[] rialphadata = ri.getAlphaData();
            if ((ridata != null) && (rialphadata != null)) {

                // We need a "working" buffer to copy data.
                int[] dest = ri.getBuffer();
                int destw = ri.getWidth();
                int desth = ri.getHeight();

                // We loop through all (x, y) points of our source
                // data array to see how close the given RatingImage
                // matches.  We return the highest percentage of
                // matching pixels.  We go by row...
                for (int y = 0; y < h; y++) {

                    for (int x = 0; x < w; x++) {

                        if (fill(x, y, data, w, h, dest, destw, desth)) {

                            // We have valid new data as the destination
                            // data array "fits" at this (x, y) point.
                            // The compare method will return a value
                            // between 0.0 and 1.0.  If it is 1.0 (doubtful)
                            // it means the two images are a perfect match.
                            // Of course the lower the value the less likely.
                            double d =
                                compare(ridata, rialphadata, dest, accept);
                            if (d > result) {

                                result = d;

                                if (result >= accept) {

                                    x = w;
                                    y = h;
                                }
                            }

                        } else {

                            // No sense in checking the rest of the row...
                            x = w;
                        }
                    }
                }
            }
        }

        return (result);
    }

    /**
     * Examine a particlur image file as determine whether it is a
     * "rating frame".
     *
     * @param ratingDir A File directory containing rating images.
     * @param f A File representing an image.
     * @param type Black or white symbol is expected.
     * @param fudge Wiggle room from full black or full white.
     * @param verbose Print out messages if true.
     * @return True if it is a "rating frame".
     * @throws IOException on an error.
     */
    public boolean examine(String ratingDir, File f, int type, int fudge,
        boolean verbose) throws IOException {

        boolean result = false;

        // First need to make sure we have images to compare.
        RatingImage[] rimages = getRatingImages(ratingDir);
        if (rimages != null) {

            BufferedImage bi = ImageIO.read(f);

            int x = 70;
            int y = 10;
            int w = 180;
            int h = 180;
            int[] data = new int[w * h];

            bi.getRGB(x, y, w, h, data, 0, w);

            String fname = f.getName();
            BufferedImage crop =
                new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            crop.setRGB(0, 0, w, h, data, 0, w);

            if (verbose) {

                ImageIO.write(crop, "png", new File(fname + "-crop.png"));
            }

            // Not lets turn non-grayscale pixels to white.
            for (int i = 0; i < data.length; i++) {

                int r = data[i] & 0x00ff0000;
                r = r >> 16;
                int g = data[i] & 0x0000ff00;
                g = g >> 8;
                int b = data[i] & 0x000000ff;

                if (type == BLACK_TYPE) {

                    // If we are "near black" then turn it black, otherwise
                    // turn it white.
                    if ((Math.min(r, fudge) == r)
                        && (Math.min(g, fudge) == g)
                        && (Math.min(b, fudge) == b)) {

                        data[i] = 0x00000000;

                    } else {

                        data[i] = 0x00ffffff;
                    }

                } else if (type == WHITE_TYPE) {

                    // If we are "near white" then turn it black, otherwise
                    // turn it white.  This makes the rest of the code in this
                    // class work when the rating box is black.
                    if ((Math.max(r, fudge) == r)
                        && (Math.max(g, fudge) == g)
                        && (Math.max(b, fudge) == b)) {

                        data[i] = 0x00000000;

                    } else {

                        data[i] = 0x00ffffff;
                    }
                }
            }

            BufferedImage white =
                new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            white.setRGB(0, 0, w, h, data, 0, w);

            if (verbose) {

                ImageIO.write(white, "png", new File(fname + "-white.png"));
            }

            // Here we have a black and white image that we now try to
            // find one of our set of rating images.
            double percent = 0.0;
            for (int i = 0; i < rimages.length; i++) {

                double d = analyzeRatingImage(data, w, h, rimages[i], 0.90);
                if (verbose) {

                    System.out.println("analyzeRatingImage: "
                        + rimages[i].getPath() + ": " + d);
                }
                if (d > percent) {

                    percent = d;

                    if (percent >= 0.90) {

                        // The idea is that once we find a rating image
                        // that works, lets use it first on subsequent
                        // checks.
                        moveToTop(i);
                        i = rimages.length;
                    }
                }
            }

            // Here we have computed the highest percentage any of our
            // rating images may be located in our screen data.
            result = (percent > 0.90);
        }

        return (result);
    }

    private int frameToSeconds(File f) {

        int result = 0;

        if (f != null) {

            String name = f.getName();
            int start = name.indexOf("-") + 1;
            int end = name.lastIndexOf(".");
            result = Util.str2int(name.substring(start, end), result);
            result *= getSpan();
            result -= getBackup();
            if (result < 0) {
                result = 0;
            }
        }

        return (result);
    }

    /**
     * This is our main worker method as it checks out all the images in
     * a given directory to determine if any are "rating frames".
     *
     * @param ratingDir The directory to look for rating images.
     * @param dir The directory to look.
     * @param ext Look for files with this extension.
     * @param type Do we look for black or white rating symbol.
     * @param fudge This gives us some wiggle room to handle not quite
     * black or white - shades of gray if you will.
     * @param verbose Print out messages if true.
     * @return An array of ints that have "seconds" pointing to time a
     * rating frame happened.
     * @throws IOException on an error.
     */
    public int[] processDirectory(String ratingDir, File dir, String ext,
        int type, int fudge, boolean verbose) throws IOException {

        int[] result = null;

        if ((dir != null) && (ext != null)) {

            String[] array = new String[1];
            array[0] = ext;
            ExtensionsFilter ef = new ExtensionsFilter(array);
            File[] all = dir.listFiles(ef);
            if ((all != null) && (all.length > 0)) {

                ArrayList<Integer> timelist = new ArrayList<Integer>();
                Arrays.sort(all);
                for (int i = 0; i < all.length; i++) {

                    if (examine(ratingDir, all[i], type, fudge, verbose)) {

                        int time = frameToSeconds(all[i]);
                        timelist.add(Integer.valueOf(time));
                        System.out.println(all[i] + " is a rating frame <"
                            + time + ">");

                        // Since we just found one, lets assume the next
                        // 360 seconds or so we don't need to check.
                        int fcount = (int) (360 / getSpan());
                        i += fcount;
                    }
                }

                if (timelist.size() > 0) {

                    result = new int[timelist.size()];
                    for (int i = 0; i < result.length; i++) {

                        result[i] = timelist.get(i).intValue();
                    }
                }
            }
        }

        return result;
    }

    public int[] processDirectory(String ratingDir, File dir, String ext,
        DetectRatingPlan[] plans, boolean verbose) throws IOException {

        int[] result = null;

        if ((plans != null) && (plans.length > 0) && (dir != null)
            && (ext != null)) {

            String[] array = new String[1];
            array[0] = ext;
            ExtensionsFilter ef = new ExtensionsFilter(array);
            File[] all = dir.listFiles(ef);
            if ((all != null) && (all.length > 0)) {

                ArrayList<Integer> timelist = new ArrayList<Integer>();
                Arrays.sort(all);

                // We are going to process each plan until we find one
                // that works, then from then on we will just use that
                // one.  So we have to start with all of them.  By default
                // our array will be filled with "false" so we don't
                // skip any.
                boolean[] skipPlan = new boolean[plans.length];
                boolean zappedPlans = false;

                for (int i = 0; i < all.length; i++) {

                    for (int j = 0; j < plans.length; j++) {

                        if (!skipPlan[j]) {

                            int type = plans[j].getType();
                            int fudge = plans[j].getValue();
                            if (examine(ratingDir, all[i], type, fudge,
                                verbose)) {

                                int time = frameToSeconds(all[i]);
                                timelist.add(Integer.valueOf(time));
                                System.out.println(all[i]
                                    + " is a rating frame <" + time + ">");

                                // Since we just found one, lets assume the next
                                // 360 seconds or so we don't need to check.
                                int fcount = (int) (360 / getSpan());
                                i += fcount;

                                if (!zappedPlans) {

                                    // Now we need to zap all plans but
                                    // this one.
                                    for (int k = 0; k < plans.length; k++) {

                                        if (k != j) {
                                            skipPlan[k] = true;
                                        }
                                    }

                                    zappedPlans = true;
                                }
                            }
                        }
                    }
                }

                if (timelist.size() > 0) {

                    result = new int[timelist.size()];
                    for (int i = 0; i < result.length; i++) {

                        result[i] = timelist.get(i).intValue();
                    }
                }
            }
        }

        return result;
    }

    /**
     * Simple main method that tests this class.
     *
     * @param args Arguments that happen to be ignored.
     * @throws IOException on an error.
     */
    public static void main(String[] args) throws IOException {

        ArrayList<DetectRatingPlan> drpList = new ArrayList<DetectRatingPlan>();
        String ratingDir = "resources/rating";
        String path = null;
        boolean verbose = false;
        String extension = "jpg";
        int backup = 3;
        int span = 5;

        for (int i = 0; i < args.length; i += 2) {

            if (args[i].equalsIgnoreCase("-path")) {
                path = args[i + 1];
            } else if (args[i].equalsIgnoreCase("-type:value")) {

                String[] splans = args[i + 1].split(",");
                if ((splans != null) && (splans.length > 0)) {

                    for (int j = 0; j < splans.length; j++) {

                        int index = splans[j].indexOf(":");
                        if (index != -1) {

                            String front = splans[j].substring(0, index);
                            String back = splans[j].substring(index + 1);
                            DetectRatingPlan drp = new DetectRatingPlan();
                            drp.setType(Util.str2int(front, 0));
                            drp.setValue(Util.str2int(back, 10));
                            drpList.add(drp);
                        }
                    }
                }

            } else if (args[i].equalsIgnoreCase("-verbose")) {
                verbose = Util.str2boolean(args[i + 1], verbose);
            } else if (args[i].equalsIgnoreCase("-extension")) {
                extension = args[i + 1];
            } else if (args[i].equalsIgnoreCase("-backup")) {
                backup = Util.str2int(args[i + 1], backup);
            } else if (args[i].equalsIgnoreCase("-span")) {
                span = Util.str2int(args[i + 1], span);
            }
        }

        if ((path != null) && (drpList.size() > 0)) {

            DetectRatingPlan[] plans =
                drpList.toArray(new DetectRatingPlan[drpList.size()]);
            DetectRating dr = new DetectRating();
            dr.setBackup(backup);
            dr.setSpan(span);
            File file = new File(path);
            if (file.isDirectory()) {

                int[] array = dr.processDirectory(ratingDir, file, extension,
                    plans, verbose);

            } else {

                //dr.examine(ratingDir, file, type, fudge, verbose);
            }
        }
    }

}


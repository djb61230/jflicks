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

import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;

/**
 * Simple class that will try to see if a set of Rating Symbol images
 * are contained in a larger image (screenshot) by finding rectangles.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class DetectRatingRectangle {

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

    public static final int MINIMUM_HEIGHT = 40;
    public static final int MINIMUM_WIDTH = 30;

    private int backup;
    private int span;

    /**
     * Default empty constructor.
     */
    public DetectRatingRectangle() {

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

    /**
     * Examine a particlur image file as determine whether it is a
     * "rating frame".
     *
     * @param f A File representing an image.
     * @param type Black or white symbol is expected.
     * @param fudge Wiggle room from full black or full white.
     * @param verbose Print out messages if true.
     * @return True if it is a "rating frame".
     * @throws IOException on an error.
     */
    public boolean examine(File f, int type, int fudge, boolean verbose) throws IOException {

        boolean result = false;

        BufferedImage bi = ImageIO.read(f);

        int x = 70;
        int y = 10;
        int w = 180;
        int h = 180;
        int[] data = new int[w * h];

        bi.getRGB(x, y, w, h, data, 0, w);

        String fname = f.getName();
        BufferedImage crop = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
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
                if ((Math.min(r, fudge) == r) && (Math.min(g, fudge) == g) && (Math.min(b, fudge) == b)) {

                    data[i] = 0x00000000;

                } else {

                    data[i] = 0x00ffffff;
                }

            } else if (type == WHITE_TYPE) {

                // If we are "near white" then turn it black, otherwise
                // turn it white.  This makes the rest of the code in this
                // class work when the rating box is black.
                if ((Math.max(r, fudge) == r) && (Math.max(g, fudge) == g) && (Math.max(b, fudge) == b)) {

                    data[i] = 0x00000000;

                } else {

                    data[i] = 0x00ffffff;
                }
            }
        }

        BufferedImage white = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        white.setRGB(0, 0, w, h, data, 0, w);

        if (verbose) {

            ImageIO.write(white, "png", new File(fname + "-white.png"));
        }

        // Ok here we add new code....
        boolean done = false;

        // No sense checking from the first row as the rating will not be there.
        int row = 4;
        while (!done) {

            //System.out.println("gern");
            Line2D.Double line = findLargestLine(data, w, h, row);
            //System.out.println("bobby");
            if (line != null) {

                // Check minimum width;
                int y1 = (int) line.getY1();
                int x1 = (int) line.getX1();
                int x2 = (int) line.getX2();
                int linelength = x2 - x1;
                if (linelength >= MINIMUM_WIDTH) {

                    int vertical = findVerticalLength(data, w, h, line);
                    if (vertical >= MINIMUM_HEIGHT) {

                        // Ok we have three sides of a rectangle.
                        // Now find the bottom Y to really see.
                        int bottomY = findBottomLineY(data, w, h, x1, x2, y1, vertical + y1 - 1);
                        int realheight = bottomY - y1;
                        if (realheight >= MINIMUM_HEIGHT) {

                            // We found one that fits the minimum sizes.  But we also
                            // know that these are always in "portrait mode" where they
                            // are taller than their width.
                            if (linelength < realheight) {

                                // Ok right shape of rectangle.  We have one more check,
                                // the line above our original line should be blank.
                                Line2D.Double aboveline = findLargestLine(data, w, h, y1 - 1);
                                if (aboveline != null) {

                                    // There is a line.  We make it on the same row and check
                                    // intersection.  No intersection means Ok.
                                    aboveline.setLine(aboveline.getX1(), y1, aboveline.getX2(), y1);
                                    if (!line.intersectsLine(aboveline)) {

                                        // Ok we take it.  Let's declare victory.
                                        done = true;
                                        result = true;
                                        System.out.println("Yes!");
                                    }

                                } else {

                                    done = true;
                                    result = true;
                                    System.out.println("Yes!");
                                }
                            }
                        }
                    }
                }
            }

            row++;

            if ((row + MINIMUM_HEIGHT) > h) {

                done = true;
            }
        }

        //System.out.println("examine done");
        return (result);
    }

    private int findBottomLineY(int[] data, int width, int height, int x1, int x2, int y1, int y2) {

        //System.out.println("findBottomLineY start y2 " + y2);
        int result = y2;

        int[] dline = new int[width];
        boolean done = false;
        while (!done) {

            int offset = result * width;
            for (int i = 0; i < width; i++) {

                dline[i] = data[offset + i];
            }

            //System.out.println("before next");
            int end = next(0x00ffffff, dline, x1);
            //System.out.println("after next " + end);
            if (end == -1) {

                if (width == x2) {

                    done = true;

                } else {

                    result--;
                }

            } else if (end == (x2 + 1)) {

                done = true;

            } else {

                result--;
            }

            if (!done) {

                if (result <= y1) {

                    done = true;
                }
            }
        }

        //System.out.println("findBottomLineY end");
        return (result);
    }

    private int findVerticalLength(int[] data, int width, int height, Line2D.Double line) {

        //System.out.println("findVerticalLength start " + width + " " + height);
        int result = 0;

        if ((data != null) && (line != null)) {

            int x1 = (int) line.getX1();
            int x2 = (int) line.getX2();
            int y = (int) line.getY1();
            int linewidth = x2 - x1;
            if (linewidth <= width) {

                while (y < height) {

                    //System.out.println("y " + y + " x2 " + x2);

                    int left = data[y * width + x1];
                    int leftOneLess = 0x00ffffff;
                    if (x1 > 0) {
                        leftOneLess = data[y * width + x1 - 1];
                    }
                    int right = data[y * width + x2];
                    int rightOneMore = 0x00ffffff;
                    if ((x2 + 1) < width) {
                        rightOneMore = data[y * width + x2 + 1];
                    }
                    if ((left == 0x00000000) && (right == 0x00000000)
                        && (leftOneLess == 0x00ffffff) && (rightOneMore == 0x00ffffff)) {

                        result++;
                        y++;

                    } else {

                        y = height;
                    }
                }
            }
        }

        //System.out.println("findVerticalLength end");
        return (result);
    }

    private Line2D.Double findLargestLine(int[] data, int width, int height, int y) {

        //System.out.println("largest line start");
        Line2D.Double result = null;

        if ((data != null) && (y < height)) {

            int[] dline = new int[width];
            int offset = y * width;
            for (int i = 0; i < width; i++) {

                dline[i] = data[offset + i];
            }

            boolean done = false;
            int index = 0;
            ArrayList<Line2D.Double> l = new ArrayList<Line2D.Double>();
            while (!done) {

                int start = next(0x00000000, dline, index);
                if (start >= 0) {

                    index = start + 1;
                    int end = next(0x00ffffff, dline, index);
                    if (end == -1) {

                        // We came to the end of the line at the end of the data.
                        done = true;
                        l.add(new Line2D.Double(start, y, dline.length - 1, y));

                    } else {

                        index = end;
                        l.add(new Line2D.Double(start, y, end - 1, y));
                    }

                } else {

                    done = true;
                }
            }

            if (l.size() > 0) {

                if (l.size() == 1) {

                    result = l.get(0);

                } else {

                    result = l.get(0);
                    double length = result.getX2() - result.getX1();
                    for (int i = 1; i < l.size(); i++) {

                        Line2D.Double tmp = l.get(i);
                        double tmplength = tmp.getX2() - tmp.getX1();
                        if (tmplength > length) {

                            length = tmplength;
                            result = tmp;
                        }
                    }
                }
            }
        }

        //System.out.println("largest line end");
        return (result);
    }

    private int next(int value, int[] line, int index) {

        int result = -1;

        if (line != null) {

            for (int i = index; i < line.length; i++) {

                if (line[i] == value) {

                    result = i;
                    break;
                }
            }
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
    public int[] processDirectory(File dir, String ext, int type, int fudge, boolean verbose) throws IOException {

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

                    if (examine(all[i], type, fudge, verbose)) {

                        int time = frameToSeconds(all[i]);
                        timelist.add(Integer.valueOf(time));
                        System.out.println(all[i] + " is a rating frame <" + time + ">");

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

    /**
     * Process a directory of images.
     *
     * @param dir The directory of images.
     * @param ext The file extention to use.
     * @param plans The palns to use.
     * @param verbose More debugging when true.
     * @throws IOException on an error.
     * @return An array of int values.
     */
    public int[] processDirectory(File dir, String ext, DetectRatingPlan[] plans, boolean verbose) throws IOException {

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
                            if (examine(all[i], type, fudge, verbose)) {

                                int time = frameToSeconds(all[i]);
                                timelist.add(Integer.valueOf(time));
                                System.out.println(all[i] + " is a rating frame <" + time + ">");

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

            DetectRatingPlan[] plans = drpList.toArray(new DetectRatingPlan[drpList.size()]);
            DetectRatingRectangle drr = new DetectRatingRectangle();
            drr.setBackup(backup);
            drr.setSpan(span);
            File file = new File(path);
            if (file.isDirectory()) {

                int[] array = drr.processDirectory(file, extension, plans, verbose);

            } else {

                //drr.examine(file, type, fudge, verbose);
            }
        }
    }

}


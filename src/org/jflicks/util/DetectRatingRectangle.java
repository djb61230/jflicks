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
     * The rating logo TEXT can be basically black in color.  This is a "hint"
     * that our code needs.
     */
    public static final int BLACK_TYPE = 0;

    /**
     * The rating logo TEXT can be basically white in color.  This is a "hint"
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
     * @param verbose Print out messages if true.
     * @return True if it is a "rating frame".
     * @throws IOException on an error.
     */
    public boolean examine(File f, int type, int red, int green, int blue, int range, boolean verbose,
        int planIndex) throws IOException {

        boolean result = false;

        BufferedImage bi = ImageIO.read(f);

        int x = 70;
        int y = 10;
        int w = 180;
        int h = 180;
        int[] data = new int[w * h];

        bi.getRGB(x, y, w, h, data, 0, w);

        double drange = range;
        String fname = f.getName();
        BufferedImage crop = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        crop.setRGB(0, 0, w, h, data, 0, w);

        if (verbose) {

            ImageIO.write(crop, "png", new File(fname + "-" + planIndex + "-crop.png"));
        }

        for (int i = 0; i < data.length; i++) {

            int r = data[i] & 0x00ff0000;
            r = r >> 16;
            int g = data[i] & 0x0000ff00;
            g = g >> 8;
            int b = data[i] & 0x000000ff;

            double distance = Math.sqrt(Math.pow(red - r, 2) + Math.pow(green - g, 2) + Math.pow(blue - b, 2));
            double percentage = distance / Math.sqrt(Math.pow(255, 2) + Math.pow(255, 2) + Math.pow(255, 2));
            if (type == BLACK_TYPE) {

                if (distance < drange) {

                    data[i] = 0x00000000;

                } else {

                    data[i] = 0x00ffffff;
                }

            } else if (type == WHITE_TYPE) {

                if (distance < drange) {

                    data[i] = 0x00000000;

                } else {

                    data[i] = 0x00ffffff;
                }
            }
        }

        BufferedImage white = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        white.setRGB(0, 0, w, h, data, 0, w);

        if (verbose) {

            ImageIO.write(white, "png", new File(fname + "-" + planIndex + "-white.png"));
            //ImageIO.write(tryit, "png", new File(fname + "-white.png"));
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
                    //System.out.println("minwidth " + linelength + " minheight " + vertical);
                    if (vertical >= MINIMUM_HEIGHT) {

                        // Ok we have three sides of a rectangle.
                        // Now find the bottom Y to really see.
                        int bottomY = findBottomLineY(data, w, h, x1, x2, y1, vertical + y1 - 1);
                        int realheight = bottomY - y1 + 1;
                        //System.out.println("bottomY " + bottomY + " y1 " + y1 + " realheight " + realheight);
                        if (realheight >= MINIMUM_HEIGHT) {

                            // We found one that fits the minimum sizes.  But we also
                            // know that these are generally in "portrait mode" where they
                            // are taller than their width.  But we have to handle squareish ones too.
                            if ((linelength <= realheight) || (Math.abs(linelength - realheight) < 10)) {

                                // Ok right shape of rectangle.  We have one more check,
                                // the line above our original line should be blank.  How ever
                                // often this line is broken up so lets go up 2.
                                Line2D.Double aboveline = findLargestLine(data, w, h, y1 - 2);
                                if (aboveline != null) {

                                    // There is a line.  We make it on the same row and check
                                    // intersection.  No intersection means Ok.
                                    aboveline.setLine(aboveline.getX1(), y1, aboveline.getX2(), y1);
                                    if (!line.intersectsLine(aboveline)) {

                                        // Ok we take it.  Let's declare victory.
                                        done = true;
                                        result = true;
                                        System.out.println("Yes!");

                                    } else {

                                        //System.out.println("tossed because line intersect");
                                        //System.out.println(aboveline);
                                        //System.out.println(line);
                                    }

                                } else {

                                    done = true;
                                    result = true;
                                    System.out.println("Yes!");
                                }

                            } else {

                                //System.out.println("tossed because not portrait");
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

                int maxMissing = 10;
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
                    int passes = 0;
                    if (left == 0x00000000) {
                        passes++;
                    }
                    if (right == 0x00000000) {
                        passes++;
                    }
                    if (leftOneLess == 0x00ffffff) {
                        passes++;
                    }
                    if (rightOneMore == 0x00ffffff) {
                        passes++;
                    }

                    if (passes == 4) {

                        result++;
                        y++;

                    } else {

                        if ((passes == 3) && (maxMissing > 0)) {

                            maxMissing--;
                            result++;
                            y++;

                        } else {

                            y = height;
                        }
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

            // The first frame bogus.  Plus we start the count at 1
            // instead of 0.  So we take off 2 from the frame.
            result -= 2;
            result *= getSpan();
            result -= getBackup();
            if (result < 0) {
                result = 0;
            }
        }

        return (result);
    }

    /**
     * Process a directory of images.
     *
     * @param dir The directory of images.
     * @param ext The file extention to use.
     * @param plans The palns to use.
     * @param verbose More debugging when true.
     * @throws IOException on an error.
     * @return An array of DetectResult instances.
     */
    public DetectResult[] processDirectory(File dir, String ext, DetectRatingPlan[] plans, boolean verbose)
        throws IOException {

        DetectResult[] result = null;

        if ((plans != null) && (plans.length > 0) && (dir != null)
            && (ext != null)) {

            String[] array = new String[1];
            array[0] = ext;
            ExtensionsFilter ef = new ExtensionsFilter(array);
            File[] all = dir.listFiles(ef);
            if ((all != null) && (all.length > 0)) {

                ArrayList<DetectResult> drlist = new ArrayList<DetectResult>();
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
                            int red = plans[j].getRed();
                            int green = plans[j].getGreen();
                            int blue = plans[j].getBlue();
                            int range = plans[j].getRange();
                            if (examine(all[i], type, red, green, blue, range, verbose, j)) {

                                int time = frameToSeconds(all[i]);
                                DetectResult dr = new DetectResult();
                                dr.setTime(time);
                                dr.setFile(all[i]);
                                drlist.add(dr);
                                System.out.println(all[i] + " is a rating frame <" + time + ">");

                                // Since we just found one, lets assume the next
                                // 30 seconds or so we don't need to check.
                                int fcount = (int) (30 / getSpan());
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

                if (drlist.size() > 0) {

                    result = drlist.toArray(new DetectResult[drlist.size()]);
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
            } else if (args[i].equalsIgnoreCase("-type:red:green:blue:range")) {

                String[] splans = args[i + 1].split(",");
                if ((splans != null) && (splans.length > 0)) {

                    for (int j = 0; j < splans.length; j++) {

                        String[] breakup = splans[j].split(":");
                        if ((breakup != null) && (breakup.length == 5)) {

                            DetectRatingPlan drp = new DetectRatingPlan();
                            drp.setType(Util.str2int(breakup[0], 0));
                            drp.setRed(Util.str2int(breakup[1], 0));
                            drp.setGreen(Util.str2int(breakup[2], 0));
                            drp.setBlue(Util.str2int(breakup[3], 0));
                            drp.setRange(Util.str2int(breakup[4], 0));
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

                DetectResult[] array = drr.processDirectory(file, extension, plans, verbose);
            }
        }
    }

}


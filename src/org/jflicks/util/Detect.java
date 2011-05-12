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
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class Detect {

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

    /**
     * Default empty constructor.
     */
    public Detect() {

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

    private Line2D.Double[] findLines(int[] data, int w, int h, int min) {

        Line2D.Double[] result = null;

        ArrayList<Line2D.Double> list = new ArrayList<Line2D.Double>();

        for (int col = 0; col < w; col++) {

            int startrow = -1;
            int endrow = -1;
            int index = col;
            for (int row = 0; row < h; row++) {

                if (data[index] == 0) {

                    if (startrow == -1) {

                        startrow = row;
                        endrow = row;

                    } else {

                        endrow = row;
                    }

                } else {

                    // We have white space so the line is ended here (well
                    // if it was ever started).  We also only care if the
                    // line is at least min length.
                    if ((startrow != -1) && ((endrow - startrow) > min)) {

                        list.add(new Line2D.Double((double) col,
                            (double) startrow, (double) col, (double) endrow));
                    }

                    startrow = -1;
                    endrow = -1;
                }

                index += w;
            }
        }

        if (list.size() > 0) {

            result = list.toArray(new Line2D.Double[list.size()]);
        }

        return (result);
    }

    private Point2D.Double computeLeft(boolean top, Line2D.Double[] array,
        int[] data, int w, int h, boolean verbose) {

        Point2D.Double result = null;

        if (array != null) {

            // Count the occurences of the proper Y.
            HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
            for (int i = 0; i < array.length; i++) {

                int y = 0;
                if (top) {
                    y = (int) array[i].getY1();
                } else {
                    y = (int) array[i].getY2();
                }
                Integer val = Integer.valueOf(y);
                Integer lookup = hm.get(val);
                if (lookup == null) {

                    hm.put(val, Integer.valueOf(1));

                } else {

                    hm.put(val, Integer.valueOf(lookup.intValue() + 1));
                }
            }

            if (verbose) {
                System.out.println(hm);
            }
            Set<Map.Entry<Integer, Integer>> set = hm.entrySet();
            Iterator<Map.Entry<Integer, Integer>> iter = set.iterator();
            int max = -1;
            Integer val = null;
            while (iter.hasNext()) {

                Map.Entry<Integer, Integer> me = iter.next();
                Integer tmax = me.getValue();
                if (tmax.intValue() > max) {

                    max = tmax.intValue();
                    val = me.getKey();
                }
            }

            if (max != -1) {

                // Find the left most line that matches up with our Y value.
                int findex = 0;
                for (int i = 0; i < array.length; i++) {

                    double dy = 0.0;
                    if (top) {
                        dy = array[i].getY1();
                    } else {
                        dy = array[i].getY2();
                    }
                    if (Math.abs(dy - val.doubleValue()) <= 2.0) {

                        // Now we only think we have it if we have a
                        // non-zero length horizontal line.  Let's
                        // check it.
                        if (computeLineLength(array[i].getX1(),
                            val.doubleValue(), data, w, h) > 0) {

                            findex = i;
                            break;
                        }
                    }
                }

                result = new Point2D.Double(array[findex].getX1(),
                    val.doubleValue());
            }
        }

        return (result);
    }

    private int computeLineLength(double x, double y, int[] data, int w,
        int h) {

        return computeLineLength(new Point2D.Double(x, y), data, w, h);
    }

    private int computeLineLength(Point2D.Double p, int[] data, int w, int h) {

        int result = 0;

        if ((p != null) && (data != null)) {

            int col = (int) p.getX();
            int row = (int) p.getY();
            int index = row * w + col;
            boolean done = false;
            while (!done) {

                if (data[index] == 0) {
                    result++;
                    index++;
                } else {
                    done = true;
                }
            }

            if (result > (w - col)) {

                result = w - col;
            }
        }

        return (result);
    }

    private Line2D.Double findLine(Line2D.Double[] array, int y1, int y2,
        int mincol, int maxcol, boolean verbose) {

        Line2D.Double result = null;

        if (array != null) {

            double dy1 = (double) y1;
            double dy2 = (double) y2;

            if (verbose) {

                System.out.println("y1: " + y1);
                System.out.println("y2: " + y2);
                System.out.println("mincol: " + mincol);
                System.out.println("maxcol: " + maxcol);
            }

            // We are going to look for more than one line...
            for (int col = mincol; col < maxcol; col++) {

                double dcol = (double) col;
                for (int i = 0; i < array.length; i++) {

                    // Now we find a correct line if it contains both
                    // points defined by our other arguments...
                    if ((array[i].ptSegDist(dcol, dy1) == 0.0)
                        && (array[i].ptSegDist(dcol, dy2) == 0.0)) {

                        result = array[i];
                    }
                }
            }
        }

        return (result);
    }

    private double computeWhitespace(Rectangle r, int[] data, int w, int h) {

        double result = 0.0;

        if ((r != null) && (data != null)) {

            double count = 0.0;
            int cols = r.width;
            for (int row = 0; row < r.height; row++) {

                int index = (r.y + row) * w + r.x;
                for (int col = 0; col < r.width; col++) {

                    if (data[index] != 0) {
                        count += 1.0;
                    }
                    index++;
                }
            }

            double max = r.width * r.height;
            result = count / max;
        }

        return (result);
    }

    private double computeBorder(Rectangle r, int[] data, int w, int h) {

        double result = 0.0;

        if ((r != null) && (data != null)) {

            double max = 2 * r.height + 2 * r.width;
            double count = 0.0;
            int cols = r.width;
            for (int row = 0; row < r.height; row++) {

                int index = (r.y + row) * w + r.x;
                if ((row == 0) || (row == (r.height - 1))) {

                    for (int col = 0; col < r.width; col++) {

                        if (data[index] == 0) {
                            count += 1.0;
                        }
                        index++;
                    }

                } else {

                    if (data[index] == 0) {
                        count += 1.0;
                    }

                    index += r.width;
                    if (data[index] == 0) {
                        count += 1.0;
                    }
                }
            }

            result = count / max;
        }

        return (result);
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
    public boolean examine(File f, int type, int fudge, boolean verbose)
        throws IOException {

        boolean result = false;

        BufferedImage bi = ImageIO.read(f);

        int x = 66;
        int y = 0;
        int w = 200;
        int h = 200;
        int min = 44;
        int minx = 10;
        int miny = 20;
        double whitespace = 0.25;
        double border = 0.65;
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

        Line2D.Double[] lines = findLines(data, w, h, min);
        if (lines != null) {

            if (verbose) {
                System.out.println("Found " + lines.length
                    + " interesting lines.");
            }
            for (int i = 0; i < lines.length; i++) {

                int x1 = (int) lines[i].getX1();
                int y1 = (int) lines[i].getY1();
                int x2 = (int) lines[i].getX2();
                int y2 = (int) lines[i].getY2();
                if (verbose) {

                    System.out.print("(X1, Y1)-(X2, Y2): (" + x1 + ", " + y1
                        + ")");
                    System.out.print("-(" + x2 + ", " + y2 + ")");
                    System.out.println(" length " + (y2 - y1));
                }
            }

            Point2D.Double toppt =
                computeLeft(true, lines, data, w, h, verbose);
            int toplength = computeLineLength(toppt, data, w, h);

            Point2D.Double botpt =
                computeLeft(false, lines, data, w, h, verbose);
            int botlength = computeLineLength(botpt, data, w, h);

            if (verbose) {

                System.out.println("topleft could be: " + toppt);
                System.out.println("top line length looks like: " + toplength);
                System.out.println("botleft could be: " + botpt);
                System.out.println("bottom line length looks like: "
                    + botlength);
            }

            int mincol = Math.min(toplength, botlength) - 5;
            mincol += (int) toppt.getX();
            int maxcol = Math.max(toplength, botlength);
            maxcol += (int) toppt.getX();
            int toprow = (int) toppt.getY();
            int botrow = (int) botpt.getY();

            Line2D.Double rightline =
                findLine(lines, toprow, botrow, mincol, maxcol, verbose);

            if (rightline != null) {

                int x1 = (int) rightline.getX1();
                int y1 = (int) rightline.getY1();
                int x2 = (int) rightline.getX2();
                int y2 = (int) rightline.getY2();
                if (verbose) {

                    System.out.println("rightline");
                    System.out.print("(X1, Y1)-(X2, Y2): (" + x1 + ", " + y1
                        + ")");
                    System.out.print("-(" + x2 + ", " + y2 + ")");
                    System.out.println(" length " + (y2 - y1));
                }

                // At this point we think we have found the rectangle
                // that is the "rating box".  We could have a false
                // positive where we are looking at a blank screen or
                // something.  Lets check the rectangle - it should have
                // a high percentage of white pixels representing the
                // text.
                int rx = (int) toppt.getX();
                int ry = (int) toppt.getY();
                int rw = (int) (x1 - rx) + 1;
                int rh = (botrow - toprow) + 1;

                if ((rx > minx) && (ry > miny)) {

                    Rectangle rect = new Rectangle(rx, ry, rw, rh);

                    double percent = computeWhitespace(rect, data, w, h);
                    result = (percent >= whitespace);

                    if (result) {

                        result = computeBorder(rect, data, w, h) > border;
                    }
                    if (verbose) {

                        System.out.println("Rectangle: " + rect);
                        System.out.println("percent whitespace: " + percent);
                    }
                }

            } else {

                if (verbose) {

                    System.out.println("Can't find right vertical line");
                }
            }

        } else {

            if (verbose) {

                System.out.println("Didn't find any interesting lines.");
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
    public int[] processDirectory(File dir, String ext, int type,
        int fudge, boolean verbose) throws IOException {

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
                        System.out.println(all[i] + " is a rating frame <"
                            + time + ">");

                        // Since we just found one, lets assume the next
                        // 20 seconds or so we don't need to check.
                        int fcount = (int) (20 / getSpan());
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
     * Simple main method that tests this class.
     *
     * @param args Arguments that happen to be ignored.
     * @throws IOException on an error.
     */
    public static void main(String[] args) throws IOException {

        String path = null;
        int type = BLACK_TYPE;
        boolean verbose = false;
        int fudge = 10;
        String extension = "jpg";
        int backup = 0;
        int span = 0;

        for (int i = 0; i < args.length; i += 2) {

            if (args[i].equalsIgnoreCase("-path")) {
                path = args[i + 1];
            } else if (args[i].equalsIgnoreCase("-type")) {
                type = Util.str2int(args[i + 1], type);
            } else if (args[i].equalsIgnoreCase("-fudge")) {
                fudge = Util.str2int(args[i + 1], fudge);
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

        if (path != null) {

            Detect detect = new Detect();
            detect.setBackup(backup);
            detect.setSpan(span);
            File file = new File(path);
            if (file.isDirectory()) {

                int[] array = detect.processDirectory(file, extension, type,
                    fudge, verbose);

            } else {

                detect.examine(file, type, fudge, verbose);
            }
        }
    }

}


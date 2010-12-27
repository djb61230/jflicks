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

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

//import com.sun.awt.AWTUtilities;

/**
 * Some very basic methods that capture some common tasks.  Implemented here
 * so other classes can reduce duplicated code.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class Util {

    /**
     * Default empty constructor.
     */
    private Util() {
    }

    /**
     * Parse a string and determine it's integer value.  If there is a
     * problem then return a default value.
     *
     * @param s A given string to parse.
     * @param defaultValue A default value to return on any problem.
     * @return The value of the parsed string.
     */
    public static int str2int(String s, int defaultValue) {

        int result = defaultValue;

        if (s != null) {

            try {

                result = Integer.parseInt(s);

            } catch (Exception ex) {

                result = defaultValue;
            }
        }

        return (result);
    }

    /**
     * Parse a string and determine it's integer value.  If there is a
     * problem then return a default value.
     *
     * @param s A given string to parse.
     * @param defaultValue A default value to return on any problem.
     * @return The value of the parsed string.
     */
    public static Integer str2Integer(String s, int defaultValue) {

        int result = defaultValue;

        if (s != null) {

            try {

                result = Integer.valueOf(s);

            } catch (Exception ex) {

                result = Integer.valueOf(defaultValue);
            }
        }

        return (result);
    }

    /**
     * Parse a string and determine it's long value.  If there is a
     * problem then return a default value.
     *
     * @param s A given string to parse.
     * @param defaultValue A default value to return on any problem.
     * @return The value of the parsed string.
     */
    public static long str2long(String s, long defaultValue) {

        long result = defaultValue;

        if (s != null) {

            try {

                result = Long.parseLong(s);

            } catch (Exception ex) {

                result = defaultValue;
            }
        }

        return (result);
    }

    /**
     * Parse a string and determine it's long value.  If there is a
     * problem then return a default value.
     *
     * @param s A given string to parse.
     * @param defaultValue A default value to return on any problem.
     * @return The value of the parsed string.
     */
    public static Long str2Long(String s, int defaultValue) {

        long result = defaultValue;

        if (s != null) {

            try {

                result = Long.valueOf(s);

            } catch (Exception ex) {

                result = Long.valueOf(defaultValue);
            }
        }

        return (result);
    }

    /**
     * Parse a string and determine it's double value.  If there is a
     * problem then return a default value.
     *
     * @param s A given string to parse.
     * @param defaultValue A default value to return on any problem.
     * @return The value of the parsed string.
     */
    public static double str2double(String s, double defaultValue) {

        double result = defaultValue;

        if (s != null) {

            try {

                result = Double.parseDouble(s);

            } catch (Exception ex) {

                result = defaultValue;
            }
        }

        return (result);
    }

    /**
     * Parse a string and determine it's double value.  If there is a
     * problem then return a default value.
     *
     * @param s A given string to parse.
     * @param defaultValue A default value to return on any problem.
     * @return The value of the parsed string.
     */
    public static Double str2Double(String s, double defaultValue) {

        double result = defaultValue;

        if (s != null) {

            try {

                result = Double.valueOf(s);

            } catch (Exception ex) {

                result = Double.valueOf(defaultValue);
            }
        }

        return (result);
    }

    /**
     * Parse a string and determine it's boolean value.  If there is a
     * problem then return a default value.
     *
     * @param s A given string to parse.
     * @param defaultValue A default value to return on any problem.
     * @return The value of the parsed string.
     */
    public static boolean str2boolean(String s, boolean defaultValue) {

        boolean result = defaultValue;

        if (s != null) {

            try {

                result = Boolean.parseBoolean(s);

            } catch (Exception ex) {

                result = defaultValue;
            }
        }

        return (result);
    }

    /**
     * Parse a string and determine it's boolean value.  If there is a
     * problem then return a default value.
     *
     * @param s A given string to parse.
     * @param defaultValue A default value to return on any problem.
     * @return The value of the parsed string.
     */
    public static Boolean str2Boolean(String s, boolean defaultValue) {

        Boolean result = Boolean.FALSE;

        if (s != null) {

            try {

                result = Boolean.valueOf(s);

            } catch (Exception ex) {

                if (defaultValue) {
                    result = Boolean.TRUE;
                }
            }
        }

        return (result);
    }

    /**
     * We have this method to convert the "standard" Color constants as a
     * String to their Color value.
     *
     * @param s A given String to examine.
     * @param defaultValue A default Color to return in case we fail.
     * @return A Color instance.
     */
    public static Color str2Color(String s, Color defaultValue) {

        Color result = defaultValue;

        if (s != null) {

            if (s.equalsIgnoreCase("BLACK")) {

                result = Color.BLACK;

            } else if (s.equalsIgnoreCase("BLUE")) {

                result = Color.BLUE;

            } else if (s.equalsIgnoreCase("CYAN")) {

                result = Color.CYAN;

            } else if (s.equalsIgnoreCase("DARK_GRAY")) {

                result = Color.DARK_GRAY;

            } else if (s.equalsIgnoreCase("GRAY")) {

                result = Color.GRAY;

            } else if (s.equalsIgnoreCase("GREEN")) {

                result = Color.GREEN;

            } else if (s.equalsIgnoreCase("LIGHT_GRAY")) {

                result = Color.LIGHT_GRAY;

            } else if (s.equalsIgnoreCase("MAGENTA")) {

                result = Color.MAGENTA;

            } else if (s.equalsIgnoreCase("ORANGE")) {

                result = Color.ORANGE;

            } else if (s.equalsIgnoreCase("PINK")) {

                result = Color.PINK;

            } else if (s.equalsIgnoreCase("RED")) {

                result = Color.RED;

            } else if (s.equalsIgnoreCase("WHITE")) {

                result = Color.WHITE;

            } else if (s.equalsIgnoreCase("YELLOW")) {

                result = Color.YELLOW;
            }
        }

        return (result);
    }

    /**
     * We have this method to help convert a String to a font style.
     *
     * @param s A given String to examine.
     * @param defaultValue Some int style default value if we fail in some way.
     * @return An int value.
     */
    public static int str2FontStyle(String s, int defaultValue) {

        int result = defaultValue;

        if (s != null) {

            if (s.equalsIgnoreCase("PLAIN")) {

                result = Font.PLAIN;

            } else if (s.equalsIgnoreCase("ITALIC")) {

                result = Font.ITALIC;

            } else if (s.equalsIgnoreCase("BOLD")) {

                result = Font.BOLD;

            } else if ((s.indexOf("BOLD") != -1)
                && (s.indexOf("PLAIN") != -1)) {

                result = Font.BOLD | Font.PLAIN;

            } else if ((s.indexOf("BOLD") != -1)
                && (s.indexOf("ITALIC") != -1)) {

                result = Font.BOLD | Font.ITALIC;
            }
        }

        return (result);
    }

    /**
     * Parse a String and determine if it is a month.  If so then return an
     * int value where January = 0, February = 1, etc.
     *
     * @param month A given month as a String.
     * @return An int value representing the month.
     */
    public static int month2int(String month) {

        int result = 0;

        if (month != null) {

            if (month.equalsIgnoreCase("january")) {
                result = 0;
            } else if (month.equalsIgnoreCase("february")) {
                result = 1;
            } else if (month.equalsIgnoreCase("march")) {
                result = 2;
            } else if (month.equalsIgnoreCase("april")) {
                result = 3;
            } else if (month.equalsIgnoreCase("may")) {
                result = 4;
            } else if (month.equalsIgnoreCase("june")) {
                result = 5;
            } else if (month.equalsIgnoreCase("july")) {
                result = 6;
            } else if (month.equalsIgnoreCase("august")) {
                result = 7;
            } else if (month.equalsIgnoreCase("september")) {
                result = 8;
            } else if (month.equalsIgnoreCase("october")) {
                result = 9;
            } else if (month.equalsIgnoreCase("november")) {
                result = 10;
            } else if (month.equalsIgnoreCase("december")) {
                result = 11;
            }
        }

        return (result);
    }

    /**
     * Examine an int and determine if it is a month.  If so then return a
     * String value where 0 = January, 1 = February, etc.
     *
     * @param month A given month as an int.
     * @return A String value representing the month.
     */
    public static String month2String(int month) {

        String result = "January";

        if (month == 0) {
            result = "January";
        } else if (month == 1) {
            result = "February";
        } else if (month == 2) {
            result = "March";
        } else if (month == 3) {
            result = "April";
        } else if (month == 4) {
            result = "May";
        } else if (month == 5) {
            result = "June";
        } else if (month == 6) {
            result = "July";
        } else if (month == 7) {
            result = "August";
        } else if (month == 8) {
            result = "September";
        } else if (month == 9) {
            result = "October";
        } else if (month == 10) {
            result = "November";
        } else if (month == 11) {
            result = "December";
        }

        return (result);
    }

    /**
     * Examine an int and determine if it is a day of the week.  If so then
     * return a String value where 1 = Sunday, 2 = Monday, etc.
     *
     * @param dayofweek A given day of the week as an int.
     * @return An String value representing the day of the week.
     */
    public static String day2String(int dayofweek) {

        String result = "Sunday";

        if (dayofweek == 1) {
            result = "Sunday";
        } else if (dayofweek == 2) {
            result = "Monday";
        } else if (dayofweek == 3) {
            result = "Tuesday";
        } else if (dayofweek == 4) {
            result = "Wednesday";
        } else if (dayofweek == 5) {
            result = "Thursday";
        } else if (dayofweek == 6) {
            result = "Friday";
        } else if (dayofweek == 7) {
            result = "Saturday";
        }

        return (result);
    }

    /**
     * Using the system properties determine if we are running on Windows.
     *
     * @return True if we are on Windows.
     */
    public static boolean isWindows() {
        return (isOsName("Windows"));
    }

    /**
     * Using the system properties determine if we are running on Mac.
     *
     * @return True if we are on Mac.
     */
    public static boolean isMac() {
        return (isOsName("Mac"));
    }

    /**
     * Using the system properties determine if we are running on Linux.
     *
     * @return True if we are on Linux.
     */
    public static boolean isLinux() {
        return (isOsName("Linux"));
    }

    private static boolean isOsName(String name) {

        boolean result = false;

        Properties p = System.getProperties();
        if (p != null) {

            String s = p.getProperty("os.name");
            if (s != null) {

                result = s.startsWith(name);
            }
        }

        return (result);
    }

    /**
     * Simple method that will compute a components location on the screen.
     *
     * @param c A given Component.
     * @return A location of it's top-left corner as a Point instance.
     */
    public static Point screenLocation(Component c) {

        Point result = new Point();

        if (c != null) {

            SwingUtilities.convertPointToScreen(result, c);
        }

        return (result);
    }

    /**
     * Given a Component, find the Frame that it is a child of.
     *
     * @param c A given Component.
     * @return The Frame the component resides in.
     */
    public static Frame findFrame(Component c) {

        Component result = c;

        while (!(result instanceof Frame)) {

            result = result.getParent();
        }

        return ((Frame) result);
    }

    /**
     * Given a Component, find the Dialog that it is a child of.
     *
     * @param c A given Component.
     * @return The Dialog the component resides in.
     */
    public static Dialog findDialog(Component c) {

        Component result = c;

        while (!(result instanceof Dialog)) {

            result = result.getParent();
        }

        return ((Dialog) result);
    }

    /**
     * Given a Component, find the Window that it is a child of.
     *
     * @param c A given Component.
     * @return The Window the component resides in.
     */
    public static Window findWindow(Component c) {

        Component result = c;

        while (!(result instanceof Window)) {

            result = result.getParent();
        }

        return ((Window) result);
    }

    /**
     * Sometimes you want a laid out component as an image.  Suitable for
     * printing or saving to disk etc.
     *
     * @param c The given component to turn into an image.
     * @return a BufferedImage instance.
     */
    public static BufferedImage componentToImage(Component c) {

        BufferedImage result = null;

        if (c != null) {

            Dimension bounds = c.getPreferredSize();
            result = new BufferedImage(bounds.width, bounds.height,
                BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = result.createGraphics();
            g2d.setClip(new java.awt.Rectangle(c.getSize()));
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, bounds.width, bounds.height);
            g2d.setComposite(AlphaComposite.SrcOver);
            c.paint(g2d);

            g2d.dispose();
        }

        return (result);
    }

    /**
     * Rotate a BufferedImage 90 degrees clockwise.
     *
     * @param bi A given image to rotate.
     * @return A new image that is rotated 90 degrees clockwise.
     */
    public static BufferedImage rotate90(BufferedImage bi) {

        BufferedImage result = null;

        if (bi != null) {

            int w = bi.getWidth();
            int h = bi.getHeight();
            int endw = h;
            int endh = w;
            int max = w;
            if (h > w) {

                max = h;
                endw = w;
                endh = h;
            }
            result = new BufferedImage(max, max, BufferedImage.TYPE_INT_ARGB);
            Graphics2D bg = result.createGraphics();
            double dmax = ((double) max) / 2.0;
            bg.rotate(Math.toRadians(90), dmax, dmax);
            bg.drawImage(bi, 0, 0, w, h, 0, 0, w, h, null);

            bg.dispose();

            if (endw == h) {

                result = result.getSubimage(max - endw, 0, endw, endh);

            } else {

                result = result.getSubimage(0, 0, endh, endw);
            }
        }

        return (result);
    }

    /**
     * This will scale an image in the "x" direction.  It also will
     * keep the aspect ratio intact.
     *
     * @param bi A given image to scale.
     * @param toWidth The new width desired.
     * @return An updated scaled image.
     */
    public static BufferedImage scale(BufferedImage bi, int toWidth) {

        BufferedImage result = bi;

        if (bi != null) {

            double sx = (double) ((double) toWidth / (double) bi.getWidth());
            AffineTransform tx = new AffineTransform();
            tx.scale(sx, sx);
            AffineTransformOp op = new AffineTransformOp(tx,
                AffineTransformOp.TYPE_BILINEAR);
            result = op.filter(bi, null);
        }

        return (result);
    }

    /**
     * Resize the given image.
     *
     * @param bi An image to resize.
     * @param w The new width.
     * @param h the new height.
     * @return A new BufferedImage instance.
     */
    public static BufferedImage resize(BufferedImage bi, int w, int h) {

        BufferedImage result = bi;

        if (bi != null) {

            result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = result.createGraphics();
            double scalex = (double) w;
            scalex = scalex / (double) bi.getWidth();
            double scaley = (double) h;
            scaley = scaley / (double) bi.getHeight();
            AffineTransform xform = AffineTransform.getScaleInstance(scalex,
                scaley);
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics2D.drawImage(bi, xform, null);
            graphics2D.dispose();
        }

        return (result);
    }

    /**
     * Show the dialog with OK, Cancel buttons.
     *
     * @param parent Parent JFrame to locate dialog near.
     * @param title Dialog box title.
     * @param c The JComponent that is the main UI.
     * @return True if the User hit "OK".
     */
    public static boolean showDialog(Frame parent, String title, JComponent c) {
        return (showDialog(parent, title, c, true));
    }

    /**
     * Show the dialog with optional Cancel button.
     *
     * @param parent Parent JFrame to locate dialog near.
     * @param title Dialog box title.
     * @param c The JComponent that is the main UI.
     * @param includeCancel False if you want a simple OK dialog.
     * @return True if the User hit "OK".
     */
    public static boolean showDialog(Frame parent, String title, JComponent c,
        boolean includeCancel) {

        boolean result = false;
        final JButton ok = new JButton("OK");
        final JButton cancel = new JButton("Cancel");
        final JDialog dialog = new JDialog(parent, title, true);
        ok.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ae) {

                    dialog.setVisible(false);
                    ok.setText("true");
                    dialog.dispose();
                }
            }
        );
        cancel.addActionListener(
            new ActionListener() {

                public void actionPerformed(ActionEvent ae) {

                    dialog.setVisible(false);
                    dialog.dispose();
                }
            }
        );

        dialog.getContentPane().setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        dialog.getContentPane().add(c, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        dialog.getContentPane().add(new JSeparator(), gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(4, 4, 4, 4);

        dialog.getContentPane().add(ok, gbc);

        if (includeCancel) {

            gbc = new GridBagConstraints();
            gbc.weightx = 0.5;
            gbc.weighty = 0.0;
            gbc.gridwidth = 1;
            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;
            gbc.insets = new Insets(4, 4, 4, 4);

            dialog.getContentPane().add(cancel, gbc);
        }

        dialog.pack();

        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        result = ok.getText().equals("true");

        return (result);
    }

    /**
     * A simple dialog that allows one to show a user a given component.
     * The idea is that the user can interact with the component and then
     * just dismiss the dialog with a "done" button.
     *
     * @param parent The Frame to center upon.
     * @param title The tile for the dialog box.
     * @param c The component to display.
     */
    public static void showDoneDialog(Frame parent, String title,
        JComponent c) {

        final JButton done = new JButton("Done");
        final JDialog dialog = new JDialog(parent, title, true);
        done.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ae) {

                    dialog.setVisible(false);
                    done.setText("true");
                    dialog.dispose();
                }
            }
        );

        dialog.getContentPane().setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        dialog.getContentPane().add(c, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);
        dialog.getContentPane().add(new JSeparator(), gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(4, 4, 4, 4);

        dialog.getContentPane().add(done, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    /**
     * A nice method that will load a properties file for a given object.
     * The properties file is assumed to be in the same package location
     * as the object.  This makes for reading properties files that are
     * located in jar files easy.  Handy for runtime read-only properties.
     *
     * @param o A given object.
     * @param s A properties file name.
     * @return An instantiated Properties file.
     */
    public static Properties findProperties(Object o, String s) {

        Properties result = new Properties();

        if ((o != null) && (s != null)) {

            URL url = o.getClass().getResource(s);
            try {

                InputStream is = url.openStream();
                result.load(is);
                is.close();

            } catch (Exception ex) {

                result = null;
            }
        }

        return (result);
    }

    /**
     * Simple method to load a Properties object from a file path.
     *
     * @param path The given File as a String path to read.
     * @return A Properties instance.
     */
    public static Properties findProperties(String path) {
        return findProperties(new File(path));
    }

    /**
     * Simple method to load a Properties object from a File.
     *
     * @param file The given File to read.
     * @return A Properties instance.
     */
    public static Properties findProperties(File file) {

        Properties result = null;

        if (file != null) {

            result = new Properties();
            FileInputStream fis = null;
            try {

                fis = new FileInputStream(file);
                result.load(fis);
                fis.close();

            } catch (IOException ex) {

                result = null;

            } finally {

                if (fis != null) {

                    try {

                        fis.close();

                    } catch (IOException ex) {

                        fis = null;
                    }
                }
            }
        }

        return (result);
    }

    /**
     * Write out our properties with the tags in alpha order.
     *
     * @param f A File to write - will clobber so be careful.
     * @param p The Properties object to write.
     */
    public static void writeProperties(File f, Properties p) {

        if ((f != null) && (p != null)) {

            Set<String> set = p.stringPropertyNames();
            String[] array = set.toArray(new String[set.size()]);
            Arrays.sort(array);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; i++) {

                String value = p.getProperty(array[i]);
                if (value == null) {

                    value = "";
                }

                sb.append(array[i] + "=" + value + "\n");
            }

            try {

                Util.writeTextFile(f, sb.toString());

            } catch (IOException ex) {

                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * If a given image is too small for the given width, then scale it
     * so it fits exactly.
     *
     * @param width A given width;
     * @param bi A given image.
     * @return A scaled image that is the same size as the width.
     */
    public static BufferedImage scaleLarger(int width, BufferedImage bi) {

        BufferedImage result = bi;

        if (bi != null) {

            if (bi.getWidth() < width) {

                result = Util.scale(bi, width);
            }
        }

        return (result);
    }

    private static String convertToHex(byte[] data) {

        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < data.length; i++) {

            int halfbyte = (data[i] >>> 4) & 0x0F;
            int twohalfs = 0;
            do {

                if ((0 <= halfbyte) && (halfbyte <= 9)) {

                    buf.append((char) ('0' + halfbyte));

                } else {

                    buf.append((char) ('a' + (halfbyte - 10)));
                }

                halfbyte = data[i] & 0x0F;

            } while (twohalfs++ < 1);
        }

        return (buf.toString());
    }

    /**
     * Method to hash an input String to a MD5 String.
     *
     * @param text Some given text.
     * @return An MD5 text String.
     */
    public static String toMD5(String text) {

        String result = null;

        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            byte[] md5hash = md.digest();
            result = convertToHex(md5hash);

        } catch (NoSuchAlgorithmException ex) {

            result = null;

        } catch (UnsupportedEncodingException ex) {

            result = null;
        }

        return (result);
    }

    /**
     * Read the entire file into a byte array.
     *
     * @param file The file to read.
     * @return The file contents as a byte array.
     */
    public static byte[] read(File file) {

        byte[] result = null;

        if ((file != null) && (file.exists()) && (file.isFile())) {

            FileInputStream fis = null;
            DataInputStream dis = null;

            try {

                int offset = 0;
                int count = 0;
                int total = (int) file.length();
                result = new byte[total];
                fis = new FileInputStream(file);
                dis = new DataInputStream(fis);
                dis.readFully(result);
                dis.close();
                fis.close();
                fis = null;

            } catch (IOException ex) {
            } finally {

                if (fis != null) {

                    try {

                        dis.close();
                        fis.close();

                    } catch (IOException ex) {

                        dis = null;
                        fis = null;
                    }
                }
            }
        }

        return (result);
    }

    /**
     * Read a text file into a String array object.
     *
     * @param file File object representing a text file.
     * @return The file read into a String array object.
     */
    public static String[] readTextFile(File file) {

        String[] result = null;

        ArrayList<String> work = new ArrayList<String>();
        if (file != null) {

            BufferedReader in = null;

            try {

                in = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = in.readLine()) != null) {
                    work.add(line);
                }

                result = (String[]) work.toArray(new String[work.size()]);
                in.close();
                in = null;

            } catch (IOException e) {

                result = null;

            } finally {

                if (in != null) {

                    try {

                        in.close();

                    } catch (IOException ex) {

                        throw new RuntimeException(ex);
                    }
                }
            }
        }

        return (result);
    }

    /**
     * Write a text file from a String object.
     *
     * @param f File object representing a text file.
     * @param data The data to write.
     * @throws IOException on an error.
     */
    public static void writeTextFile(File f, String data) throws IOException {

        if ((f != null) && (data != null)) {

            FileOutputStream fos = null;
            try {

                fos = new FileOutputStream(f);
                fos.write(data.getBytes());

            } finally {

                if (fos != null) {

                    fos.close();
                }
            }
        }
    }

    /**
     * Checks the to see if the 2 Strings are equal, or both are null.
     *
     * @param one String one to compare.
     * @param two String two to compare.
     * @return True if both Strings are null, else returns String.equals();
     */
    public static boolean equalOrNull(Object one, Object two) {

        boolean result = false;

        if ((one == null) && (two == null)) {

            result = true;

        } else if ((one == null) || (two == null)) {

            result = false;

        } else {

            result = one.equals(two);
        }

        return (result);
    }

    /**
     * Convenience method to set the mouse to invisible my making a blank
     * image for the cursor.  Users then set their component cursor using
     * the returned value.
     *
     * @return A Corsor with a blank image.
     */
    public static Cursor getNoCursor() {

        Cursor result = null;

        Toolkit t = Toolkit.getDefaultToolkit();
        if (t != null) {

            Dimension d = t.getBestCursorSize(1, 1);
            if (d != null) {

                int w = (int) d.getWidth();
                int h = (int) d.getHeight();
                if ((w != 0) && (h != 0)) {

                    result = t.createCustomCursor(
                        new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB),
                            new Point(0, 0), "NO_CURSOR");
                }
            }
        }

        return (result);
    }

//    public static boolean isTranslucencySupported() {

//        return (AWTUtilities.isTranslucencySupported(
//            AWTUtilities.Translucency.TRANSLUCENT));
//    }

    /**
     * Write out the system property values to stdout.
     */
    public static void dumpOSProperties() {

        Properties p = System.getProperties();
        if (p != null) {

            System.out.println("name: <" + p.getProperty("os.name") + ">");
            System.out.println("arch: <" + p.getProperty("os.arch") + ">");
            System.out.println("ver: <" + p.getProperty("os.version") + ">");
        }
    }

    /**
     * Get an array of Strings that represent the PATH environment variable
     * directories.
     *
     * @return An array of String instances.
     */
    public static String[] getEnvPaths() {

        String[] result = null;

        String path = System.getenv("PATH");
        if (path != null) {

            result = path.split(System.getProperty("path.separator"));
        }

        return (result);
    }

    /**
     * Convenience method to find all paths for a given program that can
     * be accessed at runtime because it's in the users PATH.  The thing to
     * remember is that the first item in the returned String array is most
     * likely the one that would be executed because it's the first one
     * found in the PATH.  But that may be platform dependent.
     *
     * @param program The program to find.
     * @return An array of paths if the program has been found.
     */
    public static String[] getProgramPaths(String program) {

        return (getProgramPaths(getEnvPaths(), program));
    }

    /**
     * Convenience method to find all paths for a given program that are
     * in the given array of directories.
     *
     * @param dirs The list of directories to check.
     * @param program The program to find.
     * @return An array of paths if the program has been found.
     */
    public static String[] getProgramPaths(String[] dirs, String program) {

        String[] result = null;

        if (isWindows()) {

            if (!program.endsWith(".exe")) {
                program = program + ".exe";
            }
        }

        if ((dirs != null) && (dirs.length > 0)) {

            ArrayList<String> l = new ArrayList<String>();
            for (int i = 0; i < dirs.length; i++) {

                String tmp = dirs[i] + System.getProperty("file.separator")
                    + program;
                File ftmp = new File(tmp);
                if ((ftmp.exists()) && (ftmp.isFile())) {

                    if (!l.contains(tmp)) {
                        l.add(tmp);
                    }
                }
            }

            if (l.size() > 0) {

                result = l.toArray(new String[l.size()]);
            }
        }

        return (result);
    }

    /**
     * Simple main method that dumps the system properties to stdout.
     *
     * @param args Arguments that happen to be ignored.
     */
    public static void main(String[] args) {

        Util.dumpOSProperties();
//        System.out.println("Translucent: " + Util.isTranslucencySupported());
    }

}


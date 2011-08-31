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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

/**
 * This is a utility class to support the Roku BIF file format.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Bif {

    private static final int HD_WIDTH = 290;
    private static final int HD_HEIGHT = 218;
    private static final int HD_X = 49;
    private static final int HD_Y = 87;
    private static final int SD_WIDTH = 214;
    private static final int SD_HEIGHT = 144;
    private static final int SD_X = 11;
    private static final int SD_Y = 50;

    private static byte[] magic = {
        (byte) 0x89, (byte) 0x42, (byte) 0x49, (byte) 0x46,
        (byte) 0x0d, (byte) 0x0a, (byte) 0x1a, (byte) 0x0a
    };
    private static byte[] version = {
        0x0, 0x0, 0x0, 0x0
    };
    private static byte[] separation = {
        0x0, 0x0, 0x0, 0x0
    };
    private static byte[] reserved = {
        0x0, 0x0, 0x0, 0x0,
        0x0, 0x0, 0x0, 0x0,
        0x0, 0x0, 0x0, 0x0,
        0x0, 0x0, 0x0, 0x0,
        0x0, 0x0, 0x0, 0x0,
        0x0, 0x0, 0x0, 0x0,
        0x0, 0x0, 0x0, 0x0,
        0x0, 0x0, 0x0, 0x0,
        0x0, 0x0, 0x0, 0x0,
        0x0, 0x0, 0x0, 0x0,
        0x0, 0x0, 0x0, 0x0
    };

    private static BufferedImage CHAR_0 = null;
    private static BufferedImage CHAR_1 = null;
    private static BufferedImage CHAR_2 = null;
    private static BufferedImage CHAR_3 = null;
    private static BufferedImage CHAR_4 = null;
    private static BufferedImage CHAR_5 = null;
    private static BufferedImage CHAR_6 = null;
    private static BufferedImage CHAR_7 = null;
    private static BufferedImage CHAR_8 = null;
    private static BufferedImage CHAR_9 = null;
    private static BufferedImage CHAR_COLON = null;
    private static BufferedImage CHAR_SPACE = null;

    static {

        try {

            CHAR_0 = ImageIO.read(Bif.class.getResource("char_0.png"));
            CHAR_1 = ImageIO.read(Bif.class.getResource("char_1.png"));
            CHAR_2 = ImageIO.read(Bif.class.getResource("char_2.png"));
            CHAR_3 = ImageIO.read(Bif.class.getResource("char_3.png"));
            CHAR_4 = ImageIO.read(Bif.class.getResource("char_4.png"));
            CHAR_5 = ImageIO.read(Bif.class.getResource("char_5.png"));
            CHAR_6 = ImageIO.read(Bif.class.getResource("char_6.png"));
            CHAR_7 = ImageIO.read(Bif.class.getResource("char_7.png"));
            CHAR_8 = ImageIO.read(Bif.class.getResource("char_8.png"));
            CHAR_9 = ImageIO.read(Bif.class.getResource("char_9.png"));
            CHAR_COLON = ImageIO.read(Bif.class.getResource("char_colon.png"));
            CHAR_SPACE = ImageIO.read(Bif.class.getResource("char_space.png"));

        } catch (IOException ex) {

            System.out.println("Bif bad: " + ex.getMessage());
        }
    }

    private Bif() {
    }

    private static int toLittleEndian(int i) {

        int b0 = (i & 0x000000ff);
        int b1 = (i & 0x0000ff00) >> 8;
        int b2 = (i & 0x00ff0000) >> 16;
        int b3 = (i & 0xff000000) >> 24;

        return ((b0 << 24) | (b1 << 16) | (b2 << 8) | b3);
    }

    private static String formatTime(int seconds) {

        int hours = seconds / 3600;
        seconds -= hours * 3600;
        int mins = seconds / 60;
        seconds -= mins * 60;
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {

            sb.append(hours);
            sb.append(":");
        }
        if (sb.length() > 0) {

            if (mins < 10) {
                sb.append("0");
            }

            sb.append(mins);
            sb.append(":");

        } else {

            sb.append(mins);
            sb.append(":");
        }

        if (seconds < 10) {
            sb.append("0");
        }

        sb.append(seconds);
        while (sb.length() < 8) {
            sb.insert(0, " ");
        }

        return (sb.toString());
    }

    private static ByteArrayOutputStream[] getImagesOLD(int[] seconds,
        boolean hd) {

        ByteArrayOutputStream[] result = null;

        if ((seconds != null) && (seconds.length > 0)) {

            result = new ByteArrayOutputStream[seconds.length];
            int w = HD_WIDTH;
            int h = HD_HEIGHT;
            if (!hd) {

                w = SD_WIDTH;
                h = SD_HEIGHT;
            }

            Color back = new Color(127, 14, 32);
            JXPanel panel = new JXPanel();
            panel.setLayout(new BorderLayout());
            JXLabel label = new JXLabel();
            panel.add(label, BorderLayout.CENTER);
            Font font = label.getFont();
            label.setTextAlignment(JXLabel.TextAlignment.RIGHT);
            label.setFont(font.deriveFont(64.0f));
            label.setPreferredSize(new Dimension(w, h));
            label.setSize(new Dimension(w, h));
            panel.setSize(new Dimension(w, h));
            panel.setPreferredSize(new Dimension(w, h));
            panel.setBackground(back);
            label.setBackground(back);
            label.setForeground(Color.WHITE);
            for (int i = 0; i < seconds.length; i++) {

                label.setText(formatTime(seconds[i]));
                result[i] = new ByteArrayOutputStream();
                BufferedImage bi = Util.componentToImage(panel);

                try {

                    ImageIO.write(bi, "GIF", result[i]);

                } catch (IOException ex) {
                }
            }
        }

        return (result);
    }

    private static ByteArrayOutputStream[] getImages(int[] seconds,
        boolean hd) {

        ByteArrayOutputStream[] result = null;

        if ((seconds != null) && (seconds.length > 0)) {

            result = new ByteArrayOutputStream[seconds.length];
            int w = HD_WIDTH;
            int h = HD_HEIGHT;
            int xindex = HD_X;
            int yindex = HD_Y;
            if (!hd) {

                w = SD_WIDTH;
                h = SD_HEIGHT;
                xindex = SD_X;
                yindex = SD_Y;
            }

            for (int i = 0; i < seconds.length; i++) {

                int tmpx = xindex;
                result[i] = new ByteArrayOutputStream();
                String tmp = formatTime(seconds[i]);
                if (tmp != null) {

                    BufferedImage bi =
                        new BufferedImage(w, h, CHAR_0.getType());
                    char[] carray = tmp.toCharArray();
                    if (carray != null) {

                        Graphics2D g2d = bi.createGraphics();
                        g2d.setColor(Color.BLACK);
                        g2d.fill(new Rectangle(0, 0, w, h));
                        for (int j = 0; j < carray.length; j++) {

                            switch (carray[j]) {

                            case '0':
                                g2d.drawImage(CHAR_0, tmpx, yindex, null);
                                break;
                            case '1':
                                g2d.drawImage(CHAR_1, tmpx, yindex, null);
                                break;
                            case '2':
                                g2d.drawImage(CHAR_2, tmpx, yindex, null);
                                break;
                            case '3':
                                g2d.drawImage(CHAR_3, tmpx, yindex, null);
                                break;
                            case '4':
                                g2d.drawImage(CHAR_4, tmpx, yindex, null);
                                break;
                            case '5':
                                g2d.drawImage(CHAR_5, tmpx, yindex, null);
                                break;
                            case '6':
                                g2d.drawImage(CHAR_6, tmpx, yindex, null);
                                break;
                            case '7':
                                g2d.drawImage(CHAR_7, tmpx, yindex, null);
                                break;
                            case '8':
                                g2d.drawImage(CHAR_8, tmpx, yindex, null);
                                break;
                            case '9':
                                g2d.drawImage(CHAR_9, tmpx, yindex, null);
                                break;
                            case ':':
                                g2d.drawImage(CHAR_COLON, tmpx, yindex, null);
                                break;
                            case ' ':
                                g2d.drawImage(CHAR_SPACE, tmpx, yindex, null);
                                break;
                            }

                            tmpx += 24;
                        }

                        g2d.dispose();
                    }

                    try {

                        ImageIO.write(bi, "GIF", result[i]);

                    } catch (IOException ex) {
                    }
                }
            }
        }

        return (result);
    }

    public static void write(File out, int[] seconds, boolean hd) {

        if ((out != null) && (seconds != null) && (seconds.length > 0)) {

            ByteArrayOutputStream[] array = getImages(seconds, hd);
            if ((array != null) && (array.length == seconds.length)) {

                FileOutputStream fos = null;
                DataOutputStream dos = null;
                try {

                    int offset = 64 + (8 * (array.length + 1));
                    fos = new FileOutputStream(out);
                    dos = new DataOutputStream(fos);

                    dos.write(magic, 0, magic.length);
                    dos.write(version, 0, version.length);
                    dos.writeInt(toLittleEndian(array.length));
                    dos.writeInt(toLittleEndian(1000));
                    dos.write(reserved, 0, reserved.length);

                    for (int i = 0; i < seconds.length; i++) {

                        dos.writeInt(toLittleEndian(seconds[i]));
                        dos.writeInt(toLittleEndian(offset));

                        int fsize = array[i].size();
                        offset += fsize;
                    }
                    dos.writeInt(0xffffffff);
                    dos.writeInt(toLittleEndian(offset + 1));

                    for (int i = 0; i < array.length; i++) {

                        byte[] data = array[i].toByteArray();
                        if (data != null) {

                            dos.write(data, 0, data.length);
                        }
                    }

                } catch (IOException ex) {
                } finally {

                    if (dos != null) {

                        try {

                            dos.close();
                            fos.close();

                        } catch (IOException ex) {

                            dos = null;
                            fos = null;
                        }
                    }
                }
            }
        }
    }

    /**
     * Create a BIF output file from an array of image files.
     *
     * @param out The place to write the BIF file.
     * @param images Use these image files to put in the BIF file.
     * @param seconds An array denoting the time of each image.
     */
    public static void write(File out, File[] images, int[] seconds) {

        if ((out != null) && (images != null) && (seconds != null)
            && (images.length == seconds.length)) {

            FileOutputStream fos = null;
            DataOutputStream dos = null;
            try {

                int offset = 64 + (8 * (images.length + 1));
                fos = new FileOutputStream(out);
                dos = new DataOutputStream(fos);

                dos.write(magic, 0, magic.length);
                dos.write(version, 0, version.length);
                dos.writeInt(toLittleEndian(images.length));
                dos.writeInt(toLittleEndian(1000));
                //dos.write(separation, 0, separation.length);
                dos.write(reserved, 0, reserved.length);

                for (int i = 0; i < images.length; i++) {

                    dos.writeInt(toLittleEndian(seconds[i]));
                    dos.writeInt(toLittleEndian(offset));

                    int fsize = (int) images[i].length();
                    offset += fsize;
                }
                dos.writeInt(0xffffffff);
                dos.writeInt(toLittleEndian(offset + 1));

                for (int i = 0; i < images.length; i++) {

                    byte[] data = Util.read(images[i]);
                    if (data != null) {

                        dos.write(data, 0, data.length);

                    } else {

                        System.out.println("Very bad, couldn't read image!");
                    }
                }

            } catch (IOException ex) {
            } finally {

                if (dos != null) {

                    try {

                        dos.close();
                        fos.close();

                    } catch (IOException ex) {

                        dos = null;
                        fos = null;
                    }
                }
            }
        }
    }

    /**
     * Simple main method that tests the BIF output.
     *
     * @param args Arguments that happen to be ignored.
     */
    public static void main(String[] args) {

        int[] seconds = {
            0, 595, 1128, 1783
        };
        //File[] images = new File[4];
        //images[0] = new File("/home/djb/tools/roku/fred.jpg");
        //images[1] = new File("/home/djb/tools/roku/fred1.jpg");
        //images[2] = new File("/home/djb/tools/roku/fred2.jpg");
        //images[3] = new File("/home/djb/tools/roku/fred3.jpg");

        File out = new File("/var/www/jflicks/tv/EP000186930464_2011_08_12_18_00.ts.hd.bif");

        //Bif.write(out, images, seconds);
        Bif.write(out, seconds, true);
        out = new File("/var/www/jflicks/tv/EP000186930464_2011_08_12_18_00.ts.sd.bif");

        //Bif.write(out, images, seconds);
        Bif.write(out, seconds, false);
    }

}

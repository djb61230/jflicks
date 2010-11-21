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
package org.jflicks.install;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class Install {

    private static final String WEBSERVICE = "http://webservices.schedules"
        + "direct.tmsdatadirect.com/schedulesdirect/tvlistings/xtvdService";

    private Properties properties;

    public Install(Properties p) {

        setProperties(p);
    }

    private Properties getProperties() {
        return (properties);
    }

    private void setProperties(Properties p) {
        properties = p;
    }

    private static String[] readTextFile(File file) {

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

    private static void writeTextFile(File f, String[] lines) {

        if ((f != null) && (lines != null)) {

            FileWriter fw = null;
            try {

                fw = new FileWriter(f);
                for (int i = 0; i < lines.length; i++) {

                    fw.write(lines[i]);
                    fw.write("\n");
                }

            } catch (IOException e) {

                System.out.println(e.getMessage());

            } finally {

                if (fw != null) {

                    try {

                        fw.close();

                    } catch (IOException ex) {

                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }

    private static void copyTextFile(String from, String to) {

        if ((from != null) && (to != null)) {

            copyTextFile(new File(from), new File(to));
        }
    }

    private static void copyTextFile(File from, File to) {

        if ((from != null) && (to != null)) {

            String[] lines = readTextFile(from);
            if (lines != null) {

                writeTextFile(to, lines);
            }
        }
    }

    private boolean str2boolean(String s, boolean defaultValue) {

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

    private void generateMplayerConfig() {

        Properties p = getProperties();
        if (p != null) {

            String val = p.getProperty("mplayerconfig");
            if (val != null) {

                if (str2boolean(val, false)) {

                    File uhome = new File(System.getProperty("user.home"));
                    File dotmplayer = new File(uhome, ".mplayer");
                    if (!dotmplayer.exists()) {

                        if (!dotmplayer.mkdir()) {

                            System.out.println("Failed to make .mplayer dir "
                                + "quitting...");
                            return;
                        }
                    }

                    File config = new File(dotmplayer, "config");
                    if (config.exists()) {

                        File configsave = new File(dotmplayer, "config.save");
                        if (config.renameTo(configsave)) {

                            System.out.println("Saved old config to "
                                + "config.save");

                        } else {

                            System.out.println("Failed to save old config");
                        }

                        config = new File(dotmplayer, "config");
                    }

                    BufferedWriter bw = null;

                    try {

                        bw = new BufferedWriter(new FileWriter(config));
                        String line = "vo=vdpau:deint=1";
                        bw.write(line, 0, line.length());
                        bw.newLine();
                        line = "vc=ffh264vdpau,ffmpeg12vdpau,ffvc1vdpau,";
                        bw.write(line, 0, line.length());
                        bw.newLine();
                        line = "ao=alsa:noblock:device=hw=0.0";
                        bw.write(line, 0, line.length());
                        bw.newLine();
                        line = "nolirc=yes";
                        bw.write(line, 0, line.length());
                        bw.newLine();
                        line = "fs=1";
                        bw.write(line, 0, line.length());
                        bw.newLine();
                        line = "zoom=1";
                        bw.write(line, 0, line.length());
                        bw.newLine();
                        line = "framedrop=1";
                        bw.write(line, 0, line.length());
                        bw.newLine();
                        line = "cache=2048";
                        bw.write(line, 0, line.length());
                        bw.newLine();

                    } catch (IOException ex) {

                        System.out.println("WARNING: Failed to create config.");

                    } finally {

                        try {

                            if (bw != null) {

                                bw.close();
                                bw = null;
                            }

                        } catch (IOException ex) {

                            bw = null;
                        }
                    }

                } else {

                    System.out.println("skipping generating mplayer config");
                }
            }
        }
    }

    private void autostart() {

        Properties p = getProperties();
        if (p != null) {

            String val = p.getProperty("autostart");
            if (val != null) {

                if (str2boolean(val, false)) {

                    String autoname = p.getProperty("autoname");
                    String installpath = p.getProperty("installpath");
                    if ((autoname != null) && (installpath != null)) {

                        File uhome = new File(System.getProperty("user.home"));
                        File dotconfig = new File(uhome, ".config");
                        if (!dotconfig.exists()) {

                            if (!dotconfig.mkdir()) {

                                System.out.println("Failed to make .config"
                                    + " dir quitting...");
                                return;
                            }
                        }

                        File autostart = new File(dotconfig, "autostart");
                        if (!autostart.exists()) {

                            if (!autostart.mkdir()) {

                                System.out.println("Failed to make autostart"
                                    + " dir quitting...");
                                return;
                            }
                        }

                        File dest = new File(autostart, autoname);
                        if ((dest.exists()) && (dest.isFile())) {

                            if (!dest.delete()) {

                                System.out.println("Failed to remove old"
                                    + " autostart file.");
                            }
                        }

                        File installdir = new File(installpath);
                        File source = new File(installdir, autoname);
                        if (source.exists()) {

                            if (!source.renameTo(dest)) {

                                System.out.println("Failed to install"
                                    + " autostart file.");
                            }
                        }
                    }

                } else {

                    System.out.println("skipping the install of autostart"
                        + " file.");
                }
            }
        }
    }

    private void generateSchedulesDirect() {

        Properties p = getProperties();
        if (p != null) {

            String installpath = p.getProperty("installpath");
            String sdusername = p.getProperty("sdusername");
            String sdpassword = p.getProperty("sdpassword");
            if ((installpath != null) && (sdusername != null)
                && (sdpassword != null)) {

                File installdir = new File(installpath);
                File conf = new File(installdir, "conf");
                if (conf.exists()) {

                    File xtvd = new File(conf, "XTVD.xml");

                    BufferedWriter bw = null;

                    try {

                        bw = new BufferedWriter(new FileWriter(xtvd));
                        String line =
                            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                        bw.write(line, 0, line.length());
                        bw.newLine();
                        line = "<properties>";
                        bw.write(line, 0, line.length());
                        bw.newLine();
                        line = "    <userName>";
                        bw.write(line, 0, line.length());
                        bw.write(sdusername, 0, sdusername.length());
                        line = "</userName>";
                        bw.write(line, 0, line.length());
                        bw.newLine();
                        line = "    <password>";
                        bw.write(line, 0, line.length());
                        bw.write(sdpassword, 0, sdpassword.length());
                        line = "</password>";
                        bw.write(line, 0, line.length());
                        bw.newLine();
                        line = "    <numberOfDays>14</numberOfDays>";
                        bw.write(line, 0, line.length());
                        bw.newLine();
                        line = "    <webserviceURI>" + WEBSERVICE
                            + "</webserviceURI>";
                        bw.write(line, 0, line.length());
                        bw.newLine();
                        line = "</properties>";
                        bw.write(line, 0, line.length());
                        bw.newLine();

                    } catch (IOException ex) {

                        System.out.println("WARNING: Failed to create config.");

                    } finally {

                        try {

                            if (bw != null) {

                                bw.close();
                                bw = null;
                            }

                        } catch (IOException ex) {

                            bw = null;
                        }
                    }
                }
            }
        }
    }

    private void copyRemote() {

        Properties p = getProperties();
        if (p != null) {

            String remote = p.getProperty("remote");
            if (remote != null) {

                File f = new File("remotes");
                if ((f.exists()) && (f.isDirectory())) {

                    File fr = new File(f, remote);
                    if ((fr.exists()) && (fr.isFile())) {

                        File conf = new File("conf");
                        if ((conf.exists()) && (conf.isDirectory())) {

                            File confr = new File(conf, "LircJob.lircrc");
                            copyTextFile(fr, confr);
                        }
                    }
                }
            }
        }
    }

    public void work() {

        System.out.println("work....");
        generateMplayerConfig();
        generateSchedulesDirect();
        copyRemote();
        autostart();
    }

    public void cleanup() {

        System.out.println("cleanup....");
        Properties p = getProperties();
        if (p != null) {

            String installpath = p.getProperty("installpath");
            if (installpath != null) {

                File installdir = new File(installpath);
                File bin = new File(installdir, "bin");
                if (bin.exists()) {

                    File installjar = new File(bin, "ltms-install.jar");
                    if (installjar.exists()) {

                        if (!installjar.delete()) {

                            System.out.println("WARNING: Could not delete"
                                + " no longer needed install jar.");
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {

        Properties p = new Properties();
        for (int i = 0; i < args.length; i += 2) {

            String tag = args[i].substring(1);
            p.setProperty(tag, args[i + 1]);
        }

        try {

            FileWriter fw = new FileWriter("my.properties");
            p.store(fw, "tset");
            fw.close();

        } catch (Exception ex) {
        }
        Install install = new Install(p);
        install.work();
        install.cleanup();
    }

}

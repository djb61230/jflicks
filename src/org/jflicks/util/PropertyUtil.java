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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Collection of handy methods to make interacting with Properties objects
 * easier.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class PropertyUtil {

    private PropertyUtil() {
    }

    /**
     * Merge two instances of Properties objects.  Ant duplicate tag/value
     * pairs will be over written.
     *
     * @param to The destination Properties instance.
     * @param from The source Properties instance.
     */
    public static void merge(Properties to, Properties from) {
        merge(to, from, true);
    }

    /**
     * Merge two instances of Properties objects.  The caller can control
     * whether an existing tag/value pair will be over written.
     *
     * @param to The destination Properties instance.
     * @param from The source Properties instance.
     * @param overwrite When true an existing tag will be over written.  When
     * false only "new" Property tag/value pairs will be merged.
     */
    public static void merge(Properties to, Properties from,
        boolean overwrite) {

        if ((to != null) && (from != null)) {

            Set<String> set = from.stringPropertyNames();
            Iterator<String> iter = set.iterator();
            while (iter.hasNext()) {

                String key = iter.next();
                String val = from.getProperty(key);
                if (overwrite) {

                    to.setProperty(key, val);

                } else {

                    if (!to.containsKey(key)) {

                        to.setProperty(key, val);
                    }
                }
            }
        }
    }

    /**
     * Convert a Dictionary to a Properties instance.  This is here because
     * OSGi ConfigurationAdmin defines their interface to use a Dictionary
     * which is quite old really as Dictionary is not recommended these days.
     * Actually Properties implements Dictionary but it doesn't mean OSGi
     * will be a Properties instance - experience shows that it isn't.  So
     * it's just easier to convert it and be sure things like serialization
     * and encrypting property values just work.
     *
     * @param d A given String based Dictionary instance.
     * @return A properties instance.
     */
    public static Properties toProperties(Dictionary d) {

        Properties result = null;

        if (d != null) {

            result = new Properties();
            Enumeration keys = d.keys();
            while (keys.hasMoreElements()) {

                Object key = keys.nextElement();
                Object val = d.get(key);
                if ((key != null) && (val != null)) {
                    result.setProperty(key.toString(), val.toString());
                }
            }
        }

        return result;
    }

    /**
     * Convert a Map to a Properties instance.  Sometimes when using
     * legacy code they require a Properties instance while more modern
     * code tends to using the newer Map objects.  Here we just want
     * to make it easy to supply a Properties object from a Map.
     *
     * @param m A given String based Map instance.
     * @return A properties instance.
     */
    public static Properties toProperties(Map<String, String> m) {

        Properties result = null;

        if (m != null) {

            result = new Properties();
            Set<Map.Entry<String, String>> set = m.entrySet();
            Iterator<Map.Entry<String, String>> iter = set.iterator();
            while (iter.hasNext()) {

                Map.Entry<String, String> me = iter.next();
                String key = me.getKey();
                String val = me.getValue();
                result.put(key, val);
            }
        }

        return (result);
    }

    /**
     * Helper method to copy a Properties instance to another.
     *
     * @param p A Properties instance to copy.
     * @return A Properties instance.
     */
    public static Properties copy(Properties p) {

        Properties result = null;

        if (p != null) {

            result = new Properties();
            Enumeration keys = p.propertyNames();
            while (keys.hasMoreElements()) {

                String key = (String) keys.nextElement();
                String val = p.getProperty(key);
                if (val != null) {

                    result.setProperty(key, val);
                }
            }
        }

        return (result);
    }

    /**
     * Helper method to copy a Properties instance to another filtered
     * by an array of tags.
     *
     * @param p A Properties instance.
     * @param tags An array of String instances that limit the copy to
     * these tags.
     * @return A Properties instance.
     */
    public static Properties copy(Properties p, String[] tags) {

        Properties result = null;

        if ((p != null) && (tags != null) && (tags.length > 0)) {

            Set<String> set = p.stringPropertyNames();
            if ((set != null) && (set.size() > 0)) {

                result = new Properties();
                for (int i = 0; i < tags.length; i++) {

                    if (set.contains(tags[i])) {

                        result.setProperty(tags[i], p.getProperty(tags[i]));
                    }
                }
            }

        } else {

            result = p;
        }

        return (result);
    }

    /**
     * Helper method to read a Properties instance from a File.
     *
     * @param file A File instance.
     * @return A Properties instance.
     */
    public static Properties read(File file) {

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
     * A nice method that will read a properties file for a given object.
     * The properties file is assumed to be in the same package location
     * as the object.  This makes for reading properties files that are
     * located in jar files easy.  Handy for runtime read-only properties.
     *
     * @param o A given object.
     * @param s A properties file name.
     * @return An instantiated Properties file.
     */
    public static Properties read(Object o, String s) {

        Properties result = new Properties();

        if ((o != null) && (s != null)) {

            URL url = o.getClass().getResource(s);
            InputStream is = null;
            try {

                is = url.openStream();
                result.load(is);

            } catch (Exception ex) {

                result = null;

            } finally {

                if (is != null) {

                    try {

                        is.close();

                    } catch (Exception ex) {
                    }
                }
            }
        }

        return (result);
    }

    /**
     * Helper method to write a Properties instance to a File.
     *
     * @param f A File instance.
     * @param p A Properties instance.
     */
    public static void write(File f, Properties p) {

        if ((f != null) && (p != null)) {

            FileWriter fw = null;
            try {

                fw = new FileWriter(f);
                p.store(fw, "");

            } catch (IOException ex) {
            } finally {

                if (fw != null) {

                    try {

                        fw.close();

                    } catch (IOException ex) {
                    }
                }
            }
        }
    }

    /**
     * Helper method to find a property value and interpret it as an
     * int primitive.
     *
     * @param p A lookup Properties instance.
     * @param s A tag value to search on.
     * @param defaultValue A fallback default in case something fails.
     * @return An int value.
     */
    public static int property2int(Properties p, String s, int defaultValue) {

        int result = defaultValue;

        if ((p != null) && (s != null)) {

            try {

                String val = p.getProperty(s);
                val = val.replace("\"", "");
                result = Integer.parseInt(val);

            } catch (Exception ex) {

                result = defaultValue;
            }
        }

        return (result);
    }

    /**
     * Helper method to find a property value and interpret it as a
     * long primitive.
     *
     * @param p A lookup Properties instance.
     * @param s A tag value to search on.
     * @param defaultValue A fallback default in case something fails.
     * @return A long value.
     */
    public static long property2long(Properties p, String s,
        long defaultValue) {

        long result = defaultValue;

        if ((p != null) && (s != null)) {

            try {

                String val = p.getProperty(s);
                val = val.replace("\"", "");
                result = Long.parseLong(val);

            } catch (Exception ex) {

                result = defaultValue;
            }
        }

        return (result);
    }

    /**
     * Helper method to find a property value and interpret it as a
     * double primitive.
     *
     * @param p A lookup Properties instance.
     * @param s A tag value to search on.
     * @param defaultValue A fallback default in case something fails.
     * @return A double value.
     */
    public static double property2double(Properties p, String s,
        double defaultValue) {

        double result = defaultValue;

        if ((p != null) && (s != null)) {

            try {

                result = Double.parseDouble(p.getProperty(s));

            } catch (Exception ex) {

                result = defaultValue;
            }
        }

        return (result);
    }

    /**
     * Helper method to find a property value and interpret it as a
     * boolean primitive.
     *
     * @param p A lookup Properties instance.
     * @param s A tag value to search on.
     * @param defaultValue A fallback default in case something fails.
     * @return A boolean value.
     */
    public static boolean property2boolean(Properties p, String s,
        boolean defaultValue) {

        boolean result = defaultValue;

        if ((p != null) && (s != null)) {

            try {

                result = Boolean.parseBoolean(p.getProperty(s));

            } catch (Exception ex) {

                result = defaultValue;
            }
        }

        return (result);
    }

    /**
     * Helper method to find a property value and interpret it as a
     * File object.
     *
     * @param p A lookup Properties instance.
     * @param s A tag value to search on.
     * @param defaultValue A fallback default in case something fails.
     * @return A File instance.
     */
    public static File property2File(Properties p, String s,
        File defaultValue) {

        File result = defaultValue;

        if ((p != null) && (s != null)) {

            try {

                String path = p.getProperty(s);
                if (path != null) {

                    result = new File(path);
                }

            } catch (Exception ex) {

                result = defaultValue;
            }
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

}

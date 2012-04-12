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
package org.jflicks.update.system;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.jflicks.util.BundleFilter;
import org.jflicks.util.Util;

/**
 *
 * @author Doug Barnum
 * @version 1.0
 */
public final class BundleTree {

    private static BundleTree instance;
    private BundleItem bundleItems;

    /**
     * Default empty constructor.
     */
    private BundleTree() {
    }

    public static BundleTree getInstance() {

        if (instance == null) {

            instance = new BundleTree();
        }

        return (instance);
    }

    private WhenSize getWhenSize(String urlstr) {

        WhenSize result = null;

        if (urlstr != null) {

            try {

                URL url = new URL(urlstr);
                URLConnection conn = url.openConnection();
                result = new WhenSize(conn.getLastModified(),
                    (long) conn.getContentLength());

            } catch (IOException ex) {

                System.out.println("Warning: " + ex.getMessage());
            }
        }

        return (result);
    }

    private boolean isNewerURL(File f, String url) {

        boolean result = false;

        if ((f != null) && (url != null)) {

            WhenSize ws = getWhenSize(url);
            if (ws != null) {

                result = (f.lastModified() < ws.getWhen());
                /*
                if (result) {

                    // Ok the file is newer.  But we want to assume the
                    // file is the same if it's the same size.  Looks like
                    // the compiler/jar program sometimes makes the file
                    // off by a byte or two even though no code has changed.
                    // This could be something like a date in the MANIFEST
                    // using the day of the week or something.  Whatever we
                    // will assume if there are many code changes then the
                    // file size will be much different.
                    long size = ws.getSize();
                    if (size != -1L) {

                        result = Math.abs(f.length() - size) > 2L;
                    }
                }
                */
            }
        }

        return (result);
    }

    private boolean isNewBundle(String name, File[] array) {

        boolean result = true;

        if ((name != null) && (array != null) && (array.length > 0)) {

            for (int i = 0; i < array.length; i++) {

                if (name.equals(array[i].getName())) {

                    result = false;
                    break;
                }
            }
        }

        return (result);
    }

    private BundleItem create(Element e) {

        BundleItem result = null;

        if (e != null) {

            String name = e.getAttributeValue("name");
            if (name != null) {

                boolean feature = Util.str2boolean(
                    e.getAttributeValue("feature"), false);
                result = new BundleItem();
                result.setName(name.trim());
                result.setFeature(feature);
                List list = e.getChildren("needs");
                if ((list != null) && (list.size() > 0)) {

                    ArrayList<String> nlist = new ArrayList<String>();
                    for (int i = 0; i < list.size(); i++) {

                        Element nelement = (Element) list.get(i);
                        if (nelement != null) {

                            name = nelement.getAttributeValue("name");
                            if (name != null) {

                                nlist.add(name.trim());
                            }
                        }
                    }

                    if (nlist.size() > 0) {

                        String[] ary = nlist.toArray(new String[nlist.size()]);
                        result.setNeedNames(ary);
                    }
                }
            }
        }

        return (result);
    }

    private BundleItem[] parse(String url) {

        BundleItem[] result = null;

        if (url != null) {

            url = url + "bundletree.xml";
            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            builder.setFeature(
              "http://apache.org/xml/features/nonvalidating/load-external-dtd",
              false);
            try {

                Document doc = builder.build(url);
                if (doc != null) {

                    Element root = doc.getRootElement();
                    if (root != null) {

                        List children = root.getChildren("bundle");
                        if ((children != null) && (children.size() > 0)) {

                            ArrayList<BundleItem> blist =
                                new ArrayList<BundleItem>();
                            for (int i = 0; i < children.size(); i++) {

                                BundleItem bi =
                                    create((Element) children.get(i));
                                if (bi != null) {

                                    blist.add(bi);
                                }
                            }

                            if (blist.size() > 0) {

                                result =
                                    blist.toArray(new BundleItem[blist.size()]);
                            }
                        }
                    }
                }

            } catch (JDOMException ex) {
                result = null;
            } catch (IOException ex) {
                result = null;
            }
        }

        return (result);
    }

    private BundleItem find(String name, BundleItem[] array) {

        BundleItem result = null;

        if ((name != null) && (array != null) && (array.length > 0)) {

            for (int i = 0; i < array.length; i++) {

                if (name.equals(array[i].getName())) {

                    result = array[i];
                    break;
                }
            }
        }

        return (result);
    }

    private void aggregate(ArrayList<String> l, String name,
        BundleItem[] array) {

        if ((l != null) && (name != null) && (array != null)) {

            if (!l.contains(name)) {

                l.add(name);
            }

            BundleItem bi = find(name, array);
            if (bi != null) {

                String[] needs = bi.getNeedNames();
                if ((needs != null) && (needs.length > 0)) {

                    for (int i = 0; i < needs.length; i++) {

                        aggregate(l, needs[i], array);
                    }
                }
            }
        }
    }

    public File[] check(String dir, String sourceURL) {

        File[] result = null;

        File fdir = new File(dir);
        if ((fdir != null) && (fdir.isDirectory()) && (sourceURL != null)) {

            File[] bundles = fdir.listFiles(new BundleFilter());

            if ((bundles != null) && (bundles.length > 0)) {

                // We have bundles to compare...
                if (!sourceURL.endsWith("/")) {
                    sourceURL = sourceURL + "/";
                }

                ArrayList<File> newer = new ArrayList<File>();
                for (int i = 0; i < bundles.length; i++) {

                    String name = bundles[i].getName();
                    if (name != null) {

                        String turl = sourceURL + name;
                        if (isNewerURL(bundles[i], turl)) {

                            newer.add(bundles[i]);
                            System.out.println(turl + " is newer");
                        }
                    }
                }

                if (newer.size() > 0) {

                    // If anything is new we have to check each one to
                    // see if any have new dependencies.  We look for
                    // a bundletree.xml file up at the sourceURL.
                    BundleItem[] biarray = parse(sourceURL);
                    if ((biarray != null) && (biarray.length > 0)) {

                        ArrayList<String> namelist = new ArrayList<String>();
                        for (int i = 0; i < newer.size(); i++) {

                            String name = newer.get(i).getName();
                            if (name != null) {

                                name = name.substring(0, name.indexOf(".jar"));
                                aggregate(namelist, name, biarray);
                            }
                        }

                        // Name list should have all unique dependents.
                        for (int i = 0; i < namelist.size(); i++) {

                            String bunname = namelist.get(i) + ".jar";
                            if (isNewBundle(bunname, bundles)) {

                                File newone = new File(fdir, bunname);
                                newer.add(newone);
                            }
                        }
                    }

                    result = newer.toArray(new File[newer.size()]);
                }
            }
        }

        return (result);
    }

    private class WhenSize {

        private long when;
        private long size;

        public WhenSize(long when, long size) {

            setWhen(when);
            setSize(size);
        }

        public long getWhen() {
            return (when);
        }

        private void setWhen(long l) {
            when = l;
        }

        public long getSize() {
            return (size);
        }

        private void setSize(long l) {
            size = l;
        }

    }

    public static void main(String[] args) {

        String dir = "bundle";
        String sourceURL = "http://www.jflicks.org/repository/";

        for (int i = 0; i < args.length; i += 2) {

            if (args[i].equalsIgnoreCase("-dir")) {
                dir = args[i + 1];
            } else if (args[i].equalsIgnoreCase("-sourceURL")) {
                sourceURL = args[i + 1];
            }
        }

        BundleTree bt = BundleTree.getInstance();
        bt.check(dir, sourceURL);
    }

}

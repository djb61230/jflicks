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
package org.jflicks.ui.view.aspirin.analyze.path;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import org.jflicks.ui.view.aspirin.analyze.Analyze;
import org.jflicks.util.BaseActivator;
import org.jflicks.util.Util;

import org.osgi.framework.BundleContext;

/**
 * Simple activator that creates a AspirinView and starts it.  Also registers
 * the AspirinView so a Controller can find it.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        ArrayList<String> namelist = new ArrayList<String>();
        ArrayList<String> dllist = new ArrayList<String>();
        ArrayList<String> shortlist = new ArrayList<String>();
        ArrayList<String> longlist = new ArrayList<String>();
        ArrayList<String> typelist = new ArrayList<String>();
        ArrayList<String> bundleslist = new ArrayList<String>();
        ArrayList<String> pathslist = new ArrayList<String>();

        File conf = new File("conf");
        if ((conf.exists()) && (conf.isDirectory())) {

            File props = new File(conf, "path.properties");
            if ((props.exists()) && (props.isFile())) {

                Properties p = Util.findProperties(props);
                if (p != null) {

                    int count = Util.str2int(p.getProperty("programCount"), 0);
                    for (int i = 0; i < count; i++) {

                        namelist.add(getProperty(p, "program_" + i + "_name"));
                        dllist.add(getProperty(p, "program_" + i
                            + "_download"));
                        shortlist.add(getProperty(p, "program_" + i
                            + "_shortDescription"));
                        longlist.add(getProperty(p, "program_" + i
                            + "_longDescription"));
                        typelist.add(getProperty(p, "program_" + i
                            + "_client"));
                        bundleslist.add(getProperty(p, "program_" + i
                            + "_bundles"));
                        pathslist.add(getProperty(p, "program_" + i
                            + "_paths"));
                    }
                }
            }
        }

        if (namelist.size() > 0) {

            for (int i = 0; i < namelist.size(); i++) {

                PathAnalyze pa = new PathAnalyze();
                String name = namelist.get(i);
                pa.setTitle("ANALYZE-PATH-" + name);
                pa.setProgram(name);
                pa.setDownload(dllist.get(i));
                pa.setShortDescription(shortlist.get(i));
                pa.setLongDescription(longlist.get(i));

                String bundles = bundleslist.get(i);
                if ((bundles != null) && (bundles.length() > 0)) {

                    String[] barray = bundles.split(",");
                    pa.setBundles(barray);
                }

                String paths = pathslist.get(i);
                if ((paths != null) && (paths.length() > 0)) {

                    String[] parray = paths.split(",");
                    pa.setPaths(parray);
                }

                Hashtable<String, String> dict =
                    new Hashtable<String, String>();
                dict.put(Analyze.TITLE_PROPERTY, pa.getTitle());

                bc.registerService(Analyze.class.getName(), pa, dict);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {
    }

    private String getProperty(Properties p, String tag) {

        String result = "";

        if ((p != null) && (tag != null)) {

            String val = p.getProperty(tag);
            if (val != null) {

                val = val.trim();
                result = val;
            }
        }

        return (result);
    }

}

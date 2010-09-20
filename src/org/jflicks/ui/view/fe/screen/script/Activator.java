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
package org.jflicks.ui.view.fe.screen.script;

import java.io.File;
import java.util.Hashtable;
import java.util.Properties;

import org.jflicks.ui.view.fe.screen.Screen;
import org.jflicks.util.BaseActivator;
import org.jflicks.util.Util;

import org.osgi.framework.BundleContext;

/**
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

        ScriptScreen[] array = getScriptScreens();

        if (array != null) {

            for (int i = 0; i < array.length; i++) {

                Hashtable<String, String> dict =
                    new Hashtable<String, String>();
                dict.put(Screen.TITLE_PROPERTY, array[i].getTitle());

                bc.registerService(Screen.class.getName(), array[i], dict);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {
    }

    private ScriptScreen[] getScriptScreens() {

        ScriptScreen[] result = null;

        File home = new File(".");
        File conf = new File(home, "conf");
        if ((conf.exists() && (conf.isDirectory()))) {

            File prop = new File(conf, "scriptscreen.properties");
            if ((prop.exists() && (prop.isFile()))) {

                Properties p = Util.findProperties(prop);
                if (p != null) {

                    int count = Util.str2int(p.getProperty("screen_count"), 0);
                    if (count > 0) {

                        result = new ScriptScreen[count];
                        for (int i = 0; i < result.length; i++) {

                            String prefix = "screen_" + i + "_";
                            String sname = p.getProperty(prefix + "name");
                            int ccount = Util.str2int(p.getProperty(prefix
                                + "command_count"), 0);
                            if ((sname != null) && (ccount > 0)) {

                                String[] cmds = new String[ccount];
                                String[] scripts = new String[ccount];
                                for (int j = 0; j < cmds.length; j++) {

                                    String cprefix = prefix + "command_" + j;
                                    cmds[j] = p.getProperty(cprefix + "_name");
                                    scripts[j] =
                                        p.getProperty(cprefix + "_script");
                                }

                                result[i] =
                                    new ScriptScreen(sname, cmds, scripts);
                            }
                        }
                    }
                }
            }
        }

        return (result);
    }

}

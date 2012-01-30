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

import org.apache.commons.io.FileUtils;

import org.jflicks.update.BaseUpdate;
import org.jflicks.update.UpdateState;
import org.jflicks.util.BundleFilter;

/**
 * This is our implementation of an Update.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SystemUpdate extends BaseUpdate {

    /**
     * Default empty constructor.
     */
    public SystemUpdate() {

        setTitle("SystemUpdate");
        setBundleDirectory("bundle");
        setSourceURL("http://www.jflicks.org/repository/");
    }

    /**
     * {@inheritDoc}
     */
    public UpdateState open() {

        UpdateState result = null;

        String dir = getBundleDirectory();
        String sourceURL = getSourceURL();
        if ((dir != null) && (sourceURL != null)) {

            if (!sourceURL.endsWith("/")) {
                sourceURL = sourceURL + "/";
            }

            BundleTree bt = BundleTree.getInstance();
            if (bt != null) {

                File[] array = bt.check(dir, sourceURL);
                result = new UpdateState(array, dir, sourceURL);
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public boolean update(UpdateState us) {

        boolean result = false;

        if (us != null) {

            result = true;
            if (us.getUpdateCount() > 0) {

                File[] array = us.getFiles();
                File wdir = us.getWorkingDirectory();
                String dir = us.getBundleDirectory();
                String sourceURL = us.getSourceURL();
                if ((array != null) && (wdir != null) && (sourceURL != null)
                    && (dir != null)) {

                    File bdir = new File(dir);
                    for (int i = 0; i < array.length; i++) {

                        String fname = array[i].getName();
                        File wfile = new File(wdir, fname);
                        try {

                            URL url = new URL(sourceURL + fname);
                            FileUtils.copyURLToFile(url, wfile);

                        } catch (IOException ex) {

                            log(WARNING, ex.getMessage());
                            result = false;
                            break;
                        }
                    }

                    if (result) {

                        // Ok we have things downloaded.
                        File[] bundles = wdir.listFiles(new BundleFilter());
                        if ((bundles != null) && (bundles.length > 0)) {

                            for (int i = 0; i < bundles.length; i++) {

                                try {

                                    FileUtils.copyFileToDirectory(bundles[i],
                                        bdir, false);

                                } catch (IOException ex) {

                                    log(WARNING, ex.getMessage());
                                    result = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     */
    public void close(UpdateState us) {

        if (us != null) {

            File wdir = us.getWorkingDirectory();
            if (wdir != null) {

                try {

                    // All we do is get rid of the temp dir.
                    FileUtils.deleteDirectory(wdir);

                } catch (IOException ex) {

                    log(WARNING, ex.getMessage());
                }
            }
        }
    }

}


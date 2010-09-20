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
package org.jflicks.photomanager.digikam;

import java.util.Hashtable;

import org.jflicks.db.Db4oServiceTracker;
import org.jflicks.photomanager.PhotoManager;
import org.jflicks.util.BaseActivator;

import org.osgi.framework.BundleContext;

/**
 * Simple activator for the digikam photo manager.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private Db4oServiceTracker db4oServiceTracker;
    private DigikamPhotoManager digikamPhotoManager;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);
        DigikamPhotoManager dpm = new DigikamPhotoManager();
        setDigikamPhotoManager(dpm);

        Db4oServiceTracker t = new Db4oServiceTracker(bc, dpm);
        setDb4oServiceTracker(t);
        t.open();

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(PhotoManager.TITLE_PROPERTY, dpm.getTitle());

        bc.registerService(PhotoManager.class.getName(), dpm, dict);
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        DigikamPhotoManager dpm = getDigikamPhotoManager();
        if (dpm != null) {
            dpm.close();
        }

        Db4oServiceTracker t = getDb4oServiceTracker();
        if (t != null) {
            t.close();
        }
    }

    private DigikamPhotoManager getDigikamPhotoManager() {
        return (digikamPhotoManager);
    }

    private void setDigikamPhotoManager(DigikamPhotoManager pm) {
        digikamPhotoManager = pm;
    }

    private Db4oServiceTracker getDb4oServiceTracker() {
        return (db4oServiceTracker);
    }

    private void setDb4oServiceTracker(Db4oServiceTracker t) {
        db4oServiceTracker = t;
    }

}

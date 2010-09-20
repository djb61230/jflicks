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
package org.jflicks.ui;

import org.jflicks.util.BaseActivator;

import org.osgi.framework.BundleContext;

/**
 * Activator that starts a Controller Job.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private ViewTracker viewTracker;
    private NMSTracker nmsTracker;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);

        JFlicksModel m = new JFlicksModel();
        JFlicksController c = new JFlicksController(m);

        ViewTracker vt = new ViewTracker(bc, c);
        setViewTracker(vt);
        vt.open();

        NMSTracker nt = new NMSTracker(bc, m);
        setNMSTracker(nt);
        nt.open();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {

        ViewTracker vt = getViewTracker();
        if (vt != null) {
            vt.close();
        }

        NMSTracker nt = getNMSTracker();
        if (nt != null) {
            nt.close();
        }
    }

    private ViewTracker getViewTracker() {
        return (viewTracker);
    }

    private void setViewTracker(ViewTracker t) {
        viewTracker = t;
    }

    private NMSTracker getNMSTracker() {
        return (nmsTracker);
    }

    private void setNMSTracker(NMSTracker t) {
        nmsTracker = t;
    }

}

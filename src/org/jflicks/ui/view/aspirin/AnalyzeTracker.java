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
package org.jflicks.ui.view.aspirin;

import org.jflicks.ui.view.aspirin.analyze.Analyze;
import org.jflicks.util.BaseTracker;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A tracker for the analyze service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class AnalyzeTracker extends BaseTracker {

    private AspirinView aspirinView;

    /**
     * Contructor with BundleContext and View instance.
     *
     * @param bc A given BundleContext needed to communicate with OSGi.
     * @param v Our View implementation.
     */
    public AnalyzeTracker(BundleContext bc, AspirinView v) {

        super(bc, Analyze.class.getName());
        setAspirinView(v);
    }

    private AspirinView getAspirinView() {
        return (aspirinView);
    }

    private void setAspirinView(AspirinView v) {
        aspirinView = v;
    }

    /**
     * A new Analyze service has come online.
     *
     * @param sr The ServiceReference object.
     * @return The instantiation.
     */
    public Object addingService(ServiceReference sr) {

        Object result = null;

        BundleContext bc = getBundleContext();
        AspirinView v = getAspirinView();
        if ((bc != null) && (v != null)) {

            Analyze service = (Analyze) bc.getService(sr);
            v.addAnalyze(service);
            result = service;
        }

        return (result);
    }

    /**
     * A recorder service has been modified.
     *
     * @param sr The Analyze ServiceReference.
     * @param svc The Analyze instance.
     */
    public void modifiedService(ServiceReference sr, Object svc) {
    }

    /**
     * A recorder service has gone away.  Bye-bye.
     *
     * @param sr The ServiceReference.
     * @param svc The instance.
     */
    public void removedService(ServiceReference sr, Object svc) {

        AspirinView v = getAspirinView();
        if (v != null) {

            v.removeAnalyze((Analyze) svc);
        }
    }

}

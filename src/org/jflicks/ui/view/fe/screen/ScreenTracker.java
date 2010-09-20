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
package org.jflicks.ui.view.fe.screen;

import org.jflicks.ui.view.fe.FrontEndView;
import org.jflicks.util.BaseTracker;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A tracker for the theme service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ScreenTracker extends BaseTracker {

    private FrontEndView frontEndView;

    /**
     * Contructor with BundleContext and NMS instance.
     *
     * @param bc A given BundleContext needed to communicate with OSGi.
     * @param fev Our front end view implementation.
     */
    public ScreenTracker(BundleContext bc, FrontEndView fev) {

        super(bc, Screen.class.getName());
        setFrontEndView(fev);
    }

    private FrontEndView getFrontEndView() {
        return (frontEndView);
    }

    private void setFrontEndView(FrontEndView v) {
        frontEndView = v;
    }

    /**
     * A new Screen service has come online.
     *
     * @param sr The ServiceReference object.
     * @return The instantiation.
     */
    public Object addingService(ServiceReference sr) {

        Object result = null;

        BundleContext bc = getBundleContext();
        FrontEndView fev = getFrontEndView();
        if ((bc != null) && (fev != null)) {

            Screen service = (Screen) bc.getService(sr);
            fev.addScreen(service);
            result = service;
        }

        return (result);
    }

    /**
     * A screen service has been modified.
     *
     * @param sr The Screen ServiceReference.
     * @param svc The Screen instance.
     */
    public void modifiedService(ServiceReference sr, Object svc) {
    }

    /**
     * A screen service has gone away.  Bye-bye.
     *
     * @param sr The ServiceReference.
     * @param svc The instance.
     */
    public void removedService(ServiceReference sr, Object svc) {

        FrontEndView s = getFrontEndView();
        if (s != null) {

            s.removeScreen((Screen) svc);
        }
    }

}

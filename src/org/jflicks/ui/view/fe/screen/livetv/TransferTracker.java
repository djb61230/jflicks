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
package org.jflicks.ui.view.fe.screen.livetv;

import org.jflicks.transfer.Transfer;
import org.jflicks.util.BaseTracker;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A tracker for the Transfer service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class TransferTracker extends BaseTracker {

    private DVRLiveTVScreen dvrLiveTVScreen;

    /**
     * Contructor with BundleContext and DVRLiveTVScreen instance.
     *
     * @param bc A given BundleContext needed to communicate with OSGi.
     * @param s Our DVRLiveTVScreen implementation.
     */
    public TransferTracker(BundleContext bc, DVRLiveTVScreen s) {

        super(bc, Transfer.class.getName());
        setDVRLiveTVScreen(s);
    }

    private DVRLiveTVScreen getDVRLiveTVScreen() {
        return (dvrLiveTVScreen);
    }

    private void setDVRLiveTVScreen(DVRLiveTVScreen s) {
        dvrLiveTVScreen = s;
    }

    /**
     * A new Transfer service has come online.
     *
     * @param sr The ServiceReference object.
     * @return The instantiation.
     */
    public Object addingService(ServiceReference sr) {

        Object result = null;

        BundleContext bc = getBundleContext();
        DVRLiveTVScreen s = getDVRLiveTVScreen();
        if ((bc != null) && (s != null)) {

            Transfer service = (Transfer) bc.getService(sr);
            s.setTransfer(service);
            result = service;
        }

        return (result);
    }

    /**
     * A recorder service has been modified.
     *
     * @param sr The Live ServiceReference.
     * @param svc The Live instance.
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

        DVRLiveTVScreen s = getDVRLiveTVScreen();
        if (s != null) {

            s.setTransfer(null);
        }
    }

}

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
package org.jflicks.tv.recorder.jhdhr;

import java.util.Hashtable;

import org.jflicks.tv.recorder.Recorder;
import org.jflicks.util.BaseActivator;
import org.jflicks.util.LogUtil;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simple activator for the HDHR recorder.
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

        HDHRConfig config = new HDHRConfig();
        HDHRDevice[] array = config.getHDHRDevices();
        if ((array != null) && (array.length > 0)) {

            for (int i = 0; i < array.length; i++) {

                registerRecorder(bc, array[i].getId(), array[i].getIp(), array[i].getModel(), array[i].getTuner());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {
    }

    private void registerRecorder(BundleContext bc, String id, String ip, String model, int tuner) {

        if ((bc != null) && (id != null)) {

            LogUtil.log(LogUtil.INFO, "register id <" + id + "> tuner <" + tuner + "> ip <" + ip + "> model <" + model + ">");
            HDHRRecorder r = new HDHRRecorder();
            r.setDevice(id + "-" + tuner);
            r.setIpAddress(ip);
            r.setModel(model);
            r.updateDefault();

            Hashtable<String, String> dict = new Hashtable<String, String>();
            dict.put(Recorder.TITLE_PROPERTY, r.getTitle());
            bc.registerService(Recorder.class.getName(), r, dict);
        }
    }

}

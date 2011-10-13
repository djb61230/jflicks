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
package org.jflicks.ui.view.vm;

import org.jflicks.metadata.Metadata;
import org.jflicks.util.BaseTracker;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A tracker for the metadata service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class MetadataTracker extends BaseTracker {

    private VideoManagerView videoManagerView;

    /**
     * Contructor with BundleContext and View instance.
     *
     * @param bc A given BundleContext needed to communicate with OSGi.
     * @param v Our View implementation.
     */
    public MetadataTracker(BundleContext bc, VideoManagerView v) {

        super(bc, Metadata.class.getName());
        setVideoManagerView(v);
    }

    private VideoManagerView getVideoManagerView() {
        return (videoManagerView);
    }

    private void setVideoManagerView(VideoManagerView v) {
        videoManagerView = v;
    }

    /**
     * A new Metadata service has come online.
     *
     * @param sr The ServiceReference object.
     * @return The instantiation.
     */
    public Object addingService(ServiceReference sr) {

        Object result = null;

        BundleContext bc = getBundleContext();
        VideoManagerView v = getVideoManagerView();
        if ((bc != null) && (v != null)) {

            Metadata service = (Metadata) bc.getService(sr);
            v.addMetadata(service);
            result = service;
        }

        return (result);
    }

    /**
     * A recorder service has been modified.
     *
     * @param sr The Metadata ServiceReference.
     * @param svc The Metadata instance.
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

        VideoManagerView v = getVideoManagerView();
        if (v != null) {

            v.removeMetadata((Metadata) svc);
        }
    }

}

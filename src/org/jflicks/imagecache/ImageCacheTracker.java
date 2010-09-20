/*
    This file is part of JFLICKS.

    JFLICKS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JFLICKS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MEImageCacheHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JFLICKS.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.jflicks.imagecache;

import org.jflicks.util.BaseTracker;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A tracker for the image cache service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class ImageCacheTracker extends BaseTracker {

    private ImageCacheProperty imageCacheProperty;

    /**
     * Contructor with BundleContext and ImageCacheProperty instance.
     *
     * @param bc A given BundleContext needed to communicate with OSGi.
     * @param icp Our user of a ImageCache service.
     */
    public ImageCacheTracker(BundleContext bc, ImageCacheProperty icp) {

        super(bc, ImageCache.class.getName());
        setImageCacheProperty(icp);
    }

    private ImageCacheProperty getImageCacheProperty() {
        return (imageCacheProperty);
    }

    private void setImageCacheProperty(ImageCacheProperty icp) {
        imageCacheProperty = icp;
    }

    /**
     * A new ImageCache service has come online.
     *
     * @param sr The ServiceReference object.
     * @return The instantiation.
     */
    public Object addingService(ServiceReference sr) {

        Object result = null;

        BundleContext bc = getBundleContext();
        ImageCacheProperty icp = getImageCacheProperty();
        if ((bc != null) && (icp != null)) {

            ImageCache service = (ImageCache) bc.getService(sr);
            icp.setImageCache(service);
            result = service;
        }

        return (result);
    }

    /**
     * A image cache service has been modified.
     *
     * @param sr The ImageCache ServiceReference.
     * @param svc The ImageCache instance.
     */
    public void modifiedService(ServiceReference sr, Object svc) {
    }

    /**
     * A image cache service has gone away.  Bye-bye.
     *
     * @param sr The ServiceReference.
     * @param svc The instance.
     */
    public void removedService(ServiceReference sr, Object svc) {

        ImageCacheProperty icp = getImageCacheProperty();
        if (icp != null) {

            icp.setImageCache(null);
        }
    }

}

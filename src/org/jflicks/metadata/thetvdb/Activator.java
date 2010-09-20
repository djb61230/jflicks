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
    along with JFLICKS.  If not, see <recorder://www.gnu.org/licenses/>.
*/
package org.jflicks.metadata.thetvdb;

import java.util.Hashtable;

import org.jflicks.metadata.Metadata;
import org.jflicks.util.BaseActivator;

import org.osgi.framework.BundleContext;

/**
 * Simple activater that starts our thetvdb service.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class Activator extends BaseActivator {

    private TheTVDBMetadata theTVDBMetadata;

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) {

        setBundleContext(bc);
        TheTVDBMetadata m = new TheTVDBMetadata();
        setTheTVDBMetadata(m);

        Hashtable<String, String> dict = new Hashtable<String, String>();
        dict.put(Metadata.TITLE_PROPERTY, m.getTitle());

        bc.registerService(Metadata.class.getName(), m, dict);
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) {
    }

    private TheTVDBMetadata getTheTVDBMetadata() {
        return (theTVDBMetadata);
    }

    private void setTheTVDBMetadata(TheTVDBMetadata m) {
        theTVDBMetadata = m;
    }

}

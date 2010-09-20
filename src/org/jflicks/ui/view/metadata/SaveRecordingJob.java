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
package org.jflicks.ui.view.metadata;

import org.jflicks.job.AbstractJob;
import org.jflicks.job.JobEvent;
import org.jflicks.nms.NMS;
import org.jflicks.nms.NMSConstants;
import org.jflicks.tv.Recording;

/**
 * A job that saves images to an NMS.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class SaveRecordingJob extends AbstractJob {

    private NMS nms;
    private Recording[] recordings;

    /**
     * Constructor with our required argument.
     *
     * @param nms A NMS to access.
     * @param recordings The recordings to save.
     */
    public SaveRecordingJob(NMS nms, Recording[] recordings) {

        setNMS(nms);
        setRecordings(recordings);
    }

    private NMS getNMS() {
        return (nms);
    }

    private void setNMS(NMS n) {
        nms = n;
    }

    private Recording[] getRecordings() {
        return (recordings);
    }

    private void setRecordings(Recording[] array) {
        recordings = array;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        setTerminate(false);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        NMS n = getNMS();
        Recording[] array = getRecordings();
        if ((n != null) && (array != null)) {

            for (int i = 0; i < array.length; i++) {

                // First save the images...
                String id = array[i].getSeriesId();
                String url = array[i].getBannerURL();
                if ((url != null) && (id != null)) {

                    if (url.indexOf(id) == -1) {

                        n.save(NMSConstants.BANNER_IMAGE_TYPE, url, id);
                    }
                }

                url = array[i].getFanartURL();
                if ((url != null) && (id != null)) {

                    if (url.indexOf(id) == -1) {

                        n.save(NMSConstants.FANART_IMAGE_TYPE, url, id);
                    }
                }

                url = array[i].getPosterURL();
                if ((url != null) && (id != null)) {

                    if (url.indexOf(id) == -1) {

                        n.save(NMSConstants.POSTER_IMAGE_TYPE, url, id);
                    }
                }

                Recording updated = n.getRecordingById(array[i].getId());
                array[i].setBannerURL(updated.getBannerURL());
                array[i].setFanartURL(updated.getFanartURL());
                array[i].setPosterURL(updated.getPosterURL());
            }
        }

        fireJobEvent(JobEvent.COMPLETE);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        setTerminate(true);
    }

}
